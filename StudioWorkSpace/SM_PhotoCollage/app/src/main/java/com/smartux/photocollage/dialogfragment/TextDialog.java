package com.smartux.photocollage.dialogfragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartux.photocollage.CollageActivity;
import com.smartux.photocollage.R;
import com.smartux.photocollage.adapter.RecycleView_FontAdapter;
import com.smartux.photocollage.adapter.RecyclerView_TextAdapter;
import com.smartux.photocollage.model.ListData;
import com.smartux.photocollage.utils.FrameSelection;

import java.util.ArrayList;

/**
 * Created by tanvir-android on 6/3/16.
 */
public class TextDialog extends DialogFragment {

    private static RecyclerView textRecyclerView;
    private static RecyclerView textfontRecyclerView;

    ArrayList<ListData> itemArray = new ArrayList<ListData>();
    private ArrayList<String> mTextFontArray = new ArrayList<String>();
    Bundle mArgs;
    RelativeLayout textOptionLayer,editLayer;
    ImageView close;
    TextView done;
    EditText editText;
    int viewsize = 0;


    public void onResume() {
        super.onResume();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        getDialog().getWindow().getAttributes().gravity = Gravity.BOTTOM;
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.width = (int) ( width);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(lp);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater
                .inflate(R.layout.text_dialog, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.AnimationBottomUpDown;

        mArgs = getArguments();
        viewsize = mArgs.getInt("TEXT");


        itemArray = FrameSelection.getTextListItems(getActivity());
        mTextFontArray = FrameSelection.getTextFontItems(getActivity());

        textRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_text_view);
        textRecyclerView.setHasFixedSize(true);
        textRecyclerView.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.HORIZONTAL,
                false));

        textfontRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_text_font_view);
        textfontRecyclerView.setHasFixedSize(true);
        textfontRecyclerView.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.VERTICAL,
                false));

        textOptionLayer = (RelativeLayout) rootView.findViewById(R.id.layout_text_options);
        editLayer = (RelativeLayout) rootView.findViewById(R.id.edit_text_options);
        close = (ImageView) rootView.findViewById(R.id.Imageview_layout_close);
        done = (TextView) rootView.findViewById(R.id.tv_action_done);
        editText = (EditText) rootView.findViewById(R.id.et_bubble_input);



        populatRecyclerView();
        setOnCilck();

        return rootView;
    }

    private void setEditOption() {
        if(textOptionLayer.getVisibility()==View.GONE){
            textOptionLayer.setVisibility(View.VISIBLE);
        }
        editLayer.setVisibility(View.VISIBLE);
        textfontRecyclerView.setVisibility(View.GONE);

        editText.setHint("Text");
        editText.setText("");
        editText.setCursorVisible(true);
        editText.setFocusableInTouchMode(true);
    }

private void setOnCilck(){


    close.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            textOptionLayer.setVisibility(View.GONE);

            if (textOptionLayer != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textOptionLayer.getWindowToken(), 0);
            }
        }
    });
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = editText.getText().toString();

                    if (str.equals("")) {
                        ((CollageActivity) getActivity())
                                .setEditText("Text");
                    } else {
                        ((CollageActivity) getActivity())
                                .setEditText(str);
                    }

                    textOptionLayer.setVisibility(View.GONE);

                    if (textOptionLayer != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textOptionLayer.getWindowToken(), 0);
                    }
                }
            });

}

    // populate the list view by adding data to arraylist
    private void populatRecyclerView() {

        final RecyclerView_TextAdapter adapter = new RecyclerView_TextAdapter(
                getActivity(), itemArray,viewsize);
        textRecyclerView.setAdapter(adapter);// set adapter on recyclerview
        adapter.notifyDataSetChanged();// Notify the adapter

        adapter.SetOnItemClickListener(new RecyclerView_TextAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                if(position==0){
                    adapter.setviewsize(1);
                    adapter.notifyDataSetChanged();
                    ((CollageActivity) getActivity())
                            .addText();
                    setEditOption();

                }

                if(adapter.views !=0) {
                    setOption(position);
                }


            }
        });

        RecycleView_FontAdapter font_adapter = new RecycleView_FontAdapter(
                getActivity(), mTextFontArray);
        textfontRecyclerView.setAdapter(font_adapter);// set adapter on recyclerview
        font_adapter.notifyDataSetChanged();// Notify the adapter

        font_adapter.SetOnItemClickListener(new RecycleView_FontAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                ((CollageActivity) getActivity()).setFont(position,mTextFontArray.get(position).toString());
                dismiss();

            }
        });
    }

    private void setOption(int position) {

        switch (position) {


            case 1: // text
//                option = 1;
//                setEditText();


                setEditOption();


                break;

            case 2: // color
//                option = 2;
//                fontList.setVisibility(View.GONE);
//                etInput.setVisibility(View.GONE);
//                layout_option.setVisibility(View.GONE);
//                setColor();
                ((CollageActivity) getActivity()).setColor();
                dismiss();

                break;

            case 3: // font
//                option = 3;
//                etInput.setVisibility(View.GONE);
//                layout_option.setVisibility(View.VISIBLE);
//                fontList.setVisibility(View.VISIBLE);
//
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                        getActivity(), R.layout.font_row, mTextFontArray);
//                fontList.setAdapter(adapter);
//
//                setFont();

                if(textOptionLayer.getVisibility()==View.GONE){
                    textOptionLayer.setVisibility(View.VISIBLE);
                }
                editLayer.setVisibility(View.GONE);
                textfontRecyclerView.setVisibility(View.VISIBLE);

                break;

            case 4: // left align
//                option = 4;
//                mInputDialog.setBubbleTextAlign(3);
                ((CollageActivity) getActivity()).setAlign(3);

                break;

            case 5: // center align
//                option = 5;
//                mInputDialog.setBubbleTextAlign(2);
                ((CollageActivity) getActivity()).setAlign(2);

                break;

            case 6: // right align
//                option = 6;
//                mInputDialog.setBubbleTextAlign(1);
                ((CollageActivity) getActivity()).setAlign(1);

                break;

            default:
                break;
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
}
