package com.example.user.finalterm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Sunpark on 2015-12-19.
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //spinner 관련
    ArrayList<String> SpinnerList;

    //맵 관련
    private GoogleMap map;
    static final LatLng SEOUL = new LatLng(37.56, 126.97);

    //텍트스 관련
    EditText editText;
    TextView txt_lat;
    TextView txt_lng;

    //버튼 관련
    Button mBtlist;

    //인텐트 관련
    Intent Int_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**************************************************************************************************************
        현재 위치 프래그먼트에 표시
         **************************************************************************************************************/
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        //좌표 표시할 텍스트 뷰
        txt_lat = (TextView) findViewById(R.id.text_latitute);
        txt_lng = (TextView) findViewById(R.id.text_longtitude);

        //맵프래그먼트
        map = mapFragment.getMap();

        //퍼미션 체크
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));//초기 위치 상관 없음.

        //현재 위치 표시
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {

            @Override
            public void gotLocation(Location location) {
                String msg = "lon: " + location.getLongitude() + " -- lat: " + location.getLatitude();
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                drawMarker(location);
                //텍스트 뷰에 좌표 표시를 위해 String 변수에 담음
                String lat = String.valueOf(location.getLatitude());
                String lng = String.valueOf(location.getLongitude());
                txt_lat.setText(lat);
                txt_lng.setText(lng);
            }
        };

        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(getApplicationContext(), locationResult);


        /**************************************************************************************************************
        Spinner 초기화 및 구현
         **************************************************************************************************************/
        SpinnerList = new ArrayList<String>();
        SpinnerList.add("학업");
        SpinnerList.add("취미");
        SpinnerList.add("식사");
        SpinnerList.add("여행");
        SpinnerList.add("모임");
        SpinnerList.add("휴식");
        SpinnerList.add("사고");
        SpinnerList.add("경조사");

        ArrayAdapter<String> spinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, SpinnerList);
        final Spinner sp = (Spinner) this.findViewById(R.id.spinner);
        sp.setAdapter(spinner);
        sp.setOnItemSelectedListener(this); //선택된 아이템을 띄워준다.

        /**************************************************************************************************************
        리스트 보기 버튼 클릭 시 리스트액티비티로 넘어가면서 데이터 전달해 줌
         **************************************************************************************************************/
        mBtlist = (Button) findViewById(R.id.bt_list);
        Int_List = new Intent(this, ListActivity.class);
        editText = (EditText) findViewById(R.id.editText);

        mBtlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //메모가 저장되는 시점을 년,월,일 까지 표시하기위해
                Calendar calendar = Calendar.getInstance();
                java.util.Date date = calendar.getTime();

                String time = (new SimpleDateFormat("yyyy.MM.dd").format(date));
                String type = sp.getSelectedItem().toString();
                String body = editText.getText().toString();
                String lat = txt_lat.getText().toString();
                String lng = txt_lng.getText().toString();

                if (!(body == null)) {
                    Int_List.putExtra("time", time);
                    Int_List.putExtra("type", type);
                    Int_List.putExtra("body", body);
                    Int_List.putExtra("lat", lat);
                    Int_List.putExtra("lng", lng);

                    editText.setText(null);
                    startActivity(Int_List);
                }
                else {
                    Toast.makeText(getApplicationContext(), "빈 내용은 저장할 수 없습니다.", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    //위치정보 받아와서 현재 위치에 마커 그리기
    private void drawMarker(Location location) {
        map.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        //currentPosition 위치로 카메라 중심을 옮기고 화면 줌을 조정한다. 줌범위는 2~21, 숫자클수록 확대
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 17));
        map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
        //마커 추가
        map.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat:" + location.getLatitude() + "Lng:" + location.getLongitude())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("현재위치"));
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}


