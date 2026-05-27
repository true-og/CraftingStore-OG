package net.craftingstore.bukkit.hooks;

import java.util.UUID;

import org.bukkit.plugin.RegisteredServiceProvider;

import net.craftingstore.bukkit.CraftingStoreBukkit;
import net.trueog.diamondbankog.DiamondBankException.EconomyDisabledException;
import net.trueog.diamondbankog.DiamondBankException.InsufficientFundsException;
import net.trueog.diamondbankog.DiamondBankException.InvalidPlayerException;
import net.trueog.diamondbankog.DiamondBankException.PlayerNotOnlineException;
import net.trueog.diamondbankog.api.DiamondBankAPIJava;

public class DiamondBankHook {

    private static final String PURCHASE_REASON = "CraftingStore-OG in-game package purchase";
    private static final String REFUND_REASON = "CraftingStore-OG in-game package purchase refund";

    private final CraftingStoreBukkit instance;
    private DiamondBankAPIJava diamondBank;

    public DiamondBankHook(CraftingStoreBukkit instance) {
        this.instance = instance;
    }

    public boolean register() {
        RegisteredServiceProvider<DiamondBankAPIJava> rsp = instance.getServer().getServicesManager()
                .getRegistration(DiamondBankAPIJava.class);
        if (rsp == null) {
            return false;
        }
        this.diamondBank = rsp.getProvider();
        return true;
    }

    public boolean hasFunds(UUID playerId, int price) throws EconomyDisabledException {
        long shards = toShards(price);
        long availableShards = diamondBank.getBankShards(playerId) + diamondBank.getInventoryShards(playerId);
        return availableShards >= shards;
    }

    public void charge(UUID playerId, int price, int packageId) throws EconomyDisabledException, InvalidPlayerException,
            PlayerNotOnlineException, InsufficientFundsException {
        diamondBank.consumeFromPlayer(playerId, toShards(price), PURCHASE_REASON, notes(packageId));
    }

    public void refund(UUID playerId, int price, int packageId) throws EconomyDisabledException {
        diamondBank.addToPlayerBankShards(playerId, toShards(price), REFUND_REASON, notes(packageId));
    }

    private long toShards(int price) {
        return diamondBank.diamondsToShards((double) price);
    }

    private String notes(int packageId) {
        return "Plugin: CraftingStore-OG; package ID: " + packageId;
    }
}
