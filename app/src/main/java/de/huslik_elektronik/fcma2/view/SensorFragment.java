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
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import de.huslik_elektronik.fcma2.R;
import de.huslik_elektronik.fcma2.activity.MainActivity;
import de.huslik_elektronik.fcma2.model.CSensorFrame;
import de.huslik_elektronik.fcma2.model.StreamData;

public class SensorFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // Chart
    // private XYSeries mSeries = null;
    private XYMultipleSeriesDataset mDataset = null;
    private XYMultipleSeriesRenderer mRenderer = null;

    // Layout
    private View view;
    private GraphicalView chart;
    private Spinner spDataset;
    private LinearLayout chartContainer;

    // Data Serie
    CSensorFrame.SENSOR sens = CSensorFrame.SENSOR.Gyro;

    // The Handler that gets information back from the BluetoothChatService
    private final Handler sHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case StreamData.SENSOR:
                    CSensorFrame sensorFrame = (CSensorFrame) msg.obj;
                    int xPos = msg.arg1;

                    int[] d = sensorFrame.getSensorArray(sens);
                    for (int j = 0; j < mDataset.getSeriesCount(); j++) {
                        mDataset.getSeriesAt(j).add(xPos, d[j]);
                    }
                    // draw chart new
                    chart.invalidate();
                    break;
                default:
                    // do nothing
            }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sensor, container, false);

        // PreWork from Ressource Files
        float sizeMenu = 24;

        // Spinner Sensor Choice
        ArrayAdapter<String> datasetAdapter = new ArrayAdapter<>(
                view.getContext(), android.R.layout.simple_spinner_dropdown_item,
                CSensorFrame.getLabel());
        spDataset = (Spinner) view.findViewById(R.id.dataType);
        spDataset.setAdapter(datasetAdapter);
        spDataset.setOnItemSelectedListener(this);

        // Chart
        chartContainer = (LinearLayout) view.findViewById(R.id.chart);
        chart = setupChart(view, sens); // .Gyro);
        chartContainer.addView(chart);

        return view;
    }

    public Handler getsHandler() {
        return sHandler;
    }

    // Spinner Methods
    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        String[] sensArray = CSensorFrame.getLabel();
        String sensStr = sensArray[pos];
        sens = CSensorFrame.SENSOR.valueOf(sensStr);

        // Chart replace for new sensor data
        if (chart != null)
            chartContainer.removeView(chart);
        chart = null;
        chart = setupChart(view, sens);
        chartContainer.addView(chart);
        ((MainActivity) getActivity()).getFcmService().getModelStreamData().sendAllSensorFrames();
        chartContainer.invalidate();

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    // Chart Methods

    private GraphicalView setupChart(View v, CSensorFrame.SENSOR sens) {

        // CSensorFrame.SENSOR sens = CSensorFrame.SENSOR.Gyro; // Gyro

        mDataset = new XYMultipleSeriesDataset();
        mRenderer = new XYMultipleSeriesRenderer();

        int dim = CSensorFrame.getDimension(sens);

        for (int j = 0; j < dim; j++) {
            // Series
            XYSeries f = new XYSeries(CSensorFrame.getChartLabel(sens, j, 0));
            mDataset.addSeries(f);
            // Renderer
            XYSeriesRenderer renderer = new XYSeriesRenderer();
            renderer.setColor(CSensorFrame.colorList[j]);
            renderer.setFillPoints(true);
            renderer.setPointStyle(PointStyle.POINT);
            renderer.setLineWidth(5.0f);
            mRenderer.addSeriesRenderer(renderer);
        }

        Resources r = v.getContext().getResources();
//        float size = r.getDimension(R.dimen.textsizeChart);
        float size = 40;
        mRenderer.setLabelsTextSize((float) (size * 0.8));
        mRenderer.setLegendTextSize(size);
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins - See more at: http://www.survivingwithandroid.com/2014/06/android-chart-tutorial-achartengine.html#sthash.rNAFEt64.dpuf
        mRenderer.setShowGrid(true);
        mRenderer.setMargins(new int[]{50, 50, 50, 50});

        mRenderer.setGridColor(Color.LTGRAY);
        mRenderer.setXAxisColor(Color.WHITE);
        mRenderer.setYAxisColor(Color.WHITE);
        mRenderer.setYLabelsColor(0, Color.WHITE);
        mRenderer.setXLabelsColor(Color.WHITE);

        mRenderer.setZoomEnabled(true);
        // mRenderer.setZoomButtonsVisible(true);
        // not sizeable -> need to put own buttons -
        // http://stackoverflow.com/questions/13444672/achartengine-zoom-button-size

        GraphicalView chart = ChartFactory.getLineChartView(this.getActivity(),
                mDataset, mRenderer);
        return chart;

    }

}

