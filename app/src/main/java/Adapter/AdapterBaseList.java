package Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.meirh.mhorgananizer.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Model.ListItem;
import Utills.PersonalEvents;

/**
 * Created by meirh on 15/05/2018.
 */

public class AdapterBaseList
{

    private Context                     context;
    private ArrayList<String>           ListItems;

    private Bundle                      extras;

    private ListView                    ListViewControl;
    public int                          LayoutCardResourceID;
    public int                          LayoutControlToShowResourceID;

    // The listener must implement the events interface and passes messages up to the parent.
    private PersonalEvents.OnListViewItemClick AdapterListener;


    public AdapterBaseList(Context context, ArrayList<String> listItems, ListView listViewControl)
    {
        this.context = context;
        this.ListItems = listItems;
        this.ListViewControl = listViewControl;

        AdapterListener = null;

        ListViewControl.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3)
            {
                OnListClick(adapterView, view, position, arg3);
            }
        });

    }


    // Assign the listener implementing events interface that will receive the events
    public void setOnListViewItemClick(PersonalEvents.OnListViewItemClick listener)
    {
        this.AdapterListener = listener;
    }


    public void GetIntent(Activity view)
    {

        extras = view.getIntent().getBundleExtra("ListItems");

        ListItems = extras.getStringArrayList("ListItems");

        FillList();

        //String[] items = extras.getStringArray("ListItems");
        //ArrayList<?> mmm=extras.getParcelableArrayList("ListItems");
        //ArrayList<ListItem>  bbb = (ArrayList<ListItem>)mmm;
        //ListItemArray = new ArrayList<String>();
        //ListItemArray.addAll(Arrays.asList(items));
        //ArrayList<?> mmm=savedInstanceState.getParcelableArrayList("ListItems");
        //ArrayList<String>  bbb = (ArrayList<String>)mmm;
        //ArrayList<? extends Parcelable> aaa;
        //extras = getIntent().getExtras();
        //ArrayList<android.os.Parcelable> aaa = extras.getParcelableArrayList("ListItems");
        //Object[] bbb = aaa.toArray();
        //ListItemArray = (ArrayList<ListItem>)bbb;
        //Bundle data = this.getIntent().getBundleExtra("result.content");
        //ArrayList<ListItem> result = data.getParcelableArrayList("search.resultset");
        //extras = getIntent().getExtras();
        //ArrayList<android.os.Parcelable> aaa = extras.getParcelableArrayList("ListItems");
        //String[] items = extras.getStringArray("ListItems");
        //ArrayList<?> mmm=extras.getParcelableArrayList("ListItems");
        //ArrayList<ListItem>  bbb = (ArrayList<ListItem>)mmm;
    }

    public void FillList()
    {
        ArrayAdapter<String>   ListAdapter;


        // Create ArrayAdapter
        ListAdapter = new ArrayAdapter<String>(context, LayoutCardResourceID, LayoutControlToShowResourceID, this.ListItems);       // planetList
        //listAdapter = new ArrayAdapter<String>(context, R.layout.list_row, this.ListItems);

        // Set the ArrayAdapeter as the ListView's adapter.
        ListViewControl.setAdapter(ListAdapter);

        //View view =inflater.inflate(R.layout.fragment_fail, container, false);

        //ListItemArray = new ArrayList<String>();
        //ListItemArray.addAll(Arrays.asList(items));
        //ArrayList<? extends Parcelable> aaa;
        //Object[] bbb = aaa.toArray();
        //ListItemArray = (ArrayList<ListItem>)bbb;
    }

    private void OnListClick(AdapterView<?> adapterView, View view, int position, long arg3)
    {
        String itemValue;


        itemValue = (String)adapterView.getItemAtPosition(position);

        System.out.println("Item index: " + position + "   Item value: "+ itemValue);

        // TODO: set background color
        //(adapterView.getAdapter())findViewById(adapterView.getItemIdAtPosition(position));

        if (AdapterListener != null)
        {
            // Now let's fire listener here
            AdapterListener.setOnListViewItemPressed(position, itemValue);
        }

        //System.out.println("View name: "+view.getTransitionName());
        //adapterView.setSelection(position);
        //listFolders.getAdapter().getItem(position);
        //System.out.println("Item index: "+ adapterView.getSelectedItemPosition());   // +adapterView.getSelectedItem().toString()+"  "

    }

    public ArrayList<String> getListItems()
    {
        return ListItems;
    }

    public void setListItems(ArrayList<String> listItems)
    {
        ListItems = listItems;
    }

    private void ListFillExample()
    {
        //Create and populate a List of planet names.
        String[] planets = new String[]{"Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"};

        ArrayList<String> planetList = new ArrayList<String>();
        planetList.addAll(Arrays.asList(planets));

        // Create ArrayAdapter using the planet list.
        //ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, 10, 11, planetList);
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
        //ListControl.setAdapter(listAdapter);

    }
}
