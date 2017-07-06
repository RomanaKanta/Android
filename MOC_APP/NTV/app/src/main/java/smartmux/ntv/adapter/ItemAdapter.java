package smartmux.ntv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import smartmux.ntv.R;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    //    ArrayList<String> list = new ArrayList<>();
    Context context;
    boolean isVideo;


    public ItemAdapter(Context context, boolean isVideo) {
        this.context = context;
        this.isVideo = isVideo;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Context context = holder.mView.getContext();

        if(!isVideo) {

            holder.txtTitle.setText("Saint Teresa of Calcutta");
            holder.itemImage.setImageResource(R.drawable.photo);
        }else {
            holder.txtTitle.setText("Monsoon forces schools to close in Bangladesh");
            holder.itemImage.setImageResource(R.drawable.video);
        }

    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @Bind(R.id.item_title)
        TextView txtTitle;

        @Bind(R.id.item_image)
        ImageView itemImage;


        //        public ProductModelClass category;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);


            mView = view;

        }

    }
}
