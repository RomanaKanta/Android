package com.aircast.photobag.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aircast.photobag.R;
import com.aircast.photobag.model.PBHistoryPhotoModel;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;

/**
 * Share password to fb. can select photo and edit text here.
 * */
public class PBFacebookPreviewActivity extends PBAbsActionBarActivity {
		    
	// View on layout
    private FButton btnPostToFacebook;    
    private FButton btnBackToConfirm;    
    private FButton btnChangeImage;    
    private EditText editPostText;    
    private ImageView imgFacebookPreview;    
    private LinearLayout mLvWaitingLayout;
    
    private static final int MSG_SEND_START = 0;    
    private static final int MSG_SEND_COMPLETE = 1;    
    private static final int MSG_NETWORK_ERROR = 2;    
    private static final int MSG_AUTHORIZE_ERROR = 3;
    
    // facebook Obj
   // private Facebook facebook = new Facebook(PBConstant.FACEBOOK_APP_ID);
    
    //private AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
    
    // photo var
    private String mCollectionPWD;    
    private String mCollectionID;    
    private ArrayList<PBHistoryPhotoModel> mPhotos;    
    private int mPhotoIndex = 0;    
    private String pathPhotoPreview;    
    private String urlPhotoPreview;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pb_layout_facebook_preview);        
        
     /*   btnPostToFacebook = (FButton) findViewById(R.id.btn_post_to_fb);      
        
        btnBackToConfirm = (FButton) findViewById(R.id.btn_cancel_post);
        
        btnChangeImage = (FButton) findViewById(R.id.btn_change_image);
        
        editPostText = (EditText) findViewById(R.id.et_facebook_post_text);
        
        imgFacebookPreview = (ImageView) findViewById(R.id.img_facebook_preview);
        
        mLvWaitingLayout = (LinearLayout) findViewById(R.id.ll_facebook_loading);
        
        ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(headerBar, getString(R.string.pb_facebook_title));

		if (getIntent() == null) {
			finish();
			return;
		}
		Bundle extras = getIntent().getBundleExtra("data");
		if (extras == null) {
			finish();
			return;
		}
		
		mCollectionPWD = extras.getString(PBConstant.COLLECTION_PASSWORD);
		mCollectionID = extras.getString(PBConstant.COLLECTION_ID);
		
		mPhotos = PBDatabaseManager.getInstance(this).getPhotos(mCollectionID);

		setNextPhoto();
		
		editPostText.setText(getString(R.string.pb_facebook_text_pre) 
							+ mCollectionPWD.toString()
							+ getString(R.string.pb_facebook_text_post));
		
        
         * Get existing access_token if any
         
        String access_token = PBPreferenceUtils.getStringPref(
        		PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, 
				PBConstant.PREF_FACEBOOK_ACCESS_TOKEN, null);
        long expires = PBPreferenceUtils.getLongPref(
        		PBApplication.getBaseApplicationContext(),
				PBConstant.PREF_NAME, 
				PBConstant.PREF_FACEBOOK_ACCESS_EXPIRES, 0);
        if(access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }
        
		
        btnPostToFacebook.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				if (!PBApplication.hasNetworkConnection()) {
					Toast.makeText(getBaseContext(), 
 					       getString(R.string.pb_network_error_content), 
 					       Toast.LENGTH_SHORT).show();
				}
				else {
					//Atik need to check whether facebook package installed or not
					ApplicationInfo info = null;
					try{
					     info = getPackageManager().
					            getApplicationInfo(PBConstant.FACEBOOK_APP_PACKAGE_NAME, 0 );
					    //return true;
					} catch( PackageManager.NameNotFoundException e ){
					    //return false;
					}
					if(info != null) {
						authorizeFacebook();	
					} else {
						Toast.makeText(getBaseContext(), 
		 					       getString(R.string.pb_facebook_app_not_exists), 
		 					       Toast.LENGTH_SHORT).show();
					}
					
				}
			}
		});
        
        btnBackToConfirm.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				cancelPost();			
			}
		});
        
        btnChangeImage.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				setNextPhoto();				
			}
		});*/
    }

	@Override
	protected void handleHomeActionListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		// TODO Auto-generated method stub
		
	}
    
    /*private Handler processHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what) {
    		case MSG_SEND_START:
    			
    			mLvWaitingLayout.setVisibility(View.VISIBLE);
    			
    			btnPostToFacebook.setClickable(false);
    			btnChangeImage.setClickable(false);
    			editPostText.setEnabled(false);

    			postImage();
    			break;
    		case MSG_SEND_COMPLETE:
    			
    			mLvWaitingLayout.setVisibility(View.GONE);
    	        
    	        Toast.makeText(getBaseContext(), 
    	        			   getString(R.string.pb_facebook_over), 
    	        			   Toast.LENGTH_SHORT).show();
    	        
    	        btnPostToFacebook.setClickable(true);
    	        btnChangeImage.setClickable(true);
    			editPostText.setEnabled(true);
    			
    			break;
    			
    		case MSG_NETWORK_ERROR:
    			
    			mLvWaitingLayout.setVisibility(View.GONE);
    			
    			Toast.makeText(getBaseContext(), 
    					       getString(R.string.pb_facebook_net_error), 
    					       Toast.LENGTH_SHORT).show();
    			
    			btnPostToFacebook.setClickable(true);
    			btnChangeImage.setClickable(true);
    			editPostText.setEnabled(true);
    			
    			break;
    			
    		case MSG_AUTHORIZE_ERROR:
    			
    			//mLvWaitingLayout.setVisibility(View.GONE);
    			
            	PBPreferenceUtils.saveStringPref(
            			PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME, 
						PBConstant.PREF_FACEBOOK_ACCESS_TOKEN, 
						null);            	
            	PBPreferenceUtils.saveLongPref(
            			PBApplication.getBaseApplicationContext(),
						PBConstant.PREF_NAME, 
						PBConstant.PREF_FACEBOOK_ACCESS_EXPIRES, 
						0);
            	
            	facebook.setAccessExpires(0);
            	facebook.setAccessToken(null);
    	        
            	//post again instead of show error msg
            	authorizeFacebook();
            	
    	        //Toast.makeText(getBaseContext(), 
    	        //		getString(R.string.pb_facebook_auth_error),
    	        //		       Toast.LENGTH_SHORT).show();
    	        
    			break;
    		default:
    			break;
    		}
    	};
    };*/
    
    /*private void authorizeFacebook() {
    	if (!facebook.isSessionValid()) {
    		
        	new Thread(){        	
            	@Override
            	public void run(){
    	
			        facebook.authorize(PBFacebookPreviewActivity.this, 
			        		new String[] {"email", "publish_stream", "user_photos"}, 
			        		new DialogListener() {
					            @Override
					            public void onComplete(Bundle values) {
					            	PBPreferenceUtils.saveStringPref(
					            			PBApplication.getBaseApplicationContext(),
											PBConstant.PREF_NAME, 
											PBConstant.PREF_FACEBOOK_ACCESS_TOKEN, 
											facebook.getAccessToken());            	
					            	PBPreferenceUtils.saveLongPref(
					            			PBApplication.getBaseApplicationContext(),
											PBConstant.PREF_NAME, 
											PBConstant.PREF_FACEBOOK_ACCESS_EXPIRES, 
											facebook.getAccessExpires());
					            	
					            	processHandler.sendEmptyMessage(MSG_SEND_START);
					            }
					
					            @Override
					            public void onFacebookError(FacebookError error) {}
					
					            @Override
					            public void onError(DialogError e) {}
					
					            @Override
					            public void onCancel() {}
					        });
	        
            	}
        	}.start();
    	}
    	else {
    		processHandler.sendEmptyMessage(MSG_SEND_START);
    	}
    }*/
    
    /*private void postImage() {
    	new Thread(){        	
        	@Override
        	public void run(){
        		
        		String response = null;
    	
		        try {
		        	String method = "POST";
		            Bundle params = new Bundle();
		            
		             * this will revoke 'publish_stream' permission
		             * Note: If you don't specify a permission then this will de-authorize the application completely.
		             
		            params.putString("message", editPostText.getText().toString());
		            
		            // post from local
		            FileInputStream fis = new FileInputStream(pathPhotoPreview);	        	            
		            byte[] buffer = readStream(fis);
		            
			        params.putByteArray("picture", buffer);
			        response = facebook.request(PBConstant.FACEBOOK_PHOTOBAG_ALBUM, params, method);	        
			        
		            // post from url 
		            // faster but photobag's photo link cannot be shown as photo
		            
		            params.putString("link", urlPhotoPreview);
			        params.putString("caption", "photobag");
		    		
			        facebook.request("me/feed", params, "POST");            
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
		        
		        if (response.contains("error")) {
		        	System.out.println("Atik response error facebook:"+response.contains("error"));
		        	if (response.contains("OAuthException")) { 

		        		processHandler.sendEmptyMessage(MSG_AUTHORIZE_ERROR);
		        		
		        	}
		        	else {
		        		processHandler.sendEmptyMessage(MSG_NETWORK_ERROR);
		        	}
		        }
		        else {
		        	processHandler.sendEmptyMessage(MSG_SEND_COMPLETE);
		        }
        
        	}
    	}.start();
    }*/
    
    /*private static byte[] readStream(InputStream inStream) throws Exception {
    	
    	byte[] buffer = new byte[1024];
    	int len = -1;
    	ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    	
    	while ((len = inStream.read(buffer)) != -1) {
    		outStream.write(buffer, 0, len);
    	}
    	
    	byte[] data = outStream.toByteArray();
    	
    	outStream.close();
    	inStream.close();
    	return data;
    } 
    
    private void cancelPost() {
    	finish();
    }*/
    
   /* private void setNextPhoto() {    	
    	try {
    		mPhotoIndex = (++mPhotoIndex >= mPhotos.size()) ? 0 : mPhotoIndex;
        	
        	PBHistoryPhotoModel model = mPhotos.get(mPhotoIndex);
        	
        	pathPhotoPreview = PBGeneralUtils.getPathFromCacheFolder(model.getPhoto());
        	
        	if (!com.kayac.photobag.utils.PBBitmapUtils
                    .isPhotoValid(pathPhotoPreview))
        	{
        		// video cant be loaded directly by imageview
        		// so use thumb files instead when is video
        		pathPhotoPreview = PBGeneralUtils.getPathFromCacheFolder(model.getThumb());
        	}
        	
        	//add TODO when both photo and thumb failed
        	
        	urlPhotoPreview = model.getPhoto();
        	
        	Bitmap imgBitmap;
    		
    		FileInputStream fis = new FileInputStream(pathPhotoPreview);
			imgBitmap = BitmapFactory.decodeStream(fis);
			
			imgFacebookPreview.setImageBitmap(imgBitmap);
			
		} catch (FileNotFoundException e) {
    		e.printStackTrace();
		}  	
    	
    	return;
    }*/
    
    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }

	@Override
	protected void handleHomeActionListener() {
		finish();			
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		return;	
	}*/
  

}
