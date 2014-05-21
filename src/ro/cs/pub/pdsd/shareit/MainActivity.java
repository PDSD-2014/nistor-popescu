package ro.cs.pub.pdsd.shareit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

    public static final int LAUNCH_GALLERY_RESULT_CODE = 20;
    public static final String TAG = "shareit";
    private WifiP2pManager mManager;
    private Channel mChannel;
    private WiFiReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private boolean isWiqfiP2pEnabled;
    private WifiP2pInfo mInfo;
    private ListView peerList;
    private FileDialog fileDialog;

    private class ConnectListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final String peerName = (String) peerList.getItemAtPosition(position);

            final Dialog dialog = new Dialog(view.getContext());
            dialog.setTitle(peerName);

            if (mReceiver.isPeerConnected(peerName)) {
                dialog.setContentView(R.layout.after_connect_dialog);
                Button disconnectBtn = (Button) dialog.findViewById(R.id.btn_disconnect);
                // TODO disconnect

                Button launchGalleryBtn = (Button) dialog.findViewById(R.id.btn_launch_gallery);
                launchGalleryBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fileDialog.showDialog();
                        dialog.dismiss();
                    }
                });
            } else {
                dialog.setContentView(R.layout.before_connect_dialog);
                Button connectBtn = (Button) dialog.findViewById(R.id.btn_connect);

                connectBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WifiP2pConfig config = new WifiP2pConfig();
                        config.deviceAddress = mReceiver.findPeerByName(peerName).deviceAddress;
                        config.wps.setup = WpsInfo.PBC;

                        mManager.connect(mChannel, config, new ActionListener() {

                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onFailure(int reason) {
                                Toast.makeText(MainActivity.this, "Connect failed. Retry.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                        Toast.makeText(
                                MainActivity.this,
                                v.getResources().getString(R.string.action_connect) + " "
                                        + peerName, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }

            Button closeButton = (Button) dialog.findViewById(R.id.btn_cancel);
            closeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            Log.v(TAG, "No image selected");
            return;
        }
        Uri uri = data.getData();
        Log.i(MainActivity.TAG, uri.toString());

    }

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

        peerList = (ListView) findViewById(R.id.list);
        peerList.setOnItemClickListener(new ConnectListener());

        File mPath = new File(Environment.getExternalStorageDirectory() + "");
        fileDialog = new FileDialog(this, mPath);
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                Log.d(getClass().getName(), "selected file " + file.toString());
                Intent serviceIntent = new Intent(MainActivity.this, FileTransferService.class);
                serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
                serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, file.toString());
                serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                        mInfo.groupOwnerAddress.getHostAddress());
                serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
                startService(serviceIntent);
            }
        });

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
            populatePeerList(new ArrayList<String>());
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

    public void populatePeerList(List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.peer_layout,
                R.id.list_entry, values);
        peerList.setAdapter(adapter);
    }

    public void setInfo(WifiP2pInfo info) {
        this.mInfo = info;
    }
}
