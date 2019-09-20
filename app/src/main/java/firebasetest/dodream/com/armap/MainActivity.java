package firebasetest.dodream.com.armap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

import firebasetest.dodream.com.armap.model.MapPoint;

import static android.util.Log.d;
import static com.skt.Tmap.TMapView.TILETYPE_HDTILE;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private Context mContext = null;
    private boolean m_bTrackingMode = true;

    private TMapGpsManager tmapgps = null;
    private TMapView tmapview = null;
    private static String mApiKey = "afaa7c03-a2be-4eaf-9747-aa8fe961424f";
    private static int mMarkerID;

    private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<TMapPoint>();
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();
    private ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>();

    private String address;
    private Double lat = null;
    private Double lon = null;

    private String destination,des_lat, des_lon;
    private String destination_name=null;

    Button btFindAddr;
    EditText etFindAddr;

    @Override
    public void onLocationChange(Location location) {
        if(m_bTrackingMode){
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // TMapPolyLine polyline = tmapview.getPolyLineFromID(TestID1);
      //  TMapCircle circle = tmapview.getCircleFromID(TestID2);
       // TMapPolyLine tpolyline = new TMapPolyLine();
       // tmapview.addTMapPolyLine("TestID", tpolyline);

        btFindAddr = (Button)findViewById(R.id.btFindAddr);
        etFindAddr = (EditText) findViewById(R.id.etFindAddr);

        mContext = this;

        TMapData tMapData = new TMapData();
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.map_view);

        tmapview = new TMapView(this);

        linearLayout.addView(tmapview);
        tmapview.setSKTMapApiKey(mApiKey);

        //현재 보는 방향
        tmapview.setIconVisibility(true);
        tmapview.setCompassMode(true);

        //줌 레벨
        tmapview.setZoomLevel(30);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);

        tmapgps = new TMapGpsManager(MainActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
            return;
        }


        tmapgps.OpenGps();

        //화면의 중심을 현재위치로 지정
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);

        //말풍선 버튼 클릭시 행위
        tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                Toast.makeText(MainActivity.this,"클릭",Toast.LENGTH_SHORT).show();
            }
        });

        TMapPoint tpoint = tmapview.getLocationPoint();
        final double Latitude = tpoint.getLatitude();
        final double Longitude = tpoint.getLongitude();

        btFindAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FindAddress.class);
                intent.putExtra("findAddr",etFindAddr.getText().toString());
                intent.putExtra("startLat", String.valueOf(tmapgps.getLocation().getLatitude()));
                intent.putExtra("startLon",String.valueOf(tmapgps.getLocation().getLongitude()));
                startActivityForResult(intent,3000);
            }
        });

     /*   tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                tmapview.addTMapPath(polyLine);
            }
        });*/


    }

    public void addPoint() { //여기에 핀을 꼽을 포인트들을 배열에 add해주세요!
        // 강남 //
        Log.d("목적지","True");
        m_mapPoint.clear();
        m_mapPoint.add(new MapPoint("목적지",Double.valueOf(des_lat), Double.valueOf(des_lon)));
        showMarkerPoint();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    public void showMarkerPoint() {// 마커 찍는거 빨간색 포인트.
        for (int i = 0; i < m_mapPoint.size(); i++) {
            TMapPoint point = new TMapPoint(m_mapPoint.get(i).getLatitude(),
                    m_mapPoint.get(i).getLongitude());
            TMapMarkerItem item1 = new TMapMarkerItem();
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.poi_dot);

            //poi_dot은 지도에 꼽을 빨간 핀 이미지입니다

            item1.setTMapPoint(point);
            item1.setName(m_mapPoint.get(i).getName());
            item1.setVisible(item1.VISIBLE);

            item1.setIcon(bitmap);

            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.poi_dot);

            // 풍선뷰 안의 항목에 글을 지정합니다.
            item1.setCalloutTitle(m_mapPoint.get(i).getName());
            item1.setCalloutSubTitle(destination_name);
            item1.setCanShowCallout(true);
            item1.setAutoCalloutVisible(true);

            Bitmap bitmap_i = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);

            item1.setCalloutRightButtonImage(bitmap_i);

            String strID = String.format("pmarker%d", mMarkerID++);

            tmapview.removeAllMarkerItem();
            tmapview.addMarkerItem(strID, item1);
            mArrayMarkerID.add(strID);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == 3000){
                destination =  data.getStringExtra("destination");
                destination_name = data.getStringExtra("destination_Name");

                des_lat = destination.split(" ")[1];
                des_lon = destination.split(" ")[3];
                Log.d("testtt",des_lat+ "     "+ des_lon);
                addPoint();
                tmapview.setTileType(TILETYPE_HDTILE);
            }
    }

}
