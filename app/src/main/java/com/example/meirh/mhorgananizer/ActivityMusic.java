package com.example.meirh.mhorgananizer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import Model.ListItem;
import Model.ListItemSong;
import Utills.PersonalEvents;



public class ActivityMusic extends AppCompatActivity implements View.OnClickListener
{

    private Button              btnList;
    private Button              btnPlay;
    private Button              btnPrev;
    private Button              btnNext;
    public MediaPlayer          mediaPlayer;
    private TextView            lblSongName;
    private TextView            lblSongArtist;
    private TextView            lblAlbum;
    private TextView            lblPosNow;
    private TextView            lblPosLeft;
    private SeekBar             barSeek;
    private ImageView           imgSongArtist;
    private ImageView           imgLine;
    private Thread              thread;


    private ArrayList<String>           ListItemSimple;
    private List<ListItemSong>          ListItemsRecycler;  // List<ListItemSong>

    public final int REQUEST_CODE_SONGLIST_SIMPLE = 2;
    public final int REQUEST_CODE_SONGLIST_RECYCLER = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        setUpUIControls();

        FillList();

        LoadSongIntoPlayer(R.raw.love_the_one, 3);
    }



    private void FillList()
    {

        Field[] fields = R.raw.class.getFields();

        ListItemsRecycler = new ArrayList<ListItemSong>();   // List<E>
        //ListItemsRecycler =  new List<ListItemSong>;
        ListItemSimple = new ArrayList<String>();

        for (int i = 0; i < fields.length; i++)
        {
            ListItemSong item = new ListItemSong(fields[i].getName(), "Artist of Item " + (i + 1), "Album of Item " + (i + 1));  // TODO: add album

            int resourceId = this.getResources().getIdentifier(fields[i].getName(), "raw", this.getPackageName());
            int duration = MediaPlayer.create(getApplicationContext(), resourceId).getDuration();

            item.setResourceID(resourceId);
            item.setDuration(duration);
            item.setYear("Year " + String.valueOf(2000 + i + 1));   // TODO: get year

            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable((Drawable) getDrawable(R.drawable.meir1));
            item.setImageItem(imageView);

            ListItemsRecycler.add(item);

            ListItemSimple.add(fields[i].getName());

            //String uriStr = getResources() + "android.resource://"+ this.getPackageName() + "/" + "raw/";
            //app\src\main\res
            //com.example.meirh.mhorgananizer
            //Uri uri = Uri.parse(uriStr);
            //File f = new File(uri.toString());
            //Files files = f.listFiles();
            //String[] someFiles = f.list();

            //imageView.setImageResource(R.drawable.purim_15);
            // Set picture from SD-Card
            // imageView.setImageBitmap(setPicFromSDCard());
            //File imgFile = new  File("/sdcard/Images/test_image.jpg");
            //if(imgFile.exists())
            //{
            //    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //     //And include this permission in the manifest file:
            //    //<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
            //}

        }

        //adapterListRecycler = new AdapterSong(this, ListItemsRecycler);
        //listControlRecycler.setAdapter(adapterListRecycler);
    }

    private void setUpUIControls()
    {

        btnList = (Button) findViewById(R.id.btnList);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPrev = (Button) findViewById(R.id.btnPrev);
        btnNext = (Button) findViewById(R.id.btnNext);
        lblSongName = (TextView) findViewById(R.id.lblSongName);
        lblSongArtist = (TextView) findViewById(R.id.lblSongArtist);
        lblAlbum = (TextView) findViewById(R.id.lblAlbum);
        lblPosNow = (TextView) findViewById(R.id.lblPosNow);
        lblPosLeft = (TextView) findViewById(R.id.lblPosLeft);
        imgSongArtist = (ImageView) findViewById(R.id.imgSongArtist);
        imgLine = (ImageView) findViewById(R.id.imgLine);


        btnPlay.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnList.setOnClickListener(this);

        imgLine.bringToFront();
        imgLine.setVisibility(View.INVISIBLE);  // TODO: Maybe to delete
        lblSongArtist.bringToFront();
        lblSongName.bringToFront();
        lblAlbum.bringToFront();
        //barSeek.bringToFront();
        lblAlbum.bringToFront();


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(ActivityMusic.this, "Finish Play", Toast.LENGTH_SHORT).show();
                MusicPause();
            }
        });

        barSeek = (SeekBar) findViewById(R.id.barSeek);
        barSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateProgress();
                if (fromUser)
                {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                    seekBar.bringToFront();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // Assigned it direct because it's Static var
        ActivitySongList.ListenerSimple = new PersonalEvents.OnListViewItemClick()
        {
            @Override
            public void setOnListViewItemPressed(int listPositionIndex, String selectedItemText)
            {
                int resID=1;
                for (ListItemSong item : ListItemsRecycler)                             // TODO: Change to simple
                {
                    if (item.getSongName().equals(selectedItemText))
                    {
                        resID = item.getResourceID();
                        break;
                    }
                }
                resID = ListItemsRecycler.get(listPositionIndex).getResourceID();       // TODO: Change to simple
                LoadSongIntoPlayer(resID, listPositionIndex);
            }
        };

        // Assigned it direct because it's Static var
        ActivitySongList.ListenerRecycler = new PersonalEvents.OnRecyclerViewItemClick()
        {
            @Override
            public void setOnRecyclerViewItemPressed(int cardViewPressedResID, int listPositionIndex)
            {
                int resID = ListItemsRecycler.get(listPositionIndex).getResourceID();
                LoadSongIntoPlayer(resID, listPositionIndex);
            }
        };
    }

    private void LoadSongIntoPlayer(int resourceID, int listPositionIndex)
    {
        MusicPause();

        mediaPlayer = MediaPlayer.create(getApplicationContext(), resourceID);  //listItems.get(listPositionIndex).getResourceID()
        //mediaPlayer = MediaPlayer.create(new Uri("/0/Music/one.mp3");         //TODO: load from smartphone disk
        barSeek.setMax(mediaPlayer.getDuration());

        lblSongName.setText(ListItemsRecycler.get(listPositionIndex).getSongName());
        lblSongArtist.setText(ListItemsRecycler.get(listPositionIndex).getArtist());
        lblAlbum.setText(ListItemsRecycler.get(listPositionIndex).getAlbum());

        MusicPlay();
    }

    private void updateProgress()
    {
        int currentPos = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();

        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        lblPosNow.setText(dateFormat.format(new Date(currentPos)));
        lblPosLeft.setText(dateFormat.format(new Date(duration - currentPos)));

        //lblPosNow.setText(String.valueOf(duration / 1000);
        //lblPosLeft.setText(String.valueOf((mediaPlayer.getDuration() - progress) / 1000));
    }

    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {
            case R.id.btnPlay:
                if (mediaPlayer != null && mediaPlayer.isPlaying())
                {
                    MusicPause();
                } else {
                    MusicPlay();
                }
                break;

            case R.id.btnNext:
                seekMusic(5000);
                break;

            case R.id.btnPrev:
                seekMusic(-5000);
                break;

            case R.id.btnList:
                OpenSongListView();
                break;

            case R.id.btnBack:
                goBack();
                break;

        }
    }

    // Open List view with songs list
    private void OpenSongListView()
    {

        Intent intentMusic = new Intent(ActivityMusic.this, ActivitySongList.class);

        Bundle bundle = new Bundle();

        bundle.putStringArrayList("ListItems", ListItemSimple);       // TODO: Maybe passe it like this instead of thrue Intent

        // Here we choose witch List tyle will be shown
        intentMusic.putExtra("ListMode",2);

        intentMusic.putExtra("ListItems", bundle);

        ActivitySongList.ListItemsRecycler = ListItemsRecycler;
        ActivitySongList.ListItemSimple = ListItemSimple;         // TODO: Maybe passe it like this instead of thrue Intent

        //startActivity(intentMusic);
        //startActivityForResult(intentMusic, REQUEST_CODE_SONGLIST_SIMPLE);
        startActivityForResult(intentMusic, REQUEST_CODE_SONGLIST_RECYCLER);


        //String[] listItemArray = new String[ListItemSimple.size()];
        //ArrayList<String> ArrayListItem = new ArrayList<String>();
        //for (int i=0; i<ListItemsRecycler.size(); i++)
        //{
        //   ArrayListItem.add(ListItemsRecycler.get(i).getSongName());
        //    listItemArray[i] = ListItemsRecycler.get(i).getSongName();
        //}
        //ArrayListItem.addAll(Arrays.asList(listItemArray));
        //data.putParcelable("ActivitySongList", activitySongList);
        //data.putStringArray("ListItems", listItemArray);
        //data.putParcelableArrayList("search.resultSet", listArray);
        //ArrayList<ListItem>  listArray = new ArrayList<ListItem>();
        //listArray.add(new ListItem("Meir", "abc", "def"));
        //listArray.add(new ListItem("Amit", "abc", "def"));
        //listArray.add(new ListItem("Ronen", "abc", "def"));
        //intentMusic.putParcelableArrayListExtra("ListItems", listItemArray);
        //intentMusic.putExtra("ListItems", listItemArray);
        //intentMusic.putExtra("ListItems", listItemArray);

    }

    private void seekMusic(int interval) {
        barSeek.setProgress(barSeek.getProgress() + interval);
        mediaPlayer.seekTo(barSeek.getProgress());
    }

    public void MusicPause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            btnPlay.setBackgroundResource(android.R.drawable.ic_media_play);   //R.drawable.ic_media_play);
            //btnPlay.setText("Play");
        }
    }

    public void MusicPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            btnPlay.setBackgroundResource(android.R.drawable.ic_media_pause);    // R.drawable.ic_media_pause);
            updateThread();
            //btnPlay.setText("Pause");
        }
    }

    public void updateThread() {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //barSeek.setMax(newMax);
                                //int newMax = mediaPlayer.getDuration();
                                int newPosition = mediaPlayer.getCurrentPosition();
                                barSeek.setProgress(newPosition);
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            Toast.makeText(ActivityMusic.this, "Destroing Media Player control", Toast.LENGTH_SHORT).show();
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    private void goBack() {
        //String returnedData;

        //returnedData = "18 emails haz been read";

        //Intent intent = getIntent();
        //intent.putExtra("returnedData",  returnedData);

        //Toast.makeText(this, "Come Back with ... " + returnedData, Toast.LENGTH_SHORT).show();
        //setResult(RESULT_OK, intent);
        finish();
    }

    // After come back from 'ActivitySongList' view
    // Never arrived here. Will use fire a Event.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case REQUEST_CODE_SONGLIST_SIMPLE:
                    int listPositionIndex = data.getIntExtra("listPositionIndex", 0);
                    String selectedItemText = data.getStringExtra("selectedItemText");
                    //Toast.makeText(MainActivity.this, "Returned data: " + result, Toast.LENGTH_LONG).show();
                    int resID = 1;
                    // TODO: Why not compare right
//                    for (int i=0; i<ListItemsRecycler.size(); i++)
//                    {
//                        if (ListItemsRecycler.get(i).getSongName() == selectedItemText)
//                        {
//                            resID = ListItemsRecycler.get(i).getResourceID();
//                            break;
//                        }
//                    }
                    resID = ListItemsRecycler.get(listPositionIndex).getResourceID();
                    LoadSongIntoPlayer(resID, listPositionIndex);
                    break;

                case REQUEST_CODE_SONGLIST_RECYCLER:
                    int cardViewPressedResID = data.getIntExtra("cardViewPressedResID", 0);
                    int listPositionIndex2 = data.getIntExtra("listPositionIndex", 0);
                    //Toast.makeText(this, "Returned data: " + String.valueOf(cardViewPressedResID) + String.valueOf(listPositionIndex2), Toast.LENGTH_LONG).show();
                    resID = ListItemsRecycler.get(listPositionIndex2).getResourceID();
                    LoadSongIntoPlayer(resID, listPositionIndex2);
                    break;
            }
        }
        else
        {
            //Toast.makeText(MainActivity.this, "No Data", Toast.LENGTH_LONG).show();
            return;
        }

    }
}