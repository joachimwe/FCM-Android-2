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

package de.huslik_elektronik.fcma2.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.huslik_elektronik.fcma2.R;
import de.huslik_elektronik.fcma2.model.CGpsFrame;

public class GpsListArrayAdapter extends ArrayAdapter<CGpsFrame> {


    private final Context context;
    private ArrayList<CGpsFrame> values;

    public GpsListArrayAdapter(Context context, ArrayList<CGpsFrame> values) {
        super(context, R.layout.fragment_gps_item, (List<CGpsFrame>) values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.fragment_gps_item, parent, false);

        CGpsFrame gpsFrame = values.get(position);

        // Frame Number
        TextView tvFrame = (TextView) rowView.findViewById(R.id.gps_frameNo);
        tvFrame.setText("#" + gpsFrame.getFrameId());
        // Location
        TextView tvLocation = (TextView) rowView.findViewById(R.id.gps_location);
        tvLocation.setText("lat: " + gpsFrame.getLatitude() + "\n" + "long: " + gpsFrame.getLongitude() + "\n" + "h: " + gpsFrame.getAltitude());
        // Speed
        TextView tvSpeed = (TextView) rowView.findViewById(R.id.gps_speed);
        tvSpeed.setText("v_x: " + gpsFrame.getxSpeed() + "\n" + "v_y: " + gpsFrame.getySpeed() + "\n" + "v_z: " + gpsFrame.getzSpeed());
        // Distance to home
        TextView tvDistToDest = (TextView) rowView.findViewById(R.id.gps_distToDestination);
        tvDistToDest.setText("d_x: " + gpsFrame.getxDist() + "\n" + "d_y: " + gpsFrame.getyDist() + "\n" + "d_z: " + gpsFrame.getzDist());

        return rowView;
    }

}
