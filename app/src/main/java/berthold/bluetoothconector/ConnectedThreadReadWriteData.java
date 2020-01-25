package berthold.bluetoothconector;

/**
 * When a bluetooth connection has been established, this thread sends
 * or receives a data- stream to/ from the devices connected.
 * Send or received data is displayed in the console textView.
 *
 */

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;

public class ConnectedThreadReadWriteData extends Thread {

    // BT
    private final BluetoothSocket mSocket;
    private final InputStream mIs;
    private final OutputStream mOs;

    Notify notify;

    // UI
    private Handler h;
    private TextView console;
    private String dataToSend;


    public ConnectedThreadReadWriteData(Notify notify, BluetoothSocket socket, Handler h, TextView console, String dataToSend) {
        this.notify = notify;
        mSocket = socket;
        this.h = h;
        this.console = console;
        this.dataToSend = dataToSend;
        InputStream tmpIs = null;
        OutputStream tmpOs = null;

        try {
            tmpIs = mSocket.getInputStream();
            tmpOs = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.v("Conected:", e.toString());
        }
        mOs = tmpOs;
        mIs = tmpIs;
    }

    /**
     * Listen to incoming data
     */
    public void run() {

        // Notify class which created this instance. This class then
        // needs to get the instance of this thread in order to
        // communicate with it.
        notify.connectionSuceeded();

        consoleOut("Waiting for incoming data.......\n");

        byte[] packetReceieved = new byte[1024];

        while (!Thread.currentThread().isInterrupted()) {
            try {
                int bytesAvailable = mIs.available();
                if (bytesAvailable > 0) {
                    packetReceieved = new byte[bytesAvailable];
                    mIs.read(packetReceieved);
                }
                String received = new String(packetReceieved, 0, bytesAvailable);
                consoleOut(received);

            } catch (IOException e) {
                Log.v("Error:", e.toString());
                closeInputStream();
            }
        }
    }

    /**
     * Send stream to device.
     */
    public void send(String dataToSend) {
        try {
            if (dataToSend.length() > 0)
                mOs.write(dataToSend.getBytes());
            consoleOut("Send:"+dataToSend+"\n");
        } catch (IOException ee) {
            closeOutputStream();
        }
    }

    /**
     * Shows text inside console- textView
     */
    private void consoleOut(String message) {
        final String m;
        m = message;
        h.post(new Runnable() {
            @Override
            public void run() {
                console.append(m);
            }
        });
    }

    private void closeInputStream() {
        try {
            mIs.close();
        } catch (IOException e) {
            consoleOut("Could not close input stream:" + e.toString()+"\n");
        }
    }

    private void closeOutputStream() {
        try {
            mOs.close();
        } catch (IOException e) {
            consoleOut("Could not close output stream:" + e.toString()+"\n");
        }
    }
}
