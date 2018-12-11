package com.rogeriocarmo.gnss_mobilecalculator.View;

import android.app.Dialog;
//import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rogeriocarmo.gnss_mobilecalculator.Controller.Tab_CustomAdapter;
import com.rogeriocarmo.gnss_mobilecalculator.R;

public class TabbedDialog2 extends DialogFragment {

    TabLayout tabLayout;
    ViewPager viewPager;

    View v = null;



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );

        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialogBuilder.setTitle("Add Beacon");


        //dialogBuilder.setCancelable(true);

        // call default fragment methods and set view for dialog
        if(v==null)
            v = onCreateView(getActivity().getLayoutInflater(), null, null);
        onViewCreated(v, null);
        dialogBuilder.setView(v);


        AlertDialog dg = dialogBuilder.create();
        //dg.setCanceledOnTouchOutside(false);


        return dg;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(v==null) {

            v  = inflater.inflate(R.layout.tab_custom_dialog,container,false);

            tabLayout = (TabLayout) v.findViewById(R.id.tabLayout);
            viewPager = (ViewPager) v.findViewById(R.id.masterViewPager);

            Tab_CustomAdapter adapter = new Tab_CustomAdapter(getActivity().getSupportFragmentManager());
            adapter.addFragment("Rogerio",Tab_CustomAdapter.createInstance("Rogerio"));
            adapter.addFragment("Girl",Tab_CustomAdapter.createInstance("Stacy"));
            adapter.addFragment("Robot",Tab_CustomAdapter.createInstance("Aeon"));

            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
        }

        return v;
    }

}
