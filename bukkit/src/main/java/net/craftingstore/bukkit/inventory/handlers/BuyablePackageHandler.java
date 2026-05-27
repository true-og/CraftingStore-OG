package net.craftingstore.bukkit.inventory.handlers;

import java.util.UUID;

import org.bukkit.entity.Player;

import net.craftingstore.bukkit.CraftingStoreBukkit;
import net.craftingstore.bukkit.inventory.BuyInventoryBuilder;
import net.craftingstore.bukkit.inventory.CraftingStoreGUI;
import net.craftingstore.bukkit.inventory.InventoryItemHandler;
import net.craftingstore.bukkit.util.ChatColorUtil;
import net.craftingstore.core.models.api.inventory.types.InventoryItemBuyablePackage;
import net.trueog.diamondbankog.DiamondBankException.EconomyDisabledException;

public class BuyablePackageHandler implements InventoryItemHandler<InventoryItemBuyablePackage> {

    private final CraftingStoreBukkit instance;
    private final BuyInventoryBuilder buyInventoryBuilder;

    public BuyablePackageHandler(CraftingStoreBukkit instance, BuyInventoryBuilder buyInventoryBuilder) {
        this.instance = instance;
        this.buyInventoryBuilder = buyInventoryBuilder;
    }

    @Override
    public void handle(Player p, InventoryItemBuyablePackage item, CraftingStoreGUI inventory) {
        if (!this.instance.isHookedWithDiamondBank()) {
            p.sendMessage(instance.getPrefix() + "CraftingStore-OG is not hooked with DiamondBank-OG!");
            p.closeInventory();
            return;
        }

        CraftingStoreGUI loading = this.buyInventoryBuilder.buildLoadInventory(p);
        loading.open(false);
        UUID playerId = p.getUniqueId();
        this.instance.getCraftingStore().getImplementation().runAsyncTask(() -> {
            try {
                if (!this.instance.getDiamondBankHook().hasFunds(playerId, item.getPrice())) {
                    this.instance.runSyncTask(() -> {
                        if (p.isOnline()) {
                            p.sendMessage(ChatColorUtil.translate(this.instance.getCraftingStore().getImplementation()
                                    .getConfiguration().getNotEnoughBalanceMessage()));
                            for (String message : item.getMessages()) {
                                p.sendMessage(ChatColorUtil.translate(message));
                            }
                            p.closeInventory();
                        }
                    });
                    return;
                }

                CraftingStoreGUI buyMenu = this.buyInventoryBuilder.build(p, item, inventory);
                if (buyMenu != null) {
                    this.instance.runSyncTask(() -> {
                        if (p.isOnline()) {
                            buyMenu.open(false);
                        }
                    });
                }
            } catch (EconomyDisabledException exception) {
                exception.printStackTrace();
                this.instance.runSyncTask(() -> {
                    if (p.isOnline()) {
                        p.sendMessage(instance.getPrefix()
                                + "There was a problem accessing DiamondBank-OG. Please try again later.");
                        p.closeInventory();
                    }
                });
            } catch (Exception exception) {
                exception.printStackTrace();
                this.instance.runSyncTask(() -> {
                    if (p.isOnline()) {
                        p.closeInventory();
                    }
                });
            }
        });
    }
}
