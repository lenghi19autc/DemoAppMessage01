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

import com.tapbi.demomessage.Addapter.MessageAdapter;
import com.tapbi.demomessage.DTO.ItemMessage;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MessageAdapter.OnCallBack {
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<ItemMessage> messageList;
    private Button btn_add_message;
    final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        initView();
    }

    private void initView() {
        messageList =  getSMS();
        recyclerView = findViewById(R.id.rv_message);
        recyclerView.setHasFixedSize(true);
        messageAdapter = new MessageAdapter( this, messageList);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btn_add_message = findViewById(R.id.btn_add_message);
        btn_add_message.setOnClickListener(this);
    }

    private List<ItemMessage> getSMS() {
        List<ItemMessage> list = new ArrayList<ItemMessage>();
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);

        cursor.moveToFirst() ;

        while (!cursor.isAfterLast()) {

          ItemMessage itemMessage = new ItemMessage();
          
          itemMessage.setName(cursor.getString(cursor.getColumnIndexOrThrow("address")));
          itemMessage.setContent(cursor.getString(cursor.getColumnIndexOrThrow("body")));
          list.add(itemMessage);
          cursor.moveToNext();
        }
        return list;
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
        intent.putExtra("position",position);
        startActivity(intent);
    }
}
