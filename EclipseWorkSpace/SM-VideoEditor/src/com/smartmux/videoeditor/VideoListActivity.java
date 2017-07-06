package com.smartmux.videoeditor;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.smartmux.videoeditor.adapter.FrameAdapter;
import com.smartmux.videoeditor.utils.Utils;

public class VideoListActivity extends Activity {


	private static final String TAG = VideoListActivity.class
			.getSimpleName();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vedio_list);
		
		
		ListView videoListView = (ListView) findViewById(R.id.listView_vedio);
		
		FrameAdapter imageAdapter = new FrameAdapter(
				VideoListActivity.this);
		videoListView.setAdapter(imageAdapter);

		File targetDirector = new File(Utils
				.getVideoThumbDirectory());

		final File[] files = targetDirector.listFiles();
		for (File file : files) {
			imageAdapter.add(file.getAbsolutePath());
		}
		
		videoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent = new Intent(VideoListActivity.this,VideoActivity.class);
				intent.putExtra("filename", files[position].getName());
				startActivity(intent);
				
			}
		});


	}


	

}
