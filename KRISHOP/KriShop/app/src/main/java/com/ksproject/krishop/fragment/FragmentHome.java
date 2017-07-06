package com.ksproject.krishop.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ksproject.krishop.R;
import com.ksproject.krishop.adapter.HomeAdapter;
import com.ksproject.krishop.modelclass.AppData;
import com.ksproject.krishop.modelclass.ProductModel;
import com.ksproject.krishop.modelclass.Products;
import com.ksproject.krishop.widget.ProgressHUD;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FragmentHome extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mPARAM1;
    private String mPARAM2;


    private OnFragmentInteractionListener mListener;


    public FragmentHome() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
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

    @Bind(R.id.recyclerViewProductList)
    RecyclerView recyclerView;

    HomeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contant_list, container, false);
        ButterKnife.bind(this, view);
        new SM_AsyncTaskForGetProduct().execute();
        return view;

    }


    private void setList(ArrayList<ProductModel> all) {

        // recyclerView.setScrollViewCallbacks(this);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
        final int gridMargin = getResources().getDimensionPixelOffset(R.dimen.grid_margin);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.set(gridMargin, gridMargin, gridMargin, gridMargin);
            }
        });

        recyclerView.setPadding(gridMargin, gridMargin, gridMargin, gridMargin);

        //  recycleHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_header, null);
        adapter = new HomeAdapter(getActivity(), all);

        recyclerView.setAdapter(adapter);

    }

    class SM_AsyncTaskForGetProduct extends AsyncTask<String, String, ArrayList<ProductModel>> {

        ProgressHUD mProgressHUD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressHUD = ProgressHUD.show(getActivity(),
                    "Loading",true);
        }

        @Override
        protected ArrayList<ProductModel> doInBackground(String... params) {

            ArrayList<ProductModel> all = new ArrayList<>();

          for (int i=0; i< AppData.catagory.size(); i++){

              String cata = AppData.catagory.get(i);
              ArrayList<Products> pro = new ArrayList<>();

              for (int p = 0; p<AppData.allProduct.size(); p++){

                  if (AppData.allProduct.get(p).getCatagory().equals(cata)){

                      pro.add(AppData.allProduct.get(p));
                  }
              }

              all.add(new ProductModel(cata, pro));

          }

            return all;


        }

        protected void onPostExecute(ArrayList<ProductModel> result) {
            super.onPostExecute(result);
            if (mProgressHUD.isShowing()) {
                mProgressHUD.dismiss();
            }
            if (result != null) {

                setList(result);

            }

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
