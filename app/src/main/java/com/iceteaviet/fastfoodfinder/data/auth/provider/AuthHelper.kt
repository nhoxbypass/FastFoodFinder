package com.iceteaviet.fastfoodfinder.data.auth.provider

import android.content.Intent

/**
 * Created by tom on 2019-05-02.
 */
interface AuthHelper<AC> {
    fun setAuthRequestListener(listener: AuthRequestListener<AC>)
    fun startRequestAuthCredential()
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent)
}