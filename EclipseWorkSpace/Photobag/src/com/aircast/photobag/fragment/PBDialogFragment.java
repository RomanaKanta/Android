package com.aircast.photobag.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.aircast.photobag.R;

public class PBDialogFragment extends DialogFragment{
	
	public static PBDialogFragment newInstance(int title,int mgs) {
		PBDialogFragment frag = new PBDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("mgs", mgs);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        int mgs = getArguments().getInt("mgs");

        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(title)
                .setMessage(getString(mgs))
                .setPositiveButton(R.string.dialog_ok_btn,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                           // ((FragmentAlertDialog)getActivity()).doPositiveClick();
                        	dialog.dismiss();
                        }
                    }
                )
//                .setNegativeButton(R.string.alert_dialog_cancel,
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                          //  ((FragmentAlertDialog)getActivity()).doNegativeClick();
//                        }
//                    }
//                )
                .create();
    }

}
