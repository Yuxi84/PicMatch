package com.example.yuxi.picmatching;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageButton;

/**
 * Created by Yuxi on 9/30/2016.
 */
public class SetImageTask extends AsyncTask<Integer, Void, Drawable>{

    ImageButton ib = null;

    public SetImageTask(ImageButton v){
        ib = v;
    }

    @Override
    protected Drawable doInBackground(Integer... integers) {

        return null;
    }

    @Override
    protected void onPostExecute(Drawable drawable) {
        super.onPostExecute(drawable);
    }
}
