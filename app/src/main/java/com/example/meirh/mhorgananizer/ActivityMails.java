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
    Message[]                       Messages = null;
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

        // Execute Asyncronic
        mailReader.execute();

        //Messages = mailReader.ReadMailImap();
        //Messages = mailReader.ReadMailImap2();
        //Messages = mailReader.ReadMailPop3();
        //SendEmail();

        //extras = getIntent().getExtras();
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
        adapterEmail = new AdapterEmail(this, listItems);

        MailCheckTimerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                MailCheckTimer_onTick();
            }
        };

        MailCheckTimer = new Timer();
        IsTimerWork = false;

        // Create the Child observer object that will fire the event
        mailReader = new MailReader(ActivityMails.this, MainActivity.MailHostAdress, MainActivity.MailAdresss, MainActivity.MailPassword);
        mailReader.IsHaveToCheckNewEmails = MainActivity.MailStayOnLine;

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

    // Fired from AdapterBaseList class (not from 'listFolder' control)
    private void OnListClick(int listPositionIndex, String selectedItemText)          //AdapterView<?> adapterView, View view, int position, long arg3)
    {
        listFolders.setVisibility(View.INVISIBLE);

        System.out.println("Item index: " + listPositionIndex + "  Item value: " + selectedItemText);

        lblFolderName.setText(selectedItemText);
        mailReader.FolderName = selectedItemText;

        Message[] messages = mailReader.FetchMails();

        FillList(messages);
    }

    private void mailReader_RecivedMessages(Message[] messages)
    {
        //System.out.println("Reach the event was fired with " + String.valueOf(messages.length) + " Messages/n");

        CheckMailThread = null;

        if (messages.length > 0)
        {
            FillList(messages);
        }

        if (mailReader.IsHaveToCheckNewEmails && !IsTimerWork)
        {
            TimerRun();
         }

        //mailReader.CheckMailThread = null;
        //if (mailReader.IsHaveToCheckNewEmails)
        //{
        //    mailReader.CheckNewMailsAsyncThread();
        //}
    }

    /// Not in Use
    public boolean CheckNewMails()
    {
        boolean  isFoundNewMessages = false;
        final Message[]  messages = new Message[0];
        Store   store = null;
        Folder  folder = null;
        Object[]    mailObjects;


        try
        {
            //Toast.makeText(context,"Checking from new mails ...", Toast.LENGTH_SHORT).show();

            while (mailReader.IsHaveToCheckNewEmails)
            {
                Thread.sleep(4000);

                System.out.println("'CheckNewMails' check new mails In loop/n/n" + (new Date()).toString());

                // Connect to email server
                mailObjects = mailReader.ConnectServer();     // folder / store

                folder = (Folder) mailObjects[0];
                store = (Store) mailObjects[1];

                mailReader.LastMessageIndexWasRead = folder.getMessageCount()- 2;  // TODO: Delete

                if (folder.getMessageCount() > mailReader.LastMessageIndexWasRead)
                {
                    System.out.println("'CheckNewMail()' found new mails: " + new Date().toString()+"/n");
                    isFoundNewMessages = true;
                    break;
                }
            }

            if (folder != null)
            {
                folder.close(true);
                store.close();
            }

            if (isFoundNewMessages)
            {
                Messages = mailReader.FetchMails();

                FillList(Messages);

                //TryFunc();

                CheckMailThread = null;
                //stopThread(this);
            }
        }
        catch (Exception ex)
        {
            //System.out.println("Exception rise at 'CheckNewMails': " + ex.getMessage());
            ex.printStackTrace();
        }

        //folder = null;
        //store = null;

        return isFoundNewMessages;
    }

    /// Not in Use
    public void CheckNewMailsAsyncThread()
    {

        CheckMailThread = new Thread()
        {
            @Override
            public void run()
            {
                if (mailReader.CheckNewMails())
                {
                    //Message[] messages = mailReader.FetchMails();
                    //FillList(messages);
                }
            }
        };

        CheckMailThread.start();

    }

    private void TryFunc()
    {
        for (int i=1; i<100; i++)
        {
            System.out.println("Loop in " + String.valueOf(i));
        }
    }

    private void FillList(Message[]  messages) //throws IOException
    {

        //listItems = new ArrayList<>();  //new List<ListItemEmail>

        for (int i=messages.length-1; i>=0; i--)
        {
            ListItemEmail item = AddItem(messages[i]);
            //listItems.add(item);
        }

        Messages = messages;

        adapterEmail = new AdapterEmail(this, listItems);
        recyclerMain.setAdapter(adapterEmail);

        System.out.println("Items count: " + String.valueOf(listItems.size()));
        //Toast.makeText(this,"Mails count: " + String.valueOf(recyclerMain.getAdapter().getItemCount()), Toast.LENGTH_SHORT).show();

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
            imageView.setImageDrawable((Drawable) getDrawable(R.drawable.meir1));       // TODO: put right picture

            //imageView.setImageResource(R.drawable.purim_15);
            // Set picture from SD-Card
            // imageView.setImageBitmap(setPicFromSDCard());
            item.setImgItem(imageView);

            listItems.add(item);
            //listItems.add(1, item);   // TODO: Insert
        }
        catch (NoSuchProviderException e) {
            e.printStackTrace();
            //System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            //System.exit(2);
        }
//        catch (IOException e) {
//            e.printStackTrace();
//            //System.exit(2);
//        }

        //recyclerMain.setAdapter(adapterEmail);

        return item;
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

        mailReader.IsHaveToCheckNewEmails = false;
        mailReader.CheckMailThread = null;

        returnedData = "Mail handle was finished";

        Toast.makeText(this, returnedData, Toast.LENGTH_SHORT).show();

        Intent  intent = getIntent();
        intent.putExtra("returnedData",  returnedData);

        setResult(RESULT_OK, intent);
        finish();
    }

    private void TimerRun()
    {
        System.out.println("Run timer " + (new Date()).toString());

        IsTimerWork = true;
        // Run the Timer
        MailCheckTimer.schedule(MailCheckTimerTask, 100, 9000); // MainActivity.MailCheckMailInterval
    }

    private void TimerStop()
    {
        System.out.println("Stop timer " + (new Date()).toString());
        MailCheckTimer.cancel();
        //MailCheckTimer.purge();
        IsTimerWork = false;
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
                mailReader.LastMessageIndexWasRead = 0;
                Messages = mailReader.FetchMails();
                FillList(Messages);
                TimerRun();
                break;

            case R.id.btnNewMail:
                //TimerStop();
                //ListItemEmail item = AddItem(Messages[0]);
                //listItems.add(1, item);   // TODO: Insert
                //recyclerMain.setAdapter(adapterEmail);
                //TimerRun();
                break;

            case R.id.lblFolderName:
                TimerStop();
                listFolders.setVisibility(View.VISIBLE);
                 //TimerRun();
                break;
        }
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

    private void MailCheckTimer_onTick()
    {
        System.out.println("Run in Timer " + new Date().toString() + "/n");

        //MailCheckTimer.cancel();
        //MailCheckTimer.purge();
        //MailCheckTimerTask.run();
        if (mailReader.IsHaveToCheckNewEmails)
        {
            //Toast.makeText(this,"Checking new mails ...", Toast.LENGTH_SHORT).show();
            mailReader.CheckNewMails();
        }
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
