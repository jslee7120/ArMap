package firebasetest.dodream.com.armap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import firebasetest.dodream.com.armap.model.FindAddressInfo;
import firebasetest.dodream.com.armap.model.Item;

public class FindAddress extends AppCompatActivity {
    Button btFindAddr2;
    EditText etFindAddr2;
    ListView listView;
    ArrayList<Item> arrayList1;
    MyAdapter adapter1;
    List<FindAddressInfo> vo;
    TMapData tMapData;
    Handler handler;
    Message msg;

    Double selLat,startLat;
    Double selLon, startLon;
    TMapPoint tMapPointEnd;
    TMapPoint tMapPointStart;
    RouteInfo routeInfo;
    Boolean start_satuts = true;
    List<Integer> t_distance = new ArrayList<Integer>();
    List<Integer> t_turn = new ArrayList<Integer>();
    List<String> t_point_lat = new ArrayList<String>();
    List<String> t_point_lon = new ArrayList<String>();
    public static TMapPoint start,end;
    public static ArrayList<RouteInfo> routeInfos;
    int tu,di,po = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findaddress);


        btFindAddr2 = (Button)findViewById(R.id.btFindAddr2);
        etFindAddr2 = (EditText)findViewById(R.id.etFindAddr2);
        listView = (ListView)findViewById(R.id.lvFindAddr);

        vo = new ArrayList<FindAddressInfo>();

        routeInfos = new ArrayList<RouteInfo>();

        arrayList1 = new ArrayList<Item>();

        Intent intent = getIntent();

        etFindAddr2.setText(intent.getExtras().getString("findAddr"));
        startLat = Double.parseDouble(intent.getExtras().getString("startLat"));
        startLon = Double.parseDouble(intent.getExtras().getString("startLon"));
        tMapPointStart = new TMapPoint(startLat,startLon);

        Log.d("시작 위치",String.valueOf(startLat));
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        tMapData = new TMapData();

        handler = new Handler(){ //리스트뷰 IU를 변경해주는 핸들러
            public void handleMessage(Message msg){
                adapter1 = new MyAdapter(FindAddress.this, arrayList1 );
                listView.setAdapter(adapter1);
            }
        };

        adapter1 = new MyAdapter(FindAddress.this, arrayList1 );
        listView.setAdapter(adapter1);
        getAddrInfo();
        btFindAddr2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddrInfo();
            }


        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d("선택한 목적지",vo.get(position).getName());
                builder.setTitle("알림");
                builder.setMessage(vo.get(position).getName() + "로 이동하시겠습니까?");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent resultIntent = new Intent();
                                setResult(3000,resultIntent);
                                tMapPointEnd = vo.get(position).gettMapPoint();
                                Log.d("출발 경로",String.valueOf(tMapPointStart));
                                Log.d("도착 경로",String.valueOf(tMapPointEnd));
                                resultIntent.putExtra("destination_Name",vo.get(position).getName());
                                resultIntent.putExtra( "destination",String.valueOf(tMapPointEnd));
                                getFindPath();
                                finish();
                            }
                        });
                builder.show();
            }
        });

    }

    public void getAddrInfo(){
        tMapData.findAllPOI(etFindAddr2.getText().toString(), new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                for(int i = 0;i < arrayList.size(); i++){
                    TMapPOIItem tMapPOIItem = arrayList.get(i);
                    FindAddressInfo item = new FindAddressInfo();
                    item.setName(tMapPOIItem.getPOIName());
                    item.setAddress(tMapPOIItem.getPOIAddress().replace("null",""));
                    item.settMapPoint(tMapPOIItem.getPOIPoint());
                    vo.add(item);

                    arrayList1.add(new Item(tMapPOIItem.getPOIName(),tMapPOIItem.getPOIAddress().replace("null","")));

                    Log.d("주소로 찾기","POI Name: "+ tMapPOIItem.getPOIName() + ", " + "Address: " +
                            tMapPOIItem.getPOIAddress().replace("null","") + ", " + "PointLat: " +
                            tMapPOIItem.getPOIPoint().mKatecLat + ", " + "PointLon: " +
                            tMapPOIItem.getPOIPoint().mKatecLon);

                }
                msg = handler.obtainMessage();
                handler.sendMessage(msg); //리스트뷰 변경하기 위해 핸들러 이용


            }
        });
    }

    public void getFindPath(){
        tMapData.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMapPointStart, tMapPointEnd, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document document) {
                Element root = document.getDocumentElement();
                Log.d("경로?",String.valueOf(root));
                NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");
                for( int i=0; i<nodeListPlacemark.getLength(); i++ ) {
                    NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                    for( int j=0; j<nodeListPlacemarkItem.getLength(); j++ ) {
                        if( nodeListPlacemarkItem.item(j).getNodeName().equals("description") ) {
                            Log.d("debug1_경로", nodeListPlacemarkItem.item(j).getTextContent().trim() );
                            //Toast.makeText(getApplicationContext(), "경로 : " + nodeListPlacemarkItem.item(j).getTextContent().trim(), Toast.LENGTH_SHORT).show();
                        }
                        if( nodeListPlacemarkItem.item(j).getNodeName().equals("tmap:distance") ) {
                            if(di == tu) {
                                Log.d("Distance_AR_Load", nodeListPlacemarkItem.item(j).getTextContent().trim());
                               //routeInfo.setDistance(Integer.parseInt(nodeListPlacemarkItem.item(j).getTextContent().trim()));
                                t_distance.add(Integer.parseInt(nodeListPlacemarkItem.item(j).getTextContent().trim()));
                           /* routeInfos.add(routeInfo);
                            Log.d("경로_배열",routeInfos.get(g).pointLat + "," + routeInfos.get(g).pointLon + " 턴타입" + routeInfos.get(g).turnType + " 미터"
                                    + routeInfos.get(g).distance);
                            g++;*/
                                di++;
                            }
                        }
                        if( nodeListPlacemarkItem.item(j).getNodeName().equals("tmap:turnType") ) {

                            if(Integer.parseInt(nodeListPlacemarkItem.item(j).getTextContent().trim()) != 200) {
                                Log.d("Turn_Type_AR_Load", nodeListPlacemarkItem.item(j).getTextContent().trim());
                                /*routeInfo.setTurnType(Integer.parseInt(nodeListPlacemarkItem.item(j).getTextContent().trim()));*/
                                t_turn.add(Integer.parseInt(nodeListPlacemarkItem.item(j).getTextContent().trim()));
                                start_satuts = false;
                                tu++;
                            }
                        }
                        if( nodeListPlacemarkItem.item(j).getNodeName().equals("Point") ) {
                            Log.d("Point_AR_Load", nodeListPlacemarkItem.item(j).getTextContent().trim() );
                            if(!start_satuts) {
                                String temp[] = nodeListPlacemarkItem.item(j).getTextContent().trim().split(",");
                                t_point_lat.add(temp[1]);
                                t_point_lon.add(temp[0]);
                              /*  routeInfo.setPointLat(temp[0]);
                                routeInfo.setPointLon(temp[1]);*/
                                po++;

                            }
                        }

                    }
                }
                Log.d("경로_배열2",String.valueOf(t_distance.size()));
                for(int i=0; i<t_distance.size(); i++){
                    routeInfo = new RouteInfo();
                    routeInfo.setDistance(t_distance.get((i)));
                    routeInfo.setTurnType(t_turn.get(i));
                    routeInfo.setPointLat(t_point_lat.get(i));
                    routeInfo.setPointLon(t_point_lon.get(i));
                    Log.d("경로_배열3",String.valueOf(t_distance.get((i))) + String.valueOf(t_turn.get(i)));
                    routeInfos.add(routeInfo);
                    Log.d("경로_배열2",String.valueOf(i));
                }
                for(int g= 0; g<routeInfos.size(); g++){
                    Log.d("경로_배열",routeInfos.get(g).pointLat + "," + routeInfos.get(g).pointLon + " 턴타입" + routeInfos.get(g).turnType + " 미터"
                            + routeInfos.get(g).distance);
                }

                Intent intent_1 = new Intent(FindAddress.this, ARActivity.class);
                start = tMapPointStart;
                end = tMapPointEnd;
                startActivity(intent_1);
            }
        });
    }


}
