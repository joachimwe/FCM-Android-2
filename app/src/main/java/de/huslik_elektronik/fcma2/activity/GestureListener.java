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

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import de.huslik_elektronik.fcma2.service.Controller;

/**
 * detects gesture for changing fragments
 */

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private MainActivity mainActivity;

    public GestureListener(MainActivity mActivity) {
        super();
        mainActivity = mActivity;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {


        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                }
                result = true;
            } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
            }
            result = true;

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }


    public void onSwipeRight() {
        switch (mainActivity.getFcmService().getActiveFragment()) {
            case SENSOR:
                mainActivity.setFragment(Controller.ActiveFragment.GPS, MainActivity.FragmentAnimation.LEFT_TO_RIGHT);
                break;
            case GPS:
                mainActivity.setFragment(Controller.ActiveFragment.SENSOR, MainActivity.FragmentAnimation.LEFT_TO_RIGHT);
                break;
            default:
        }
        Log.d("Gesture", "onSwipeRight");

    }

    public void onSwipeLeft() {
        switch (mainActivity.getFcmService().getActiveFragment()) {
            case SENSOR:
                mainActivity.setFragment(Controller.ActiveFragment.GPS, MainActivity.FragmentAnimation.RIGHT_TO_LEFT);
                break;
            case GPS:
                mainActivity.setFragment(Controller.ActiveFragment.SENSOR, MainActivity.FragmentAnimation.RIGHT_TO_LEFT);
                break;
            default:
        }
        Log.d("Gesture", "onSwipeLeft");
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }

}
