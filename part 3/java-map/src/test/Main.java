package test;

import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;
import waypoint.EventWaypoint;
import waypoint.MyWaypoint;
import waypoint.WaypointRender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class Main extends javax.swing.JFrame {

    private final Set<MyWaypoint> waypoints = new HashSet<>();
    private EventWaypoint event;
    private Point mousePosition;
    private GeoPosition randomGeoPosition;
    private GeoPosition guessedLocation;
    private MouseAdapter mapClickListener;
    private Location randomLocation;
    private JLabel imageLabel; // JLabel to display the images
    private boolean dragging = false;
    private Point pressPoint;
    private int score;

    // Define the default center position and zoom level
    private static final GeoPosition DEFAULT_CENTER_POSITION = new GeoPosition(20.0, 0.0);
    private static final int DEFAULT_ZOOM_LEVEL = 2;

    public Main() {
        initComponents();
        initLocations();
        init();
    }

    private List<Location> locations = new ArrayList<>();

    private void initLocations() {
        locations.add(new Location(new GeoPosition(13.4125, 103.8670), "Angkor Wat", "/test/PLACES/2-angkor-wat-getty (1).jpg"));
        locations.add(new Location(new GeoPosition(-33.8568, 151.2153), "Sydney Opera House", "/test/PLACES/3-sydney-opera-house-submitted-masaru-kitano-snak-productions-tourism-australia-4 (1).jpg"));
        locations.add(new Location(new GeoPosition(48.8584, 2.2945), "Eiffel Tower", "/test/PLACES/4-eiffel-tower-getty.jpg"));
        locations.add(new Location(new GeoPosition(27.1751, 78.0421), "Taj Mahal", "/test/PLACES/5-taj-mahal-getty (1).jpg"));
        locations.add(new Location(new GeoPosition(40.4319, 116.5704), "Great Wall of China", "/test/PLACES/8-great-wall-getty.jpg"));
        locations.add(new Location(new GeoPosition(43.8803, -103.4538), "Mount Rushmore", "/test/PLACES/9-mount-rushmore-getty.jpg"));
        locations.add(new Location(new GeoPosition(48.6361, -1.5115), "Mont Saint Michel", "/test/PLACES/10-mont-saint-michel-getty.jpg"));
        locations.add(new Location(new GeoPosition(37.9715, 23.7257), "Acropolis", "/test/PLACES/12-acropolis-getty.jpg"));
        locations.add(new Location(new GeoPosition(-27.1127, -109.3497), "Easter Island", "/test/PLACES/13-easter-island-getty.jpg"));
        locations.add(new Location(new GeoPosition(37.8199, -122.4786), "Golden Gate Bridge", "/test/PLACES/14-san-francisco-getty.jpg"));
        locations.add(new Location(new GeoPosition(47.5576, 10.7498), "Neuschwanstein Castle", "/test/PLACES/15-german-castle-getty.jpg"));
        locations.add(new Location(new GeoPosition(-22.9519, -43.2105), "Christ the Redeemer", "/test/PLACES/23-christ-the-redeemer-getty.jpg"));
        locations.add(new Location(new GeoPosition(36.2679, -112.3535), "Grand Canyon", "/test/PLACES/grand-canyon-stock (1).jpg"));
        locations.add(new Location(new GeoPosition(29.9792, 31.1342), "Pyramid of Giza", "/test/PLACES/17-pyramids-getty.jpg"));
        locations.add(new Location(new GeoPosition(43.7230, 10.3966), "Leaning Tower of Pisa", "/test/PLACES/16-leaning-tower-of-pisa-getty.jpg"));
    }

    private void init() {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        jXMapViewer.setTileFactory(tileFactory);

        // Center the map and zoom out to default values
        setDefaultMapState();

        // Create event mouse move
        PanMouseInputListener mm = new PanMouseInputListener(jXMapViewer);
        jXMapViewer.addMouseListener(mm);
        jXMapViewer.addMouseMotionListener(mm);
        jXMapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(jXMapViewer));
        event = getEvent();

        // Initialize the JLabel for displaying images
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(400, 300));

        // Use BorderLayout to add components
        setLayout(new BorderLayout());
        add(jXMapViewer, BorderLayout.CENTER);
        add(imageLabel, BorderLayout.SOUTH);

        // Start the game
        startNewGame();
    }

    private void startNewGame() {
        // Remove the previous listener if it exists
        if (mapClickListener != null) {
            jXMapViewer.removeMouseListener(mapClickListener);
            jXMapViewer.removeMouseMotionListener(mapClickListener);
        }

        // Center the map and zoom out to default values at the start of each round
        setDefaultMapState();

        // Generate a random location
        randomLocation = getRandomLocation();
        randomGeoPosition = randomLocation.getGeoPosition();

        // Notify the user to make a guess
        ImageIcon icon = new ImageIcon(getClass().getResource(randomLocation.getImagePath()));
        imageLabel.setIcon(icon); // Display the image in the JLabel

        // Clear previous waypoints
        clearWaypoint();

        // Set up a new listener for user guesses
        mapClickListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                if (SwingUtilities.isLeftMouseButton(evt)) {
                    pressPoint = evt.getPoint();
                    dragging = false;
                }
            }

            @Override
            public void mouseDragged(MouseEvent evt) {
                dragging = true;
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (SwingUtilities.isLeftMouseButton(evt) && !dragging) {
                    mousePosition = evt.getPoint();
                    guessedLocation = jXMapViewer.convertPointToGeoPosition(mousePosition);
                    addWaypoint(new MyWaypoint("Guess", guessedLocation, new ImageIcon(getClass().getResource("/icon/pin.png"))));
                    checkGuess();
                }
            }
        };
        jXMapViewer.addMouseListener(mapClickListener);
        jXMapViewer.addMouseMotionListener(mapClickListener);
    }

    private void setDefaultMapState() {
        jXMapViewer.setZoom(DEFAULT_ZOOM_LEVEL);
        jXMapViewer.setAddressLocation(DEFAULT_CENTER_POSITION);
    }

    private Location getRandomLocation() {
        Random rand = new Random();
        return locations.get(rand.nextInt(locations.size()));
    }

    private void checkGuess() {
        double distance = calculateDistance(randomGeoPosition, guessedLocation);
        int score = calculateScore(distance);
        String message = String.format("Your guess is %.2f km away from the correct location.\nScore: %d", distance, score);
        JOptionPane.showMessageDialog(this, message, "Result", JOptionPane.INFORMATION_MESSAGE);

        // Add waypoint for the correct location
        MyWaypoint actualLocationWaypoint = new MyWaypoint("Actual Location", randomGeoPosition, new ImageIcon(getClass().getResource("/icon/Background.png")));
        System.out.println("Adding waypoint for actual location: " + randomGeoPosition);
        addWaypoint(actualLocationWaypoint);

        // Start a new round after displaying the result
        startNewGame();
    }


    private double calculateDistance(GeoPosition pos1, GeoPosition pos2) {
        final int R = 6371; // Radius of the Earth
        double lat1 = pos1.getLatitude();
        double lon1 = pos1.getLongitude();
        double lat2 = pos2.getLatitude();
        double lon2 = pos2.getLongitude();
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // convert to km
    }

    private int calculateScore(double distance) {
        int maxScore = 5000;
        double maxDistance = 20000; // Maximum distance in kilometers
        if (distance >= maxDistance) {
            return 0;
        }
        return (int) ((1 - (distance / maxDistance)) * maxScore);
    }

    private void addWaypoint(MyWaypoint waypoint) {
        waypoints.add(waypoint);
        initWaypoint();
    }

    private void initWaypoint() {
        WaypointPainter<MyWaypoint> wp = new WaypointRender();
        wp.setWaypoints(waypoints);
        jXMapViewer.setOverlayPainter(wp);
        for (MyWaypoint d : waypoints) {
            jXMapViewer.add(d.getButton());
        }
    }

    private void clearWaypoint() {
        for (MyWaypoint d : waypoints) {
            jXMapViewer.remove(d.getButton());
        }
        waypoints.clear();
        jXMapViewer.repaint();
    }

    private EventWaypoint getEvent() {
        return (MyWaypoint waypoint) -> {
            JOptionPane.showMessageDialog(Main.this, waypoint.getName());
        };
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jPopupMenu1 = new javax.swing.JPopupMenu();
        menuStart = new javax.swing.JMenuItem();
        menuEnd = new javax.swing.JMenuItem();
        jXMapViewer = new data.JXMapViewerCustom();
        cmdClear = new javax.swing.JButton();

        menuStart.setText("Start Point");
        jPopupMenu1.add(menuStart);

        menuEnd.setText("End Point");
        jPopupMenu1.add(menuEnd);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        cmdClear.setText("Clear");
        cmdClear.addActionListener(evt -> cmdClearActionPerformed(evt));

        // Removed GroupLayout initialization here as we're using BorderLayout now

        pack();
    }

    private void cmdClearActionPerformed(java.awt.event.ActionEvent evt) {
        clearWaypoint();
    }

    private void menuStartActionPerformed(java.awt.event.ActionEvent evt) {
        // Custom action for Start Point menu
    }

    private void menuEndActionPerformed(java.awt.event.ActionEvent evt) {
        // Custom action for End Point menu
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new Main().setVisible(true));
    }

    private javax.swing.JButton cmdClear;
    private javax.swing.JPopupMenu jPopupMenu1;
    private data.JXMapViewerCustom jXMapViewer;
    private javax.swing.JMenuItem menuEnd;
    private javax.swing.JMenuItem menuStart;
}
