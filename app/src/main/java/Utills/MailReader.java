package Utills;

/**
 * Created by meirh on 22/04/2018.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.NoSuchProviderException;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Store;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.search.FlagTerm;

//import static android.support.v4.content.ContextCompat.startActivity;

import com.example.meirh.mhorgananizer.ActivityMails;
import com.example.meirh.mhorgananizer.MainActivity;


public class MailReader extends AsyncTask<Void, Void, Message[]>
{

    private String                              HostAddress = "imap.gmail.com";         //"pop.gmail.com";
    private String                              StoreType   = "imaps";                  //"pop3";
    private String                              UserAddress = "hemedmeir@gmail.com";
    private String                              Password;

    private Context                             context;
    private ProgressDialog                      progressDialog;

    private Message[]                           MessageResults;
    private Thread                              thread;
    public int                                  LastMessageIndexWasRead;
    public static final String                  FOLDER_NAME = "INBOX";
    public boolean                              IsHaveToCheckNewEmails;
    public int                                  ModeLoad;

    // The listener must implement the events interface and passes messages up to the parent.
    private PersonalEvents.OnMessageLoaded      listener;

    // Assign the listener implementing events interface that will receive the events
    public void setOnMessagesLoaded(PersonalEvents.OnMessageLoaded  listener)
    {
        this.listener = listener;
    }


    public MailReader(Context context,String hostAddress, String userAddress, String password)
    {
        this.HostAddress = hostAddress;
        this.UserAddress = userAddress;
        this.Password = password;
        this.context = context;
        thread=null;
        IsHaveToCheckNewEmails = true;
        MessageResults = null;
        ModeLoad=1;
    }

    public MailReader(Context context, String userAddress, String password)
    {
        this.UserAddress = userAddress;
        this.Password = password;
        this.context = context;
        thread=null;
        IsHaveToCheckNewEmails = true;
        MessageResults = null;
        ModeLoad=1;
    }

    @Override
    protected void onCancelled(Message[] messages)
    {
        super.onCancelled(messages);
        thread=null;
    }

    @Override
    protected void onCancelled()
    {
        super.onCancelled();
        thread=null;
    }

    @Override
    protected void onProgressUpdate(Void... values)
    {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Reciveing Mails... ", "Reading mails...", false);
    }

    @Override
    protected Message[] doInBackground(Void... params)
    {
        Message[] messages=null;

        if (ModeLoad==1)
        {
            messages = ReadMailImap();
            //ReadMailImap2();
            //ReadMailPop3();
        }
        else
        {
            CheckNewMails();
        }

        return messages;
    }

    @Override
    protected void onPostExecute(Message[] messages)
    {
        super.onPostExecute(messages);

        progressDialog.dismiss();

        MessageResults = messages;

        Toast.makeText(context,"Messages read: " + String.valueOf(messages.length), Toast.LENGTH_LONG).show();

        //cancel(true);

        if (listener != null)
        {
            // Now let's fire listener here
            listener.onDataLoaded(messages);
        }
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

    public int getLastMessageIndexWasRead()
    {
        return LastMessageIndexWasRead;
    }



    public Message[] ReadMailImap()
    {
        Message[]   messages;
        Folder      folder;
        Store       store = null;
        int         fetachMessageFrom=0;
        int         fetachMessageUntil=0;
        int         tmpMessageCount=0;



        messages= new Message[0];

        try
        {
            // Connect to email server
            folder = ConnectServer(store);

            //folder.getMessages(new int[]{1,2,3,4,5,6,7,8,9,10});
            //messages = folder.search(new FlagTerm(new Flags(Flags.Flag.RECENT), false));  //Flag.SEEN / RECENT
            //folder.setFlags(1,30, new Flags(Flags.Flag.RECENT), false);
            //folder.setFlags(messages, new Flags(Flags.Flag.RECENT), false);
            //messages = folder.getMessages(1, 30);

            tmpMessageCount = folder.getMessageCount();
            if (LastMessageIndexWasRead==0)
            {
                LastMessageIndexWasRead = tmpMessageCount;
            }
            if (tmpMessageCount > LastMessageIndexWasRead)
            {
                fetachMessageFrom = tmpMessageCount - (tmpMessageCount - LastMessageIndexWasRead -1);
            }
            else
            {
                fetachMessageFrom = tmpMessageCount - MainActivity.DEFUALT_MESSAGES_TO_READ -1;
            }
            fetachMessageUntil = tmpMessageCount;
            LastMessageIndexWasRead = tmpMessageCount;

             messages = folder.getMessages(fetachMessageFrom, fetachMessageUntil);
            // Get All messages
            //messages = folder.getMessages();
            // Get specific message
            //Message message = folder.getMessage(inbox.getMessageCount());  // .getUnreadMessageCount()

            //System.out.println("No of Unread Messages : " + folder.getUnreadMessageCount());

            FetchProfile fetchProfile = new FetchProfile();
            fetchProfile.add(FetchProfile.Item.ENVELOPE);
            fetchProfile.add(FetchProfile.Item.CONTENT_INFO);
            fetchProfile.add(FetchProfile.Item.FLAGS);

            try
            {
                folder.fetch(messages, fetchProfile);
                //printAllMessages(messages);
                folder.close(true);
                //TODO:store.close();
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

    public Folder ConnectServer(Store store)
    {
        Properties  properties = null;
        Session     session = null;
        Folder      foler = null;
        //Store       store;




        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //HostAddress = "imap.gmail.com";
        StoreType = "imaps";

        try
        {
            properties = System.getProperties();
            properties.setProperty("mail.store.protocol", StoreType);

            //session = Session.getDefaultInstance(properties, null);
            session = Session.getDefaultInstance(properties);
            store = session.getStore(StoreType);

            store.connect(HostAddress,UserAddress,Password);

            foler = store.getFolder(FOLDER_NAME);
            foler.open(Folder.READ_ONLY);

            //ToDO: check if can close folder here
        }
        catch (NoSuchProviderException e)
        {
            e.printStackTrace();
            //System.exit(1);
        }
        catch (MessagingException e) {
            e.printStackTrace();
            //System.exit(2);
        }


        return  foler;
    }

    public void CheckNewMails()
    {

        MessageResults = new Message[0];


//        thread = new Thread()
//        {
//            @Override
//            public void run()
//            {
                try
                {
                    while (IsHaveToCheckNewEmails)
                    {
                        Thread.sleep(4000);

                        System.out.println("'CheckNewMails' check new mails In loop/n/n");
                        Store store = null;
                        Folder folder;

                        // Connect to email server
                        folder = ConnectServer(store);

                        LastMessageIndexWasRead = folder.getMessageCount()- 2;  // TODO: Delete
                        //if (mailReader.getLastMessageIndexWasRead() > 100)
                        if (folder.getMessageCount() > LastMessageIndexWasRead)
                        {
                            System.out.println("'CheckNewMail()' found new mails" + new Date().toString()+"/n");
                            folder.close(true);
                            store=null;  // store.close();

                            IsHaveToCheckNewEmails=false;

                            //execute();
                            //MessageResults = ReadMailImap();
                            //thread.stop();
                            //if (listener != null)
                            //{
                            //    // Now let's fire listener here
                            //    listener.onDataLoaded(messages);
                            //}
                            //return;
                            //if (thread.isInterrupted());
                            //stopThread(this);
                            //return;
                            //break;
                        }

                        folder.close(true);
                        //store.close();
                        store=null;
                    }

                }
                catch (Exception ex)
                {
                    System.out.println("Exception rise at 'CheckNewMails': " + ex.getMessage());
                    ex.printStackTrace();
                }
//            }

//        };

        //thread = new Thread(myRunnable);

//        thread.start();
//
//
    }

    private synchronized void stopThread(Thread theThread)
    {
        if (theThread != null)
        {
            //theThread.stop();
            //theThread.interrupt();
            //theThread.destroy();
            //thread.stop();
            theThread = null;
        }

        if (listener != null && MessageResults.length>0)
        {
            // Now let's fire listener here
            listener.onDataLoaded(MessageResults);
        }

    }

    public Message[] ReadMailImap2()
    {
        Folder  inbox = null;
        javax.mail.Message      message;
        javax.mail.Message[]    messages = null;

        HostAddress = "imap.gmail.com";          // change accordingly
        StoreType = "imaps";
        UserAddress = "hemedmeir@gmail.com";    // I purposefully hid these
        Password = "13579Mot";           // I purposefully hid these

        try
        {
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", StoreType);

            Session session = Session.getInstance(props, null);
            Store store = session.getStore();   // session.getStore(StoreType);

            store.connect(HostAddress, UserAddress, Password);

            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Get specific message
            message = inbox.getMessage(inbox.getMessageCount());  // .getUnreadMessageCount()

            javax.mail.Address[] in = message.getFrom();

            for (javax.mail.Address address : in)
            {
                System.out.println("FROM:" + address.toString());
            }

            Multipart mp = (Multipart) message.getContent();
            BodyPart bp = mp.getBodyPart(0);

            System.out.println("SENT DATE:" + message.getSentDate());
            System.out.println("SUBJECT:" + message.getSubject());
            System.out.println("CONTENT:" + bp.getContent());

        }
        catch (Exception mex)
        {
            mex.printStackTrace();
        }

        return messages;
    }

    public Message[] ReadMailPop3()
    {
        Message[]   messages = null;
        Message[]   result = null;
        Store store = null;


        // TODO: delete
        HostAddress = "pop.gmail.com";          // change accordingly
        StoreType = "pop3s";

        try
        {
            //create properties field
//            Properties properties = new Properties();

//            properties.put("mail.pop3.host", HostAddress);
//            properties.put("mail.pop3.port", "995");
//            properties.put("mail.pop3.starttls.enable", "true");

            Properties properties = System.getProperties();
            properties.setProperty("mail.pop3.protocol", StoreType);

            Session session = Session.getDefaultInstance(properties);
            //Session session = Session.getDefaultInstance(properties, null);

            //create the POP3 store object and connect with the pop server
            store = session.getStore(StoreType);

            // Connect the server
            store.connect(HostAddress, UserAddress, Password);

            //create the folder object and open it
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            // retrieve All the messages from the folder in an array and print it
            messages = folder.getMessages();
            //System.out.println("messages.length---" + messages.length);

            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                System.out.println("Email Number " + (i + 1));
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + message.getContent().toString());

            }

            //close the store and folder objects
            folder.close(false);
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

}
