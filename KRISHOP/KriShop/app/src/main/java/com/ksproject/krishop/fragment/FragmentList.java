package com.ksproject.krishop.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ksproject.krishop.R;
import com.ksproject.krishop.adapter.ProductListAdapter;
import com.ksproject.krishop.adapter.SearchAdapter;
import com.ksproject.krishop.modelclass.AppData;
import com.ksproject.krishop.modelclass.Products;
import com.ksproject.krishop.widget.ProgressHUD;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FragmentList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String CATA = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String Catagory;
    private String mPARAM2;


    private OnFragmentInteractionListener mListener;


    public FragmentList() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentList newInstance(String cata, String param2) {
        FragmentList fragment = new FragmentList();
        Bundle args = new Bundle();
        args.putString(CATA, cata);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Catagory = getArguments().getString(CATA);
            mPARAM2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Bind(R.id.recyclerViewProductList)
    RecyclerView recyclerView;

    ProductListAdapter adapter;

    @Bind(R.id.recyclerViewFilter)
    RecyclerView filterView;

    SearchAdapter serachAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        // ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));

        setFilterList();
        setList();
        new SM_AsyncTaskForGetProduct().execute();

        return view;

    }


    private void setList() {

        // recyclerView.setScrollViewCallbacks(this);
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

        //  recycleHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_header, null);
        adapter = new ProductListAdapter(getActivity(), new ArrayList<Products>());

        recyclerView.setAdapter(adapter);

    }


    private void setFilterList() {

        filterView.setHasFixedSize(true);
        filterView.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.HORIZONTAL,
                false));
        serachAdapter = new SearchAdapter(getActivity());
        filterView.setAdapter(serachAdapter);


    }


    class SM_AsyncTaskForGetProduct extends AsyncTask<String, String, ArrayList<Products>> {

        ProgressHUD mProgressHUD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressHUD = ProgressHUD.show(getActivity(),
                    "Loading",true);
        }

        @Override
        protected ArrayList<Products> doInBackground(String... params) {

                ArrayList<Products> pro = new ArrayList<>();

                for (int p = 0; p<AppData.allProduct.size(); p++){

                    if (AppData.allProduct.get(p).getCatagory().equals(Catagory)){

                        pro.add(AppData.allProduct.get(p));
                    }
                }

            return pro;


        }

        protected void onPostExecute(ArrayList<Products> result) {
            super.onPostExecute(result);
            if (mProgressHUD.isShowing()) {
                mProgressHUD.dismiss();
            }
            if (result != null) {

                adapter = new ProductListAdapter(getActivity(), result);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


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
