package com.smartmux.pos.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartmux.pos.R;
import com.smartmux.pos.adapter.CashAdapter;
import com.smartmux.pos.database.DBConstant;
import com.smartmux.pos.database.DBManager;
import com.smartmux.pos.dialogfragment.DiscountDialog;
import com.smartmux.pos.dialogfragment.ItemOptionDialog;
import com.smartmux.pos.model.Sell;
import com.smartmux.pos.utils.ItemClickSupport;
import com.smartmux.pos.utils.ToastUtils;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this com.smartmux.pos.fragment must implement the
 * {@link CashFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CashFragment#newInstance} factory method to
 * create an instance of this com.smartmux.pos.fragment.
 */
public class CashFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the com.smartmux.pos.fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private CashAdapter adapter;
    private DBManager dbManager;

    @Bind(R.id.cash_recyleView)
    RecyclerView recyclerView;
    @Bind(R.id.cash_subtotal)
    TextView txtSubTotal;
    @Bind(R.id.cash_vat_title)
    TextView txtVatTitle;
    @Bind(R.id.cash_vat)
    TextView txtVatAmount;
    @Bind(R.id.cash_total)
    TextView txtTotal;
    @Bind(R.id.cash_textView_discount)
    TextView txtDiscount;

    float total = 0;
    float subtotal = 0;
    double vat = 0;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @OnClick(R.id.pay_content)
    public void sell(View view) {


        for(Sell item : adapter.getList()){

            int quantity = dbManager.getProductQuantity(DBConstant.TABLE_BUY,item.getProduct().getProductID());
            dbManager.sellProduct(item,quantity);
        }

        new LovelyStandardDialog(getActivity())
                .setTopColorRes(R.color.colorAccent)
                .setButtonsColorRes(R.color.colorPrimary)
              // .setIcon(R.drawable.taka)
                .setTitle(R.string.payment_done)
                .setMessage(R.string.purchase_success_mgs)
                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.setList(new ArrayList<Sell>());
                        subtotal = 0;
                        total = 0;
                        vat = 0;
                        txtSubTotal.setText(String.format(getString(R.string.total_amount), subtotal));
                        txtTotal.setText(String.format(getString(R.string.total_amount), total));
                        txtVatAmount.setText(String.format(getString(R.string.total_amount), vat));
                        txtDiscount.setText("Add Discount");
                    }
                })
                .show();

        invoiceID = "SM" + SystemClock.currentThreadTimeMillis();
    }

    @OnClick(R.id.card_discount)
    public void discount(View view) {

        Bundle args = new Bundle();
        args.putFloat("charge", total);

        DiscountDialog discountDialog = new DiscountDialog();
        discountDialog.setArguments(args);
        discountDialog.show(getFragmentManager(), "Discount_Dialog_Fragment");

    }

        public CashFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this com.smartmux.pos.fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of com.smartmux.pos.fragment CashFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CashFragment newInstance(String param1, String param2) {
        CashFragment fragment = new CashFragment();
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


    private String invoiceID;
    private String sellDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.smartmux.pos.fragment
        View view = inflater.inflate(R.layout.fragment_cash, container, false);
        ButterKnife.bind(this, view);

        dbManager = new DBManager(getActivity());
        invoiceID = "SM" + SystemClock.currentThreadTimeMillis();
        Calendar calendar = Calendar.getInstance();
        sellDate = format.format(calendar.getTime());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLongClickable(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        final int gridMargin = getResources().getDimensionPixelOffset(R.dimen.grid_margin);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.set(gridMargin, gridMargin, gridMargin, gridMargin);
            }
        });

        recyclerView.setPadding(gridMargin, gridMargin, gridMargin, gridMargin);

        adapter = new CashAdapter(getActivity(), new ArrayList<Sell>());

        recyclerView.setAdapter(adapter);

        ItemClickSupport itemClick = ItemClickSupport
                .addTo(recyclerView);
        itemClick
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        Bundle args = new Bundle();
                        args.putInt("position", position);
                        args.putString("name", adapter.getList().get(position).getProduct().getProductName());
                        args.putString("quantity", adapter.getList().get(position).getQuantity());

                        ItemOptionDialog optionDialog = new ItemOptionDialog();
                        optionDialog.setArguments(args);
                        optionDialog.show(getFragmentManager(), "Option_Dialog_Fragment");

                    }
                });

//        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//
////               Toast.makeText(getActivity(), " " +viewHolder.getAdapterPosition() + " " +  adapter.getList().get(viewHolder.getAdapterPosition()).getProduct().getProductName(),Toast.LENGTH_SHORT).show();
//                netAmount(adapter.getList().get(viewHolder.getAdapterPosition()));
//                adapter.getList().remove(viewHolder.getAdapterPosition());
//                adapter.notifyDataSetChanged();
//
//
//            }
//
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//        };
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
//
//        itemTouchHelper.attachToRecyclerView(recyclerView);


        txtVatTitle.setText(String.format(getString(R.string.vat), 5));

        if (total == 0) {

            view.findViewById(R.id.calculation_container).setVisibility(View.INVISIBLE);

        }


        return view;
    }

    public void removeFromList(int position){

         netAmount(adapter.getList().get(position));
         adapter.getList().remove(position);
         adapter.notifyDataSetChanged();
    }

    public void modifyList(int position,int quantity){

        adapter.getList().get(position).setQuantity(String.valueOf(quantity));
        adapter.notifyDataSetChanged();

        float price =subtotal - (Integer.parseInt(adapter.getList().get(position).getProduct().getProductPrice()) * Integer.parseInt(adapter.getList().get(position).getQuantity()));
        subtotal = subtotal + (Integer.parseInt(adapter.getList().get(position).getProduct().getProductPrice()) * quantity);
        double  vat = price * 0.05;
        total = price + (float) vat;

        if (total == 0) {


            getView().findViewById(R.id.calculation_container).setVisibility(View.INVISIBLE);

        } else {
            txtSubTotal.setText(String.format(getString(R.string.total_amount), price));
            txtTotal.setText(String.format(getString(R.string.total_amount), total));
            txtVatAmount.setText(String.format(getString(R.string.total_amount), vat));


            getView().findViewById(R.id.calculation_container).setVisibility(View.VISIBLE);
        }

    }

    public void addDiscount(float dtotal, int discount){

        txtTotal.setText(String.format(getString(R.string.total_amount), dtotal));
        txtDiscount.setText("Discount ("+ discount + "%)");

    }

    public void addSellItem(Sell sell) {

        sell.setInvoicID(invoiceID);
        sell.setDate(sellDate);
        sell.setCustomerId("common");
        int quantity = 0;
        int productQuantity = 0;
        List<Sell> sellList = new ArrayList<>();

        for(int i = 0 ; i < adapter.getList().size();i++){
            Sell item = adapter.getList().get(i);
            if (item.getProduct().getProductID().equals(sell.getProduct().getProductID()) && item.getProduct().getProductCategoryID().equals(sell.getProduct().getProductCategoryID())) {

                quantity = Integer.parseInt(item.getQuantity()) +  Integer.parseInt(sell.getQuantity());
                 System.out.println(quantity);
                String productId = sell.getProduct().getProductID();
                 productQuantity = dbManager.getProductQuantity(DBConstant.TABLE_BUY,productId);
                productQuantity = productQuantity - quantity;
                item.setQuantity(String.valueOf(quantity));
            }
            sellList.add(item);
        }



        if(quantity != 0){
            if(productQuantity < 1){

                new LovelyStandardDialog(getActivity())
                        .setTopColorRes(R.color.colorAccent)
                        .setButtonsColorRes(R.color.colorPrimary)
                        // .setIcon(R.drawable.taka)
                        .setTitle(R.string.stock)
                        .setMessage(R.string.stock_unavailable_message)
                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();

                return;
            }else if(productQuantity < 5){

                ToastUtils.showLong("Stock low");
            }
        }


        if(quantity == 0){

            sellList.add(sell);
        }

        adapter.setList(sellList);

      /*  if(adapter.getList().size() == 0){

            adapter.addSell(sell);
        }else{

            List<Sell> sellList = new ArrayList<>();
            for (Sell item : adapter.getList()) {
                if (item.getProduct().getProductID().equals(sell.getProduct().getProductID()) && item.getProduct().getProductCategoryID().equals(sell.getProduct().getProductCategoryID())) {

                    int quantity = Integer.parseInt(item.getQuantity()) +  Integer.parseInt(sell.getQuantity());
                    sell.setQuantity(String.valueOf(quantity));
                }
                sellList.add(item);
            }

            adapter.setList(sellList);
        }*/


        subtotal = subtotal + (Integer.parseInt(sell.getProduct().getProductPrice()) * Integer.parseInt(sell.getQuantity()));
        vat = subtotal * 0.05;
        total = subtotal + (float) vat;

        if (total == 0) {


            getView().findViewById(R.id.calculation_container).setVisibility(View.INVISIBLE);

        } else {
            txtSubTotal.setText(String.format(getString(R.string.total_amount), subtotal));
            txtTotal.setText(String.format(getString(R.string.total_amount), total));
            txtVatAmount.setText(String.format(getString(R.string.total_amount), vat));


            getView().findViewById(R.id.calculation_container).setVisibility(View.VISIBLE);
        }
    }

    private void netAmount(Sell sell){
       float price =subtotal - (Integer.parseInt(sell.getProduct().getProductPrice()) * Integer.parseInt(sell.getQuantity()));
        double  vat = price * 0.05;
         total = price + (float) vat;

        if (total == 0) {


            getView().findViewById(R.id.calculation_container).setVisibility(View.INVISIBLE);

        } else {
            txtSubTotal.setText(String.format(getString(R.string.total_amount), price));
            txtTotal.setText(String.format(getString(R.string.total_amount), total));
            txtVatAmount.setText(String.format(getString(R.string.total_amount), vat));


            getView().findViewById(R.id.calculation_container).setVisibility(View.VISIBLE);
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
    }


}
