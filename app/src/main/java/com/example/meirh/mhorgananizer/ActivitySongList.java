package com.example.meirh.mhorgananizer;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import Adapter.AdapterBaseList;
import Model.ListItem;
import Utills.PersonalEvents;

public class ActivitySongList extends AppCompatActivity
{

    private ListView                listSongs;
    private ArrayList<String>       ListItemArray;
    private AdapterBaseList         adapterBaseList;

    private Bundle                  extras;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        listSongs = (ListView) findViewById(R.id.listSongs);

        GetIntent();

        // Register the listener for this object
        adapterBaseList.setOnListViewItemClick(new PersonalEvents.OnListViewItemClick()
        {
            // Listen to event. wait here when the event invoked in child object.
            @Override
            public void onListViewItemPressed(int listPositionIndex, String selectedItemText)
            {

            }
        });

    }


    private void GetIntent()
    {
        extras = this.getIntent().getBundleExtra("ListItems");

        ListItemArray = extras.getStringArrayList("ListItems");

        adapterBaseList = new AdapterBaseList(this, ListItemArray, listSongs);
        adapterBaseList.LayoutCardResourceID = R.layout.list_row;
        adapterBaseList.LayoutControlToShowResourceID = R.id.lblListRow;

        adapterBaseList.FillList();

    }
}
