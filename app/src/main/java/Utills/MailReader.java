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
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.NoSuchProviderException;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Store;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.search.FlagTerm;

//import static android.support.v4.content.ContextCompat.startActivity;

import com.example.meirh.mhorgananizer.ActivityMails;
import com.example.meirh.mhorgananizer.MainActivity;


public class MailReader     //extends AsyncTask<Void, Void, Message[]>
{

    private String                              HostAddress = "imap.gmail.com";         //"pop.gmail.com";
    private String                              StoreType   = "imaps";                  //"pop3";
    private String                              UserAddress = "hemedmeir@gmail.com";
    private String                              Password;

    private Context                             context;
    private ProgressDialog                      progressDialog;

    private Message[]                           MessageResults;
    public Thread                               CheckMailThread;
    public int                                  LastMessageIndexWasRead;
    public String                               FolderName;
    //public static final String                  FOLDER_NAME = "INBOX";
    public boolean                              IsHaveToCheckNewEmails;

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
        MessageResults = null;
        FolderName = "INBOX";
    }

    public MailReader(Context context, String userAddress, String password)
    {
        this.UserAddress = userAddress;
        this.Password = password;
        this.context = context;
        MessageResults = null;
        FolderName = "INBOX";
    }

//    @Override
//    protected void onCancelled()
//    {
//        super.onCancelled();
//        CheckMailThread=null;
//    }
//    @Override
//    protected void onProgressUpdate(Void... values)
//    {
//        super.onProgressUpdate(values);
//    }
//
//    @Override
//    protected void onPreExecute()
//    {
//        super.onPreExecute();
//        progressDialog = ProgressDialog.show(context, "Reciveing Mails... ", "Reading mails...", false);
//    }
//    @Override
//    protected Message[] doInBackground(Void... params)
//    {
//        Message[] messages = new Message[0];
//
//        messages = FetchMails();
//
//        //progressDialog.dismiss();
//
//        return messages;
//    }
//    @Override
//    protected void onPostExecute(Message[] messages)
//    {
//        super.onPostExecute(messages);
//
//        progressDialog.dismiss();
//
////        if (listener != null)
////        {
////            // Now let's fire listener here
////            listener.onDataLoaded(messages);
////        }
//    }


    public Message[] FetchMails()
    {
        Message[]  messages;

        //progressDialog = ProgressDialog.show(context, "Reciveing Mails... ", "Reading mails...", false);

        messages = ReadMailImap();

        //progressDialog.dismiss();

        //Messages = ReadMailImap2();
        //Messages = ReadMailPop3();

        ///if (listener != null && messages.length > 0)
        //{
        //    // Now let's fire listener here
        //    listener.onDataLoaded(MessageResults);
        //}

        return messages;
    }

    public Message[] ReadMailImap()
    {
        Message[]   messages;
        Folder      folder;
        Store       store = null;
        int         fetachMessageFrom=0;
        int         fetachMessageUntil=0;
        int         tmpMessageCount=0;
        Object[]    mailObjects;



        messages= new Message[0];

        try
        {
            // Connect to email server
            mailObjects = ConnectServer();     // folder / store

            if (mailObjects==null || mailObjects.length==0 || mailObjects[0]==null)
            {
                return messages;
            }

            folder = (Folder) mailObjects[0];
            store = (Store) mailObjects[1];

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
                fetachMessageFrom = tmpMessageCount - MainActivity.DEFUALT_MESSAGES_TO_READ + 1;
            }
            fetachMessageUntil = tmpMessageCount;
            LastMessageIndexWasRead = tmpMessageCount;

            messages = folder.getMessages(fetachMessageFrom, fetachMessageUntil);

            FetchProfile fetchProfile = new FetchProfile();
            fetchProfile.add(FetchProfile.Item.ENVELOPE);
            fetchProfile.add(FetchProfile.Item.CONTENT_INFO);
            fetchProfile.add(FetchProfile.Item.FLAGS);

            // Get All messages
            //messages = folder.getMessages();
            // Get specific message
            //Message message = folder.getMessage(inbox.getMessageCount());  // .getUnreadMessageCount()
            //folder.getMessages(new int[]{1,2,3,4,5,6,7,8,9,10});
            //messages = folder.search(new FlagTerm(new Flags(Flags.Flag.RECENT), false));  //Flag.SEEN / RECENT
            //folder.setFlags(1,30, new Flags(Flags.Flag.RECENT), false);
            //folder.setFlags(messages, new Flags(Flags.Flag.RECENT), false);
            //messages = folder.getMessages(1, 30);

            try
            {
                //Toast.makeText(context,"Fetching mails and there's content from server ... " + String.valueOf(messages.length) + " Messages", Toast.LENGTH_LONG).show();
                //System.out.println(""Fetching mails and there's content from server ... " + String.valueOf(messages.length) + " Messages"");

                // Fetching the mails with all it's content
                folder.fetch(messages, fetchProfile);

                folder.close(true);
                store.close();

                //printAllMessages(messages);
            }
            catch (Exception ex)
            {
                //Toast.makeText(context,"Exception rised when try to fetach mails: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                //System.out.println("Exception rised when try to fetach mails");
                ex.printStackTrace();
            }
        }

        catch (NoSuchProviderException e)
        {
            //Toast.makeText(context,"Exception rised when try to read mails: " + e.getMessage(), Toast.LENGTH_LONG).show();
            //System.out.println("Exception rised when try to fetach mails");
            e.printStackTrace();
            //System.exit(1);
        }
        catch (MessagingException e)
        {
            //Toast.makeText(context,"Exception rised when try to read mails: " + e.getMessage(), Toast.LENGTH_LONG).show();
            //System.out.println("Exception rised when try to fetach mails");
            e.printStackTrace();
            //System.exit(2);
        }

        MessageResults = messages;

        return messages;
    }

    public Object[] ConnectServer()
    {
        Properties  properties = null;
        Session     session;
        Folder      folder;
        Store       store;
        Object[]    mailObjects = null;



        //Toast.makeText(context,"Connecting to mails server", Toast.LENGTH_SHORT).show();

        if (HostAddress==null || HostAddress.isEmpty() || UserAddress==null || UserAddress.isEmpty() || Password==null || Password.isEmpty())
        {
            //Toast.makeText(context,"Email address or password are empty", Toast.LENGTH_LONG).show();
            return mailObjects;
        }

        // TODO: Maybe to delete
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //HostAddress = "imap.gmail.com";
        StoreType = "imaps";

        try
        {
            properties = System.getProperties();
            properties.setProperty("mail.store.protocol", StoreType);

            //Authenticator aa = new javax.mail.Authenticator()
            //{
            //    //Authenticating the password
            //    protected PasswordAuthentication getPasswordAuthentication() {
            //        return new PasswordAuthentication(UserAddress, Password); //Config.EMAIL, Bitmap.Config.PASSWORD);
            //    }
            //};

            session = Session.getDefaultInstance(properties, null);
            store = session.getStore(StoreType);

            store.connect(HostAddress, UserAddress, Password);

            //store.connect(HostAddress, 993, UserAddress, Password);
            //store.connect(UserAddress, Password);

            folder = store.getFolder(FolderName);
            folder.open(Folder.READ_ONLY);

            mailObjects = new Object[2];
            mailObjects[0] = folder;
            mailObjects[1] = store;

        }
        catch (NoSuchProviderException e)
        {
            e.printStackTrace();
            Toast.makeText(context,"Can't connect to Mail server: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            //System.exit(1);
        }
        catch (MessagingException e)
        {
            //Toast.makeText(context,"Can't connect to Mail server: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            //System.exit(2);
        }


        return mailObjects;
    }

    public void TryMe()
    {

        System.out.println("TryMe");

    }

    public boolean CheckNewMails()
    {
        boolean  isFoundNewMessages = false;
        Store   store = null;
        Folder  folder = null;
        Object[]    mailObjects;



        try
        {
            //Toast.makeText(context,"Checking new mails ...", Toast.LENGTH_SHORT).show();

            System.out.println("'CheckNewMails' check new mails In loop  " + (new Date()).toString() + "/n");

            // Connect to email server
            mailObjects = ConnectServer();

            if (mailObjects==null || mailObjects.length==0 || mailObjects[0]==null)
            {
                return false;
            }

            folder = (Folder) mailObjects[0];
            store = (Store) mailObjects[1];

            if (folder.getMessageCount() > LastMessageIndexWasRead)
            {
                System.out.println("'CheckNewMail()' found new mails" + new Date().toString()+"/n");
                isFoundNewMessages = true;
            }

            //if (folder.hasNewMessages()) // TODO: this instead
            //{
            //    System.out.println("'CheckNewMail()' found new mails" + new Date().toString()+"/n");
            //    isFoundNewMessages = true;
            //    break;
            //}

            if (folder != null)
            {
                folder.close(true);
                store.close();
            }

          }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return isFoundNewMessages;
    }

    // No in use
    public void CheckNewMailsAsyncThread()
    {

        CheckMailThread = new Thread()
        {
            @Override
            public void run()
            {
                if (CheckNewMails())
                {
                    CheckMailThread.stop();
                    CheckMailThread=null;
                    //stopThread(this);

                    MessageResults = FetchMails();

//                    if (listener != null)
//                    {
//                        System.out.println("Going to fire event " + new Date().toString()+"/n");
//                        // Now let's fire listener here
//                        listener.onDataLoaded(MessageResults);
//                    }
                }
            }

        };

        CheckMailThread.start();

    }

    private synchronized void stopThread(Thread theThread)
    {
        if (theThread != null)
        {
//            theThread.stop();
//            //theThread.interrupt();
//            theThread.destroy();
            theThread = null;
//        }
//
        }

    }

    public Message[] ReadMailImap2()
    {
        Folder  inbox = null;
        javax.mail.Message      message;
        javax.mail.Message[]    messages = null;

        HostAddress = "imap.gmail.com";          // change accordingly
        StoreType = "imaps";

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
            Properties properties = new Properties();

            properties.put("mail.pop3.host", HostAddress);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");

            //Properties properties = System.getProperties();
            //properties.setProperty("mail.pop3.protocol", StoreType);

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
            for (int j = 0; j < 1; j++)
            {
                System.out.println(("FROM: " + a[j]).toString());
            }
        }

        // TO
        if ((a = message.getRecipients(Message.RecipientType.TO)) != null)
        {
            for (int j = 0; j < a.length; j++)
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
        //System.out.println("Content : " + message.getDescription());
        //System.out.println("Content : " + message.getFileName());

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
            System.out.println("Exception arise at get Content: " + ex.getMessage());
            //ex.printStackTrace();
        }
    }

    public void dumpPart(Part p) throws Exception
    {
        // Dump input stream ..
        InputStream is = p.getInputStream();

        //p.getHeader("a");
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

    public boolean CheckNewMailsOld()
    {
        boolean  isFoundNewMessages = false;
        Store   store = null;
        Folder  folder = null;
        Object[]    mailObjects;


        try
        {
            //Toast.makeText(context,"Checking new mails ...", Toast.LENGTH_SHORT).show();

            //while (IsHaveToCheckNewEmails)
            //{
            //Thread.sleep(6000);     //Long.valueOf(MainActivity.MailCheckMailInterval) * 3600 * 1000);

            System.out.println("'CheckNewMails' check new mails In loop  " + (new Date()).toString() + "/n");

            // Connect to email server
            mailObjects = ConnectServer();     // folder / store

            if (mailObjects==null || mailObjects.length==0 || mailObjects[0]==null)
            {
                return false;
            }

            folder = (Folder) mailObjects[0];
            store = (Store) mailObjects[1];

            //LastMessageIndexWasRead = folder.getMessageCount() - 2;  // TODO: Delete

            if (folder.getMessageCount() > LastMessageIndexWasRead)
            {
                System.out.println("'CheckNewMail()' found new mails" + new Date().toString()+"/n");
                isFoundNewMessages = true;
                //break;
            }

            //if (folder.hasNewMessages()) // TODO: this instead
            //{
            //    System.out.println("'CheckNewMail()' found new mails" + new Date().toString()+"/n");
            //    isFoundNewMessages = true;
            //    break;
            //}
            //}

            if (folder != null)
            {
                folder.close(true);
                store.close();
            }

            if (isFoundNewMessages)
            {
                MessageResults = FetchMails();

                CheckMailThread = null;
                //stopThread(this);

//                if (listener != null)
//                {
//                    // Now let's fire listener here
//                    listener.onDataLoaded(MessageResults);
//                }
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

}
