package net.craftingstore.bukkit.inventory;

import java.util.Map;

import org.bukkit.entity.Player;

import net.craftingstore.bukkit.CraftingStoreBukkit;
import net.craftingstore.core.models.api.inventory.CraftingStoreInventory;
import net.craftingstore.core.models.api.inventory.InventoryItem;
import net.craftingstore.core.models.api.inventory.types.InventoryItemBuyablePackage;
import net.trueog.gxui.GUIBase;

public class CraftingStoreGUI extends GUIBase {

    private final InventoryBuilder inventoryBuilder;
    private final CraftingStoreInventory inventory;
    private final CraftingStoreGUI parentInventory;
    private final InventoryItemBuyablePackage itemBuyablePackage;
    private final Map<String, ?> placeholders;

    public CraftingStoreGUI(
            CraftingStoreBukkit instance,
            Player player,
            InventoryBuilder inventoryBuilder,
            CraftingStoreInventory inventory,
            CraftingStoreGUI parentInventory,
            InventoryItemBuyablePackage itemBuyablePackage,
            Map<String, ?> placeholders
    ) {
        super(instance, player, inventoryBuilder.buildTitle(inventory, placeholders), inventory.getSize(), false);
        this.inventoryBuilder = inventoryBuilder;
        this.inventory = inventory;
        this.parentInventory = parentInventory;
        this.itemBuyablePackage = itemBuyablePackage;
        this.placeholders = placeholders;
    }

    @Override
    public void setupItems() {
        for (InventoryItem item : this.inventory.getContent()) {
            addItem(item.getIndex(), this.inventoryBuilder.buildItem(item, this.placeholders, this));
        }
    }

    public CraftingStoreGUI getParentInventory() {
        return parentInventory;
    }

    public InventoryItemBuyablePackage getItemBuyablePackage() {
        return itemBuyablePackage;
    }
}
