package net.craftingstore.bukkit.inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.text.StringSubstitutor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.craftingstore.bukkit.CraftingStoreBukkit;
import net.craftingstore.bukkit.inventory.handlers.BackButtonHandler;
import net.craftingstore.bukkit.inventory.handlers.BuyButtonHandler;
import net.craftingstore.bukkit.inventory.handlers.BuyablePackageHandler;
import net.craftingstore.bukkit.inventory.handlers.CategoryItemHandler;
import net.craftingstore.bukkit.inventory.handlers.CloseButtonHandler;
import net.craftingstore.bukkit.inventory.handlers.MessageButtonHandler;
import net.craftingstore.bukkit.util.ChatColorUtil;
import net.craftingstore.bukkit.util.VersionUtil;
import net.craftingstore.core.models.api.inventory.CraftingStoreInventory;
import net.craftingstore.core.models.api.inventory.InventoryItem;
import net.craftingstore.core.models.api.inventory.types.InventoryItemBackButton;
import net.craftingstore.core.models.api.inventory.types.InventoryItemBuyButton;
import net.craftingstore.core.models.api.inventory.types.InventoryItemBuyablePackage;
import net.craftingstore.core.models.api.inventory.types.InventoryItemCategory;
import net.craftingstore.core.models.api.inventory.types.InventoryItemCloseButton;
import net.craftingstore.core.models.api.inventory.types.InventoryItemMessage;
import net.trueog.gxui.GUIButton;
import net.trueog.gxui.GUIItem;

public class InventoryBuilder {

    private final CraftingStoreBukkit instance;
    private final InventoryItemBuilder inventoryItemBuilder;
    private final HashMap<Class<? extends InventoryItem>, InventoryItemHandler> handlers = new HashMap<>();

    public InventoryBuilder(CraftingStoreBukkit instance) {
        this.instance = instance;
        this.inventoryItemBuilder = new InventoryItemBuilder(instance);
        BuyInventoryBuilder buyInventoryBuilder = new BuyInventoryBuilder(instance, this);
        this.handlers.put(InventoryItemBackButton.class, new BackButtonHandler());
        this.handlers.put(InventoryItemCategory.class, new CategoryItemHandler(this));
        this.handlers.put(InventoryItemCloseButton.class, new CloseButtonHandler());
        this.handlers.put(InventoryItemMessage.class, new MessageButtonHandler());
        this.handlers.put(InventoryItemBuyablePackage.class, new BuyablePackageHandler(instance, buyInventoryBuilder));
        this.handlers.put(InventoryItemBuyButton.class, new BuyButtonHandler(instance, buyInventoryBuilder));
    }

    public CraftingStoreGUI build(Player player, CraftingStoreInventory inventory) {
        return build(player, inventory, null);
    }

    public CraftingStoreGUI build(Player player, CraftingStoreInventory inventory, CraftingStoreGUI parentInventory) {
        return new CraftingStoreGUI(this.instance, player, this, inventory, parentInventory, null, new HashMap<>());
    }

    public CraftingStoreGUI buildBuyMenu(
            Player player,
            CraftingStoreInventory inventory,
            CraftingStoreGUI parentInventory,
            InventoryItemBuyablePackage itemBuyablePackage,
            Map<String, ?> placeholders
    ) {
        return new CraftingStoreGUI(
                this.instance,
                player,
                this,
                inventory,
                parentInventory,
                itemBuyablePackage,
                placeholders
        );
    }

    String buildTitle(CraftingStoreInventory inventory, Map<String, ?> placeholders) {
        String title = inventory.getTitle();
        if (title == null || title.isEmpty()) {
            title = "CraftingStore-OG";
        }

        StringSubstitutor stringSubstitutor = new StringSubstitutor(placeholders, "{", "}");
        return ChatColorUtil.translate(stringSubstitutor.replace(title)).replace(ChatColor.COLOR_CHAR, '&');
    }

    GUIItem buildItem(InventoryItem inventoryItem, Map<String, ?> placeholders, CraftingStoreGUI inventory) {
        StringSubstitutor stringSubstitutor = new StringSubstitutor(placeholders, "{", "}");
        ItemStack itemStack = this.inventoryItemBuilder.getItemStack(
                inventoryItem.getIcon().getMaterial(),
                inventoryItem.getIcon().getAmount()
        );
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColorUtil.translate(stringSubstitutor.replace(inventoryItem.getName())));
            if (inventoryItem.getDescription() != null && inventoryItem.getDescription().length != 0) {
                meta.setLore(Arrays.stream(inventoryItem.getDescription())
                        .map(stringSubstitutor::replace)
                        .map(ChatColorUtil::translate)
                        .collect(Collectors.toList())
                );
            }
            if (VersionUtil.isCustomModalDataAvailable() && inventoryItem.getIcon().getCustomModelData() != null) {
                meta.setCustomModelData(inventoryItem.getIcon().getCustomModelData());
            }
            itemStack.setItemMeta(meta);
        }

        GUIItem item = new ItemStackGUIItem(itemStack);
        item.setPlayErrorSound(false);
        if (this.handlers.containsKey(inventoryItem.getClass())) {
            item.button(new InventoryButton(inventoryItem, inventory));
        }
        return item;
    }

    @SuppressWarnings("unchecked")
    private boolean handleClick(InventoryItem item, CraftingStoreGUI inventory) {
        InventoryItemHandler<InventoryItem> handler = this.handlers.get(item.getClass());
        if (handler == null) {
            return false;
        }
        handler.handle(inventory.getPlayer(), item, inventory);
        return true;
    }

    private final class InventoryButton implements GUIButton {

        private final InventoryItem item;
        private final CraftingStoreGUI inventory;

        private InventoryButton(InventoryItem item, CraftingStoreGUI inventory) {
            this.item = item;
            this.inventory = inventory;
        }

        @Override
        public boolean leftClick() {
            return handleClick(this.item, this.inventory);
        }

        @Override
        public boolean leftClickShift() {
            return leftClick();
        }

        @Override
        public boolean rightClick() {
            return leftClick();
        }

        @Override
        public boolean rightClickShift() {
            return leftClick();
        }
    }

    private static final class ItemStackGUIItem extends GUIItem {

        private final ItemStack itemStack;

        private ItemStackGUIItem(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @Override
        public ItemStack build() {
            return itemStack.clone();
        }
    }
}
