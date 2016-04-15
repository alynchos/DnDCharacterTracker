package com.dnd.alynchos.dndcharactertracker;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.alynchos.dndcharactertracker.Character.CharacterManager;
import com.dnd.alynchos.dndcharactertracker.Character.CharacterSheetActivity;
import com.dnd.alynchos.dndcharactertracker.Character.CombatActivity;
import com.dnd.alynchos.dndcharactertracker.Character.NotesActivity;
import com.dnd.alynchos.dndcharactertracker.Debug.Logger;
import com.dnd.alynchos.dndcharactertracker.Items.InventoryActivity;
import com.dnd.alynchos.dndcharactertracker.SaveData.FeedReaderDbHelper;

/**
 * Created by Alex Lynchosky on 12/22/2014.
 */
public class DnDTabBarActivity extends TabActivity {

    /* Debugging */
    private static final String TAG    = DnDTabBarActivity.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    /* Main Tab */
    private static DnDTabBarActivity dndTabBarActivity;

    private Resources mResources;
    private TabHost mTabHost;

    /* Tab Names */
    private final String TAB_CHARACTER      = "character";
    private final String TAB_INVENTORY      = "inventory";
    private final String TAB_COMBAT         = "combat";
    private final String TAB_NOTES         = "notes";

    /* Save Data */
    private static FeedReaderDbHelper feedReaderDbHelper;
    private static boolean dataSaved = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.debug("onCreate called");

        // Prevent launching the main activity on top of other activities
        // This will ensure we only have one instance displayed
        final Intent intent = getIntent();
        if (!isTaskRoot()) {
            final String intentAction = intent.getAction();

            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                logger.debug("Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return;
            }
        }

        dndTabBarActivity = this;
        FeedReaderDbHelper.setContext(getApplicationContext());
        feedReaderDbHelper = FeedReaderDbHelper.getInstance();
        mTabHost = getTabHost();

        mResources = getResources();
        /* Insert Tabs */
        mTabHost.addTab(mTabHost.newTabSpec(TAB_CHARACTER)
                .setIndicator(getString(R.string.tab_header_character))
                .setContent(new Intent(dndTabBarActivity, CharacterSheetActivity.class)));
        TextView x = (TextView) mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        x.setTextSize(11);
        mTabHost.getTabWidget().getChildAt(0).getLayoutParams().width = 100;
        mTabHost.addTab(mTabHost.newTabSpec(TAB_COMBAT)
                .setIndicator(getString(R.string.tab_header_combat))
                .setContent(new Intent(dndTabBarActivity, CombatActivity.class)));
        x = (TextView) mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        x.setTextSize(12);
        mTabHost.getTabWidget().getChildAt(1).getLayoutParams().width = 100;
        mTabHost.addTab(mTabHost.newTabSpec(TAB_INVENTORY)
                .setIndicator(getString(R.string.tab_header_inventory))
                .setContent(new Intent(dndTabBarActivity, InventoryActivity.class)));
        x = (TextView) mTabHost.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
        x.setTextSize(11);
        mTabHost.getTabWidget().getChildAt(2).getLayoutParams().width = 100;
        mTabHost.addTab(mTabHost.newTabSpec(TAB_NOTES)
                .setIndicator(getString(R.string.tab_header_notes))
                .setContent(new Intent(dndTabBarActivity, NotesActivity.class)));
        x = (TextView) mTabHost.getTabWidget().getChildAt(3).findViewById(android.R.id.title);
        x.setTextSize(8);
        mTabHost.getTabWidget().getChildAt(3).getLayoutParams().width = 30;

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                logger.debug("Changing to " + tabId + " tab");
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        logger.debug("onResume");
        CharacterManager characterManager = CharacterManager.getInstance();
        characterManager.syncWithDatabase(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        CharacterManager characterManager = CharacterManager.getInstance();
        characterManager.saveData(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public static DnDTabBarActivity getInstance(){
        return dndTabBarActivity;
    }

}
