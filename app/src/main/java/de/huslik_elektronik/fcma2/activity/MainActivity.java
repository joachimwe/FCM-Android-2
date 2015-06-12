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

package de.huslik_elektronik.fcma2.activity;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import de.huslik_elektronik.fcma2.R;
import de.huslik_elektronik.fcma2.service.Controller;
import de.huslik_elektronik.fcma2.service.IFcm;
import de.huslik_elektronik.fcma2.view.GpsFragment;
import de.huslik_elektronik.fcma2.view.HelloFragment;
import de.huslik_elektronik.fcma2.view.MenuFragment;
import de.huslik_elektronik.fcma2.view.SensorFragment;
import de.huslik_elektronik.fcma2.view.SettingFragment;


public class MainActivity extends Activity {

    private boolean mSerivceBound;
    private Controller.FcmController fcmService;

    private MenuFragment frag_menu;
    private HelloFragment frag_about;
    private SensorFragment frag_sensor;
    private GpsFragment frag_gps;
    private SettingFragment frag_setting;

    public static enum FragmentAnimation {NONE, LEFT_TO_RIGHT, RIGHT_TO_LEFT}

    ;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int FILE_SELECTED_REQUEST = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    public static final String FILENAME_DATA = "filenameData";


    private final ServiceConnection mServiceConnection =
            new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    fcmService = (Controller.FcmController) iBinder;
                    fcmService.setActiveFragmet(Controller.ActiveFragment.ABOUT);
                    mSerivceBound = true;
                    fcmService.getBridge().setApplicationContext(getApplicationContext());
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    fcmService = null;
                    mSerivceBound = false;
                }
            };

    public Controller.FcmController getFcmService() {
        return fcmService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        frag_about = new HelloFragment();
        frag_menu = new MenuFragment();
        frag_sensor = new SensorFragment();
        frag_gps = new GpsFragment();
        frag_setting = new SettingFragment();

        setFragment(Controller.ActiveFragment.ABOUT);

        final Intent intent = new Intent(this, Controller.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        // First create the GestureListener that will include all our callbacks.
        // Then create the GestureDetector, which takes that listener as an argument.
        GestureDetector.SimpleOnGestureListener gestureListener = new GestureListener(this);

        final GestureDetector gd = new GestureDetector(this.getApplicationContext(), gestureListener);

        /* For the view where gestures will occur, create an onTouchListener that sends
         * all motion events to the gesture detector.  When the gesture detector
         * actually detects an event, it will use the callbacks you created in the
         * SimpleOnGestureListener to alert your application.
        */

        View gestureView = findViewById(R.id.fragment_container);
        gestureView.setClickable(true);
        gestureView.setFocusable(true);
        gestureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gd.onTouchEvent(motionEvent);
                return false;
            }
        });


    }

    public void onDestroy() {
        // Unbind from the service
        if (mSerivceBound) {
            unbindService(mServiceConnection);
            mSerivceBound = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (fcmService != null && fcmService.getBridge().getBtStatus() == IFcm.BtStatus.CONNECTED) {
            menu.findItem(R.id.action_connect).setVisible(false);
            menu.findItem(R.id.action_disconnect).setVisible(true);
            menu.findItem(R.id.action_bluetooth_searching).setVisible(false);
        } else {
            menu.findItem(R.id.action_connect).setVisible(true);
            menu.findItem(R.id.action_disconnect).setVisible(false);
            menu.findItem(R.id.action_bluetooth_searching).setVisible(true);
        }

        // if datastreaming -> no menu
        if ((fcmService.getActiveFragment() == Controller.ActiveFragment.SENSOR || fcmService.getActiveFragment() == Controller.ActiveFragment.GPS)
                && fcmService.getBridge().getBtType() == IFcm.BtType.DATA_STREAM) {
            menu.findItem(R.id.action_DataStream).setVisible(false);
            menu.findItem(R.id.action_stopStream).setVisible(true);
            menu.findItem(R.id.action_settings).setVisible(false);
            menu.findItem(R.id.action_startMenu).setVisible(false);
        } else {
            menu.findItem(R.id.action_DataStream).setVisible(true);
            menu.findItem(R.id.action_stopStream).setVisible(false);
            menu.findItem(R.id.action_settings).setVisible(true);
            menu.findItem(R.id.action_startMenu).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_connect) {
            fcmService.getBridge().connect();
        }

        if (id == R.id.action_disconnect) {
            fcmService.getBridge().disconnect();
        }

        if (id == R.id.action_startMenu) {
            setFragment(Controller.ActiveFragment.MENU);
            fcmService.getModelMenu().setvMenuHandler(frag_menu.getvMenuHandler());
            fcmService.getBridge().getFcmVersion();
        }

        // touch on Fragment without StreamData -> Change; touch on Frament with Streamdata -> stream FCM Data
        if (id == R.id.action_DataStream) {
            if (fcmService.getActiveFragment() == Controller.ActiveFragment.SENSOR || fcmService.getActiveFragment() == Controller.ActiveFragment.GPS) {
                fcmService.getModelStreamData().clear();
                fcmService.getBridge().startDataStream();
            } else {
                setFragment(Controller.ActiveFragment.SENSOR);
            }
        }

        if (id == R.id.action_stopStream) {
            fcmService.getBridge().stopDataStream();
        }


        if (id == R.id.action_settings) {
            setFragment(Controller.ActiveFragment.SETTING);
        }

        if (id == R.id.action_dataExport) {
            //fcmService.getModelStreamData().saveViaJson();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String dateStr = formatter.format(fcmService.getModelStreamData().getLoggingStart());
            String filename = "FCM_" + dateStr + ".log";
            String payLoad = fcmService.getModelStreamData().saveViaJson();

            File f = null;
            FileOutputStream outputStream;

            try {
                f = new File(getExternalFilesDir(null).getAbsolutePath(), filename);
                outputStream = new FileOutputStream(f);
                outputStream.write(payLoad.getBytes(getString(R.string.JSON)));
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (id == R.id.action_dataclear) {
            fcmService.getModelStreamData().clear();
            setFragment(Controller.ActiveFragment.ABOUT);
            fcmService.setActiveFragmet(Controller.ActiveFragment.ABOUT);
        }

        if (id == R.id.action_dataImport) {
            //   fcmService.getModelStreamData().loadViaJson();
            Intent intent = new Intent(this, FileSelector.class);
            startActivityForResult(intent, FILE_SELECTED_REQUEST);
        }


        if (id == R.id.action_bluetooth_searching) {
            //fcmService.getBridge().btSearching();
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;
        }

        if (id == R.id.action_about)

        {
            setFragment(Controller.ActiveFragment.ABOUT);
            fcmService.setActiveFragmet(Controller.ActiveFragment.ABOUT);
        }

        // update ActionBarMenu
        invalidateOptionsMenu();

        return super.onOptionsItemSelected(item);

    }

    public void setFragment(Controller.ActiveFragment newFrag) {
        setFragment(newFrag, FragmentAnimation.NONE);
    }

    public void setFragment(Controller.ActiveFragment newFrag, FragmentAnimation direction) {
        FragmentManager fragMgr = getFragmentManager();
        FragmentTransaction xact = fragMgr.beginTransaction();

        // Animation on Change
        // TODO: https://github.com/DesarrolloAntonio/FragmentTransactionExtended
        if (direction == FragmentAnimation.LEFT_TO_RIGHT)
            xact.setCustomAnimations(R.animator.fly_left_in, R.animator.fly_right_out);
        if (direction == FragmentAnimation.RIGHT_TO_LEFT)
            xact.setCustomAnimations(R.animator.fly_right_in, R.animator.fly_left_out);

        switch (newFrag) {
            case ABOUT:
                if (null == fragMgr.findFragmentByTag("ABOUT")) {
                    xact.replace(R.id.fragment_container, frag_about, "ABOUT");
                }
                break;
            case MENU:
                if (null == fragMgr.findFragmentByTag("MENU")) {
                    xact.replace(R.id.fragment_container, frag_menu, "MENU");
                }
                break;
            case SENSOR:
                if (null == fragMgr.findFragmentByTag("SENSOR")) {
                    xact.replace(R.id.fragment_container, frag_sensor, "SENSOR");
                }
                break;
            case GPS:
                if (null == fragMgr.findFragmentByTag("GPS")) {
                    xact.replace(R.id.fragment_container, frag_gps, "GPS");
                }
                break;
            case SETTING:
                if (null == fragMgr.findFragmentByTag("SETTING")) {
                    xact.replace(R.id.fragment_container, frag_setting, "SETTING");
                }
                break;
        }
        if (fcmService != null) {
            fcmService.setActiveFragmet(newFrag);

            // update Listeners
            fcmService.getModelStreamData().clearSensorHandler();
            if (newFrag == Controller.ActiveFragment.SENSOR)
                fcmService.getModelStreamData().addSensorHandler(frag_sensor.getsHandler());
            if (newFrag == Controller.ActiveFragment.GPS)
                fcmService.getModelStreamData().addSensorHandler(frag_gps.getGpsHandler());


        }
        xact.commit();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true, false);
                }
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    // setupFcmMenu();

                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case FILE_SELECTED_REQUEST:
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                    String filename = data.getStringExtra(FILENAME_DATA);


                    String payLoad = fcmService.getModelStreamData().saveViaJson();

                    File f = null;
                    FileInputStream inputStream;

                    StringBuilder s = new StringBuilder();

                    try {
                        f = new File(getExternalFilesDir(null).getAbsolutePath(), filename);
                        inputStream = new FileInputStream(f);
                        byte[] isBuffer = new byte[1024];
                        int bytesRead = 0;
                        while ((bytesRead = inputStream.read(isBuffer)) != -1) {
                            String strBuffer = new String(isBuffer, 0, bytesRead, getString(R.string.JSON));
                            s.append(strBuffer);
                        }
                        inputStream.close();
                    } catch (Exception e) {
                        Log.e("xmlParser", e.toString());
                    }

                    fcmService.getModelStreamData().loadViaJson(s.toString());
                }
                break;

        }
    }


    private void connectDevice(Intent data, boolean secure, boolean lastConnect) {
        String address = "test";

        if (!lastConnect)
            address = data.getExtras().getString(
                    DeviceListActivity.EXTRA_DEVICE_ADDRESS); // Get the device MAC address
        else
            ; //   address = setting.getString(LAST_BT, "");


       /* // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        // Save last BT device
        if (device != null) {
            SharedPreferences.Editor editor = setting.edit();
            editor.putString(LAST_BT, address);
            editor.commit();
        }

        // StartupFcmService and Processing

        startupFcmService();

        // Attempt to connect to the device
        mFcmService.connect(device);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.d(TAG, "Wait to complete Connection \n" + e);
        }

        // FCM Hello
        sendMessage(fd.getCmdStr(COMMAND.FCM));*/
    }


}
