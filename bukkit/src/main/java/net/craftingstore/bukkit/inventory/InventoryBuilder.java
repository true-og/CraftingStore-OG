package net.craftingstore.bukkit.inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.text.StringSubstitutor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.craftingstore.bukkit.CraftingStoreBukkit;
import net.craftingstore.bukkit.util.TextUtil;
import net.craftingstore.bukkit.util.VersionUtil;
import net.craftingstore.core.models.api.inventory.CraftingStoreInventory;
import net.craftingstore.core.models.api.inventory.InventoryItem;
import net.kyori.adventure.text.TextComponent;

public class InventoryBuilder {

	private final InventoryItemBuilder inventoryItemBuilder;

	public InventoryBuilder(CraftingStoreBukkit instance) {
		this.inventoryItemBuilder = new InventoryItemBuilder(instance);
	}

	public Inventory buildInventory(CraftingStoreInventory csInventory) {
		return buildInventory(csInventory, new CraftingStoreInventoryHolder(csInventory, null));
	}

	public Inventory buildInventory(CraftingStoreInventory csInventory, CraftingStoreInventoryHolder holder) {
		return buildInventory(csInventory, holder, new HashMap<>());
	}

	public Inventory buildInventory(CraftingStoreInventory csInventory, CraftingStoreInventoryHolder holder, Map<String, ?> placeholders) {
		TextComponent title = TextUtil.legacySerializerAnyCase(csInventory.getTitle());
		if (title == null || title.content().isEmpty()) {
			title = TextUtil.legacySerializerAnyCase("CraftingStore");
		}

		StringSubstitutor stringSubstitutor = new StringSubstitutor(placeholders, "{", "}");
		title = TextUtil.legacySerializerAnyCase(stringSubstitutor.replace(title));
		Inventory inventory = Bukkit.createInventory(holder, csInventory.getSize(), title);

		for (InventoryItem inventoryItem : csInventory.getContent()) {
			ItemStack itemStack = this.inventoryItemBuilder.getItemStack(inventoryItem.getIcon().getMaterial(), inventoryItem.getIcon().getAmount());
			ItemMeta meta = itemStack.getItemMeta();
			if (meta != null) {
				meta.displayName(TextUtil.legacySerializerAnyCase(stringSubstitutor.replace(inventoryItem.getName())));
				if (inventoryItem.getDescription() != null && inventoryItem.getDescription().length != 0) {
					meta.lore(Arrays.stream(inventoryItem.getDescription())
							.map(stringSubstitutor::replace)
							.map(TextUtil::legacySerializerAnyCase)
							.collect(Collectors.toList()));
				}
				if (VersionUtil.isCustomModalDataAvailable() && inventoryItem.getIcon().getCustomModelData() != null) {
					meta.setCustomModelData(inventoryItem.getIcon().getCustomModelData());
				}
				itemStack.setItemMeta(meta);
			}
			inventory.setItem(inventoryItem.getIndex(), itemStack);
		}

		return inventory;
	}

}