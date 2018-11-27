package com.example.administrator.ruler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.administrator.ruler.custom.ScaleRulerView;
import com.example.administrator.ruler.custom.ScaleViewGroup;

public class MainActivity extends AppCompatActivity {

    private ScaleViewGroup scaleViewGroup;
    private TextView tv_scale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scaleViewGroup = (ScaleViewGroup) findViewById(R.id.scaleGroup);
        tv_scale = (TextView) findViewById(R.id.tv_scale);
        scaleViewGroup.setScrollCallback(new ScaleRulerView.ScrollCallback() {
            @Override
            public void setScale(int scale) {
                tv_scale.setText(scale+"");
            }
        });
    }
}
