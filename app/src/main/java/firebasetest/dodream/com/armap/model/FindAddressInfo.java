package firebasetest.dodream.com.armap.model;

import android.widget.ScrollView;

import com.skt.Tmap.TMapPoint;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

public class FindAddressInfo {
        public String name;
        public String Address;
        public TMapPoint tMapPoint;

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

    public TMapPoint gettMapPoint() {
        return tMapPoint;
    }

    public void settMapPoint(TMapPoint tMapPoint) {
        this.tMapPoint = tMapPoint;
    }
}
