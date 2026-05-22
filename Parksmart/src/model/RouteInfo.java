package model;

public class RouteInfo {
    private String sourceZone;
    private String destinationZone;
    private int distance;
    private String pathType;

    public RouteInfo(String sourceZone, String destinationZone, int distance, String pathType) {
        this.sourceZone = sourceZone;
        this.destinationZone = destinationZone;
        this.distance = distance;
        this.pathType = pathType;
    }

    public String getSourceZone() {
        return sourceZone;
    }

    public String getDestinationZone() {
        return destinationZone;
    }

    public int getDistance() {
        return distance;
    }

    public String getPathType() {
        return pathType;
    }
}