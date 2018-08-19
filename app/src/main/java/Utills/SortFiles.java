package Utills;

//import android.support.v4.util.ArrayMap;
import android.support.v7.util.SortedList;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by meirh on 22/07/2018.
 */

public class SortFiles
{

    private List<ArrayMap<String, String>>  SortedList = new ArrayList<ArrayMap<String, String>>();


    public List<ArrayMap<String, String>> SortByValue(ArrayMap<String, String> unSortedArray)
    {
        SortedList = FillArray(unSortedArray);

        Collections.sort(SortedList, new ListMapComparator());

        //ArrayMap<String, String> result = FillResutl();

        return SortedList;
    }

    public List<ArrayMap<String, String>> SortByKey(ArrayMap<String, String> unSortedArray)
    {
        SortedList = FillArray(unSortedArray);

        Collections.sort(SortedList, new ListMapComparatorByKey());

        //ArrayMap<String, String> result = FillResutl();

        return SortedList;
    }

    private List<ArrayMap<String, String>> FillArray(ArrayMap<String, String> unSortedArray)
    {
        List<ArrayMap<String, String>> list = new ArrayList<ArrayMap<String, String>>();

        for (int i=0; i<unSortedArray.size(); i++)
        {
            ArrayMap<String, String> myMap1 = new ArrayMap<String, String>();
            myMap1.put(unSortedArray.keyAt(i), unSortedArray.valueAt(i));
            list.add(myMap1);
        }

//        ArrayMap<String, String> myMap1 = new ArrayMap<String, String>();
//        myMap1.put("name", "Josh");
//
//        ArrayMap<String, String> myMap2 = new ArrayMap<String, String>();
//        myMap2.put("family", "Anna");
//
//        ArrayMap<String, String> myMap3 = new ArrayMap<String, String>();
//        myMap3.put("aba", "Bernie");

//        list.add(myMap1);
//        list.add(myMap2);
//        list.add(myMap3);

//        unSortedArray = new ArrayMap<String, String>();
//
//        unSortedArray.put("C", "ccc");
//        unSortedArray.put("B", "bbb");
//        unSortedArray.put("A", "aaa");
//        unSortedArray.put("D", "ddd");
//        unSortedArray.put("AA", "aaaa");

        return list;
    }

    // After sort the list, fill back the original ArrayMap from list
    private ArrayMap<String, String> FillResutl()
    {
        ArrayMap<String, String> result = new ArrayMap<String, String>();

        for (int i=0; i<SortedList.size(); i++)
        {
            ArrayMap<String, String> sortedItem = (ArrayMap<String, String>)SortedList.get(i);

            String key = sortedItem.keySet().iterator().next();

            result.put(key, sortedItem.get(key));
        }

        return result;
    }

    public String ShowResults(List<ArrayMap<String, String>> sortedMap)
    {
        String  message = "";

        for (int i=0; i<sortedMap.size(); i++)
        {
            message += "Key: " + ((ArrayMap<String, String>)sortedMap.get(i)).keyAt(0) + "  Value: " + ((ArrayMap<String, String>)sortedMap.get(i)).valueAt(0) + "\n";
        }

        return message;
    }

    private class ListMapComparator implements Comparator
    {
        private int arrayIndex = -1;

        public int compare(Object obj1, Object obj2)
        {
            ArrayMap<String, String> test1 = (ArrayMap<String, String>) obj1;
            ArrayMap<String, String> test2 = (ArrayMap<String, String>) obj2;

            arrayIndex=0;       //arrayIndex++;
            return test1.valueAt(arrayIndex).compareTo(test2.valueAt(arrayIndex));
            //return test1.get("name").compareTo(test2.get("name"));
        }
    }

    private class ListMapComparatorByKey implements Comparator
    {
        private int arrayIndex = -1;

        public int compare(Object obj1, Object obj2)
        {
            ArrayMap<String, String> test1 = (ArrayMap<String, String>) obj1;
            ArrayMap<String, String> test2 = (ArrayMap<String, String>) obj2;

            arrayIndex=0;       //arrayIndex++;
            return test1.keyAt(arrayIndex).compareTo(test2.keyAt(arrayIndex));
            //return test1.get("name").compareTo(test2.get("name"));
        }
    }
}

