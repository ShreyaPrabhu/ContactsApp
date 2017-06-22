package com.example.shreyaprabhu.contactsapp.Activity;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.shreyaprabhu.contactsapp.ContactsUtil.Contact;
import com.example.shreyaprabhu.contactsapp.ContactsUtil.ContactsAdapter;
import com.example.shreyaprabhu.contactsapp.R;
import com.example.shreyaprabhu.contactsapp.Realm.PhoneNumType;
import com.example.shreyaprabhu.contactsapp.Realm.RealmContactsAdapter;
import com.example.shreyaprabhu.contactsapp.Realm.RealmController;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class ContactList extends AppCompatActivity {

    Context context = this;
    private final String TAG = ContactList.class.getSimpleName();
    private Realm realm;

    public Button selectbutton;
    RecyclerView contactRecycler;
    ContactsAdapter contactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        realm = RealmController.with(this).getRealm();
        selectbutton = (Button) findViewById(R.id.select_button);


        selectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent SelectedContactsList = new Intent(ContactList.this,SelectedContactsList.class);
                startActivity(SelectedContactsList);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        contactRecycler = (RecyclerView) findViewById(R.id.contactRecycler);
        setupRecycler();


        try {
            readPhoneContacts(context);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }

        RealmController.with(this).refresh();
        setRealmAdapter(RealmController.with(this).getContactsforRealm());

    }

    public void setRealmAdapter(RealmResults<Contact> sraContacts) {

        RealmContactsAdapter realmAdapter = new RealmContactsAdapter(this.getApplicationContext(), sraContacts, true);
        // Set the data and tell the RecyclerView to draw
        contactsAdapter.setRealmAdapter(realmAdapter);
        contactsAdapter.notifyDataSetChanged();
    }

    private void setupRecycler() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        contactRecycler.setHasFixedSize(true);

        // use a linear layout manager since the cards are vertically scrollable
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        contactRecycler.setLayoutManager(layoutManager);

        // create an empty adapter and add it to the recycler view
        contactsAdapter = new ContactsAdapter(this);
        contactRecycler.setAdapter(contactsAdapter);
    }


    //Read contacts from the contacts app list and store it in database. ]
    //The contacts are added only if it was previously not added

    public void readPhoneContacts(Context context) throws NumberParseException {


        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        assert cursor != null;
        Integer contactsCount = cursor.getCount(); // get how many contacts you have in your contacts list
        if (contactsCount > 0) {

            while (cursor.moveToNext()) {
                final Contact rpcContact = new Contact();
                RealmList<PhoneNumType> phoneNumTypes = new RealmList<>();
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                RealmResults<Contact> res = realm.where(Contact.class)
                        .equalTo("contactID", id).findAll();

                if (res.isEmpty()) {

                    String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    rpcContact.setSelected("false");
                    rpcContact.setContactName(contactName);
                    rpcContact.setContactID(id);

                    //retrive photo
                    Bitmap photo = null;
                    try {
                        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getApplicationContext().getContentResolver(),
                                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id)));

                        if (inputStream != null) {
                            photo = BitmapFactory.decodeStream(inputStream);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            rpcContact.setPhoto(byteArray);
                        }

                        if (inputStream != null) inputStream.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("--> ", photo + "");

                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        //the below cursor will give you details for multiple contacts
                        Cursor pCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        HashSet<String> set = new HashSet<String>();
                        // continue till this cursor reaches to all phone numbers which are associated with a contact in the contact list
                        assert pCursor != null;
                        while (pCursor.moveToNext()) {

                            int phoneType = pCursor.getInt(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            String phoneNo = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                           if (isValidPhone(phoneNo)) {
                                if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                                    String spacesRemoved = phoneNo.replaceAll("\\s","");
                                    if(isMatch(phoneNo,set))
                                    {
                                        set.add(spacesRemoved);
                                        Log.e("i", spacesRemoved);
                                        Log.e("phoneType", String.valueOf(phoneType));
                                    }

                                }
                            }

                        }
                        realm.beginTransaction();
                        for (String pNT : set) {
                            PhoneNumType phoneNumType1 = new PhoneNumType();
                            phoneNumType1.setContactNumber(pNT);
                            phoneNumType1.setContactType("2");
                            phoneNumTypes.add(phoneNumType1);
                        }
                        realm.commitTransaction();
                        pCursor.close();
                    }
                    rpcContact.setPhoneNumTypes(phoneNumTypes);
                    for(int i = 0; i< phoneNumTypes.size();i++)
                        Log.e("PhoneNumTypes", phoneNumTypes.get(i).getContactNumber() + " " + phoneNumTypes.get(i).getContactType() + "\n");

                    realm.beginTransaction();
                    realm.copyToRealm(rpcContact);
                    realm.commitTransaction();
                    contactsAdapter.notifyDataSetChanged();

                    // scroll the recycler view to bottom if new entry is added
                    contactRecycler.scrollToPosition(RealmController.getInstance().getContactsforRealm().size() - 1);

                }



            }
        }
        cursor.close();

    }


    public static boolean isValidPhone(String phone)
    {
        String expression = "^([0-9\\+]|\\(\\d{1,3}\\))[0-9\\-\\. ]{3,16}$";
        CharSequence inputString = phone;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputString);
        return matcher.matches();
    }

    public static boolean isMatch(String phone1, HashSet<String> set){
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

        for(String phone2 : set)
        {
            Enum matchType = phoneNumberUtil.isNumberMatch(phone1,phone2);
            Log.e("phone1", phone1);
            Log.e("phone2", phone2);
            Log.e("matchtype", matchType.toString());
            if(matchType.toString().equals("EXACT_MATCH")
                    || matchType.toString().equals("NSN_MATCH")
                    || matchType.toString().equals("SHORT_NSN_MATCH"))
                return false;
        }
        return true;

    }

}
