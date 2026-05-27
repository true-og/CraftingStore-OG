package net.craftingstore.bukkit.inventory.handlers;

import net.craftingstore.bukkit.inventory.CraftingStoreGUI;
import net.craftingstore.bukkit.inventory.InventoryItemHandler;
import net.craftingstore.core.models.api.inventory.types.InventoryItemCloseButton;
import org.bukkit.entity.Player;

public class CloseButtonHandler implements InventoryItemHandler<InventoryItemCloseButton> {
    @Override
    public void handle(Player p, InventoryItemCloseButton item, CraftingStoreGUI inventory) {
        p.closeInventory();
    }
}
