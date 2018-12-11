package com.rogeriocarmo.gnss_mobilecalculator.View;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rogeriocarmo.gnss_mobilecalculator.Controller.Tab_CustomAdapter;
import com.rogeriocarmo.gnss_mobilecalculator.R;

public class Tab_Dialog extends DialogFragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.tab_custom_dialog,container,false);
        tabLayout = (TabLayout) rootview.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) rootview.findViewById(R.id.masterViewPager);
        Tab_CustomAdapter adapter = new Tab_CustomAdapter(getChildFragmentManager());
        adapter.addFragment("Boy",Tab_CustomFragment.createInstance("John"));
        adapter.addFragment("Girl",Tab_CustomFragment.createInstance("Stacy"));
        adapter.addFragment("Robot",Tab_CustomFragment.createInstance("Aeon"));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return rootview;
    }

//    @Override
//    public void onStart()
//    {
//        super.onStart();
//        Dialog dialog = getDialog();
//        if (dialog != null)
//        {
//            int width = ViewGroup.LayoutParams.MATCH_PARENT;
//            int height = ViewGroup.LayoutParams.MATCH_PARENT;
//            dialog.getWindow().setLayout(width, height);
//        }
//    }
}
