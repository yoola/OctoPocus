package com.octopocus.octopocus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView objectText;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        objectText = (TextView) findViewById(R.id.object_name);
        text = (TextView) findViewById(R.id.textView);
        //text.
    }



    public void writeDollar(Dollar dollar){
        objectText.setText("Object: " + dollar.result.Name + " Score: " + dollar.result.Score);

    }

    public void excecuteCommand(String name) {
        if (name.equals("triangle")) {
            // instructions
        }else if (name.equals("check")) {
            // ...
        }
    }

}
