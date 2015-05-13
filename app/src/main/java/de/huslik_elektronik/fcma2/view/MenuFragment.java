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
import android.os.Handler;
import android.os.Message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.huslik_elektronik.fcma2.R;

public class MenuFragment extends Fragment {

    public final static int NEW_VERSION = 1;
    public final static int NEW_MENUTEXT = 2;

    private View view;
    private TextView tvVersion, tvMenuText;

    private Handler vMenuHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NEW_VERSION:
                    tvVersion.setText("test jetzt kommts an");
                    break;
                case NEW_MENUTEXT:
                    String menuText = (String) msg.obj;
                    tvMenuText.setText(menuText);
                    break;
            }
        }
    };


    public MenuFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        tvVersion = (TextView) view.findViewById(R.id.fm_version);
        tvMenuText = (TextView) view.findViewById(R.id.fm_menutext);

        return view;
    }

    public Handler getvMenuHandler() {
        return vMenuHandler;
    }
}

