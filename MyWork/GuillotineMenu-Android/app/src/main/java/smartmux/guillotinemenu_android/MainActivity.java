package smartmux.guillotinemenu_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yalantis.guillotine.animation.GuillotineAnimation;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final long RIPPLE_DURATION = 250;


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.root)
    FrameLayout root;
    @Bind(R.id.content_hamburger)
    View contentHamburger;

    TextView title;
    LinearLayout profile;
    LinearLayout feed;
    LinearLayout activity;
    LinearLayout setteing;
    GuillotineAnimation.GuillotineBuilder  menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);

            title = (TextView) toolbar.findViewById(R.id.tool_title);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);

        menu =  new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger);

        menu.setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();

        final GuillotineAnimation amin = new GuillotineAnimation(menu);



        profile = (LinearLayout) guillotineMenu.findViewById(R.id.profile_group);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("Profile");
                amin.close();
            }
        });
        feed = (LinearLayout) guillotineMenu.findViewById(R.id.feed_group);

        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("feed");
            }
        });
        activity = (LinearLayout) guillotineMenu.findViewById(R.id.activity_group);

        activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("activity");
            }
        });
        setteing = (LinearLayout) guillotineMenu.findViewById(R.id.settings_group);

        setteing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("setteing");
            }
        });

    }

}
