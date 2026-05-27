package net.craftingstore.bukkit.inventory.handlers;

import java.util.UUID;

import org.bukkit.entity.Player;

import net.craftingstore.bukkit.CraftingStoreBukkit;
import net.craftingstore.bukkit.inventory.BuyInventoryBuilder;
import net.craftingstore.bukkit.inventory.CraftingStoreGUI;
import net.craftingstore.bukkit.inventory.InventoryItemHandler;
import net.craftingstore.bukkit.util.ChatColorUtil;
import net.craftingstore.core.models.api.ApiPackageInformation;
import net.craftingstore.core.models.api.inventory.types.InventoryItemBuyButton;
import net.craftingstore.core.models.api.inventory.types.InventoryItemBuyablePackage;
import net.trueog.diamondbankog.DiamondBankException;
import net.trueog.diamondbankog.DiamondBankException.InsufficientFundsException;

public class BuyButtonHandler implements InventoryItemHandler<InventoryItemBuyButton> {

    private final CraftingStoreBukkit instance;
    private final BuyInventoryBuilder buyInventoryBuilder;

    public BuyButtonHandler(CraftingStoreBukkit instance, BuyInventoryBuilder buyInventoryBuilder) {
        this.instance = instance;
        this.buyInventoryBuilder = buyInventoryBuilder;
    }

    @Override
    public void handle(Player p, InventoryItemBuyButton item, CraftingStoreGUI inventory) {
        if (inventory.getItemBuyablePackage() == null) {
            throw new RuntimeException("BuyButtonHandler only works in a buy menu");
        }
        InventoryItemBuyablePackage itemBuyablePackage = inventory.getItemBuyablePackage();

        InventoryItemBuyButton.InventoryItemBuyButtonAction action = item.getAction();
        if (action.equals(InventoryItemBuyButton.InventoryItemBuyButtonAction.ONLINE)) {
            for (String message : itemBuyablePackage.getMessages()) {
                p.sendMessage(ChatColorUtil.translate(message));
            }
            p.closeInventory();
            return;
        }

        if (action.equals(InventoryItemBuyButton.InventoryItemBuyButtonAction.IN_GAME)) {
            if (!this.instance.isHookedWithDiamondBank()) {
                p.sendMessage(instance.getPrefix() + "CraftingStore-OG is not hooked with DiamondBank-OG!");
                p.closeInventory();
                return;
            }

            UUID playerId = p.getUniqueId();
            String playerName = p.getName();
            String ip = p.getAddress().getAddress().getHostAddress();
            CraftingStoreGUI loading = this.buyInventoryBuilder.buildLoadInventory(p);
            loading.open(false);
            this.instance.getCraftingStore().getImplementation().runAsyncTask(() -> {
                try {
                    ApiPackageInformation packageInformation = this.instance.getCraftingStore().getApi().getPackageInformation(
                            playerName,
                            playerId,
                            ip,
                            itemBuyablePackage.getPackageId()
                    ).get();
                    if (!packageInformation.isAllowedToBuy()) {
                        p.sendMessage(ChatColorUtil.translate(packageInformation.getMessage()));
                        for (String message : itemBuyablePackage.getMessages()) {
                            p.sendMessage(ChatColorUtil.translate(message));
                        }
                        closeInventory(p);
                        return;
                    }
                    if (packageInformation.getPrice() != itemBuyablePackage.getPrice()) {
                        p.sendMessage(this.instance.getPrefix() + "There was a problem with the payment data. Please try again later.");
                        closeInventory(p);
                        return;
                    }
                    if (!this.instance.getDiamondBankHook().hasFunds(playerId, packageInformation.getPrice())) {
                        p.sendMessage(ChatColorUtil.translate(
                                this.instance.getCraftingStore().getImplementation().getConfiguration().getNotEnoughBalanceMessage()
                        ));
                        closeInventory(p);
                        return;
                    }

                    try {
                        this.instance.getDiamondBankHook().charge(
                                playerId,
                                packageInformation.getPrice(),
                                itemBuyablePackage.getPackageId()
                        );
                    } catch (InsufficientFundsException exception) {
                        p.sendMessage(ChatColorUtil.translate(
                                this.instance.getCraftingStore().getImplementation().getConfiguration().getNotEnoughBalanceMessage()
                        ));
                        closeInventory(p);
                        return;
                    } catch (DiamondBankException exception) {
                        exception.printStackTrace();
                        p.sendMessage(this.instance.getPrefix() + "There was a problem with the transaction. Please try again later");
                        closeInventory(p);
                        return;
                    }

                    try {
                        boolean success = this.instance.getCraftingStore().getApi().createPayment(
                                playerName,
                                packageInformation.getPrice(),
                                new int[]{itemBuyablePackage.getPackageId()}
                        ).get();
                        if (success) {
                            p.sendMessage(ChatColorUtil.translate(itemBuyablePackage.getSuccessMessage()));
                            closeInventory(p);
                            return;
                        }
                        throw new RuntimeException("Failed to create a transaction at CraftingStore. Giving money back.");
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        this.instance.getCraftingStore().getLogger().error(String.format(
                                "Unable to create a transaction for player %s with package %s and cost %s. Giving the player's money back...",
                                playerName,
                                itemBuyablePackage.getPackageId(),
                                packageInformation.getPrice()
                        ));
                        try {
                            this.instance.getDiamondBankHook().refund(
                                    playerId,
                                    packageInformation.getPrice(),
                                    itemBuyablePackage.getPackageId()
                            );
                        } catch (DiamondBankException rollbackException) {
                            rollbackException.printStackTrace();
                            p.sendMessage(this.instance.getPrefix() + "There was a problem with the transaction, and we were unable to give you back your money. Please contact an administrator");
                            this.instance.getCraftingStore().getLogger().error(String.format(
                                    "Unable to give the player's money back. Player: %s, cost: %s.",
                                    playerName,
                                    packageInformation.getPrice()
                            ));
                            closeInventory(p);
                            return;
                        }

                        p.sendMessage(this.instance.getPrefix() + "A problem occurred while creating your payment. Your money has been given back. Please try again later");
                        closeInventory(p);
                    }
                } catch (DiamondBankException exception) {
                    exception.printStackTrace();
                    closeInventory(p);
                    p.sendMessage(this.instance.getPrefix()
                            + "There was a problem accessing DiamondBank-OG. Please try again later.");
                } catch (Exception exception) {
                    exception.printStackTrace();
                    closeInventory(p);
                    p.sendMessage(this.instance.getPrefix() + "A problem occurred. Please try again later.");
                }
            });
        }
    }

    private void closeInventory(Player p) {
        this.instance.runSyncTask(() -> {
            if (p.isOnline()) {
                p.closeInventory();
            }
        });
    }
}
