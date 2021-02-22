package com.uc.vlogshippedclient.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.uc.vlogshippedclient.R;

import java.util.ArrayList;

public class CustomSpinnerAdapater extends BaseAdapter implements SpinnerAdapter {

    private final Context activity;
    private ArrayList<String> asr;

    public CustomSpinnerAdapater(Context context, ArrayList<String> asr) {
        this.asr = asr;
        activity = context;
    }

    @Override
    public int getCount() {
        return asr.size();
    }

    @Override
    public Object getItem(int position) {
        return asr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(activity);
        txt.setGravity(Gravity.CENTER);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(16);
        txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
        txt.setText(asr.get(position));
        Typeface face = Typeface.createFromAsset(activity.getAssets(),
                "fonts/OPENSANS-REGULAR.ttf");
        txt.setTypeface(face);
        //txt.setTextColor(Color.parseColor("#000000"));
        return txt;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(activity);
        txt.setPadding(16, 18, 16, 18);
        txt.setTextSize(16);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setText(asr.get(position));
        txt.setBackground(ContextCompat.getDrawable(activity, R.drawable.spinner_item_divider));
        Typeface face = Typeface.createFromAsset(activity.getAssets(),
                "fonts/OPENSANS-REGULAR.ttf");
        txt.setTypeface(face);
        // txt.setTextColor(Color.parseColor("#000000"));
        return txt;
    }
}
