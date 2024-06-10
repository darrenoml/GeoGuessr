package test;

import javax.swing.ImageIcon;
import org.jxmapviewer.viewer.GeoPosition;

public class Location {
    private GeoPosition geoPosition;
    private String name;
    private String imagePath;
    private ImageIcon imageIcon;

    public Location(GeoPosition geoPosition, String name, String imagePath) {
        this.geoPosition = geoPosition;
        this.name = name;
        this.imagePath = imagePath;
        this.imageIcon = createImageIcon(imagePath);
    }

    public GeoPosition getGeoPosition() {
        return geoPosition;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    private ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
