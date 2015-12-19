package com.example.user.finalterm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sunpark on 2015-12-19.
 */
public class ResultActivity extends AppCompatActivity {

    //버튼 관련
    Button Btback;

    //텍스트 관련
    TextView textView;

    //인텐트 관련
    Intent Int_Result;

    //각 타입에 대한 count

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        /*************************************************************************************
         인텐트로 전달 받은 데이타 초기화
         *************************************************************************************/
        Int_Result = getIntent();

        String count1 = Int_Result.getStringExtra("학업");
        String count2 = Int_Result.getStringExtra("취미");
        String count3 = Int_Result.getStringExtra("식사");
        String count4 = Int_Result.getStringExtra("여행");
        String count5 = Int_Result.getStringExtra("모임");
        String count6 = Int_Result.getStringExtra("휴식");
        String count7 = Int_Result.getStringExtra("사고");
        String count8 = Int_Result.getStringExtra("경조사");

        /*************************************************************************************
         텍스트뷰에 받아온 통계 표기
         *************************************************************************************/
        textView = (TextView) findViewById(R.id.text_result);
        textView.setText("현재까지 메모 통계\n" +
                "학업: 총 " + count1 + " 건" + "\n" +
                "취미: 총 " + count2 + " 건" + "\n" +
                "식사: 총 " + count3 + " 건" + "\n" +
                "여행: 총 " + count4 + " 건" + "\n" +
                "모임: 총 " + count5 + " 건" + "\n" +
                "휴식: 총 " + count6 + " 건" + "\n" +
                "사고: 총 " + count7 + " 건" + "\n" +
                "경조사: 총 " + count8 + " 건" + "\n" );

        /*************************************************************************************
         돌아가기 버튼 클릭 시 액티비티 finish
         *************************************************************************************/

        Btback = (Button) findViewById(R.id.bt_backforresult);
        Btback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
