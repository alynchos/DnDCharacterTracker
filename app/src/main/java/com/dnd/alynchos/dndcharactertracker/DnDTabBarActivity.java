package com.dnd.alynchos.dndcharactertracker;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.alynchos.dndcharactertracker.Character.CharacterManager;
import com.dnd.alynchos.dndcharactertracker.Character.CharacterSheetFragment;
import com.dnd.alynchos.dndcharactertracker.Debug.Logger;
import com.dnd.alynchos.dndcharactertracker.SaveData.FeedReaderDbHelper;

/**
 * Created by Alex Lynchosky on 12/22/2014.
 */
public class DnDTabBarActivity extends AppCompatActivity
        implements View.OnClickListener, CharacterSheetFragment.OnFragmentInteractionListener {

    /* Debugging */
    private static final String TAG = DnDTabBarActivity.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    /* Main Activity */
    private static DnDTabBarActivity dndTabBarActivity;

    /* Fragments */
    private final String mCharacterSheetFragmentTag = "FRAG_CHAR_SHEET";
    private final String mCombatFragmentTag = "FRAG_COMBAT";
    private final String mInventoryFragmentTag = "FRAG_INVENTORY";
    private final String mNotesFragmentTag = "FRAG_NOTES";

    /* Hamburger Menu */
    private ListView mHamburgerList;
    private DrawerLayout mHamburgerLayout;
    private ActionBarDrawerToggle mHamburgerToggle;
    private ArrayAdapter<String> mHamburgerAdapter;
    private RelativeLayout mCharacterInfoLayout;
    private LinearLayout mCurrencyLayout;

    /* Menu HUD */
    private RelativeLayout mRelativeLayoutMenuHUD;
    private ImageButton mButtonHamburgerMenu;
    private TextView mTextViewTitle;
    private Tab mCurrentTab = Tab.character;

    private enum Tab {
        character,
        combat,
        inventory,
        notes
    }

    /* Currency */
    private LinearLayout mLinearLayoutPlat;
    private LinearLayout mLinearLayoutGold;
    private LinearLayout mLinearLayoutSilver;
    private TextView mTextViewPlatVal;
    private TextView mTextViewGoldVal;
    private TextView mTextViewSilverVal;
    private TextView mTextViewCopperVal;


    /* Broadcast Receivers */

    boolean updateUIRegistered = false;

    /**
     * Broadcast receiver listening to Personal Status updates from VPTabBarActivity
     */
    private BroadcastReceiver updateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            if (intent.getAction().equals(CharacterManager.UPDATE_UI)) {
                logger.debug("Update UI from broadcast");
                ((CharacterSheetFragment) getSupportFragmentManager().findFragmentByTag(mCharacterSheetFragmentTag)).updateUI();
                Toast.makeText(dndTabBarActivity, "Data Loaded", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.debug("onCreate called");
        setContentView(R.layout.main);

        dndTabBarActivity = this;

        FeedReaderDbHelper.setContext(this);

        // Init all components
        setupMenuHUD();
        setupHamburgerList();
        setupHamburgerMenu();
        setupHamburgerOnClick();
        switchTab(Tab.character);
    }

    @Override
    public void onResume() {
        super.onResume();
        logger.debug("onResume");
        // Register the update intent listener
        if (!updateUIRegistered) {
            registerReceiver(updateUIReceiver, new IntentFilter(CharacterManager.UPDATE_UI));
            updateUIRegistered = true;
        }
        CharacterManager characterManager = CharacterManager.getInstance();
        characterManager.syncWithDatabase(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (updateUIRegistered) {
            unregisterReceiver(updateUIReceiver);
            updateUIRegistered = false;
        }
        CharacterManager characterManager = CharacterManager.getInstance();
        characterManager.saveData(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_hamburger_menu:
                mHamburgerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.include_currency_layout:
                currencyOnClick(v);
                break;
        }
    }

    public static DnDTabBarActivity getInstance() {
        return dndTabBarActivity;
    }

    /* Fragment Listeners */

    @Override
    public void onCharacterSheetFragmentInteraction(String string) {
        Toast.makeText(dndTabBarActivity, ("Fragment communication successful!"), Toast.LENGTH_SHORT).show();
    }

    /* End Fragment Listeners */

    /* Private Helper Methods */

    private void currencyOnClick(final View v) {
        logger.debug("Showing Modify Currency Dialog");
        LayoutInflater inflater = getLayoutInflater();
        final TextView text_plat = (TextView) v.findViewById(R.id.currency_plat_text);
        final TextView text_gold = (TextView) v.findViewById(R.id.currency_gold_text);
        final TextView text_silver = (TextView) v.findViewById(R.id.currency_silver_text);
        final TextView text_copper = (TextView) v.findViewById(R.id.currency_copper_text);
        final View addCurrencyView = inflater.inflate(R.layout.add_currency_layout, null);
        // Text
        final TextView new_text_plat = (TextView) addCurrencyView.findViewById(R.id.include_currency_single_plat).findViewById(R.id.currency_single_text);
        new_text_plat.setText(text_plat.getText());
        final TextView new_text_gold = (TextView) addCurrencyView.findViewById(R.id.include_currency_single_gold).findViewById(R.id.currency_single_text);
        new_text_gold.setText(text_gold.getText());
        final TextView new_text_silver = (TextView) addCurrencyView.findViewById(R.id.include_currency_single_silver).findViewById(R.id.currency_single_text);
        new_text_silver.setText(text_silver.getText());
        final TextView new_text_copper = (TextView) addCurrencyView.findViewById(R.id.include_currency_single_copper).findViewById(R.id.currency_single_text);
        new_text_copper.setText(text_copper.getText());
        // Images
        ((ImageView) addCurrencyView.findViewById(R.id.include_currency_single_plat).findViewById(R.id.currency_single_image)).setImageResource(R.drawable.plat_coin);
        ((ImageView) addCurrencyView.findViewById(R.id.include_currency_single_gold).findViewById(R.id.currency_single_image)).setImageResource(R.drawable.gold_coin);
        ((ImageView) addCurrencyView.findViewById(R.id.include_currency_single_silver).findViewById(R.id.currency_single_image)).setImageResource(R.drawable.silver_coin);
        ((ImageView) addCurrencyView.findViewById(R.id.include_currency_single_copper).findViewById(R.id.currency_single_image)).setImageResource(R.drawable.copper_coin);
        AlertDialog modifyCurrency = new AlertDialog.Builder(this)
                .setTitle(R.string.text_add_currency_title)
                .setView(addCurrencyView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateCurrency(new_text_plat.getText(), new_text_gold.getText(), new_text_silver.getText(), new_text_copper.getText());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        modifyCurrency.show();
    }

    private void updateCurrency(CharSequence plat_val,
                                CharSequence gold_val,
                                CharSequence silver_val,
                                CharSequence copper_val) {
        if (plat_val == null || plat_val.length() == 0)
            mLinearLayoutPlat.setVisibility(View.INVISIBLE);
        else mLinearLayoutPlat.setVisibility(View.VISIBLE);

        if (gold_val == null || gold_val.length() == 0)
            mLinearLayoutGold.setVisibility(View.INVISIBLE);
        else mLinearLayoutGold.setVisibility(View.VISIBLE);

        if (silver_val == null || silver_val.length() == 0)
            mLinearLayoutSilver.setVisibility(View.INVISIBLE);
        else mLinearLayoutSilver.setVisibility(View.VISIBLE);

        if (copper_val == null || copper_val.length() == 0) {
            copper_val = "0";
        }
        mTextViewPlatVal.setText(plat_val);
        mTextViewGoldVal.setText(gold_val);
        mTextViewSilverVal.setText(silver_val);
        mTextViewCopperVal.setText(copper_val);
    }

    /* End Private Helper Methods */

    /* Setup Helper Methods */

    private void setupMenuHUD() {
        mRelativeLayoutMenuHUD = (RelativeLayout) findViewById(R.id.include_menu_hud);
        assert mRelativeLayoutMenuHUD != null;
        mTextViewTitle = (TextView) mRelativeLayoutMenuHUD.findViewById(R.id.text_screen_title);
        mButtonHamburgerMenu = (ImageButton) mRelativeLayoutMenuHUD.findViewById(R.id.but_hamburger_menu);
        mButtonHamburgerMenu.setOnClickListener(this);
    }

    private void setupCharacterInfo() {
        mCharacterInfoLayout = (RelativeLayout) mHamburgerLayout.findViewById(R.id.include_character_info);
        mCurrencyLayout = (LinearLayout) mCharacterInfoLayout.findViewById(R.id.include_currency_layout);
        mCurrencyLayout.setOnClickListener(this);
        mLinearLayoutPlat = (LinearLayout) mCurrencyLayout.findViewById(R.id.currency_plat_view);
        mLinearLayoutGold = (LinearLayout) mCurrencyLayout.findViewById(R.id.currency_gold_view);
        mLinearLayoutSilver = (LinearLayout) mCurrencyLayout.findViewById(R.id.currency_silver_view);
        mTextViewPlatVal = (TextView) mCurrencyLayout.findViewById(R.id.currency_plat_text);
        mTextViewGoldVal = (TextView) mCurrencyLayout.findViewById(R.id.currency_gold_text);
        mTextViewSilverVal = (TextView) mCurrencyLayout.findViewById(R.id.currency_silver_text);
        mTextViewCopperVal = (TextView) mCurrencyLayout.findViewById(R.id.currency_copper_text);
    }

    private void setupHamburgerList() {
        mHamburgerList = (ListView) findViewById(R.id.navList);
        String[] osArray = {getString(R.string.text_character_info_screen),
                getString(R.string.text_combat_screen),
                getString(R.string.text_inventory_screen)};
        mHamburgerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mHamburgerList.setAdapter(mHamburgerAdapter);
    }

    private void setupHamburgerMenu() {
        mHamburgerLayout = (DrawerLayout) findViewById(R.id.layout_main);
        mHamburgerToggle = new ActionBarDrawerToggle(this, mHamburgerLayout, R.string.but_open_hamburger, R.string.but_close_hamburger) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };

        mHamburgerToggle.setDrawerIndicatorEnabled(true);
        mHamburgerLayout.setDrawerListener(mHamburgerToggle);
        setupCharacterInfo();
    }

    private void setupHamburgerOnClick() {
        mHamburgerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (mCurrentTab != Tab.character) switchTab(Tab.character);
                        break;
                    case 1:
                        if (mCurrentTab != Tab.combat) switchTab(Tab.combat);
                        break;
                    case 2:
                        if (mCurrentTab != Tab.inventory) switchTab(Tab.inventory);
                        break;
                    case 3:
                        if (mCurrentTab != Tab.notes) switchTab(Tab.notes);
                        break;
                    default:
                        logger.error("Unacceptable tab selected");
                        break;
                }
                mHamburgerLayout.closeDrawers();
            }
        });
    }

    private void switchTab(Tab tab) {
        switch (tab) {
            case character:
                mCurrentTab = Tab.character;
                mTextViewTitle.setText(getString(R.string.text_character_title));
                CharacterSheetFragment characterSheetFragment = new CharacterSheetFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_body, characterSheetFragment, mCharacterSheetFragmentTag)
                        .commit();
                break;
            case combat:
                mCurrentTab = Tab.combat;
                mTextViewTitle.setText(getString(R.string.text_combat_title));
                break;
            case inventory:
                mCurrentTab = Tab.inventory;
                mTextViewTitle.setText(getString(R.string.text_inventory_title));
                break;
            case notes:
                mCurrentTab = Tab.notes;
                mTextViewTitle.setText(getString(R.string.text_notes_title));
                break;
            default:
                logger.error("Unacceptable tab selected");
                break;
        }
    }

    /* End Setup Helper Methods */
}
