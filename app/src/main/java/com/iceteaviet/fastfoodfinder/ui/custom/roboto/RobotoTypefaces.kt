package com.iceteaviet.fastfoodfinder.ui.custom.roboto

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.iceteaviet.fastfoodfinder.R


/**
 * Utilities for working with roboto typefaces.
 */
object RobotoTypefaces {

    const val TYPEFACE_ROBOTO_THIN = 0
    const val TYPEFACE_ROBOTO_THIN_ITALIC = 1
    const val TYPEFACE_ROBOTO_REGULAR = 4
    const val TYPEFACE_ROBOTO_ITALIC = 5
    const val TYPEFACE_ROBOTO_BOLD = 8
    const val TYPEFACE_ROBOTO_BOLD_ITALIC = 9
    const val TYPEFACE_ROBOTO_SLAB_THIN = 18
    const val TYPEFACE_ROBOTO_SLAB_REGULAR = 20
    const val TYPEFACE_ROBOTO_SLAB_BOLD = 21

    const val FONT_FAMILY_ROBOTO = 0
    //const val FONT_FAMILY_ROBOTO_CONDENSED = 1
    const val FONT_FAMILY_ROBOTO_SLAB = 2
    //const val FONT_FAMILY_ROBOTO_MONO = 3

    const val TEXT_WEIGHT_NORMAL = 0
    const val TEXT_WEIGHT_THIN = 1
    //const val TEXT_WEIGHT_LIGHT = 2
    //const val TEXT_WEIGHT_MEDIUM = 3
    const val TEXT_WEIGHT_BOLD = 4
    //const val TEXT_WEIGHT_ULTRA_BOLD = 5

    const val TEXT_STYLE_NORMAL = 0
    const val TEXT_STYLE_ITALIC = 1

    private const val DEFAULT_TYPEFACE = TYPEFACE_ROBOTO_REGULAR
    private const val DEFAULT_FONT_FAMILY = FONT_FAMILY_ROBOTO
    private const val DEFAULT_TEXT_WEIGHT = TEXT_WEIGHT_NORMAL
    private const val DEFAULT_TEXT_STYLE = TEXT_STYLE_NORMAL

    /**
     * Array of created typefaces for later reused.
     */
    private val typefacesCache = SparseArray<Typeface>(32)

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(TYPEFACE_ROBOTO_THIN, TYPEFACE_ROBOTO_THIN_ITALIC, /*TYPEFACE_ROBOTO_LIGHT, TYPEFACE_ROBOTO_LIGHT_ITALIC,*/ TYPEFACE_ROBOTO_REGULAR, TYPEFACE_ROBOTO_ITALIC, /*TYPEFACE_ROBOTO_MEDIUM, TYPEFACE_ROBOTO_MEDIUM_ITALIC,*/ TYPEFACE_ROBOTO_BOLD, TYPEFACE_ROBOTO_BOLD_ITALIC, /*TYPEFACE_ROBOTO_BLACK, TYPEFACE_ROBOTO_BLACK_ITALIC,*/ TYPEFACE_ROBOTO_SLAB_THIN, /*TYPEFACE_ROBOTO_SLAB_LIGHT,*/ TYPEFACE_ROBOTO_SLAB_REGULAR, TYPEFACE_ROBOTO_SLAB_BOLD)
    annotation class RobotoTypeface

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(FONT_FAMILY_ROBOTO, /*FONT_FAMILY_ROBOTO_CONDENSED,*/ FONT_FAMILY_ROBOTO_SLAB/*, FONT_FAMILY_ROBOTO_MONO*/)
    annotation class RobotoFontFamily

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(TEXT_WEIGHT_NORMAL, TEXT_WEIGHT_THIN, /*TEXT_WEIGHT_LIGHT, TEXT_WEIGHT_MEDIUM,*/ TEXT_WEIGHT_BOLD/*, TEXT_WEIGHT_ULTRA_BOLD*/)
    annotation class RobotoTextWeight


    @Retention(AnnotationRetention.SOURCE)
    @IntDef(TEXT_STYLE_NORMAL, TEXT_STYLE_ITALIC)
    annotation class RobotoTextStyle

    /**
     * Obtain typeface.
     *
     * @param context       The Context the widget is running in, through which it can access the current theme, resources, etc.
     * @param typefaceValue The value of "robotoTypeface" attribute
     * @return specify [Typeface] or throws IllegalArgumentException if unknown `robotoTypeface` attribute value.
     */
    @NonNull
    fun obtainTypeface(@NonNull context: Context, @RobotoTypeface typefaceValue: Int): Typeface? {
        var typeface: Typeface? = typefacesCache.get(typefaceValue)
        if (typeface == null) {
            typeface = createTypeface(context, typefaceValue)
            typefacesCache.put(typefaceValue, typeface)
        }
        return typeface
    }

    /**
     * Obtain typeface.
     *
     * @param context    The Context the widget is running in, through which it can access the current theme, resources, etc.
     * @param fontFamily The value of "robotoFontFamily" attribute
     * @param textWeight The value of "robotoTextWeight" attribute
     * @param textStyle  The value of "robotoTextStyle" attribute
     * @return specify [Typeface] or throws IllegalArgumentException.
     */
    @NonNull
    fun obtainTypeface(@NonNull context: Context, @RobotoFontFamily fontFamily: Int,
                       @RobotoTextWeight textWeight: Int, @RobotoTextStyle textStyle: Int): Typeface? {
        @RobotoTypeface val typeface: Int
        if (fontFamily == FONT_FAMILY_ROBOTO) {
            if (textStyle == TEXT_STYLE_NORMAL) {
                when (textWeight) {
                    TEXT_WEIGHT_NORMAL -> typeface = TYPEFACE_ROBOTO_REGULAR
                    TEXT_WEIGHT_THIN -> typeface = TYPEFACE_ROBOTO_THIN
                    TEXT_WEIGHT_BOLD -> typeface = TYPEFACE_ROBOTO_BOLD
                    else -> throw IllegalArgumentException("`robotoTextWeight` attribute value " + textWeight + " is not supported for this fontFamily " + fontFamily + " and textStyle " + textStyle)
                }
            } else if (textStyle == TEXT_STYLE_ITALIC) {
                when (textWeight) {
                    TEXT_WEIGHT_NORMAL -> typeface = TYPEFACE_ROBOTO_ITALIC
                    TEXT_WEIGHT_THIN -> typeface = TYPEFACE_ROBOTO_THIN_ITALIC
                    TEXT_WEIGHT_BOLD -> typeface = TYPEFACE_ROBOTO_BOLD_ITALIC
                    else -> throw IllegalArgumentException("`robotoTextWeight` attribute value " + textWeight + " is not supported for this fontFamily " + fontFamily + " and textStyle " + textStyle)
                }
            } else {
                throw IllegalArgumentException("`robotoTextStyle` attribute value " + textStyle + " is not supported for this fontFamily " + fontFamily)
            }
        } else if (fontFamily == FONT_FAMILY_ROBOTO_SLAB) {
            if (textStyle == TEXT_STYLE_NORMAL) {
                when (textWeight) {
                    TEXT_WEIGHT_NORMAL -> typeface = TYPEFACE_ROBOTO_SLAB_REGULAR
                    TEXT_WEIGHT_THIN -> typeface = TYPEFACE_ROBOTO_SLAB_THIN
                    TEXT_WEIGHT_BOLD -> typeface = TYPEFACE_ROBOTO_SLAB_BOLD
                    else -> throw IllegalArgumentException("`robotoTextWeight` attribute value " + textWeight + " is not supported for this fontFamily " + fontFamily + " and textStyle " + textStyle)
                }
            } else {
                throw IllegalArgumentException("`robotoTextStyle` attribute value " + textStyle + " is not supported for this fontFamily " + fontFamily)
            }
        } else {
            throw IllegalArgumentException("Unknown `robotoFontFamily` attribute value $fontFamily")
        }
        return obtainTypeface(context, typeface)
    }

    /**
     * Create typeface from assets.
     *
     * @param context  The Context the widget is running in, through which it can
     * access the current theme, resources, etc.
     * @param typeface The value of "robotoTypeface" attribute
     * @return Roboto [Typeface] or throws IllegalArgumentException if unknown `robotoTypeface` attribute value.
     */
    @NonNull
    private fun createTypeface(@NonNull context: Context, @RobotoTypeface typeface: Int): Typeface {
        val path: String
        when (typeface) {
            TYPEFACE_ROBOTO_THIN -> path = "fonts/Roboto-Thin.ttf"
            TYPEFACE_ROBOTO_THIN_ITALIC -> path = "fonts/Roboto-ThinItalic.ttf"
            TYPEFACE_ROBOTO_REGULAR -> path = "fonts/Roboto-Regular.ttf"
            TYPEFACE_ROBOTO_ITALIC -> path = "fonts/Roboto-Italic.ttf"
            TYPEFACE_ROBOTO_BOLD -> path = "fonts/Roboto-Bold.ttf"
            TYPEFACE_ROBOTO_BOLD_ITALIC -> path = "fonts/Roboto-BoldItalic.ttf"
            TYPEFACE_ROBOTO_SLAB_THIN -> path = "fonts/RobotoSlab-Thin.ttf"
            TYPEFACE_ROBOTO_SLAB_REGULAR -> path = "fonts/RobotoSlab-Regular.ttf"
            TYPEFACE_ROBOTO_SLAB_BOLD -> path = "fonts/RobotoSlab-Bold.ttf"
            else -> throw IllegalArgumentException("Unknown `robotoTypeface` attribute value $typeface")
        }
        return Typeface.createFromAsset(context.getAssets(), path)
    }

    /**
     * Obtain typeface from attributes.
     *
     * @param context The Context the widget is running in, through which it can access the current theme, resources, etc.
     * @param attrs   The styled attribute values in this Context's theme.
     * @return specify [Typeface]
     */
    @NonNull
    fun obtainTypeface(@NonNull context: Context, @NonNull attrs: TypedArray): Typeface? {
        if (attrs.hasValue(R.styleable.RobotoTextView_robotoTypeface)) {
            @RobotoTypeface val typefaceValue = attrs.getInt(R.styleable.RobotoTextView_robotoTypeface, DEFAULT_TYPEFACE)
            return obtainTypeface(context, typefaceValue)
        } else {
            @RobotoFontFamily val fontFamily = attrs.getInt(R.styleable.RobotoTextView_robotoFontFamily, DEFAULT_FONT_FAMILY)
            @RobotoTextWeight val textWeight = attrs.getInt(R.styleable.RobotoTextView_robotoTextWeight, DEFAULT_TEXT_WEIGHT)
            @RobotoTextStyle val textStyle = attrs.getInt(R.styleable.RobotoTextView_robotoTextStyle, DEFAULT_TEXT_STYLE)
            return obtainTypeface(context, fontFamily, textWeight, textStyle)
        }
    }

    /**
     * Set up typeface for TextView from the attributes.
     *
     * @param textView The roboto text view
     * @param context  The context the widget is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs    The attributes of the XML tag that is inflating the widget.
     */
    fun setUpTypeface(@NonNull textView: TextView, @NonNull context: Context, @Nullable attrs: AttributeSet?) {
        val typeface: Typeface?
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.RobotoTextView)
            try {
                typeface = obtainTypeface(context, a)
            } finally {
                a.recycle()
            }
        } else {
            typeface = obtainTypeface(context, TYPEFACE_ROBOTO_REGULAR)
        }
        setUpTypeface(textView, typeface)
    }

    /**
     * Set up typeface for TextView.
     *
     * @param textView The text view
     * @param typeface The value of "robotoTypeface" attribute
     */
    /*fun setUpTypeface(@NonNull textView: TextView, @RobotoTypeface typeface: Int) {
        setUpTypeface(textView, obtainTypeface(textView.context, typeface))
    }*/

    /**
     * Set up typeface for TextView.
     *
     * @param textView The text view
     * @param fontFamily The value of "robotoFontFamily" attribute
     * @param textWeight The value of "robotoTextWeight" attribute
     * @param textStyle  The value of "robotoTextStyle" attribute
     */
    /*fun setUpTypeface(@NonNull textView: TextView, @RobotoFontFamily fontFamily: Int,
                      @RobotoTextWeight textWeight: Int, @RobotoTextStyle textStyle: Int) {
        setUpTypeface(textView, obtainTypeface(textView.context, fontFamily, textWeight, textStyle))
    }*/

    /**
     * Set up typeface for TextView. Wrapper over [TextView.setTypeface]
     * for making the font anti-aliased.
     *
     * @param textView The text view
     * @param typeface The specify typeface
     */
    fun setUpTypeface(@NonNull textView: TextView, @NonNull typeface: Typeface?) {
        textView.paintFlags = textView.paintFlags or Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG
        textView.typeface = typeface
    }

    /**
     * Set up typeface for Paint. Wrapper over [Paint.setTypeface]
     * for making the font anti-aliased.
     *
     * @param paint    The paint
     * @param typeface The specify typeface
     */
    /*fun setUpTypeface(@NonNull paint: Paint, @NonNull typeface: Typeface) {
        paint.setFlags(paint.getFlags() or Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG)
        paint.setTypeface(typeface)
    }*/
}