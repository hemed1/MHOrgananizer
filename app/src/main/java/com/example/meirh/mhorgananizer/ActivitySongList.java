package com.example.meirh.mhorgananizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class ActivitySongList extends AppCompatActivity
{
    private ListView                listSongsFloat;
    //private ArrayAdapter<String>    listAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        listSongsFloat = (ListView) findViewById( R.id.listSongsFloat);

//        // Create and populate a List of planet names.
//        String[] planets = new String[] { "Mercury", "Venus", "Earth", "Mars",
//                                          "Jupiter", "Saturn", "Uranus", "Neptune"};
//
//        ArrayList<String> planetList = new ArrayList<String>() ;
//        planetList.addAll( Arrays.asList(planets) );
//
//        // Create ArrayAdapter using the planet list.
//        listAdapter = new ArrayAdapter<String>(this, R.layout.list_row, planetList);
//
//        // Add more planets. If you passed a String[] instead of a List<String>
//        // into the ArrayAdapter constructor, you must not add more items.
//        // Otherwise an exception will occur.
////        listAdapter.add( "Ceres" );
////        listAdapter.add( "Pluto" );
////        listAdapter.add( "Haumea" );
////        listAdapter.add( "Makemake" );
////        listAdapter.add( "Eris" );
//
//        // Set the ArrayAdapter as the ListView's adapter.
//        listSongsFloat.setAdapter( listAdapter );

    }
}
