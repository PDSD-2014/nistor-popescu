package ro.cs.pub.pdsd.shareit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import android.os.Environment;
import android.util.Log;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String name = br.readLine().trim();

            File f = new File(Environment.getExternalStorageDirectory() + "/Download/" + name);
            File dirs = new File(f.getParent());
            if (!dirs.exists()) {
                dirs.mkdirs();
            }
            f.createNewFile();

            OutputStream os = new FileOutputStream(f);

            FileServerAsyncTask.copyFile(is, os);
            os.close();
        } catch (IOException e) {
            Log.w(MainActivity.TAG, e.getMessage());
        }
    }

}
