package com.example.shreyaprabhu.contactsapp.Realm;

import io.realm.RealmObject;

/**
 * Created by ShreyaPrabhu on 11-06-2017.
 */

public class PhoneNumType extends RealmObject {

    private String contactNumber;
    private String contactType;

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }
}
