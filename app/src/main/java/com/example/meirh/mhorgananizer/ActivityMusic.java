package com.example.meirh.mhorgananizer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;

//import android.support.v4.util.ArrayMap;
import android.util.ArrayMap;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Model.ListItemSong;
import Utills.PersonalEvents;
import Utills.SortFiles;


public class ActivityMusic extends AppCompatActivity implements View.OnClickListener
{

    public enum ShowListModeEn
    {
        //    MALE(0),
//    FEMALE(1);
        RecyclerView,
        SimpleView
    }

    private Button              btnList;
    private Button              btnPlay;
    private Button              btnPrev;
    private Button              btnNext;
    private Button              btnFolders;
    public  static MediaPlayer  mediaPlayer;
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

    public final int REQUEST_CODE_SONGLIST_SIMPLE = 2;
    public final int REQUEST_CODE_SONGLIST_RECYCLER = 3;
    public final int REQUEST_CODE_FOLDERLIST_RECYCLER = 4;
    private final int PERMISSIONS_REQUEST_READ_STORAGE = 111;


    private int                 ListPositionIndex;
    private String              ListFolderName;

    private ArrayList<String>           AllSongsImages;
    private ArrayList<String>           ListItemSimple;
    private List<ListItemSong>          ListItemsRecycler;
    private List<ListItemSong>          keepListItemsRecyclerAllSongs;

    //Song Name, Song SourceID // pictures SourcesIDs for specific song
    private List<Pair<Pair<String, Integer>, ArrayList<Integer>>>   listResources;
    // For All song list - First Pair - Song name (without path) , Full song path (with path), Array of all pictures to specific song (in same folder)
    private ArrayList<Pair<Pair<String, String>, ArrayList<String>>> listAllSongs;
    // Keep all files recorsive from starting folder - Used just in func 'GetFolderFiles()' as Output
    private ArrayList<File>                     songsFiles;

    // Key: Song name (without path), Value: Full Song path & name
    private ArrayMap<String, String>            songsMap;
    // All Files/Folders that contain 'mp3' files - Key: Full path, Value: ArraList of all Files (File object) in folder
    private ArrayMap<String, ArrayList<File>>   pathMap;
    // Key: Song Path (without song name), Value: ArrayList of all pics files names (with full path) for a song - in same folder of the song file
    private ArrayMap<String, ArrayList<String>> picsMap;

    // The result files & folders (without the full folder path - just the folder name) that shown when peress 'Folder' button
    private ArrayList<String>                  ListItemSimpleFolders;
    // Allways add LowerCase - Keep the result files & folders. Key: Folder name - without the full folder path, Value: full path - that shown when press 'Folder' button
    private ArrayMap<String, String>           ListItemSimpleFoldersFullPath;

    // Keep the Full path file or folder was press when press the 'Folders' button
    private String                              keepLastFolderWasChoosen;
    private boolean                             isFirstSongListLoad;
    private final String                        chars ="abcdefghijklmnopqrstuvwxyz";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        setUpUIControls();

        LoadSongs();

    }

    private void LoadSongs()
    {
        String pathToSearch;
        ArrayList<Pair<Pair<String, String>, ArrayList<String>>> listSongsMerged = new ArrayList<Pair<Pair<String, String>, ArrayList<String>>>();


        //pathToSearch = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
        //pathToSearch = this.getExternalFilesDir(null).getAbsolutePath();
        //pathToSearch = this.getFilesDir().getAbsolutePath();

        Toast.makeText(this, "Search songs on phone disk ...", Toast.LENGTH_LONG).show();

        if (MainActivity.StorageLoadMode.equals(0) || MainActivity.StorageLoadMode.equals(1) || MainActivity.StorageLoadMode.equals(3))
        {
            //Toast.makeText(this, "enter 1 ", Toast.LENGTH_SHORT).show();
            pathToSearch = "/sdcard/";  //Environment.getExternalStorageDirectory().getAbsolutePath();     //"/sdcard/";    //"/sdcard/Music/Genesis";  //"/sdcard/Music/Genesis";  //Environment.getExternalStorageDirectory().getAbsolutePath()   // /storage/3437-3532/
            listAllSongs = ReadSongs(pathToSearch);
            listSongsMerged = listAllSongs;
        }

        if (isExternalStorageAvailable() && !MainActivity.StorageSDCardName.equals("") &&
           (MainActivity.StorageLoadMode.equals(0) || MainActivity.StorageLoadMode.equals(2) || MainActivity.StorageLoadMode.equals(3)))
        {
            //Toast.makeText(this, "enter 2 ", Toast.LENGTH_SHORT).show();
            pathToSearch = MainActivity.StorageSDCardName;  //"/storage/3437-3532/";
            listAllSongs = ReadSongs(pathToSearch);
            listSongsMerged.addAll(listAllSongs);
            listAllSongs = listSongsMerged;
        }

        FillListAllSongs();

        Toast.makeText(this, "Found " + String.valueOf(listAllSongs.size()) + " Songs", Toast.LENGTH_SHORT).show();

        //ReadAllResources();

        ListPositionIndex=0;
        if (ListItemsRecycler.size()>0)
        {
            //LoadSongIntoPlayer(ListPositionIndex);
            // Set the Song props - Name, Artist, Album, Duration
            setSongControls(ListPositionIndex);

        }
    }

    // Read All Song in phone disk
    // Return: Key: Song name (without path), Value: Song path & name
    // Key: Song Path (without song name), Value: ArrayList of all pics names (with full path) for a song in same folder of the song file
    private ArrayList<Pair<Pair<String, String>, ArrayList<String>>> ReadSongs(String startReadFromFolder)
    {
        ArrayList<String>   picsToSong;
        String              songName;
        String              songPath;
        File                songFile;
        String              picName;
        String              picPath;
        List<ArrayMap<String, String>> sortedList;
        ArrayMap<String, String> sortedArray;



        RequestPermissions();

        listAllSongs = new ArrayList<Pair<Pair<String, String>, ArrayList<String>>>();

        songsFiles = new ArrayList<File>();
        songsMap = new ArrayMap<String, String>();
        picsMap = new ArrayMap<String, ArrayList<String>>();
        pathMap = new ArrayMap<String, ArrayList<File>>();

        // Collect all files from started folder name (recursive) with specific extention
        GetFolderFilesSongPictsMap(startReadFromFolder, ".mp3", true);

        SortFiles sortFiles = new SortFiles();
        sortedList = sortFiles.Sort(songsMap);

        // Just put the result into Final pair arrays 'listAllSongs'

        for (int songsIndex = 0; songsIndex < sortedList.size(); songsIndex++)    //songsMap.size()
        {
            sortedArray = (ArrayMap<String, String>)sortedList.get(songsIndex);

            songName = (String)sortedArray.keyAt(0);
            songPath = (String)sortedArray.valueAt(0);

            //songName = (String)songsMap.keyAt(songsIndex);
            //songPath = (String)songsMap.valueAt(songsIndex);

            Pair<String, String> pairSongPath = new Pair<String, String>(songName, songPath);

            // The path without file name
            songPath = songPath.substring(0, songPath.toLowerCase().indexOf(songName.toLowerCase()));

            picsToSong = new ArrayList<String>();
            int picIndex=-1;

            if (picsMap.containsKey(songPath))
            {
                picsToSong = (ArrayList<String>) picsMap.get(songPath);
                picIndex = picsMap.indexOfKey(songPath);
            }

            Pair<Pair<String, String>, ArrayList<String>> songPair = new Pair<Pair<String, String>, ArrayList<String>>(pairSongPath, picsToSong);
            listAllSongs.add(songPair);
        }

        return listAllSongs;
    }

    // Fill by Disk files
    private void FillListAllSongs()
    {
        ArrayList<String>   picsPathsToSong = new ArrayList<String>();
        ImageView           imageView;
        String              songName;
        String              songPath;
        String              picPath;
        int                 duration;





        ListItemsRecycler = new ArrayList<ListItemSong>();   // List<E>
        ListItemSimple = new ArrayList<String>();


        for (int i = 0; i < listAllSongs.size(); i++)
        {
            Pair<String, String>  songProps = listAllSongs.get(i).first;

            songName = songProps.first;
            songPath = songProps.second;

            picsPathsToSong = listAllSongs.get(i).second;

            songName = FixSongName(songName);

            ListItemSong item = new ListItemSong(songName, "Artist of song " + (i + 1), "All Songs");  // TODO: add album

            // Done in bind items in adaptor
//            LoadMusicMediaWithSong(songPath);
//            try
//            {
//                Uri uri = Uri.parse(songPath);
//                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
//                if (mediaPlayer!=null)
//                {
//                    duration = mediaPlayer.getDuration();
//                    item.setDuration(duration);
//                }
//                //mediaPlayer.stop();
//                //mediaPlayer.release();
//                //mediaPlayer = null;
//            }
//            catch (Exception e)
//            {
//                Log.e("FillListAllSongs - ", e.getMessage());
//            }

            item.setSongPath(songPath);
            //item.setResourceID(resourceId);
            item.setYear("Year " + String.valueOf(2000 + i + 1));   // TODO: get year


            // Just for the small pic in list items
            item.setImageItem(new ImageView(this));
            if (picsPathsToSong.size()>0)
            {
                picPath = picsPathsToSong.get(0);
                item.getPicsToSongPathsArray().add(picPath);
                Bitmap bitmap = ConvertPictureFileToDrawable(picPath);
                item.getImageItem().setImageBitmap(bitmap);
            }
            else
            {
                item.getImageItem().setImageDrawable(this.getDrawable(R.drawable.music1));
            }


            if (picsPathsToSong.size()>1)
            {
                picPath = picsPathsToSong.get(1);
                item.getPicsToSongPathsArray().add(picPath);
            }
            if (picsPathsToSong.size()>2)
            {
                picPath = picsPathsToSong.get(2);
                item.getPicsToSongPathsArray().add(picPath);
            }

            ListItemsRecycler.add(item);
            ListItemSimple.add(songName);
        }

        keepListItemsRecyclerAllSongs = ListItemsRecycler;

    }

    private void FillListFolderMode()
    {
        ArrayList<String>   picsPathsToSong = new ArrayList<String>();
        ImageView           imageView;
        String              songName;
        String              songPath;
        String              picPath;
        int                 duration;
        boolean             isDirectory;
        ListItemSong        item;
        List<ArrayMap<String, String>> sortedList;
        ArrayMap<String, String> sortedArray;



        ListItemsRecycler = new ArrayList<ListItemSong>();
        ListItemSimple = new ArrayList<String>();

        item = new ListItemSong("...", "", "");
        item.setImageItem(new ImageView(this));
        ListItemsRecycler.add(item);
        ListItemSimple.add("...");

        SortFiles sortFiles = new SortFiles();
        sortedList = sortFiles.Sort(ListItemSimpleFoldersFullPath);


        for (int i = 0; i < sortedList.size(); i++)
        {
            sortedArray = (ArrayMap<String, String>)sortedList.get(i);

            songName = (String)sortedArray.keyAt(0);
            songPath = (String)sortedArray.valueAt(0);

            //songName = (String)ListItemSimpleFoldersFullPath.keyAt(i);
            //songPath = (String)ListItemSimpleFoldersFullPath.valueAt(i);

            picsPathsToSong = new ArrayList<String>();

            if (picsMap.containsKey(songPath))
            {
                ArrayList<String>  files = (ArrayList<String>) picsMap.get(songPath);
                picsPathsToSong = files;
            }
            else
            {
                // Not found specific song picture, try it's parent folder
                String filePath = songPath.substring(0, songPath.lastIndexOf(songName));
                if (picsMap.containsKey(filePath))
                {
                    ArrayList<String>  files = (ArrayList<String>) picsMap.get(filePath);
                    picsPathsToSong = files;
                }
            }

            String filesCount="Artist of song " + String.valueOf(i + 1);
            isDirectory = false;
            if (pathMap.containsKey(songPath.toLowerCase()))
            {
                File file = new File(songPath);
                if (file.exists() && file.isDirectory())
                {
                    isDirectory=true;
                    filesCount = String.valueOf(((ArrayList<File>)pathMap.get(songPath)).size()) + " Items";
                }
            }

            songName = FixSongName(songName);

            if (ListFolderName==null)
            {
                ListFolderName = "Folders Mode";
            }

            // Add the item
            item = new ListItemSong(songName, filesCount, ListFolderName);

            item.setSongPath(songPath);
            //item.setResourceID(resourceId);
            item.setYear("Year " + String.valueOf(2000 + i + 1));   // TODO: get year


            // Just for the small pic in list items
            item.setImageItem(new ImageView(this));
            if (picsPathsToSong.size()>0 && !isDirectory)
            {
                picPath = picsPathsToSong.get(0);
                item.getPicsToSongPathsArray().add(picPath);
                Bitmap bitmap = ConvertPictureFileToDrawable(picPath);
                item.getImageItem().setImageBitmap(bitmap);
            }
            else
            {
                if (isDirectory)
                {
                    item.getImageItem().setImageDrawable(getDrawable(R.drawable.pic_folder));
                }
                else
                {
                    item.getImageItem().setImageDrawable(this.getDrawable(R.drawable.defualt_song_pic));
                }
            }


            if (picsPathsToSong.size()>1)
            {
                picPath = picsPathsToSong.get(1);
                item.getPicsToSongPathsArray().add(picPath);
            }
            if (picsPathsToSong.size()>2)
            {
                picPath = picsPathsToSong.get(2);
                item.getPicsToSongPathsArray().add(picPath);
            }

            ListItemsRecycler.add(item);
            ListItemSimple.add(songName);
        }

    }


    // Collect all files from started folder name (recursive) with specific extention
    // songsMap - Key: Song name (without path), Value: Song path & name
    // picsMap  - Key: Song Path (without song name), Value: ArrayList of all pics names (with full path) for a song in same folder of the song file
    private File[] GetFolderFilesSongPictsMap(String path, String fileExtentionToSearch, boolean searchInFolders)
    {
        File file;
        File[] files = new File[0];
        File directory;
        //static ArrayList<File>  foundfiles
        String[]   multiSearchValuse = new String[0];
        String  filePath;
        String fileName;
        ArrayList<File>  filesToFolder = new ArrayList<File>();


        fileExtentionToSearch = fileExtentionToSearch.toLowerCase();

        directory = new File(path);

        if (!directory.exists())
        {
            return files;
        }

        files = directory.listFiles();

        if (files == null)
        {
            return files;
        }


//        if (fileExtentionToSearch.contains(";"))
//        {
//            multiSearchValuse = fileExtentionToSearch.split(";");
//            fileExtentionToSearch = multiSearchValuse[0];
//        }

        for (int i = 0; i < files.length; i++)
        {
            file = files[i];

            if (searchInFolders && file.isDirectory())
            {
                GetFolderFilesSongPictsMap(file.getPath(), fileExtentionToSearch, searchInFolders);
            }
            else
            {
                filePath = file.getAbsolutePath().toLowerCase();
                fileName = file.getName().toLowerCase();
                if (fileName.endsWith(fileExtentionToSearch))
                {
                    // Erase the extension '.mp3'
                    fileName = fileName.substring(0, fileName.lastIndexOf(fileExtentionToSearch));

                    // Key: Song name (without path), Value: Song path & name
                    songsMap.put(fileName, filePath);

                    // Song Path only - Erase song name
                    filePath = filePath.substring(0, filePath.lastIndexOf(fileName));

                    // All Files/Folders that contain 'mp3' files - Key: Full path, Value: ArraList of all Files in folder
                    if (!pathMap.containsKey(filePath))
                    {
                        // Create the array of files
                        filesToFolder = new ArrayList<File>();
                        filesToFolder.add(file);
                        pathMap.put(filePath, filesToFolder);
                    }
                    else
                    {
                        // Update exist array with 1 more file
                        filesToFolder = (ArrayList<File>) pathMap.get(filePath);
                        filesToFolder.add(file);
                        pathMap.setValueAt(pathMap.indexOfKey(filePath), filesToFolder);
                    }

                    // Get all pictures files in song path
                    // Key: Song Path (without song name), Value: ArrayList of all pics names (with full path) for a song in same folder of the song file
                    if (!picsMap.containsKey(filePath))
                    {
                        songsFiles = new ArrayList<File>();
                        // Get all pictures files in song path
                        GetFolderFiles(filePath, ".jpg", false);
                        ArrayList<File> picsFilesInFolder = songsFiles;
                        ArrayList<String> picsNamesInFolder = new ArrayList<String>();
                        for (int picsIndex = 0; picsIndex < picsFilesInFolder.size(); picsIndex++)
                        {
                            picsNamesInFolder.add(picsFilesInFolder.get(picsIndex).getPath());
                        }
                        if (picsFilesInFolder.size() > 0) {
                            picsMap.put(filePath, picsNamesInFolder);
                        }
                    }
                }
            }
        }

        return files;
    }

    private File[] GetFolderFiles(String path, String fileExtentionToSearch, boolean searchInFolders)
    {
        File file;
        File[] files;
        File directory;
        //static ArrayList<File>  foundfiles
        String[]   multiSearchValuse = new String[0];


        fileExtentionToSearch = fileExtentionToSearch.toLowerCase();

        directory = new File(path);

        files = directory.listFiles();

        if (files == null)
        {
            return files;
        }

//        if (fileExtentionToSearch.contains(";"))
//        {
//            multiSearchValuse = fileExtentionToSearch.split(";");
//            fileExtentionToSearch = multiSearchValuse[0];
//        }

        for (int i = 0; i < files.length; i++)
        {
            file = files[i];

            if (searchInFolders && file.isDirectory())
            {
                GetFolderFiles(file.getPath(), fileExtentionToSearch, searchInFolders);
            }
            else if (file.getName().toLowerCase().endsWith(fileExtentionToSearch))
            {
                songsFiles.add(file);
            }
        }


//        files = directory.listFiles(new FileFilter()
//        {
//            //public final String ext=fielExantion;
//            @Override
//            public boolean accept(File file)
//            {
//                if (file.isDirectory())
//                {
//                    GetFolderFiles(file.getPath());
//                }
//                else
//                {
//                    if (file.getName().toUpperCase().endsWith(".JPG"))
//                    {
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });

//        if (files != null)
//        {
//            for (int i = 0; i < files.length; i++)
//            {
//                songsFiles.add(files[i]);
//            }
//        }

        return files;
    }

    private File[] GetFolderFilesMap(String path, String fileExtentionToSearch, boolean searchInFolders)
    {
        File        file;
        File[]      files;
        File        directory;
        //static ArrayList<File>  foundfiles
        String[]   multiSearchValuse = new String[0];
        String      fileName;
        String      filePath;


        fileExtentionToSearch = fileExtentionToSearch.toLowerCase();

        directory = new File(path);

        files = directory.listFiles();

        if (files == null)
        {
            return files;
        }

//        if (fileExtentionToSearch.contains(";"))
//        {
//            multiSearchValuse = fileExtentionToSearch.split(";");
//            fileExtentionToSearch = multiSearchValuse[0];
//        }

        for (int i = 0; i < files.length; i++)
        {
            file = files[i];

            filePath = file.getAbsolutePath().toLowerCase();
            fileName = file.getName().toLowerCase();

            if (searchInFolders && file.isDirectory())
            {
                GetFolderFilesMap(filePath, fileExtentionToSearch, searchInFolders);
            }
            else if (fileName.endsWith(fileExtentionToSearch))
            {
                // Key: Song name (without path), Value: Song path & name
                songsMap.put(fileName, filePath);
            }
        }

        return files;
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

    // Fill by Resources files
    private void FillListAllSongByResources()
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
                //Toast.makeText(ActivityMusic.this, "FillListAllSongs - Pictures to Song:  " + getDrawable(resourceId).toString(), Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(ActivityMusic.this, "FillListAllSongs - No Pictures to Song:  " + fileName, Toast.LENGTH_LONG).show();
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

    public static Bitmap ConvertPictureFileToDrawable(String picPath)
    {
        Bitmap resultBitmap = null;

        File imgFile = new File(picPath);

        if (imgFile.exists())
        {
            resultBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            //CompressPictureFile(resultBitmap ,picPath);

            //ImageView myImage = (ImageView) findViewById(R.id.imageviewTest);
            //myImage.setImageBitmap(resultBitmap);
        }

        return resultBitmap;
    }

    public void CompressPictureFile(Bitmap bitmap, String picPath)
    {
        File imageFile = new File(picPath);
        FileOutputStream fileOutputStream = null;

        try
        {
            fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
        }
        catch (IOException e)
        {
            Log.e("app", e.getMessage());
        }
    }

    boolean saveBitmapToFile(File dir, String fileName, Bitmap bm, Bitmap.CompressFormat format, int quality)
    {

        File imageFile = new File(dir, fileName);

        FileOutputStream fos = null;

        try
        {
            fos = new FileOutputStream(imageFile);

            bm.compress(format,quality,fos);

            fos.close();

            return true;
        }
        catch (IOException e)
        {
            Log.e("app",e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return false;

    }

    private void RequestPermissions()
    {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Permission READ_EXTERNAL_STORAGE is Not OK", Toast.LENGTH_SHORT).show();
            //return;
        }
        else
        {
            // Have to ask permmissions
        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            //Toast.makeText(this, "Permission WRITE_CALENDAR not granted", Toast.LENGTH_SHORT).show();
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }
            else
            {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_STORAGE);

                // PERMISSIONS_REQUEST_READ_STORAGE is an app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else
        {
            // Permission has already been granted
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String  permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_READ_STORAGE:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageAvailable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            return true;
        }
        return false;
    }


    private void setUpUIControls()
    {

        btnList = (Button) findViewById(R.id.btnList);
        btnFolders = (Button) findViewById(R.id.btnFolders);
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
        //RelativeLayout relativeLayout = findViewById(R.id.layoutRel);

        btnPlay.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnList.setOnClickListener(this);
        btnFolders.setOnClickListener(this);

        //imgLine.bringToFront();   // crash
        imgLine.setVisibility(View.INVISIBLE);  // TODO: Maybe to delete
        lblSongArtist.bringToFront();
        lblSongName.bringToFront();
        lblAlbum.bringToFront();
        lblPosLeft.bringToFront();
        lblPosNow.bringToFront();

        mediaPlayer = new MediaPlayer();

        barSeek = (SeekBar) findViewById(R.id.barSeek);
        barSeek.bringToFront();
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

    }

    public int LoadMusicMediaWithSong(String songPath)
    {
        int result=0;

        try
        {
            MusicPause();

            Uri uri = Uri.parse(songPath);
            mediaPlayer = MediaPlayer.create(this, uri);

            if (mediaPlayer != null)
            {
                result = ActivityMusic.mediaPlayer.getDuration();
                // Set the Song props - Name, Artist, Album, Duration
                mediaPlayer.seekTo(0);
                barSeek.setMax(mediaPlayer.getDuration());
                barSeek.setProgress(0);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer)
                    {
                        PlaySongNext();
                    }
                });

                setSongControls(ListPositionIndex);

                MusicPlay();

            }
        }
        catch (Exception e)
        {
            System.out.println("Error while load a song into Media player.. \n" + e.getMessage());
        }

        return result;
    }

    private void LoadSongIntoPlayer(int listPositionIndex)
    {
        int resourceID;




        try
        {
            MusicPause();

            Uri uri = Uri.parse(ListItemsRecycler.get(listPositionIndex).getSongPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

            if (mediaPlayer != null)
            {
                //resourceID = ListItemsRecycler.get(listPositionIndex).getResourceID();
                //mediaPlayer = MediaPlayer.create(getApplicationContext(), resourceID);  //listItems.get(listPositionIndex).getResourceID()
                mediaPlayer.seekTo(0);
                barSeek.setMax(mediaPlayer.getDuration());
                barSeek.setProgress(0);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        PlaySongNext();
                    }
                });

            }
            // Set the Song props - Name, Artist, Album, Duration
            setSongControls(listPositionIndex);

            MusicPlay();
        }
        catch (Exception e)
        {
            System.out.println("Error while load a song into Media player.. \n" + e.getMessage());
        }

    }

    // Set the Song props - Name, Artist, Album, Duration
    private void setSongControls(int listPositionIndex)
    {
        Drawable drawable;
        Bitmap bitmap;


        ListItemSong item = ListItemsRecycler.get(listPositionIndex);

        lblSongName.setText(item.getSongName());
        lblSongArtist.setText(item.getArtist());
        lblAlbum.setText(item.getAlbum());

        //item.setDuration(mediaPlayer.getDuration());

        if (item.getPicsToSongPathsArray().size()>0)
        {
            bitmap = ConvertPictureFileToDrawable(item.getPicsToSongPathsArray().get(0));
            imgSongArtist1.setImageBitmap(bitmap);
            //FitPictureDrawableToScreenSize(imgSongArtist1);
            //drawable = this.getDrawable(item.getPicsToSongResIDsArray().get(0));
            //imgSongArtist1.setImageDrawable(drawable);    // Equal to prop 'app:srcCompat="@drawable/meir1"'
            //imgSongArtist1.setBackground(drawable);       // Smash the picture and loos proportion
        }
        else
        {
            imgSongArtist1.setImageDrawable(getDrawable(R.drawable.default1));
        }

        if (item.getPicsToSongPathsArray().size()>1)
        {
            bitmap = ConvertPictureFileToDrawable(item.getPicsToSongPathsArray().get(1));
            imgSongArtist2.setImageBitmap(bitmap);
            //FitPictureDrawableToScreenSize(imgSongArtist2);
            //drawable = this.getDrawable(item.getPicsToSongResIDsArray().get(1));
            //imgSongArtist2.setImageDrawable(drawable);    // Equal to prop 'app:srcCompat="@drawable/meir1"'
            //imgSongArtist2.setBackground(drawable);       // Smash the picture and loos proportion
        }
        else
        {
            imgSongArtist2.setImageDrawable(getDrawable(R.drawable.default2));
        }


        if (item.getPicsToSongPathsArray().size()>2)
        {
            bitmap = ConvertPictureFileToDrawable(item.getPicsToSongPathsArray().get(2));
            imgSongArtist2.setImageBitmap(bitmap);
            //FitPictureDrawableToScreenSize(imgSongArtist3);
            //drawable = this.getDrawable(item.getPicsToSongResIDsArray().get(2));
            //imgSongArtist3.setImageDrawable(drawable);    // Equal to prop 'app:srcCompat="@drawable/meir1"'
            //imgSongArtist3.setBackground(drawable);       // Smash the picture and loos proportion
        }
        else
        {
            imgSongArtist3.setImageDrawable(getDrawable(R.drawable.default3));
        }

    }

    private void FitPictureDrawableToScreenSize(ImageView imageView)
    {
        int widthScreen = (int) (this.getWindow().getDecorView().getWidth() * 0.9);
        int heightScreen = (int) (this.getWindow().getDecorView().getHeight() * 0.9);

        if ((imageView.getDrawable().getIntrinsicWidth() < widthScreen * 1.0) ||
            (imageView.getDrawable().getIntrinsicHeight() < heightScreen))  // imageView.getWidth()
        {
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        else
            {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        //imgSongArtist1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //Toast.makeText(this, "Width: "+String.valueOf(drawable.getIntrinsicWidth()) +"  Height: "+ String.valueOf(drawable.getIntrinsicHeight()), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Width: "+String.valueOf(imgSongArtist1.getWidth()) +"  Height: "+ String.valueOf(imgSongArtist1.getHeight()), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Screen Width: "+String.valueOf(widthScreen) +"  Screen Height: "+ String.valueOf(heightScreen), Toast.LENGTH_SHORT).show();
    }

    private void updateProgressControls()
    {
        int currentPos = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();

        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        lblPosNow.setText(dateFormat.format(new Date(currentPos)));
        lblPosLeft.setText(dateFormat.format(new Date(duration)));
        //lblPosLeft.setText(dateFormat.format(new Date(duration - currentPos)));
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
                PlaySongNext();
                break;

            case R.id.btnPrev:
                PlaySongPrev();
                break;

            case R.id.btnList:
                OpenSongListView();
                break;

            case R.id.btnFolders:
                OpenFoldersListView();
                break;

            case R.id.btnBack:
                goBack();
                break;

        }
    }

    private void PlaySongNext()
    {
        if (ListPositionIndex < ListItemsRecycler.size()-1)
        {
            ListPositionIndex++;
            LoadSongIntoPlayer(ListPositionIndex);
        }

        //seekMusic(5000);
    }

    private void PlaySongPrev()
    {
        if (ListPositionIndex < ListItemsRecycler.size() && ListPositionIndex>0)
        {
            ListPositionIndex--;
            LoadSongIntoPlayer(ListPositionIndex);
        }
        //seekMusic(-5000);
    }

    // Open List view with songs list
    private void OpenFoldersListView()
    {

        // Fill the main Folders names without full path, just the file/folder name
        FillListWithFolderNames();

        Intent intentListView = new Intent(ActivityMusic.this, ActivitySongList.class);

        // Here we choose witch List tyle will be shown
        intentListView.putExtra("ListMode", 1);     //ShowListModeEn.SimpleView);
        intentListView.putStringArrayListExtra("ListItems", ListItemSimpleFolders);

        ActivitySongList.setOnListRecyclerItemClick(new PersonalEvents.OnRecyclerViewItemClick()
        {
            @Override
            public void setOnRecyclerViewItemPressed(int cardViewPressedResID, int listPositionIndex)
            {
                ListPositionIndex = listPositionIndex;
                String selectedItemText = ListItemsRecycler.get(listPositionIndex).getSongName();
                ShowFilesToFolder(selectedItemText);
                return;
            }
        });
        ActivitySongList.setOnListSimpleItemClick(new PersonalEvents.OnListViewItemClick()
        {
            @Override
            public void setOnListViewItemPressed(int listPositionIndex, String selectedItemText)
            {
                ListPositionIndex = listPositionIndex;
                ShowFilesToFolder(selectedItemText);
                return;
            }
        });

        // Fill songs files
        FillListFolderMode();

        ActivitySongList.ListItemsRecycler = ListItemsRecycler;
        ActivitySongList.ListItemSimple = ListItemSimpleFolders;

        startActivityForResult(intentListView, REQUEST_CODE_FOLDERLIST_RECYCLER);
    }

    private void ShowFilesToFolder(String folderName)
    {
        File  path;
        String pathFull="";
        String songFile;



        // Choosed a file
        if (folderName.equals("...")==false)
        {
            // Keep the result files & folders. Key: Folder name - without the full folder path, Value: full path - that shown when peress 'Folder' button
            ListFolderName = folderName;
            folderName = folderName.toLowerCase();
            if (ListItemSimpleFoldersFullPath.containsKey(folderName))
            {
                // It's a folder
                pathFull = (String) ListItemSimpleFoldersFullPath.get(folderName);
                //ListPositionIndex = ListItemSimpleFoldersFullPath.indexOfKey(folderName);
            }
            else
            {
                pathFull = keepLastFolderWasChoosen + "/" + folderName;
            }
        }
        else
        {
            // Go Level Up - Fill All folders name only
            pathFull = keepLastFolderWasChoosen;
        }


        path = new File(pathFull);

        if (path.exists())
        {
            // It's a Song
            if (!path.isDirectory())
            {
                // Play the song
                //ListPositionIndex++;
                LoadSongIntoPlayer(ListPositionIndex);
                //LoadMusicMediaWithSong(pathFull);
                return;
            }
        }


        File[] files = path.listFiles();

//        ArrayList<File>  arrayFiles = (ArrayList<File>)pathMap.get(pathFull);
//        if (arrayFiles!=null && arrayFiles.size()>0)
//        {
//            File[] files = new File[arrayFiles.size()];
//            for (int i = 0; i < files.length; i++) {
//                files[i] = arrayFiles.get(i);
//            }
//        }

        keepLastFolderWasChoosen = path.getParentFile().getAbsolutePath();

        // Allways add LowerCase - The result files & folders (without the full folder path - just the folder name) that shown when peress 'Folder' button
        ListItemSimpleFoldersFullPath.clear();
        ListItemSimpleFolders = new ArrayList<String>();
        ListItemSimpleFolders.add("...");

        if (files!=null)
        {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                songFile = file.getName();
                pathFull = file.getAbsolutePath().toLowerCase();

                if (file.isFile()) {
                    if (songFile.toLowerCase().endsWith(".mp3")) {
                        // Erase the '.mp3' extention
                        songFile = songFile.substring(0, songFile.toLowerCase().lastIndexOf(".mp3"));
                        ListItemSimpleFolders.add(songFile);
                        ListItemSimpleFoldersFullPath.put(songFile.toLowerCase(), pathFull);
                    }
                } else {
                    // It's a Folder
                    pathFull += "/";
                    ListItemSimpleFolders.add(songFile);
                    ListItemSimpleFoldersFullPath.put(songFile.toLowerCase(), pathFull);
                }
            }
        }

        Intent intentListView = new Intent(ActivityMusic.this, ActivitySongList.class);

        // Here we choose witch List tyle will be shown
        intentListView.putExtra("ListMode", 1);     //ShowListModeEn.SimpleView);
        intentListView.putStringArrayListExtra("ListItems", ListItemSimpleFolders);

        // Fill songs files into 'ListItemsRecycler'
        FillListFolderMode();

        ActivitySongList.ListItemsRecycler = ListItemsRecycler;
        ActivitySongList.ListItemSimple = ListItemSimpleFolders;

        startActivityForResult(intentListView, REQUEST_CODE_FOLDERLIST_RECYCLER);
    }

    // Fill just 1 time the main Folders names without full path, just the file/folder name
    private void FillListWithFolderNames()
    {
        String path;
        String pathShort;
        String pathFull;



        // Keep the result files & folders. Key: Folder name - without the full folder path, Value: full path - that shown when peress 'Folder' button
        ListItemSimpleFoldersFullPath = new ArrayMap<String, String>();
        // The result files & folders (without the full folder path - just the folder name) - that shown when peress 'Folder' button
        ListItemSimpleFolders = new ArrayList<String>();

        for (int i=0; i<pathMap.size(); i++)
        {
            pathShort =(String)pathMap.keyAt(i);
            pathFull = pathShort;

            if (pathShort.substring(pathShort.length()-1).equals("/"))
            {
                pathShort = pathShort.substring(0, pathShort.length()-1);
            }
            // Take just the Name, without full path
            pathShort = pathShort.substring(pathShort.lastIndexOf("/")+1);

            ListItemSimpleFoldersFullPath.put(pathShort, pathFull);
            ListItemSimpleFolders.add(pathShort);
        }
    }

    // Open List view with songs list
    private void OpenSongListView()
    {
        int listMode;

        ListItemsRecycler = keepListItemsRecyclerAllSongs;

        Intent intentListView = new Intent(ActivityMusic.this, ActivitySongList.class);

        listMode= 1;

        if (isFirstSongListLoad==false)
        {
            isFirstSongListLoad=true;
        }

        // Here we choose witch List tyle will be shown
        intentListView.putExtra("ListMode", listMode);     //ShowListModeEn.RecyclerView);
        intentListView.putStringArrayListExtra("ListItems", ListItemSimple);
        intentListView.putExtra("FirstLoad", isFirstSongListLoad);     //ShowListModeEn.RecyclerView);

        ActivitySongList.ListItemsRecycler = ListItemsRecycler;     // Can't send it thru Intent, so must be static
        //ActivitySongList.ListItemSimple = ListItemSimple;                 // TODO: Maybe passe it like this instead of thrue Intent

        if (listMode==1)
        {
            ActivitySongList.setOnListRecyclerItemClick(new PersonalEvents.OnRecyclerViewItemClick()
            {
                @Override
                public void setOnRecyclerViewItemPressed(int cardViewPressedResID, int listPositionIndex)
                {
                    ListPositionIndex = listPositionIndex;
                    int resID = ListItemsRecycler.get(listPositionIndex).getResourceID();
                    LoadSongIntoPlayer(listPositionIndex);
                }
            });
        }
        else if (listMode==2)
        {
            // Assigned it direct because it's Static var
            ActivitySongList.setOnListSimpleItemClick(new PersonalEvents.OnListViewItemClick()
            {
                @Override
                public void setOnListViewItemPressed(int listPositionIndex, String selectedItemText)
                {
                    ListPositionIndex = listPositionIndex;
                    LoadSongIntoPlayer(listPositionIndex);
                }
            });
        }

        //startActivity(intentListView);
        startActivityForResult(intentListView, REQUEST_CODE_SONGLIST_RECYCLER);

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

    public void updateThread()
    {
        thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    while (mediaPlayer != null && mediaPlayer.isPlaying())
                    {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
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
    protected void onDestroy()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Toast.makeText(ActivityMusic.this, "Destroing Media Player control", Toast.LENGTH_SHORT).show();
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
        onDestroy();

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

                case REQUEST_CODE_FOLDERLIST_RECYCLER:
                    String dataReturned = data.getStringExtra("returnedData");
                    ShowFilesToFolder(ListFolderName);
                    break;
            }
        }
        else
        {
//            if (requestCode==REQUEST_CODE_FOLDERLIST_RECYCLER)
//            {
//                ShowFilesToFolder(ListFolderName);
//            }
//            //Toast.makeText(MainActivity.this, "No Data", Toast.LENGTH_LONG).show();
//            return;
        }

    }

    // Set Upper case Letters in begining
    private String FixSongName(String songName)
    {
        if (chars.contains(songName.substring(0, 1)))
        {
            songName = songName.substring(0, 1).toUpperCase() + songName.substring(1);
        }
        if (songName.contains("- "))
        {
            int pos = songName.indexOf("- ");
            if (pos+3<songName.length()-1)
            {
                songName = songName.substring(0, pos + 2) + songName.substring(pos + 2, pos + 3).toUpperCase() + songName.substring(pos + 3);
            }
        }

        return songName;
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


//drawable.setLevel(5000);
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
//imgSongArtist3.setMinimumWidth(widthScreen);
//imgSongArtist3.setMaxWidth(widthScreen);
//imgSongArtist3.setMinimumHeight(heightScreen);
//imgSongArtist3.setMaxHeight(heightScreen);


//imgSongArtist1.setScaleType(ImageView.ScaleType.CENTER_CROP);
//Toast.makeText(this, "Width: "+String.valueOf(drawable.getIntrinsicWidth()) +"  Height: "+ String.valueOf(drawable.getIntrinsicHeight()), Toast.LENGTH_SHORT).show();
//Toast.makeText(this, "Width: "+String.valueOf(imgSongArtist1.getWidth()) +"  Height: "+ String.valueOf(imgSongArtist1.getHeight()), Toast.LENGTH_SHORT).show();