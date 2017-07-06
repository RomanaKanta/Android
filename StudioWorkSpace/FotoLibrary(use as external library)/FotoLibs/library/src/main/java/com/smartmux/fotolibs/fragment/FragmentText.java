package com.smartmux.fotolibs.fragment;

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.abner.stickerdemo.view.BubbleTextView;
import com.example.abner.stickerdemo.view.InputDialog;
import com.smartmux.fotolibs.R;
import com.smartmux.fotolibs.adapter.TextAdapter;
import com.smartmux.fotolibs.modelclass.BitmapHolder;
import com.smartmux.fotolibs.modelclass.ListData;
import com.smartmux.fotolibs.utils.AddItems;
import com.smartmux.fotolibs.utils.FotoLibsConstant;
import com.smartmux.fotolibs.widget.HorizontalListView;

public class FragmentText extends Fragment implements OnClickListener,
		OnItemClickListener {

	private ArrayList<ListData> mTextListData = new ArrayList<ListData>();
	HorizontalListView text_listview;
	TextAdapter mListAdapter;
	String strtext = "";
	Animation downDrawerOpen, downDrawerClose = null;
	ImageView a_imageview_done, a_imageview_close;
	TextView a_textview_title, a_textview_done, a_imageview_logo;
	RelativeLayout a_footer = null;
	ImageView text_image_view = null;
	boolean changed, new_text = false;
	Bitmap changed_bitmap = null;
	
	RelativeLayout layout_option = null;
	ImageView imageview_option_close = null;
	TextView inputDone = null;
	EditText etInput = null;
	ListView fontList = null;
	private ArrayList<String> mTextFontArray = new ArrayList<String>();

	private ArrayList<View> mViews;
	private RelativeLayout mContentRootView;
	private InputDialog mInputDialog;
	private BubbleTextView mCurrentEditTextView;
	BitmapHolder mBitmapHolder = null;
	int option = 0;

	public static FragmentText newInstance() {
		FragmentText fragment = new FragmentText();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_text, container,
				false);

		mTextListData = AddItems.getTextListItems(getActivity());
		mTextFontArray = AddItems.getTextFontItems(getActivity());

		downDrawerOpen = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_up);
		downDrawerClose = AnimationUtils.loadAnimation(getActivity(),
				R.anim.bottom_down);

		mBitmapHolder = new BitmapHolder();

		mViews = new ArrayList<View>();

		initActivityItem();

		// layout_input = (RelativeLayout) rootView
		// .findViewById(R.id.layout_input);

		text_listview = (HorizontalListView) rootView
				.findViewById(R.id.fragment_text_listview);
		text_listview.startAnimation(downDrawerOpen);

		mListAdapter = new TextAdapter(getActivity(), mTextListData, false,
				FragmentText.this);
		text_listview.setAdapter(mListAdapter);

		text_listview.setOnItemClickListener(this);

		text_image_view = (ImageView) rootView
				.findViewById(R.id.text_Imageview);
		text_image_view.setImageBitmap(mBitmapHolder.getBm());

		mContentRootView = (RelativeLayout) rootView
				.findViewById(R.id.rl_content_root);

		mInputDialog = new InputDialog(getActivity());
		initInputLayout(rootView);

		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		changed = true;

		if (mViews.size() == 0) {
			new_text = false;
		} else {
			new_text = true;
		}

		if (position == 0) {
			new_text = true;
			addText();
			setEditText();
			// layout_option.setVisibility(View.VISIBLE);
			for (int i = 0; i < text_listview.getChildCount(); i++) {

				TextView textView = (TextView) text_listview.getChildAt(i)
						.findViewById(R.id.text_title);
				ImageView imageView = (ImageView) text_listview.getChildAt(i)
						.findViewById(R.id.text_icon);

				textView.setTextColor(Color.parseColor("#FFFFFF"));
				imageView.setColorFilter(Color.parseColor("#FFFFFF"),
						PorterDuff.Mode.SRC_ATOP);
			}
		}

		if (new_text) {
			setOption(position);
		}
	}

	public int textNum() {
		return mViews.size();
	}

	private void initInputLayout(View rootView) {

		layout_option = (RelativeLayout) rootView
				.findViewById(R.id.layout_text_options);

		imageview_option_close = (ImageView) rootView
				.findViewById(R.id.Imageview_layout_close);
		imageview_option_close.setOnClickListener(this);

		inputDone = (TextView) rootView.findViewById(R.id.tv_action_done);
		etInput = (EditText) rootView.findViewById(R.id.et_bubble_input);

		// layout_typeface = (RelativeLayout) rootView
		// .findViewById(R.id.text_typeface);

		fontList = (ListView) rootView.findViewById(R.id.listview_typeface);

		// listAssetFiles("fonts");
	}

	private void initActivityItem() {

		a_imageview_done = (ImageView) getActivity().findViewById(
				R.id.imageview_done);
		a_imageview_close = (ImageView) getActivity().findViewById(
				R.id.imageview_close);
		a_footer = (RelativeLayout) getActivity().findViewById(
				R.id.horizontalLayout);

		a_imageview_logo = (TextView) getActivity().findViewById(
				R.id.imageview_logo);
		a_textview_title = (TextView) getActivity().findViewById(
				R.id.textview_header);
		a_textview_done = (TextView) getActivity().findViewById(
				R.id.textview_done);

		a_textview_title.setText(R.string.text);
		a_imageview_logo.setVisibility(View.GONE);
		a_textview_done.setVisibility(View.GONE);
		a_imageview_close.setVisibility(View.VISIBLE);
		a_imageview_done.setVisibility(View.VISIBLE);

		a_imageview_done.setOnClickListener(this);
		a_imageview_close.setOnClickListener(this);
	}

	private void addText() {

		final BubbleTextView bubbleTextView = new BubbleTextView(getActivity(),
				Color.BLACK, 0);
		mInputDialog.setBubbleView(bubbleTextView);
		bubbleTextView.setImageResource(R.drawable.fake_img);
		bubbleTextView
				.setOperationListener(new BubbleTextView.OperationListener() {
					@Override
					public void onDeleteClick() {
						mViews.remove(bubbleTextView);
						mContentRootView.removeView(bubbleTextView);

						if (mViews.size() == 0) {
							for (int i = 0; i < text_listview.getChildCount(); i++) {

								TextView textView = (TextView) text_listview
										.getChildAt(i).findViewById(
												R.id.text_title);
								ImageView imageView = (ImageView) text_listview
										.getChildAt(i).findViewById(
												R.id.text_icon);

								if (i == 0) {
									textView.setTextColor(Color
											.parseColor("#FFFFFF"));
									imageView.setColorFilter(
											Color.parseColor("#FFFFFF"),
											PorterDuff.Mode.SRC_ATOP);
								} else {
									textView.setTextColor(Color
											.parseColor("#787878"));
									imageView.setColorFilter(
											Color.parseColor("#787878"),
											PorterDuff.Mode.SRC_ATOP);
								}
							}
						}
					}

					@Override
					public void onEdit(BubbleTextView bubbleTextView) {
						mCurrentEditTextView.setInEdit(false);
						mCurrentEditTextView = bubbleTextView;
						mCurrentEditTextView.setInEdit(true);
						mInputDialog.setBubbleView(bubbleTextView);
					}

					@Override
					public void onClick(BubbleTextView bubbleTextView) {

						mInputDialog.setBubbleView(bubbleTextView);
					}

					@Override
					public void onTop(BubbleTextView bubbleTextView) {
						int position = mViews.indexOf(bubbleTextView);
						if (position == mViews.size() - 1) {
							return;
						}
						BubbleTextView textView = (BubbleTextView) mViews
								.remove(position);
						mViews.add(mViews.size(), textView);

					}
				});

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		mContentRootView.addView(bubbleTextView, lp);
		mViews.add(bubbleTextView);
		setCurrentEdit(bubbleTextView);

	}

	private void setCurrentEdit(BubbleTextView bubbleTextView) {

		if (mCurrentEditTextView != null) {
			mCurrentEditTextView.setInEdit(false);
		}
		mCurrentEditTextView = bubbleTextView;
		mCurrentEditTextView.setInEdit(true);
	}

	private void setEditText() {

		fontList.setVisibility(View.GONE);
		etInput.setVisibility(View.VISIBLE);
		etInput.setHint("Text");
		etInput.setText("");
		etInput.setCursorVisible(true);
		etInput.setFocusableInTouchMode(true);

		layout_option.setVisibility(View.VISIBLE);

		inputDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String str = etInput.getText().toString();

				if (str.equals("")) {
					mInputDialog.setBubbleText("Text");
				} else {
					mInputDialog.setBubbleText(str);
				}

				hideKeyBoard(layout_option);
				layout_option.setVisibility(View.GONE);

			}
		});

	}

	private void setFont() {

		fontList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Typeface tf = null;

				if (position <= 7) {
					String typeface = mTextFontArray.get(position);

					tf = Typeface.createFromAsset(getActivity().getAssets(),
							"fonts/" + typeface + ".ttf");
				}

				if (position > 7) {
					String typeface = mTextFontArray.get(position);

					tf = Typeface.createFromAsset(getActivity().getAssets(),
							"fonts/" + typeface + ".otf");
				}

				mInputDialog.setBubbleTextTypeFace(tf);
			}
		});

		inputDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				layout_option.setVisibility(View.GONE);
			}
		});
	}

	private void setColor() {
		AmbilWarnaDialog colorDialog = new AmbilWarnaDialog(getActivity(),
				R.color.black_semi_transparent,
				new AmbilWarnaDialog.OnAmbilWarnaListener() {

					@Override
					public void onOk(AmbilWarnaDialog dialog, int color) {

						mInputDialog.setBubbleTextColor(color);

						// Log.d("getColor", "" + color);
					}

					@Override
					public void onCancel(AmbilWarnaDialog dialog) {

					}
				});

		colorDialog.show();
	}

	private void setOption(int position) {

		switch (position) {

		case 1: // text
			option = 1;
			setEditText();

			break;

		case 2: // color
			option = 2;
			fontList.setVisibility(View.GONE);
			etInput.setVisibility(View.GONE);
			layout_option.setVisibility(View.GONE);
			setColor();

			break;

		case 3: // font
			option = 3;
			etInput.setVisibility(View.GONE);
			layout_option.setVisibility(View.VISIBLE);
			fontList.setVisibility(View.VISIBLE);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getActivity(), R.layout.font_row, mTextFontArray);
			fontList.setAdapter(adapter);

			setFont();

			break;

		case 4: // left align
			option = 4;
			mInputDialog.setBubbleTextAlign(3);

			break;

		case 5: // center align
			option = 5;
			mInputDialog.setBubbleTextAlign(2);

			break;

		case 6: // right align
			option = 6;
			mInputDialog.setBubbleTextAlign(1);

			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.Imageview_layout_close) {
			hideKeyBoard(layout_option);
			if (option == 3) {
				Typeface tf = Typeface.createFromAsset(getActivity()
						.getAssets(), "fonts/" + "SinkinSans-300Light.otf");

				mInputDialog.setBubbleTextTypeFace(tf);
			}
			layout_option.setVisibility(View.GONE);
		} else if (id == R.id.imageview_done) {
			if (changed) {
				mCurrentEditTextView.setInEdit(false);
				mBitmapHolder.setBm(generateBitmap());
			}
			layout_option.setVisibility(View.GONE);
			changed_bitmap = null;
			setHeaderContent();
			if (changed_bitmap != null) {
				changed_bitmap.recycle();
				changed_bitmap = null;
			}
		} else if (id == R.id.imageview_close) {
			layout_option.setVisibility(View.GONE);
			changed_bitmap = null;
			setHeaderContent();
			if (changed_bitmap != null) {
				changed_bitmap.recycle();
				changed_bitmap = null;
			}
		} else {
		}
	}

	private void hideKeyBoard(View view) {

		if (view != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager
					.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	private Bitmap generateBitmap() {

		changed_bitmap = Bitmap.createBitmap(mContentRootView.getWidth(),
				mContentRootView.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(changed_bitmap);
		mContentRootView.draw(canvas);
		return changed_bitmap;

	}

	private void setHeaderContent() {
		a_footer.setVisibility(View.VISIBLE);
		a_footer.startAnimation(downDrawerOpen);

		Bundle bundle = new Bundle();
		bundle.putString("message", "From Fragment");

		FragmentTransparent frag = new FragmentTransparent();
		frag.setArguments(bundle);
		replaceFrag(frag, FotoLibsConstant.MAIN);
	}

	private void replaceFrag(Fragment frag, String tag) {

		FragmentTransaction fragTransaction = getFragmentManager()
				.beginTransaction();
		fragTransaction.replace(R.id.fragment_main, frag, tag);
		fragTransaction.addToBackStack(tag);
		fragTransaction.commit();

	}

}
