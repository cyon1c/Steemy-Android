package io.steemapp.steemy.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.HashMap;

import io.steemapp.steemy.R;
import io.steemapp.steemy.SteemyGlobals;

/**
 * Created by John on 7/28/2016.
 */
public class SteemyTextView extends TextView {

    private Context mContext;

    private static final String ASSET_FOLDER = "fonts/";


    public SteemyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        extractAttrs(attrs);
    }

    private void extractAttrs(AttributeSet attrs){
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.io_steemy_steemy_views_SteemyTextView);
        try{
            String fontName = a.getString(R.styleable.io_steemy_steemy_views_SteemyTextView_typeface);
            Typeface typeface = SteemyGlobals.typefaces.get(fontName);
            if(typeface == null) {
                typeface = Typeface.createFromAsset(mContext.getAssets(), ASSET_FOLDER + fontName);
                SteemyGlobals.typefaces.put(fontName, typeface);
            }
            if(typeface != null)
                setTypeface(typeface);
        }catch(Exception e) {

        }


    }
}
