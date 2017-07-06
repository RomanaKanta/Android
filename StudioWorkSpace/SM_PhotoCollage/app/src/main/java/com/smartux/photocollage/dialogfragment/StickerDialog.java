package com.smartux.photocollage.dialogfragment;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.smartux.photocollage.CollageActivity;
import com.smartux.photocollage.R;
import com.smartux.photocollage.adapter.RecyclerView_StickerAdapter;
import com.smartux.photocollage.model.StickerCatagory;
import com.smartux.photocollage.utils.Constant;
import com.smartux.photocollage.utils.FrameSelection;

import java.util.ArrayList;

public class StickerDialog extends DialogFragment{

    private RecyclerView stickerRecyclerView;
    private RecyclerView catagoryRecyclerView;
    LinearLayout catagory_layer,sticker_layer;
    ImageView img_back;
    FrameLayout btn_emoticons, btn_masquerade, btn_sunglass;

    ArrayList<StickerCatagory> catagoryArrayList;
    ArrayList<Integer> emoticonsArrayList;
    ArrayList<Integer> masqueradeArrayList;
    ArrayList<Integer> sunglassArrayList;

    Bundle mArgs;
    boolean isPurchasedIC;

    Animation animViewOpen, animViewClose = null;

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
                .inflate(R.layout.sticker_dialog, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.AnimationBottomUpDown;

        mArgs = getArguments();
        isPurchasedIC = mArgs.getBoolean(Constant.PURCHASE);

        animViewOpen = AnimationUtils.loadAnimation(getActivity(),
                R.anim.push_left_in);
        animViewClose = AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_in);

        catagoryArrayList = FrameSelection.getStickerCatagory();
        emoticonsArrayList = FrameSelection.getEmoticonsItems();
        masqueradeArrayList = FrameSelection.getMasqueradeItems();
        sunglassArrayList = FrameSelection.getSunglassItems();

        stickerRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_sticker_view);
        stickerRecyclerView.setHasFixedSize(true);
        stickerRecyclerView.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.HORIZONTAL,
                false));

        catagory_layer = (LinearLayout) rootView.findViewById(R.id.sticker_catagory_layer);
        sticker_layer = (LinearLayout) rootView.findViewById(R.id.sticker_layout);

        img_back = (ImageView) rootView.findViewById(R.id.image_back);

        btn_emoticons = (FrameLayout) rootView.findViewById(R.id.sticker_emoticon);
        GradientDrawable drawable_emoticons = (GradientDrawable)  btn_emoticons
                .getBackground();
        drawable_emoticons.setColor(Color.parseColor("#8e3606"));

        btn_masquerade = (FrameLayout) rootView.findViewById(R.id.sticker_masquerade);
        GradientDrawable drawable_masquerade = (GradientDrawable)  btn_masquerade
                .getBackground();
        drawable_masquerade.setColor(Color.parseColor("#ac80c5"));

        btn_sunglass = (FrameLayout) rootView.findViewById(R.id.sticker_sunglass);
        GradientDrawable drawable_sunglass = (GradientDrawable)  btn_sunglass
                .getBackground();
        drawable_sunglass.setColor(Color.parseColor("#73d4b4"));


        btn_emoticons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sticker_layer.startAnimation(animViewOpen);
                sticker_layer.setVisibility(View.VISIBLE);
                catagory_layer.setVisibility(View.GONE);

                setStickerItems(emoticonsArrayList);
            }
        });

        btn_masquerade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sticker_layer.startAnimation(animViewOpen);
                sticker_layer.setVisibility(View.VISIBLE);
                catagory_layer.setVisibility(View.GONE);

                setStickerItems(masqueradeArrayList);
            }
        });

        btn_sunglass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sticker_layer.startAnimation(animViewOpen);
                sticker_layer.setVisibility(View.VISIBLE);
                catagory_layer.setVisibility(View.GONE);

                setStickerItems(sunglassArrayList);
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                catagory_layer.startAnimation(animViewClose);
                catagory_layer.setVisibility(View.VISIBLE);
                sticker_layer.setVisibility(View.GONE);
            }
        });

       /* catagoryRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_sticker_catagory_view);

        catagoryRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        catagoryRecyclerView.setLayoutManager(layoutManager);
        final int gridMargin = getResources().getDimensionPixelOffset(R.dimen.grid_margin);

        catagoryRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.set(gridMargin, gridMargin, gridMargin, gridMargin);
            }
        });

        catagoryRecyclerView.setPadding(gridMargin, gridMargin, gridMargin, gridMargin);

        RecyclerView_CatagoryAdapter catagoryAdapter =  new RecyclerView_CatagoryAdapter(getActivity(),catagoryArrayList);

        catagoryRecyclerView.setAdapter(catagoryAdapter);
        catagoryAdapter.notifyDataSetChanged();// Notify the adapter

        catagoryAdapter.SetOnItemClickListener(new RecyclerView_CatagoryAdapter.OnCatagoryItemClickListener() {
            @Override
            public void onCatagoryItemClick(View view, int position) {
                Log.e("LOG", " " + position);
            }
        });*/

        return rootView;
    }

    // populate the list view by adding data to arraylist
    private void setStickerItems(final ArrayList<Integer> array) {

        RecyclerView_StickerAdapter stickersAdapter = new RecyclerView_StickerAdapter(
                getActivity(), array,isPurchasedIC);
        stickerRecyclerView.setAdapter(stickersAdapter);// set adapter on recyclerview
        stickersAdapter.notifyDataSetChanged();// Notify the adapter

        stickersAdapter.SetOnItemClickListener(new RecyclerView_StickerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                    ((CollageActivity) getActivity())
                            .addStickerView(array.get(position));

                    dismiss();

//                    if (isPurchasedIC) {
//
//                        ((CollageActivity) getActivity()).addStickerView(array.get(position));
//
//                        dismiss();
//
//                    } else {
//
//                        if (position > 9) {
//                            ((CollageActivity) getActivity()).purchaseDialog(getString(R.string.purch_alart_msg2));
//                            dismiss();
//                        } else {
//                            ((CollageActivity) getActivity())
//                                    .addStickerView(array.get(position));
//
//                            dismiss();
//                        }
//                    }

            }
        });
    }



}
