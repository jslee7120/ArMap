package firebasetest.dodream.com.armap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.opengl.Matrix;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static android.hardware.SensorManager.getOrientation;
import static firebasetest.dodream.com.armap.ARActivity.tv_ReDis;

public class AROverlayView extends View {

    Context context;
    String turn;
    Bitmap tl, tr, t10, t8, t2, t4, cross;
    private float[] rotatedProjectionMatrix = new float[16];
    private Location currentLocation;
    Location tempLocation, location;
    private List<ARPoint> arPoints;
    double currentLat, currentLon;
    LocationManager locationManager;
    int i = 0;
    float declination;
    double bearing, routeBearing;
    boolean location_Status = true;
    Location start_Location;


    public AROverlayView(Context context) {
        super(context);

        this.context = context;

        Resources r = context.getResources();

        tl = BitmapFactory.decodeResource(r, R.drawable.l_turn);
        tr = BitmapFactory.decodeResource(r, R.drawable.r_turn);
        t10 = BitmapFactory.decodeResource(r, R.drawable.ten_turn);
        t8 = BitmapFactory.decodeResource(r, R.drawable.eight_turn);
        t2 = BitmapFactory.decodeResource(r, R.drawable.tow_turn);
        t4 = BitmapFactory.decodeResource(r, R.drawable.four_turn);
        cross = BitmapFactory.decodeResource(r, R.drawable.cross_turn);

        //Demo points
        arPoints = new ArrayList<ARPoint>() {{
           /* for(int i = 0; i<FindAddress.routeInfos.size(); i++){
                Log.d("AR경로_",String.valueOf(FindAddress.routeInfos.get(i).turnType)+FindAddress.routeInfos.get(i).pointLat+FindAddress.routeInfos.get(i).pointLon);
            }*/
            for (int i = 0; i < FindAddress.routeInfos.size(); i++) {
                add(new ARPoint(String.valueOf(FindAddress.routeInfos.get(i).turnType), Double.valueOf(FindAddress.routeInfos.get(i).pointLat),
                        Double.valueOf(FindAddress.routeInfos.get(i).pointLon), 0));
            }
          /*  add(new ARPoint(String.valueOf(FindAddress.routeInfos.get(0).turnType), Double.valueOf(FindAddress.routeInfos.get(0).pointLat),
                    Double.valueOf(FindAddress.routeInfos.get(0).pointLon), 0));*/
        }};
    }

    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;

        this.invalidate();
    }

    public void updateCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
        currentLat = this.currentLocation.getLatitude();
        currentLon = this.currentLocation.getLongitude();
        if(location_Status){
            start_Location = currentLocation;
            location_Status = false;
        }
        this.invalidate();
    }

    public void updateLocation(Location currentLocation) {
        this.location = currentLocation;

        Log.d("location_call", "true");
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float[] orientation = new float[3];
        getOrientation(rotatedProjectionMatrix, orientation);
        bearing = Math.toDegrees(orientation[0]) + declination;

/*
        bearingP1toP2(currentLocation.getLatitude(), currentLocation.getLongitude(),
                Double.parseDouble(FindAddress.routeInfos.get(i).getPointLat()), Double.parseDouble(FindAddress.routeInfos.get(i).getPointLon()
                ));
*/

        if (currentLocation == null) {
            return;
        }

        final int radius = 30;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);

        float[] currentLocationInECEF = LocationHelper.WSG84toECEF(start_Location);
        float[] pointInECEF = LocationHelper.WSG84toECEF(arPoints.get(i).getLocation());
        float[] pointInENU = LocationHelper.ECEFtoENU(start_Location, currentLocationInECEF, pointInECEF);

        Log.d("start_location",start_Location+"");
        Log.d("current_Location",currentLocation+"");

        float[] cameraCoordinateVector = new float[4];
        Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

        // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
        // if z > 0, the point will display on the opposite
        if (cameraCoordinateVector[2] < 0) {
           // float x = (0.5f + cameraCoordinateVector[0] / cameraCoordinateVector[3]) * canvas.getWidth();
            //float y = (0.5f - cameraCoordinateVector[1] / cameraCoordinateVector[3]) *canvas.getHeight();
            float x = canvas.getWidth()/2-200;
            float y = canvas.getHeight()/2-480;
            // canvas.drawCircle(x, y, radius, paint);
            switch (arPoints.get(i).getName()) {
                case "12":
                    turn = "   좌회전   ";
                    tl = Bitmap.createScaledBitmap(tl, 400, 400, true);
                    canvas.drawBitmap(tl, x, y, null);
                    break;
                case "13":
                    turn = "   우회전   ";
                    tr = Bitmap.createScaledBitmap(tr, 400, 400, true);
                    canvas.drawBitmap(tr, x, y, null);
                    break;
                case "16":
                    turn = "8시 방향 좌회전";
                    t8 = Bitmap.createScaledBitmap(t8, 400, 400, true);
                    canvas.drawBitmap(t8, x, y, null);
                    break;
                case "17":
                    turn = "10시 방향 좌회전";
                    t10 = Bitmap.createScaledBitmap(t10, 400, 400, true);
                    canvas.drawBitmap(t10, x, y, null);
                    break;
                case "18":
                    turn = "2시 방향 우회전";
                    t2 = Bitmap.createScaledBitmap(t2, 400, 400, true);
                    canvas.drawBitmap(t2, x, y, null);
                    break;
                case "19":
                    turn = "4시 방향 우회전";
                    t4 = Bitmap.createScaledBitmap(t4, 400, 400, true);
                    canvas.drawBitmap(t4, x, y, null);
                    break;
                case "211":
                case "212":
                case "213":
                case "214":
                case "215":
                case "216":
                case "217":
                    turn = "   횡단보도  ";
                    cross = Bitmap.createScaledBitmap(cross, 400, 400, true);
                    canvas.drawBitmap(cross, x, y, null);
                    break;
                case "201":
                    turn = "   목적지   ";
                    break;

            }
            Log.d("실행시간?", "22");


              /* if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                else {
                    try {
                        Thread.sleep(300);

                        String locationProvider = LocationManager.GPS_PROVIDER;
                        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                        tempLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        updateCurrentLocation(tempLocation);
                        Log.d("시바", currentLat+"    "+currentLon);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }*/

            distance(ARActivity.myGPS.getLatitude(), ARActivity.myGPS.getLongitude(),
                    Double.parseDouble(FindAddress.routeInfos.get(i).getPointLat()),
                    Double.parseDouble(FindAddress.routeInfos.get(i).getPointLon()));


            canvas.drawText(turn, x - (30 * arPoints.get(i).getName().length() / 2), y + 480, paint);


        }
    }

    public double distance(double lat1, double lon1, double lat2, double lon2) {

        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        Log.e("km: ", dist + "km");

        if (dist <= 0.003) {
            i++;
            location_Status = true;
            return dist;
        }
        dist = dist * 1000.0;


        int dis_m;

        dis_m = (int) dist;
        tv_ReDis.setText(String.valueOf(dis_m) + "m 앞");


       /* dist = dist * 1000.0;      // 단위  km 에서 m 로 변환
        Log.e("meter: ",dist+"meter");*/


        return dist;

    }

    private void beforeDraw() {

    }


    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }




    public short bearingP1toP2(double P1_latitude, double P1_longitude, double P2_latitude, double P2_longitude) {
        // 현재 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에 라디안 각도로 변환한다.
        double Cur_Lat_radian = P1_latitude * (3.141592 / 180);
        double Cur_Lon_radian = P1_longitude * (3.141592 / 180);


        // 목표 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에 라디안 각도로 변환한다.
        double Dest_Lat_radian = P2_latitude * (3.141592 / 180);
        double Dest_Lon_radian = P2_longitude * (3.141592 / 180);

        // radian distance
        double radian_distance = 0;
        radian_distance = Math.acos(Math.sin(Cur_Lat_radian) * Math.sin(Dest_Lat_radian) + Math.cos(Cur_Lat_radian) * Math.cos(Dest_Lat_radian) * Math.cos(Cur_Lon_radian - Dest_Lon_radian));

        // 목적지 이동 방향을 구한다.(현재 좌표에서 다음 좌표로 이동하기 위해서는 방향을 설정해야 한다. 라디안값이다.
        double radian_bearing = Math.acos((Math.sin(Dest_Lat_radian) - Math.sin(Cur_Lat_radian) * Math.cos(radian_distance)) / (Math.cos(Cur_Lat_radian) * Math.sin(radian_distance)));        // acos의 인수로 주어지는 x는 360분법의 각도가 아닌 radian(호도)값이다.

        double true_bearing = 0;
        if (Math.sin(Dest_Lon_radian - Cur_Lon_radian) < 0) {
            true_bearing = radian_bearing * (180 / 3.141592);
            true_bearing = 360 - true_bearing;
        } else {
            true_bearing = radian_bearing * (180 / 3.141592);
        }

        return (short) true_bearing;
    }


}
