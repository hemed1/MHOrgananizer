package com.example.meirh.mhorgananizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import Utills.EventsReader;

public class ActivityMeetings extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings);

        EventsReader eventsReader = new EventsReader();

        eventsReader.readCalendar(ActivityMeetings.this);
    }
}
