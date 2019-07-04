package com.iceteaviet.fastfoodfinder.ui.custom.roboto

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

/**
 * Implementation of a {@link Button} with native support for all the Roboto fonts.
 *
 * Created by tom on 2019-06-30.
 */
class RobotoButton : AppCompatButton {
    /**
     * Constructor that is called when inflating a widget from XML. This is called
     * when a widget is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     *
     *
     *
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the widget is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the widget.
     * @see .RobotoButton
     */
    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        if (!isInEditMode) {
            RobotoTypefaces.setUpTypeface(this, context, attrs)
        }
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of View allows subclasses to use their own base style when
     * they are inflating.
     *
     * @param context  The Context the widget is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs    The attributes of the XML tag that is inflating the widget.
     * @param defStyle The default style to apply to this widget. If 0, no style
     * will be applied (beyond what is included in the theme). This may
     * either be an attribute resource, whose value will be retrieved
     * from the current theme, or an explicit style resource.
     * @see .RobotoButton
     */
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        if (!isInEditMode) {
            RobotoTypefaces.setUpTypeface(this, context, attrs)
        }
    }
}
/**
 * Simple constructor to use when creating a widget from code.
 *
 * @param context The Context the widget is running in, through which it can
 * access the current theme, resources, etc.
 */