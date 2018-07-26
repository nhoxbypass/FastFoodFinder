package com.iceteaviet.fastfoodfinder.data.transport.model;

/**
 * Created by Genius Doan on 11/28/2016.
 */
public class SearchEventResult {
    public static final int SEARCH_ACTION_QUICK = 0;
    public static final int SEARCH_ACTION_QUERY_SUBMIT = 1;
    public static final int SEARCH_ACTION_COLLAPSE = 2;
    private int mResult;
    private String mSearchString;
    private int mStoreType;

    public SearchEventResult(int resultCode, String searchString, int storeType) {
        mResult = resultCode;
        mSearchString = searchString;
        mStoreType = storeType;
    }

    public SearchEventResult(int resultCode, int storeType) {
        mResult = resultCode;
        mStoreType = storeType;
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
