package com.example.sen.finddownload;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WifiP2pActivity extends AppCompatActivity {

    private final static int PORT = 8080;
    private final static String SERVER_LOCATION = "172.20.10.7";
    private final static String fileName = "2.pdf";
    private final static String message = "2-1";

    public static final String TAG = "WIFIP2P";
    private boolean isWiFiP2pEnabled = false;
    private boolean retryChannel = false;
    private WifiP2pManager manager;
    private List peers = new ArrayList();

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver wifiReceiver = null;


    public void setIsWiFiP2pEnabled(boolean isWiFiP2pEnabled) {
        this.isWiFiP2pEnabled = isWiFiP2pEnabled;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_p2p);
        // whether wifi peers are enabled
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // whether the peer list has changed
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // whether the connection is changed
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // whether the config is changed
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        wifiReceiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                // when start successfully and usually left blank.
                Toast.makeText(WifiP2pActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }

            public void onFailure(int reasonCode) {
                // when initiation goes wrong and user should be alerted.
                Toast.makeText(WifiP2pActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }

        });

        WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                // out the old and in the new
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                // set the listView
                if(peers.size() == 0) {
                    Log.d(WifiP2pActivity.TAG, "No device found!");
                }
            }
        };

        WifiP2pDevice device = null;
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //success logic
            }

            @Override
            public void onFailure(int i) {
                //failure logic
            }
        });

        Button searchButton = (Button) findViewById(R.id.wifiFind);
        assert searchButton != null;
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                discoverPeers();
            }
        });

        Button downloadButton = (Button) findViewById(R.id.wifiDownload);
        assert downloadButton != null;
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FileDownload.downloadFile(SERVER_LOCATION, PORT, message);
            }
        });


        Button transferButton = (Button) findViewById(R.id.wifiTransfer);
        assert transferButton != null;
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                transferFiles();
            }
        });
    }


    public void onResume() {
        super.onResume();
        wifiReceiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(wifiReceiver, intentFilter);
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(wifiReceiver);
    }


    /* remove all peers and clear all fields when receiver receive a state to change event.
     */

//    public void resetData() {
//        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
//        DeviceDetailFragment fragmentDetail = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
//        if (fragmentList != null) {
//            fragmentList.clearPeers();
//        }
//        if (fragmentDetail != null) {
//            fragmentDetail.resetViews();
//        }
//    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.action_items, menu);
//        return true;
//    }

//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.atn_di
//        }
//    }
}