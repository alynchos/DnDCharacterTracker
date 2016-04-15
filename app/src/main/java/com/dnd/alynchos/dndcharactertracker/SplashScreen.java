package com.dnd.alynchos.dndcharactertracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dnd.alynchos.dndcharactertracker.Debug.Logger;

/**
 * Created by Alex Lynchosky on 12/22/2014.
 */
public class SplashScreen extends Activity {

    /* Debugging variables */
    private static final String TAG    = SplashScreen.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.debug("onCreate called");
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        logger.debug("OnResume");

        // Create an Intent that will start the main activity.  Pass extras through to TabBar
        Intent mainIntent = new Intent(SplashScreen.this, DnDTabBarActivity.class);
        mainIntent.putExtras(getIntent());
        this.startActivity(mainIntent);
        this.finish();
    }
}
