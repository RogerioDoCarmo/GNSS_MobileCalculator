package com.rogeriocarmo.gnss_mobilecalculator.Controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rogeriocarmo.gnss_mobilecalculator.R;
import com.rogeriocarmo.gnss_mobilecalculator.View.Fragment_RecyclerView_Epchs.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display an item and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class RecyclerViewAdapter_Epchs extends RecyclerView.Adapter<RecyclerViewAdapter_Epchs.ViewHolder> {

    private final List<String> mValues;
    private final OnListFragmentInteractionListener mListener;
    private SingletronController controller;


    public RecyclerViewAdapter_Epchs(OnListFragmentInteractionListener listener) {
        controller = SingletronController.getInstance();
        mValues = load_data();

        mListener = listener;
    }

    private List<String> load_data() {
        return new ArrayList<>(Arrays.asList(controller.getListaEpocas()));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_epch_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.mItem = mValues.get(position);
        holder.mIdView.setText(String.valueOf(position + 1));
        holder.mContentView.setText(mValues.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
                    mListener.onListFragmentInteraction();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.id_content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
