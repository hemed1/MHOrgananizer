package com.example.meirh.mhorgananizer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
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

        txtUserName.setText(extras.getString(MainActivity.SETTING_MAIL_USER_NAME));
        txtEmailAddress.setText(extras.getString(MainActivity.SETTING_MAIL_EMAIL_ADDRESS));
        txtPassword.setText(extras.getString(MainActivity.SETTING_MAIL_EMAIL_PASSWORD));
        txtMailHostAddress.setText(extras.getString(MainActivity.SETTING_MAIL_EMAIL_HOST_ADDRESS));
        txtCheckMailInterval.setText(extras.getString(MainActivity.SETTING_MAIL_EMAIL_CHECK_INTERVAL));
        chkStayOnLine.setChecked(extras.getBoolean(MainActivity.SETTING_MAIL_EMAIL_STAY_ONLINE));
    }

    private void SaveSetting()
    {
        SaveByPrefsFile();

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

    private void SaveByFileOnDisk()
    {
        String textToSave = "";

        try
        {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(MainActivity.PREFS_FILE_NAME, Context.MODE_PRIVATE));

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(MainActivity.SETTING_MAIL_USER_NAME + ": " + txtUserName.getText().toString() + "\n");
            stringBuilder.append(MainActivity.SETTING_MAIL_EMAIL_ADDRESS + ": " + txtEmailAddress.getText().toString() + "\n");
            stringBuilder.append(MainActivity.SETTING_MAIL_EMAIL_PASSWORD + ": " + txtPassword.getText().toString() + "\n");
            stringBuilder.append(MainActivity.SETTING_MAIL_EMAIL_HOST_ADDRESS + ": " + txtMailHostAddress.getText().toString() + "\n");
            stringBuilder.append(MainActivity.SETTING_MAIL_EMAIL_CHECK_INTERVAL + ": " + txtCheckMailInterval.getText().toString() + "\n");
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
        setResult(RESULT_OK, intent);
        finish();
    }
}
