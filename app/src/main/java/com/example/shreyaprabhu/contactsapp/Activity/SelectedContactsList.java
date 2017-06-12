package com.example.shreyaprabhu.contactsapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shreyaprabhu.contactsapp.ContactsUtil.Contact;
import com.example.shreyaprabhu.contactsapp.R;
import com.example.shreyaprabhu.contactsapp.Realm.RealmController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class SelectedContactsList extends AppCompatActivity {

    private Realm realm;
    private TextView totalContacts;
    private TextView selectedContacts;
    ArrayList<Contact> list;
    //private Button homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_contacts_list);

        totalContacts = (TextView) findViewById(R.id.totalContacts);
        selectedContacts = (TextView) findViewById(R.id.selectedContacts);
        //homeButton = (Button) findViewById(R.id.homeButton);

        //get realm instance
        this.realm = RealmController.with(this).getRealm();

        //get alll contacts
        RealmResults<Contact> alLResult = realm.where(Contact.class).findAll();
        for (Contact mycontact : alLResult) {
            Log.e("allResult", mycontact.getContactName() + "\t" +
                    mycontact.getSelected() + "\t" + mycontact.getSelectedNumber());
        }
        int total = alLResult.size();

        //get selected contacts
        RealmResults<Contact> selectedResult = realm.where(Contact.class)
                .equalTo("selected", "true")
                .findAll();
        for (Contact mycontact : selectedResult) {

            Log.e("Selected", mycontact.getContactName() + "\t" +
                    mycontact.getSelected() + "\t" + mycontact.getSelectedNumber());
        }


        int selected = selectedResult.size();

        totalContacts.setText(String.valueOf(total));
        selectedContacts.setText(String.valueOf(selected));

        /*
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent HomeActivity = new Intent(SelectedContactsList.this,MainActivity.class);
                startActivity(HomeActivity);
            }
        });

        */
    }

}






