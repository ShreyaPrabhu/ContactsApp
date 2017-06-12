package com.example.shreyaprabhu.contactsapp.DialogUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shreyaprabhu.contactsapp.Activity.ContactList;
import com.example.shreyaprabhu.contactsapp.Activity.SelectedContactsList;
import com.example.shreyaprabhu.contactsapp.ContactsUtil.Contact;
import com.example.shreyaprabhu.contactsapp.R;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmObject;


/**
 * Created by ShreyaPrabhu on 11-06-2017.
 */

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.DialogItemViewHolder> {

    private final String TAG1 = DialogAdapter.class.getSimpleName();
    private static RadioButton lastChecked;
    private static int lastCheckedPos;
    private static Dialog dialog;
    private ArrayList<String> dialogNumbersList = new ArrayList<>();
    private static Context mContext;

    public class DialogItemViewHolder extends RecyclerView.ViewHolder {
        private RadioButton dialogRadioButton;
        private TextView dialogRadioContent;

        public DialogItemViewHolder(View itemView) {
            super(itemView);
            dialogRadioButton = (RadioButton) itemView.findViewById(R.id.radioButton);
            dialogRadioContent = (TextView) itemView.findViewById(R.id.radioContent);
        }
    }

    public DialogAdapter(Context context, ArrayList<String> dialogNumbersList, Dialog dialog) {
        super();
        lastChecked = null;
        lastCheckedPos = -1;
        mContext = context;
        this.dialogNumbersList = dialogNumbersList;
        this.dialog = dialog;
    }


    @Override
    public DialogItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_list_item, parent, false);
        return new DialogItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DialogItemViewHolder holder, final int position) {

        String dialogAdapterNumber = dialogNumbersList.get(position);
        Log.v(TAG1,"dialogAdapterNumber" +dialogAdapterNumber);
        holder.dialogRadioContent.setText(dialogAdapterNumber);
        Log.e("addedinadapter", dialogAdapterNumber);
        holder.dialogRadioButton.setTag(position);

        //Add onclick for radiobutton
        holder.dialogRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton cb = (RadioButton) view ;
                int clickedPos = (Integer) cb.getTag();
                if(cb.isChecked())
                {
                    if((lastCheckedPos != -1) && (lastChecked != null))
                    {
                        lastChecked.setChecked(false);
                        cb.setChecked(true);
                        lastChecked = cb;
                        lastCheckedPos = clickedPos;
                    }
                    else
                    {
                        cb.setChecked(true);
                        lastChecked = cb;
                        lastCheckedPos = clickedPos;
                    }

                }
                else if(!cb.isChecked())
                {
                    //Do nothing
                }

            }
        });

    }

    //dismiss dialog on click of OKAY buttom only if a number is chosen
    public void dialogDismissMethod(Dialog dialog, Realm realm, Contact contact){

        if((lastCheckedPos != -1) && (lastChecked != null))
        {
            Log.e("checkpos", lastCheckedPos +" " + lastChecked);
            realm.beginTransaction();
            contact.setSelectedNumber(dialogNumbersList.get(lastCheckedPos));
            Log.e("selecNum",  dialogNumbersList.get(lastCheckedPos)  + " " + contact.getSelectedNumber());
            realm.commitTransaction();
            lastCheckedPos = -1;
            lastChecked = null;
            dialog.dismiss();
        }
        else
            Toast.makeText(mContext,"Select one Contact Number", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int getItemCount() {
        return dialogNumbersList.size();
    }

}
