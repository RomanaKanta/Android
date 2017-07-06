package smartmux.carouselviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by tanvir-android on 9/20/16.
 */
public class MyPagerAdapter  extends PagerAdapter {

    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    Context context;
    ArrayList<Integer> imageArray;
    int PAGES;
    int LOOPS = 1000;
    public MyPagerAdapter(MainActivity context, ArrayList<Integer> images) {
        this.context = context;
        this.imageArray = images;
        this.PAGES = images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (container == null) {
            return null;
        }
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.page, container, false);


        position = position % PAGES;

        ImageView image=(ImageView)view.findViewById(R.id.pagerimage);
        image.setImageResource(imageArray.get(position));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return PAGES * LOOPS;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }


}