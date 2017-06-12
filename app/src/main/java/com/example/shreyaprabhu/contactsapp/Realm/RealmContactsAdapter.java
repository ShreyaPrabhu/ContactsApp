package com.example.shreyaprabhu.contactsapp.Realm;

import android.content.Context;

import com.example.shreyaprabhu.contactsapp.ContactsUtil.Contact;

import io.realm.RealmResults;

/**
 * Created by ShreyaPrabhu on 10-06-2017.
 */

public class RealmContactsAdapter extends RealmModelAdapter<Contact>{

    public RealmContactsAdapter(Context context, RealmResults<Contact> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
}
