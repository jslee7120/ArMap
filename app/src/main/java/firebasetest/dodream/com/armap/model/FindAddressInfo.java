package firebasetest.dodream.com.armap.model;

import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

public class FindAddressInfo {
        public String name;
        public String Address;
        public Double Pointlat;
        public Double Pointlon;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String address) {
            Address = address;
        }

    public Double getPointlat() {
        return Pointlat;
    }

    public void setPointlat(Double pointlat) {
        Pointlat = pointlat;
    }

    public Double getPointlon() {
        return Pointlon;
    }

    public void setPointlon(Double pointlon) {
        Pointlon = pointlon;
    }
}
