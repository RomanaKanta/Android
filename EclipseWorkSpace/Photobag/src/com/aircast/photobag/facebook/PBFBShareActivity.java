package com.aircast.photobag.facebook;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aircast.photobag.R;
import com.aircast.photobag.activity.PBAbsActionBarActivity;
import com.aircast.photobag.application.PBConstant;
import com.aircast.photobag.database.PBDatabaseManager;
import com.aircast.photobag.model.PBHistoryPhotoModel;
import com.aircast.photobag.utils.PBGeneralUtils;
import com.aircast.photobag.widget.FButton;
import com.aircast.photobag.widget.actionbar.ActionBar;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;

public class PBFBShareActivity extends PBAbsActionBarActivity implements OnClickListener  {
    private static final String TAG = "PBFBShareActivity";
    private static final String FACEBOOK_PERMISSION_PUBLISH = "publish_actions";
    private static final String PENDING_ACTION_BUNDLE_KEY = "com.kayac.photobag:PendingAction";
    private enum PendingAction {
        NONE,
        POST_STATUS_UPDATE
    }
    
    private LoginButton loginButton;
    private EditText postText;
    private Button postBtn;
    private FButton btnChangeImage;    
    private UiLifecycleHelper uiHelper;
    private PendingAction pendingAction = PendingAction.NONE;
    private Session.StatusCallback callback = new Session.StatusCallback() {	
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
	private String facebookPostMessage = "";
    
    // photo var
    private String mCollectionPWD;    
    private String mCollectionID;    
    private ArrayList<PBHistoryPhotoModel> mPhotos;    
    private int mPhotoIndex = 0;    
    private String pathPhotoPreview;    
    private String urlPhotoPreview;
    private ImageView imgFacebookPreview;    

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        if ( savedInstanceState != null ) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }
        
        setContentView(R.layout.activity_fb_login_new);
        ActionBar headerBar = (ActionBar) findViewById(R.id.headerbar);
        setHeader(headerBar, getString(R.string.pb_facebook_title));

        loginButton = (LoginButton)findViewById(R.id.login_button);
        imgFacebookPreview = (ImageView) findViewById(R.id.img_facebook_preview1);
        btnChangeImage = (FButton) findViewById(R.id.btn_change_image);


        loginButton.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "loginButton onError():"+error.getMessage());
                if ( error != null ) {
                    if ( error instanceof FacebookOperationCanceledException ) {
                        Log.d(TAG, "FacebookOperationCanceledException");
                    } else if ( error instanceof FacebookAuthorizationException ) {
                        Log.d(TAG, "FacebookAuthorizationException");
                        new AlertDialog.Builder(PBFBShareActivity.this)
                        .setMessage("ログイン処理でエラーが発生しました。\n公式facebookアプリにログインしている場合には、一旦ログアウトした後再度ログインしてください。")
                        .setPositiveButton("Ok", null)
                        .show();
                    } else {
                        Log.d(TAG, "other Exception");
                    }
                }
            }
        });

        	Bundle extras = getIntent().getExtras();
        	Bundle extrasData = getIntent().getBundleExtra("data");
    		if (extrasData != null) {
    			mCollectionPWD = extrasData.getString(PBConstant.COLLECTION_PASSWORD);
    			mCollectionID = extrasData.getString(PBConstant.COLLECTION_ID);
    			mPhotos = PBDatabaseManager.getInstance(this).getPhotos(mCollectionID);
    		}
		
		//mSlug = savedInstanceState.getString(KEY_SLUG, null);
		
	    if(extras != null) {
	    	facebookPostMessage = extras.getString("FB_MESSAGE");
	    }
		
        
        postText = (EditText)findViewById(R.id.editText_post);
        postText.setText(facebookPostMessage);
        postBtn = (Button)findViewById(R.id.btn_post);
        postBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                performPublish(PendingAction.POST_STATUS_UPDATE);
            }
        });

        updateUI();
        setNextPhoto();
        
        btnChangeImage.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				setNextPhoto();				
			}
		});

    }
 
    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
        
        updateUI();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }
 
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
        
        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }
    
    /* セッション状態が変更になった時 */
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {	
    	
    	 /*if (state.isOpened()) {
    		 loginButton.setVisibility(View.GONE);
    	 }*/
        if ( pendingAction != PendingAction.NONE &&
               (exception instanceof FacebookOperationCanceledException
               || exception instanceof FacebookAuthorizationException) ) {
            Log.w(TAG, "error occured:" + exception.getMessage());
            new AlertDialog.Builder(PBFBShareActivity.this)
            .setMessage("Permission not granted")
            .setPositiveButton("Ok", null)
            .show();
            pendingAction = PendingAction.NONE;
            return;
        } else if ( state == SessionState.OPENED_TOKEN_UPDATED ) {
            handlePendingAction();
        }

        updateUI();
    }
    
    private void handlePendingAction() {	
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        if ( previouslyPendingAction == PendingAction.POST_STATUS_UPDATE ) {
           // postStatusUpdate();
        	publishStory();
        }
    }
    
    /* 投稿権限確認＆取得 */
    private void performPublish(PendingAction action) {	
        Session session = Session.getActiveSession();
        if ( session != null ) {
            pendingAction = action;
            if ( hasPublishPermission() ) {
                // We can do the action right away.
                handlePendingAction();
                return;
            } else if ( session.isOpened() ) {
                // We need to get new permissions, then complete the action when we get called back.
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, FACEBOOK_PERMISSION_PUBLISH));
                return;
            }
        }
    }
    
    /* 投稿権限有無返却 */
    private boolean hasPublishPermission() {	
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains(FACEBOOK_PERMISSION_PUBLISH);
    }
    
    /* UI更新 */
    private void updateUI() {
        Session session = Session.getActiveSession();
        if ( session != null && session.isOpened() ) {
            Log.d(TAG, "session.isOpened");
            postText.setVisibility(View.VISIBLE);
            postBtn.setVisibility(View.VISIBLE);
            //btnChangeImage.setVisibility(View.VISIBLE);
            btnChangeImage.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            imgFacebookPreview.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "session.isClosed");
            postText.setVisibility(View.INVISIBLE);
            postBtn.setVisibility(View.INVISIBLE);
            btnChangeImage.setVisibility(View.INVISIBLE);
            imgFacebookPreview.setVisibility(View.INVISIBLE);
        }
    }
    
    /* Facebookに投稿 */
    private void postStatusUpdate() {
        if ( hasPublishPermission() ) {
            final String postMessage = postText.getText().toString();
            Request request = Request.newStatusUpdateRequest(Session.getActiveSession(), postMessage, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    showPublishResult(response.getGraphObject(), response.getError());
                }
            });
            request.executeAsync();
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }

    }
    
    /* 投稿結果表示 */
    private void showPublishResult(GraphObject result, FacebookRequestError error) {
        if ( error == null ) {
            showToast("Facebookに投稿しました。");
            finish();
        } else {
            showToast("Facebookへの投稿に失敗しました。");
            Log.d(TAG, "error:"+ error.getErrorMessage());
        }
    }
    
    /* Toast表示 */
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

	@Override
	protected void handleHomeActionListener() {
		// TODO Auto-generated method stub
		finish();	
	}

	@Override
	protected void setLeftBtnsDetailHeader(ActionBar headerBar) {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	private void setNextPhoto() {    	
    	try {
    		mPhotoIndex = (++mPhotoIndex >= mPhotos.size()) ? 0 : mPhotoIndex;
        	
        	PBHistoryPhotoModel model = mPhotos.get(mPhotoIndex);
        	
        	pathPhotoPreview = PBGeneralUtils.getPathFromCacheFolder(model.getPhoto());
        	
        	if (!com.aircast.photobag.utils.PBBitmapUtils
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
    }
	
	private void publishStory()  {
		
		
		//Session session = Session.getActiveSession();
	    //Session session = Session.getActiveSession();
        if ( hasPublishPermission() ) {

	   //

	        // Check for publish permissions    
	       // List<String> permissions = session.getPermissions();
	        /*if (!isSubsetOf(PERMISSIONS, permissions)) {
	            pendingPublishReauthorization = true;
	            Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(this, PERMISSIONS);
	        session.requestNewPublishPermissions(newPermissionsRequest);
	            return;
	        }*/
	        
	        /*if (!isSubsetOf(PERMISSIONS, permissions)) {
            	pendingPublishReauthorization = true;
	            Session.NewPermissionsRequest newPermissionsRequest = new Session
	                    .NewPermissionsRequest(this, PERMISSIONS);
            	session.requestNewPublishPermissions(newPermissionsRequest);
            	return;
	        }*/
			//String message = "This is Customize Post this to my wall";
			final String postMessage = postText.getText().toString();
			// post from local
            FileInputStream fis;
            byte[] buffer = null;
			try {
				fis = new FileInputStream(pathPhotoPreview);
	            buffer = readStream(fis);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	

            
	        Bundle postParams = new Bundle();
	        /*postParams.putString("name", "Facebook SDK for Android");
	        postParams.putString("caption", "Build great social apps and get more installs.");
	        postParams.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	        postParams.putString("link", "https://developers.facebook.com/android");
	        postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");*/
	        postParams.putString("message", postMessage);
	       
	       // postParams.putString("name", "写真袋アプリ-Android");
	        postParams.putString("name", postMessage);
	        postParams.putString("caption", "Build great social apps and get more installs.");
	        postParams.putString("description", "You can share dialog Using Photobag application");
	        /*postParams.putString("link", "https://play.google.com/store/apps/details?id=com.kayac.photobag&hl=ja");*/
	        postParams.putString("link", urlPhotoPreview);
	        //postParams.putByteArray("picture", buffer);
	        System.out.println("Atik picture value: "+pathPhotoPreview);
	        //postParams.putString("method", "photos.upload");
            postParams.putByteArray("picture", buffer);
	        //postParams.putString("picture", pathPhotoPreview);
	        //postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

	        Request.Callback callback= new Request.Callback() {
	            public void onCompleted(Response response) {
	            	showPublishResult(response.getGraphObject(), response.getError());
	                /*JSONObject graphResponse = response
	                                           .getGraphObject()
	                                           .getInnerJSONObject();
	                String postId = null;
	                try {
	                    postId = graphResponse.getString("id");
	                } catch (JSONException e) {
	                    Log.i(TAG,
	                        "JSON error "+ e.getMessage());
	                }
	                FacebookRequestError error = response.getError();
	                if (error != null) {
	                    Toast.makeText(getApplicationContext(),
	                         error.getErrorMessage(),
	                         Toast.LENGTH_SHORT).show();
	                    } else {
	                        Toast.makeText(
	                             getApplicationContext(), 
	                             postId,
	                             Toast.LENGTH_LONG).show();
	                }*/
	            }
	        };

	        /*Request request = new Request(Session.getActiveSession(), "me/feed", postParams, 
	                              HttpMethod.POST, callback);*/
	        Request request = new Request(Session.getActiveSession(), "me/photos", postParams, 
                    HttpMethod.POST, callback);

	        RequestAsyncTask task = new RequestAsyncTask(request);
	        task.execute();
	    }  else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }


	}

	private byte[] readStream(FileInputStream inStream)   {
		byte[] buffer = new byte[1024];
    	int len = -1;
    	ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    	
    	try {
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	byte[] data = outStream.toByteArray();
    	
    	try {
			outStream.close();
	    	inStream.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return data;
	}
	
	
}