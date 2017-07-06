package com.smartmux.pos.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartmux.pos.R;
import com.smartmux.pos.adapter.ProductAdapter;
import com.smartmux.pos.database.DBConstant;
import com.smartmux.pos.database.DBManager;
import com.smartmux.pos.model.Product;
import com.smartmux.pos.model.Sell;
import com.smartmux.pos.utils.ItemClickSupport;
import com.smartmux.pos.utils.ToastUtils;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductFragment.OnProductFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnProductFragmentInteractionListener mListener;
    private  List<Product> productList;
    private DBManager dbManager;
    private  int[]  colorNumberarray;



    @Bind(R.id.product_recyleView)
    RecyclerView recyclerView;


    public ProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        ButterKnife.bind(this,view);

        colorNumberarray = getActivity().getResources().getIntArray(R.array.colorNumberList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLongClickable(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(layoutManager);
        final int gridMargin = getResources().getDimensionPixelOffset(R.dimen.spacing);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.set(gridMargin, gridMargin, gridMargin, gridMargin);
            }
        });

        recyclerView.setPadding(gridMargin , gridMargin,gridMargin,gridMargin);





        dbManager = new DBManager(getActivity());


        ItemClickSupport itemClick = ItemClickSupport
                .addTo(recyclerView);
        itemClick
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        Product product = productList.get(position);


                        Sell sell = new Sell();
                        sell.setProduct(product);
                        sell.setQuantity("1");
                        CashFragment cashFragment = (CashFragment) getActivity().getSupportFragmentManager()
                                .findFragmentByTag(getString(R.string.fragment_cash));
                        cashFragment.addSellItem(sell);

                    }
                });


        setData();
        return view;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (this.isVisible()) {

            if (isVisibleToUser) {
                setData();

            }
        }
    }

    private void setData(){

        productList = dbManager.getAllProduct();
        for(int i = 0 ;i < productList.size();i++){

            productList.get(i).setProductThumb(String.valueOf(colorNumberarray[i]));
        }


        recyclerView.setAdapter(new ProductAdapter(getActivity(),productList));
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
        if (context instanceof OnProductFragmentInteractionListener) {
            mListener = (OnProductFragmentInteractionListener) context;
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
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnProductFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

//    String productID;
//    String productPrice;
//    String productName;
//    String productManufacture;
//    String productThumb;
//    String productCategoryID;
//    String productExPareDate;



}
