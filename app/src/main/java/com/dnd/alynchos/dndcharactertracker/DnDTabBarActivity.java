package com.dnd.alynchos.dndcharactertracker;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.alynchos.dndcharactertracker.Debug.Logger;
import com.dnd.alynchos.dndcharactertracker.SaveData.FeedReaderDbHelper;

/**
 * Created by Alex Lynchosky on 12/22/2014.
 */
public class DnDTabBarActivity extends AppCompatActivity implements View.OnClickListener {

    /* Debugging */
    private static final String TAG    = DnDTabBarActivity.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    /* Main Activity */
    private static DnDTabBarActivity dndTabBarActivity;

    /* Hamburger Menu */
    private ListView mHamburgerList;
    private DrawerLayout mHamburgerLayout;
    private ActionBarDrawerToggle mHamburgerToggle;
    private ArrayAdapter<String> mHamburgerAdapter;

    /* Menu HUD */
    private RelativeLayout mRelativeLayoutMenuHUD;
    private ImageButton mButtonHamburgerMenu;
    private TextView mTextViewTitle;

    /* Save Data */
    private static FeedReaderDbHelper feedReaderDbHelper;
    private static boolean dataSaved = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.debug("onCreate called");
        setContentView(R.layout.main);

        dndTabBarActivity = this;
        FeedReaderDbHelper.setContext(getApplicationContext());
        feedReaderDbHelper = FeedReaderDbHelper.getInstance();

        // Setup Menu HUD
        setupMenuHUD();

        // Setup Hamburger Menu
        setupHamburgerList();
        setupHamburgerMenu();
        // Create onClick responses
        setupHamburgerOnClick();

    }

    @Override
    public void onResume(){
        super.onResume();
        logger.debug("onResume");
        //CharacterManager characterManager = CharacterManager.getInstance();
        //characterManager.syncWithDatabase(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        //CharacterManager characterManager = CharacterManager.getInstance();
        //characterManager.saveData(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.but_hamburger_menu:
                mHamburgerLayout.openDrawer(GravityCompat.START);
                break;
        }
    }

    public static DnDTabBarActivity getInstance(){
        return dndTabBarActivity;
    }

    private void setupMenuHUD(){
        mRelativeLayoutMenuHUD = (RelativeLayout) findViewById(R.id.include_menu_hud);
        mTextViewTitle = (TextView) mRelativeLayoutMenuHUD.findViewById(R.id.text_screen_title);
        mButtonHamburgerMenu = (ImageButton) mRelativeLayoutMenuHUD.findViewById(R.id.but_hamburger_menu);
        mButtonHamburgerMenu.setOnClickListener(this);
    }

    /* Private Helper Methods */
    private void setupHamburgerList(){
        mHamburgerList = (ListView)findViewById(R.id.navList);
        String[] osArray = {getString(R.string.text_character_info_screen) ,
                getString(R.string.text_combat_screen),
                getString(R.string.text_inventory_screen)};
        mHamburgerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mHamburgerList.setAdapter(mHamburgerAdapter);
    }

    private void setupHamburgerMenu(){
        mHamburgerLayout = (DrawerLayout) findViewById(R.id.layout_main);
        mHamburgerToggle = new ActionBarDrawerToggle(this, mHamburgerLayout, R.string.but_open_hamburger, R.string.but_close_hamburger) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mTextViewTitle.setText(getString(R.string.text_testing));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mTextViewTitle.setText(getString(R.string.text_character_title));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };

        mHamburgerToggle.setDrawerIndicatorEnabled(true);
        mHamburgerLayout.setDrawerListener(mHamburgerToggle);
    }

    private void setupHamburgerOnClick(){
        mHamburgerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(dndTabBarActivity,("You've clicked: " + position), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
