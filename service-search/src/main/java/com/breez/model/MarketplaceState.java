package com.breez.model;

import com.breez.service.MarketplaceService;

public class MarketplaceState {

	final MarketplaceService client;
	long cacheSize = -1;
	int nextPageToFetch = 1;
	boolean isExhausted = false;
	boolean fetchFailed = false;

	public MarketplaceState(MarketplaceService client) {
		this.client = client;
	}

	public MarketplaceService getClient() {
		return client;
	}

	public long getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(long cacheSize) {
		this.cacheSize = cacheSize;
	}

	public int getNextPageToFetch() {
		return nextPageToFetch;
	}

	public void setNextPageToFetch(int nextPageToFetch) {
		this.nextPageToFetch = nextPageToFetch;
	}

	public boolean isExhausted() {
		return isExhausted;
	}

	public void setExhausted(boolean exhausted) {
		isExhausted = exhausted;
	}

	public boolean isFetchFailed() {
		return fetchFailed;
	}

	public void setFetchFailed(boolean fetchFailed) {
		this.fetchFailed = fetchFailed;
	}

}
