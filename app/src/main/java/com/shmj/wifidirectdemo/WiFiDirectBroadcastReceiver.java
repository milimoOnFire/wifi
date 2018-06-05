package com.shmj.wifidirectdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shahriar on 2/25/2018.
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    private Chat chat;

    //private List peers = new ArrayList();


    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wifi P2P is enabled
                    Log.i("Wifi_P2P: ","is enabled!");
                } else {
                    // Wi-Fi P2P is not enabled
                    Log.i("Wifi_P2P: ","is not enabled!");
                }
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (mManager != null) {
                mManager.requestPeers(mChannel,peerListListener);
                Log.e("peers",MainActivity.peers.toString());

                //MainActivity.msg.setText(peers.toString());
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            // Connection state changed!  We should probably do something about
            // that.
            Log.e("WiFiP2PBroadcast","WIFI_P2P_CONNECTION_CHANGED_ACTION");
            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                mManager.requestConnectionInfo(mChannel, connectionInfoListener);
            } else {
                mActivity.showMsg("no peer is connected!");
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            Log.e("WiFiP2PBroadcast",((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)).toString());
//            DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
//                    .findFragmentById(R.id.frag_list);
//            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
//                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

           // mActivity.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
             //       WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }



    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            // Out with the old, in with the new.
            MainActivity.peers.clear();
            MainActivity.peers.addAll(peerList.getDeviceList());

            // If an AdapterView is backed by this data, notify it
            // of the change.  For instance, if you have a ListView of available
            // peers, trigger an update.
//            ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
//            if (peers.size() == 0) {
//                Log.d(WiFiDirectActivity.TAG, "No devices found");
//                return;
//            }
            mActivity.notifyDataSetChanged();
           // mActivity.msg.setText(  MainActivity.peers.toString());
        }
    };

    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            Log.i("ConnectionInfoListener",wifiP2pInfo.toString());
            // InetAddress from WifiP2pInfo struct.


            mActivity.showMsg("connection established!");
            try {
                Log.i("wifiP2pInfo BR" , wifiP2pInfo.toString() );
                mActivity.openChat(wifiP2pInfo);
                //chat.setSender(wifiP2pInfo);
            } catch (Exception e){
                mActivity.showMsg("no connection info available");
                e.printStackTrace();
            }



            /*// After the group negotiation, we can determine the group owner.
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                // Do whatever tasks are specific to the group owner.
                // One common case is creating a server thread and accepting
                // incoming connections.
                new Server(groupOwnerAddress).start();
//                chat.sendAsServer(groupOwnerAddress);

            } else if (wifiP2pInfo.groupFormed) {
                // The other device acts as the client. In this case,
                // you'll want to create a client thread that connects to the group
                // owner.
                new Client(groupOwnerAddress).start();
                //chat.sendAsClient(groupOwnerAddress);
            }*/

        }
    };

}
