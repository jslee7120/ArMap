package firebasetest.dodream.com.armap;

import org.w3c.dom.Node;

public class RouteInfo {
    public  String pointLat;
    public  String pointLon;
    public  int turnType;
    public  int distance;



    public String getPointLat() {
        return pointLat;
    }

    public void setPointLat(String pointLat) {
        this.pointLat = pointLat;
    }

    public String getPointLon() {
        return pointLon;
    }

    public void setPointLon(String pointLon) {
        this.pointLon = pointLon;
    }

    public int getTurnType() {
        return turnType;
    }

    public void setTurnType(int turnType) {
        this.turnType = turnType;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
