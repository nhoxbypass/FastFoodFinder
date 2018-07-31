package com.iceteaviet.fastfoodfinder.utils.exception;

/**
 * Created by tom on 7/31/18.
 */
public class EmptyParamsException extends Exception {
    public EmptyParamsException() {
        super("Params is empty");
    }
}
