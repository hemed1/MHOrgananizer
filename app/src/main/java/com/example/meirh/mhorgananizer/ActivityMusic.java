package com.example.meirh.mhorgananizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private ImageView           imgSongArtist1;
    private ImageView           imgSongArtist2;
    private ImageView           imgSongArtist3;
    private ImageView           imgLine;
    private Thread              thread;
    private ArrayList<String>   AllSongsImages;


    private ArrayList<String>           ListItemSimple;
    private List<ListItemSong>          ListItemsRecycler;  // List<ListItemSong>

    public final int REQUEST_CODE_SONGLIST_SIMPLE = 2;
    public final int REQUEST_CODE_SONGLIST_RECYCLER = 3;

    private int                 ListPositionIndex;

    //Song Name, Song SourceID // pictures SourcesIDs for specific song
    private List<Pair<Pair<String, Integer>, ArrayList<Integer>>>   listResources;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        setUpUIControls();

        ReadAllResources();

        FillList();

        ListPositionIndex = 1;
        LoadSongIntoPlayer(ListPositionIndex);
    }


    private void ReadAllResources()
    {
        String      fileNameSong;
        String      fileNamePic;
        int         songResourceID;
        int         picResourceID;
        Field[]     fieldsSongs;
        Field[]     fieldsPics;




        fieldsSongs = R.raw.class.getFields();
        fieldsPics = R.drawable.class.getFields();

        listResources = new ArrayList<Pair<Pair<String, Integer>, ArrayList<Integer>>>();


        for (int i = 0; i < fieldsSongs.length; i++) {

            fileNameSong = fieldsSongs[i].getName();
            songResourceID = this.getResources().getIdentifier(fileNameSong, "raw", this.getPackageName());

            //TODO: Assume that all files in this folder ara '*mp3'. will be filter just the '*mp3'
            if (fileNameSong.endsWith(".mp3"))
            {
                //continue;
            }

            ArrayList<Integer> PicsToSongSourcesIDs = new ArrayList<Integer>();
            Pair<Pair<String, Integer>, ArrayList<Integer>> pairOfSong;


            for (int j = 0; j < fieldsPics.length; j++)
            {
                fileNamePic = fieldsPics[j].getName();

                //System.out.println(this.getResources().getResourceName(picResourceID));
                //System.out.println(this.getResources().getResourceEntryName(picResourceID));

                if (fileNamePic.startsWith(fileNameSong)) //.jpg
                {
                    picResourceID = this.getResources().getIdentifier(fileNamePic, "drawable", this.getPackageName());
                    //Toast.makeText(ActivityMusic.this, "Song name: " + fileNameSong + "   Pic name: " + fileNamePic + "   ResID: " + String.valueOf(picResourceID), Toast.LENGTH_SHORT).show();
                    if (picResourceID==0)
                    {
                        Toast.makeText(ActivityMusic.this, "Not Found - Song name: " + fileNameSong + "   Pic name: " + fileNamePic, Toast.LENGTH_LONG).show();
                        picResourceID = this.getResources().getIdentifier("default1", "drawable", this.getPackageName());
                    }
                    PicsToSongSourcesIDs.add(picResourceID);
                }
            }

            // Assume that all files in this folder ara '*mp3', even if it dont have fit picture
            if (PicsToSongSourcesIDs.size() < 1)
            {
                picResourceID = this.getResources().getIdentifier("default1", "drawable", this.getPackageName());
                PicsToSongSourcesIDs.add(picResourceID);
            }
            if (PicsToSongSourcesIDs.size() < 2)
            {
                picResourceID = this.getResources().getIdentifier("default2", "drawable", this.getPackageName());
                PicsToSongSourcesIDs.add(picResourceID);
            }
            if (PicsToSongSourcesIDs.size() < 3)
            {
                picResourceID = this.getResources().getIdentifier("default3", "drawable", this.getPackageName());
                PicsToSongSourcesIDs.add(picResourceID);
            }

            Pair<String, Integer>  songProps = new Pair<String, Integer>(fileNameSong, songResourceID);
            pairOfSong = new Pair<Pair<String, Integer>, ArrayList<Integer>>(songProps,PicsToSongSourcesIDs);
            listResources.add(pairOfSong);

        }
    }


    private void FillList()
    {
        ArrayList<Integer>  picsResIDs = new ArrayList<Integer>();
        ImageView           imageView;
        String              fileName;
        Integer             resourceId;




        ListItemsRecycler = new ArrayList<ListItemSong>();   // List<E>
        ListItemSimple = new ArrayList<String>();


        for (int i = 0; i < listResources.size(); i++)
        {
            Pair<String, Integer>  songProps = listResources.get(i).first;

            fileName = songProps.first;
            picsResIDs = listResources.get(i).second;

            ListItemSong item = new ListItemSong(fileName, "Artist of Item " + (i + 1), "Album of Item " + (i + 1));  // TODO: add album

            resourceId = songProps.second;
            int duration = MediaPlayer.create(getApplicationContext(), resourceId).getDuration();

            item.setResourceID(resourceId);
            item.setDuration(duration);
            item.setYear("Year " + String.valueOf(2000 + i + 1));   // TODO: get year

            if (picsResIDs.size()>0)
            {
                resourceId = picsResIDs.get(0);
                item.getPicsToSongResIDsArray().add(resourceId);
                // For the pic in the list item image
                imageView = new ImageView(this);
                //imageView.setBackground(getDrawable(resourceId));
                item.setImageItem(imageView);
                //item.setImagePicture(getDrawable(resourceId));
                //Toast.makeText(ActivityMusic.this, "FillList - Pictures to Song:  " + getDrawable(resourceId).toString(), Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(ActivityMusic.this, "FillList - No Pictures to Song:  " + fileName, Toast.LENGTH_LONG).show();
            }


            if (picsResIDs.size()>1)
            {
                resourceId = picsResIDs.get(1);
                //Toast.makeText(ActivityMusic.this, "Pic 2 resID: " + String.valueOf(resourceId), Toast.LENGTH_SHORT).show();
                item.getPicsToSongResIDsArray().add(resourceId);
            }
            if (picsResIDs.size()>2)
            {
                resourceId = picsResIDs.get(2);
                //Toast.makeText(ActivityMusic.this, "Pic 3 resID: " + String.valueOf(resourceId), Toast.LENGTH_SHORT).show();
                item.getPicsToSongResIDsArray().add(resourceId);
            }

            //File imgFile = new  File("/sdcard/Images/test_image.jpg")

            ListItemsRecycler.add(item);

            ListItemSimple.add(fileName);
        }

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
        imgSongArtist1 = (ImageView) findViewById(R.id.imgSongArtist1);
        imgSongArtist2 = (ImageView) findViewById(R.id.imgSongArtist2);
        imgSongArtist3 = (ImageView) findViewById(R.id.imgSongArtist3);
        imgLine = (ImageView) findViewById(R.id.imgLine);


        btnPlay.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnList.setOnClickListener(this);

        //imgLine.bringToFront();   // crash
        imgLine.setVisibility(View.INVISIBLE);  // TODO: Maybe to delete
        lblSongArtist.bringToFront();
        lblSongName.bringToFront();
        lblAlbum.bringToFront();
        //barSeek.bringToFront();  // crash


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //Toast.makeText(ActivityMusic.this, "Finish Play", Toast.LENGTH_SHORT).show();
                MusicPause();
            }
        });

        barSeek = (SeekBar) findViewById(R.id.barSeek);
        barSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                updateProgressControls();
                if (fromUser)
                {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                    //seekBar.bringToFront();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });


        // Assigned it direct because it's Static var
        ActivitySongList.ListenerSimple = new PersonalEvents.OnListViewItemClick()
        {
            @Override
            public void setOnListViewItemPressed(int listPositionIndex, String selectedItemText)
            {
                ListPositionIndex = listPositionIndex;
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
                LoadSongIntoPlayer(listPositionIndex);
            }
        };

        // Assigned it direct because it's Static var
        ActivitySongList.ListenerRecycler = new PersonalEvents.OnRecyclerViewItemClick()
        {
            @Override
            public void setOnRecyclerViewItemPressed(int cardViewPressedResID, int listPositionIndex)
            {
                ListPositionIndex = listPositionIndex;
                int resID = ListItemsRecycler.get(listPositionIndex).getResourceID();
                LoadSongIntoPlayer(listPositionIndex);
            }
        };
    }

    private void LoadSongIntoPlayer(int listPositionIndex)
    {
        int resourceID;

        MusicPause();

        resourceID = ListItemsRecycler.get(listPositionIndex).getResourceID();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), resourceID);  //listItems.get(listPositionIndex).getResourceID()
        //mediaPlayer = MediaPlayer.create(new Uri("/0/Music/one.mp3");         //TODO: load from smartphone disk
        //MusicPause();
        mediaPlayer.seekTo(0);
        barSeek.setMax(mediaPlayer.getDuration());
        barSeek.setProgress(0);

        setSongControls(listPositionIndex);

        MusicPlay();
    }

    // Set the Song props - Name, Artist, Album, Duration
    private void setSongControls(int listPositionIndex)
    {
        Drawable drawable;


        ListItemSong item = ListItemsRecycler.get(listPositionIndex);

        lblSongName.setText(item.getSongName());
        lblSongArtist.setText(item.getArtist());
        lblAlbum.setText(item.getAlbum());

        int widthScreen = (int)(this.getWindow().getDecorView().getWidth() * 0.9);
        int heightScreen = (int)(this.getWindow().getDecorView().getHeight() * 0.9);
        //Toast.makeText(this, "Screen Width: "+String.valueOf(widthScreen) +"  Screen Height: "+ String.valueOf(heightScreen), Toast.LENGTH_SHORT).show();

        if (item.getPicsToSongResIDsArray().size()>0)
        {
            drawable = this.getDrawable(item.getPicsToSongResIDsArray().get(0));
            drawable.setLevel(5000);
            //imgSongArtist1.setMinimumWidth(widthScreen);
            //imgSongArtist1.setMaxWidth(widthScreen);
            //Toast.makeText(this, "Width: "+String.valueOf(drawable.getIntrinsicWidth()) +"  Height: "+ String.valueOf(drawable.getIntrinsicHeight()), Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "Width: "+String.valueOf(imgSongArtist1.getWidth()) +"  Height: "+ String.valueOf(imgSongArtist1.getHeight()), Toast.LENGTH_SHORT).show();
            //imgSongArtist1.setMinimumWidth(drawable.getIntrinsicWidth());
            //imgSongArtist1.setMinimumHeight(drawable.getIntrinsicHeight());
            //Bitmap myBitmap=null
            //Bitmap bitmap = Bitmap.createBitmap(new int[]{255,0,0},700, 900, Bitmap.Config.RGB_565);
            //Canvas canvas = new Canvas(bitmap);
            //Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            //paint.setColor(Color.BLACK);
            //canvas.drawCircle(50, 50, 10, paint);
            //imgSongArtist1.setImageBitmap(bitmap);
            //imgSongArtist1.setImageDrawable(drawable);
            //imgSongArtist1.setCropToPadding(false);
            // This prop make sure the picture NOT smash and loss it's proportion - Just crop in center
            //imgSongArtist1.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgSongArtist1.setImageDrawable(drawable);   // Equal to prop 'app:srcCompat="@drawable/meir1"'
            //imgSongArtist1.setBackground(drawable);    // Smash the picture and loos proportion
        }

        if (item.getPicsToSongResIDsArray().size()>1 && item.getPicsToSongResIDsArray().get(1)>0)
        {
            drawable = this.getDrawable(item.getPicsToSongResIDsArray().get(1));
            //Toast.makeText(ActivityMusic.this, "setSongControls, Loading Pic 2: " + String.valueOf(drawable), Toast.LENGTH_SHORT).show();
            // This prop make sure the picture NOT smash and loss it's proportion - Just crop in center
            //imgSongArtist2.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgSongArtist2.setImageDrawable(drawable);   // Equal to prop 'app:srcCompat="@drawable/meir1"'
            //imgSongArtist2.setBackground(drawable);    // Smash the picture and loos proportion
        }

        if (item.getPicsToSongResIDsArray().size()>2 && item.getPicsToSongResIDsArray().get(2)>0)
        {
            drawable = this.getDrawable(item.getPicsToSongResIDsArray().get(2));
            //Toast.makeText(ActivityMusic.this, "setSongControls, Loading Pic 3: " + String.valueOf(drawable), Toast.LENGTH_SHORT).show();
            // This prop make sure the picture NOT smash and loss it's proportion - Just crop in center
            //imgSongArtist3.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //Toast.makeText(this, "Before: ImageView Width: "+String.valueOf(imgSongArtist3.getWidth()) +"  ImageView Height: "+ String.valueOf(imgSongArtist3.getHeight()), Toast.LENGTH_SHORT).show();
            imgSongArtist3.setImageDrawable(drawable);   // Equal to prop 'app:srcCompat="@drawable/meir1"'
            //imgSongArtist3.setBackground(drawable);    // Smash the picture and loos proportion
            //imgSongArtist3.setMinimumWidth(widthScreen);
            //imgSongArtist3.setMaxWidth(widthScreen);
            //imgSongArtist3.setMinimumHeight(heightScreen);
            //imgSongArtist3.setMaxHeight(heightScreen);
            //Toast.makeText(this, "After: ImageView Width: "+String.valueOf(imgSongArtist3.getWidth()) +"  ImageView Height: "+ String.valueOf(imgSongArtist3.getHeight()), Toast.LENGTH_SHORT).show();
        }

    }
    private void updateProgressControls()
    {
        int currentPos = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();

        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        lblPosNow.setText(dateFormat.format(new Date(currentPos)));
        lblPosLeft.setText(dateFormat.format(new Date(duration - currentPos)));
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
                if (ListPositionIndex < ListItemsRecycler.size()-1)
                {
                    ListPositionIndex++;
                    LoadSongIntoPlayer(ListPositionIndex);
                }
                //seekMusic(5000);
                break;

            case R.id.btnPrev:
                if (ListPositionIndex < ListItemsRecycler.size() && ListPositionIndex>0)
                {
                    ListPositionIndex--;
                    LoadSongIntoPlayer(ListPositionIndex);
                }
                //seekMusic(-5000);
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
        intentMusic.putExtra("ListMode",1);

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

    private void seekMusic(int interval)
    {
        barSeek.setProgress(barSeek.getProgress() + interval);
        mediaPlayer.seekTo(barSeek.getProgress());
    }

    public void MusicPause()
    {
        if (mediaPlayer != null)
        {
            thread=null;
            mediaPlayer.pause();
            //mediaPlayer.stop();
            btnPlay.setBackgroundResource(android.R.drawable.ic_media_play);   //R.drawable.ic_media_play);
            //btnPlay.setText("Play");
        }
    }

    public void MusicPlay()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.start();
            btnPlay.setBackgroundResource(android.R.drawable.ic_media_pause);    // R.drawable.ic_media_pause);

            updateThread();
        }
    }

    public void updateThread() {
        thread = new Thread() {
            @Override
            public void run()
            {
                try
                {
                    while (mediaPlayer != null && mediaPlayer.isPlaying())
                    {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                //barSeek.setMax(newMax);
                                //int newMax = mediaPlayer.getDuration();
                                int newPosition = mediaPlayer.getCurrentPosition();
                                barSeek.setProgress(newPosition);
                            }
                        });

                    }
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null)
        {
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
                    LoadSongIntoPlayer(listPositionIndex);
                    break;

                case REQUEST_CODE_SONGLIST_RECYCLER:
                    int cardViewPressedResID = data.getIntExtra("cardViewPressedResID", 0);
                    int listPositionIndex2 = data.getIntExtra("listPositionIndex", 0);
                    //Toast.makeText(this, "Returned data: " + String.valueOf(cardViewPressedResID) + String.valueOf(listPositionIndex2), Toast.LENGTH_LONG).show();
                    resID = ListItemsRecycler.get(listPositionIndex2).getResourceID();
                    LoadSongIntoPlayer(listPositionIndex2);
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

//String path = "..app/src/main";
//File f = new File(path);
//Files files = f.listFiles();
//String[] someFiles = f.list();
