package com.roundflat.musclecard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class TutorialInfoActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_info);

		this.findViewById(R.id.button_close).setOnClickListener(this);
		this.findViewById(R.id.TextView_weblink).setOnClickListener(this);
		this.findViewById(R.id.imageView_roundflat_logo).setOnClickListener(this);

	}


	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.button_close) {

			finish();
			overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
		}else if(v.getId() == R.id.TextView_weblink){
			
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse("http://www.kinken.org/"));
			startActivity(i);
			
		}else if(v.getId() == R.id.imageView_roundflat_logo){
			
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse("http://www.roundflat.jp"));
			startActivity(i);
			
		}

	}
	
	@Override
	public void onBackPressed() {
		
		finish();
		overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
	}
}
