package ro.cs.pub.pdsd.shareit;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;

public class ConnectionListener implements ConnectionInfoListener {

    private MainActivity mActivity;

    public ConnectionListener(MainActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        mActivity.setInfo(info);
        
        if (info.groupFormed && info.isGroupOwner) {
            new FileServerAsyncTask(mActivity).execute();
        } else {
//            mActivity.findViewById(R.id.btn_launch_gallery).setVisibility(View.VISIBLE);
        }
    }
}
