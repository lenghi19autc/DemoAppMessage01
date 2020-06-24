package com.tapbi.demomessage;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.demomessage.addapter.ListMessageAdapter;
import com.tapbi.demomessage.dto.ItemMessage;

import java.util.ArrayList;
import java.util.List;

public class SendMessageActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton ib_back_message;
    private TextView tv_name_contact;
    private List<ItemMessage> messageList, itemMessageList;
    private RecyclerView rv_list_message;
    private ListMessageAdapter listMessageAdapter;
    private int id ;
    private String address;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initView();

    }


    private void initView() {
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        messageList = getSMS();
      //  id = messageList.get(position).getId();
        address = messageList.get(position).getName();

        ib_back_message = findViewById(R.id.ib_back_message);
        ib_back_message.setOnClickListener(this);

        tv_name_contact = findViewById(R.id.tv_name_contact);
        tv_name_contact.setText(messageList.get(position).getName());

         rv_list_message = findViewById(R.id.rv_message);
         rv_list_message.setHasFixedSize(true);

//        SendMessageAsyncTask sendMessageAsyncTask = new SendMessageAsyncTask();
//        sendMessageAsyncTask.execute(id);

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
                    new String[]{"address", "body","date","creator"},
                    "address = '" + address + "'", null, "date" + " ASC");
            if (c.getCount()>0) {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    ItemMessage itemMessage = new ItemMessage();
                    itemMessage.setName(c.getString(c.getColumnIndexOrThrow("address")));
                    itemMessage.setContent(c.getString(c.getColumnIndexOrThrow("body")));
                    itemMessage.setCreator(c.getString(c.getColumnIndexOrThrow("creator")));

                    list.add(itemMessage);
                    c.moveToNext();
                }
                c.close();
            }
            return list;
    }

//    public class SendMessageAsyncTask extends AsyncTask<Integer, Void, List<ItemMessage>>{
//
//        @Override
//        protected List<ItemMessage> doInBackground(Integer... integers) {
//            List<ItemMessage> list = new ArrayList<ItemMessage>();
//            Uri myMessage = Uri.parse("content://sms/");
//            ContentResolver cr = getContentResolver();
//            Cursor c = cr.query(myMessage,
//                    new String[]{"_id", "address", "body"},
//                    "_id = '" + integers[0] + "'", null, null);
//            if (c.getCount()>0) {
//                c.moveToFirst();
//                while (!c.isAfterLast()) {
//                    ItemMessage itemMessage = new ItemMessage();
//                    itemMessage.setName(c.getString(c.getColumnIndexOrThrow("address")));
//                    itemMessage.setContent(c.getString(c.getColumnIndexOrThrow("body")));
//                    String body = itemMessage.getContent();
//
//                    list.add(itemMessage);
//                    c.moveToNext();
//                }
//                c.close();
//            }
//            return list;
//
//        }
//
//        @Override
//        protected void onPostExecute(List<ItemMessage> itemMessages) {
//            super.onPostExecute(itemMessages);
//            itemMessageList = itemMessages;
//            listMessageAdapter = new ListMessageAdapter(itemMessageList);
//            rv_list_message.setAdapter(listMessageAdapter);
//        }
//    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
