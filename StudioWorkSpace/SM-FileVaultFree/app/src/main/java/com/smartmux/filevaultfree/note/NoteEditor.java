package com.smartmux.filevaultfree.note;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.smartmux.filevaultfree.AppMainActivity;
import com.smartmux.filevaultfree.LoginWindowActivity;
import com.smartmux.filevaultfree.R;
import com.smartmux.filevaultfree.utils.AppActionBar;
import com.smartmux.filevaultfree.utils.AppExtra;
import com.smartmux.filevaultfree.utils.AppToast;
import com.smartmux.filevaultfree.utils.FileManager;
import com.smartmux.filevaultfree.utils.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;

@SuppressLint({ "SimpleDateFormat", "NewApi" })
public class NoteEditor extends AppMainActivity{
	
	public static int numTitle = 1;	
	public static String curDate = "";
	public static String curText = "";	
    private EditText mTitleText;
    private AdvancedEditText mBodyText;
    private TextView mDateText;
    
	private String folderName;
	private String subFolderName;
	private String noteMode;
	private String noteFileName = null;
	private String justName = null;
	
	private FileManager	fileManager;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.note_editor);
        
		fileManager		= new FileManager();
		fileManager.setBackCode(getApplicationContext());
		AppActionBar.changeActionBarFont(getApplicationContext(),NoteEditor.this);
		AppActionBar.updateAppActionBar(getActionBar(), this,true);
		
        Bundle bundle = getIntent().getExtras();
		
		folderName 		= bundle.getString(AppExtra.FOLDER_NAME);
		subFolderName 	= bundle.getString(AppExtra.SUB_FOLDER_NAME);
		noteMode		= bundle.getString(AppExtra.NOTE_MODE);
		noteFileName	= bundle.getString(AppExtra.NOTE_FILENAME);
		
		
        getActionBar().setTitle("New Text Memo");
        

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (AdvancedEditText) findViewById(R.id.body);
        mDateText = (TextView) findViewById(R.id.notelist_date);
        
        mTitleText.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
            	String str = mTitleText.getText().toString();
            	if(str.length() > 0){
            		getActionBar().setTitle(str + ".txt");
            	}else{
            		getActionBar().setTitle("");
            	}
            	if(str.length() > 0){
            		getActionBar().setTitle(str + ".txt");
            	}
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        }); 
        
        mDateText.setText(FileUtil.getJustCurrentDateTime());
        
        if(noteFileName != null){
        	int pos = noteFileName.lastIndexOf(".");
        	justName = pos > 0 ? noteFileName.substring(0, pos) : noteFileName;
        	
        	mTitleText.setText(justName);
        	mDateText.setText(fileManager.getNoteDateTime(folderName, subFolderName, noteFileName));
        	try {
				mBodyText.setText(fileManager.getNoteContent(folderName, subFolderName, noteFileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        	getActionBar().setTitle(noteFileName);
        }
    }
	
	  
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.noteedit_menu, menu);
			return true;		
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		    case R.id.menu_save:
		    	String title = mTitleText.getText().toString();
			    String body  = mBodyText.getText().toString();
		    	if(title.equals(null) || "".equals(title) || body.equals(null) || "".equals(body)){
			         AppToast.show(getApplicationContext(), "You must write something both in title and body!");
			      }
			      else{
			    	  fileManager.setBackCode(getApplicationContext());
				    	//this.finish();
				    	if (noteMode.equals(AppExtra.NOTE_MODE_EDIT)){
				    		if(!title.equals(justName)){
				    			String fileFullPath = AppExtra.APP_ROOT_FOLDER + "/"+ noteFileName;
				    			File file = new File(fileFullPath);
				    			file.renameTo(new File(AppExtra.APP_ROOT_FOLDER + "/",title+".txt"));
				    		}
				    	}
				    	saveNote(); 
				    	
			      }
		    	return true;
		    default:
		    	return super.onOptionsItemSelected(item);
		    }
		}
	    
	    private void saveNote() {
	        String title = mTitleText.getText().toString();
	        String body  = mBodyText.getText().toString();
	       fileManager.saveNote(getApplicationContext(), folderName, title, body);
	        AppToast.show(getApplicationContext(), "Note Saved");
	        finish();
	      
	    }
	    @Override
		protected void onUserLeaveHint() {
	    	fileManager.setHomeCode(getApplicationContext());
			super.onUserLeaveHint();
			}
	    
	    @Override
		protected void onResume() {
			super.onResume();
			int event_code = fileManager.getReturnCode(getApplicationContext());
			if(event_code == AppExtra.HOME_CODE ){
				Intent i = new Intent(NoteEditor.this, LoginWindowActivity.class);
		        startActivity(i);
			}
	    }
		
		@Override
		 public boolean onKeyDown(int keyCode, KeyEvent event) {
		        if (keyCode == KeyEvent.KEYCODE_BACK){   
		        	fileManager.setBackCode(getApplicationContext());
				    finish();
				    overridePendingTransition (R.anim.push_right_out, R.anim.push_right_in);
		            return true;
		        }
		        return super.onKeyDown(keyCode, event);
		 }
}
