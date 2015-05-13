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

package de.huslik_elektronik.fcma2.model;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

import de.huslik_elektronik.fcma2.service.Controller;

public class StreamData {

    public final static int SENSOR = 0;
    public final static int GPS = 1;
    public final static int PACKET_TYPES = 2;       // must be the highest value

    private int[] lastPacketSend;

    private Controller controller;

    private ArrayList<CDataStreamSetting> dataStreamSettings;

    // private ArrayList<CDataPacket> dataPackets;
    private ArrayList dataStreams;

    private ArrayList<Handler> vHandlers;
    private Handler btStreamHandler =
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Message m = null;

                    switch (msg.what) {
                        case SENSOR:
                            CSensorFrame sf = (CSensorFrame) msg.obj;
                            ((ArrayList<CSensorFrame>) dataStreams.get(SENSOR)).add(sf);
                            if (vHandlers != null) {
                                for (Handler h : vHandlers) {
                                    m = Message.obtain(h, SENSOR, sf.getFrameId(), 0, sf);
                                    h.sendMessage(m);
                                }
                                lastPacketSend[SENSOR] = ((ArrayList<CSensorFrame>) dataStreams.get(SENSOR)).size();
                            }
                            break;
                        case GPS:
                            CGpsFrame gf = (CGpsFrame) msg.obj;
                            ((ArrayList<CGpsFrame>) dataStreams.get(GPS)).add(gf);
                            if (vHandlers != null) {
                                for (Handler h : vHandlers) {
                                    m = Message.obtain(h, GPS, gf.getFrameId(), 0, gf);
                                    h.sendMessage(m);
                                }
                                lastPacketSend[GPS] = ((ArrayList<CGpsFrame>) dataStreams.get(GPS)).size();
                            }
                            break;


                    }
                    // TODO - Packet loss; if packet ID differs more than 1

                }
            };

    public StreamData(Controller c) {
        controller = c;
        // DataPackets
        //dataPackets = new ArrayList<CDataPacket>();
        dataStreams = new ArrayList<>();
        dataStreams.add(new ArrayList<CSensorFrame>());
        dataStreams.add(new ArrayList<CGpsFrame>());

        // set inital lastPacketSend -> for loss detection
        lastPacketSend = new int[PACKET_TYPES];
        for (int elem : lastPacketSend)
            elem = 0;

        // Datastream Settings
        dataStreamSettings = new ArrayList<>();
        dataStreamSettings.add(new CDataStreamSetting(SENSOR, "Sensors", true, true, 100));
        dataStreamSettings.add(new CDataStreamSetting(GPS, "GPS", true, true, 200));

        // Handler Queue
        vHandlers = new ArrayList<>();


    }

    public ArrayList<CDataStreamSetting> getDataStreamSettings() {
        return dataStreamSettings;
    }

    public void setDataStreamSettings(ArrayList<CDataStreamSetting> dataStreamSettings) {
        this.dataStreamSettings = dataStreamSettings;
    }

    public ArrayList<CGpsFrame> getGpsFrameList() {
        return (ArrayList<CGpsFrame>) dataStreams.get(GPS);
    }

    public void addSensorHandler(Handler handler) {
        vHandlers.add(handler);
    }

    public void clearSensorHandler() {
        vHandlers.clear();
    }

    public Handler getBtStreamHandler() {
        return btStreamHandler;
    }

    public int getStreamSize(int streamNumber) {
        return ((ArrayList) dataStreams.get(streamNumber)).size();
    }

    public void clear() {
        for (int i = 0; i < dataStreams.size(); i++)
            ((ArrayList) dataStreams.get(i)).clear();
    }

    public void sendAllSensorFrames() {
        Message m = null;

        for (CSensorFrame frame : ((ArrayList<CSensorFrame>) dataStreams.get(SENSOR))) {
            if (vHandlers != null) {
                for (Handler h : vHandlers) {
                    m = Message.obtain(h, SENSOR, frame.getFrameId(), 0, frame);
                    h.sendMessage(m);
                }
                lastPacketSend[SENSOR] = ((ArrayList<CSensorFrame>) dataStreams.get(SENSOR)).size();
            }
        }
//
//        for (CDataPacket dp : dataPackets) {
//            for (Object frame : dp.getFrames()) {
//                // Sensor Frames
//                if (frame instanceof CSensorFrame) {
//                    for (Handler h : vHandlers) {
//                        m = Message.obtain(h, SENSOR, dp.getId(), 0, frame);
//                        h.sendMessage(m);
//                    }
//
//                }
//            }
//        }
//        lastPacketSend[SENSOR] = dataPackets.size();
    }


}
