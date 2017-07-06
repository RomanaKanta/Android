package com.smartux.photocollage.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smartux.photocollage.CustomGalleryActivity;
import com.smartux.photocollage.R;
import com.smartux.photocollage.adapter.SelectedImageListAdapter.SelectedRecyclerViewHolder;
import com.smartux.photocollage.utils.CustomGallery;

public class SelectedImageListAdapter extends
RecyclerView.Adapter<SelectedRecyclerViewHolder> {// Recyclerview will extend to
	// recyclerview adapter
		private ArrayList<CustomGallery> arrayList;
		private Context context;

		public SelectedImageListAdapter(Context context, ArrayList<CustomGallery> arrayList) {
			this.context = context;
			this.arrayList = arrayList;

		}

		@Override
		public int getItemCount() {
			return (null != arrayList ? arrayList.size() : 0);

		}

		@Override
		public void onBindViewHolder(SelectedRecyclerViewHolder holder, final int position) {
			final CustomGallery model = arrayList.get(position);

			SelectedRecyclerViewHolder mainHolder = (SelectedRecyclerViewHolder) holder;// holder

		

			// setting title
//			mainHolder.colorCircleView1.setText(model.getTitle());

		mainHolder.imageView
		.setImageBitmap(generateBitmap(model.sdcardPath
				.toString()));
			
				mainHolder.delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						if (context instanceof CustomGalleryActivity) {
							((CustomGalleryActivity) context).refreshList(position,
									model.id, model.view);
						}

					}
				});


		}

		@Override
		public SelectedRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup,
				int viewType) {

			// This method will inflate the custom layout and return as viewholder
			LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

			ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
					R.layout.selected_item, viewGroup, false);
			SelectedRecyclerViewHolder listHolder = new SelectedRecyclerViewHolder(mainGroup);
			return listHolder;

		}

		public class SelectedRecyclerViewHolder extends RecyclerView.ViewHolder  {
		    // View holder for gridview recycler view as we used in listview
			public ImageView imageView,delete;
		 
		    public SelectedRecyclerViewHolder(View view) {
		        super(view);
		        // Find all views ids
		        this.imageView =(ImageView)view.findViewById(R.id.image_selected);
		        this.delete =(ImageView)view.findViewById(R.id.delete);
		    }
		}

		public Bitmap generateBitmap(String path) {

			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inTempStorage = new byte[16 * 1024];

			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opt);
			
			opt.inSampleSize = calculateInSampleSize(opt, 50, 50);

			opt.inJustDecodeBounds = false;

			return BitmapFactory.decodeFile(path, opt);


		}

		public static int calculateInSampleSize(BitmapFactory.Options options,
				int reqWidth, int reqHeight) {
			// Raw height and width of image
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;

			if (height > reqHeight || width > reqWidth) {

				final int halfHeight = height / 2;
				final int halfWidth = width / 2;

				// Calculate the largest inSampleSize value that is a power of 2 and
				// keeps both
				// height and width larger than the requested height and width.
				while ((halfHeight / inSampleSize) > reqHeight
						&& (halfWidth / inSampleSize) > reqWidth) {
					inSampleSize *= 2;
				}
			}

			return inSampleSize;
		}

	}