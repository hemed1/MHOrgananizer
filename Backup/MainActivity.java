package com.example.meirh.mhorgananizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static com.example.meirh.mhorgananizer.R.string.app_name;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static String       MainUserName;
    public static String       MailAdresss;
    public static String       MailPassword;
    public static String       MailHostAdress;
    public static boolean      MailStayOnLine;

    public static final String PREFS_NAME = "MHOrganaizerPrefsFile";

    public static final String  SETTING_MAIL_USER_NAME = "UserName";
    public static final String  SETTING_MAIL_EMAIL_ADDRESS = "EmailAddress";
    public static final String  SETTING_MAIL_EMAIL_PASSWORD = "EmailPassword";
    public static final String  SETTING_MAIL_EMAIL_HOST_ADDRESS = "EmailHostAddress";
    public static final String  SETTING_MAIL_EMAIL_STAY_ONLINE = "MailStayOnLine";

    private Button      btnMails;
    private Button      btnMusic;
    private Button      btnMeetings;
    private Button      btnSetting;

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
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnMails:
                Intent intentMail = new Intent(MainActivity.this, ActivityMails.class);
                intentMail.putExtra("email_address", MailAdresss);
                intentMail.putExtra("email_password", MailPassword);
                intentMail.putExtra("email_hostAddress", MailPassword);
                startActivityForResult(intentMail, REQUEST_CODE_MAILS);
                break;

            case R.id.btnMusic:
                Intent intentMusic = new Intent(MainActivity.this, ActivityMusic.class);
                startActivity(intentMusic);
                break;

            case R.id.btnMeetings:
                Intent intentMeeting = new Intent(MainActivity.this, ActivityMeetings.class);
                intentMeeting.putExtra("email_address", MailAdresss);
                intentMeeting.putExtra("email_password", MailPassword);
                intentMeeting.putExtra("email_hostAddress", MailPassword);
                startActivityForResult(intentMeeting, REQUEST_CODE_MEETINGS);
                break;

            case R.id.btnSetting:
                Intent intentSetting = new Intent(MainActivity.this, ActivitySettings.class);
                intentSetting.putExtra(MainActivity.SETTING_MAIL_USER_NAME, MainUserName);
                intentSetting.putExtra(MainActivity.SETTING_MAIL_EMAIL_ADDRESS, MailAdresss);
                intentSetting.putExtra(MainActivity.SETTING_MAIL_EMAIL_PASSWORD, MailPassword);
                intentSetting.putExtra(MainActivity.SETTING_MAIL_EMAIL_HOST_ADDRESS, MailHostAdress);
                intentSetting.putExtra(MainActivity.SETTING_MAIL_EMAIL_STAY_ONLINE, MailStayOnLine);

                startActivityForResult(intentSetting, REQUEST_CODE_SETTING);
                break;
        }
    }

    private void LoadSettings()
    {
        LoadByPrefsFile();

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
            MailPassword     = prefs.getString(MainActivity.SETTING_MAIL_EMAIL_PASSWORD, "Not Found");
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

    private void LoadByFileOnDisk()
    {
    }

    private void SetIOControls()
    {
        btnMails = (Button)findViewById(R.id.btnMails);
        btnMusic = (Button)findViewById(R.id.btnMusic);
        btnMeetings = (Button)findViewById(R.id.btnMeetings);
        btnSetting = (Button)findViewById(R.id.btnSetting);

        btnMails.setOnClickListener(this);
        btnMusic.setOnClickListener(this);
        btnMeetings.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
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
            Toast.makeText(MainActivity.this, "No Data", Toast.LENGTH_LONG).show();
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
