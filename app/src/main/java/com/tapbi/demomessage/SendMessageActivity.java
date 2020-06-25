package com.tapbi.demomessage;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.demomessage.addapter.ListMessageAdapter;
import com.tapbi.demomessage.dto.ItemMessage;

import java.util.ArrayList;
import java.util.List;

public class SendMessageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_ASK_PERMISSIONS_SENDSMS = 111;
    private ImageButton ib_back_message, ib_send_message;
    private TextView tv_name_contact;
    private EditText ed_send_message;
    private List<ItemMessage> messageList, itemMessageList;
    private RecyclerView rv_list_message;
    private ListMessageAdapter listMessageAdapter;
    private String number ;
    private String name ;
    private String address;
    private boolean sendSMS;
    private boolean back = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
         checkPermission();
        initView();

    }

    public void checkPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE_ASK_PERMISSIONS_SENDSMS);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS_SENDSMS : {
                sendSMS = true;
            }
        }
    }
    private void initView() {
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",-1);

        name = intent.getStringExtra("name");
        messageList = getSMS();
      //  id = messageList.get(position).getId();
        ib_back_message = findViewById(R.id.ib_back_message);
        ib_back_message.setOnClickListener(this);

        ib_send_message = findViewById(R.id.ib_send_message);
        ib_send_message.setOnClickListener(this);

        ed_send_message = findViewById(R.id.ed_message);

        tv_name_contact = findViewById(R.id.tv_name_contact);

       if (position !=-1){
           tv_name_contact.setText(messageList.get(position).getName());
           address = messageList.get(position).getName();
           number = messageList.get(position).getNumber();
           back = true;
       }
       else {
           number = intent.getStringExtra("phone");
           tv_name_contact.setText(name);
           address = name;
       }
       
         rv_list_message = findViewById(R.id.rv_message);
         rv_list_message.setHasFixedSize(true);

         loadAllListMessage();
//        SendMessageAsyncTask sendMessageAsyncTask = new SendMessageAsyncTask();
//        sendMessageAsyncTask.execute(id);


    }
    private void loadAllListMessage(){
        messageList = getListMessage(address);
        listMessageAdapter = new ListMessageAdapter(messageList);
        rv_list_message.setAdapter(listMessageAdapter);
        rv_list_message.setLayoutManager(new LinearLayoutManager(this));
        rv_list_message.scrollToPosition(messageList.size()-1);
    }


    public List<ItemMessage> getSMS() {
        List<ItemMessage> list = new ArrayList<ItemMessage>();
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);

        cursor.moveToFirst() ;
        ItemMessage itemMessage1 = new ItemMessage();
        itemMessage1.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
        itemMessage1.setName(cursor.getString(cursor.getColumnIndexOrThrow("address")));
        itemMessage1.setContent(cursor.getString(cursor.getColumnIndexOrThrow("body")));
        list.add(itemMessage1);
        cursor.moveToNext();
        while (!cursor.isAfterLast()) {

            ItemMessage itemMessage = new ItemMessage();
            itemMessage.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
            itemMessage.setName(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            itemMessage.setContent(cursor.getString(cursor.getColumnIndexOrThrow("body")));
            itemMessage.setNumber(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            if (check(list, itemMessage.getName())){
                list.add(itemMessage);
            }
            cursor.moveToNext();
        }
        return list;
    }

    public boolean check(List<ItemMessage> list, String address){
        for (int i =0 ; i< list.size(); i++){
            if ((list.get(i).getName()).equals(address)){
                return false;
            }
        }
        return true;
    }



    public List<ItemMessage> getListMessage(String address){
        List<ItemMessage> list = new ArrayList<ItemMessage>();
            Uri myMessage = Uri.parse("content://sms/");
            ContentResolver cr = getContentResolver();
            Cursor c = cr.query(myMessage,
                    new String[]{"address", "body","date","type"},
                    "address = '" + address + "'", null, "date" + " ASC");
            if (c.getCount()>0) {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    ItemMessage itemMessage = new ItemMessage();
                    itemMessage.setName(c.getString(c.getColumnIndexOrThrow("address")));
                    itemMessage.setContent(c.getString(c.getColumnIndexOrThrow("body")));
                    if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                       itemMessage.setFolder("inbox");
                    } else {
                        itemMessage.setFolder("sent");
                    }

                    list.add(itemMessage);
                    c.moveToNext();
                }
                c.close();
            }
            return list;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_back_message:
                if (back){
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(this, SearchActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.ib_send_message:
                if (sendSMS){
                    doSendMessages();
                    loadAllListMessage();
                }
                
                break;
        }

    }

    private void doSendMessages() {
        String content = ed_send_message.getText().toString();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, content, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
