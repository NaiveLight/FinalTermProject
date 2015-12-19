package com.example.user.finalterm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Sunpark on 2015-12-19.
 */
public class ListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //인텐트 및 Data
    Intent IntOfMain;
    Intent Int_Result;
    String time, type, body, lat, lng;

    //DB 관련
    SQLiteDatabase db;
    String dbName = "MemoList.db"; // name of Database;
    String tableName = "MemoTable"; // name of Table;
    int dbMode = Context.MODE_PRIVATE;
    ArrayList<String> BodyList;
    ArrayAdapter<String> baseAdapter;

    //버튼 관련
    Button Btback;
    Button Btresult;
    Button Btreset;

    //스피너 관련
    ArrayList<String> SpinnerList2;

    //리스트 뷰 관련
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        /*************************************************************************************************************
         * 인텐트로 전달받은 데이터 초기화
         *************************************************************************************************************/
        IntOfMain = getIntent();
        time = IntOfMain.getStringExtra("time");
        type = IntOfMain.getStringExtra("type");
        body = IntOfMain.getStringExtra("body");
        lat = IntOfMain.getStringExtra("lat");
        lng = IntOfMain.getStringExtra("lng");

        /**************************************************************************************************************
         DB 오픈 및 테이블 생성
         **************************************************************************************************************/
        db = openOrCreateDatabase(dbName, dbMode, null);
        createTable();
        BodyList = new ArrayList<String>();

        /**************************************************************************************************************
        리스트 뷰 관련
         **************************************************************************************************************/

        //리스트 뷰
        listView = (ListView) findViewById(R.id.listView);
        baseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, BodyList);
        listView.setAdapter(baseAdapter);
        listView.setOnItemClickListener(mItemClickListener);

        insertData(time, type, body, lat, lng);

        /*************************************************************************************************************
         스피너 관련
         *************************************************************************************************************/
        SpinnerList2 = new ArrayList<String>();
        SpinnerList2.add("전체");
        SpinnerList2.add("학업");
        SpinnerList2.add("취미");
        SpinnerList2.add("식사");
        SpinnerList2.add("여행");
        SpinnerList2.add("모임");
        SpinnerList2.add("휴식");
        SpinnerList2.add("사고");
        SpinnerList2.add("경조사");

        ArrayAdapter<String> spinner2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, SpinnerList2);
        Spinner sp2 = (Spinner) this.findViewById(R.id.spinner2);
        sp2.setAdapter(spinner2);
        sp2.setOnItemSelectedListener(this);

        /**************************************************************************************************************
         통계 조회 버튼 클릭 시 리절트액티비티 실행
         **************************************************************************************************************/

        Btresult = (Button) findViewById(R.id.bt_result);
        Int_Result = new Intent(this, ResultActivity.class);

        Btresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countAll();
                startActivity(Int_Result);
            }
        });

        /**************************************************************************************************************
        전체 삭제 버튼 클릭 시 테이블 삭제와 BodyList 비운 후 리스트뷰 갱신
         **************************************************************************************************************/
        Btreset = (Button) findViewById(R.id.bt_reset);
        Btreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ListActivity.this);
                alert.setTitle("전체 삭제");
                alert.setMessage("정말 삭제 하시겠습니까?");
                alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeTable();
                        createTable();
                        BodyList.clear();
                        selectAll();
                        baseAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        /**************************************************************************************************************
         돌아가기 버튼 클릭 시 액티비티 finish
         **************************************************************************************************************/

        Btback = (Button) findViewById(R.id.bt_backforlist);
        Btback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //리스트 OnItemClickListener 구현
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
            String selectedItem = (String)parent.getAdapter().getItem(position); //선택된 아이템을 String으로 가져오며
            final String SelectItem_id = selectedItem.substring(11, 12); //그 string의 11~12번째 값인 id 값을 받아온다.

            AlertDialog.Builder alert = new AlertDialog.Builder(ListActivity.this);
            alert.setTitle("메모 확인");
            alert.setMessage(selectedItem);
            alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeData(Integer.parseInt(SelectItem_id)); //해당 id 값의 데이타 삭제 후 리스트 갱신
                    BodyList.clear();
                    selectAll();
                    baseAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
            alert.show();
        }
    };

    //테이블 생성
    public void createTable() {
        try {
            String sql = "create table " + tableName + "(id integer primary key autoincrement, " + "time text not null, " +
                    "type text not null, " + "body text not null, " + "lat text not null, " + "lng text not null);";
            db.execSQL(sql);
        } catch (SQLiteException e) {
            Log.d("Lab sqlite", "error: " + e);
        }
    }

    //테이블 삭제
    public void removeTable() {
        String sql = "drop table " + tableName;
        db.execSQL(sql);
    }

    //데이터 저장
    public void insertData(String time, String type, String body, String latitude, String longitude) {
        String sql = "insert into " + tableName + " values(NULL, '" + time + "', '" + type + "', '" + body + "', '" + latitude + "', '" + longitude + "');";
        db.execSQL(sql);
    }

    //데이터 삭제
    public void removeData(int index) {
        String sql = "delete from " + tableName + " where id = " + index + ";";
        db.execSQL(sql);
    }

    //선택된 타입의 Data 읽기
    public void selectData(String type) {
        String sql = "select * from " + tableName + " where type = '" + type + "';";
        Cursor results = db.rawQuery(sql, null);
        results.moveToFirst();

        while (!results.isAfterLast()) {
            int id2 = results.getInt(0);
            String time2 = results.getString(1);
            String type2 = results.getString(2);
            String body2 = results.getString(3);
            String lat2 = results.getString(4);
            String lng2 = results.getString(5);

            String All2 = time2 + "\n" + id2 + "번째 메모\n" + type2 + "\n" + body2 + "\n" + "lat: " + lat2 + " lng: " + lng2;

            BodyList.add(All2);
            results.moveToNext();
        }
        results.close();
        listView.setAdapter(baseAdapter);
    }

    // 모든 Data 읽기
    public void selectAll() {
        String sql = "select * from " + tableName + ";";
        Cursor results = db.rawQuery(sql, null);
        results.moveToFirst();

        while (!results.isAfterLast()) {
            int id = results.getInt(0);
            String time = results.getString(1);
            String type = results.getString(2);
            String body = results.getString(3);
            String lat = results.getString(4);
            String lng = results.getString(5);
            //Toast.makeText(this, "index= " + id + " type=" + type + " " + body + " " + lat + " " + lng, Toast.LENGTH_LONG).show();
            //Log.d("lab_sqlite", "index= " + id + " type=" + type + body + lat + lng);

            String All = time + "\n" + id + "번째 메모\n" + type + "\n" + body + "\n" + "lat: " + lat + " lng: " + lng;

            BodyList.add(All);
            results.moveToNext();
        }
        results.close();
        listView.setAdapter(baseAdapter);
    }

    //데이터 타입 종류별로 cnt 증가
    public void countAll() {
        String sql = "select * from " + tableName + ";";
        Cursor results = db.rawQuery(sql, null);
        results.moveToFirst();

        //통계 관련 Data
        int cnt1 = 0, cnt2 = 0, cnt3 = 0, cnt4 = 0, cnt5 = 0, cnt6 = 0, cnt7 = 0, cnt8 = 0;

        while (!results.isAfterLast()) {
            String type = results.getString(2);
            switch (type){
                case "학업" :
                    cnt1++;
                    break;
                case "취미" :
                    cnt2++;
                    break;
                case "식사" :
                    cnt3++;
                    break;
                case "여행" :
                    cnt4++;
                    break;
                case "모임" :
                    cnt5++;
                    break;
                case "휴식" :
                    cnt6++;
                    break;
                case "사고" :
                    cnt7++;
                    break;
                case "경조사" :
                    cnt8++;
                    break;
            }
            results.moveToNext();
        }
        results.close();

        //여기서 putExtra를 하여 Data 전체 삭제 후 에도 바로 적용되게 하였다.
        Int_Result.putExtra("학업", String.valueOf(cnt1));
        Int_Result.putExtra("취미", String.valueOf(cnt2));
        Int_Result.putExtra("식사", String.valueOf(cnt3));
        Int_Result.putExtra("여행", String.valueOf(cnt4));
        Int_Result.putExtra("모임", String.valueOf(cnt5));
        Int_Result.putExtra("휴식", String.valueOf(cnt6));
        Int_Result.putExtra("사고", String.valueOf(cnt7));
        Int_Result.putExtra("경조사", String.valueOf(cnt8));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = (String)parent.getAdapter().getItem(position);

        if(selectedItem == "전체"){
            selectAll();
            baseAdapter.notifyDataSetChanged();
        }else {
            Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_SHORT).show();
            BodyList.clear();
            selectData(selectedItem);
            baseAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
