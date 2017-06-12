package com.example.shreyaprabhu.contactsapp.ContactsUtil;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shreyaprabhu.contactsapp.Activity.ContactList;
import com.example.shreyaprabhu.contactsapp.DialogUtils.DialogAdapter;
import com.example.shreyaprabhu.contactsapp.R;
import com.example.shreyaprabhu.contactsapp.Realm.RealmController;
import com.example.shreyaprabhu.contactsapp.Realm.RealmRecyclerViewAdapater;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * Created by ShreyaPrabhu on 10-06-2017.
 */

public class ContactsAdapter extends RealmRecyclerViewAdapater<Contact> {

    private final String TAG1 = ContactsAdapter.class.getSimpleName();
    private Realm realm;
    private static Context mContext;

    public ContactsAdapter(Context context) {
        super();
        mContext = context;
    }

    @Override
    public ContactItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
        return new ContactItemViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewholder, final int position) {
        realm = RealmController.getInstance().getRealm();
        final Contact contact = getItem(position);
        Log.v(TAG1,"position" +position);
        final ContactItemViewHolder holder = (ContactItemViewHolder) viewholder;

        //retireve contact name
        holder.contact_name.setText(contact.getContactName());
        Log.v(TAG1, "getContactName " + contact.getContactName());

        //retrieve byte[] and convert to bitmap
        byte[] bitmapdata = contact.getPhoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
        Log.e("bitmap", bitmap + " " + contact.getContactName());
        if(bitmap==null)
            holder.contact_image.setImageResource(R.drawable.contact_image);
        else
            holder.contact_image.setImageBitmap(bitmap);

        //check if it was selected previously by user
        if(contact.getSelected().equals("true"))
            holder.select_contact.setChecked(true);
        else
            holder.select_contact.setChecked(false);

        //Add onclick to checkbox, commit changes to realm simultaneously
        holder.select_contact.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v ;

                if(contact.getSelected().equals("true"))
                {
                    cb.setChecked(false);
                    //change value of selected to false for the object
                    realm.beginTransaction();
                    contact.setSelected("false");
                    contact.setSelectedNumber("null");
                    realm.commitTransaction();
                    notifyDataSetChanged();

                }
                else if(contact.getSelected().equals("false"))
                {
                    cb.setChecked(true);
                    //change value of selected to true for the object
                    realm.beginTransaction();
                    contact.setSelected("true");
                    realm.commitTransaction();

                    //if more than one mobile number is saved for a contact, show dialog box to select one
                    showDialogtoSelect(contact, viewholder.getAdapterPosition());

                    notifyDataSetChanged();

                }

            }
        });
    }

    //if more than one mobile number is saved for a contact, show dialog box to select one
    public void showDialogtoSelect(final Contact contact, int position){

        int count = contact.getPhoneNumTypes().size();
        Log.e("numbers", contact.getContactName() + " " + count);
        if(count > 1){
            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.custom_dialog);
            TextView dialogTitle = (TextView) dialog.findViewById(R.id.DialogTitle);
            TextView dialogOkay = (TextView) dialog.findViewById(R.id.dialogOkay);
            RecyclerView dialogRecycler = (RecyclerView) dialog.findViewById(R.id.customDialogRecycler);
            ArrayList<String> numbers = new ArrayList<String>();
            final DialogAdapter dialogAdapter = new DialogAdapter(dialog.getContext(),numbers,dialog);
            for(int i=0;i<count;i++){
                numbers.add(contact.getPhoneNumTypes().get(i).getContactNumber());
                Log.e("added", numbers.get(i));
                dialogAdapter.notifyDataSetChanged();
            }
            dialogRecycler.setLayoutManager(new GridLayoutManager(dialog.getContext(), 1));
            dialogRecycler.setAdapter(dialogAdapter);
            Log.e("size", " " + dialogAdapter.getItemCount());
            dialogTitle.setText(contact.getContactName());

            dialogOkay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //dismiss dialog on click of OKAY buttom only if a number is chosen
                    dialogAdapter.dialogDismissMethod(dialog, realm, contact);
                }
            });
            dialog.show();
        }
        else if(count==1){
            realm.beginTransaction();
            contact.setSelectedNumber(contact.getPhoneNumTypes().get(0).getContactNumber());
            realm.commitTransaction();
        }

    }

    public int getItemCount() {

        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    public class ContactItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView contact_image;
        private TextView contact_name;
        private CheckBox select_contact;

        public ContactItemViewHolder(View itemView) {
            super(itemView);
            contact_image = (ImageView) itemView.findViewById(R.id.contact_image);
            contact_name = (TextView) itemView.findViewById(R.id.contact_name);
            select_contact = (CheckBox) itemView.findViewById(R.id.select_contact);
        }
    }

}


