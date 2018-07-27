package com.example.meirh.mhorgananizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import Adapter.AdapterBaseList;
import Adapter.AdapterSong;
import Model.ListItemSong;
import Utills.PersonalEvents;



public class ActivitySongList extends AppCompatActivity //implements Parcelable
{

    private ListView                    listControlSimple;
    public  static ArrayList<String>    ListItemSimple;
    private AdapterBaseList             adapterListSimple;

    private RecyclerView                listControlRecycler;
    public  static List<ListItemSong>   ListItemsRecycler;    // ArrayList<ListItemSong>
    private List<ListItemSong>          keepListItemsRecycler;
    private AdapterSong                 adapterListRecycler;  // RecyclerView.Adapter

    private ImageView                   imgSearch;
    private EditText                    txtSearch;
    private TableRow                    tblSearch;

    private int  ListMode;
    //private ActivityMusic.ShowListModeEn  ListMode;
    private boolean                     isFirstSongListLoad;
    private static int                  keepLastItemIndexPressed;


    // The listener must implement the events interface and passes messages up to the parent.
    public static PersonalEvents.OnListViewItemClick        ListenerSimple;

    // The listener must implement the events interface and passes messages up to the parent.
    public static PersonalEvents.OnRecyclerViewItemClick    ListenerRecycler;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        GetIntent();

        setUpUIControls();

        setControlsByListMode(ListMode);

        keepListItemsRecycler = ListItemsRecycler;
    }


    private void setUpUIControls()
    {
        // TODO: Done here, just because we need Initialing 'AdapterBaseList' object for the next line Event 'setOnSongClick'
        listControlRecycler = (RecyclerView) findViewById(R.id.listControlRecycler);
        listControlRecycler.setHasFixedSize(true);
        listControlRecycler.setLayoutManager(new LinearLayoutManager(this));


        // TODO: Done here, just because we need Initialing 'AdapterBaseList' object for the next line Event 'setOnListViewItemClick'
        listControlSimple = (ListView) findViewById(R.id.listControlSimple);

        txtSearch = (EditText)findViewById(R.id.txtSearch);
        txtSearch.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent)
            {
                return false;
            }
        });
        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                return false;
            }
        });
        txtSearch.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                SearchWord(txtSearch.getText().toString()); //, keyCode);
                return false;
            }
        });

        imgSearch = (ImageView)findViewById(R.id.imgSearch);
        imgSearch.bringToFront();
        tblSearch = (TableRow) findViewById(R.id.tblSearch);

        imgSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SearchMode((tblSearch.getVisibility()==View.VISIBLE));
            }
        });

    }

    private void SearchMode(boolean mode)
    {
        if (mode)
        {
            adapterListRecycler = new AdapterSong(this, keepListItemsRecycler);
            listControlRecycler.setAdapter(adapterListRecycler);
            tblSearch.setVisibility(View.INVISIBLE);
        }
        else
        {
            tblSearch.setVisibility(View.VISIBLE);
            txtSearch.setText("");
            //txtSearch.setFocusable(View.FOCUSABLE);
        }
    }

    private void SearchWord(String wordToSearch)    //, int keyCode)
    {
        boolean found;
        ListItemSong listItemSong;
        List<ListItemSong> addListItemsRecycler = new ArrayList<ListItemSong>();

        //wordToSearch += String.valueOf((char)keyCode);

        if (wordToSearch.trim().equals(""))
        {
            return;
        }

        wordToSearch = wordToSearch.toLowerCase();

        for (int i=0; i<ListItemsRecycler.size(); i++)
        {
            listItemSong = ListItemsRecycler.get(i);
            found = listItemSong.getSongName().toLowerCase().contains(wordToSearch);
            if (found)
            {
                addListItemsRecycler.add(listItemSong);
            }

            if (found && listItemSong.getArtist().toLowerCase().contains(wordToSearch))
            {
                found = listItemSong.getArtist().toLowerCase().contains(wordToSearch);
                if (found)
                {
                    addListItemsRecycler.add(listItemSong);
                }
            }
        }

        adapterListRecycler = new AdapterSong(this, addListItemsRecycler);
        listControlRecycler.setAdapter(adapterListRecycler);
    }

    // Assigned it direct because it's Static var
    // Assign the listener implementing events interface that will receive the events
    public static void setOnListSimpleItemClick(PersonalEvents.OnListViewItemClick listener)
    {
        ListenerSimple = listener;
    }

    // Assigned it direct because it's Static var
    // Assign the listener implementing events interface that will receive the events
    public static void setOnListRecyclerItemClick(PersonalEvents.OnRecyclerViewItemClick listener)
    {
        ListenerRecycler = listener;
    }

    private void GetIntent()
    {

        ListItemSimple = this.getIntent().getStringArrayListExtra("ListItems");

        ListMode = this.getIntent().getIntExtra("ListMode", 2);  //, ActivityMusic.ShowListModeEn.SimpleView.toString());
        //ListMode = ((ActivityMusic.ShowListModeEn) this.getIntent().getStringExtra("ListMode"));  //, ActivityMusic.ShowListModeEn.SimpleView.toString());
        isFirstSongListLoad = this.getIntent().getBooleanExtra("FirstLoad", true);
    }

    // Return from this view by Intent object
    private void goBack(int listPositionIndex, String selectedItemText)
    {
        String returnedData;

        Intent intent = getIntent();
        intent.putExtra("listPositionIndex",  listPositionIndex);
        intent.putExtra("selectedItemText", selectedItemText);

        returnedData = "Go back from list";
        intent.putExtra("returnedData",  returnedData);

        //Toast.makeText(this, "Come Back with ... " + returnedData, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK, intent);
        finish();
    }

    public int getListMode()
    {
        return ListMode;
    }

    public void setControlsByListMode(int listMode)   // ActivityMusic.ShowListModeEn
    {
        this.ListMode = listMode;

        if (this.ListMode==1)   //ActivityMusic.ShowListModeEn.RecyclerView)
        {
            adapterListRecycler = new AdapterSong(this, ListItemsRecycler);
            listControlRecycler.setAdapter(adapterListRecycler);

            if (adapterListRecycler == null)  // isFirstSongListLoad
            {
                //adapterListRecycler = new AdapterSong(this, ListItemsRecycler);
                //listControlRecycler.setAdapter(adapterListRecycler);
            }

            // Register the listener for this object
            adapterListRecycler.setOnSongClick(new PersonalEvents.OnRecyclerViewItemClick()
            {
                // Listen to event. wait here when the event invoked in child object.
                @Override
                public void setOnRecyclerViewItemPressed(int cardViewPressedResID, int listPositionIndex)
                {
                    //goBack(listPositionIndex, selectedItemText);      // TODO: return by Intent object
                    if (ListenerRecycler != null)
                    {
                        keepLastItemIndexPressed = listPositionIndex;
                        // Now let's fire listener here
                        ListenerRecycler.setOnRecyclerViewItemPressed(cardViewPressedResID, listPositionIndex);
                    }
                    finish();
                }
            });

            if (keepLastItemIndexPressed>0)
            {
                listControlRecycler.scrollToPosition(keepLastItemIndexPressed);
            }
            listControlRecycler.setVisibility(View.VISIBLE);
            listControlSimple.setVisibility(View.INVISIBLE);
        }
        else if (this.ListMode==2)  //ActivityMusic.ShowListModeEn.SimpleView)
        {
            adapterListSimple = new AdapterBaseList(this, ListItemSimple, listControlSimple);
            adapterListSimple.LayoutCardResourceID = R.layout.list_row;
            adapterListSimple.LayoutControlToShowResourceID = R.id.lblListRow;

            // Register the listener for this object
            adapterListSimple.setOnListViewItemClick(new PersonalEvents.OnListViewItemClick()
            {
                @Override
                public void setOnListViewItemPressed(int listPositionIndex, String selectedItemText)
                {
                    //goBack(listPositionIndex, selectedItemText);      // TODO: return by Intent object

                    if (ListenerSimple != null)
                    {
                        keepLastItemIndexPressed = listPositionIndex;
                        // Now let's fire listener here
                        ListenerSimple.setOnListViewItemPressed(listPositionIndex, selectedItemText);
                    }
                    finish();
                }
            });

            //adapterListSimple.setListItems(ListItemSimple);
            adapterListSimple.FillList();

            listControlRecycler.setVisibility(View.INVISIBLE);
            listControlSimple.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        //goBack();
    }



}
