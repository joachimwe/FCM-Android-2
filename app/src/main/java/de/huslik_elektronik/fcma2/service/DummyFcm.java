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

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import de.huslik_elektronik.fcma2.model.CGpsFrame;
import de.huslik_elektronik.fcma2.model.CSensorFrame;
import de.huslik_elektronik.fcma2.model.CVersion;
import de.huslik_elektronik.fcma2.model.Menu;
import de.huslik_elektronik.fcma2.model.StreamData;

public class DummyFcm implements IFcm {
    private Handler mMenuHandler;
    private Handler mDataHandler;

    private BtStatus btStatus;
    private BtType btType;

    protected Context ctx;

    public DummyFcm(Handler m, Handler d) {
        ctx = null;
        mMenuHandler = m;
        mDataHandler = d;
        btStatus = BtStatus.NOTCONNECTED;
        btType = BtType.NONE;
    }

    @Override
    public void setApplicationContext(Context c) {
        ctx = c;
    }

    @Override
    public BtStatus connect() {
        btStatus = BtStatus.CONNECTED;
        return BtStatus.CONNECTED;
    }

    @Override
    public BtStatus disconnect() {
        btStatus = BtStatus.NOTCONNECTED;
        return BtStatus.NOTCONNECTED;
    }

    public BtStatus getBtStatus() {
        return btStatus;
    }

    @Override
    public void getFcmVersion() {
        btType = BtType.SINGLE;
        CVersion v = new CVersion(2, 3, 80);
        Message m = Message.obtain(null, Menu.VERSION, 0, 0, v);
        mMenuHandler.sendMessage(m);
    }

    @Override
    public void startMenuStream() {
        btType = BtType.MENU_STREAM;
    }

    @Override
    public void startMenuStream(MenuCommand mc) {
        btType = BtType.MENU_STREAM;
    }

    @Override
    public void stopMenuStream() {
        btType = BtType.NONE;
    }

    @Override
    public BtType getBtType() {
        return btType;
    }

    @Override
    public void startDataStream() {
        btType = BtType.DATA_STREAM;

        // Test
        // Sensor
        int[] data0 = {10, 20, 15};
        int[] data1 = {4, 10, 30};

        CSensorFrame s1 = new CSensorFrame(0);
        s1.setG(data0);
        s1.setA(data1);

        CSensorFrame s2 = new CSensorFrame(1);
        s2.setG(data1);
        s2.setA(data0);

        Message m = Message.obtain(mDataHandler, StreamData.SENSOR, s1);
        mDataHandler.sendMessage(m);

        Message m2 = Message.obtain(mDataHandler, StreamData.SENSOR, s2);
        mDataHandler.sendMessage(m2);

        // Gps
        CGpsFrame g0 = new CGpsFrame(0, 10.332f, 48.389f, 440f, 2f, 0f, 0f, 0f, 0f,
                0f, 6);
        Message mg0 = Message.obtain(mDataHandler, StreamData.GPS, g0);
        mDataHandler.sendMessage(mg0);

        CGpsFrame g1 = new CGpsFrame(1, 10.332f, 48.489f, 440f, 2f, 0f, 0f, 0f, 0f,
                0f, 6);
        Message mg1 = Message.obtain(mDataHandler, StreamData.GPS, g1);
        mDataHandler.sendMessage(mg1);

        CGpsFrame g2 = new CGpsFrame(2, 10.432f, 48.289f, 440f, 2f, 0f, 0f, 0f, 0f,
                0f, 6);
        Message mg2 = Message.obtain(mDataHandler, StreamData.GPS, g2);
        mDataHandler.sendMessage(mg2);

        g0 = new CGpsFrame(3, 10.338f, 48.384f, 420f, 2f, 0f, 0f, 0f, 0f,
                0f, 6);
        mg0 = Message.obtain(mDataHandler, StreamData.GPS, g0);
        mDataHandler.sendMessage(mg0);

        g0 = new CGpsFrame(4, 10.315f, 48.284f, 410f, 2f, 0f, 0f, 0f, 0f,
                0f, 6);
        mg0 = Message.obtain(mDataHandler, StreamData.GPS, g0);
        mDataHandler.sendMessage(mg0);

        g0 = new CGpsFrame(5, 10.415f, 48.184f, 400f, 2f, 0f, 0f, 0f, 0f,
                0f, 6);
        mg0 = Message.obtain(mDataHandler, StreamData.GPS, g0);
        mDataHandler.sendMessage(mg0);

    }

    @Override
    public void stopDataStream() {
        btType = BtType.NONE;
    }


}
