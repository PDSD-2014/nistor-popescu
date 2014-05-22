package ro.cs.pub.pdsd.shareit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

    private MainActivity mActivity;

    public FileServerAsyncTask(MainActivity activity) {
        this.mActivity = activity;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            ServerSocket serverSocket = new ServerSocket(8988);
            Log.d(MainActivity.TAG, "Server: Socket opened");
            Socket client = serverSocket.accept();
            MainActivity.setSocket(client);

            if (!MainActivity.isUploader()) {
                // TODO: instead of saving socket, start downloader / receiver
                new Downloader(client).start();
            }

            Log.d(MainActivity.TAG, "Server: connection done");
        } catch (IOException e) {
            Log.e(MainActivity.TAG, e.getMessage());
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            // TODO: notify file finished copying
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + result), "image/*");
            mActivity.startActivity(intent);
        }

    }

    @Override
    protected void onPreExecute() {
        // TODO: notify opening socket
    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            Log.i(MainActivity.TAG, "File transfer finished");
            // out.close();
            // inputStream.close();
        } catch (IOException e) {
            Log.d(MainActivity.TAG, e.toString());
            return false;
        }
        return true;
    }
}