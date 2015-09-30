package au.edu.deakin.student.melbournehistory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.widget.Button;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Hook View by Map button to launch Map activity
        Button btnMap = (Button) findViewById(R.id.wbuttonMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMap(v);
            }
        });

        //Hook View by List button to launch List activity
        Button btnList = (Button) findViewById(R.id.wbuttonList);
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchList(v);
            }
        });

    }

    public void launchMap(View view) {
        startActivity(new Intent(this, MapsActivity.class));
    }

    public void launchList(View view) {
        startActivity(new Intent(this, POIListActivity.class));
    }


}
