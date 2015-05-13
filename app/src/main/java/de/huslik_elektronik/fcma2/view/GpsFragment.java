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

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.MapBoxTileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

import de.huslik_elektronik.fcma2.R;
import de.huslik_elektronik.fcma2.activity.MainActivity;
import de.huslik_elektronik.fcma2.model.CGpsFrame;
import de.huslik_elektronik.fcma2.model.StreamData;

public class GpsFragment extends Fragment {

    private View view;
    private MapView vMap;
    private TextView vInfo;

    private Marker actualMarker = null;

    private ListView lWaypoints;
    private GpsListArrayAdapter wayPointsAdapter;

    private ArrayList<CGpsFrame> gpsFrameList;

    private ArrayList<GeoPoint> gp = new ArrayList<>();

    // The Handler that gets information back from the BluetoothChatService
    private final Handler gpsHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case StreamData.GPS:
                    CGpsFrame gpsFrame = (CGpsFrame) msg.obj;
                    int xPos = msg.arg1;

                    // Update ListView
                    lWaypoints.invalidateViews();

                    // Update Chart
                    // not while streaming data

                    // Update Info
            }
        }

    };

    public Handler getGpsHandler() {
        return gpsHandler;
    }

    public GpsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gps, container, false);

        // Flight Waypoints
        gpsFrameList = ((MainActivity) this.getActivity()).getFcmService().getModelStreamData().getGpsFrameList();
        wayPointsAdapter = new GpsListArrayAdapter(this.getActivity().getApplicationContext(), gpsFrameList);
        lWaypoints = (ListView) view.findViewById(R.id.fg_liste);
        lWaypoints.setAdapter(wayPointsAdapter);

        lWaypoints.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Set Flightpath
                updatePolyline();
                // set actual position
                if (actualMarker != null)
                    vMap.getOverlays().remove(actualMarker);
                actualMarker = new Marker(vMap);
                GeoPoint actualPos = new GeoPoint(gpsFrameList.get(i).getLatitude(), gpsFrameList.get(i).getLongitude());
                actualMarker.setPosition(actualPos);
                actualMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                actualMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker_poi));
                vMap.getOverlays().add(actualMarker);
                vMap.invalidate();
            }

        });

        // Map Issues
        vMap = (MapView) view.findViewById(R.id.fg_map);
        // MAPQUESTOSM
        //vMap.setTileSource(TileSourceFactory.MAPQUESTOSM);
        vMap.setTileSource(TileSourceFactory.MAPNIK);

        vMap.setBuiltInZoomControls(true);
        vMap.setMultiTouchControls(true);
        vMap.getController().setZoom(11);
        vMap.getController().setCenter(new GeoPoint(10.332f, 48.389f));

        updatePolyline();

        // Flight Info
        // TODO


        return view;
    }

    /**
     * Create Path if status no streaming (due to delay); gps data present
     */

    private void updatePolyline() {
        gp.clear();
        if (gpsFrameList.size() > 0) {
            for (CGpsFrame gpsFrame : gpsFrameList)
                gp.add(new GeoPoint(gpsFrame.getLatitude(), gpsFrame.getLongitude(), gpsFrame.getAltitude()));
            org.osmdroid.bonuspack.overlays.Polyline pl = new org.osmdroid.bonuspack.overlays.Polyline(getActivity().getBaseContext());
            pl.setPoints(gp);
            vMap.getOverlays().add(pl);
            vMap.getController().setCenter(gp.get(0));
        }
    }

}

