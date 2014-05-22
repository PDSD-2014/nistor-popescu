package ro.cs.pub.pdsd.shareit;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;

public class ConnectionListener implements ConnectionInfoListener {

    private MainActivity mActivity;

    public ConnectionListener(MainActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

        if (info.groupFormed && info.isGroupOwner) {
            new FileServerAsyncTask(mActivity).execute();
        } else {
            // start receiver / downloader for client with service
            Intent serviceIntent = new Intent(mActivity, FileTransferService.class);
            serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                    info.groupOwnerAddress.getHostAddress());
            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
            mActivity.startService(serviceIntent);
        }
    }
}
