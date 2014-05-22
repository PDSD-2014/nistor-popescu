package ro.cs.pub.pdsd.shareit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.os.Environment;

public class Downloader extends Thread {

    private Socket socket;

    public Downloader(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // receive filename and file
        InputStream is = null;
        try {
            is = socket.getInputStream();
            byte[] data = new byte[128];
            int ret = is.read(data, 0, 128);
            byte[] realData = new byte[ret];
            for (int i = 0; i < ret; i++) {
                realData[i] = data[i];
            }
            String name = new String(realData);

            OutputStream os = new FileOutputStream(new File(
                    Environment.getExternalStorageDirectory() + "/Download" + name));

            FileServerAsyncTask.copyFile(is, os);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
