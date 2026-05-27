package net.craftingstore.bukkit.inventory.handlers;

import net.craftingstore.bukkit.inventory.CraftingStoreGUI;
import net.craftingstore.bukkit.inventory.InventoryItemHandler;
import net.craftingstore.core.models.api.inventory.types.InventoryItemBackButton;
import org.bukkit.entity.Player;

public class BackButtonHandler implements InventoryItemHandler<InventoryItemBackButton> {

    @Override
    public void handle(Player p, InventoryItemBackButton item, CraftingStoreGUI inventory) {
        if (inventory.getParentInventory() != null) {
            inventory.getParentInventory().open(false);
        }
    }
}
