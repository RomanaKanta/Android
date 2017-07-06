package smartmux.ntv.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import smartmux.ntv.R;


public class PhotoVideoAdapter extends RecyclerView.Adapter<PhotoVideoAdapter.ViewHolder> {

    ArrayList<String> list = new ArrayList<>();
    Context context;
    boolean isVideo;


    public PhotoVideoAdapter(Context context,ArrayList<String> list,boolean isVideo) {
        this.context = context;
        this.list = list;
        this.isVideo = isVideo;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        final Context context = holder.mView.getContext();

        holder.txtTopic.setText(list.get(position));
holder.itemList.setAdapter(new ItemAdapter(context,isVideo));

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @Bind(R.id.topic)
        TextView txtTopic;

        @Bind(R.id.photo_video_items)
        RecyclerView itemList;



        //        public ProductModelClass category;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            itemList.setHasFixedSize(true);
            itemList.setLayoutManager(new LinearLayoutManager(
                    context, LinearLayoutManager.HORIZONTAL,
                    false));
            mView = view;

        }

    }
}
