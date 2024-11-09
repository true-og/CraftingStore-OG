package net.craftingstore.core.models.api;

public class Root<T> {

	private T result;
	private boolean success;

	public T getResult() {
		return result;
	}

	public boolean isSuccess() {
		return success;
	}

}