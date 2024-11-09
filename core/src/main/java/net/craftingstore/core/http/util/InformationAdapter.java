package net.craftingstore.core.http.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import net.craftingstore.core.models.api.provider.ProviderInformation;
import net.craftingstore.core.models.api.provider.ProviderType;

public class InformationAdapter implements JsonDeserializer<ProviderInformation> {

	public ProviderInformation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		JsonPrimitive prim = (JsonPrimitive) jsonObject.get("type");
		ProviderType providerType = ProviderType.valueOf(prim.getAsString());
		Class<?> klass = providerType.getActualClass();
		return context.deserialize(jsonObject, klass);
	}

}