package com.smartmux.shopsy.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.smartmux.shopsy.R;
import com.smartmux.shopsy.adapter.ProductItemAdapter;
import com.smartmux.shopsy.modelclass.AppData;
import com.smartmux.shopsy.modelclass.ProductModel;
import com.smartmux.shopsy.utils.Constant;
import com.smartmux.shopsy.widget.AppButton;
import com.smartmux.shopsy.widget.ProgressHUD;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tanvir-android on 7/26/16.
 */
public class FragmentList extends Fragment implements ObservableScrollViewCallbacks {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mPARAM1;
    private String mPARAM2;
    private String mPARAM3 = "0";


    private OnFragmentInteractionListener mListener;


    public FragmentList() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentList newInstance(String param1, String param2) {
        FragmentList fragment = new FragmentList();
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
            mPARAM1 = getArguments().getString(ARG_PARAM1);
            mPARAM2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Bind(R.id.recycler)
    ObservableRecyclerView recyclerView;

    @Bind(R.id.header)
    View mHeaderView;

    @Bind(R.id.content_banner)
    View mBannerView;

    private int mBaseTranslationY;
    View recycleHeaderView;

    ProductItemAdapter adapter;

    @Bind(R.id.btn_advice)
    AppButton btn_new;

    @OnClick(R.id.btn_advice)
    public void setAdvice() {
        setButtonColor(Constant.PRO_ADVICE);
    }

    @Bind(R.id.btn_recent)
    AppButton btn_popular;

    @OnClick(R.id.btn_recent)
    public void setRecent() {
        setButtonColor(Constant.PRO_RECENT);
    }

    @Bind(R.id.btn_popular)
    AppButton btn_sale;

    @OnClick(R.id.btn_popular)
    public void setPopular() {
        setButtonColor(Constant.PRO_POPULAR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        ButterKnife.bind(this, view);
        ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));

        setList();

        FragmentBanner fragmentBanner = FragmentBanner.newInstance("", "");
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_banner, fragmentBanner).commit();
        setButtonColor(Constant.PRO_ADVICE);

        return view;
    }

    private void setList() {

        recyclerView.setScrollViewCallbacks(this);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getActivity().getResources()
                .getInteger(R.integer.grid_number_cols)));
        final int gridMargin = getResources().getDimensionPixelOffset(R.dimen.grid_margin);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.set(gridMargin, gridMargin, gridMargin, gridMargin);
            }
        });

        recyclerView.setPadding(gridMargin, gridMargin, gridMargin, gridMargin);

        recycleHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_header, null);
        adapter = new ProductItemAdapter(getActivity(), new ArrayList<ProductModel>(), recycleHeaderView);

        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (dragging) {
            int toolbarHeight = mBannerView.getHeight();
            if (firstScroll) {
                float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
                if (-toolbarHeight < currentHeaderTranslationY) {
                    mBaseTranslationY = scrollY;
                }
            }
            float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;

        if (scrollState == ScrollState.DOWN) {
            showBannerView();
        } else if (scrollState == ScrollState.UP) {
            int toolbarHeight = mBannerView.getHeight();
            int scrollY = recyclerView.getCurrentScrollY();
            if (toolbarHeight <= scrollY) {
                hideBannerView();
            } else {
                showBannerView();
            }
        } else {
            // Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
            if (!toolbarIsShown() && !toolbarIsHidden()) {
                // Toolbar is moving but doesn't know which to move:
                // you can change this to hideBannerView()
                showBannerView();
            }
        }
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mHeaderView) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mHeaderView) == -mBannerView.getHeight();
    }

    private void showBannerView() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
        }
    }

    private void hideBannerView() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        int toolbarHeight = mBannerView.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
        }
    }


    private void setButtonColor(String pos) {

        mPARAM3 = pos;

        if (pos.equals(Constant.PRO_ADVICE)) {

            selected(btn_new);
            unSelected(btn_popular);
            unSelected(btn_sale);

        } else if (pos.equals(Constant.PRO_RECENT)) {

            unSelected(btn_new);
            selected(btn_popular);
            unSelected(btn_sale);

        } else if (pos.equals(Constant.PRO_POPULAR)) {

            unSelected(btn_new);
            unSelected(btn_popular);
            selected(btn_sale);

        } else {

            unSelected(btn_new);
            unSelected(btn_popular);
            unSelected(btn_sale);

        }
        new SM_AsyncTaskForGetData(mPARAM1, mPARAM2, mPARAM3).execute();

    }

    private void selected(AppButton btn) {

        btn.setBackgroundResource(R.drawable.button_bg_blue);
        btn.setTextColor(Color.parseColor("#ffffff"));
    }

    private void unSelected(AppButton btn) {

        btn.setBackgroundResource(R.drawable.button_bg_white);
        btn.setTextColor(Color.parseColor("#000000"));
    }

    class SM_AsyncTaskForGetData extends AsyncTask<String, String, ArrayList<ProductModel>> {

        ProgressHUD mProgressHUD;
        String error = "";
        String catagory = "";
        String subCatagory = "";
        String pro_tag = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressHUD = ProgressHUD.show(getActivity(),
                    "Loading", true);
        }

        public SM_AsyncTaskForGetData(String catagory, String subCatagory, String pro_tag) {

            this.catagory = catagory;
            this.subCatagory = subCatagory;
            this.pro_tag = pro_tag;

        }

        @Override
        protected ArrayList<ProductModel> doInBackground(String... params) {

            if (catagory.equals(Constant.MENS)) {

                ArrayList<ProductModel> mensProductList = new ArrayList<>();
                for (int i = 0; i < AppData.mensProduct.size(); i++) {

                    ProductModel product = AppData.mensProduct.get(i);

                    if (pro_tag.equals(Constant.PRO_ADVICE) && product.getType().equals(Constant.PRO_ADVICE)) {

                        if (subCatagory.equals(Constant.ALL)) {

                            mensProductList.add(product);

                        } else if (subCatagory.equals(product.getSub_catarogy())) {

                            mensProductList.add(product);
                        } else {
                        }

                    } else if (pro_tag.equals(Constant.PRO_RECENT) && product.getType().equals(Constant.PRO_RECENT)) {

                        if (subCatagory.equals(Constant.ALL)) {

                            mensProductList.add(product);

                        } else if (subCatagory.equals(product.getSub_catarogy())) {

                            mensProductList.add(product);
                        } else {
                        }

                    } else if (pro_tag.equals(Constant.PRO_POPULAR) && product.getType().equals(Constant.PRO_POPULAR)) {

                        if (subCatagory.equals(Constant.ALL)) {

                            mensProductList.add(product);

                        } else if (subCatagory.equals(product.getSub_catarogy())) {

                            mensProductList.add(product);
                        } else {
                        }

                    } else {
                    }

                }

                return mensProductList;


            } else if (catagory.equals(Constant.WOMENS)) {

                ArrayList<ProductModel> womensProductList = new ArrayList<>();
                for (int i = 0; i < AppData.womensProduct.size(); i++) {

                    ProductModel product = AppData.womensProduct.get(i);

                    if (pro_tag.equals(Constant.PRO_ADVICE) && product.getType().equals(Constant.PRO_ADVICE)) {

                        if (subCatagory.equals(Constant.ALL)) {

                            womensProductList.add(product);

                        } else if (subCatagory.equals(product.getSub_catarogy())) {

                            womensProductList.add(product);
                        } else {
                        }

                    } else if (pro_tag.equals(Constant.PRO_RECENT) && product.getType().equals(Constant.PRO_RECENT)) {

                        if (subCatagory.equals(Constant.ALL)) {

                            womensProductList.add(product);

                        } else if (subCatagory.equals(product.getSub_catarogy())) {

                            womensProductList.add(product);
                        } else {
                        }

                    } else if (pro_tag.equals(Constant.PRO_POPULAR) && product.getType().equals(Constant.PRO_POPULAR)) {

                        if (subCatagory.equals(Constant.ALL)) {

                            womensProductList.add(product);

                        } else if (subCatagory.equals(product.getSub_catarogy())) {

                            womensProductList.add(product);
                        } else {
                        }

                    } else {
                    }

                }

                return womensProductList;

            } else {
            }


            return null;
        }

        protected void onPostExecute(ArrayList<ProductModel> result) {
            super.onPostExecute(result);

            if (result != null) {


                if (result.size() == 0) {
                    showBannerView();
                }

                result.add(0, new ProductModel(""));

                adapter.setList(result);

                if (mProgressHUD.isShowing()) {
                    mProgressHUD.dismiss();

                }


            } else if (!error.isEmpty()) {

//                if (mProgressHUD.isShowing()) {
//                    mProgressHUD.dismiss();
//
//                }
//                showExceptionAlart( error);
            }


        }
    }


    public void setProduct(String catagory, String subCatagory) {

        mPARAM1 = catagory;
        mPARAM2 = subCatagory;

        showBannerView();
        new SM_AsyncTaskForGetData(mPARAM1, mPARAM2, mPARAM3).execute();

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }


}
