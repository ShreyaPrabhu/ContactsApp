package com.example.shreyaprabhu.contactsapp.Realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.example.shreyaprabhu.contactsapp.ContactsUtil.Contact;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ShreyaPrabhu on 10-06-2017.
 */

public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }

    //clear all objects from Book.class
    public void clearAll() {

        realm.beginTransaction();
        realm.clear(Contact.class);
        realm.commitTransaction();
    }

    //find all objects in the Contact.class
    //find all objects
    public RealmResults<Contact> getContactsforRealm() {

        return realm.where(Contact.class).findAll();
    }

    //query a single item with the given id
    public Contact getOneContactforRealm(String id) {

        return realm.where(Contact.class).equalTo("id", id).findFirst();
    }


    //check if Contact.class is empty
    public boolean hasContacts() {

        return !realm.allObjects(Contact.class).isEmpty();
    }

}
