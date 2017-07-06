package za.co.riggaroo.materialhelptutorial.tutorial;

import android.app.ActionBar;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintroexample.R;

import java.util.ArrayList;
import java.util.List;

import za.co.riggaroo.materialhelptutorial.MaterialTutorialFragment;
import za.co.riggaroo.materialhelptutorial.TutorialItem;
import za.co.riggaroo.materialhelptutorial.adapter.MaterialTutorialAdapter;
import za.co.riggaroo.materialhelptutorial.view.CirclePageIndicator;


public class MaterialTutorialActivity extends AppCompatActivity implements MaterialTutorialContract.View {

    private static final String TAG = "MaterialTutActivity";
    public static final String MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS = "tutorial_items";
    private ViewPager mHelpTutorialViewPager;
    private View mRootView;
//    private TextView mTextViewSkip;
//    private ImageButton mNextButton;
//    private Button mDoneButton;
    private MaterialTutorialPresenter materialTutorialPresenter;

    private View.OnClickListener finishTutorialClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            materialTutorialPresenter.doneOrSkipClick();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_tutorial);

        materialTutorialPresenter = new MaterialTutorialPresenter(this, this);
        setStatusBarColor();
        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            getActionBar().hide();
        }
        mRootView = findViewById(R.id.activity_help_root);
        mHelpTutorialViewPager = (ViewPager) findViewById(R.id.activity_help_view_pager);
//        mTextViewSkip = (TextView) findViewById(R.id.activity_help_skip_textview);
//        mNextButton = (ImageButton) findViewById(R.id.activity_next_button);
//        mDoneButton = (Button) findViewById(R.id.activity_tutorial_done);

//        mTextViewSkip.setOnClickListener(finishTutorialClickListener);
//        mDoneButton.setOnClickListener(finishTutorialClickListener);



//        mNextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                materialTutorialPresenter.nextClick();
//
//
//            }
//        });
//        List<TutorialItem> tutorialItems = getIntent().getParcelableArrayListExtra(MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS);
        materialTutorialPresenter.loadViewPagerFragments(getTutorialItems(this));
    }

    private ArrayList<TutorialItem> getTutorialItems(Context context) {
        TutorialItem tutorialItem1 = new TutorialItem(R.string.slide_1_african_story_books, R.string.slide_1_african_story_books,
                R.color.slide_1, R.drawable.tut_page_1_front,  R.drawable.tut_page_1_background);

        TutorialItem tutorialItem2 = new TutorialItem(R.string.slide_2_volunteer_professionals, R.string.slide_2_volunteer_professionals_subtitle,
                R.color.slide_2,  R.drawable.tut_page_2_front,  R.drawable.tut_page_2_background);

        TutorialItem tutorialItem3 = new TutorialItem(context.getString(R.string.slide_3_download_and_go), null,
                R.color.slide_3, R.drawable.tut_page_3_foreground);

        TutorialItem tutorialItem4 = new TutorialItem(R.string.slide_4_different_languages, R.string.slide_4_different_languages_subtitle,
                R.color.slide_4,  R.drawable.tut_page_4_foreground, R.drawable.tut_page_4_background);

        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        tutorialItems.add(tutorialItem4);

        return tutorialItems;
    }

    private void setStatusBarColor() {
        if (isFinishing()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void showNextTutorial() {
        int currentItem = mHelpTutorialViewPager.getCurrentItem();
        if (currentItem < materialTutorialPresenter.getNumberOfTutorials()) {
            mHelpTutorialViewPager.setCurrentItem(mHelpTutorialViewPager.getCurrentItem() + 1);
        }
    }

    @Override
    public void showEndTutorial() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void setBackgroundColor(int color) {
        mRootView.setBackgroundColor(color);
    }

    @Override
    public void showDoneButton() {
//        mTextViewSkip.setVisibility(View.INVISIBLE);
//        mNextButton.setVisibility(View.GONE);
//        mDoneButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSkipButton() {
//        mTextViewSkip.setVisibility(View.VISIBLE);
//        mNextButton.setVisibility(View.VISIBLE);
//        mDoneButton.setVisibility(View.GONE);
    }

    @Override
    public void setViewPagerFragments(List<MaterialTutorialFragment> materialTutorialFragments) {
        mHelpTutorialViewPager.setAdapter(new MaterialTutorialAdapter(getSupportFragmentManager(), materialTutorialFragments));
        CirclePageIndicator mCirclePageIndicator = (CirclePageIndicator) findViewById(R.id.activity_help_view_page_indicator);

        mCirclePageIndicator.setViewPager(mHelpTutorialViewPager);
        mCirclePageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                materialTutorialPresenter.onPageSelected(mHelpTutorialViewPager.getCurrentItem());

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mHelpTutorialViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
                    @Override
                    public void transformPage(View page, float position) {
                        materialTutorialPresenter.transformPage(page, position);
                    }
                }

        );
    }
}
