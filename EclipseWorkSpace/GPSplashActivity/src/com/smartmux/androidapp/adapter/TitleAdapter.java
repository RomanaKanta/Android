package com.smartmux.androidapp.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmux.androidapp.R;
import com.smartmux.androidapp.database.DatabaseHandler;
import com.smartmux.androidapp.model.TutorialModel;

public class TitleAdapter  extends BaseAdapter {

		private List<TutorialModel> list;
		private Context context;
		private DatabaseHandler db;

		public TitleAdapter(Context context,List<TutorialModel> list) {
			super();
			this.context = context;
			this.list = list;
			db = new DatabaseHandler(context);
		}

		public int getCount() {

			return list.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {

			ViewHolder holder;
			if (convertView == null) {

				holder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater
						.from(context);

				convertView = inflater.inflate(R.layout.title_item,
						parent, false);

				holder.titleName = (TextView) convertView
						.findViewById(R.id.textView_title);
				
				holder.iconFav = (ImageView) convertView
						.findViewById(R.id.imageView_memorize);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			
			boolean isExistTutorial = db.isFavoriteTutorialExists(list.get(position).getId());
			if(isExistTutorial){
				
				holder.iconFav.setImageResource(R.drawable.memorize);
			}else{
				
				holder.iconFav.setImageResource(R.drawable.memorize_not);
			}
			holder.titleName.setText(list.get(position).getTitle());
			return convertView;

		}

		private class ViewHolder {
			public TextView titleName;
			public ImageView iconFav;
		}


}
