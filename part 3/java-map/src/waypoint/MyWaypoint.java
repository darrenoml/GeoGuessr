package waypoint;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

public class MyWaypoint extends DefaultWaypoint {

    private String name;
    private JButton button;
    private PointType pointType;

    public MyWaypoint(String name, GeoPosition coord, ImageIcon icon) {
        super(coord);
        this.name = name;
        initButton(icon);
    }

    public MyWaypoint() {
    }

    private void initButton(ImageIcon icon) {
        button = new JButton(icon);
        button.setSize(icon.getIconWidth(), icon.getIconHeight());
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
    }

    public String getName() {
        return name;
    }

    public JButton getButton() {
        return button;
    }

    public static enum PointType {
        START, END
    }
}
