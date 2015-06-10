/*
 * (c) 2015 by Joachim Weishaupt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.huslik_elektronik.fcma2.service;

// RealFcm extends with real functionality

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import de.huslik_elektronik.fcma2.bluetooth.FcmByteBuffer;
import de.huslik_elektronik.fcma2.bluetooth.FcmConnector;
import de.huslik_elektronik.fcma2.bluetooth.FcmData;

public class RealFcm extends DummyFcm implements IFcm {

    // Log Tag
    public static final String TAG = "RealFcm";

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Name of the connected device
    private String mConnectedDeviceName = null;

    // Bluetooth Adapter
    private BluetoothAdapter mBluetoothAdapter;
    private FcmConnector mFcmConnector;
    private FcmByteBuffer mByteBuffer;

    public static int MENUREPEAT = 5; // after MENUREPEAT loops - menu was

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        // TODO Status to Main
                        /*case FcmService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to,
                                    mConnectedDeviceName));
                            break;
                        case FcmService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case FcmService.STATE_LISTEN:
                        case FcmService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;*/
                    }
                    break;
                case MESSAGE_WRITE:
                    //byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    //String writeMessage = new String(writeBuf);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    mByteBuffer.add(readBuf, msg.arg1);

                    break;

                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(ctx, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(ctx, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    public RealFcm(Handler m, Handler d) {
        super(m, d);
        mByteBuffer = new FcmByteBuffer(m, d);
    }

    @Override
    public BtStatus connect(String address) {
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(ctx, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            return BtStatus.NOT_AVAILABLE;
        } else if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(ctx, "Bluetooth is not enabled",
                    Toast.LENGTH_LONG).show();
            return BtStatus.NOT_ENABLED;
        } else {

            // Get the BluetoothDevice object
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

            btStatus = BtStatus.CONNECTED;

            // StartupFcmService and Processing
            // Initialize the FcmConnector to perform bluetooth connections
            if (mFcmConnector == null)
                mFcmConnector = new FcmConnector(ctx, mHandler);
            // Buffer Processing start
            if (mByteBuffer.running() == false)
                mByteBuffer.startProcessing();

            // Attempt to connect to the device
            mFcmConnector.connect(device);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.d(TAG, "Wait to complete Connection \n" + e);
            }

            return BtStatus.CONNECTED;
        }
    }

    public BtStatus disconnect() {
        btStatus = BtStatus.NOTCONNECTED;
        // sendMessage(fd.getCmdStr(COMMAND.STS));
        mFcmConnector.stop();
        mByteBuffer.stopProcessing(); // stop worker task
        return btStatus;
    }

    public void getFcmVersion() {
        byte[] bFcmHello = FcmData.getCmd(FcmData.COMMAND.FCM);
        mFcmConnector.write(bFcmHello);
        mByteBuffer.setLastCmd(FcmData.COMMAND.FCM);
    }

    public void startMenuStream() {
        String sMenuStream = FcmData.getCmdStr(FcmData.COMMAND.MNU0);
        byte[] bMenuStream = sMenuStream.getBytes();
        mFcmConnector.write(bMenuStream);
        mByteBuffer.setLastCmd(FcmData.COMMAND.MNU0);
    }

    @Override
    public void startDataStream() {
        btType = BtType.DATA_STREAM;
        startGpsStream();
    }

    @Override
    public void stopDataStream() {
        btType = BtType.NONE;
        stopStreaming();
    }

    public void startGpsStream() {
        byte[] bGpsStream = FcmData.getCmdDelay(FcmData.COMMAND.STG, 50);
        String str = bGpsStream.toString();
        mFcmConnector.write(bGpsStream);
        mByteBuffer.setLastCmd(FcmData.COMMAND.STG);
    }

    public void stopStreaming() {
        String sStopStream = FcmData.getCmdStr(FcmData.COMMAND.STS);
        byte[] bStopStream = sStopStream.getBytes();
        mFcmConnector.write(bStopStream);
        mByteBuffer.setLastCmd(FcmData.COMMAND.STS);
    }

}
