package ro.cs.pub.pdsd.shareit;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

    public static final String TAG = "shareit";
    private WifiP2pManager mManager;
    private Channel mChannel;
    private WiFiReceiver mReceiver;
    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        switch (id) {
        case R.id.btn_discover:
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Log.d("ShareIt-MainActivity", "discover peers succesful");
                }

                @Override
                public void onFailure(int reasonCode) {
                    switch (reasonCode) {
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        Log.d(TAG, "discover peers failure P2P_UNSUPPORTED");
                        break;
                    case WifiP2pManager.ERROR:
                        Log.d(TAG, "discover peers failure ERROR");
                        break;
                    case WifiP2pManager.BUSY:
                        Log.d(TAG, "discover peers failure BUSY");
                        break;
                    default:
                        break;
                    }
                }
            });
            break;
        default:
            break;
        }
    }
}
