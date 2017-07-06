package com.smartux.photocollage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.smartux.photocollage.model.Boxes;
import com.smartux.photocollage.model.SizeHolder;
import com.smartux.photocollage.utils.CompressImage;
import com.smartux.photocollage.utils.JsonManager;
import com.smartux.photocollage.widget.MagazinTextView;
import com.smartux.photocollage.widget.MagazinePhotoView;
import com.smartux.photocollage.widget.SAFrameFunPhotoView;

import java.util.ArrayList;


public class MagazineActivity extends AppMainActivity {

    RelativeLayout image_layer,root;
    MagazinePhotoView main_image_view;
    ImageView magazine_frame;
    CompressImage mCompressImage;

    public ArrayList<Boxes> boxArrayList;
    private ArrayList<Point> pathpointsArray;
    private Path path;
    EditText newNameEditText;

    MagazinTextView newText;

    GestureDetectorCompat gestureDetector;

    JsonManager mJsonManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        getScreenSize(MagazineActivity.this);
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = inflater.inflate(R.layout.activity_magazine, null);
        setContentView(R.layout.activity_magazine);

        mJsonManager = new JsonManager(MagazineActivity.this);
        boxArrayList = mJsonManager
                .getDataFromJson("image_text_template.json",true);

//        RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        root = (RelativeLayout) findViewById(R.id.magazine_layer);
        image_layer = (RelativeLayout) findViewById(R.id.main_ImageView);
        magazine_frame = (ImageView) findViewById(R.id.magazine_ImageView);

        mCompressImage = new CompressImage(MagazineActivity.this);

        main_image_view = new MagazinePhotoView(MagazineActivity.this);

//        main_image_view.setBitmap(mCompressImage.compressImage(getIntent().getExtras().getString("single_path")));
//        main_image_view.setLayoutParams(viewParams);
//        main_image_view.setBackgroundColor(Color.TRANSPARENT);
//
//        image_layer.addView(main_image_view);

        root.getLayoutParams().height = SizeHolder.getHt();

        for (int i = 0; i < boxArrayList.size(); i++) {

            int position = Integer.parseInt(boxArrayList.get(i).getPosition());

            pathpointsArray = boxArrayList.get(i).getPathPoints();

            RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(
                    boxArrayList.get(i).getBoxWidth(), boxArrayList.get(i)
                    .getBoxHeight());
                if (i > 0) {
                    viewParams.setMargins(boxArrayList.get(i).getLeftMargin(),
                            boxArrayList.get(i).getTopMargin(), boxArrayList
                                    .get(i).getRightMargin(), 0);

                }



            path = new Path();

            path.moveTo(pathpointsArray.get(0).x, pathpointsArray.get(0).y);

            for (int j = 0; j < pathpointsArray.size(); j++) {

                path.lineTo(pathpointsArray.get(j).x, pathpointsArray.get(j).y);

            }
            path.lineTo(pathpointsArray.get(0).x, pathpointsArray.get(0).y);

            if(i==position){
                MagazinTextView text = new MagazinTextView(this);
                text.setLayoutParams(viewParams);
                text.setBackgroundColor(Color.TRANSPARENT);
                text.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.fake_img));
                text.setPath(path);
                text.setActivity(this);
                text.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                image_layer.addView(text);

            }else{
                SAFrameFunPhotoView m_viewFrame = new SAFrameFunPhotoView(this);
                m_viewFrame.setId(i + 100);
                m_viewFrame.setLayoutParams(viewParams);
                m_viewFrame.setBackgroundColor(Color.TRANSPARENT);
                m_viewFrame.setBitmap(mCompressImage.compressImage(getIntent().getExtras().getString("single_path")));
//                m_viewFrame.setImagePath(imagePaths.get(i));
                m_viewFrame.setImageIndex(i);
                m_viewFrame.setPath(path);
                m_viewFrame.setBox(boxArrayList.get(i));

                m_viewFrame.setActivity(this);
                m_viewFrame.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            customViewList.add(m_viewFrame);
                // collageLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                image_layer.addView(m_viewFrame);
            }
        }
    }

    public void showRenameFolderDialog(final MagazinTextView text) {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MagazineActivity.this);
        View promptView = layoutInflater.inflate(R.layout.dialog_screen_rename, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MagazineActivity.this);
        alertDialogBuilder.setView(promptView);

         newNameEditText = (EditText) promptView.findViewById(R.id.newName);

        // setup a dialog window
        alertDialogBuilder.setCancelable(true);


        alertDialogBuilder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {
                        String newName = newNameEditText.getText()
                                .toString().trim();
                        if (newName.length() == 0) {
                            return;
                        }

                        text.setText(newName);
                        dialog.dismiss();
                    }
                });

        // create an alert dialog
        alertDialogBuilder.create().show();
    }
}
