package com.ksproject.krishop.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.ksproject.krishop.R;
import com.ksproject.krishop.adapter.DialogListAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by home on 05-Feb-17.
 */
public class CustomizeDialog extends Dialog {

    View v = null;

    @Bind(R.id.dig_title)
    TextView Diatitle;

    @Bind(R.id.recyclerViewProductList)
    RecyclerView recyclerView;

    public CustomizeDialog(Context context, String title, ArrayList<String> all) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_list);
        ButterKnife.bind(this);
        v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);



        Diatitle.setText(title);

        recyclerView.setLayoutManager(new GridLayoutManager(context,1));
        DialogListAdapter adapter = new DialogListAdapter(context,all);
        recyclerView.setAdapter(adapter);

        adapter.SetOnItemClickListener(new DialogListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                dismiss();
            }
        });

    }



}
