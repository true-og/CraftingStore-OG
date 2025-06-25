package net.craftingstore.core.exceptions;

import java.io.IOException;

public class CraftingStoreApiException extends Exception {

    private static final long serialVersionUID = 1L;

    public CraftingStoreApiException(String s, IOException e) {
        super(s, e);
    }

    @Override
    public void printStackTrace() {
        super.getCause().printStackTrace();
    }
}
