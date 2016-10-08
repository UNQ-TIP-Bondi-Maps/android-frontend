package tpi.unq.bondimaps.model;

import java.io.Serializable;

public class Place implements Serializable {

    private long id;
    private String name;
    private String address;
    private CustomLatLng coordinate;

    public Place() {}

    public Place(String name, String address, CustomLatLng coordinate) {
        this.name = name;
        this.address = address;
        this.coordinate = coordinate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CustomLatLng getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(CustomLatLng coordinate) {
        this.coordinate = coordinate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
