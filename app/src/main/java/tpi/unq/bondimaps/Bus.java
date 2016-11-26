package tpi.unq.bondimaps;

public class Bus{

    private double lat;
    private double lng;
    private String directionOfTravel;
    private String routeWay;
    private String routeBack;
    private String timeToDestiny;

    public String getTimeToDestiny() { return timeToDestiny; }

    public void setTimeToDestiny(String timeToDestiny) { this.timeToDestiny = timeToDestiny; }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getDirectionOfTravel() {
        return directionOfTravel;
    }

    public void setDirectionOfTravel(String directionOfTravel) {
        this.directionOfTravel = directionOfTravel;
    }


    public String getRouteWay() {
        return routeWay;
    }

    public void setRouteWay(String routeWay) {
        this.routeWay = routeWay;
    }

    public String getRouteBack() {
        return routeBack;
    }

    public void setRouteBack(String routeBack) {
        this.routeBack = routeBack;
    }
}