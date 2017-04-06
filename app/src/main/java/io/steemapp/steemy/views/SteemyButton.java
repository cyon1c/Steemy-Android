package io.steemapp.steemy.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import io.steemapp.steemy.R;
import io.steemapp.steemy.SteemyGlobals;

/**
 * Created by John on 10/3/2015.
 */
public class SteemyButton extends Button {

    private static final String ASSET_FOLDER = "fonts/";

    public SteemyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadTypeface(context, attrs);
    }

    public SteemyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadTypeface(context, attrs);
    }

    private void loadTypeface(Context context, AttributeSet attrs) {
        // used to view xml layouts within Android Studio
        if (isInEditMode()) return;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.io_steemy_steemy_views_SteemyTextView);
        try{
            String fontName = a.getString(R.styleable.io_steemy_steemy_views_SteemyTextView_typeface);
            Typeface typeface = SteemyGlobals.typefaces.get(fontName);
            if(typeface == null) {
                typeface = Typeface.createFromAsset(context.getAssets(), ASSET_FOLDER + fontName);
                SteemyGlobals.typefaces.put(fontName, typeface);
            }
            if(typeface != null)
                setTypeface(typeface);
        }catch(Exception e) {

        }
    }
}
