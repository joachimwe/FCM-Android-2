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

import de.huslik_elektronik.fcma2.service.Controller;
import de.huslik_elektronik.fcma2.view.MenuFragment;

public class Menu {

    public final static int VERSION = 100;
    public final static int MENU = 102;

    private String menuText;
    private CVersion version;

    private Controller controller;

    // to inform View if menuText is updated

    private Handler vMenuHandler;
    private Handler btMenuHandler =
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Message m = null;

                    switch (msg.what) {
                        case VERSION:
                            version = (CVersion) msg.obj;
                            m = Message.obtain(null, MenuFragment.NEW_VERSION, 0, 0, version);
                            break;
                        case MENU:
                            menuText = (String) msg.obj;
                            m = Message.obtain(null, MenuFragment.NEW_MENUTEXT, 0, 0, menuText);
                            break;
                        default:
                            // do nothing
                    }
                    // signal new data, if active
                    if (controller.getActiveFragment() == Controller.ActiveFragment.MENU)
                        vMenuHandler.sendMessage(m);
                }
            };

    public Menu(Controller c) {
        this.controller = c;
    }

    public void setvMenuHandler(Handler vMenuHandler) {
        this.vMenuHandler = vMenuHandler;
    }

    public String getMenuText() {

        return menuText;
    }

    public CVersion getVersion() {
        return version;
    }

    public Handler getBtMenuHandler() {
        return btMenuHandler;
    }
}
