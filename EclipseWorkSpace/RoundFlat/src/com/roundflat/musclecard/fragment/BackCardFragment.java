package com.roundflat.musclecard.fragment;

import com.roundflat.musclecard.R;
import com.roundflat.musclecard.model.TutorialModel;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BackCardFragment extends Fragment {
	
	private static final String BUNDLE_KEY_SUBTITLE                = "bundle_key_subtitle";
    private static final String BUNDLE_KEY_TITLE    = "bundle_key_title";
    private static final String BUNDLE_KEY_LABEL_JP                = "bundle_key_label_jp";
    private static final String BUNDLE_KEY_LABEL_EN    = "bundle_key_label_en";
    String imagePatterName = "_A_000.png";
    private String    sub_title,title,label_japanese,label_english;

    public static BackCardFragment instantiateWithArgs(final Context context, final TutorialModel card) {
        final BackCardFragment fragment = (BackCardFragment) instantiate(context, BackCardFragment.class.getName());
        final Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_SUBTITLE, card.getSub_title());
        args.putString(BUNDLE_KEY_TITLE, card.getTitle());
        args.putString(BUNDLE_KEY_LABEL_JP, card.getLabel_japanese());
        args.putString(BUNDLE_KEY_LABEL_EN, card.getLabel_english());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArguments();
    }

    private void initArguments() {
        final Bundle arguments = getArguments();
        if(arguments != null) {
        	sub_title = arguments.getString(BUNDLE_KEY_SUBTITLE);
        	title = arguments.getString(BUNDLE_KEY_TITLE);
        	label_japanese = arguments.getString(BUNDLE_KEY_LABEL_JP);
        	label_english = arguments.getString(BUNDLE_KEY_LABEL_EN);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.pager_item_back, container, false);
        
        TextView txtSubtitle = (TextView) view.findViewById(R.id.textView_subtitle);
		TextView txtTitle = (TextView) view.findViewById(R.id.textView_title);
		TextView txtJP = (TextView) view.findViewById(R.id.textView_jp);
		TextView txtEng = (TextView) view.findViewById(R.id.textView_eng);

		txtSubtitle.setText(sub_title);
		txtTitle.setText(title);
		txtJP.setText(label_japanese);
		txtEng.setText(label_english);

		if ( title.equals("NIL")) {

			txtTitle.setText(sub_title);
		}
        return view;
    }


}
