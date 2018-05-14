package Utills;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * Created by meirh on 22/04/2018.
 */

public class MailSender     //extends AsyncTask<Void,Void,Void>
{

    //Declaring Variables
    private Context context;
    private Session session;

    private String UserAddress = "hemedmeir@gmail.com";    // I purposefully hid these
    private String Password = "13579Mot";           // I purposefully hid these

    //Information to send email
    private String emailAddress;
    private String subject;
    private String message;

    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;

    //Class Constructor
    public MailSender(Context context, String emailAddress, String subject, String message)
    {
        this.context = context;

        this.emailAddress = emailAddress;
        this.subject = subject;
        this.message = message;
    }


//    @Override
//    protected void onPreExecute()
//    {
//        super.onPreExecute();
//
//        progressDialog = ProgressDialog.show(context,"Sending message","Please wait...",false,false);
//    }
//
//    @Override
//    protected void onPostExecute(Void aVoid)
//    {
//        super.onPostExecute(aVoid);
//        progressDialog.dismiss();
//
//        Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    protected Void doInBackground(Void... params)
//    {
//        //SendMailSimple();
//        SendMailSmpt();
//
//        return null;
//    }

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
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(UserAddress));
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

    public void SendMailsSmpt2()
    {

            //Message msg2;
            //msg2 = new Message() {
//                @Override
//                public Address[] getFrom() throws MessagingException
//                {
//                    return new Address[0];
//                }
//
//                @Override
//                public void setFrom() throws MessagingException
//                {
//                    setFrom(new InternetAddress(UserAddress));
////                            new Address() {
////                        @Override
////                        public String getType() {
////                            return null;
////                        }
////
////                        @Override
////                        public String toString() {
////                            return null;
////                        }
////
////                        @Override
////                        public boolean equals(Object o) {
////                            return false;
////                        }
////                    });
//                }
//
//                @Override
//                public void setFrom(Address address) throws MessagingException
//                {
////                    Address[]  addresses = new Address[1];
////                    addresses[0] = address;
////                    addFrom(addresses);
//                }
//
//                @Override
//                public void addFrom(Address[] addresses) throws MessagingException
//                {
//
//                }
//
//                @Override
//                public Address[] getRecipients(RecipientType recipientType) throws MessagingException
//                {
//                    return new Address[0];
//                }
//
//                @Override
//                public void setRecipients(RecipientType recipientType, Address[] addresses) throws MessagingException {
////                    Address  address = new InternetAddress("hemedmeir@gmail.com");
////                    Address[]  addresses2 = new Address[1];
////                    addresses2[0] = address;
////                    addRecipients(Message.RecipientType.TO, addresses);
//                }
//
//                @Override
//                public void addRecipients(RecipientType recipientType, Address[] addresses) throws MessagingException {
//
//                }
//
//                @Override
//                public String getSubject() throws MessagingException {
//                    return null;
//                }
//
//                @Override
//                public void setSubject(String s) throws MessagingException {
//
//                }
//
//                @Override
//                public Date getSentDate() throws MessagingException {
//                    return null;
//                }
//
//                @Override
//                public void setSentDate(Date date) throws MessagingException {
//
//                }
//
//                @Override
//                public Date getReceivedDate() throws MessagingException {
//                    return null;
//                }
//
//                @Override
//                public Flags getFlags() throws MessagingException {
//                    return null;
//                }
//
//                @Override
//                public void setFlags(Flags flags, boolean b) throws MessagingException {
//
//                }
//
//                @Override
//                public Message reply(boolean b) throws MessagingException {
//                    return null;
//                }
//
//                @Override
//                public void saveChanges() throws MessagingException {
//
//                }
//
//                @Override
//                public int getSize() throws MessagingException {
//                    return 0;
//                }
//
//                @Override
//                public int getLineCount() throws MessagingException {
//                    return 0;
//                }
//
//                @Override
//                public String getContentType() throws MessagingException {
//                    return null;
//                }
//
//                @Override
//                public boolean isMimeType(String s) throws MessagingException {
//                    return false;
//                }
//
//                @Override
//                public String getDisposition() throws MessagingException {
//                    return null;
//                }
//
//                @Override
//                public void setDisposition(String s) throws MessagingException {
//
//                }
//
//                @Override
//                public String getDescription() throws MessagingException {
//                    return null;
//                }
//
//                @Override
//                public void setDescription(String s) throws MessagingException {
//
//                }
//
//                @Override
//                public String getFileName() throws MessagingException {
//                    return null;
//                }
//
//                @Override
//                public void setFileName(String s) throws MessagingException {
//
//                }
//
//                @Override
//                public InputStream getInputStream() throws IOException, MessagingException {
//                    return null;
//                }
//
//                @Override
//                public DataHandler getDataHandler() throws MessagingException {
//                    return null;
//                }
//
//                @Override
//                public Object getContent() throws IOException, MessagingException {
//                    return null;
//                }
//
//                @Override
//                public void setDataHandler(DataHandler dataHandler) throws MessagingException {
//
//                }
//
//                @Override
//                public void setContent(Object o, String s) throws MessagingException {
//
//                }
//
//                @Override
//                public void setText(String s) throws MessagingException {
//
//                }
//
//                @Override
//                public void setContent(Multipart multipart) throws MessagingException {
//
//                }
//
//                @Override
//                public void writeTo(OutputStream outputStream) throws IOException, MessagingException {
//
//                }
//
//                @Override
//                public String[] getHeader(String s) throws MessagingException {
//                    return new String[0];
//                }
//
//                @Override
//                public void setHeader(String s, String s1) throws MessagingException {
//
//                }
//
//                @Override
//                public void addHeader(String s, String s1) throws MessagingException {
//
//                }
//
//                @Override
//                public void removeHeader(String s) throws MessagingException {
//
//                }
//
//                @Override
//                public Enumeration getAllHeaders() throws MessagingException {
//                    return null;
//                }
//
//                @Override
//                public Enumeration getMatchingHeaders(String[] strings) throws MessagingException {
//                    return null;
//                }
//
//                @Override
//                public Enumeration getNonMatchingHeaders(String[] strings) throws MessagingException {
//                    return null;
//                }
//            };

            //msg2.setSubject("Hello");


            try
            {
                Properties props = System.getProperties();
                // Get a Session object
                Session session = Session.getInstance(props, null);            // session.setDebug(true);
                // Get a Store object
                Store store = session.getStore("imap");
                // Connect
                store.connect("smpt.gmail.com", "hemedmeir@gmail.com", "13579Mot");
                // Open a Folder
                Folder folder = store.getFolder("Outbox");
                if (folder == null || !folder.exists())
                {
                    System.out.println("Invalid folder");                System.exit(1);
                }
                //80 Appendix B: Examples Using the JavaMail API Example: Sending a Message
                //August 2017  JavaMail™ API Design Specification
                folder.open(Folder.READ_WRITE);

            //msg2.setRecipient(Message.RecipientType.TO, new InternetAddress("hemedmeir@gmail.com"));
            //System.out.println(msg2.getRecipients(Message.RecipientType.TO)[0].toString());
            //msg2.setFrom(new InternetAddress("hemedmeir@gmail.com"));
            //System.out.println(msg2.getFrom()[0].toString());
            //msg2.setFrom();
            //msg2.setSubject("Hello");
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("hemedmeir@gmail.com"));
            msg.setRecipients(Message.RecipientType.TO,"hemedmeir@gmail.com");
            msg.setSubject("JavaMail hello world example");
            msg.setSentDate(new Date());
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText("kk");
                // Appendix B: Examples Using the JavaMail API Example: Sending a Message
                //August 2017  JavaMail™ API Design Specification
                // create and fill the second message par
                MimeBodyPart mbp2 = new MimeBodyPart();
                // Use setText(text, charset), to show it off !
                mbp2.setText("lll", "us-ascii");
                // create the Multipart and its parts to it
                Multipart mp = new MimeMultipart();
                mp.addBodyPart(mbp1);
                mp.addBodyPart(mbp2);
                // add the Multipart to the message
                msg.setContent(mp);
            msg.setText("Hello, world!\n");
            Transport.send(msg);   //, "me@example.com", "my-password");
            //Transport.send(msg2);   //, "me@example.com", "my-password");
        }
        catch (MessagingException mex)
        {
            System.out.println("send failed, exception: " + mex);
        }
    }

    public void SendMailsSmpt3()
    {
        String msgText1 = "This is a message body.\nHere's line two.";
        String msgText2 = "This is the text in the message attachment.";
        String to = "hemedmeir@gmail.com";
        String from = "hemedmeir@gmail.com";
        String host = "smpt.gmail.com";
        boolean debug = false;   //Boolean.valueOf(host.booleanValue();

        // create some properties and get the default Session
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getInstance(props, null);
        session.setDebug(debug);
        try
        {
            // create a message
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("JavaMail APIs Multipart Test");
            msg.setSentDate(new Date());
            // create and fill the first message part
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(msgText1);
            // create and fill the second message part
            MimeBodyPart mbp2 = new MimeBodyPart();
            mbp2.setText(msgText2, "us-ascii");
            // create the Multipart and its parts to it
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);
            mp.addBodyPart(mbp2);
            // add the Multipart to the message
            msg.setContent(mp);

            // send the message
            Transport.send(msg);
        }
        catch (MessagingException mex)
        {   mex.printStackTrace();
            Exception ex = null;
            if ((ex = mex.getNextException()) != null)
            {
                ex.printStackTrace();
            }
        }
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

        this.subject="Test Me";
        this.message = "Body test";

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
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
            //Toast.makeText(context, "Mail sent",Toast.LENGTH_LONG).show();
            //finish();
            result = true;
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(context, "Error n send mail: " + ex.getMessage(),Toast.LENGTH_SHORT).show();
        }


        return true;
    }
}
