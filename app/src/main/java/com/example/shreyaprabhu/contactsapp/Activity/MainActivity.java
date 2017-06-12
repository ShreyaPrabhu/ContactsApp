package com.example.shreyaprabhu.contactsapp.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.shreyaprabhu.contactsapp.R;


import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

    Button getContactsButton;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 20;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        textView = (TextView) findViewById(R.id.text);
        textView.setText("");

        getContactsButton = (Button) findViewById(R.id.getContactsButton);
        getContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Ask for permissions to read contacts if its first time use

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.READ_CONTACTS)) {

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);


                    } else {

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                    }
                }
                else
                {

                        textView.setText("");
                        Toast.makeText(MainActivity.this,"Loading contacts may take some time....", Toast.LENGTH_SHORT).show();
                        Intent ContactListActivity = new Intent(MainActivity.this,ContactList.class);
                        startActivity(ContactListActivity);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);


                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    textView.setText("Loading contacts may take some time....");
                    Intent ContactListActivity = new Intent(MainActivity.this,ContactList.class);
                    startActivity(ContactListActivity);


                } else {

                    Toast.makeText(MainActivity.this, "Grant Permission to proceed", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

}
