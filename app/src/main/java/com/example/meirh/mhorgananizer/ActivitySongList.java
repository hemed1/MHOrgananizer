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
    public static ArrayList<String>     ListItemSimple;
    private AdapterBaseList             adapterListSimple;

    private RecyclerView                listControlRecycler;
    public static List<ListItemSong>    ListItemsRecycler;    // ArrayList<ListItemSong>
    private AdapterSong                 adapterListRecycler;  // RecyclerView.Adapter

    private int                         ListMode;
    private Bundle                      extras;

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
        // Assigned direct because it's Static var
        //ListItemsRecycler = new ArrayList<ListItemSong>();                      // TODO: new List<ListItem>  //new List<ListItemSong>[];
        adapterListRecycler = new AdapterSong(this, ListItemsRecycler);

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


        // TODO: Done here, just because we need Initialing 'AdapterBaseList' object for the next line Event 'setOnListViewItemClick'
        listControlSimple = (ListView) findViewById(R.id.listControlSimple);
        adapterListSimple = new AdapterBaseList(this, ListItemSimple, listControlSimple);
        adapterListSimple.LayoutCardResourceID = R.layout.list_row;
        adapterListSimple.LayoutControlToShowResourceID = R.id.lblListRow;

        // Register the listener for this object
        adapterListSimple.setOnListViewItemClick(new PersonalEvents.OnListViewItemClick()
        {
            // Listen to event. wait here when the event invoked in child object.
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

        setListMode(ListMode);

        if (ListMode == 1)
        {
            // Fill Recycler list - Passed by 'Static' var, because cant pass a object thru Intent
            //adapterListRecycler = new AdapterSong(this, ListItemsRecycler);  // TODO:
            //adapterListRecycler.setListItems(ListItemsRecycler);
            listControlRecycler.setAdapter(adapterListRecycler);
        }
        else
        {
            System.out.println("List Simple count: "+ListItemSimple.size()+"  ListMode: " + String.valueOf(ListMode));
            //adapterListSimple.setListItems(`);
            adapterListSimple.FillList();
        }

    }

    // Assigned it direct because it's Static var
    // Assign the listener implementing events interface that will receive the events
    public void setOnListSimpleItemClick(PersonalEvents.OnListViewItemClick listener)
    {
        ListenerSimple = listener;
    }

    // Assigned it direct because it's Static var
    // Assign the listener implementing events interface that will receive the events
    public void setOnListRecyclerItemClick(PersonalEvents.OnRecyclerViewItemClick listener)
    {
        ListenerRecycler = listener;
    }

    private void GetIntent()
    {
        extras = this.getIntent().getBundleExtra("ListItems");

        ListItemSimple = extras.getStringArrayList("ListItems");
        ListMode = this.getIntent().getIntExtra("ListMode", 2);

        System.out.println("List Mode: "+ListMode);
        System.out.println("List Simple count: "+ListItemSimple.size());
        System.out.println("List Recycler count: "+String.valueOf(ListItemsRecycler.size())+"/n/n");

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

    public void setListMode(int listMode)
    {
        this.ListMode = listMode;

        if (this.ListMode==1)
        {
            listControlRecycler.setVisibility(View.VISIBLE);
            listControlSimple.setVisibility(View.INVISIBLE);
        }
        else
        {
            listControlRecycler.setVisibility(View.INVISIBLE);
            listControlSimple.setVisibility(View.VISIBLE);
        }

    }

}
