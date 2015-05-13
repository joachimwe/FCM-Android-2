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

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import de.huslik_elektronik.fcma2.model.Menu;
import de.huslik_elektronik.fcma2.model.StreamData;

public class Controller extends Service {

    public static enum ActiveFragment {ABOUT, MENU, SENSOR, GPS, SETTING}

    ;

    private ActiveFragment activeFragment;

    private Menu mMenu;
    private StreamData mStreamData;
    private IFcm bridge;

    private final IBinder mBinder = new FcmController();

    public class FcmController extends Binder {

        public Menu getModelMenu() {
            return mMenu;
        }

        public StreamData getModelStreamData() {
            return mStreamData;
        }

        public IFcm getBridge() {
            return bridge;
        }

        public void setActiveFragmet(ActiveFragment af) {
            activeFragment = af;
        }

        public ActiveFragment getActiveFragment() {
            return activeFragment;
        }

    }


    public Controller() {
        // Factory
        mMenu = new Menu(this);
        mStreamData = new StreamData(this);
        bridge = new DummyFcm(mMenu.getBtMenuHandler(), mStreamData.getBtStreamHandler());
    }

    public ActiveFragment getActiveFragment() {
        return activeFragment;
    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "Binding to Service", Toast.LENGTH_SHORT).show();
        return mBinder;
    }
}
