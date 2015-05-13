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

import java.util.ArrayList;


public class CDataPacket {
    private int id;
    private ArrayList frames;

    public CDataPacket(int id, ArrayList l) {
        this.id = id;
        frames = l;
    }

    public CDataPacket(int id, CSensorFrame sf) {
        ArrayList l = new ArrayList();
        l.add(sf);
        this.id = id;
        frames = l;
    }

    public ArrayList getFrames() {
        return frames;
    }

    public int getId() {

        return id;
    }
}
