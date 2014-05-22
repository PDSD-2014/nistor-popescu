package ro.cs.pub.pdsd.shareit;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;

public class Uploader extends Thread {

    private Socket socket;
    private File file;

    public Uploader(Socket socket, File file) {
        this.socket = socket;
        this.file = file;
    }

    @Override
    public void run() {
        // send filename and file
        try {
            OutputStream dos = new DataOutputStream(socket.getOutputStream());
            PrintWriter pw = new PrintWriter(dos, true);
            pw.println(file.getName());
            FileServerAsyncTask.copyFile(new FileInputStream(file), dos);
            dos.close();
        } catch (IOException e) {
            Log.w(MainActivity.TAG, e.getMessage());
        }
    }

}
