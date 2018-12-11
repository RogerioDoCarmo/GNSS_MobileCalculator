package com.rogeriocarmo.gnss_mobilecalculator.View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rogeriocarmo.gnss_mobilecalculator.R;

public class Tab_CustomFragment extends Fragment {
    private String mText = "";
    public static Tab_CustomFragment createInstance(String txt)
    {
        Tab_CustomFragment fragment = new Tab_CustomFragment();
        fragment.mText = txt;
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_fragment_sample,container,false);
        ((TextView) v.findViewById(R.id.textView)).setText(mText);
        return v;
    }
}
