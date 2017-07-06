//package com.smartmux.smtaskmonitor;
//
//
//
//import java.util.ArrayList;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.widget.ListView;
//
// public class ProcessListActivity extends Activity{
//	private ArrayList<ProcessModel> processModelList;
//	private ListView processList;
//	private ProcessModel processModel1 ;
//	private ProcessModel processModel2 ;
//	private ProcessModel processModel3 ;
//	private ProcessModel processModel4 ;
//	
//	 @Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_process_list);
//		processModelList = new ArrayList<ProcessModel>();
//		
//		processModel1=new ProcessModel("Face Book", "1472", "20%", "7373 MB", "fdsf");
//		processModel2=new ProcessModel("Face Book", "1472", "20%", "7373 MB", "fdsf");
//		processModel3=new ProcessModel("Face Book", "1472", "20%", "7373 MB", "fdsf");
//		processModel4=new ProcessModel("Face Book", "1472", "20%", "7373 MB", "fdsf");
//		
//		processModelList.add(processModel1);
//		processModelList.add(processModel2);
//		processModelList.add(processModel3);
//		processModelList.add(processModel4);
//		
//		ProcessAdapter adapter = new ProcessAdapter(this, processModelList);
//		processList = (ListView) findViewById(R.id.processList);
//		processList.setAdapter(adapter);
//	}
// }