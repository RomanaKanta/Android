package smartmux.ntv.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import smartmux.ntv.R;
import smartmux.ntv.adapter.PhotoVideoAdapter;

public class PhotoFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public PhotoFragment() {
        // Required empty public constructor
    }

    public static PhotoFragment newInstance(String param1, String param2) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Bind(R.id.photo_video_feeds)
    RecyclerView mListViewFeeds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        ButterKnife.bind(this, view);

        setList();

        return view;
    }

    private void setList() {

        ArrayList<String> list = new ArrayList<>();
        list.add("Bangladesh");
        list.add("World");
        list.add("Entertainment");
        list.add("Sport");
        list.add("Technology");
        list.add("Economy");

        mListViewFeeds.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//        final int gridMargin = getResources().getDimensionPixelOffset(R.dimen.grid_margin);
        mListViewFeeds.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.set(0, 0, 0, 0);
            }
        });

        mListViewFeeds.setPadding(0, 0, 0, 0);

        mListViewFeeds.setAdapter(new PhotoVideoAdapter(getActivity(),list,false));

    }

}
