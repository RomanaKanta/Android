package com.smartmux.musiclist.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.smartmux.musiclist.R;
import com.smartmux.musiclist.adapter.AllSongsListAdapter;
import com.smartmux.musiclist.models.SongDetail;
import com.smartmux.musiclist.phonemidea.PhoneMediaControl;

import java.util.ArrayList;

public class FragmentPlayListSongs extends Fragment{

    private static Context context;
    View rootview;
    TextView playlist_title;
    ListView playlist_song;
    private long playlist_id;
    private AllSongsListAdapter mAllSongsListAdapter;
    private ArrayList<SongDetail> songList = new ArrayList<SongDetail>();

    public static FragmentPlayListSongs newInstance(int position, Context mContext) {
        FragmentPlayListSongs f = new FragmentPlayListSongs();
        context = mContext;
        return f;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.activity_playlist_song, null);


            playlist_id = getArguments().getLong("PLAYLIST_ID");

            rootview.findViewById(R.id.back_to_palylist_image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    FragmentPlayList fragmentsettings = new FragmentPlayList();
                    fragmentTransaction.setCustomAnimations(R.anim.push_right_out,
                            R.anim.push_right_in);
                    fragmentTransaction.replace(R.id.fragment, fragmentsettings);
                    fragmentTransaction.commit();

                }
            });

            playlist_title = (TextView) rootview.findViewById(R.id.playlist_title);

            playlist_title.setText(getArguments().getString("PLAYLIST_TITLE"));
            playlist_song = (ListView) rootview.findViewById(R.id.playlist_song);

        return rootview;
        }

    @Override
    public void onResume() {
        super.onResume();
        loadAllSongs();
    }

    private void loadAllSongs() {
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControl.PhoneMediaControlINterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
                songList = songsList_;
                Log.e("songList",""+ songList);
                mAllSongsListAdapter = new AllSongsListAdapter(getActivity(),songList,playlist_id);
                playlist_song.setAdapter(mAllSongsListAdapter);
            }
        });
        mPhoneMediaControl.loadMusicList(getActivity(), -1, PhoneMediaControl.SonLoadFor.Playlist, "",playlist_id);
    }

}
