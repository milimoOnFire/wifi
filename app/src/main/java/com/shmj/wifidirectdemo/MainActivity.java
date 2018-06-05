package com.shmj.wifidirectdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity  {

    Button startButton;
    TextView devicesList;
    ListView listView;
    static TextView msg;
    Intent chatPage;

    boolean serverOrClient;

    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;

    IntentFilter mIntentFilter;
    //private ArrayList peers = new ArrayList();
    public static ArrayList<WifiP2pDevice> peers = new ArrayList<>();
    private WifiP2pDeviceAdapter adapter;

    public void showMsg (String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        listView = (ListView) findViewById(R.id.listitem);
        //msg = (TextView) findViewById(R.id.msg);

        initFilter();

        adapter = new WifiP2pDeviceAdapter(this,peers);
        listView.setAdapter(adapter);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i; //Integer.parseInt(v.getTag().toString());
                WifiP2pDevice device = peers.get(position);
                switch (device.status){
                    case WifiP2pDevice.AVAILABLE:
                    case WifiP2pDevice.CONNECTED:
                    case WifiP2pDevice.INVITED:
                        connect(device);
                            try {
                                WifiP2pInfo wifiP2pInfo2 = null;
                                wifiP2pInfo2.groupOwnerAddress =  InetAddress.getAllByName(device.deviceAddress)[0];
                                openChat( wifiP2pInfo2 );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        break;
                    case WifiP2pDevice.FAILED:
                    case WifiP2pDevice.UNAVAILABLE:
                        Toast.makeText(getApplicationContext(), String.format(Locale.getDefault(),"status=%d",device.status),Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

    private void initFilter() {
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, intentFilter);
        discoverPeers();
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        stopPeerDiscovery();
    }

    private void discoverPeers(){
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e("discover","onSuccess");
            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }
    private void stopPeerDiscovery(){

        mManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                showMsg("stopPeerDiscovery: onSuccess");
                Log.e("stopPeerDiscovery","onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                switch (reason){
                    case WifiP2pManager.ERROR:
                        Log.e("stopPeerDiscovery","ERROR");
                        break;
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        Log.e("stopPeerDiscovery","P2P_UNSUPPORTED");
                        break;
                    case WifiP2pManager.BUSY:
                        Log.e("stopPeerDiscovery","BUSY");
                        break;
                    case WifiP2pManager.NO_SERVICE_REQUESTS:
                        Log.e("stopPeerDiscovery","NO_SERVICE_REQUESTS");
                        break;
                }
            }
        });
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
        if (peers.size() == 0) {
            Log.d("notifyDataSetChanged", "No devices found");
            showMsg("No devices found");
        }
    }

    public void connect(final WifiP2pDevice device) {
        // Picking the selected device found on the network.

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        Log.i("deviceAddress connect", String.valueOf(device.deviceAddress));

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

                Log.e("connect","onSuccess");
                showMsg("connected.");

                //openChat();
                //chatPage = new Intent(getApplicationContext(), chat.class);
                //startActivity(chatPage);
                //mManager.
            }

            @Override
            public void onFailure(int reason) {
                switch (reason){
                    case WifiP2pManager.ERROR:
                        Log.e("connect","ERROR");
                        break;
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        Log.e("connect","P2P_UNSUPPORTED");
                        break;
                    case WifiP2pManager.BUSY:
                        Log.e("connect","BUSY");
                        break;
                    case WifiP2pManager.NO_SERVICE_REQUESTS:
                        Log.e("connect","NO_SERVICE_REQUESTS");
                        break;
                }
            }
        });
    }

    public void openChat(WifiP2pInfo wifiP2pInfo){
        Log.i("wifiP2pinfo openChat", String.valueOf(wifiP2pInfo));
        serverOrClient = false;
        chatPage = new Intent(getApplicationContext(), Chat.class).putExtra("serverOrClient",serverOrClient);
        chatPage.putExtra("WifiP2pInfo",wifiP2pInfo);
        startActivity(chatPage);
    }

    public void openChat(){
        serverOrClient = true;
        chatPage = new Intent(getApplicationContext(), Chat.class).putExtra("serverOrClient", serverOrClient);
        startActivity(chatPage);
    }

    public void search(View v) {
        onResume();
        Log.e("peers",peers.toString());



        /*int pos = listView.getSelectedItemPosition();
        //connect(peers.get(pos));
        switch (v.getId()){
            case R.id.listitem:
                connect(peers.get(pos));
                //startRegistration();
                break;
            case R.id.startButton:
                onResume();
                Log.e("peers",peers.toString());

                //discoverService();
              //  break;
        }*/
    }

}
