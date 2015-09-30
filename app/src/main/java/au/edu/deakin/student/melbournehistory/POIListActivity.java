package au.edu.deakin.student.melbournehistory;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.Context;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

////////////////////////////////////////
//
//  Handle the Listview activities
//
////////////////////////////////////////

public class POIListActivity extends ListActivity {

    //class wide variables
    private static final String POI_LISTFILE = "poi_rootlist";
    private SlidingUpPanelLayout SlidingLayout;
    private ArrayList<String> POI_Text = new ArrayList<String>();
    private ArrayList<String> POI_Desc = new ArrayList<String>();
    private ArrayList<String> POI_Icon = new ArrayList<String>();
    private ArrayList<String> POI_Html = new ArrayList<String>();
    private IconicAdapter myAdapter;
    private SlidingUpPanelLayout mLayout;
    int screenWidth;

    ////////////////////////////////////////
    //
    //  General methods for activity
    //
    ////////////////////////////////////////

    //Activity OnCreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set content view
        setContentView(R.layout.activity_poilist);

        //slidepanel related
        SlidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        SlidingLayout.setPanelSlideListener(onSlideListener());
        SlidingLayout.setTouchEnabled(false);

        //List Related
        myAdapter = new IconicAdapter();
        //setListAdapter(myAdapter);
        setListAdapter(myAdapter);
        ListView lv = getListView();
        //List Item Click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Set POI Title Bar from Item
                String Title = (String) parent.getItemAtPosition(position);
                TextView POItextView = (TextView) findViewById(R.id.POITitleTxt);
                POItextView.setText(Title);

                //Get HTML filename from tag
                TextView desc = (TextView) view.findViewById(R.id.BottomText);
                String TheHTML = (String) desc.getTag();

                //Slide up panel and insert HTML file
                onPOIExpButtonClick(TheHTML);
            }
        });

        //Grab the back button and handle it as we expect
        TextView ListPOI_BackButton = (TextView)findViewById(R.id.LbuttonBack);
        ListPOI_BackButton.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPOIExpButtonClick("");
            }
        });

        //Get device dimensions
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;
        //load and display POIs
        prepare_POI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_poilist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_credits) {
            showInfo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //actionbar activity
    public void showInfo() {
        startActivity(new Intent(this, CreditActivity.class));
    }





    ////////////////////////////////////////
    //
    //  POI content management code
    //
    ////////////////////////////////////////

    // Load XML POI file and parse for POI's and related parameters
    private void prepare_POI()
    {
        try
        {
            InputStream inputstream;
            XmlPullParserFactory xmlFactoryObject;
            int resID = getResources().getIdentifier(POI_LISTFILE, "raw", this.getPackageName());
            inputstream = this.getResources().openRawResource(resID);
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myparser = xmlFactoryObject.newPullParser();
            myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            myparser.setInput(inputstream, null);

            try
            {
                String text = null;
                String val_DisplayName = "";
                String val_Icon = "";
                String val_Desc = "";
                String val_Html = "";
                boolean XMLelementMarker = false;
                String name;
                int event = myparser.getEventType();
                while (event != XmlPullParser.END_DOCUMENT)  //while not EOF
                {
                    name = myparser.getName();
                    switch (event)
                    {
                        case XmlPullParser.START_TAG:
                            break;
                        case XmlPullParser.TEXT:
                            text = myparser.getText();
                            break;
                        case XmlPullParser.END_TAG:
                            if (name.equals("DisplayName")) {
                                val_DisplayName = text;
                            }
                            else if (name.equals("ListLogo")) {
                                val_Icon = text;
                            }
                            else if (name.equals("ListDesc")) {
                                val_Desc = text;
                            }
                            else if (name.equals("Html")) {
                                val_Html = text;
                            }

                            break;
                    }

                    if (name != null) {
                        if (name.equals("POI")) {
                            if (!XMLelementMarker) {
                                XMLelementMarker = true;
                            } else {
                                XMLelementMarker = false;
                                //Add marker to list
                                if (!"".equals(val_DisplayName)) {
                                    POI_Text.add(val_DisplayName);
                                    POI_Desc.add(val_Desc);
                                    POI_Icon.add(val_Icon);
                                    POI_Html.add(val_Html);
                                }
                            }
                        }
                    }

                    event = myparser.next();
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    //Adapter to draw custom list items, whcih for us is an icon, POI name and brief description
    class IconicAdapter extends ArrayAdapter<String> {

        IconicAdapter() {
            super(POIListActivity.this, R.layout.row, R.id.TopText, POI_Text);
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent)
        {
            View row=super.getView(position, convertView, parent);

            //Set Icon
            ImageView icon=(ImageView)row.findViewById(R.id.icon);
            String imageName = POI_Icon.get(position);
            int resID = getResources().getIdentifier(imageName, "raw", getPackageName());
            Drawable d = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                d = getResources().getDrawable(resID, getApplicationContext().getTheme());
            } else {
                d = getResources().getDrawable(resID);
            }
            icon.setImageDrawable(d);

            //Set Description
            TextView desc=(TextView)row.findViewById(R.id.BottomText);
            desc.setText(POI_Desc.get(position));

            //Store HTML file name
            desc.setTag(POI_Html.get(position));
            //String TheHTML = desc.getTag();

            return(row);
        }
    }

    //Listener for Sliding Panel events
    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelCollapsed(View view)
            { } //available if needed

            @Override
            public void onPanelExpanded(View view)
            { } //available if needed

            @Override
            public void onPanelSlide(View view, float v)
            { }  //available if needed

            @Override
            public void onPanelAnchored(View view)
            { }  //available if needed

            @Override
            public void onPanelHidden(View view)
            {
                //show actionbar
                getActionBar().show();
            }
        };
    }

    //Handle events from when a user clicks a list item, to load the relevant content and slide the panel up or down
    private void onPOIExpButtonClick(String HTMLfileName)
    {
        if (SlidingLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.HIDDEN))
        {//Show POI panel

            //Populate the panel with the relevant HTML
            //Get HTML test from file
            if (!"".equals(HTMLfileName)) {
                String htmlFName = HTMLfileName;
                int resID = getResources().getIdentifier(htmlFName, "raw", getPackageName());
                String htmlText = readRawTextFile(getApplicationContext(), resID);
                //Load HTML into TextView
                TextView htmlTextView = (TextView) findViewById(R.id.main_text);
                htmlTextView.setText(Html.fromHtml(htmlText, new ImageGetter(), null));
            }

            //Scroll view to top
            ScrollView scrollView = (ScrollView)findViewById(R.id.main_scroll);
            scrollView.smoothScrollTo(0, 0);

            //hide actionbar
            getActionBar().hide();

            // Delay panel slide for 100ms to wait for content to load
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    //slide up panel
                    SlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }, 100);

        }
        else if (SlidingLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED))
        {//close POI panel

            //slide down panel
            SlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        }
    }

    //Grab the back button and handle it as we expect
    @Override
    public void onBackPressed() {
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED))
        {
            //slide down panel
            SlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        } else {
            super.onBackPressed();
        }
    }

    //Load HTML file for POI into a TextView
    public static String readRawTextFile(Context ctx, int resId)
    {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while (( line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }

    //Find HTML image by name and returns a resized-to-hardware drawable object - inserts images into POI display
    private class ImageGetter implements Html.ImageGetter {

        public Drawable getDrawable(String source) {
            //convert image name to resource id
            String imageName = source.substring(0, source.indexOf('.'));
            int resID = getResources().getIdentifier(imageName, "raw", getPackageName());

            Drawable d = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                d = getResources().getDrawable(resID, getApplicationContext().getTheme());
            } else {
                d = getResources().getDrawable(resID);
            }
            float ratio = (float) screenWidth / (float) d.getIntrinsicWidth();

            d.setBounds(0, 0, (int) (d.getIntrinsicWidth() * ratio), (int) (d.getIntrinsicHeight() * ratio));
            ScaleDrawable sd = new ScaleDrawable(d, 0, 1.0f, 1.0f);
            sd.setLevel(10000);

            return sd.getDrawable();
        }

    }

}
