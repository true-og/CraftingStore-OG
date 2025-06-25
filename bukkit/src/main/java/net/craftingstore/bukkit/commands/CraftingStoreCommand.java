package net.craftingstore.bukkit.commands;

import java.util.concurrent.ExecutionException;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import net.craftingstore.bukkit.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CraftingStoreCommand implements CommandExecutor {

    private CraftingStoreBukkit instance;

    public CraftingStoreCommand(CraftingStoreBukkit instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(instance.getCraftingStore().ADMIN_PERMISSION)) {
            TextUtil.craftingStoreMessage(
                    sender, CraftingStoreBukkit.getPrefix() + "You don't have the required permission!");
            return false;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            instance.getCraftingStore().reload();
            TextUtil.craftingStoreMessage(sender, CraftingStoreBukkit.getPrefix() + "The plugin is reloading!");
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("key")) {
            instance.getConfig().set("api-key", args[1]);
            instance.getConfigWrapper().saveConfig();
            instance.getServer().getScheduler().runTaskAsynchronously(instance, () -> {
                try {
                    if (instance.getCraftingStore().reload().get()) {
                        TextUtil.craftingStoreMessage(
                                sender,
                                CraftingStoreBukkit.getPrefix()
                                        + "The new API key has been set in the config, and the plugin has been reloaded.");
                    } else {
                        TextUtil.craftingStoreMessage(
                                sender,
                                CraftingStoreBukkit.getPrefix()
                                        + "The API key is invalid. The plugin will not work until you set a valid API key.");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        if ((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("debug")) {
            boolean isDebugging = this.instance.getCraftingStore().getLogger().isDebugging();
            if (args.length == 1) {
                TextUtil.craftingStoreMessage(
                        sender,
                        String.format(
                                "%sDebug mode is currently %s.",
                                CraftingStoreBukkit.getPrefix(), isDebugging ? "enabled" : "disabled"));
                return true;
            }
            String debugValue = args[1].toLowerCase();
            if (debugValue.equalsIgnoreCase("true")) {
                isDebugging = true;
            } else if (debugValue.equalsIgnoreCase("false")) {
                isDebugging = false;
            } else {
                TextUtil.craftingStoreMessage(sender, CraftingStoreBukkit.getPrefix() + "Unknown debug value.");
                return true;
            }
            this.instance.getCraftingStore().getLogger().setDebugging(isDebugging);
            instance.getConfig().set("debug", isDebugging);
            instance.getConfigWrapper().saveConfig();
            TextUtil.craftingStoreMessage(
                    sender,
                    String.format(
                            "%sDebug mode has been %s.",
                            CraftingStoreBukkit.getPrefix(), isDebugging ? "enabled" : "disabled"));
            return true;
        }

        TextUtil.craftingStoreMessage(sender, "&7&m-----------------------");
        TextUtil.craftingStoreMessage(sender, "&8> &7/" + label + " reload &8-> &7Reload the config.");
        TextUtil.craftingStoreMessage(sender, "&8> &7/" + label + " key <your key> &8-> &7Update the key.");
        TextUtil.craftingStoreMessage(sender, "&7&m-----------------------");
        return true;
    }
}
