package net.craftingstore.core.http;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import net.craftingstore.core.CraftingStore;
import net.craftingstore.core.exceptions.CraftingStoreApiException;
import net.craftingstore.core.models.api.ApiInventory;
import net.craftingstore.core.models.api.ApiPayment;
import net.craftingstore.core.models.api.ApiTopDonator;

public class CraftingStoreCachedAPI extends CraftingStoreAPIImpl {

    private final HashMap<String, Object> cache = new HashMap<>();

    public CraftingStoreCachedAPI(CraftingStore instance) {
        super(instance);
    }

    @Override
    public Future<ApiInventory> getGUI() throws CraftingStoreApiException {
        return executor.submit(() -> {
            String key = "plugin/inventory";
            if (!cache.containsKey(key)) {
                cache.put(key, super.getGUI().get());
            }
            return (ApiInventory) cache.get(key);
        });
    }

    @Override
    public Future<ApiTopDonator[]> getTopDonators() throws CraftingStoreApiException {
        return executor.submit(() -> {
            String key = "buyers/top";
            if (cache.containsKey(key)) {
                return (ApiTopDonator[]) cache.get(key);
            }
            return null;
        });
    }

    @Override
    public Future<ApiPayment[]> getPayments() throws CraftingStoreApiException {
        return executor.submit(() -> {
            String key = "buyers/recent";
            if (cache.containsKey(key)) {
                return (ApiPayment[]) cache.get(key);
            }
            return null;
        });
    }

    public void refreshGUICache() throws CraftingStoreApiException, ExecutionException, InterruptedException {
        cache.put("plugin/inventory", super.getGUI().get());
    }

    public void refreshTopDonatorsCache() throws CraftingStoreApiException, ExecutionException, InterruptedException {
        cache.put("buyers/top", super.getTopDonators().get());
    }

    public void refreshPaymentsCache() throws CraftingStoreApiException, ExecutionException, InterruptedException {
        cache.put("buyers/recent", super.getPayments().get());
    }
}
