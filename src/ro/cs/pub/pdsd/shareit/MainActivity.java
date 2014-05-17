package ro.cs.pub.pdsd.shareit;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

    public static final String TAG = "shareit";
    private WifiP2pManager mManager;
    private Channel mChannel;
    private WiFiReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private boolean isWiqfiP2pEnabled;
    private ListView list;

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

        list = (ListView) findViewById(R.id.list);
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

        switch (id) {
        case R.id.btn_discover:
            if (!isWiqfiP2pEnabled) {
                Toast.makeText(MainActivity.this, R.string.wifi_disabled, Toast.LENGTH_SHORT)
                        .show();
                return true;
            }

            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, R.string.wifi_discovery_started,
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reasonCode) {
                    switch (reasonCode) {
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        Toast.makeText(MainActivity.this, R.string.wifi_discovery_unsupported,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case WifiP2pManager.ERROR:
                        Toast.makeText(MainActivity.this, R.string.wifi_discovery_error,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case WifiP2pManager.BUSY:
                        Toast.makeText(MainActivity.this, R.string.wifi_discovery_busy,
                                Toast.LENGTH_SHORT).show();
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

    public void setWifiP2pEnabled(boolean value) {
        isWiqfiP2pEnabled = value;
    }

    public void populateList(List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.activity_main, R.id.list_entry, values);
        list.setAdapter(adapter);
    }
}
