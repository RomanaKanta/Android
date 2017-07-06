package com.smartmux.couriermoc.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.smartmux.couriermoc.R;
import com.smartmux.couriermoc.adapter.LeftDrawerAdapter;
import com.smartmux.couriermoc.modelclass.DrawerOption;
import com.smartmux.couriermoc.modelclass.UserInfo;
import com.smartmux.couriermoc.utils.ItemClickSupport;
import com.smartmux.couriermoc.utils.PreferenceUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FragmentLeftDrawer extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public FragmentLeftDrawer() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentLeftDrawer newInstance(String param1, String param2) {
        FragmentLeftDrawer fragment = new FragmentLeftDrawer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    ArrayList<DrawerOption> items = new ArrayList<>();
    @Bind(R.id.user_name)
    TextView txtName;
    @Bind(R.id.user_email)
    TextView txtEmail;
    @Bind(R.id.recyclerViewDrawer)
    RecyclerView recyclerView;
    @Bind(R.id.imageViewCover)
    ImageView userPhoto;

    @OnClick(R.id.imageViewSetting)
    public void setSettingFrag() {

        mListener.setDrawerOption(21);

    }

    @OnClick(R.id.imageViewLogout)
    public void logout() {

        mListener.setDrawerOption(22);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);
        ButterKnife.bind(this, view);
        setUserInfo();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLongClickable(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        final int gridMargin = getResources().getDimensionPixelOffset(R.dimen.spacing);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.set(gridMargin, gridMargin, gridMargin, gridMargin);
            }
        });

        recyclerView.setPadding(gridMargin, gridMargin, gridMargin, gridMargin);


        items.add(new DrawerOption(R.drawable.arrange_delivery, getString(R.string.arrange)));
        items.add(new DrawerOption(R.drawable.delivery_status, getString(R.string.status)));
//        items.add(new DrawerOption(R.drawable.favorite, getString(R.string.favourites)));
        items.add(new DrawerOption(R.drawable.history, getString(R.string.history)));
        items.add(new DrawerOption(R.drawable.profile, getString(R.string.profile)));
        items.add(new DrawerOption(R.drawable.feedback, getString(R.string.feedback)));
        items.add(new DrawerOption(R.drawable.help, getString(R.string.help)));
        items.add(new DrawerOption(R.drawable.about_us, getString(R.string.about_us)));


        recyclerView.setAdapter(new LeftDrawerAdapter(getActivity(), items));

        ItemClickSupport itemClick = ItemClickSupport
                .addTo(recyclerView);
        itemClick
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        mListener.setDrawerOption(position);
                    }
                });

        return view;
    }


    private void setUserInfo() {

        Gson gson = new Gson();
        UserInfo obj = gson.fromJson(PreferenceUtils.getUserInfo(getActivity()), UserInfo.class);

        txtName.setText(obj.getName());
        txtEmail.setText(obj.getEmail());
        setPhoto(obj.getImageUrl());

    }

    private void setPhoto(String path) {

        if (!TextUtils.isEmpty(path)) {

            Glide.with(getActivity()).load(path).asBitmap().centerCrop()
                    .placeholder(R.drawable.loading)
                    .into(new BitmapImageViewTarget(userPhoto) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            userPhoto.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        } else {
            userPhoto.setImageResource(R.drawable.illust_029);
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * com.smartmux.pos.fragment to allow an interaction in this com.smartmux.pos.fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void setDrawerOption(int pos);

    }


}
