
package com.musiclist.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dmplayer.R;
import com.musiclist.dbhandler.Playlist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * CursorAdapter backed by MediaStore playlists.
 */
public class PlaylistAdapter extends BaseAdapter {


    private final LayoutInflater mInflater;
    ArrayList<Playlist>  playlist;
    private Context mContext;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    
     public PlaylistAdapter(Context context,ArrayList<Playlist>  playlist) {
        super();
        mInflater=LayoutInflater.from(context);
        this.playlist = playlist;
        this.mContext = context;

    }

    static class ViewHolder {
        TextView playListTitle;
        TextView playListNumberOfSongs;
        TextView playListTotalDuration;
        ImageView arrowImage;
    }

	@Override
	public int getCount() {
		return playlist.size();
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
		 ViewHolder holder ;
		 if (convertView == null) {
	            holder = new ViewHolder();
	            convertView = mInflater.inflate(R.layout.playlist_row,parent,false);

	            holder.playListTitle = (TextView) convertView.findViewById(R.id.textView_playlist_title);
	            holder.playListNumberOfSongs = (TextView) convertView.findViewById(R.id.textView_playlist_total_songs);
	            holder.playListTotalDuration = (TextView) convertView.findViewById(R.id.textView_playlist_total_duration);
	            holder.arrowImage = (ImageView) convertView.findViewById(R.id.imageView_arrow);
	            
	            
	            convertView.setTag(holder);
	        }else {

				holder = (ViewHolder) convertView.getTag();
			}
	        
	        holder.playListTitle.setText(playlist.get(position).getName());
	        
	        holder.playListNumberOfSongs.setText("Total Songs: "+playlist.get(position).getTotalSong());
	        
	        holder.playListTotalDuration.setText("Total Duration: "+String.format(
					"%d:%02d",
					TimeUnit.MILLISECONDS.toMinutes(playlist.get(position).getTotalDuration())
							- TimeUnit.HOURS
									.toMinutes(TimeUnit.MILLISECONDS
											.toHours(playlist.get(position).getTotalDuration())),

					TimeUnit.MILLISECONDS.toSeconds(playlist.get(position).getTotalDuration())
							- TimeUnit.MINUTES
									.toSeconds(TimeUnit.MILLISECONDS
											.toMinutes(playlist.get(position).getTotalDuration()))));


        holder.arrowImage.setColorFilter(Color.DKGRAY);
        if (Build.VERSION.SDK_INT > 15) {
            holder.arrowImage.setImageAlpha(255);
        } else {
            holder.arrowImage.setAlpha(255);
        }

        holder.arrowImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    PopupMenu popup = new PopupMenu(mContext, v);
                    popup.getMenuInflater().inflate(R.menu.play_list_menu, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.playlist_rename:
                                    playlistRename(playlist.get(position).getId());
                                    break;
                                case R.id.playlist_delete:
                                    playlistDelete(playlist.get(position).getId());
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

    public void playlistDelete(long id){
        Playlist.deletePlaylist(mContext.getContentResolver(), id);

        notifyDataSetChanged();

        Toast.makeText(mContext,
                "Delete",
                Toast.LENGTH_SHORT).show();
    }

    public void playlistRename(final long playlist_id){
// get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_screen_rename, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setView(promptView);

        TextView headerTitle = (TextView) promptView.findViewById(R.id.dialog_header);
        headerTitle.setText("Rename PlayList");

       final EditText newNameEditText = (EditText) promptView.findViewById(R.id.newName);
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

        alertDialogBuilder.setPositiveButton(R.string.done,
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {

//                        String folderName = newNameEditText
//                                .getText().toString().trim();

                        Playlist.renamePlaylist(mContext.getContentResolver(), playlist_id, newNameEditText.getText()
                                .toString());
                        notifyDataSetChanged();

                        Toast.makeText(mContext,
                                "Rename Successfull",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

        // create an alert dialog
        alertDialogBuilder.create().show();




    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.playlist =Playlist.getAllPlaylists(mContext.getContentResolver());
    }
}
