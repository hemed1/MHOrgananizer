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
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

import Adapter.AdapterEmail;
import Model.ListItemEmail;
import Utills.*;



public class ActivityMails extends AppCompatActivity
{
    private Button                  btnBack;
    private Button                  btnAddItem;
    private RecyclerView            recyclerView;
    private RecyclerView.Adapter    adapter;
    private List<ListItemEmail>     listItems;

    private Bundle                  extras;
    
    
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

        btnAddItem = (Button)findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AddItem();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerMain);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();  //new List<ListItem>

        //adapter = new AdapterEmail(this, listItems);
        //recyclerView.setAdapter(adapter);

        extras = getIntent().getExtras();

//        if (extras!=null)
//        {
//            ConnectMailServer(MainActivity.MailHostAdress, MainActivity.MailAdresss, MainActivity.MailPassword);
//        }

        //FillList();
        SendEmail();
    }

    private boolean SendEmail()
    {
        boolean  result = true;

        MailSender mailSender = new MailSender(this, "hemedmeir@gmail.com", "Test Me", "Body Test");
        mailSender.SendMailSimple();

        return result;
    }

    private void FillList() //throws IOException
    {
        Message[]  messages;


        //MailReader mailRead = new MailReader(this, "hemedmeir@gmail.com", "13579Mot");
        MailReader mailRead = new MailReader(this,"imap.gmail.com", "hemedmeir@gmail.com", "13579Mot");

        //ProgressDialog progressDialog = ProgressDialog.show(ActivityMails.this, "Recieving Mails... ", "Reading mails ...", false);

        messages = mailRead.ReadMailImap2();
        //messages = mailRead.ReadMailPop3();

        listItems = new ArrayList<>();  //new List<ListItem>

        //mailRead.ReadMailPop3();
        //mailRead.execute();

        for (int i=messages.length-1; i>messages.length-30; i--)
        {
            try
            {
                Address[] addressesFrom, addressesTo;
                addressesFrom = messages[i].getFrom();
                addressesTo = messages[i].getRecipients(Message.RecipientType.TO);
                ListItemEmail item = new ListItemEmail(addressesFrom[0].toString(), messages[i].getSubject());

                item.setContent(messages[i].getContent().toString());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy mm:ss");
                item.setDateSent(dateFormat.format(messages[i].getSentDate()));
                item.setDateReceive(dateFormat.format(messages[i].getReceivedDate()));

                ImageView imageView = new ImageView(this);
                imageView.setImageDrawable((Drawable) getDrawable(R.drawable.meir1));
                //imageView.setImageResource(R.drawable.purim_15);
                // Set picture from SD-Card
                // imageView.setImageBitmap(setPicFromSDCard());
                item.setImgItem(imageView);
                listItems.add(item);
            }
            catch (NoSuchProviderException e)
            {
                e.printStackTrace();
                //System.exit(1);
            }
            catch (MessagingException e)
            {
                e.printStackTrace();
                //System.exit(2);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                //System.exit(2);
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

        adapter = new AdapterEmail(this, listItems);
        recyclerView.setAdapter(adapter);

        //progressDialog.dismiss();
    }

    private void AddItem()
    {
        ListItemEmail item = new ListItemEmail("Sender Added", "Subject of Added");

        item.setContent("Added ");
        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable((Drawable) getDrawable(R.drawable.meir1));
        item.setImgItem(imageView);
        listItems.add(item);

        recyclerView.setAdapter(adapter);
    }

    private boolean ConnectMailServer(String mailHostAdress, String mailAdresss, String mailPassword)
    {
        boolean  result = true;

        return result;
    }

    private void goBack()
    {
        String returnedData;

        returnedData = "18 emails haz been read";

        Intent  intent = getIntent();
        intent.putExtra("returnedData",  returnedData);

        Toast.makeText(this, "Come Back with ... " + returnedData, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK, intent);
        finish();
    }

}
