package com.example.sen.finddownload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

/**
 * Created by Sen on 19/07/2016.
 * <p>
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private Channel channel;
    private WifiP2pActivity activity;

    /**
     * Constructor
     *
     * @param manager
     * @param channel
     * @param activity
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
                                       WifiP2pActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    /**
     * Method which gets the events and do stuff .
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                activity.setIsWiFiP2pEnabled(true);
            } else {
                // Wi-Fi P2P is not enabled
                activity.setIsWiFiP2pEnabled(false);
//                activity.resetData();
            }
            Log.d(WifiP2pActivity.TAG, "P2P state changed - " + state);

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // The peer list has changed. Call WifiP2pManager.requestPeers(). It will call onPeersAvailable
            //of the PeerListListener, which allows us to get back the list
            //of peers .
//            if (manager != null) {
//                request peers from this manager. This is a asynchronous call and this behavior is notified with a callback on PeerListListener.onPeerAvailable();
//                manager.requestPeers(channel, (PeerListListener) activity.getFragmentManager().findFragmentById(R.id.frag_list));
//            }
//            Log.d(WifiP2pActivity.TAG, "P2P peers changed.");

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // connection state changed.
//            if (manager == null) return;
//            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
//
//            if (networkInfo.isConnected()) {
//                // connected with other device, request connection info to find group owner IPs
//                DeviceDetailFragment fragment = (DeviceDetailFragment) activity.getFragmentManager().findFragmentById(R.id.frag_detail);
//                manager.requestConnectionInfo(channel, fragment);
//            } else {
//                // disconnected
//                activity.resetData();
//            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
//            DeviceListFragment fragment = (DeviceDetailFragment) activity.getFragmentManager().findFragmentById(R.id.frag_list);
//            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }
    }
}