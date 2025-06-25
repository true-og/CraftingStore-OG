package net.craftingstore.core.http.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import net.craftingstore.core.models.api.inventory.InventoryItem;
import net.craftingstore.core.models.api.inventory.InventoryItemType;

public class InventoryAdapter implements JsonDeserializer<InventoryItem> {

    public InventoryItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonPrimitive prim = (JsonPrimitive) jsonObject.get("type");
        InventoryItemType itemType = InventoryItemType.valueOf(prim.getAsString());
        Class<? extends InventoryItem> klass = itemType.getActualClass();
        return context.deserialize(jsonObject, klass);
    }
}
