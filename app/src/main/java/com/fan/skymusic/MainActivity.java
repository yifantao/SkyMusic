package com.fan.skymusic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView lvDisp;
    private List<String> data;
    private PlayLayout mPlayLayout;

    private <T extends View>  T getView(int resId){
        return (T) findViewById(resId);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvDisp=getView(R.id.lvDisp);
        mPlayLayout=getView(R.id.playLayout);
        mPlayLayout.setSplitBackgroud(R.drawable.img7);//splitLayout为上下分割的控件
        mPlayLayout.setControlBackground(R.drawable.img8);//controlPanel是中间旋转的圆盘
        mPlayLayout.setSplitScale(0.2f);
        mPlayLayout.setRadias(200);
        data=new ArrayList<>();
        for(int i=0;i<25;i++){
            data.add("item"+i);
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,data);
        lvDisp.setAdapter(adapter);
        lvDisp.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mPlayLayout.open();
    }
}
