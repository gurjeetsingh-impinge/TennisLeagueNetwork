package com.tennisdc.tln.common;

import android.content.Context;
import android.graphics.Typeface;
import android.util.SparseArray;

import java.lang.reflect.Field;

public class FontUtils {

    /**
     * List of created typefaces for later reused.
     */
    private final static SparseArray<Typeface> mTypefaces = new SparseArray<>(4);

    /**
     * Parse the attributes.
     *
     * @param context
     *            The Context the view is running in, through which it can
     *            access the current theme, resources, etc.
     * @param attrs
     *            The attributes of the XML tag that is inflating the view.
     */

	/*
     * private static void parseAttributes(Context context, AttributeSet attrs)
	 * { TypedArray values = context.obtainStyledAttributes(attrs,
	 * R.styleable.RobotoTextView);
	 * 
	 * int typefaceValue = values.getInt(R.styleable.RobotoTextView_typeface,
	 * 0); values.recycle();
	 * 
	 * setTypeface(obtainTypeface(context, typefaceValue)); }
	 */

    /**
     * @param context
     * @param nativeTypeface
     *         "MONOSPACE", "SERIF"
     * @param typefaceValue
     */
        public static void setReplaceNativeTypeface(Context context, NativeTypefaces nativeTypeface, AppTypefaces typefaceValue) {
        Typeface regular = obtainTypeface(context, typefaceValue.getValue());
        replaceFont(nativeTypeface.name(), regular);
    }

    /**
     * Obtain typeface.
     *
     * @param context
     *         The Context the view is running in, through which it can
     *         access the current theme, resources, etc.
     * @param typefaceValue
     *         values ​​for the "typeface" attribute
     *
     * @return Roboto {@link android.graphics.Typeface}
     *
     * @throws IllegalArgumentException
     *         if unknown `typeface` attribute value.
     */
    public static Typeface obtainTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
        Typeface typeface = mTypefaces.get(typefaceValue);
        if (typeface == null) {
            typeface = createTypeface(context, typefaceValue);
            mTypefaces.put(typefaceValue, typeface);
        }
        return typeface;
    }

    /**
     *
     * @param context
     * @param typefaceValue
     * @return
     * @throws IllegalArgumentException
     */
    private static Typeface createTypeface(Context context, int typefaceValue) throws IllegalArgumentException {
        return Typeface.createFromAsset(context.getAssets(), AppTypefaces.parseValue(typefaceValue).getPathInAsset());
    }

    protected static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static enum NativeTypefaces {
        MONOSPACE, SERIF, NORMAL, SANS
    }

    public static enum AppTypefaces {
        NOTEWORTHY_BOLD(1, "Noteworthy-Bold.ttf"),
        DEFAULT(NOTEWORTHY_BOLD);

        private final int mValue;
        private final String mPathInAsset;

        AppTypefaces(AppTypefaces appTypeface) {
            mValue = appTypeface.getValue();
            mPathInAsset = appTypeface.getPathInAsset();
        }

        AppTypefaces(int typeFace, String pathInAsset) {
            mValue = typeFace;
            mPathInAsset = pathInAsset;
        }

        public String getPathInAsset() {
            return mPathInAsset;
        }

        public int getValue() {
            return mValue;
        }

        public static AppTypefaces parseValue(int value) {
            switch (value) {
                case 1:
                    return NOTEWORTHY_BOLD;
                default:
                    return DEFAULT;
            }
        }

    }

}