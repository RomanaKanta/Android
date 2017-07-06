package com.smartmux.banglamusic;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.smartmux.banglamusic.LeftContainerMenu.SizeCallback;
import com.smartmux.banglamusic.adapter.MenuAdapter;
import com.smartmux.banglamusic.adapter.MenuAdapterForSong;
import com.smartmux.banglamusic.model.ItemRow;
import com.smartmux.banglamusic.model.RowItemForSong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@SuppressLint({ "SetJavaScriptEnabled", "CutPasteId"})
public class MainContainer extends Activity {
	//JSON part for album
    private static final String TAG_ALBUMS = "response";
    private static final String TAG_ID = "album_id";
    private static final String TAG_ETITLE = "album_english_title";
    private static final String TAG_BTITLE = "album_bengali_title";
    private static final String TAG_ARTIST = "album_artist_name";
    private static final String TAG_THUMBURL = "album_thumb_image_url";
    private static final String TAG_NSONGS = "album_number_of_songs";
    
    //JSON part for SONGS
    private static final String TAG_SONGS = "response";
    private static final String TAG_TITLE = "song_title";
    private static final String TAG_DOWNURL = "song_download_url";
    
    //Download segment
    private static String filename = null;
    String sSongTitle;
    String sSongPath;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    public final static String SONG_TITLE = "com.smartmux.banglamusic.MESSAGE1";
    public final static String SONG_PATH = "com.smartmux.banglamusic.MESSAGE2";
    //end of download segment
   
    
    JSONArray albums = null;
    JSONArray songs = null;
    private static String jsonUrl = null;
    private static String jsonSongsUrl = null;
    private static String sTitle = null;
    //final String mVidUrl;
    final ArrayList<String> albumID = new ArrayList<String>();
    final ArrayList<String> albumArtist = new ArrayList<String>();
    final ArrayList<String> albumTitle = new ArrayList<String>();
    final ArrayList<String> downLink = new ArrayList<String>();
    final ArrayList<String> songName = new ArrayList<String>();
    ArrayList<ItemRow> albumList = new ArrayList<ItemRow>();
    ArrayList<RowItemForSong> songList = new ArrayList<RowItemForSong>();
	
    LeftContainerMenu scrollView;
    MenuAdapter menuAdapter;
    MenuAdapterForSong menuAdapterForSong;
    View menu;
    View app;
    View detailView;
    Button btnSlide;
    static boolean menuOut = false;
    static boolean isPlaying = false;
    int btnWidth;
    ProgressDialog mProgress;
    ProgressDialog songProgress;
    
    

    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        if( Build.VERSION.SDK_INT >= 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy); 
        }

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                LayoutInflater inflater = LayoutInflater.from(MainContainer.this);
                scrollView = (LeftContainerMenu) inflater.inflate(R.layout.screen_scroll_with_list_menu, null);
                setContentView(scrollView);

                menu = inflater.inflate(R.layout.left_container_menu, null);
                app = inflater.inflate(R.layout.main_display, null);
                ViewGroup tabBar = (ViewGroup) app.findViewById(R.id.tabBar);


        /* Slide Button Action*/
                btnSlide = (Button) tabBar.findViewById(R.id.btnSlide);
                btnSlide.setOnClickListener(new ClickListenerForScrolling(scrollView, menu));
                final View[] children = new View[] { menu, app };
                int scrollToViewIdx = 1;
                scrollView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu(btnSlide));

                mProgress = new ProgressDialog(MainContainer.this);
                songProgress = new ProgressDialog(MainContainer.this);
                jsonUrl = "http://smiley-bangladesh.com/sp-apps/admin/getContentJson.php?contentType=album";
                new SM_AsyncTaskForAlbums().execute(jsonUrl);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//                Toast.makeText(MainContainer.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
       
    }
 
    static class ClickListenerForScrolling implements OnClickListener {
        LeftContainerMenu scrollView;
        View menu;

        public ClickListenerForScrolling(LeftContainerMenu scrollView, View menu) {
            super();
            this.scrollView = scrollView;
            this.menu = menu;
        }

        @Override
        public void onClick(View v) {
            
            int menuWidth = menu.getMeasuredWidth();
            
            // Ensure menu is visible
            menu.setVisibility(View.VISIBLE);

            if (!menuOut) {
                // Scroll to 0 to reveal menu
            	Log.d("===slide==","Scroll to right");
            	Log.d("===clicked==","clicked");
                int left = 20;
                scrollView.smoothScrollTo(left, 0);
            } else {
                // Scroll to menuWidth so menu isn't on screen.
            	Log.d("===slide==","Scroll to left");
            	Log.d("===clicked==","clicked");
                int left = menuWidth;
                scrollView.smoothScrollTo(left, 0);
            }
            menuOut = !menuOut;
        }
    }

    static class SizeCallbackForMenu implements SizeCallback {
        int btnWidth;
        View btnSlide;

        public SizeCallbackForMenu(View btnSlide) {
            super();
            this.btnSlide = btnSlide;
        }

        @Override
        public void onGlobalLayout() {
            btnWidth = btnSlide.getMeasuredWidth();
            System.out.println("btnWidth=" + btnWidth);
        }

        @Override
        public void getViewSize(int idx, int w, int h, int[] dims) {
            dims[0] = w;
            dims[1] = h;
            final int menuIdx = 0;
            if (idx == menuIdx) {
                dims[0] = w - btnWidth;
            }
        }
    }
  
    //scroll the page and open the webview
    private void scrollWebviw(LeftContainerMenu scrollView, View menu) {
         int menuWidth = menu.getMeasuredWidth();

         // Ensure menu is visible
         menu.setVisibility(View.VISIBLE);

         if (!menuOut) {
             // Scroll to 0 to reveal menu
         	Log.d("===slide==","Scroll to right");
             int left = 0;
             scrollView.smoothScrollTo(left, 0);
         } else {
             // Scroll to menuWidth so menu isn't on screen.
         	Log.d("===slide==","Scroll to left");
             int left = menuWidth;
             scrollView.smoothScrollTo(left, 0);
            
         }
         menuOut = false;
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    }
    
    class SM_AsyncTaskForAlbums extends AsyncTask<String, Integer, List<ItemRow>> {
		
		
		 @Override
		 protected void onPreExecute() {
			    super.onPreExecute();
			    mProgress.setMessage("Please wait..");
			    mProgress.setIndeterminate(true);
			    mProgress.setCancelable(false);
			    mProgress.show();
		        }
		 
		@Override
		protected List<ItemRow> doInBackground(
		        String... params) {
			// Creating new JSON Parser
	        JSONParser jParser = new JSONParser();
	 
	        // Getting JSON from URL
	        JSONObject json = jParser.getJSONFromUrl(jsonUrl);
		    
		    try {
		        // Getting JSON Array
               albums = json.getJSONArray(TAG_ALBUMS);
	        	
	              for (int i = 0; i < albums.length(); i++) {
	              JSONObject c = albums.getJSONObject(i);
	              String id = c.getString(TAG_ID);
	              String bengaliTitle = c.getString(TAG_BTITLE);
	              String title = c.getString(TAG_ETITLE);
	              String image = c.getString(TAG_THUMBURL);
	              String artist = c.getString(TAG_ARTIST);
	              String totalSongs = c.getString(TAG_NSONGS);
	              
	              albumArtist.add(artist);
	              albumID.add(id);
	              albumTitle.add(title);
		          
		          
		          ItemRow item = new ItemRow(bengaliTitle,title,artist,totalSongs,image);
		          albumList.add(item);
		          
		        
		        }
		        
		    } catch (JSONException e) {
		        e.printStackTrace();
		    }
		
		    return albumList;
		}
		
		
		
		protected void onPostExecute(List<ItemRow> result) {
			super.onPostExecute(result);
			mProgress.dismiss();
			
		       ListView listView = (ListView) menu.findViewById(R.id.list);
			   menuAdapter = new MenuAdapter(MainContainer.this,
		            R.layout.item_row, result);
			   listView.setAdapter(menuAdapter);
			   
			   menuOut = true;
	   		   scrollWebviw(scrollView,menu);
	   		   listView.setOnItemClickListener(new OnItemClickListener() {
	
		    	public void onItemClick(AdapterView<?> parent, View view, int position,
		    	        long id) {
				    		final String sID = albumID.get(position);
				    		sTitle = albumTitle.get(position);
			    			
			    			jsonSongsUrl = "http://smiley-bangladesh.com/sp-apps/admin/getContentJson.php?contentType=song&albumID="+sID;
			    	        new SM_AsyncTaskForSongs().execute(jsonSongsUrl);
		    	    }
		    	});
 
	   }

	}
    
    class SM_AsyncTaskForSongs extends AsyncTask<String, Integer, List<RowItemForSong>> {
    	
    	 @Override
		 protected void onPreExecute() {
			    super.onPreExecute();
			    songProgress.setMessage("Please wait..");
			    songProgress.setIndeterminate(true);
			    songProgress.setCancelable(false);
			    songProgress.show();
		        }
		 
		@Override
		protected List<RowItemForSong> doInBackground(
		        String... params) {
			// Creating new JSON Parser
	        JSONParser jParser = new JSONParser();
	 
	        // Getting JSON from URL
	        JSONObject json = jParser.getJSONFromUrl(jsonSongsUrl);
		    
		    try {
		        // Getting JSON Array
               songs = json.getJSONArray(TAG_SONGS);
	        	
	              for (int i = 0; i < songs.length(); i++) {
	              JSONObject d = songs.getJSONObject(i);
	              String titleSong = d.getString(TAG_TITLE);
	              String dlink = d.getString(TAG_DOWNURL);
	              String artist = albumArtist.get(0);
	              
	              
	              downLink.add(dlink);
	              songName.add(titleSong);
		          
		          
		          RowItemForSong itemSong = new RowItemForSong(titleSong,artist);
		          songList.add(itemSong);
		        
		        }
		        
		    } catch (JSONException e) {
		        e.printStackTrace();
		    }
		
		    return songList;
		}
		
		
		
		protected void onPostExecute(List<RowItemForSong> result) {
			super.onPostExecute(result);
			songProgress.dismiss();
		   		RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.mainContent);
	    		//to remove previous inflate view
	    		LinearLayout detlayout = (LinearLayout) findViewById(R.id.detailLayout);
				if(detlayout != null){
					View myView = findViewById(R.id.detailLayout);
		            ViewGroup p = (ViewGroup) myView.getParent();
		            p.removeView(myView);
				}
		   		detailView = getLayoutInflater().inflate(R.layout.music_detail, rLayout,false);
		   		
				final TextView tvMainTitle = (TextView) findViewById(R.id.mainTitle);
	    		tvMainTitle.setText(sTitle);
	    		
	    		//hide agreement..............
	    		TextView tvEngFirst = (TextView) findViewById(R.id.engFirst);
	    		TextView tvEngSecond = (TextView) findViewById(R.id.engSecond);
	    		TextView tvBengaliFirst = (TextView) findViewById(R.id.bengFirst);
	    		TextView tvBengaliSecond = (TextView) findViewById(R.id.bengSecond);
	    		TextView tvBTitle = (TextView) findViewById(R.id.mainBengTitle);
	    		TextView tvETitle = (TextView) findViewById(R.id.mainAggTitle);
	    		TextView tvBelowTitle = (TextView) findViewById(R.id.mainBelowTitle);
	    		tvEngFirst.setVisibility(View.GONE);
	    		tvEngSecond.setVisibility(View.GONE);
	    		tvBengaliFirst.setVisibility(View.GONE);
	    		tvBengaliSecond.setVisibility(View.GONE);
	    		tvBTitle.setVisibility(View.GONE);
	    		tvETitle.setVisibility(View.GONE);
	    		tvBelowTitle.setVisibility(View.GONE);
	    		//end of hiding agreement codes
	    		
	    		ListView songListView = (ListView) detailView.findViewById(R.id.songList);
			       menuAdapterForSong = new MenuAdapterForSong(MainContainer.this,
			            R.layout.song_item_row, result);
			       songListView.setAdapter(menuAdapterForSong);
	    		rLayout.addView(detailView);
				   menuOut = true;
		   		   scrollWebviw(scrollView,menu);
	   		    songListView.setOnItemClickListener(new OnItemClickListener() {
	
		    	public void onItemClick(AdapterView<?> parent, View view, int position,
		    	        long id) {
		    	    sSongTitle = songName.get(position);
		    		String sDownloadUrl = downLink.get(position);
		    		filename= sSongTitle+".mp3";
		    		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Songs/"+filename);
		    		if (file.exists()) {
		    			sSongPath = file.getAbsolutePath();
		    			Intent intent = new Intent(MainContainer.this, MusicActivity.class);
		        	    intent.putExtra(SONG_TITLE, sSongTitle);
		        	    intent.putExtra(SONG_PATH, sSongPath);
		        	    startActivity(intent);
		    		}else{
		    			new DownloadFileAsync().execute(sDownloadUrl);
		    		}
		    	    }
		    	});
 
	   }
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Downloading file..");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
        }
    }
    
    class DownloadFileAsync extends AsyncTask<String, String, String> {
		   
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(DIALOG_DOWNLOAD_PROGRESS);
		}

		@Override
		protected String doInBackground(String... aurl) {
			int count;

		try {

		URL url = new URL(aurl[0]);
		URLConnection conexion = url.openConnection();
		conexion.connect();

		int lenghtOfFile = conexion.getContentLength();
		Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

		InputStream input = new BufferedInputStream(url.openStream());
		OutputStream output = new FileOutputStream(getSaveFilePath(filename));

		byte data[] = new byte[1024];

		long total = 0;

			while ((count = input.read(data)) != -1) {
				total += count;
				publishProgress(""+(int)((total*100)/lenghtOfFile));
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			input.close();
		} catch (Exception e) {}
		return null;

		}
		protected void onProgressUpdate(String... progress) {
			 Log.d("ANDRO_ASYNC",progress[0]);
			 mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		@SuppressWarnings({ "deprecation" })
		@Override
		protected void onPostExecute(String unused) {
			dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
			//start music player
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Songs/"+filename);
			sSongPath = file.getAbsolutePath();
			Intent intent = new Intent(MainContainer.this, MusicActivity.class);
    	    intent.putExtra(SONG_TITLE, sSongTitle);
    	    intent.putExtra(SONG_PATH, sSongPath);
    	    startActivity(intent);
		}
	}
    
    public static File getSaveFilePath(String fileName) {
	    File dir = new File(Environment.getExternalStorageDirectory(),"Songs");
	    dir.mkdirs();
	    File file = new File(dir, fileName);
	    return file;
	}

}
