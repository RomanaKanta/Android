package com.smartmux.musiclist.adapter;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.smartmux.musiclist.R;
import com.smartmux.musiclist.activities.DMPlayerBaseActivity;
import com.smartmux.musiclist.dbhandler.Playlist;
import com.smartmux.musiclist.manager.MediaController;
import com.smartmux.musiclist.manager.MusicPreferance;
import com.smartmux.musiclist.models.SongDetail;
import com.smartmux.musiclist.phonemidea.DMPlayerUtility;
import com.smartmux.musiclist.phonemidea.PhoneMediaControl;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by tanvir-android on 5/12/16.
 */
public class AllSongsListAdapter extends BaseAdapter {
    private Context context = null;
    private LayoutInflater layoutInflater;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ArrayList<SongDetail> songList = new ArrayList<SongDetail>();
    long playlistID;

    public AllSongsListAdapter(Context mContext, ArrayList<SongDetail> songList, long playlistID) {
        this.context = mContext;
        this.songList = songList;
        this.playlistID = playlistID;
        this.layoutInflater = LayoutInflater.from(mContext);
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.inflate_allsongsitem, null);
            mViewHolder.song_row = (LinearLayout) convertView.findViewById(R.id.inflate_allsong_row);
            mViewHolder.textViewSongName = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongname);
            mViewHolder.textViewSongArtisNameAndDuration = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongArtisName_duration);
            mViewHolder.imageSongThm = (ImageView) convertView.findViewById(R.id.inflate_allsong_imgSongThumb);
            mViewHolder.imagemore = (ImageView) convertView.findViewById(R.id.img_moreicon);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        SongDetail mDetail = songList.get(position);

        String audioDuration = "";
        try {
            audioDuration = DMPlayerUtility.getAudioDuration(Long.parseLong(mDetail.getDuration()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        mViewHolder.textViewSongArtisNameAndDuration.setText((audioDuration.isEmpty() ? "" : audioDuration + " | ") + mDetail.getArtist());
        mViewHolder.textViewSongName.setText(mDetail.getTitle());
        String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
        imageLoader.displayImage(contentURI, mViewHolder.imageSongThm, options);

        if(MusicPreferance.showMusicDetail(context)){
            mViewHolder.textViewSongArtisNameAndDuration.setVisibility(View.VISIBLE);
        }else{
            mViewHolder.textViewSongArtisNameAndDuration.setVisibility(View.GONE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SongDetail mDetail = songList.get(position);
                ((DMPlayerBaseActivity)context).loadSongsDetails(mDetail);

                if (mDetail != null) {
                    if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
                        MediaController.getInstance().pauseAudio(mDetail);
                    } else {
                        MediaController.getInstance().setPlaylist(songList, mDetail, PhoneMediaControl.SonLoadFor.All.ordinal(), -1);
                    }
                }

            }
        });

        mViewHolder.imagemore.setColorFilter(Color.DKGRAY);
        if (Build.VERSION.SDK_INT > 15) {
            mViewHolder.imagemore.setImageAlpha(255);
        } else {
            mViewHolder.imagemore.setAlpha(255);
        }

        mViewHolder.imagemore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.list_item_option, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
//                                case R.id.playnext:
//                                    break;
//                                case R.id.addtoque:
//                                    break;
                                case R.id.addtoplaylist:

                                    playlistAdd(songList.get(position).getId());

                                    break;
                                case R.id.gotoartis:
                                    ((DMPlayerBaseActivity)context).setFragment(1,1);
                                    break;
                                case R.id.gotoalbum:
                                    ((DMPlayerBaseActivity)context).setFragment(1,0);
                                    break;
                                case R.id.delete:

                                    deleteSong(position);



                                    break;
                                default:
                                    break;
                            }

                            return true;
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        return convertView;
    }



    //delete from phone
    private int deleteMusic(long id){


    Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            id);
    return context.getContentResolver().delete(uri, null, null);

    }

    // delete from playlist
    public int deletePlaylistTracks(Context context, long playlistId,
                                    long audioId) {
        ContentResolver resolver = context.getContentResolver();
        int countDel = 0;
        try {
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri(
                    "external", playlistId);
            String where = MediaStore.Audio.Playlists.Members._ID + "=?" ; // my mistake was I used .AUDIO_ID here

            String audioId1 = Long.toString(audioId);
            String[] whereVal = { audioId1 };
            countDel=resolver.delete(uri, where,whereVal);
            Log.d("TAG", "tracks deleted=" + countDel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return countDel;

    }

    @Override
    public int getCount() {
        return (songList != null) ? songList.size() : 0;
    }

    class ViewHolder {
        TextView textViewSongName;
        ImageView imageSongThm, imagemore;
        TextView textViewSongArtisNameAndDuration;
        LinearLayout song_row;
    }

    public void playlistAdd(final int audioId){
// get prompts.xml view
        ListView playlist;
        AddSongtoPlaylist adapter;
        final ArrayList<Playlist> playlistarray;

        playlistarray = Playlist.getAllPlaylists(context.getContentResolver());


        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_add_to_playlist, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);


        // setup a dialog window
        alertDialogBuilder.setCancelable(true);

        adapter = new AddSongtoPlaylist(
                context, playlistarray,audioId
        );

        alertDialogBuilder .setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int pos) {
                Playlist.addToPlaylist(context.getContentResolver(), audioId, playlistarray.get(pos).getId());
                dialog.cancel();
            }
        });
        alertDialogBuilder.create().show();
    }

    private void deleteSong(final int position){

        {

// get prompts.xml view
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View promptView = layoutInflater.inflate(R.layout.dailog_screen_delete, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptView);
            TextView headerTitle = (TextView) promptView.findViewById(R.id.alert_text);
            headerTitle.setText("Want to delete?");
//            alertDialogBuilder.setMessage("Want to delete?");

            // setup a dialog window
            alertDialogBuilder.setCancelable(true);

            // Add the buttons
            alertDialogBuilder.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            alertDialogBuilder.setPositiveButton(R.string.done,
                    new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int id) {

                            int delete = -1;
                            if(playlistID==-1) {
                                delete = deleteMusic(songList.get(position).getId());
                            }else{

                                delete = deletePlaylistTracks(context, playlistID,
                                        songList.get(position).getId());
                            }

                            if(delete != -1){
                                songList.remove(position);
                            }
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });

            // create an alert dialog
            alertDialogBuilder.create().show();




        }
    }
}
