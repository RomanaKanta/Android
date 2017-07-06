package com.kanta.studio.watchout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.kanta.studio.watchout.utils.ConstantString;
import com.kanta.studio.watchout.utils.PreferenceUtils;

/**
 * Created by tanvir-android on 3/7/16.
 */
public class HomeActivity extends Activity implements View.OnClickListener {

    Button btnNewGame, btnContinue, btnIntroduction = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        btnNewGame = (Button) findViewById(R.id.newgame);
        btnContinue = (Button) findViewById(R.id.continu);
        btnIntroduction = (Button) findViewById(R.id.intru);

        btnNewGame.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        btnIntroduction.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();

        switch (id) {
            case R.id.newgame:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(R.string.sure);
                builder.setIcon(R.drawable.resatface);
                builder.setMessage(R.string.alart);
                builder.setCancelable(false);
                // Add the buttons
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceUtils.saveIntPref(
                                getApplicationContext(),
                                ConstantString.PREF_NAME,
                                ConstantString.level, 0);

                        Intent intentNewGame = new Intent(getBaseContext(),
                                LevelActivity.class);
                        startActivity(intentNewGame);
                        dialog.dismiss();
                    }
                });


                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO Auto-generated method stub

                        dialog.dismiss();

                    }
                });

                // Create the AlertDialog
                builder.show();

                break;
            case R.id.continu:

                Intent intentContinue = new Intent(getBaseContext(),
                        LevelActivity.class);
                startActivity(intentContinue);

                break;
            case R.id.intru:
                Toast.makeText(getApplicationContext(), "introduction", Toast.LENGTH_SHORT)
                        .show();
                break;

            default:
                break;

        }

    }

}
