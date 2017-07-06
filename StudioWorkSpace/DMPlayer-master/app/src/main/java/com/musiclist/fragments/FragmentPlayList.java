package com.musiclist.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dmplayer.R;
import com.musiclist.adapter.PlaylistAdapter;
import com.musiclist.dbhandler.Playlist;

import java.util.ArrayList;

/**
 * Created by tanvir-android on 5/16/16.
 */
public class FragmentPlayList extends Fragment {

    private static final String TAG = "FragmentPlayList";
    private static Context context;
    private ListView recycler_playlist;
    View rootview;
    PlaylistAdapter mAdapter;

    public static FragmentPlayList newInstance(int position, Context mContext) {
        FragmentPlayList f = new FragmentPlayList();
        context = mContext;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         rootview = inflater.inflate(R.layout.fragment_playlist, null);

        rootview.findViewById(R.id.addPlaylist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCreateNewPlaylistDialog();
            }
        });

        new GetPlayListTask().execute();
        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mAdapter!=null) {
        mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    EditText newNameEditText;
    private void showCreateNewPlaylistDialog() {
// get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.dialog_screen_rename, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        TextView headerTitle = (TextView) promptView.findViewById(R.id.dialog_header);
        headerTitle.setText("Create PlayList");

        newNameEditText = (EditText) promptView.findViewById(R.id.newName);
//        OnFocusChangeListener ofcListener = new MyFocusChangeListener();
//        newNameEditText.setOnFocusChangeListener(ofcListener);

        newNameEditText.setHint("Playlist Name");

        // setup a dialog window
        alertDialogBuilder.setCancelable(true);


        // Add the buttons
        alertDialogBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        alertDialogBuilder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {

//                        String folderName = newNameEditText
//                                .getText().toString().trim();

                        Playlist.createPlaylist(
                                getActivity().getContentResolver(), newNameEditText.getText()
                                        .toString());

                        new GetPlayListTask().execute();

                        dialog.dismiss();
                    }
                });

        // create an alert dialog
        alertDialogBuilder.create().show();




    }

    public class GetPlayListTask extends AsyncTask<Void, String,  ArrayList<Playlist>> {

        String select = null;
        String[] ALBUM_SUMMARY_PROJECTION = { "*" };
        Uri tempPlaylistURI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

        @Override
        protected  ArrayList<Playlist> doInBackground(Void... params) {
            ArrayList<Playlist>  playlist = Playlist.getAllPlaylists(getActivity().getContentResolver());
            return playlist;
        }

        @Override
        protected void onPostExecute( final ArrayList<Playlist> playlist) {
            super.onPostExecute(playlist);

             mAdapter = new PlaylistAdapter(getActivity(), playlist);

            ListView listView = (ListView) rootview.findViewById(R.id.recycler_playlist);
            listView.setAdapter(mAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {

                    Bundle bundle=new Bundle();
                    bundle.putLong("PLAYLIST_ID", playlist.get(position).getId());
                    bundle.putString("PLAYLIST_TITLE",playlist.get(position).getName());

                    FragmentPlayListSongs fragmentPlayListSongs = new FragmentPlayListSongs();
                    fragmentPlayListSongs.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.push_left_in,
                            R.anim.push_left_out);
                    fragmentTransaction.replace(R.id.fragment, fragmentPlayListSongs);
                    fragmentTransaction.commit();

                }

            });
        }
    }
}
