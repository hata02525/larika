package com.example.fluper.larika_user_app.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by rohit on 5/6/17.
 */

public class MonstratRegularTextView extends android.support.v7.widget.AppCompatTextView  {


    public MonstratRegularTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MonstratRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MonstratRegularTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Montserrat-Regular.otf");
        setTypeface(tf ,0);

    }

}


