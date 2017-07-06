package com.smartmux.photocutter.dialogfragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.smartmux.photocutter.MainActivity;
import com.smartmux.photocutter.R;
import com.smartmux.photocutter.adapter.RecyclerView_CommonAdapter;
import com.smartmux.photocutter.modelclass.ListData;
import com.smartmux.photocutter.utils.GetArrayItems;

import java.util.ArrayList;

public class FilterDialog extends DialogFragment {

    private static RecyclerView stickerRecyclerView;

    ArrayList<ListData> array = new ArrayList<ListData>();
    Bundle mArgs;
    boolean isPurchasedIC;



    public void onResume() {
        super.onResume();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        getDialog().getWindow().getAttributes().gravity = Gravity.BOTTOM;
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.width = (int) ( width);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(lp);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater
                .inflate(R.layout.common_dialog, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.AnimationBottomUpDown;



        array = GetArrayItems.getFilter();

        stickerRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_color_effect_view);
        stickerRecyclerView.setHasFixedSize(true);
        stickerRecyclerView.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.HORIZONTAL,
                false));

        populatRecyclerView();


        return rootView;
    }



    // populate the list view by adding data to arraylist
    private void populatRecyclerView() {

        RecyclerView_CommonAdapter adapter = new RecyclerView_CommonAdapter(
                getActivity(), array,isPurchasedIC);
        stickerRecyclerView.setAdapter(adapter);// set adapter on recyclerview
        adapter.notifyDataSetChanged();// Notify the adapter

        adapter.SetOnItemClickListener(new RecyclerView_CommonAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                ((MainActivity) getActivity()).new ApplyFilter(position).execute();;

                dismiss();

            }
        });
    }





}
