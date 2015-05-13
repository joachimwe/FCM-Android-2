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


public class CVersion {

    private int menuItems;
    private int version_h, version_l;

    public CVersion(byte[] result) {
        version_h = (((0xFF & (int) result[0]) * 256)
                + ((0xFF & (int) result[1])));
        version_l = (((0xFF & (int) result[2]) * 256)
                + ((0xFF & (int) result[3])));
        menuItems = ((0xFF & (int) result[4]) * 256)
                + ((0xFF & (int) result[5]));
    }

    public CVersion(int vh, int vl, int mItems) {
        version_h = vh;
        version_l = vl;
        menuItems = mItems;
    }

    public int getMenuItems() {
        return menuItems;
    }

    public int getVersion_h() {
        return version_h;
    }

    public int getVersion_l() {
        return version_l;
    }
}
