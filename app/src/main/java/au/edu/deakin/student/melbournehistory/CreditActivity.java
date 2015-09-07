package au.edu.deakin.student.melbournehistory;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class CreditActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        int resID = getResources().getIdentifier("credits", "raw", getPackageName());
        String creditText = MapsActivity.readRawTextFile(getApplicationContext(), resID);
        TextView creditTextView = (TextView)findViewById(R.id.CreditView);
        creditTextView.setText(creditText);
    }
}
