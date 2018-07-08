package com.example.meirh.mhorgananizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
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
    private static AdapterBaseList      adapterListSimple;

    private static RecyclerView         listControlRecycler;
    public  static List<ListItemSong>   ListItemsRecycler;    // ArrayList<ListItemSong>
    private static AdapterSong          adapterListRecycler;  // RecyclerView.Adapter

    private int  ListMode;
    //private ActivityMusic.ShowListModeEn  ListMode;
    private boolean                     isFirstSongListLoad;


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

    }


    private void setUpUIControls()
    {
        // TODO: Done here, just because we need Initialing 'AdapterBaseList' object for the next line Event 'setOnSongClick'
        listControlRecycler = (RecyclerView) findViewById(R.id.listControlRecycler);
        listControlRecycler.setHasFixedSize(true);
        listControlRecycler.setLayoutManager(new LinearLayoutManager(this));


        // TODO: Done here, just because we need Initialing 'AdapterBaseList' object for the next line Event 'setOnListViewItemClick'
        listControlSimple = (ListView) findViewById(R.id.listControlSimple);


        setControlsByListMode(ListMode);

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
    private void ReturnedResult(int listPositionIndex, String selectedItemText)
    {
        Intent intent = getIntent();
        intent.putExtra("listPositionIndex",  listPositionIndex);
        intent.putExtra("selectedItemText", selectedItemText);

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
            else
            {
                //adapterListRecycler.notify();
                //adapterListRecycler.notifyAll();
                //listControlRecycler.invalidate();
                //adapterListRecycler.notifyItemRangeChanged(2, 2);
                //adapterListRecycler.notifyDataSetChanged();
                //adapterListRecycler = new AdapterSong(this, ListItemsRecycler);
                //listControlRecycler.setAdapter(adapterListRecycler);
                //adapterListRecycler.setListItems(ListItemsRecycler);
                //adapterListRecycler.notifyAll();
            }

            // Register the listener for this object
            adapterListRecycler.setOnSongClick(new PersonalEvents.OnRecyclerViewItemClick()
            {
                // Listen to event. wait here when the event invoked in child object.
                @Override
                public void setOnRecyclerViewItemPressed(int cardViewPressedResID, int listPositionIndex)
                {
                    //ReturnedResult(listPositionIndex, selectedItemText);      // TODO: return by Intent object
                    if (ListenerRecycler != null)
                    {
                        // Now let's fire listener here
                        ListenerRecycler.setOnRecyclerViewItemPressed(cardViewPressedResID, listPositionIndex);
                    }
                    finish();
                }
            });

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
                    //ReturnedResult(listPositionIndex, selectedItemText);      // TODO: return by Intent object

                    if (ListenerSimple != null)
                    {
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

}
