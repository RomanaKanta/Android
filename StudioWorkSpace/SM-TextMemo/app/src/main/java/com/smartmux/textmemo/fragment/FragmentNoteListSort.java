package com.smartmux.textmemo.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.smartmux.textmemo.NoteEditorActivity;
import com.smartmux.textmemo.R;
import com.smartmux.textmemo.adapter.NoteListAdapter;
import com.smartmux.textmemo.modelclass.NoteListItem;
import com.smartmux.textmemo.modelclass.SelectedNoteArray;
import com.smartmux.textmemo.utils.AppExtra;
import com.smartmux.textmemo.utils.FileManager;
import com.smartmux.textmemo.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentNoteListSort extends Fragment {

	int position = 0;
	private ListView listView;
	TextView match_found;
	public ArrayList<NoteListItem> listItems = new ArrayList<>();
	public NoteListAdapter listAdapter;
	private FileManager fileManager;
	EditText etSearch = null;
	ImageView ivSearchClear = null;
	CharSequence searchChar = "";
	Typeface tf;

	public static FragmentNoteListSort newInstance() {
		FragmentNoteListSort fragment = new FragmentNoteListSort();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_note_list,
				container, false);

		position = FragmentPagerItem.getPosition(getArguments());

		tf = Typeface.createFromAsset(getActivity().getAssets(),
				AppExtra.AVENIRLSTD_BLACK);

		fileManager = new FileManager();

		listItems = fileManager.getAllNotes(getActivity());
		System.out.println("listItems "+listItems);

		match_found = (TextView) rootView.findViewById(R.id.textview_not_found);
		match_found.setTypeface(tf);

		if (listItems != null && listItems.size() > 0) {
			match_found.setVisibility(View.GONE);

		} else {

			match_found.setText(R.string.creat_info);

			match_found.setVisibility(View.VISIBLE);
		}

		etSearch = (EditText) getActivity().findViewById(R.id.edittext_search);
		etSearch.setTypeface(tf);
		ivSearchClear = (ImageView) getActivity().findViewById(
				R.id.imageview_search_clear);
		listView = (ListView) rootView.findViewById(R.id.listviewNoteList);

		setListAdapter();

		// ((NoteListActivity) getActivity())
		// .setFragmentRefreshListener(new
		// NoteListActivity.FragmentRefreshListener() {
		// @Override
		// public void onRefresh() {
		//
		// listAdapter.notifyDataSetChanged();
		// }
		// });

		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				searchChar = cs;
				if (cs.length() > 0) {
					ivSearchClear.setVisibility(View.VISIBLE);
				} else {
					ivSearchClear.setVisibility(View.GONE);
				}

				if (listItems != null && listItems.size() != 0) {

					listAdapter.getFilter().filter(cs.toString());
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

		ivSearchClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				etSearch.setText(null);
			}
		});

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		// int event_code = fileManager.getReturnCode(getActivity());
		// if (event_code == AppExtra.BACK_CODE) {

		setListAdapter();
		 if(SelectedNoteArray.getSelectedData().size()!=0){
		 setSelectedNotes(position);
		 }
		// Toast.makeText(getActivity(), "On Resume" + position, 1000).show();
		//

		// }
		if (listItems != null && listItems.size() != 0) {
			if (etSearch.getText().toString() != "") {
				listAdapter.getFilter().filter(searchChar.toString());
			}
		}

	}

	public void setSelectedNotes(int page) {

		if (listAdapter != null) {

			ArrayList<NoteListItem> setSelected = new ArrayList<NoteListItem>();

			setSelected = SelectedNoteArray.getSelectedData();
			if (setSelected.size() > 0) {

				if (page == 0) {
					Collections.sort(setSelected,
							new GeneralUtils.TitleComparator());
				}
				if (page == 1) {
					Collections.sort(setSelected,
							new GeneralUtils.DateComparator());
				}

				if (page == 2) {
					Collections.sort(setSelected,
							new GeneralUtils.NoteSizeComparator());

				}

				listAdapter.setCheckedItem(setSelected,
						SelectedNoteArray.getCount());
				// listAdapter.notifyDataSetChanged();

			}
		}
	}

	public void setListAdapter() {

//		if(listItems == null){
//
//			return;
//		}
        if(listItems!=null ) {
            if ((listItems.size() == 0)
                    || (listItems.size() != fileManager.getAllNotes(getActivity())
                    .size())) {
                listItems = fileManager.getAllNotes(getActivity());
            }

            if (listItems != null && listItems.size() > 0) {

                match_found.setVisibility(View.GONE);

                if (listAdapter != null) {
                    listAdapter = null;
                }

                if (position == 0 && listItems != null) {

                    Collections.sort(listItems, new GeneralUtils.TitleComparator());

                }
                if (position == 1 && listItems != null) {

                    Collections.sort(listItems, new GeneralUtils.DateComparator());

                }
                if (position == 2 && listItems != null) {

                    Collections.sort(listItems,
                            new GeneralUtils.NoteSizeComparator());

                }

                listAdapter = new NoteListAdapter(getActivity(), listItems);
                listView.setAdapter(listAdapter);

                // if(SelectedNoteArray.actionMode){
                // listAdapter.setCheckedItem(SelectedNoteArray.getSelectedData(),SelectedNoteArray.getCount());
                // }

                listView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        String fileName = listItems.get(position).getTitle();

                        Intent i = new Intent(getActivity(),
                                NoteEditorActivity.class);
                        i.putExtra(AppExtra.NOTE_MODE, AppExtra.NOTE_MODE_EDIT);
                        i.putExtra(AppExtra.NOTE_FILENAME, fileName);
                        startActivity(i);

//					SelectedNoteArray.finishMode();
                        getActivity().overridePendingTransition(
                                R.anim.push_left_in, R.anim.push_left_out);
                    }
                });

            }
        }
	}

}
