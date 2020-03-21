package berthold.bluetoothconector;

/**
 * This try's to establish a connection to a BT device.
 * If a connection could be established, it listens for incoming
 * data and data to be send and display's it inside the console
 * textView.
 */

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.UUID;


public class ClientThread extends Thread {

    // BT
    private BluetoothSocket mSocket = null;
    private BluetoothDevice mDevice = null;
    private int action;


    ConnectedThreadReadWriteData ct;
    Notify notify;

    // UI
    private TextView console;
    private String dataToSend;
    private Handler h;

    // Use this uuid for connection with other devs....
    //private UUID myUUID=UUID.fromString("00002415-0000-1000-8000-00805F9B34FB");
    // Use this uuid for connection with Hc05 Module....
    private UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private String devName;

    /*
     * Constructor, get socket
     */

    public ClientThread(Notify notify, BluetoothDevice mDevice, TextView console, String dataToSend, Handler h) {

        this.notify = notify;
        this.action = action;
        this.mDevice = mDevice;
        this.console = console;
        this.dataToSend = dataToSend;
        this.h = h;
        mSocket = null;

        try {
            mSocket = mDevice.createRfcommSocketToServiceRecord(myUUID);
        } catch (IOException e) {
            Log.v("Client:Socket:", e.toString());
        }
    }

    /*
     * Run!
     */

    public void run() {
        try {
            Log.v("Client:Connecting......", "");
            mSocket.connect();
            consoleOut("Client: Connection successful!\n");

            // This reads incomming data from the conneted device...
            ct = new ConnectedThreadReadWriteData(notify, mSocket, h, console, dataToSend);
            ct.start();

        } catch (IOException e) {
            Log.v("Client: Thread:", e.toString());
            consoleOut("Client: Connection failed! Cause:" + e.toString() + "\n");
        }
        return;
    }

    /**
     * Returns instance of{@link ConnectedThreadReadWriteData} back to instance
     * which created this.
     */

    public ConnectedThreadReadWriteData getConnectedThread() {
        return ct;
    }

    /*
     * Cancel
     */

    public void cancel() {
        try {
            mSocket.close();
            consoleOut("Disconected!\n");
        } catch (IOException e) {
            Log.v("Client: thread:", e.toString());
        }
        return;
    }

    /*
     * Output to console
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
}
