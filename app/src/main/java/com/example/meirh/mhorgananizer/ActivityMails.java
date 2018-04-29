package com.example.meirh.mhorgananizer;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Store;

import Adapter.AdapterEmail;
import Model.ListItemEmail;
import Utills.*;




public class ActivityMails extends AppCompatActivity
{
    private Button                  btnBack;
    private Button                  btnAddItem;
    private RecyclerView            recyclerView;
    private AdapterEmail            adapterEmail;    // RecyclerView.Adapter
    private List<ListItemEmail>     listItems;
    private TextView                lblFolderName;
    private TextView                lblEmailAddress;

    private Bundle                  extras;

    MailReader                      mailReader;
    Message[]                       Messages = null;
    private Thread                  thread;
    private boolean                 IsHaveToCheckNewEmails;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mails);

        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                goBack();
            }
        });
        lblEmailAddress = (TextView) findViewById(R.id.lblEmailAddress);
        lblFolderName = (TextView) findViewById(R.id.lblFolderName);

        lblEmailAddress.setText(MainActivity.MailAdresss);
        lblFolderName.setText("INBOX");

        btnAddItem = (Button)findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ListItemEmail item = AddItem(Messages[3]);
                //listItems.add(1, item);
                recyclerView.setAdapter(adapterEmail);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerMain);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();  //new List<ListItemEmail>
        adapterEmail = new AdapterEmail(this, listItems);

        // Create the Child observer object that will fire the event
        mailReader = new MailReader(this,"imap.gmail.com", "hemedmeir@gmail.com", "13579Mot");
        IsHaveToCheckNewEmails = MainActivity.MailStayOnLine;

        // Register the listener for this object
        mailReader.setOnMessagesLoaded(new PersonalEvents.OnMessageLoaded()
        {
            // Listen to event. wait here when the event invoked in child object.
            @Override
            public void onDataLoaded(Message[] messages)
            {
                FillList(messages);
            }
        });

        // Execute Asyncronic
        mailReader.execute();

        //Messages = mailReader.ReadMailImap();

        if (MainActivity.MailStayOnLine)
        {
            CheckNewMails();
        }

        int rrr;

        rrr=2;
        System.out.println(rrr);


        //Messages = mailReader.ReadMailImap2();
        //Messages = mailReader.ReadMailPop3();

        //FillList(Messages);
        //FillList(Messages);

//        extras = getIntent().getExtras();
//

    }


    private boolean SendEmail()
    {
        boolean  result = true;

        MailSender mailSender = new MailSender(this, "hemedmeir@gmail.com", "Test Me", "Body Test");

        mailSender.SendMailSimple();

        return result;
    }

    private void FillList(Message[]  messages) //throws IOException
    {

        listItems = new ArrayList<>();  //new List<ListItemEmail>

        for (int i=messages.length-1; i>0; i--)
        {
            ListItemEmail item = AddItem(messages[i]);
            //listItems.add(item);
        }

        Messages = messages;

        adapterEmail = new AdapterEmail(this, listItems);
        recyclerView.setAdapter(adapterEmail);

    }

    private ListItemEmail AddItem(Message message)
    {
        Address[] addressesFrom, addressesTo;
        ListItemEmail item = null;

        try
        {
            addressesFrom = message.getFrom();
            addressesTo = message.getRecipients(Message.RecipientType.TO);

            item = new ListItemEmail(addressesFrom[0].toString(), message.getSubject());

            Multipart multipart = (Multipart) message.getContent();
            BodyPart bodyPart = null;

            if (multipart.getCount() > 0)
            {
                bodyPart = multipart.getBodyPart(0);
                if (bodyPart.getLineCount() > 0)  //  .getContent()!=null)
                {
                    item.setContent(bodyPart.getDescription());     //.getContent().toString());
                    //item.setContent(bodyPart.getContent().toString());
                } else {
                    item.setContent(message.getContent().toString());
                }
            }
            else
            {
                item.setContent(message.getContent().toString());
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy mm:ss");
            item.setDateSent(dateFormat.format(message.getSentDate()));
            item.setDateReceive(dateFormat.format(message.getReceivedDate()));

            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable((Drawable) getDrawable(R.drawable.meir1));
            //imageView.setImageResource(R.drawable.purim_15);
            // Set picture from SD-Card
            // imageView.setImageBitmap(setPicFromSDCard());
            item.setImgItem(imageView);

            listItems.add(item);
        }
        catch (NoSuchProviderException e) {
            e.printStackTrace();
            //System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            //System.exit(2);
        } catch (IOException e) {
            e.printStackTrace();
            //System.exit(2);
        }

        //recyclerView.setAdapter(adapterEmail);

        return item;
    }

    public void CheckNewMails()
    {
        thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    while (IsHaveToCheckNewEmails)
                    {
                        Thread.sleep(5000);

                        Store store = null;
                        Folder folder;
                        // Connect to email server
                        folder = mailReader.ConnectServer(store);
                        if (mailReader.getLastMessageIndexWasRead() > 100)
                        //TODO: if (folder.getMessageCount() > LastMessageIndexWasRead)
                        {
                            folder.close(true);
                            store.close();
                            mailReader.execute();
                            //Message[] messages = mailReader.ReadMailImap();

//                            if (listener != null)
//                            {
//                                // Now let's fire listener here
//                                listener.onDataLoaded(messages);
//                            }
                        }
                        //TODO: folder.close(true);
                        //store.close();
                    }
                }
                catch (Exception ex)
                {
                    System.out.println("Exception arise at the time of read mail");
                    ex.printStackTrace();
                }
            }
        };

        thread.start();


//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable()
//        {
//            @Override
//            public void run() {
//                try
//                {
//                    Store store = null;
//                }
//                catch (Exception ex)
//                {
//                    System.out.println("Exception arise at the time of read mail");
//                    ex.printStackTrace();
//                }
//            }
//        }, 6000);

    }

    private void goBack()
    {
        String returnedData;

        this.IsHaveToCheckNewEmails = false;
        returnedData = "18 emails haz been read";

        Intent  intent = getIntent();
        intent.putExtra("returnedData",  returnedData);

        Toast.makeText(this, "Come Back with ... " + returnedData, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK, intent);
        finish();
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
