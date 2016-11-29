package com.example.mypc.fastfoodfinder.helper;

/**
 * Created by nhoxb on 11/28/2016.
 */
public class SearchResult {
    int mResult;
    String mSearchString;
    int mStoreType;

    public SearchResult(int resultCode, String searchString, int storeType) {
        mResult = resultCode;
        mSearchString = searchString;
        mStoreType = storeType;
    }

    public SearchResult(int resultCode, int storeType) {
        mResult = resultCode;
        mStoreType = mResult = resultCode;
    }

    public int getResultCode() {
        return mResult;
    }

    public String getSearchString() {
        return mSearchString;
    }

    public int getStoreType()
    {
        return mStoreType;
    }
}
