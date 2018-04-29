package Utills;

/**
 * Created by meirh on 22/04/2018.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import  android.content.Intent;
import android.net.Uri;
import android.view.View;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;

//import static android.support.v4.content.ContextCompat.startActivity;
import android.support.v7.app.AppCompatActivity;


public class MailReader extends AsyncTask<Void, Void, Void>
{

    private String HostAddress = "pop.gmail.com";           // change accordingly
    private String StoreType = "pop3";
    private String UserAddress = "hemedmeir@gmail.com";     // I purposefully hid these
    private String Password = "13579Mot";                   // I purposefully hid these

    private Context context;
    private ProgressDialog progressDialog;


    public MailReader(Context context,String hostAddress, String userAddress, String password)
    {
        this.HostAddress = hostAddress;
        this.UserAddress = userAddress;
        this.Password = password;
        this.context = context;
    }

    public MailReader(Context context, String userAddress, String password)
    {
        this.UserAddress = userAddress;
        this.Password = password;
        this.context = context;
    }


    public String getHostAddress()
    {
        return HostAddress;
    }

    public void setHostAddress(String hostAddress)
    {
        HostAddress = hostAddress;
    }

    public String getUserAddress()
    {
        return UserAddress;
    }

    public void setUserAddress(String userAddress)
    {
        UserAddress = userAddress;
    }

    public String getPassword()
    {
        return Password;
    }

    public void setPassword(String password)
    {
        Password = password;
    }



    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Recive Mails... ", "Reading mails ...", false);
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        Toast.makeText(context,"Messages read",Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        //ReadMailImap();
        ReadMailPop3();

        return null;
    }

    public boolean ReadMailImap()
    {
        boolean  result = false;


        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");

        HostAddress = "imap.gmail.com";          // change accordingly
        StoreType = "pop3";
        UserAddress = "hemedmeir@gmail.com";    // I purposefully hid these
        Password = "13579Mot";           // I purposefully hid these

        try
        {
            Session session = Session.getInstance(props, null);
            Store store = session.getStore();

            store.connect(HostAddress, UserAddress, Password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            javax.mail.Message msg = inbox.getMessage(inbox.getMessageCount());

            javax.mail.Address[] in = msg.getFrom();

            for (javax.mail.Address address : in) {
                System.out.println("FROM:" + address.toString());
            }

            Multipart mp = (Multipart) msg.getContent();
            BodyPart bp = mp.getBodyPart(0);

            System.out.println("SENT DATE:" + msg.getSentDate());
            System.out.println("SUBJECT:" + msg.getSubject());
            System.out.println("CONTENT:" + bp.getContent());

            result = true;
        }
        catch (Exception mex)
        {
            mex.printStackTrace();
        }

        return result;
    }

    public Message[] ReadMailImap2()
    {
        Message[]   messages;
        Folder      inbox;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");

        messages= new Message[0];

        try
        {
            Session session = Session.getDefaultInstance(props, null);
            Store store;
            store = session.getStore("imaps");
            store.connect(HostAddress,UserAddress,Password);

            inbox = store.getFolder("Inbox");
            System.out.println("No of Unread Messages : " + inbox.getUnreadMessageCount());

            inbox.open(Folder.READ_ONLY);

            messages = inbox.getMessages(1, 30);
            ////messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.USER), false));  //Flag.SEEN / RECENT
            //inbox.setFlags(1,30, new Flags(Flags.Flag.USER), false);

            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.CONTENT_INFO);
            fp.add(FetchProfile.Item.FLAGS);

            try
            {
                ////inbox.fetch(messages, fp);
                //printAllMessages(messages);
                inbox.close(true);
                store.close();
            }
            catch (Exception ex)
            {
                System.out.println("Exception arise at the time of read mail");
                ex.printStackTrace();
            }
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            //System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            //System.exit(2);
        }

        return messages;
    }


    public void printAllMessages(Message[] msgs) throws Exception
    {
        for (int i = 0; i < msgs.length; i++)
        {
            System.out.println("MESSAGE #" + (i + 1) + ":");
            printEnvelope(msgs[i]);
        }
    }

    public void printEnvelope(Message message) throws Exception
    {
        Address[] a;
        // FROM
        if ((a = message.getFrom()) != null)
        {
            for (int j = 0; j < 2; j++)
            {
                System.out.println(("FROM: " + a[j]).toString());
            }
        }

        // TO
        if ((a = message.getRecipients(Message.RecipientType.TO)) != null)
        {
            for (int j = 0; j < 2; j++)
            {
                System.out.println(("TO: " + a[j]).toString());
            }
        }
        String subject = message.getSubject();
        Date receivedDate = message.getReceivedDate();
        String content = message.getContent().toString();
        System.out.println("Subject : " + subject);
        System.out.println("Received Date : " + receivedDate.toString());
        System.out.println("Content : " + content);
        getContent(message);
    }

    public void getContent(Message msg)
    {
        try
        {
            String contentType = msg.getContentType();
            System.out.println("Content Type : " + contentType);
            Multipart mp = (Multipart) msg.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++)
            {
                dumpPart(mp.getBodyPart(i));
            }
        }
        catch (Exception ex)
        {
            System.out.println("Exception arise at get Content");
            ex.printStackTrace();
        }
    }

    public void dumpPart(Part p) throws Exception
    {
        // Dump input stream ..
        InputStream is = p.getInputStream();
        // If "is" is not already buffered, wrap a BufferedInputStream
        // around it.
        if (!(is instanceof BufferedInputStream))
        {
            is = new BufferedInputStream(is);
        }
        int c;
        System.out.println("Message : ");
        while ((c = is.read()) != -1)
        {
            System.out.write(c);
        }
    }

    public Message[] ReadMailPop3()
    {
        Message[]   messages = null;
        Message[]   result = null;


        // TODO: delete
        HostAddress = "pop.gmail.com";          // change accordingly
        StoreType = "pop3";
        UserAddress = "hemedmeir@gmail.com";    // I purposefully hid these
        Password = "13579Mot";           // I purposefully hid these

        try
        {
            //create properties field
            Properties properties = new Properties();

            properties.put("mail.pop3.host", HostAddress);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");

            Session emailSession = Session.getDefaultInstance(properties);

            //create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore("pop3s");

            store.connect(HostAddress, UserAddress, Password);

            //create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and print it
            messages = emailFolder.getMessages();
            //System.out.println("messages.length---" + messages.length);

            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                System.out.println("Email Number " + (i + 1));
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + message.getContent().toString());

            }

            //close the store and folder objects
            emailFolder.close(false);
            store.close();

            result = messages;
        }
        catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.out.println("MainActivity1 " + e.getMessage());
        }
        catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("MainActivity1" + e.getMessage());

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("MainActivity1" + e.getMessage());
        }

        return result;
    }
}
