package firebasetest.dodream.com.armap.model;

public class MapPoint {
    public String Name;
    public double latitude;
    public double longitude;

    public MapPoint(){
        super();
    }

    public MapPoint(String Name, double latitude, double longitude){
        this.Name = Name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
