package com.example.meirh.mhorgananizer;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Store;

import Adapter.AdapterBaseList;
import Adapter.AdapterEmail;
import Model.ListItemEmail;
import Utills.*;




public class ActivityMails extends AppCompatActivity implements View.OnClickListener
{
    private Button                  btnOpenMail;
    private Button                  btnNewMail;
    private Button                  btnRefresh;
    private RecyclerView            recyclerMain;
    private AdapterEmail            adapterEmail;    // RecyclerView.Adapter
    private List<ListItemEmail>     listItems;
    private TextView                lblFolderName;
    private TextView                lblEmailAddress;
    private TextView                lblEmailTitle;
    private ListView                listFolders;

    private Bundle                  extras;

    MailReader                      mailReader;
    Message[]                       MessagesAll = null;
    TimerTask                       MailCheckTimerTask;
    Timer                           MailCheckTimer;
    public Thread                   CheckMailThread;
    private PersonalEvents.OnMessageLoaded      listener;
    private boolean                 IsTimerWork;
    private AdapterBaseList         adapterFoldersList;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mails);

        SetIOControls();

        FillFoldersListView();

        FetchMails();

        // Execute Asyncronic
        //mailReader.execute();

        //SendEmail();
        //extras = getIntent().getExtras();
    }


    private Message[] FetchMails()
    {
        return FetchMails(false);
    }

    private Message[] FetchMails(boolean insertItem)
    {

        MessagesAll = mailReader.FetchMails();

        FillList(MessagesAll, insertItem);

        if (mailReader.IsHaveToCheckNewEmails && !IsTimerWork)
        {
            TimerRun();
        }

        // Execute Asyncronic
        //mailReader.execute();

        return MessagesAll;
    }

    private void FillList(Message[]  messages, boolean insertItem)
    {

        for (int i=messages.length-1; i>=0; i--)
        {
            ListItemEmail item = AddItem(messages[i]);
            if (!insertItem)
            {
                listItems.add(item);
            }
            else
            {
                listItems.add(0, item);
            }
        }

        MessagesAll = messages;

        adapterEmail = new AdapterEmail(this, listItems);

        // set thread in background
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                recyclerMain.setAdapter(adapterEmail);
            }
        });

        System.out.println("Items count: " + String.valueOf(listItems.size()));
        //Toast.makeText(this, "Mails count: " + String.valueOf(recyclerMain.getAdapter().getItemCount()), Toast.LENGTH_SHORT).show();

    }

    private ListItemEmail AddItem(Message message)
    {
        Address[] addressesFrom, addressesTo;
        ListItemEmail item = null;

        try
        {
            addressesFrom = message.getFrom();
            addressesTo = message.getRecipients(Message.RecipientType.TO);

            String from = addressesFrom[0].toString();
            from = extractFromAddress(from);

            item = new ListItemEmail(from, message.getSubject());

//            Multipart multipart = (Multipart) message.getContent();
//            BodyPart bodyPart = null;
//
//            if (multipart.getCount() > 0)
//            {
//                bodyPart = multipart.getBodyPart(0);
//                if (bodyPart.getLineCount() > 0)  //  .getContent()!=null)
//                {
//                    item.setContent(bodyPart.getDescription());     //.getContent().toString());
//                    //item.setContent(bodyPart.getContent().toString());
//                } else {
//                    item.setContent(message.getContent().toString());
//                }
//            }
//            else
//            {
//                item.setContent(message.getContent().toString());
//            }
            item.setContent("Content of message");
            //item.setContent(message.getContent().toString());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            item.setDateSent(dateFormat.format(message.getSentDate()));
            item.setDateReceive(dateFormat.format(message.getReceivedDate()));

            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable((Drawable) getDrawable(R.mipmap.meir_oval));       // TODO: put right picture
            //imageView.setImageDrawable((Drawable) getDrawable(R.drawable.mail_read4));       // TODO: put right picture
            item.setImgItem(imageView);

            //listItems.add(item);
            //listItems.add(1, item);   // TODO: Insert
        }
        catch (NoSuchProviderException e)
        {
            e.printStackTrace();
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
        }


        return item;
    }

    private void SetIOControls()
    {
        btnOpenMail = (Button)findViewById(R.id.btnOpenMail);
        btnNewMail = (Button)findViewById(R.id.btnNewMail);
        btnRefresh = (Button)findViewById(R.id.btnRefresh);
        listFolders = (ListView) findViewById(R.id.listFolders);
        lblEmailAddress = (TextView) findViewById(R.id.lblEmailAddress);
        lblEmailTitle = (TextView) findViewById(R.id.lblEmailTitle);
        lblFolderName = (TextView) findViewById(R.id.lblFolderName);
        recyclerMain = (RecyclerView) findViewById(R.id.recyclerMain);

        lblEmailTitle.setText("Hello " + MainActivity.MainUserName);
        lblEmailAddress.setText(MainActivity.MailAdresss);
        lblFolderName.setText("Inbox");

        listFolders.bringToFront();
        listFolders.setVisibility(View.INVISIBLE);

        recyclerMain.setHasFixedSize(true);
        recyclerMain.setLayoutManager(new LinearLayoutManager(this));
        listItems = new ArrayList<>();  //new List<ListItemEmail>
        IsTimerWork = false;

        // Create the Child observer object that will fire the event
        mailReader = new MailReader(ActivityMails.this, MainActivity.MailHostAdress, MainActivity.MailAdresss, MainActivity.MailPassword);
        mailReader.IsHaveToCheckNewEmails = (MainActivity.MailCheckMailInterval>0);

        // Events
        btnOpenMail.setOnClickListener(this);
        btnNewMail.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
        lblFolderName.setOnClickListener(this);

        // Register the listener for this object
        mailReader.setOnMessagesLoaded(new PersonalEvents.OnMessageLoaded()
        {
            // Listen to event. wait here when the event invoked in child object.
            @Override
            public void onDataLoaded(Message[] messages)
            {
                mailReader_RecivedMessages(messages);
            }
        });

    }

    private void mailReader_RecivedMessages(Message[] messages)
    {

        if (mailReader.IsHaveToCheckNewEmails)
        {
            TimerStop();
        }

        if (messages.length > 0)
        {
            FillList(messages, false);
        }

        if (mailReader.IsHaveToCheckNewEmails && !IsTimerWork)
        {
            TimerRun();
        }

    }

    private void TimerRun()
    {
        System.out.println("Run timer " + (new Date()).toString());

        IsTimerWork = true;

        MailCheckTimerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                MailCheckTimer_onTick();
            }
        };


        // Run the Timer
        MailCheckTimer = new Timer();
        int interval = MainActivity.MailCheckMailInterval * 1000 * 60;
        MailCheckTimer.schedule(MailCheckTimerTask, interval, interval);
    }

    private void TimerStop()
    {
        System.out.println("Stop timer " + (new Date()).toString());

        if (IsTimerWork)
        {
            MailCheckTimer.cancel();
            MailCheckTimer.purge();
            MailCheckTimerTask.cancel();
            MailCheckTimer = null;
            MailCheckTimerTask=null;
            //MailCheckTimerTask.run();
        }

        IsTimerWork = false;
    }

    private void MailCheckTimer_onTick()
    {
        boolean isFoundNewMessages = false;

        System.out.println("Run in Timer " + new Date().toString() + "/n");

        if (mailReader.IsHaveToCheckNewEmails)
        {
            TimerStop();

            isFoundNewMessages = mailReader.CheckNewMails();

            if (isFoundNewMessages)
            {
                FetchMails(true);
            }
            else
            {
                TimerRun();
            }
        }
    }



    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnOpenMail:
                goBack();
                break;

            case R.id.btnRefresh:
                TimerStop();
                listItems = new ArrayList<>();
                FetchMails();
                break;

            case R.id.btnNewMail:
                //TimerStop();
                //TimerRun();
                break;

            case R.id.lblFolderName:
                TimerStop();
                listFolders.setVisibility(View.VISIBLE);
                 //TimerRun();
                break;
        }
    }

    // Fired from AdapterBaseList class (not from 'listFolder' control)
    private void OnListClick(int listPositionIndex, String selectedItemText)          //AdapterView<?> adapterView, View view, int position, long arg3)
    {
        listFolders.setVisibility(View.INVISIBLE);

        System.out.println("Item index: " + listPositionIndex + "  Item value: " + selectedItemText);

        lblFolderName.setText(selectedItemText);
        mailReader.FolderName = selectedItemText;

        FetchMails();

    }

    private String extractFromAddress(String from)
    {
        int pos = from.lastIndexOf("<");
        if (from.length()>30 && pos>-1)
        {
            from = from.substring(pos+1, from.lastIndexOf(">")-0);
        }

        return from;
    }


    private boolean SendEmail()
    {
        boolean  result = true;

        MailSender mailSender = new MailSender(this, "hemedmeir@gmail.com", "Test Me", "Body Test");

        mailSender.SendMailsSmpt3();
        //mailSender.SendMailsSmpt2();
        //mailSender.execute();
        //mailSender.SendMailSmpt();
        //mailSender.SendMailSimple();

        return result;
    }

    private void goBack()
    {
        String returnedData;

        TimerStop();

        mailReader.CheckMailThread = null;

        returnedData = "Mail handle was finished";

        Toast.makeText(this, returnedData, Toast.LENGTH_SHORT).show();

        Intent  intent = getIntent();
        intent.putExtra("returnedData",  returnedData);

        setResult(RESULT_OK, intent);
        finish();
    }



    private void FillFoldersListView()
    {
        String[]  items = new String[3];
        items[0]="Inbox";
        items[1]="Drafts";
        items[2]="Sent Items";

        ArrayList<String> ListItemArray = new ArrayList<String>();
        ListItemArray.addAll(Arrays.asList(items));

        adapterFoldersList = new AdapterBaseList(this, ListItemArray, listFolders);
        adapterFoldersList.LayoutCardResourceID = R.layout.list_row;
        adapterFoldersList.LayoutControlToShowResourceID = R.id.lblListRow;

        // Register the listener for this object
        adapterFoldersList.setOnListViewItemClick(new PersonalEvents.OnListViewItemClick()
        {
            // Listen to event. wait here when the event invoked in child object.
            @Override
            public void setOnListViewItemPressed(int listPositionIndex, String selectedItemText)
            {
                OnListClick(listPositionIndex, selectedItemText);
            }
        });

        adapterFoldersList.FillList();
    }

}

//        for (int i=0; i<7; i++)
//        {
//            ListItemEmail item = new ListItemEmail("Sender: "+ (i+1), "Subject of Item " + (i+1));
//
//            item.setContent("Great " + String.valueOf(i+1));
//            ImageView imageView = new ImageView(this);
//            imageView.setImageDrawable((Drawable) getDrawable(R.drawable.meir1));
//            //imageView.setImageResource(R.drawable.purim_15);
//            // Set picture from SD-Card
//            // imageView.setImageBitmap(setPicFromSDCard());
//            item.setImgItem(imageView);
//            listItems.add(item);
//        }
