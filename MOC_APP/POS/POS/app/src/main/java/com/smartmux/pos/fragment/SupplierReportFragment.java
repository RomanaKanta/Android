package com.smartmux.pos.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.smartmux.pos.R;
import com.smartmux.pos.database.DBConstant;
import com.smartmux.pos.database.DBManager;
import com.smartmux.pos.model.Buy;
import com.smartmux.pos.model.Product;
import com.smartmux.pos.model.ProductCategory;
import com.smartmux.pos.model.Supplier;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.smartmux.pos.fragment.SupplierReportFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.smartmux.pos.fragment.SupplierReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SupplierReportFragment extends Fragment implements OnChartGestureListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private final int itemcount = 12;
    @Bind(R.id.supplier_report_container)
    FrameLayout frameLayout;
    @Bind(R.id.supplier_barchart)
    BarChart barChart;
    @Bind(R.id.spinner_supplier_re)
    MaterialSpinner productSupplier;
    private  List<Supplier> supplierList;
    @Bind(R.id.spinner_category_re)
    MaterialSpinner productCategory;
    private  List<ProductCategory> categoryList;
    @Bind(R.id.spinner_product_re)
    MaterialSpinner product;
    private  List<Product> productList;

    private DBManager dbManager;

    public SupplierReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SupplierReportFragment newInstance(String param1, String param2) {
        SupplierReportFragment fragment = new SupplierReportFragment();
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


    private void setChartData(String SupplierID, String productID){

        List<String> dateList = dbManager.getBuyDate(SupplierID, productID);
        // barChart.setOnChartGestureListener(this);

        if(dateList.size() > 0){
            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<String>();


                for (int i = 0; i < dateList.size(); i++) {

                    String query = "SELECT * FROM " + DBConstant.TABLE_BUY + " WHERE " + DBConstant.date + "='" + dateList.get(i) + "'"
                            +" AND " + DBConstant.supplierId + "='" + SupplierID + "'";

                    Log.e("DATE" , "" + dateList.get(i));

                    if(!productID.equals("")){
                        query = "SELECT * FROM " + DBConstant.TABLE_BUY + " WHERE " + DBConstant.date + "='" + dateList.get(i) + "'"
                                +" AND " + DBConstant.supplierId + "='" + SupplierID + "'" +" AND " + DBConstant.productId + "='" + productID + "'" ;
                    }

                    List<Buy> buyList = dbManager.getbuyProduct(query);
                    float total = 0;
                    String dateTime = dateList.get(i);
                    for (int j = 0; j < buyList.size(); j++) {

                        total = total + Integer.parseInt(buyList.get(j).getQuantity());
                        Log.e("Quantity" , "" + total);
//                        dateTime = buyList.get(j).getDate();
                    }
//                    double vat = subtotal * 0.05;
//                    total = subtotal + (float) vat;
//                    System.out.println(total);
                    entries.add(new BarEntry(total, i));
                    labels.add(dateTime);
            }

            BarDataSet dataset = new BarDataSet(entries, "");
            dataset.setColors(ColorTemplate.COLORFUL_COLORS);

            BarData data = new BarData(labels, dataset);

            barChart.setData(data);
            barChart.animateY(1000);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_supplier_report, container, false);
        ButterKnife.bind(this, view);
        // create a new chart object
        barChart.setDescription("Suppliers Report");
        dbManager = new DBManager(getActivity());

        setSpinnerItems();

        return view;
    }

    private void setSpinnerItems(){

        setProduct("1");
        setChartData("1","");


        supplierList = dbManager.getAllSupplier();
        List<String> suppliers = new ArrayList<>();
        for(Supplier supplier : supplierList){
            suppliers.add(supplier.getName());
            Log.e("SUPPLIER" , "  name "+supplier.getName() + "   id    " + supplier.getId());
        }


        if(suppliers.size() > 0){
            productSupplier.setItems(suppliers);
        }
        productSupplier.setSelectedIndex(0);
        productSupplier.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, item + " Selected ", Snackbar.LENGTH_LONG).show();

                //SupplierID =  "" + (position+1);
                setProduct("" + supplierList.get(position).getId());
                setChartData("" + supplierList.get(position).getId(),"");



            }
        });




//        categoryList = dbManager.getAllCategory();
//        List<String> categories = new ArrayList<>();
//        categories.add("All");
//        for(ProductCategory productCategory : categoryList){
//            categories.add(productCategory.getGetProductCategoryName());
//        }
//
//
//        if(categories.size() > 0){
//            productCategory.setItems(categories);
//        }
//
//        productCategory.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
//
//            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                Snackbar.make(view, item + " Selected ", Snackbar.LENGTH_LONG).show();
//            }
//        });





    }

    private void setProduct(final String supplierID){
        String query = "SELECT * FROM " + DBConstant.TABLE_PRODUCT + " WHERE " + DBConstant.productSupplierId + " = " + supplierID;
        productList = dbManager.getProduct(query);
       final List<String> products = new ArrayList<>();
        products.add("All");
        for(Product product : productList){
            products.add(product.getProductName());

        }


        if(products.size() > 0){
            product.setItems(products);
        }

        product.setSelectedIndex(0);

        product.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, item + " Selected ", Snackbar.LENGTH_LONG).show();

                if(position==0){
                    setChartData(supplierID,"");
                }else {
                    setChartData(supplierID, "" + productList.get(position-1).getProductID());
                }

                Log.e("Product", "" + products.size());
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (this.isVisible()) {

            if (isVisibleToUser) {
                setProduct("1");
                setChartData("1","");
                productSupplier.setSelectedIndex(0);
                product.setSelectedIndex(0);

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

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START");
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END");
        barChart.highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
