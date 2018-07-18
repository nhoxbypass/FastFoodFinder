package com.iceteaviet.fastfoodfinder.data.remote.user.model;

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;

/**
 * Created by tom on 7/17/18.
 */
public class UserStoreEvent {
    public static final int ACTION_ADDED = 0;
    public static final int ACTION_CHANGED = 1;
    public static final int ACTION_REMOVED = 2;
    public static final int ACTION_MOVED = 3;

    private Store store;
    private int eventActionCode;

    public UserStoreEvent(Store store, int eventActionCode) {
        this.store = store;
        this.eventActionCode = eventActionCode;
    }

    public Store getStore() {
        return store;
    }

    public int getEventActionCode() {
        return eventActionCode;
    }
}
