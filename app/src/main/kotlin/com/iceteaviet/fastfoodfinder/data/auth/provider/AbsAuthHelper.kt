package com.iceteaviet.fastfoodfinder.data.auth.provider

/**
 * Created by tom on 2019-05-03.
 */
abstract class AbsAuthHelper<AC> protected constructor() : AuthHelper<AC> {

    protected var listener: AuthRequestListener<AC>? = null

    init {
        this.setupAuthProvider()
    }

    protected abstract fun setupAuthProvider()

    override fun setAuthRequestListener(listener: AuthRequestListener<AC>) {
        this.listener = listener
    }
}