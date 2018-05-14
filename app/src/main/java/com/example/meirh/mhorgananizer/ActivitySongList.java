package com.example.meirh.mhorgananizer;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import Model.ListItem;

public class ActivitySongList extends AppCompatActivity
{

    private ListView                listSongs;
    private ArrayAdapter<String>    listAdapter;
    private ArrayList<String>       ListItemArray;      //ListItem

    private Bundle                  extras;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        listSongs = (ListView) findViewById(R.id.listSongs);

//        ArrayList<?> mmm=savedInstanceState.getParcelableArrayList("ListItems");
//        ArrayList<String>  bbb = (ArrayList<String>)mmm;

        FillList();
    }

    private void FillList()
    {

        extras = this.getIntent().getBundleExtra("ListItems");
        //ArrayList<?> mmm=extras.getParcelableArrayList("ListItems");
        //ArrayList<ListItem>  bbb = (ArrayList<ListItem>)mmm;
        String[] items = extras.getStringArray("ListItems");

        ListItemArray = new ArrayList<String>();
        ListItemArray.addAll(Arrays.asList(items));

        //View view =inflater.inflate(R.layout.fragment_fail, container, false);

        //ArrayList<? extends Parcelable> aaa;
        //extras = getIntent().getExtras();
        //ArrayList<android.os.Parcelable> aaa = extras.getParcelableArrayList("ListItems");
        //Object[] bbb = aaa.toArray();
        //ListItemArray = (ArrayList<ListItem>)bbb;
        //Bundle data = this.getIntent().getBundleExtra("result.content");
        //ArrayList<ListItem> result = data.getParcelableArrayList("search.resultset");

        //Create and populate a List of planet names.
        String[] planets = new String[]{"Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"};

        ArrayList<String> planetList = new ArrayList<String>();
        planetList.addAll(Arrays.asList(planets));

//        if (ListItemArray!=null && ListItemArray.size()>0)
//        {
//            for (String item : ListItemArray)
//            {
//                planetList.add(item); // item.getName()
//            }
//        }

        // Create ArrayAdapter using the planet list.
        listAdapter = new ArrayAdapter<String>(this, R.layout.list_row, R.id.lblListRow, ListItemArray);       // planetList
        //listAdapter = new ArrayAdapter<String>(this, R.layout.list_row, planetList);

        // Add more planets. If you passed a String[] instead of a List<String>
        // into the ArrayAdapter constructor, you must not add more items.
        // Otherwise an exception will occur.
//        listAdapter.add("Ceres");
//        listAdapter.add("Pluto");
//        listAdapter.add("Haumea");
//        listAdapter.add("Makemake");
//        listAdapter.add("Eris");

        // Set the ArrayAdapter as the ListView's adapter.
        listSongs.setAdapter(listAdapter);
    }
}
