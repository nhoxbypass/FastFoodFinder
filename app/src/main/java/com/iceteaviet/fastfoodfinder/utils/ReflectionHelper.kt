package com.iceteaviet.fastfoodfinder.utils

import java.lang.reflect.Field
import java.lang.reflect.Modifier


/**
 * Created by tom on 2019-06-09.
 */

@Throws(Exception::class)
fun setFinalStatic(field: Field, newValue: Any) {
    field.isAccessible = true

    val modifiersField = Field::class.java.getDeclaredField("modifiers")
    modifiersField.isAccessible = true
    modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())

    field.set(null, newValue)
}