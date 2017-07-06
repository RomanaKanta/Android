//package com.smartmux.trackfriends.fragment;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
//import android.app.Fragment;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ListView;
//
//import com.smartmux.trackfriends.R;
//import com.smartmux.trackfriends.adapter.EventListAdapter;
//import com.smartmux.trackfriends.modelclass.EventModelClass;
//
//public class fragment_left_header_drawer extends Fragment{
//	
//	ListView eventList = null;
//	EventListAdapter mEventListAdapter = null;
//	EventModelClass mEventModelClass = null;
//	
//	public static fragment_left_header_drawer newInstance() {
//		fragment_left_header_drawer fragment = new fragment_left_header_drawer();
//		return fragment;
//	}
//	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.left_header_drawer_screen,
//				container, false);
//		
//		eventList  = (ListView)view.findViewById(R.id.eventList);
//		
//	
//		
//		// care center informations
//				String[] mEventName = { "BirthDay Party",
//					                   	 "Engagement",
//						                 "Football Game",
//						                 "Feoneral" };
//
//				
//
//				String[] mMemberNumber = { "5", 
//						                  "5", 
//						                  "12",
//					    	              "7" };
//
//			
//
//				ArrayList<String> eventnameList = new ArrayList<String>();
//				eventnameList.addAll(Arrays.asList(mEventName));
//
//
//				ArrayList<String> numberList = new ArrayList<String>();
//				numberList.addAll(Arrays.asList(mMemberNumber));
//
//				
//				mEventListAdapter = new EventListAdapter(getActivity(),eventnameList,numberList);
//				eventList.setAdapter(mEventListAdapter);
//
//
//		
//		return view;
//	}
//
//}
