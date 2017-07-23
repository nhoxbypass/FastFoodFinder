package com.iceteaviet.fastfoodfinder.model.store;

/**
 * Created by Genius Doan on 11/28/2016.
 */
public class SearchEventResult {
    public static final int SEARCH_QUICK_OK = 0;
    public static final int SEARCH_STORE_OK = 1;
    public static final int SEARCH_COLLAPSE = 2;
    int mResult;
    String mSearchString;
    int mStoreType;

    public SearchEventResult(int resultCode, String searchString, int storeType) {
        mResult = resultCode;
        mSearchString = searchString;
        mStoreType = storeType;
    }

    public SearchEventResult(int resultCode, int storeType) {
        mResult = resultCode;
        mStoreType = mResult = resultCode;
    }

    public SearchEventResult(int resultCode, String searchString) {
        mResult = resultCode;
        mSearchString = searchString;
    }

    public SearchEventResult(int resultCode) {
        mResult = resultCode;
    }

    public int getResultCode() {
        return mResult;
    }

    public String getSearchString() {
        return mSearchString;
    }

    public int getStoreType() {
        return mStoreType;
    }
}
