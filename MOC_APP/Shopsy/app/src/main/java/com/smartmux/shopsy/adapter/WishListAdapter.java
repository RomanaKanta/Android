package com.smartmux.shopsy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.smartmux.shopsy.R;
import com.smartmux.shopsy.activity.ProductDetailActivity;
import com.smartmux.shopsy.modelclass.ProductModel;
import com.smartmux.shopsy.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private final ArrayList<ProductModel> products;
    ArrayList<ProductModel> itemsPendingRemoval = new ArrayList<ProductModel>();
    Context context;
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<ProductModel, Runnable> pendingRunnables = new HashMap<>();
    boolean undoOn = true;

    public WishListAdapter(Context context, ArrayList<ProductModel> categories) {
        this.products = categories;
        this.context = context;
    }

    public void add(List<ProductModel> categories) {
        this.products.clear();
        this.products.addAll(categories);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_wishlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ProductModel product = products.get(position);

        if (itemsPendingRemoval.contains(product)) {
            // we need to show the "undo" state of the row
            holder.rowDelete.setBackgroundColor(Color.RED);
            holder.row.setVisibility(View.GONE);
            holder.rowDelete.setVisibility(View.VISIBLE);
            holder.txtundo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(product);
                    pendingRunnables.remove(product);
                    if (pendingRemovalRunnable != null)
                        handler.removeCallbacks(pendingRemovalRunnable);
                    itemsPendingRemoval.remove(product);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(products.indexOf(product));
                }
            });

            holder.txtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    new SM_AsyncTaskForDelete(product).execute();
                    remove(products.indexOf(product));
                }
            });
        } else {
            // we need to show the "normal" state

            holder.row.setBackgroundColor(Color.WHITE);
            holder.row.setVisibility(View.VISIBLE);
            holder.rowDelete.setVisibility(View.GONE);
            holder.txtDelete.setOnClickListener(null);
            holder.txtundo.setOnClickListener(null);


            holder.txtTitle.setText(product.getProductName());
            holder.txtBrand.setText(product.getSub_catarogy());
            holder.txtPrice.setText(product.getProductPrice() + " " + product.getCurrency());




            String thumbUrl = Constant.IMAGE_HTTP + product.getProductThumbSmall();

            if (thumbUrl != null && !TextUtils.isEmpty(thumbUrl)) {

                Glide
                        .with(context)
                        .load(thumbUrl)
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher)
                        .crossFade()
                        .into(holder.thumbIcon);

            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("PRODUCT",product);
                    context.startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.push_left_in,
                            R.anim.push_left_out);;

                }
            });
        }
    }


    public void pendingRemoval(int position) {
        final ProductModel product = products.get(position);
        if (!itemsPendingRemoval.contains(product)) {
            itemsPendingRemoval.add(product);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
//                    remove(products.indexOf(product));
                }
            };
//            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(product, pendingRemovalRunnable);
        }
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void remove(int position) {
        ProductModel product = products.get(position);
        if (itemsPendingRemoval.contains(product)) {
            itemsPendingRemoval.remove(product);
        }
        if (products.contains(product)) {
            products.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        ProductModel product = products.get(position);
        return itemsPendingRemoval.contains(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.wish_image)
        ImageView thumbIcon;
        @Bind(R.id.product)
        TextView txtTitle;
        @Bind(R.id.brand)
        TextView txtBrand;
        @Bind(R.id.price)
        TextView txtPrice;

        @Bind(R.id.item_row)
        RelativeLayout row;
        @Bind(R.id.item_row_delete)
        RelativeLayout rowDelete;
        @Bind(R.id.delete)
        TextView txtDelete;
        @Bind(R.id.undo)
        TextView txtundo;



        //        public ProductModelClass category;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    public ArrayList<ProductModel> getProducts() {
        return products;
    }


//    class SM_AsyncTaskForDelete extends AsyncTask<String, String, JSONObject> {
//
//
//        ProgressHUD mProgressHUD;
//        ProductModelClass product;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mProgressHUD = ProgressHUD.show(context,
//                    "Delete...", true);
//
//        }
//
//        public SM_AsyncTaskForDelete(ProductModelClass product){
//            this.product = product;
//        }
//
//        @Override
//        protected JSONObject doInBackground(String... params) {
//
//            try {
//
//
//                HashMap<String, String> postDataParams = new HashMap<>();
//
//                postDataParams.put("customer_id", new PrefUtils().getCustomerID(context));
//                postDataParams.put("product_id", product.getProductID());
//                String keyValue = new JSONParser().getPostDataString(postDataParams);
//
//                return new JSONParser().getApiResult(Constant.DELETE_WISH_LIST_URL + keyValue);
//            }catch (Exception e){
//
//            }
//
//            return null;
//        }
//
//        protected void onPostExecute(final JSONObject result) {
//            super.onPostExecute(result);
//            if (result != null) {
//
//                if(result.has(Constant.JSON_STATUS)){
//
//                    try {
//                        if(result.getString(Constant.JSON_STATUS).equals("true")){
//
//                            remove(products.indexOf(product));
//
//                            Toast.makeText(context,"" + result.getString("message"), Toast.LENGTH_SHORT).show();
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//
//            }
////            if (loading != null) {
////                loading.setVisibility(View.GONE);
////            }
//            if (mProgressHUD.isShowing()) {
//                mProgressHUD.dismiss();
//            }
//
//        }
//    }
}
