package data;

import org.jxmapviewer.viewer.GeoPosition;

public class RoutingData {
    private String name;
    private GeoPosition position;

    public RoutingData(String name, GeoPosition position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public GeoPosition getPosition() {
        return position;
    }
}
