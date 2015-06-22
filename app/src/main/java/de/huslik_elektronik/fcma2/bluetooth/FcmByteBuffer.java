/**
 * 28.05.2014 - Changes ArrayList -> Vector due to synchronized List effort
 * implements BufferProcessing Thread
 * <p/>
 * (c) 2014 by Joachim Weishaupt
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.huslik_elektronik.fcma2.bluetooth;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Arrays;
import java.util.Vector;

import de.huslik_elektronik.fcma2.model.CGpsFrame;
import de.huslik_elektronik.fcma2.model.CSensorFrame;
import de.huslik_elektronik.fcma2.model.CVersion;
import de.huslik_elektronik.fcma2.model.Menu;
import de.huslik_elektronik.fcma2.model.StreamData;
import de.huslik_elektronik.fcma2.service.RealFcm;

public class FcmByteBuffer extends Vector<Byte> {

    static final long serialVersionUID = 0;

    public final static String TAG = "Processing";
    private transient Processing mProcessingThread = null;
    private boolean timeToQuit = true;

    // Message Handlers
    private transient Handler mMenuHandler;
    private transient Handler mDataHandler;

    // frameID
    private int frameId = 0;

    // searchstring
    byte[] search = new byte[FcmData.maxCmdLen];

    public FcmByteBuffer(Handler mHandler, Handler dHandler) {
        super();

        // Handler registration
        mMenuHandler = mHandler;
        mDataHandler = dHandler;

        // initial searchstring
        int i;
        for (i = 0; i < FcmData.prefix.length; i++)
            search[i] = FcmData.prefix[i];
    }

    // Thread Methods
    public void startProcessing() {
        timeToQuit = false;
        if (!running()) {
            mProcessingThread = new Processing(this); // everytime a new
            // Thread, no reuse
            // possible
            mProcessingThread.start();
        }
    }

    public void stopProcessing() {
        timeToQuit = true;
        mProcessingThread = null;
    }

    public boolean running() {
        return (mProcessingThread != null);
    }

    public void setLastCmd(FcmData.COMMAND cmd) {
        if (mProcessingThread != null) // don't update cmd if Processing is
            // stopped
            mProcessingThread.setLastCmd(cmd);
    }

    // Data Processing

    public synchronized void add(byte[] seq, int readBytes) {
        for (int i = 0; i < readBytes; i++)
            this.add((Byte) seq[i]);
    }

    public synchronized int find(byte[] seq, int len, int start, int end) {
        int p = -1, j = 1;
        boolean found = false;
        for (int i = start; i < end - len + 1; i++) {
            try {

                if (this.get(i) == (Byte) seq[0] && !found) {
                    p = i;
                    found = true;
                } else {
                    if (this.get(i) == (Byte) seq[j] && j < len) {
                        j++;
                        if (len == j) {
                            // found
                            break;
                        }
                    } else {
                        found = false;
                        j = 1;
                        p = -1;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.d(TAG, "Index out of bound, size: " + this.size());
            }
        }
        return p;
    }

    public synchronized byte[] rangeArray(int start, int end) {
        byte[] result = new byte[end - start + 1];
        int n = 0;

        for (int i = start; i <= end; i++) {
            try {
                result[n] = (byte) this.get(i);
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "delete: start " + start + " end " + end
                        + " buffersize " + this.size() + "\n" + e);
            }
            n++;
        }

        return result;
    }

    public synchronized void delete(int start, int end) {
        try {
            this.removeRange(start, end);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "delete: start " + start + " end " + end
                    + " buffersize " + this.size() + "\n" + e);
        }
    }

    // Buffer Processing Thread
    private class Processing extends Thread {

        private FcmData.COMMAND lastCmd;
        private FcmByteBuffer buffer;
        private byte[] result;
        private int menuLoop = 0;

        public Processing(FcmByteBuffer buffer) {
            this.lastCmd = null;
            this.buffer = buffer;
            this.menuLoop = 0;
        }

        public void setLastCmd(FcmData.COMMAND lastcmd) {
            this.lastCmd = lastcmd;
        }

        @Override
        public void run() {

            Log.d(TAG, "Processing Buffer started");
            while (!timeToQuit) // repeat until stop -> other command
            // or STS, etc.
            {
                // Menue Command
                if (lastCmd == FcmData.COMMAND.MNU0) {

                    StringBuilder sb = new StringBuilder();

                    if ((menuLoop > RealFcm.MENUREPEAT)
                            && (result = ProcessBuffer(buffer, FcmData.COMMAND.TXT)) != null) {
                        byte[] b;
                        menuLoop = 0; // reset update menu
                        // getParameter(result);
                        if (result.length >= 21 * 8) {
                            b = Arrays.copyOfRange(result, 0, 0 + 21 * 8);
                            String menu = new String(b);
                            for (int i = 0; i < 8; i++) {
                                String part = menu.substring(21 * i,
                                        21 * (i + 1) - 1);
                                sb.append(part + "\n");
                            }
                        } else {
                            Log.d(TAG, "Menu Result to short");
                        }

                    }
                    menuLoop++;

                    if (buffer.size() > 500) // clear when streaming MNU
                    {
                        buffer.clear();
                        menuLoop = 0; // reset update menu
                    }

                    // Send the obtained bytes to the UI Activity
                    if (sb.length() > 1) {
                        Message m = Message.obtain(null, Menu.MENU, 0, 0, sb.toString());
                        mMenuHandler.sendMessage(m);
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Processing " + e);
                    }
                }

                // Parameter

                if (lastCmd == FcmData.COMMAND.PARL) {
                    if ((result = ProcessBuffer(buffer, FcmData.COMMAND.PAR)) != null) {
                        /*paraHandler.obtainMessage(FragmentPara.PARAMETER,
                                result.length, -1, result).sendToTarget();*/

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Processing " + e);
                        }
                    }
                }

                // RealFcm Hello

                if (lastCmd == FcmData.COMMAND.FCM) {
                    if ((result = ProcessBuffer(buffer, FcmData.COMMAND.FCM)) != null) {
                        int VersionH = (((0xFF & (int) result[0]) * 256)
                                + ((0xFF & (int) result[1])));
                        int VersionL = (((0xFF & (int) result[2]) * 256)
                                + ((0xFF & (int) result[3])));
                        int para = ((0xFF & (int) result[4]) * 256)
                                + ((0xFF & (int) result[5]));

                        CVersion v = new CVersion(VersionH, VersionL, para);
                        Message m = Message.obtain(null, Menu.VERSION, 0, 0, v);
                        mMenuHandler.sendMessage(m);

                    }
                }

                // Sensor Data, packetlen = 72+1 ? TODO test
                // Todo put data via handler
                if (lastCmd == FcmData.COMMAND.STD) {
                    if ((result = ProcessBuffer(buffer, FcmData.COMMAND.D)) != null) {
                        // TODO Sensor Stream
                        /*SensorFrame sensorFrame = new SensorFrame();
                        if (result.length == 73) {
							sensorFrame.setFrame(result);
							main.getfSens().addSensorFrame(sensorFrame);
						}*/
                        if (result.length == 73) {

                            // TODO - FCM increment
                            frameId++;
                            CSensorFrame sensorFrame = new CSensorFrame(frameId);
                                                     
                            int pos = 0;
                            int i = 0;

                            int b[] = new int[CSensorFrame.getDimension(CSensorFrame.SENSOR.Gyro)];
                            // Gyro
                            for (i = 0; i < CSensorFrame.getDimension(CSensorFrame.SENSOR.Gyro); i++) {
                                b[i] = FcmData.convFcmAndroidInt32(result, pos);
                                pos += 4;
                            }
                            sensorFrame.setG(b);

                            // Acc
                            int a[] = new int[CSensorFrame.getDimension(CSensorFrame.SENSOR.Acceleration)];
                            for (i = 0; i < CSensorFrame.getDimension(CSensorFrame.SENSOR.Acceleration); i++) {
                                a[i] = FcmData.convFcmAndroidInt32(result, pos);
                                pos += 4;
                            }
                            sensorFrame.setA(a);
                            // Magnetic
                            int m[] = new int[CSensorFrame.getDimension(CSensorFrame.SENSOR.Magneto)];
                            for ( i = 0; i < CSensorFrame.getDimension(CSensorFrame.SENSOR.Magneto); i++) {
                                m[i] = FcmData.convFcmAndroidInt32(result, pos);
                                pos += 4;
                            }
                            sensorFrame.setM(m);
                            // Gov
                            int gov [] = new int[CSensorFrame.getDimension(CSensorFrame.SENSOR.Governor)];
                            for ( i = 0; i < CSensorFrame.getDimension(CSensorFrame.SENSOR.Governor); i++) {
                                gov[i] = FcmData.convFcmAndroidInt32(result, pos);
                                pos += 4;
                            }
                            sensorFrame.setGov(gov);
                            // RC
                            int rc[] = new int[CSensorFrame.getDimension(CSensorFrame.SENSOR.Gyro)];
                            for ( i = 0; i < CSensorFrame.getDimension(CSensorFrame.SENSOR.Gyro); i++) {
                                rc[i] = FcmData.convFcmAndroidInt32(result, pos);
                                pos += 4;
                            }
                            sensorFrame.setRc(rc);
                            // h
                            int h = FcmData.convFcmAndroidInt32(result, pos);
                            pos += 4;
                            sensorFrame.setH(h);
                            // temp
                            int temp[] = new int[CSensorFrame.getDimension(CSensorFrame.SENSOR.Temperature)];
                            for ( i = 0; i < CSensorFrame.getDimension(CSensorFrame.SENSOR.Temperature); i++) {
                                temp[i] = FcmData.convFcmAndroidInt16(result, pos);
                                pos += 2;
                            }
                            sensorFrame.setTemp(temp);

                            Message mSensor = Message.obtain(null, StreamData.SENSOR, 0, 0, sensorFrame);
                            mDataHandler.sendMessage(mSensor);
                        }

                    }
                }

                // Gps Data
                if (lastCmd == FcmData.COMMAND.STG) {
                    result = ProcessBuffer(buffer, FcmData.COMMAND.G);
                    if (result != null) {

                        int pos = 0;
                        int longitude = FcmData.convFcmAndroidInt32(result, pos);
                        pos += 4;
                        int latitude = FcmData.convFcmAndroidInt32(result, pos);
                        pos += 4;
                        int height = FcmData.convFcmAndroidInt32(result, pos);
                        // Speed
                        pos += 4;
                        float xSpeed = FcmData.convFcmAndroidFloat32(result, pos);
                        pos += 4;
                        float ySpeed = FcmData.convFcmAndroidFloat32(result, pos);
                        pos += 4;
                        float zSpeed = FcmData.convFcmAndroidFloat32(result, pos);
                        // Distance to target
                        pos += 4;
                        float xDist = FcmData.convFcmAndroidFloat32(result, pos);
                        pos += 4;
                        float yDist = FcmData.convFcmAndroidFloat32(result, pos);
                        pos += 4;
                        float zDist = FcmData.convFcmAndroidFloat32(result, pos);
                        int satNum = result[pos + 4];

                        // TODO - FCM increment
                        frameId++;
                        CGpsFrame gpsFrame = new CGpsFrame(frameId, longitude, latitude, height, xSpeed, ySpeed, zSpeed, xDist, yDist, zDist, satNum);

                        Message mGps = Message.obtain(null, StreamData.GPS, 0, 0, gpsFrame);
                        mDataHandler.sendMessage(mGps);

                    }
                }

            }

            Log.d(TAG, "Processing Buffer stopped");
        }

        // moved to Processing thread
        private synchronized byte[] ProcessBuffer(FcmByteBuffer buf, FcmData.COMMAND cmd) {
            int i;
            int len = FcmData.prefix.length + cmd.name().length();

            for (i = 0; i < len - FcmData.prefix.length; i++)
                search[i + FcmData.prefix.length] = (byte) cmd.name().charAt(i);

            int idxS = 0, idxE = 0;
            byte[] result = null;
            idxS = buf.find(search, len, 0, buf.size());
            if (idxS != -1) {
                idxE = buf.find(FcmData.postfix, FcmData.postfix.length, idxS
                        + len, buf.size());
                if (idxE != -1) {
                    result = buf.rangeArray(idxS + len, idxE);
                    buf.delete(idxS, idxE + FcmData.postfix.length);
                    idxS = buf.find(search, len, 0, buf.size());
                } else
                    idxS = -1;
            }
            return result;
        }

    }

}
