package com.aircast.photobag.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBHoneyShopActivity;
import com.aircast.photobag.model.PBHoneyShopGoodsModel;
import com.aircast.photobag.widget.FButton;

public class PBHoneyShopListAdapter extends BaseAdapter {
	
	private PBHoneyShopActivity mContext;
	private ArrayList<PBHoneyShopGoodsModel> mGoodsList;
	private LayoutInflater mInflater;
	private Map<String, String> mPrices;
	
	
	/**
	 * Create goods adapter contains <b>[honey1, honey3, honey6]</b>
	 * <p>When the adapter's btn is clicked, it will call sendMessage to PBHoneyShopActivity's main handler.
	 * and the message contain the goods's id in its object.</p>
	 * <p>In fact, not only price, all goods information can be get from google play except sample image if you have ID,
	 * so the best way to init is get IDs from photobag's server and then other information from google instead to save them in local.</p>
	 * @param context <b>MUST</b> be PBHoneyShopActivity as adapter will use its handler
	 * @param data Data get from google play that contains goods details, use to update price
     */
	public PBHoneyShopListAdapter(PBHoneyShopActivity context, Bundle data) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		
		mGoodsList = new ArrayList<PBHoneyShopGoodsModel>();
		for (int i=0; i<3; i++) {
			PBHoneyShopGoodsModel goods;
			goods = new PBHoneyShopGoodsModel(i);
			mGoodsList.add(goods);
		}
		
		mPrices = new HashMap<String, String>();
		try {
			ArrayList<String> responseList
				= data.getStringArrayList("DETAILS_LIST");
			for (String thisResponse : responseList) {
				JSONObject object = new JSONObject(thisResponse);
				String sku = object.getString("productId");
				String price = object.getString("price");
				mPrices.put(sku, price);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getCount() {
		if (mGoodsList != null) {
			return mGoodsList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mGoodsList != null && position < mGoodsList.size()) {
			return mGoodsList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mGoodsList == null || mGoodsList.size() == 0) {
            return convertView;
        }
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.pb_adapter_list_honey_shop_item, null);
            convertView.setClickable(true);

            holder = new ViewHolder();
            holder.textTitle = (TextView) convertView
                    .findViewById(R.id.tv_shop_goods_name);
            holder.textPrice = (TextView) convertView
                    .findViewById(R.id.tv_shop_goods_price);
            holder.imageIcon = (ImageView) convertView
                    .findViewById(R.id.icon_shop_item);
            holder.btnPurchase = (FButton) convertView
                    .findViewById(R.id.btn_shop_purchase);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position < 0 || position > mGoodsList.size()) {
            return convertView;
        }
        PBHoneyShopGoodsModel item = mGoodsList.get(position);
        if (item != null) {
            holder.textTitle.setText(item.getTitle());
            //holder.textPrice.setText(item.getPrice());
            String price = TextUtils.isEmpty(mPrices.get(item.getGoodsId()))
            		? "DEBUG"
            		: mPrices.get(item.getGoodsId());
            holder.textPrice.setText(price);
            holder.imageIcon.setImageResource(item.getIcon());
            holder.btnPurchase.setTag(item.getGoodsId());
            holder.btnPurchase.setOnClickListener(mBtnPurchaseListener);
        }
        return convertView;
	}
	
	private View.OnClickListener mBtnPurchaseListener = new View.OnClickListener() {	
		@Override
		public void onClick(View v) {
			Message msg = Message.obtain(null, 
					mContext.MSG_START_PURCHASE, 
					v.getTag().toString());
			
			mContext.mHandler.sendMessage(msg);
		}
	};
	
	private static class ViewHolder {
        public TextView textTitle;
        public TextView textPrice;
        public ImageView imageIcon;
        public FButton btnPurchase;
    }
	
}