package net.craftingstore.bukkit.listeners;

import java.util.HashMap;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import net.craftingstore.bukkit.inventory.BuyInventoryBuilder;
import net.craftingstore.bukkit.inventory.CraftingStoreInventoryHolder;
import net.craftingstore.bukkit.inventory.InventoryBuilder;
import net.craftingstore.bukkit.inventory.InventoryItemHandler;
import net.craftingstore.bukkit.inventory.handlers.BackButtonHandler;
import net.craftingstore.bukkit.inventory.handlers.BuyButtonHandler;
import net.craftingstore.bukkit.inventory.handlers.BuyablePackageHandler;
import net.craftingstore.bukkit.inventory.handlers.CategoryItemHandler;
import net.craftingstore.bukkit.inventory.handlers.CloseButtonHandler;
import net.craftingstore.bukkit.inventory.handlers.MessageButtonHandler;
import net.craftingstore.core.models.api.inventory.InventoryItem;
import net.craftingstore.core.models.api.inventory.types.InventoryItemBackButton;
import net.craftingstore.core.models.api.inventory.types.InventoryItemBuyButton;
import net.craftingstore.core.models.api.inventory.types.InventoryItemBuyablePackage;
import net.craftingstore.core.models.api.inventory.types.InventoryItemCategory;
import net.craftingstore.core.models.api.inventory.types.InventoryItemCloseButton;
import net.craftingstore.core.models.api.inventory.types.InventoryItemMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {

    private final HashMap<Class<? extends InventoryItem>, InventoryItemHandler<?>> handlers = new HashMap<>();

    private final InventoryBuilder inventoryBuilder;

    public InventoryListener(CraftingStoreBukkit instance) {
        this.inventoryBuilder = new InventoryBuilder(instance);
        BuyInventoryBuilder buyInventoryBuilder = new BuyInventoryBuilder(instance, this.inventoryBuilder);
        this.handlers.put(InventoryItemBackButton.class, new BackButtonHandler(this.inventoryBuilder));
        this.handlers.put(InventoryItemCategory.class, new CategoryItemHandler(this.inventoryBuilder));
        this.handlers.put(InventoryItemCloseButton.class, new CloseButtonHandler());
        this.handlers.put(InventoryItemMessage.class, new MessageButtonHandler());
        this.handlers.put(InventoryItemBuyablePackage.class, new BuyablePackageHandler(instance, buyInventoryBuilder));
        this.handlers.put(InventoryItemBuyButton.class, new BuyButtonHandler(instance, buyInventoryBuilder));
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        // Fix "Negative, non outside slot -1" error.
        if (e.getRawSlot() < 0) {
            return;
        }

        if (e.getInventory() == null
                || e.getInventory().getHolder() == null
                || !(e.getInventory().getHolder() instanceof CraftingStoreInventoryHolder)) {
            return;
        }

        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        CraftingStoreInventoryHolder holder =
                (CraftingStoreInventoryHolder) e.getInventory().getHolder();
        InventoryItem item = holder.getCsInventory().getByIndex(e.getRawSlot());
        if (item == null) {
            return;
        }

        @SuppressWarnings("unchecked") // Suppress unchecked cast warning
        InventoryItemHandler<InventoryItem> handler =
                (InventoryItemHandler<InventoryItem>) handlers.get(item.getClass());

        if (handler != null) {
            handler.handle(p, item, holder);
        }
    }
}
