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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;

public class JsonHandler {

    JsonStreamData jsd;

    public JsonHandler(Date loggingStart, ArrayList<CDataStreamSetting> dataStreamSettings, ArrayList<CSensorFrame> dataStreamSensors, ArrayList<CGpsFrame> dataStreamGps) {
        jsd = new JsonStreamData(loggingStart, dataStreamSettings, dataStreamSensors, dataStreamGps);
    }

    public String exportToJson() {
        Gson gson = new Gson();
        String jsonDataStream = gson.toJson(jsd);
        return jsonDataStream;
    }

    public void importFromJson(String jsonStr) {
        jsd.jsonClearStreamData();
        Gson gson = new Gson();
        jsd = gson.fromJson(jsonStr, jsd.getClass());
    }

    public ArrayList<CDataStreamSetting> getDataStreamSetting() {
        return jsd.getDataStreamSettings();
    }

    public ArrayList<CSensorFrame> getDataStreamSensors() {
        return jsd.getDataStreamSensors();
    }

    public ArrayList<CGpsFrame> getDataStreamGps() {
        return jsd.getDataStreamGps();
    }

    public Date getLoggingStart() {
        return jsd.getLoggingStart();
    }

}
