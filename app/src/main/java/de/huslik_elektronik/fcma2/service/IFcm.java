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

public interface IFcm {

    public static enum MenuCommand {PP, P, ENTER, M, MM}

    ;

    public static enum BtStatus {CONNECTED, NOTCONNECTED}

    ;

    public static enum BtType {NONE, SINGLE, MENU_STREAM, DATA_STREAM}

    ;

    /**
     * SetApplicationContext
     */
    public void setApplicationContext(Context ctx);

    /**
     * Connect to source e.g. BT
     */
    public BtStatus connect(String address);

    /**
     * Disconnect from source
     */
    public BtStatus disconnect();


    /**
     * getBluetooth Status
     *
     * @return BtStatus
     */

    public BtStatus getBtStatus();

    /**
     * Connection type
     *
     * @return BtType
     */
    public BtType getBtType();


    /**
     * get FcmVersion and Menusize, result via handler in model menu
     */
    public void getFcmVersion();

    /**
     * startMenuStream -> result via handler in model menu
     */
    public void startMenuStream();

    /**
     * startMenuStream - send MenuCommands to RealFcm
     *
     * @param mc - navigation commands
     */
    public void startMenuStream(MenuCommand mc);

    /**
     * stopMenuStream - stops fcm streaming...
     */
    public void stopMenuStream();

    /**
     * startMenuStream
     */
    public void startDataStream();

    /**
     * stopMenuStream - stops fcm streaming...
     */
    public void stopDataStream();

}
