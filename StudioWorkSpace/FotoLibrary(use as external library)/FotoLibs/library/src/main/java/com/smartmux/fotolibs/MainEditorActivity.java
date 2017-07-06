package com.smartmux.fotolibs;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ortiz.touch.TouchImageView;
import com.smartmux.fotolibs.adapter.RecycleView_HorizontalListAdapter;
import com.smartmux.fotolibs.adapter.RecycleView_HorizontalListAdapter.OnItemClickListener;
import com.smartmux.fotolibs.fragment.FragmentAdjustment;
import com.smartmux.fotolibs.fragment.FragmentBlur;
import com.smartmux.fotolibs.fragment.FragmentCrop;
import com.smartmux.fotolibs.fragment.FragmentDraw;
import com.smartmux.fotolibs.fragment.FragmentEffect;
import com.smartmux.fotolibs.fragment.FragmentFilter;
import com.smartmux.fotolibs.fragment.FragmentFrame;
import com.smartmux.fotolibs.fragment.FragmentResize;
import com.smartmux.fotolibs.fragment.FragmentRotate;
import com.smartmux.fotolibs.fragment.FragmentSticker;
import com.smartmux.fotolibs.fragment.FragmentText;
import com.smartmux.fotolibs.fragment.FragmentTransparent;
import com.smartmux.fotolibs.modelclass.BitmapHolder;
import com.smartmux.fotolibs.modelclass.BitmapManager;
import com.smartmux.fotolibs.modelclass.ListData;
import com.smartmux.fotolibs.utils.AddItems;
import com.smartmux.fotolibs.utils.ClassCompressImage;
import com.smartmux.fotolibs.utils.FotoLibsConstant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class MainEditorActivity extends FragmentActivity implements
        OnClickListener {

    private static Bitmap bmp_main;
    private TouchImageView image;

    private ArrayList<ListData> mBottomListData = new ArrayList<ListData>();

    RecycleView_HorizontalListAdapter list_Adapter;
    private static RecyclerView bottom_listview;

    String strtext = "";
    Animation downDrawerOpen, downDrawerClose = null;
    ImageView imageview_done, imageview_close;
    TextView imageview_logo, textview_title, textview_done;
    RelativeLayout footer = null;
    private FragmentManager fragmentManager;
    ClassCompressImage compressPhoto;
    TouchImageView mTouchImageView;
    BitmapHolder mBitmapHolder = null;

    Intent intent;
    File m_imagePath;

    BitmapManager mBitmapManager;
    private String cacheKey = "MainBitmap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main_editor);
        mBitmapHolder = new BitmapHolder();

        compressPhoto = new ClassCompressImage(MainEditorActivity.this);
        intent = getIntent();

        // kanta
        fragmentManager = getFragmentManager();
        setHeader();
        setFooter();

        mBitmapManager = new BitmapManager();

        // ishtiaq
        image = (TouchImageView) findViewById(R.id.editor_imageView);
        // setPhoto();
        new LoadImage().execute();

    }

    public void setHeader() {

        footer = (RelativeLayout) findViewById(R.id.horizontalLayout);

        imageview_logo = (TextView) findViewById(R.id.imageview_logo);
        imageview_done = (ImageView) findViewById(R.id.imageview_done);
        imageview_close = (ImageView) findViewById(R.id.imageview_close);
        textview_title = (TextView) findViewById(R.id.textview_header);
        textview_done = (TextView) findViewById(R.id.textview_done);

        imageview_logo.setOnClickListener(this);
        textview_done.setOnClickListener(this);

    }

    public void setFooter() {

        downDrawerOpen = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        downDrawerClose = AnimationUtils
                .loadAnimation(this, R.anim.bottom_down);

        mBottomListData = AddItems.getFirstListItems();

        bottom_listview = (RecyclerView) findViewById(R.id.horizontal_listview);
        bottom_listview.startAnimation(downDrawerOpen);
        bottom_listview.setHasFixedSize(true);
        bottom_listview.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        list_Adapter = new RecycleView_HorizontalListAdapter(this,
                mBottomListData);
        bottom_listview.setAdapter(list_Adapter);// set adapter on
        // recyclerview
        list_Adapter.notifyDataSetChanged();

        list_Adapter.SetOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                footer.startAnimation(downDrawerClose);
                footer.setVisibility(View.INVISIBLE);
                setItem(mBottomListData.get(position).getMid());
            }
        });

    }

    public class LoadImage extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progress = null;
        String path;

        public LoadImage() {
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progress = ProgressDialog.show(MainEditorActivity.this, null,
                    "Photo Loading");

        }

        @Override
        protected Void doInBackground(Void... params) {
             path = intent.getExtras().getString("path");
//            String path ="/mnt/sdcard/cutter/1460352060661.png";

            if(path!=null) {
                bmp_main = compressPhoto.compressImage(path);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if(path!=null && bmp_main!=null) {
                mBitmapHolder.setBm(bmp_main);
                image.setImageBitmap(bmp_main);
                progress.dismiss();
            }else{
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Runtime runtime = Runtime.getRuntime();
        // System.out.println("onLowMemory : "
        // + runtime.freeMemory());

        Toast.makeText(getApplicationContext(),
                "onLowMemory : " + runtime.freeMemory(), Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bmp_main = null;
        if (bmp_main != null) {

            bmp_main.recycle();
            bmp_main = null;
        }
        mBitmapHolder.drop();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imageview_logo) {

            setResult(RESULT_OK, intent);
            finish();

        } else if (id == R.id.textview_done) {

            new SaveLoader().execute();

        } else {
        }
    }

    private void setItem(int id) {

        Runtime runtime = Runtime.getRuntime();
        System.out.println("From FOTOLibs Free memory : "
                + runtime.freeMemory());

        if (id == 0) {

            FragmentFilter frag = new FragmentFilter();
            replaceFrag(frag, FotoLibsConstant.FILTER);

        } else if (id == 1) {

            FragmentFrame frag = new FragmentFrame();
            replaceFrag(frag, FotoLibsConstant.FRAME);

        } else if (id == 2) {

            FragmentEffect frag = new FragmentEffect();
            replaceFrag(frag, FotoLibsConstant.EFFECT);

        } else if (id == 3) {

            FragmentBlur frag = new FragmentBlur();
            replaceFrag(frag, FotoLibsConstant.BLUR);

        }
        if (id == 4) {

            FragmentAdjustment frag = new FragmentAdjustment();
            replaceFrag(frag, FotoLibsConstant.ADJUST);

        } else if (id == 5) {

            FragmentRotate frag = new FragmentRotate();
            replaceFrag(frag, FotoLibsConstant.ROTATE);

        } else if (id == 6) {

            FragmentDraw frag = new FragmentDraw();
            replaceFrag(frag, FotoLibsConstant.DRAW);

        } else if (id == 7) {

            FragmentCrop frag = new FragmentCrop();
            replaceFrag(frag, FotoLibsConstant.CROP);

        } else if (id == 8) {

            FragmentResize frag = new FragmentResize();
            replaceFrag(frag, FotoLibsConstant.RESIZE);

        } else if (id == 9) {

            FragmentSticker frag = new FragmentSticker();
            replaceFrag(frag, FotoLibsConstant.STICKER);

        } else if (id == 10) {

            FragmentText frag = new FragmentText();
            replaceFrag(frag, FotoLibsConstant.TEXT);

        }
    }

    private void replaceFrag(Fragment frag, String tag) {

        FragmentTransaction fragTransaction = getFragmentManager()
                .beginTransaction();
        fragTransaction.replace(R.id.fragment_main, frag, tag);
        fragTransaction.addToBackStack(tag);
        fragTransaction.commit();

    }

    @Override
    public void onBackPressed() {

        FragmentTransparent test = (FragmentTransparent) fragmentManager
                .findFragmentByTag(FotoLibsConstant.MAIN);
        if (test != null && test.isVisible()) {
            setResult(RESULT_OK, intent);
            finish();
        } else {

            addMainFragment();
        }

    }

    private void addMainFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("message", "From Activity");

        FragmentTransparent frag = new FragmentTransparent();
        frag.setArguments(bundle);
        replaceFrag(frag, FotoLibsConstant.MAIN);
    }

    public void removeCurrentFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment currentFrag = fragmentManager
                .findFragmentById(R.id.fragment_main);

        @SuppressWarnings("unused")
        String fragName = "NONE";

        if (currentFrag != null)
            fragName = currentFrag.getClass().getSimpleName();

        if (currentFrag != null)
            transaction.remove(currentFrag);

        transaction.commit();

    }

    private Bitmap getBitmap() {

        image.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(image.getDrawingCache());
        image.setDrawingCacheEnabled(false);

        return bmp;
    }

    public class SaveLoader extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progress = null;

        public SaveLoader() {
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainEditorActivity.this, null,
                    "Saving");

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            save();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progress.dismiss();
            super.onPostExecute(result);

            intent.putExtra("IMAGE_PATH", m_imagePath.toString());
            intent.putExtra("EDIT_DONE", "edit_done");
            setResult(RESULT_OK, intent);
            finish();

        }
    }

    private void save() {

        String root = Environment.getExternalStorageDirectory().toString()
                + "/Photo_Collage/Edit_Photo";

        File directory = new File(root);
        directory.mkdirs();

        long newdate = new Date().getTime();
        String fileName = newdate + ".png";
        m_imagePath = new File(directory + "/", fileName);
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(m_imagePath);
            getBitmap().compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();
            if (android.os.Build.VERSION.SDK_INT > 18) {

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://"
                                + Environment.getExternalStorageDirectory())));

            } else {

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://"
                                + Environment.getExternalStorageDirectory())));

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}