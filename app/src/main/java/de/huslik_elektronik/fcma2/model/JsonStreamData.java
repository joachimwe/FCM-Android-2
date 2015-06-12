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
import java.util.Date;

public class JsonStreamData {

    private Date loggingStart;
    private ArrayList<CDataStreamSetting> dataStreamSettings;
    private ArrayList<CSensorFrame> dataStreamSensors;
    private ArrayList<CGpsFrame> dataStreamGps;

    public JsonStreamData(){}

    public JsonStreamData(Date loggingStart, ArrayList<CDataStreamSetting> dataStreamSettings, ArrayList<CSensorFrame> dataStreamSensors, ArrayList<CGpsFrame> dataStreamGps) {
        this.loggingStart = loggingStart;
        this.dataStreamSettings = dataStreamSettings;
        this.dataStreamSensors = dataStreamSensors;
        this.dataStreamGps = dataStreamGps;
    }

    public ArrayList<CDataStreamSetting> getDataStreamSettings() {
        return dataStreamSettings;
    }

    public ArrayList<CSensorFrame> getDataStreamSensors() {
        return dataStreamSensors;
    }

    public ArrayList<CGpsFrame> getDataStreamGps() {
        return dataStreamGps;
    }

    public Date getLoggingStart() {
        return loggingStart;
    }

    public void jsonClearStreamData()
    {
        loggingStart = null;
        dataStreamSettings.clear();
        dataStreamSensors.clear();
        dataStreamGps.clear();
    }

}
