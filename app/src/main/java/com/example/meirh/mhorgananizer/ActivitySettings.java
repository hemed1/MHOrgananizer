package com.example.meirh.mhorgananizer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ActivitySettings extends AppCompatActivity
{

    private Button          btnSave;
    private Button          btnBack;
    private EditText        txtEmailAddress;
    private EditText        txtPassword;
    private EditText        txtUserName;
    private EditText        txtMailHostAddress;
    private EditText        txtCheckMailInterval;
    private EditText        txtStorageSDCardName;
    private EditText        txtStorageLoadMode;
    private CheckBox        chkStayOnLine;

    private Bundle          extras;
    private SharedPreferences myPrefs;
    private static final String PREFS_NAME = "MHOrganaizerPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnSave = (Button) this.findViewById(R.id.btnSave);
        btnBack = (Button) this.findViewById(R.id.btnBack);
        txtUserName = (EditText) this.findViewById(R.id.txtUsserName);
        txtEmailAddress = (EditText) this.findViewById(R.id.txtEmailAddress);
        txtPassword = (EditText) this.findViewById(R.id.txtPassword);
        txtMailHostAddress = (EditText) this.findViewById(R.id.txtMailHostAddress);
        txtCheckMailInterval = (EditText) this.findViewById(R.id.txtCheckMailInterval);
        txtStorageSDCardName = (EditText) this.findViewById(R.id.txtStorageSDCardName);
        txtStorageLoadMode = (EditText) this.findViewById(R.id.txtStorageLoadMode);
        chkStayOnLine = (CheckBox) this.findViewById(R.id.chkStayOnLine);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveSetting();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                goBack();
            }
        });

        LoadDefaults();
    }

    private void LoadDefaults()
    {
        extras = getIntent().getExtras();

        if (extras==null)
        {
            return;
        }

        LoadByFileOnDisk();

        txtUserName.setText(MainActivity.MainUserName);
        txtEmailAddress.setText(MainActivity.MailAdresss);
        txtPassword.setText(MainActivity.MailPassword);
        txtMailHostAddress.setText(MainActivity.MailHostAdress);
        txtCheckMailInterval.setText(MainActivity.MailCheckMailInterval);
        chkStayOnLine.setChecked(MainActivity.MailStayOnLine);
        txtStorageSDCardName.setText(MainActivity.StorageSDCardName);
        txtStorageLoadMode.setText(String.valueOf(MainActivity.StorageLoadMode));

//        txtUserName.setText(extras.getString(MainActivity.SETTING_MAIL_USER_NAME));
//        txtEmailAddress.setText(extras.getString(MainActivity.SETTING_MAIL_EMAIL_ADDRESS));
//        txtPassword.setText(extras.getString(MainActivity.SETTING_MAIL_EMAIL_PASSWORD));
//        txtMailHostAddress.setText(extras.getString(MainActivity.SETTING_MAIL_EMAIL_HOST_ADDRESS));
//        txtCheckMailInterval.setText(extras.getString(MainActivity.SETTING_MAIL_EMAIL_CHECK_INTERVAL));
//        chkStayOnLine.setChecked(extras.getBoolean(MainActivity.SETTING_MAIL_EMAIL_STAY_ONLINE));
//        txtStorageSDCardName.setText(extras.getString(MainActivity.STORAGE_SDCARD_NAME));
//        txtStorageLoadMode.setText(String.valueOf(extras.getInt(MainActivity.STORAGE_LOAD_MODE)));
    }

    private void SaveSetting()
    {
        //SaveByPrefsFile();

        SaveByFileOnDisk();

        goBack();
    }

    private void SaveByPrefsFile()
    {
        myPrefs = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor  editor = myPrefs.edit();
        //editor.clear();

        editor.putString(MainActivity.SETTING_MAIL_USER_NAME, txtUserName.getText().toString());
        editor.putString(MainActivity.SETTING_MAIL_EMAIL_ADDRESS, txtEmailAddress.getText().toString());
        editor.putString(MainActivity.SETTING_MAIL_EMAIL_PASSWORD, txtPassword.getText().toString());
        editor.putString(MainActivity.SETTING_MAIL_EMAIL_HOST_ADDRESS, txtMailHostAddress.getText().toString());  //TODO:
        editor.putString(MainActivity.SETTING_MAIL_EMAIL_CHECK_INTERVAL, txtCheckMailInterval.getText().toString());
        editor.putBoolean(MainActivity.SETTING_MAIL_EMAIL_STAY_ONLINE, chkStayOnLine.isChecked());

        editor.commit();
    }

    public String LoadByFileOnDisk()
    {
        String result ="";
        int    pos;

        try
        {
            //FileInputStream fileInputStream = openFileInput(PREFS_FILE_NAME);
            InputStream inputStream = openFileInput(MainActivity.PREFS_FILE_NAME);
            String path = new File(MainActivity.PREFS_FILE_NAME).getAbsolutePath();

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
                    pos = tmpLine.indexOf(MainActivity.SETTING_MAIL_USER_NAME);
                    if (pos>-1 && (pos + MainActivity.SETTING_MAIL_USER_NAME.length() + 1 < tmpLine.length()))
                    {
                        MainActivity.MainUserName = tmpLine.substring(pos + MainActivity.SETTING_MAIL_USER_NAME.length() + 2);
                    }
                    pos = tmpLine.indexOf(MainActivity.SETTING_MAIL_EMAIL_ADDRESS);
                    if (pos>-1 && (pos + MainActivity.SETTING_MAIL_EMAIL_ADDRESS.length() + 1 < tmpLine.length()))
                    {
                        MainActivity.MailAdresss = tmpLine.substring(pos + MainActivity.SETTING_MAIL_EMAIL_ADDRESS.length() + 2);
                    }
                    pos = tmpLine.indexOf(MainActivity.SETTING_MAIL_EMAIL_PASSWORD);
                    if (pos>-1 && (pos + MainActivity.SETTING_MAIL_EMAIL_PASSWORD.length() + 1 < tmpLine.length()))
                    {
                        MainActivity.MailPassword = tmpLine.substring(pos + MainActivity.SETTING_MAIL_EMAIL_PASSWORD.length() + 2);
                    }
                    pos = tmpLine.indexOf(MainActivity.SETTING_MAIL_EMAIL_HOST_ADDRESS);
                    if (pos>-1 && (pos + MainActivity.SETTING_MAIL_EMAIL_HOST_ADDRESS.length() + 1 < tmpLine.length()))
                    {
                        MainActivity.MailHostAdress = tmpLine.substring(pos + MainActivity.SETTING_MAIL_EMAIL_HOST_ADDRESS.length() + 2);
                    }
                    pos = tmpLine.indexOf(MainActivity.SETTING_MAIL_EMAIL_CHECK_INTERVAL);
                    if (pos>-1 && (pos + MainActivity.SETTING_MAIL_EMAIL_CHECK_INTERVAL.length() + 1 < tmpLine.length()))
                    {
                        MainActivity.MailCheckMailInterval = tmpLine.substring(pos + MainActivity.SETTING_MAIL_EMAIL_CHECK_INTERVAL.length() + 2);
                    }
                    pos = tmpLine.indexOf(MainActivity.STORAGE_SDCARD_NAME);
                    if (pos>-1 && (pos + MainActivity.STORAGE_SDCARD_NAME.length() + 1 < tmpLine.length()))
                    {
                        MainActivity.StorageSDCardName = tmpLine.substring(pos + MainActivity.STORAGE_SDCARD_NAME.length() + 2);
                    }
                    pos = tmpLine.indexOf(MainActivity.STORAGE_LOAD_MODE);
                    if (pos>-1 && (pos + MainActivity.STORAGE_LOAD_MODE.length() + 1 < tmpLine.length()))
                    {
                        MainActivity.StorageLoadMode = Integer.valueOf(tmpLine.substring(pos + MainActivity.STORAGE_LOAD_MODE.length() + 2));
                    }
                    pos = tmpLine.indexOf(MainActivity.SETTING_MAIL_EMAIL_STAY_ONLINE);
                    if (pos>-1 && (pos + MainActivity.SETTING_MAIL_EMAIL_STAY_ONLINE.length() + 1 < tmpLine.length()))
                    {
                        MainActivity.MailStayOnLine = Boolean.valueOf(tmpLine.substring(pos + MainActivity.SETTING_MAIL_EMAIL_STAY_ONLINE.length() + 2));
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

    private void SaveByFileOnDisk()
    {
        String textToSave = "";

        try
        {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    openFileOutput(MainActivity.PREFS_FILE_NAME, Context.MODE_PRIVATE));    //Environment.getExternalStorageDirectory().getPath()+"/"+

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(MainActivity.SETTING_MAIL_USER_NAME + ": " + txtUserName.getText().toString() + "\n");
            stringBuilder.append(MainActivity.SETTING_MAIL_EMAIL_ADDRESS + ": " + txtEmailAddress.getText().toString() + "\n");
            stringBuilder.append(MainActivity.SETTING_MAIL_EMAIL_PASSWORD + ": " + txtPassword.getText().toString() + "\n");
            stringBuilder.append(MainActivity.SETTING_MAIL_EMAIL_HOST_ADDRESS + ": " + txtMailHostAddress.getText().toString() + "\n");
            stringBuilder.append(MainActivity.SETTING_MAIL_EMAIL_CHECK_INTERVAL + ": " + txtCheckMailInterval.getText().toString() + "\n");
            stringBuilder.append(MainActivity.STORAGE_SDCARD_NAME + ": " + txtStorageSDCardName.getText().toString() + "\n");
            stringBuilder.append(MainActivity.STORAGE_LOAD_MODE + ": " + txtStorageLoadMode.getText().toString() + "\n");
            stringBuilder.append(MainActivity.SETTING_MAIL_EMAIL_STAY_ONLINE + ": " + String.valueOf(chkStayOnLine.isChecked()) + "\n");

            textToSave = stringBuilder.toString();
            outputStreamWriter.write(textToSave);
            outputStreamWriter.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void goBack()
    {
        String returnedData;

        returnedData = "Setting was saved";

        Intent intent = getIntent();
        intent.putExtra("returnedData",  returnedData);

        //Toast.makeText(this, "Come Back with ... " + returnedData, Toast.LENGTH_SHORT).show();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
