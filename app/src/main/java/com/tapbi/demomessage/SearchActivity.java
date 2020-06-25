package com.tapbi.demomessage;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.demomessage.addapter.ContactAdapter;
import com.tapbi.demomessage.dto.ItemContact;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, ContactAdapter.OnCallBack {
    private RecyclerView rvContact;
    private List<ItemContact> contactList;
    private ContactAdapter contactAdapter;
    private EditText ed_search, ed_send;
    private ImageButton ib_back, ib_send_message;
    final int REQUEST_CODE_ASK_PERMISSIONS_CONTACT = 123;
    private String number ;
    private boolean sendSMS = false;
    private boolean clickItem = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_search);
        checkPermission();
    }

    public void checkPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_ASK_PERMISSIONS_CONTACT);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS_CONTACT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initView();
                }
            }
        }
    }
    
    private void initView() {
        rvContact = findViewById(R.id.rv_contact);
        rvContact.setHasFixedSize(true);
        
        contactList = getListContacts();
        contactAdapter = new ContactAdapter(contactList, this);
        rvContact.setAdapter(contactAdapter);
        rvContact.setLayoutManager(new LinearLayoutManager(this));

        ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(this);

        ed_search = findViewById(R.id.ed_search);

        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }

        });
    }

    private List<ItemContact> getListContacts() {
        List<ItemContact> list = new ArrayList<ItemContact>();

            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        Cursor phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id},
                                null);
                        if (phoneCursor != null) {
                            if (phoneCursor.moveToNext()) {
                                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                ItemContact itemContact = new ItemContact();
                                itemContact.setName(name);
                                itemContact.setNumber(phoneNumber);

                                list.add(itemContact);

                                phoneCursor.close();
                            }
                        }
                    }
                }
            }
            return list;
    }

    private void filter(String name) {
        //new array list that will hold the filtered data
        ArrayList<ItemContact> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (ItemContact s : contactList) {
            //if the existing elements contains the search input
            if (s.getName().toLowerCase().contains(name.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }
        //calling a method of the adapter class and passing the filtered list
        contactAdapter.filterList(filterdNames);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_back:
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClicked(int position) {
        Intent intentSend = new Intent(this, SendMessageActivity.class);
        intentSend.putExtra("name", contactList.get(position).getName());
        intentSend.putExtra("phone",contactList.get(position).getNumber());
        startActivity(intentSend);
    }
}
