package com.smartmux.musiclist.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.smartmux.musiclist.R;
import com.smartmux.musiclist.adapter.AllSongsListAdapter;
import com.smartmux.musiclist.models.SongDetail;

import java.io.File;
import java.util.ArrayList;


public class FragmentFolder extends Fragment {

    ListView folderList;
    private ArrayList<String> mfolderArray = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    AllSongsListAdapter mAllSongsListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_allsongs, null);

        if(mfolderArray.size()>0){
            mfolderArray.clear();
        }

        File dir = Environment.getExternalStorageDirectory();
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();

            if (files != null) {
                getFolder(files);
            }
        }

        setFolderList(v);

        return v;
    }

    private void setFolderList(View v){

        folderList = (ListView)v.findViewById(R.id.recycler_allSongs);
        adapter = new ArrayAdapter<String>(
                getActivity(), R.layout.font_row, mfolderArray);

        folderList.setAdapter(adapter);

        folderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                getMusicFile(mfolderArray.get(position));

            }
        });

    }

    private void getMusicFile(String path){

//        File root  = new File(path);

        init(path);



//        if(root.isDirectory()) {
//            mfolderArray.clear();
//            File[] item = root.listFiles();
//            for (File rootItem : item) {
//                if (rootItem.getName().endsWith(".mp3")) {
//                    mfolderArray.add("" + rootItem.getName());
//                }
//            }
//
//            adapter = new ArrayAdapter<String>(
//                    getActivity(), R.layout.font_row, mfolderArray);
//
//            folderList.setAdapter(adapter);
//        }else{
//            Toast.makeText(getActivity(),"song",Toast.LENGTH_SHORT).show();
//            mAllSongsListAdapter = new AllSongsListAdapter(getActivity(), songList);
//            folderList.setAdapter(mAllSongsListAdapter);
//        }


    }

    Cursor cursor = null;
    ArrayList<SongDetail> songsList = new ArrayList<>();
    final String[] projectionSongs = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM_ID};

    private void init(String filepath){

       Uri uri = Uri.parse(filepath);

        Log.e("URI" , "" + uri);
//        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
//                sortOrder = MediaStore.Audio.Media.DATE_ADDED + " desc";
        String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        cursor = (getActivity()).getContentResolver().query(uri, projectionSongs, null, null, null);
//                cursor = ((Activity) context).getContentResolver().query(uri, projectionSongs, null, null, sortOrder);
        songsList = getSongsFromCursor(cursor);

        mAllSongsListAdapter = new AllSongsListAdapter(getActivity(), songsList,-1);
        folderList.setAdapter(mAllSongsListAdapter);

    }


    private void getFolder(File[] rootFile){

        for (File rootItem : rootFile) {
            if (rootItem.exists()){
                if(rootItem.isDirectory()) {

                File[] item = rootItem.listFiles();
                    getFolder(item);

            }else{
                    if (rootItem.getName().endsWith(".mp3")) {
                        Log.e("SONG", "" + rootItem.getName());
                        Log.e("FOLDER", "" + rootItem.getParent());
                        mfolderArray.add(""+rootItem.getParent());
                        break;

                    }
                }
            }
        }

    }

    private ArrayList<SongDetail> getSongsFromCursor(Cursor cursor) {
        ArrayList<SongDetail> generassongsList = new ArrayList<SongDetail>();
        try {
            if (cursor != null && cursor.getCount() >= 1) {
                int _id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int album_id = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int display_name = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

                while (cursor.moveToNext()) {

                    int ID = cursor.getInt(_id);
                    String ARTIST = cursor.getString(artist);
                    String TITLE = cursor.getString(title);
                    String DISPLAY_NAME = cursor.getString(display_name);
                    String DURATION = cursor.getString(duration);
                    String Path = cursor.getString(data);

                    SongDetail mSongDetail = new SongDetail(ID, album_id, ARTIST, TITLE, Path, DISPLAY_NAME, DURATION);
                    generassongsList.add(mSongDetail);
                }
            }
            closeCrs();
        } catch (Exception e) {
            closeCrs();
            e.printStackTrace();
        }
        return generassongsList;
    }

    private void closeCrs() {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                Log.e("tmessages", e.toString());
            }
        }
    }

}
