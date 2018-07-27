package com.example.meirh.mhorgananizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.example.meirh.mhorgananizer.R.string.app_name;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static String       MainUserName;
    public static String       MailAdresss;
    public static String       MailPassword;
    public static String       MailHostAdress;      // "imap.gmail.com"
    public static String       MailCheckMailInterval;
    public static boolean      MailStayOnLine;
    public static String       StorageSDCardName;
    // 1: Internal, 2: External, 3: Internal, External
    public static Integer      StorageLoadMode;

    public static final String  PREFS_NAME = "MHOrganaizerPrefsFile";
    public static final String  PREFS_FILE_NAME = "MHOrganaizer-Config.txt";    // Environment.getExternalStorageDirectory().getPath()+"/"+
    public static final String  SETTING_MAIL_USER_NAME = "UserName";
    public static final String  SETTING_MAIL_EMAIL_ADDRESS = "EmailAddress";
    public static final String  SETTING_MAIL_EMAIL_PASSWORD = "EmailPassword";
    public static final String  SETTING_MAIL_EMAIL_HOST_ADDRESS = "EmailHostAddress";
    public static final String  SETTING_MAIL_EMAIL_CHECK_INTERVAL = "EmailCheckMailInterval";
    public static final String  SETTING_MAIL_EMAIL_STAY_ONLINE = "MailStayOnLine";
    public static final String  STORAGE_SDCARD_NAME = "StorageSDCardName";
    public static final String  STORAGE_LOAD_MODE = "StorageLoadMode";

    public static final int     DEFUALT_MESSAGES_TO_READ = 30;

    private TextView    lblMainTitle;
    private ImageView   imageMails;
    private ImageView   imageMusic;
    private ImageView   imageMeetings;
    private ImageView   imageSetting;

    public final int    REQUEST_CODE_MAILS = 2;
    public final int    REQUEST_CODE_MUSIC = 3;
    public final int    REQUEST_CODE_MEETINGS = 4;
    public final int    REQUEST_CODE_SETTING = 5;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetIOControls();

        LoadSettings();

        lblMainTitle.setText("Hello "+MainUserName);
        lblMainTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.imageMail:
                Intent intentMail = new Intent(MainActivity.this, ActivityMails.class);
                intentMail.putExtra(MainActivity.SETTING_MAIL_USER_NAME, MainUserName);
                intentMail.putExtra(MainActivity.SETTING_MAIL_EMAIL_ADDRESS, MailAdresss);
                intentMail.putExtra(MainActivity.SETTING_MAIL_EMAIL_PASSWORD, MailPassword);
                intentMail.putExtra(MainActivity.SETTING_MAIL_EMAIL_HOST_ADDRESS, MailHostAdress);
                intentMail.putExtra(MainActivity.SETTING_MAIL_EMAIL_STAY_ONLINE, MailStayOnLine);
                startActivityForResult(intentMail, REQUEST_CODE_MAILS);
                break;

            case R.id.imageMusic:
                Intent intentMusic = new Intent(MainActivity.this, ActivityMusic.class);
                startActivity(intentMusic);
                break;

            case R.id.imageMeeting:
                Intent intentMeeting = new Intent(MainActivity.this, ActivityMeetings.class);
                intentMeeting.putExtra("email_address", MailAdresss);
                intentMeeting.putExtra("email_password", MailPassword);
                intentMeeting.putExtra("email_hostAddress", MailPassword);
                startActivityForResult(intentMeeting, REQUEST_CODE_MEETINGS);
                break;

            case R.id.imageSetting:
                Intent intentSetting = new Intent(MainActivity.this, ActivitySettings.class);
                intentSetting.putExtra(MainActivity.SETTING_MAIL_USER_NAME, MainUserName);
                intentSetting.putExtra(MainActivity.SETTING_MAIL_EMAIL_ADDRESS, MailAdresss);
                intentSetting.putExtra(MainActivity.SETTING_MAIL_EMAIL_PASSWORD, MailPassword);
                intentSetting.putExtra(MainActivity.SETTING_MAIL_EMAIL_HOST_ADDRESS, MailHostAdress);
                intentSetting.putExtra(MainActivity.SETTING_MAIL_EMAIL_CHECK_INTERVAL, MailCheckMailInterval);
                intentSetting.putExtra(MainActivity.SETTING_MAIL_EMAIL_STAY_ONLINE, MailStayOnLine);

                startActivityForResult(intentSetting, REQUEST_CODE_SETTING);
                break;
        }
    }

    private void LoadSettings()
    {
        //LoadByPrefsFile();

        StorageLoadMode=1;
        StorageSDCardName="";
        MailHostAdress = "imap.gmail.com";

        LoadByFileOnDisk();
    }

    // Get Data back

    private void LoadByPrefsFile()
    {

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);

        if (prefs.contains(MainActivity.SETTING_MAIL_USER_NAME))     //TODO: getResources().getValue(app_name, TypedValue.TYPE_STRING, false)))
        {
            MainUserName = prefs.getString(MainActivity.SETTING_MAIL_USER_NAME, "Not Found");
        }
        if (prefs.contains(MainActivity.SETTING_MAIL_EMAIL_ADDRESS))
        {
            MailAdresss = prefs.getString(MainActivity.SETTING_MAIL_EMAIL_ADDRESS, "Not Found");
        }
        if (prefs.contains(MainActivity.SETTING_MAIL_EMAIL_PASSWORD))
        {
            MailPassword = prefs.getString(MainActivity.SETTING_MAIL_EMAIL_PASSWORD, "Not Found");
        }
        if (prefs.contains(MainActivity.SETTING_MAIL_EMAIL_HOST_ADDRESS))
        {
            MailHostAdress = prefs.getString(MainActivity.SETTING_MAIL_EMAIL_HOST_ADDRESS, "Not Found");
        }
        if (prefs.contains(MainActivity.SETTING_MAIL_EMAIL_STAY_ONLINE))
        {
            MailStayOnLine = prefs.getBoolean(MainActivity.SETTING_MAIL_EMAIL_STAY_ONLINE, false);
        }

    }

    public String LoadByFileOnDisk()
    {
        String result ="";
        int    pos;

        try
        {
            //FileInputStream fileInputStream = openFileInput(PREFS_FILE_NAME);
            InputStream inputStream = openFileInput(PREFS_FILE_NAME);
            String path = new File(PREFS_FILE_NAME).getAbsolutePath();

            if (inputStream!=null)
            {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String  tmpLine;
                StringBuilder stringBuilder = new StringBuilder();

                while ((tmpLine = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(tmpLine + "\n");

                    //MainUserName = GetWordValue(SETTING_MAIL_USER_NAME);
                    pos = tmpLine.indexOf(SETTING_MAIL_USER_NAME);
                    if (pos>-1 && (pos + SETTING_MAIL_USER_NAME.length() + 1 < tmpLine.length()))
                    {
                        MainUserName = tmpLine.substring(pos + SETTING_MAIL_USER_NAME.length() + 2);
                    }
                    pos = tmpLine.indexOf(SETTING_MAIL_EMAIL_ADDRESS);
                    if (pos>-1 && (pos + SETTING_MAIL_EMAIL_ADDRESS.length() + 1 < tmpLine.length()))
                    {
                        MailAdresss = tmpLine.substring(pos + SETTING_MAIL_EMAIL_ADDRESS.length() + 2);
                    }
                    pos = tmpLine.indexOf(SETTING_MAIL_EMAIL_PASSWORD);
                    if (pos>-1 && (pos + SETTING_MAIL_EMAIL_PASSWORD.length() + 1 < tmpLine.length()))
                    {
                        MailPassword = tmpLine.substring(pos + SETTING_MAIL_EMAIL_PASSWORD.length() + 2);
                    }
                    pos = tmpLine.indexOf(SETTING_MAIL_EMAIL_HOST_ADDRESS);
                    if (pos>-1 && (pos + SETTING_MAIL_EMAIL_HOST_ADDRESS.length() + 1 < tmpLine.length()))
                    {
                        MailHostAdress = tmpLine.substring(pos + SETTING_MAIL_EMAIL_HOST_ADDRESS.length() + 2);
                    }
                    pos = tmpLine.indexOf(SETTING_MAIL_EMAIL_CHECK_INTERVAL);
                    if (pos>-1 && (pos + SETTING_MAIL_EMAIL_CHECK_INTERVAL.length() + 1 < tmpLine.length()))
                    {
                        MailCheckMailInterval = tmpLine.substring(pos + SETTING_MAIL_EMAIL_CHECK_INTERVAL.length() + 2);
                    }
                    pos = tmpLine.indexOf(STORAGE_SDCARD_NAME);
                    if (pos>-1 && (pos + STORAGE_SDCARD_NAME.length() + 1 < tmpLine.length()))
                    {
                        StorageSDCardName = tmpLine.substring(pos + STORAGE_SDCARD_NAME.length() + 2);
                    }
                    pos = tmpLine.indexOf(MainActivity.STORAGE_LOAD_MODE);
                    if (pos>-1 && (pos + MainActivity.STORAGE_LOAD_MODE.length() + 1 < tmpLine.length()))
                    {
                        MainActivity.StorageLoadMode = Integer.valueOf(tmpLine.substring(pos + MainActivity.STORAGE_LOAD_MODE.length() + 2));
                    }
                    pos = tmpLine.indexOf(SETTING_MAIL_EMAIL_STAY_ONLINE);
                    if (pos>-1 && (pos + SETTING_MAIL_EMAIL_STAY_ONLINE.length() + 1 < tmpLine.length()))
                    {
                        MailStayOnLine = Boolean.valueOf(tmpLine.substring(pos + SETTING_MAIL_EMAIL_STAY_ONLINE.length() + 2));
                    }
                }

                inputStream.close();
                result = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    private String GetWordValue(String textToSearch, String keyWordToSearch)
    {
        int  pos = textToSearch.indexOf(SETTING_MAIL_USER_NAME);
        String  result = "";

        if (pos>-1)
        {
            result = textToSearch.substring(pos + keyWordToSearch.length() +1 );
        }

        return result;
    }

    private void SetIOControls()
    {
        lblMainTitle = (TextView)findViewById(R.id.lblMainTitle);
        imageMails = (ImageView)findViewById(R.id.imageMail);
        imageMusic = (ImageView)findViewById(R.id.imageMusic);
        imageMeetings = (ImageView)findViewById(R.id.imageMeeting);
        imageSetting = (ImageView)findViewById(R.id.imageSetting);

        imageMails.setOnClickListener(this);
        imageMusic.setOnClickListener(this);
        imageMeetings.setOnClickListener(this);
        imageSetting.setOnClickListener(this);
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            String result = data.getStringExtra("returnedData");
            //Toast.makeText(MainActivity.this, "Returned data: " + result, Toast.LENGTH_LONG).show();
        }
        else
        {
            //Toast.makeText(MainActivity.this, "No Data", Toast.LENGTH_LONG).show();
            return;
        }

        switch (requestCode)
        {
            case REQUEST_CODE_MAILS:
                break;

            case REQUEST_CODE_SETTING:
                LoadSettings();
                break;
        }
    }
}
