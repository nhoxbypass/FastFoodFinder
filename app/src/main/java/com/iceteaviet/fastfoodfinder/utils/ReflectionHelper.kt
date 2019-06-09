package com.iceteaviet.fastfoodfinder.utils

import java.lang.reflect.Field
import java.lang.reflect.Modifier


/**
 * Created by tom on 2019-06-09.
 */

@Throws(Exception::class)
fun setFinalStatic(field: Field, newValue: Any) {
    field.setAccessible(true)

    val modifiersField = Field::class.java.getDeclaredField("modifiers")
    modifiersField.setAccessible(true)
    modifiersField.setInt(field, field.getModifiers() and Modifier.FINAL.inv())

    field.set(null, newValue)
}