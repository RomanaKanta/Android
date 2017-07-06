package com.ksproject.krishop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ksproject.krishop.R;

import butterknife.ButterKnife;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    //    ArrayList<String> list = new ArrayList<>();
    Context context;


    public SearchAdapter(Context context) {
        this.context = context;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Context context = holder.mView.getContext();


    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;


        //        public ProductModelClass category;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);


            mView = view;

        }

    }
}
