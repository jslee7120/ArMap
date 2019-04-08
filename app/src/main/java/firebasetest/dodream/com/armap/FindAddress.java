package firebasetest.dodream.com.armap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findaddress);

        btFindAddr2 = (Button)findViewById(R.id.btFindAddr2);
        etFindAddr2 = (EditText)findViewById(R.id.etFindAddr2);
        listView = (ListView)findViewById(R.id.lvFindAddr);

        vo = new ArrayList<FindAddressInfo>();

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
                                tMapPointEnd = vo.get(position).gettMapPoint();
                                Log.d("출발 경로",String.valueOf(tMapPointStart));
                                Log.d("도착 경로",String.valueOf(tMapPointEnd));
                                getFindPath();
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
                arrayList1.clear();
                vo.clear();
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
                            Log.d("debug", nodeListPlacemarkItem.item(j).getTextContent().trim() );
                        }
                        if( nodeListPlacemarkItem.item(j).getNodeName().equals("Point") ) {
                            Log.d("debug2", nodeListPlacemarkItem.item(j).getTextContent().trim() );
                        }
                    }
                }
            }
        });
    }


}
