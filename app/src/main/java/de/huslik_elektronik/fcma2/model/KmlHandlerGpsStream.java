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

public class KmlHandlerGpsStream {

    /**
     * Formats tracker data as KML output
     */

    final public static String version = "<version value = \"1\"/>";

    public String getKML(ArrayList<CGpsFrame> track) {
        StringBuilder kml = new StringBuilder();

        kml.append(getHeader());
        kml.append(getFlight(track, "fcmLine"));
        kml.append(getFooter());

        return kml.toString();
    }

    public String getHeader() {
        LineBuilder builder = new LineBuilder();
        builder.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        builder.add("<kml xmlns=\"http://earth.google.com/kml/2.1\">");
        builder.add("<Document>");
        builder.add("<Description>");
        builder.add("FCM Android - Copter Log");
        builder.add("</Description>");
        builder.add("<Style id=\"fcmLine\">");
        builder.add("<LineStyle>" + "<color>7f0000ff</color>"
                + "<width>4</width>" + "</LineStyle>");
        // + "<PolyStyle><color>4f000077</color></PolyStyle>");
        builder.add("</Style>");
        return builder.toString();

    }

    public String getFooter() {
        LineBuilder builder = new LineBuilder();
        builder.add("</Document>");
        builder.add("</kml>");
        return builder.toString();
    }

    public String getFlight(ArrayList<CGpsFrame> track, String style) {
        LineBuilder builder = new LineBuilder();

        builder.add("<Folder>");
        builder.add("<name>All about Flight Track</name>");
        builder.add("<visibility>0</visibility>");
        builder.add("<description>Copter Log No. 1</description>");
        builder.add("<Placemark>");
        builder.add("<name>Flight Track</name>");
        builder.add(getTrack(track, style));
        builder.add(getExtendedData(track));
        builder.add("</Placemark>");
        builder.add("</Folder>");

        return builder.toString();

    }

    public String getTrack(ArrayList<CGpsFrame> track, String style) {
        LineBuilder builder = new LineBuilder();

        builder.add("<styleUrl>");
        builder.add("#" + style);
        builder.add("</styleUrl>");
        builder.add("<LineString>");
        builder.add("<extrude>1</extrude>");
        builder.add("<tessellate>1</tessellate>");
        builder.add("<altitudeMode>relative</altitudeMode>");
        builder.add("<coordinates>");
        for (CGpsFrame loc : track)
            builder.add(loc.getLongitude() + "," + loc.getLatitude() + ","
                    + loc.getAltitude());

        builder.add("</coordinates>");
        builder.add("</LineString>");

        return builder.toString();
    }


    private String getExtendedData(ArrayList<CGpsFrame> track) {
        LineBuilder builder = new LineBuilder();
        builder.add("<ExtendedData>");
        builder.add(KmlHandlerGpsStream.version);
        for (CGpsFrame loc : track) {
            builder.add("<" + CGpsFrame.Frame + ">");
            builder.add("<" + CGpsFrame.Position + " lon=\"" + loc.getLongitude() + "\" lat=\"" + loc.getLatitude() + "\" height = \"" +
                    +loc.getAltitude() + "\"/>");
            builder.add("<" + CGpsFrame.Speed + " vx=\"" + loc.getxSpeed() + "\" vy=\"" + loc.getySpeed() + "\" vz = \"" +
                    +loc.getzSpeed() + "\"/>");
            builder.add("<" + CGpsFrame.DistanceToHome + " dx=\"" + loc.getxDist() + "\" dy=\"" + loc.getyDist() + "\" dz = \"" +
                    +loc.getzDist() + "\"/>");
            builder.add("</" + CGpsFrame.Frame + ">");
        }
        builder.add("</ExtendedData>");
        return builder.toString();
    }


    private static class LineBuilder {
        private StringBuilder mBuilder;

        public LineBuilder() {
            mBuilder = new StringBuilder();
        }

        public void add(String line) {
            mBuilder.append(line);
            mBuilder.append("\n");
        }

        @Override
        public String toString() {
            return mBuilder.toString();
        }


    }
}
