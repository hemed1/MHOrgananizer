package Utills;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.AsyncTask;
import android.widget.Toast;
import java.util.Properties;
import  android.content.Intent;
import android.net.Uri;
import android.view.View;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.BodyPart;
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

//import static android.support.v4.content.ContextCompat.startActivity;
import android.support.v7.app.AppCompatActivity;


public class MailRead extends AsyncTask<Void, Void, Void>
{

    private String HostAddress = "pop.gmail.com";           // change accordingly
    private String StoreType = "pop3";
    private String UserAddress = "hemedmeir@gmail.com";     // I purposefully hid these
    private String Password = "13579Mot";                   // I purposefully hid these

    private Context context;
    private ProgressDialog progressDialog;


    public MailRead(Context context,String hostAddress, String userAddress, String password)
    {
        this.HostAddress = hostAddress;
        this.UserAddress = userAddress;
        this.Password = password;
        this.context = context;
    }

    public MailRead(Context context, String userAddress, String password)
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
        progressDialog = ProgressDialog.show(context, "Recive Mails... ", false, false);
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

    private boolean ReadMailImap()
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


    private boolean ReadMailPop3()
    {
        boolean  result = false;

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
            Message[] messages = emailFolder.getMessages();
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

            result = true;
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


public class MailSend extends AsyncTask<Void,Void,Void>
{

    //Declaring Variables
    private Context context;
    private Session session;

    private String UserAddress = "hemedmeir@gmail.com";    // I purposefully hid these
    private String Password = "13579Mot";           // I purposefully hid these

    //Information to send email
    private String email;
    private String subject;
    private String message;

    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;

    //Class Constructor
    public MailSend(Context context, String email, String subject, String message)
    {
        this.context = context;

        this.email = email;
        this.subject = subject;
        this.message = message;
    }


    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        progressDialog = ProgressDialog.show(context,"Sending message","Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();

        Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        SendMailSimple();
        //SendMailSmpt();
    }

    public boolean SendMailSmpt()
    {
        boolean  result = false;


        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(UserAddress, Password); //Config.EMAIL, Bitmap.Config.PASSWORD);
                    }
                });

        try
        {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            //Setting sender address
            mm.setFrom(new InternetAddress(UserAddress));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            //Adding subject
            mm.setSubject(subject);
            //Adding message
            mm.setText(message);

            //Sending email
            Transport.send(mm);

            result = true;
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public boolean SendMailSimple()
    {
        boolean  result = false;


        String[]    ToAddress;
        String[]    CCAddress;

        ToAddress = new String[1];
        CCAddress = new String[1];
        ToAddress[0] = "hemedmeir@gmail.com";
        CCAddress[0] = "hemedmeir@gmail.com";

        Intent  emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        //emailIntent.setType("message/rfc822");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, ToAddress);
        emailIntent.putExtra(Intent.EXTRA_CC, CCAddress);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, this.subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, this.message);
        //emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, this.message);

        try
        {
            context.startActivity(emailIntent);
            Toast.makeText(context, "Error n send mail",Toast.LENGTH_LONG).show();
            //finish();
            result = true;
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(context, "Error n send mail",Toast.LENGTH_SHORT).show();
        }


        return true;
    }
}
