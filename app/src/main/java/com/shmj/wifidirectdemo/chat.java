package com.shmj.wifidirectdemo;

import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;

/**
 * Created by Shahriar on 3/5/2018.
 */

public class chat extends AppCompatActivity {

    TextView otherDevicename;
    static TextView messages;
    Button sendBbutton;
    EditText textTosend;
    String msgToSend;
    Server server;
    Client client;
    Boolean flag = true;
    InetAddress groupOwnerAddress;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_page);

        sendBbutton = (Button) findViewById(R.id.sendButton);
        textTosend = (EditText) findViewById(R.id.textToSend);
        messages = (TextView) findViewById(R.id.messages);

        otherDevicename = (TextView) findViewById(R.id.otherDeviceName);
        otherDevicename.setText("this is a new chat");
        msgToSend = "nullmsg";
    }

    public void showMsg (String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void fuckingSend(View view){

        if(String.valueOf(textTosend.getText()) != null){
            msgToSend = String.valueOf(textTosend.getText());
        }


        if(flag == true){ // for server
            server.sendFromServer(String.valueOf(msgToSend));
        }else{    // for client
            client.sendFromClient(String.valueOf(msgToSend));
        }

    }

    public void setSender(WifiP2pInfo wifiP2pInfo) {
        groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;

        if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a server thread and accepting
            // incoming connections.
            server = new Server(groupOwnerAddress);
            server.start();
            flag = true;
            showMsg("server created.");
//                chat.sendAsServer(groupOwnerAddress);

        } else if (wifiP2pInfo.groupFormed) {
            // The other device acts as the client. In this case,
            // you'll want to create a client thread that connects to the group
            // owner.
            client = new Client(groupOwnerAddress);
            client.start();
            flag = false;
            showMsg("client created");
            //chat.sendAsClient(groupOwnerAddress);
        }
    }

    static void updateMessagesfromServer(String msgs){
        messages.setText(messages.getText() + "\n" + "client: " + msgs);
    }

    public static void updateMessagesfromClient(String ret) {
        messages.setText(messages.getText() + "\n" + "Server: " + ret);
    }
}
