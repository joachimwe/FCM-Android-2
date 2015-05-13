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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import de.huslik_elektronik.fcma2.R;
import de.huslik_elektronik.fcma2.activity.MainActivity;
import de.huslik_elektronik.fcma2.model.CDataStreamSetting;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingFragment extends Fragment {

    public static String TAG = "SETTING FRAG";

    private View view;
    private Button btn_save;
    private ListView placeholder;
    // Reference to Settings
    private ArrayList<CDataStreamSetting> dataStreamSettings;
    private SettingArrayAdapter saAdapter;


    public SettingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);

        dataStreamSettings = ((MainActivity) this.getActivity()).getFcmService().getModelStreamData().getDataStreamSettings();

        // Create dynamic Setting Layouts
        placeholder = (ListView) view.findViewById(R.id.fsetting_placeholder);
        saAdapter = new SettingArrayAdapter(this.getActivity().getApplicationContext(), dataStreamSettings);
        placeholder.setAdapter(saAdapter);

        return view;
    }


}

