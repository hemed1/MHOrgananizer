package Model;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.example.meirh.mhorgananizer.ActivityMusic;

import java.util.ArrayList;

/**
 * Created by meirh on 09/04/2018.
 */

public class ListItemSong
{
    private String      SongName;
    private String      Artist;
    private String      Album;
    private String      Year;
    private int         Duration;
    private int         ResourceID;
    private ArrayList<Integer> picsToSongResourcesIDs;

    private ImageView   ImageItem;



    public ListItemSong(String songName, String artist, String album)
    {
        this.SongName = songName;
        this.Artist = artist;
        this.Album=album;
        //ImageItem = new ImageView(ActivityMusic.this);
        picsToSongResourcesIDs = new ArrayList<Integer>();
    }

    public ArrayList<Integer> getPicsToSongResIDsArray()
    {
        return picsToSongResourcesIDs;
    }

    public int getDuration()
    {
        return Duration;
    }

    public void setDuration(int duration)
    {
        Duration = duration;
    }

    public int getResourceID()
    {
        return ResourceID;
    }

    public void setResourceID(int resourceID) {
        this.ResourceID = resourceID;
    }

    public String getSongName()
    {
        return SongName;
    }

    public void setSongName(String songName) {
        SongName = songName;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getAlbum() {
        return Album;
    }

    public void setAlbum(String album) {
        Album = album;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public ImageView getImageItem()
    {
        return ImageItem;
    }

    public void setImageItem(ImageView imageItem)
    {
        ImageItem = imageItem;
    }

    public void setImagePicture(Drawable drawable)
    {
        ImageItem.setBackground(drawable);
    }


}
