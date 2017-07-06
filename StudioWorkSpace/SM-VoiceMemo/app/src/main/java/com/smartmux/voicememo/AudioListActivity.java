package com.smartmux.voicememo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.Manifest;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.smartmux.voicememo.adapter.CommonListAdapter;
import com.smartmux.voicememo.modelclass.CommonItemRow;
import com.smartmux.voicememo.utils.AppActionBar;
import com.smartmux.voicememo.utils.AppExtra;
import com.smartmux.voicememo.utils.FileManager;

import java.util.ArrayList;

public class AudioListActivity extends AppMainActivity{

	private FileManager	fileManager;
    public boolean allow = false;
	
	private ArrayList<CommonItemRow> listItems = null;
	private CommonListAdapter 		 listAdapter;
	private ListView 				 listView;

    static final String ITEM_SKU = "com.smartmux.voicememo.upgrade";
//static final String ITEM_SKU = "android.test.purchased";
	private boolean isPurchased = false;
    public BillingProcessor billingProcessor;
    private static final String LICENSE_KEY = null;
    public boolean readyToPurchase = false;

	   protected void onResume() {
		super.onResume();
		
		int event_code = fileManager.getReturnCode(getApplicationContext());
		if(event_code != AppExtra.BACK_CODE){
			Intent i = new Intent(AudioListActivity.this, LoginWindowActivity.class);
	        startActivity(i);
		}

           if(allow) {

               if (listAdapter != null) {
                   listAdapter = null;
               }

               listItems = fileManager.getAllAudios(getApplicationContext());
               if (listItems != null) {
                   listAdapter = new CommonListAdapter(this, R.layout.common_item_row, listItems);
                   listView.setAdapter(listAdapter);
               }
           }
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        fileManager	= new FileManager();
        initBillingProcessor();

		setContentView(R.layout.audio_list_activity);
		  if (getIntent().getBooleanExtra("EXIT", false)) {
			    finish();
			}
		  else{

              PermissionListener permissionlistener = new PermissionListener() {
                  @Override
                  public void onPermissionGranted() {
//                      Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                      allow = true;
                      fileManager.setBackCode(getApplicationContext());

                      AppActionBar.updateAppActionBar(getActionBar(), AudioListActivity.this, false);

                      listView = (ListView) findViewById(R.id.listviewAudioList);

                      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                          @Override
                          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                              String fileName = listItems.get(position).getTitle();
                              Intent i = new Intent(AudioListActivity.this, AudioRecoderActivity.class);
                              i.putExtra(AppExtra.AUDIO_MODE, AppExtra.AUDIO_MODE_PLAY);
                              i.putExtra(AppExtra.AUDIO_FILENAME, fileName);
                              startActivity(i);
                              overridePendingTransition (R.anim.goto_right_next, R.anim.close_main);

                          }
                      });
                      listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                          public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int position, long id) {
                              String fileName = listItems.get(position).getTitle();
                              showFileOptionDialog(fileName,position);
                              return true;
                          }
                      });

                      Intent i = new Intent(AudioListActivity.this, LoginWindowActivity.class);
                      startActivity(i);

                  }

                  @Override
                  public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//                      Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                  }


              };

              new TedPermission(this)
                      .setPermissionListener(permissionlistener)
                              //	.setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                      .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                              , Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE)
                      .check();
			}
	}

	private void showFileOptionDialog(final String fileName,final int index){
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle(fileName);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item);
        arrayAdapter.add(AppExtra.DELETE_AUDIO);
        arrayAdapter.add(AppExtra.RENAME_AUDIO);
        arrayAdapter.add(AppExtra.SHARE_AUDIO);
        builderSingle.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strItemName = arrayAdapter.getItem(which);
                        if(strItemName.equals(AppExtra.DELETE_AUDIO)){
                        	fileManager.deleteAnyFile(getApplicationContext(),fileName);
                        	listItems.remove(index);
                        	listAdapter.notifyDataSetChanged();
                        }
                        if(strItemName.equals(AppExtra.RENAME_AUDIO)){
                        	fileManager.renameAnyFile(AudioListActivity.this,fileName);
                        	listAdapter.notifyDataSetChanged();
                        }
                        if(strItemName.equals(AppExtra.SHARE_AUDIO)){
                        	fileManager.shareAnyFile(AudioListActivity.this,fileName);
                        }
                    }
                });
        builderSingle.show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.audio_list_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		fileManager.setDefaultCode(getApplicationContext());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_create_audio:{
			isPurchased = fileManager.getPaidStatus(getApplicationContext());
			if(isPurchased){
				Intent i = new Intent(AudioListActivity.this, AudioRecoderActivity.class);
				i.putExtra(AppExtra.AUDIO_MODE, AppExtra.AUDIO_MODE_RECORD);
		        startActivity(i);
		        overridePendingTransition (R.anim.goto_right_next, R.anim.close_main);
			}else{
				int totalRow = 0;
				if(listItems != null) totalRow = listItems.size();

		        	if(totalRow >= AppExtra.TOTAL_VOICE_LIMIT){
		        		AlertDialog.Builder alertDialog = new AlertDialog.Builder(AudioListActivity.this);
	    				alertDialog.setTitle("Purchase Required!");
	    				alertDialog.setMessage("Please Upgrade Full Version to create unlimited number of Voice Memo");
	    				  alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
	    			            public void onClick(DialogInterface dialog,int which) {
                                    billingProcessor.purchase(AudioListActivity.this,
                                            ITEM_SKU);
	    			               dialog.cancel();
	    			            }
	    			        });
	    				  
	    			      alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
	    			            public void onClick(DialogInterface dialog, int which) {
	    			            dialog.cancel();
	    			            }
	    			        });
	    			        alertDialog.show();
		        	}else{
						Intent i = new Intent(AudioListActivity.this, AudioRecoderActivity.class);
						i.putExtra(AppExtra.AUDIO_MODE, AppExtra.AUDIO_MODE_RECORD);
				        startActivity(i);
				        overridePendingTransition (R.anim.goto_right_next, R.anim.close_main);
		        	}
			}
			
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onBackPressed() {
	    finish();
	    overridePendingTransition (R.anim.open_main, R.anim.close_next);   
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent
	             data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode,
                data))
            ;

	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();

        if (billingProcessor != null)
            billingProcessor.release();

	}

    void initBillingProcessor(){

        billingProcessor = new BillingProcessor(this, LICENSE_KEY,
                new BillingProcessor.IBillingHandler() {
                    @Override
                    public void onBillingError(int errorCode, Throwable error) {
                        // showToast("onBillingError: "
                        // + Integer.toString(errorCode));

                    }

                    @Override
                    public void onBillingInitialized() {
                        // showToast("onBillingInitialized");
                        readyToPurchase = true;
                        billingProcessor.loadOwnedPurchasesFromGoogle();

                    }

                    @Override
                    public void onPurchaseHistoryRestored() {

                        for(String sku : billingProcessor.listOwnedProducts()){

                            Log.d("item ", sku);
                        }

                        if(billingProcessor.isPurchased(ITEM_SKU)){


                            isPurchased = fileManager.getPaidStatus(getApplicationContext());



                        }

                    }

                    @Override
                    public void onProductPurchased(String productId,
                                                   TransactionDetails details) {



                        fileManager.setPaidStatus(AudioListActivity.this);

                    }
                });


        isPurchased = fileManager.getPaidStatus(AudioListActivity.this);

    }


}
