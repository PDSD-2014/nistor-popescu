package ro.cs.pub.pdsd.shareit;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

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

            dos.write(file.getName().getBytes());

            FileServerAsyncTask.copyFile(new FileInputStream(file), dos);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
