package com.iceteaviet.fastfoodfinder.data.auth.provider

/**
 * Created by tom on 2019-05-02.
 */
interface AuthRequestListener<AC> {
    fun onSuccess(authCredential: AC, fromLastSignIn: Boolean)
    fun onFailed()
}