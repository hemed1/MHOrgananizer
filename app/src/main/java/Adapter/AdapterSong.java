package Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;


import com.example.meirh.mhorgananizer.ActivityMusic;
import com.example.meirh.mhorgananizer.MainActivity;
import com.example.meirh.mhorgananizer.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import Model.ListItemSong;
import Utills.PersonalEvents;

import static android.app.Activity.RESULT_OK;


public class AdapterSong extends RecyclerView.Adapter<AdapterSong.SongHolder>
{
    private Context                 context;
    private List<ListItemSong>      listItems;


    // The listener must implement the events interface and passes messages up to the parent.
    private PersonalEvents.OnRecyclerViewItemClick      listener;

    // Assign the listener implementing events interface that will receive the events
    public void setOnSongClick(PersonalEvents.OnRecyclerViewItemClick  listener)
    {
        this.listener = listener;
    }

    public AdapterSong(Context context, List listItems)
    {
        this.context = context;
        this.listItems = listItems;
        listener = null;
    }

    @Override
    public AdapterSong.SongHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View  cardViewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_song, parent, false);
        SongHolder songHolder = new SongHolder(cardViewItem);

        return songHolder;
    }

    @Override
    public void onBindViewHolder(AdapterSong.SongHolder holder, int position)
    {
        ListItemSong item = listItems.get(position);

        holder.lblSongName.setText(item.getSongName());
        holder.lblArtist.setText(item.getArtist());
        holder.lblAlbum.setText(item.getAlbum());

        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        holder.lblYear.setText(dateFormat.format(new Date(item.getDuration())));
        //holder.lblYear.setText(String.valueOf(item.getDuration()/1000));
        //holder.lblYear.setText(item.getYear());

        holder.Image.setImageDrawable(item.getImageItem().getDrawable());
    }

    @Override
    public int getItemCount()
    {
        return listItems.size();
    }

    public class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView  lblSongName;
        private TextView  lblArtist;
        private TextView  lblAlbum;
        private TextView  lblYear;
        private ImageView Image;

        public SongHolder(View cardViewItem)   // Represent the Item in List
        {
            super(cardViewItem);

            cardViewItem.setOnClickListener(this);

            lblSongName = (TextView) cardViewItem.findViewById(R.id.lblSongName);
            lblArtist = (TextView) cardViewItem.findViewById(R.id.lblArtist);
            lblAlbum = (TextView) cardViewItem.findViewById(R.id.lblAlbum);
            lblAlbum.setVisibility(View.INVISIBLE);
            lblYear = (TextView) cardViewItem.findViewById(R.id.lblYear);
            lblYear.setVisibility(View.VISIBLE);
            Image = (ImageView) cardViewItem.findViewById(R.id.imgItem);
        }


        @Override
        public void onClick(View view)
        {
            ListItemSong item = listItems.get(getAdapterPosition());

            String returnedData;

            returnedData = item.getSongName();

            Toast.makeText(context, returnedData, Toast.LENGTH_SHORT).show();

            if (listener != null)
            {
                // Now let's fire listener here
                listener.setOnRecyclerViewItemPressed(item.getResourceID(), getAdapterPosition());
            }

//            Intent intent = getIntent();
//            intent.putExtra("returnedData",  returnedData);
//
//            Toast.makeText(this, "Come Back with ... " + returnedData, Toast.LENGTH_SHORT).show();
//            context.setResult(RESULT_OK, intent);

              //((ActivityMusic)context).SongResourceID = item.getResourceID();
              //((ActivityMusic)context).mediaPlayer.create(((ActivityMusic)context).getApplicationContext(), item.getResourceID());

//            Intent intent = new Intent(context, DetailsActivity.class);
//            intent.putExtra("Name", item.getName());
//            intent.putExtra("Description", item.getDescription());
//            intent.putExtra("Rating", item.getRating());
//
//            context.startActivity(intent);

        }
    }

    public List<ListItemSong> getListItems() {
        return listItems;
    }

    public void setListItems(List<ListItemSong> listItems)
    {
        this.listItems = listItems;
    }


}
