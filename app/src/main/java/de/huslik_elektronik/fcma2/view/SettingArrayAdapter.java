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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import de.huslik_elektronik.fcma2.R;
import de.huslik_elektronik.fcma2.model.CDataStreamSetting;


public class SettingArrayAdapter extends ArrayAdapter<CDataStreamSetting> {
    public final String TAG = "SETTING_ARRAY_ADAPTER";
    private final Context context;
    private ArrayList<CDataStreamSetting> values;

    public SettingArrayAdapter(Context context, ArrayList<CDataStreamSetting> values) {
        super(context, R.layout.fragment_setting_item, (List<CDataStreamSetting>) values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.fragment_setting_item, parent, false);

        // update elements
        CheckBox ckb = (CheckBox) layout.findViewById(R.id.setting_ckb);
        ckb.setText(values.get(position).getDesc());
        ckb.setChecked(values.get(position).isActive());

        ckb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                if (buttonView.isChecked()) {
                    values.get(position).setActive(true);
                } else {
                    values.get(position).setActive(false);
                }
            }
        });

        final EditText delay = (EditText) layout.findViewById(R.id.setting_delay);
        delay.setText("" + values.get(position).getDelay());

        delay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String sDelay = delay.getText().toString();
                int delay = values.get(position).getDefaultDelay();
                try {
                    delay = ((int) Integer.parseInt(sDelay));
                } catch (NumberFormatException e) {
                    Log.d(TAG, e.toString());
                }
                values.get(position).setDelay(delay);
            }
        });

        return layout;
    }

}
