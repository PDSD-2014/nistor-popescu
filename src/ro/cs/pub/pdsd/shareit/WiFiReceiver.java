package ro.cs.pub.pdsd.shareit;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

public class WiFiReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private PeerListListener peerListListener;
    private MainActivity mActivity;
    private List<WifiP2pDevice> mPeers;

    public WiFiReceiver(WifiP2pManager manager, Channel channel, MainActivity activity) {
        super();

        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        mPeers = new ArrayList<WifiP2pDevice>();

        peerListListener = new PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                Log.d(MainActivity.TAG, "onPeersAvailable: start");
                // Out with the old, in with the new.
                mPeers.clear();
                mPeers.addAll(peerList.getDeviceList());

                if (mPeers.size() == 0) {
                    Log.d(MainActivity.TAG, "onPeersAvailable: No devices found");
                }
            }
        };
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d(MainActivity.TAG, "WiFi is enabled");
            } else {
                Log.d(MainActivity.TAG, "WiFi is not enabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}