package com.example.meirh.mhorgananizer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.CardView;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Adapter.AdapterSong;
import Model.ListItemSong;
import Utills.PersonalEvents;


public class ActivityMusic extends AppCompatActivity implements View.OnClickListener
{

    private Button          btnBack;
    private Button          btnPlay;
    private Button          btnPrev;
    private Button          btnNext;
    public  MediaPlayer     mediaPlayer;
    private TextView        lblSongName;
    private TextView        lblSongArtist;
    private TextView        lblAlbum;
    private TextView        lblPosNow;
    private TextView        lblPosLeft;
    private SeekBar         barSeek;
    private ImageView       imgSongArtist;
    private Thread          thread;
    private CardView        crdView;
    private RecyclerView            listSongs;
    private AdapterSong             adapterSong;  // RecyclerView.Adapter
    private List<ListItemSong>      listItems;

    public int              SongResourceID;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        setUpUI();

        FillList();

        LoadSongIntoPlayer(R.raw.love_the_one, 1);
    }

    private void setUpUI()
    {

        btnBack = (Button) findViewById(R.id.btnBack);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPrev = (Button) findViewById(R.id.btnPrev);
        btnNext = (Button) findViewById(R.id.btnNext);
        lblSongName = (TextView) findViewById(R.id.lblSongName);
        lblSongArtist = (TextView) findViewById(R.id.lblSongArtist);
        lblAlbum = (TextView) findViewById(R.id.lblAlbum);
        lblPosNow = (TextView) findViewById(R.id.lblPosNow);
        lblPosLeft = (TextView) findViewById(R.id.lblPosLeft);
        imgSongArtist = (ImageView) findViewById(R.id.imgSongArtist);
        //crdView = (CardView) findViewById(R.id.cardItemSong);

        listSongs = (RecyclerView) findViewById(R.id.listSongs);
        listSongs.setHasFixedSize(true);
        listSongs.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();  //new List<ListItem>  //new List<ListItemSong>[];

        adapterSong = new AdapterSong(this, listItems);

        // Register the listener for this object
        adapterSong.setOnSongClick(new PersonalEvents.OnSongClick()
        {
            // Listen to event. wait here when the event invoked in child object.
            @Override
            public void onSongPressed(int cardViewPressedResID, int listPositionIndex)
            {
                LoadSongIntoPlayer(cardViewPressedResID, listPositionIndex);
            }
        });

        btnPlay.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        //crdView.setOnClickListener(this);
        listSongs.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer)
            {
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
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void LoadSongIntoPlayer(int resourceID, int listPositionIndex)
    {
        MusicPause();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), resourceID);
        barSeek.setMax(mediaPlayer.getDuration());

        lblSongName.setText(listItems.get(listPositionIndex).getSongName());
        lblSongArtist.setText(listItems.get(listPositionIndex).getArtist());
        lblAlbum.setText(listItems.get(listPositionIndex).getAlbum());

        MusicPlay();
    }

    private void FillList()
    {

        Field[] fields = R.raw.class.getFields();

        for (int i=0; i<fields.length; i++)
        {
            ListItemSong item = new ListItemSong(fields[i].getName(), "Artist of Item " + (i+1), "Album of Item " + (i+1));  // TODO: add album

            int resourceId = this.getResources().getIdentifier(fields[i].getName(), "raw", this.getPackageName());
            item.setResourceID(resourceId);
            item.setYear("Year " + String.valueOf(2000+i+1));   // TODO: get year

            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable((Drawable) getDrawable(R.drawable.meir1));
            item.setImageItem(imageView);
            listItems.add(item);

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

        //adapterSong = new AdapterSong(this, listItems);
        listSongs.setAdapter(adapterSong);
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
                }
                else
                {
                    MusicPlay();
                }
                break;

            case R.id.btnNext:
                seekMusic(5000);
                break;

            case R.id.btnPrev:
                seekMusic(-5000);
                break;

            case R.id.btnBack:
                goBack();
                break;

            case R.id.listSongs:
                SongResourceID = listItems.get(0).getResourceID();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), SongResourceID);
                break;

            case R.id.cardItemSong:
                //SongResourceID = listItems.get(0).getResourceID();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), SongResourceID);
                break;
        }
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
    protected void onDestroy()
    {
        if (mediaPlayer != null) {
            Toast.makeText(ActivityMusic.this, "Destroing Media Player control", Toast.LENGTH_SHORT).show();
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    private void goBack()
    {
        //String returnedData;

        //returnedData = "18 emails haz been read";

        //Intent intent = getIntent();
        //intent.putExtra("returnedData",  returnedData);

        //Toast.makeText(this, "Come Back with ... " + returnedData, Toast.LENGTH_SHORT).show();
        //setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
}