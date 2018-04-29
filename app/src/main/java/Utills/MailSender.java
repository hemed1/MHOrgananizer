package Utills;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by meirh on 22/04/2018.
 */

public class MailSender extends AsyncTask<Void,Void,Void>
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
    public MailSender(Context context, String email, String subject, String message)
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

        return null;
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
            Toast.makeText(context, "Error n send mail",Toast.LENGTH_LONG).show();
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
