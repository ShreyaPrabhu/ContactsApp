package com.example.shreyaprabhu.contactsapp.ContactsUtil;


import android.graphics.Bitmap;

import com.example.shreyaprabhu.contactsapp.Realm.PhoneNumType;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ShreyaPrabhu on 10-06-2017.
 */

public class Contact extends RealmObject{

    @PrimaryKey
    private String contactID;
    private String contactName;
    private String selected;
    private RealmList<PhoneNumType> phoneNumTypes = new RealmList<>();
    private String selectedNumber = "null";
    private byte[] photo = null;

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public RealmList<PhoneNumType> getPhoneNumTypes() {
        return phoneNumTypes;
    }

    public void setPhoneNumTypes(RealmList<PhoneNumType> phoneNumTypes) {
        this.phoneNumTypes = phoneNumTypes;
    }

    public String getSelectedNumber() {
        return selectedNumber;
    }

    public void setSelectedNumber(String selectedNumber) {
        this.selectedNumber = selectedNumber;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

}
