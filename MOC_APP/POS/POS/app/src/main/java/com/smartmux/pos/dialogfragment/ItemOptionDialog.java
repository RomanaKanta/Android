package com.smartmux.pos.dialogfragment;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.smartmux.pos.R;
import com.smartmux.pos.adapter.ItemOptionAdapter;
import com.smartmux.pos.utils.ItemClickSupport;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemOptionDialog extends DialogFragment {

    private int itemPosition;
    Bundle mArgs;
    String itemName,itemQuantity;

    @Bind(R.id.recycleView_item_option)
    RecyclerView optionList;

    ArrayList arraylist = new ArrayList<String>();

	public void onResume() {
		super.onResume();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;

		getDialog().getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
		lp.width = (int) (width);
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getDialog().getWindow().setAttributes(lp);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.dialog_fragment_item_option, container,
				false);
        ButterKnife.bind(this, rootView);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//		getDialog().getWindow().setBackgroundDrawableResource(
//				android.R.color.transparent);
		getDialog().getWindow().setGravity(Gravity.BOTTOM);
//		getDialog().getWindow().getAttributes().windowAnimations = R.style.AnimationBottomUpDown;

        mArgs = getArguments();
        itemPosition = mArgs.getInt("position");
        itemName = mArgs.getString("name");
        itemQuantity = mArgs.getString("quantity");

        if(arraylist.size()>0){
            arraylist.clear();
        }

        arraylist.add("Modify");
        arraylist.add("Delete");


        optionList.setHasFixedSize(true);
        optionList.setLongClickable(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        optionList.setLayoutManager(layoutManager);
        final int gridMargin = getResources().getDimensionPixelOffset(R.dimen.grid_margin);

        optionList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.set(gridMargin, gridMargin, gridMargin, gridMargin);
            }
        });

        optionList.setPadding(gridMargin, gridMargin, gridMargin, gridMargin);

        ItemOptionAdapter adapter = new ItemOptionAdapter(getActivity(), arraylist);

        optionList.setAdapter(adapter);

        ItemClickSupport itemClick = ItemClickSupport
                .addTo(optionList);
        itemClick
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        if (position==0){

                            Bundle args = new Bundle();
                            args.putInt("position", itemPosition);
                            args.putString("name", itemName);
                            args.putString("quantity", itemQuantity);

                            OptionModifyDialog modifyDialog = new OptionModifyDialog();
                            modifyDialog.setArguments(args);
                            modifyDialog.show(getFragmentManager(), "Modify_Dialog_Fragment");
                            dismiss();

                        }else if (position==1){


                            Bundle args = new Bundle();
                            args.putInt("position", itemPosition);

                            OptionDeleteDialog deleteDialog = new OptionDeleteDialog();
                            deleteDialog.setArguments(args);
                            deleteDialog.show(getFragmentManager(), "Delete_Dialog_Fragment");
                            dismiss();


                        }else {

                        }

                    }
                });

		return rootView;
	}

	
}
