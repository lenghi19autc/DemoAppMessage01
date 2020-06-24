package com.tapbi.demomessage;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.demomessage.addapter.MessageAdapter;
import com.tapbi.demomessage.dto.ItemMessage;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MessageAdapter.OnCallBack {
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<ItemMessage> messageList;
    private Button btn_add_message;
    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final int REQUEST_CODE_ASK_PERMISSIONS_CONTACT = 123;
    final int REQUEST_CODE_ASK_PERMISSIONS_SENDSMS = 143;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
       // displaySmsLog();
    }


    private void initView() {
        ActivityCompat.requestPermissions(
                MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        messageList =  getSMS();
        recyclerView = findViewById(R.id.rv_message);
        recyclerView.setHasFixedSize(true);
        messageAdapter = new MessageAdapter( this, messageList);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btn_add_message = findViewById(R.id.btn_add_message);
        btn_add_message.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add_message:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(MainActivity.this, SendMessageActivity.class);
       // id = messageList.get(position).getId();
        intent.putExtra("position",position);
        startActivity(intent);
    }
}
