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

public class CDataStreamSetting {
    private int packetType;
    private String desc;
    private boolean visible;
    private boolean active;
    private int delay;

    private int defaultDelay;

    private static int timebase = 10;           // FCM timebase for unit ist 10 ms

    public CDataStreamSetting(int packetType, String desc, boolean visible, boolean active, int delay) {
        this.packetType = packetType;
        this.desc = desc;
        this.visible = visible;
        this.active = active;
        this.delay = delay;
        this.defaultDelay = delay;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getPacketType() {

        return packetType;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isActive() {
        return active;
    }

    public int getDelay() {
        return delay;
    }

    public String getDesc() {
        return desc;
    }

    public int getDefaultDelay() {
        return defaultDelay;
    }

    public int getDelayParameterFCM() {
        return ((int) (((double) getDelay()) / ((double) timebase)));
    }
}
