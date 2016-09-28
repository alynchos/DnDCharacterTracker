package com.dnd.alynchos.dndcharactertracker;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.alynchos.dndcharactertracker.Character.BaseCharacter;
import com.dnd.alynchos.dndcharactertracker.Character.CharacterManager;
import com.dnd.alynchos.dndcharactertracker.Character.CharacterSheetFragment;
import com.dnd.alynchos.dndcharactertracker.Character.CombatFragment;
import com.dnd.alynchos.dndcharactertracker.Character.NotesFragment;
import com.dnd.alynchos.dndcharactertracker.Debug.Logger;
import com.dnd.alynchos.dndcharactertracker.Items.InventoryFragment;
import com.dnd.alynchos.dndcharactertracker.SaveData.FeedReaderDbHelper;

/**
 * Created by Alex Lynchosky on 12/22/2014.
 */
public class DnDTabBarActivity extends AppCompatActivity
        implements View.OnClickListener {

    /* Debugging */
    private static final String TAG = DnDTabBarActivity.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    /* Main Activity */
    private static DnDTabBarActivity dndTabBarActivity;
    ViewConfiguration mViewConfiguration;

    /* Fragments */
    private final String mCurrentFragmentTag = "FRAG_CURRENT";
    private final int NUM_TABS = 4;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    /* Touch Events */
    private GestureDetectorCompat mDetector;

    /* Hamburger Menu */
    private ListView mHamburgerList;
    private DrawerLayout mHamburgerLayout;
    private ActionBarDrawerToggle mHamburgerToggle;
    private ArrayAdapter<String> mHamburgerAdapter;
    private RelativeLayout mCharacterInfoLayout;
    /* Identity */
    private TextView mCharacterAlignment;
    private TextView mCharacterBackground;
    private TextView mCharacterClass;
    private TextView mCharacterName;
    private TextView mCharacterRace;
    /* Currency */
    private LinearLayout mLinearLayoutPlat;
    private LinearLayout mLinearLayoutGold;
    private LinearLayout mLinearLayoutSilver;
    private TextView mTextViewPlatVal;
    private TextView mTextViewGoldVal;
    private TextView mTextViewSilverVal;
    private TextView mTextViewCopperVal;

    private LinearLayout mButtonHamburgerMenu; // LinearLayout is the wrapper to increase clickbox size
    private TextView mTextViewTitle;
    private Tab mCurrentTab = Tab.character;

    private enum Tab {
        character,
        combat,
        inventory,
        notes
    }

    private enum Direction {
        left,
        right,
        up,
        down
    }


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
                updateHamburgerMenuInfo();
                Fragment frag = getSupportFragmentManager().findFragmentByTag(mCurrentFragmentTag);
                if (frag instanceof CharacterSheetFragment) {
                    ((CharacterSheetFragment) frag).updateUI();
                    Toast.makeText(dndTabBarActivity, "Data Loaded", Toast.LENGTH_SHORT).show();
                }
            } else if (intent.getAction().equals(CharacterManager.UPDATE_INV_UI)) {
                logger.debug("Update Inventory UI from broadcast");
                Fragment frag = getSupportFragmentManager().findFragmentByTag(mCurrentFragmentTag);
                if (frag instanceof InventoryFragment) {
                    ((InventoryFragment) frag).updateInventoryList();
                    ((InventoryFragment) frag).updateHeader();
                    //Toast.makeText(dndTabBarActivity, "Inventory Updated", Toast.LENGTH_SHORT).show();
                }
            } else if (intent.getAction().equals(CharacterManager.CLEAR_INV)) {
                logger.debug("Clear Inventory broadcast");
                Fragment frag = getSupportFragmentManager().findFragmentByTag(mCurrentFragmentTag);
                if (frag instanceof InventoryFragment) {
                    ((InventoryFragment) frag).updateInventoryList();
                    ((InventoryFragment) frag).updateHeader();
                    //Toast.makeText(dndTabBarActivity, "Inventory Deleted", Toast.LENGTH_SHORT).show();
                }
            } else if (intent.getAction().equals(CharacterManager.HIDE_KEYBOARD)) {
                hideKeyboard(true);
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
        mViewConfiguration = ViewConfiguration.get(this);
        // ViewPager
        mPager = (ViewPager) findViewById(R.id.pager_main_slide_view);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(pagerChangeListener);
        CharacterManager characterManager = CharacterManager.getInstance();
        if (characterManager.getCharacter() == null) {
            characterManager.setCharacter(new BaseCharacter());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        logger.debug("onResume");
        // Register the update intent listener
        if (!updateUIRegistered) {
            registerReceiver(updateUIReceiver, new IntentFilter(CharacterManager.UPDATE_UI));
            registerReceiver(updateUIReceiver, new IntentFilter(CharacterManager.UPDATE_INV_UI));
            registerReceiver(updateUIReceiver, new IntentFilter(CharacterManager.CLEAR_INV));
            registerReceiver(updateUIReceiver, new IntentFilter(CharacterManager.HIDE_KEYBOARD));
            registerReceiver(updateUIReceiver, new IntentFilter(CharacterManager.SHOW_KEYBOARD));
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
            case R.id.layout_hamburger_menu:
                mHamburgerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.include_currency_layout:
                currencyOnClick(v);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    public static DnDTabBarActivity getInstance() {
        return dndTabBarActivity;
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
        CharacterManager characterManager = CharacterManager.getInstance();
        if (plat_val == null || plat_val.length() == 0 || plat_val.equals("0"))
            mLinearLayoutPlat.setVisibility(View.INVISIBLE);
        else {
            mLinearLayoutPlat.setVisibility(View.VISIBLE);
            characterManager.setCurrency(Integer.parseInt(plat_val.toString()), CharacterManager.CurrencyType.platinum);
        }

        if (gold_val == null || gold_val.length() == 0 || gold_val.equals("0"))
            mLinearLayoutGold.setVisibility(View.INVISIBLE);
        else {
            mLinearLayoutGold.setVisibility(View.VISIBLE);
            characterManager.setCurrency(Integer.parseInt(gold_val.toString()), CharacterManager.CurrencyType.gold);
        }

        if (silver_val == null || silver_val.length() == 0 || silver_val.equals("0"))
            mLinearLayoutSilver.setVisibility(View.INVISIBLE);
        else {
            mLinearLayoutSilver.setVisibility(View.VISIBLE);
            characterManager.setCurrency(Integer.parseInt(silver_val.toString()), CharacterManager.CurrencyType.silver);
        }

        if (copper_val == null || copper_val.length() == 0 || copper_val.equals("0")) {
            copper_val = "0";
        } else {
            characterManager.setCurrency(Integer.parseInt(copper_val.toString()), CharacterManager.CurrencyType.copper);
        }
        mTextViewPlatVal.setText(plat_val);
        mTextViewGoldVal.setText(gold_val);
        mTextViewSilverVal.setText(silver_val);
        mTextViewCopperVal.setText(copper_val);
    }

    private void updateHamburgerMenuInfo() {
        CharacterManager characterManager = CharacterManager.getInstance();
        mCharacterAlignment.setText(characterManager.getAlign());
        mCharacterBackground.setText(characterManager.getBackground());
        mCharacterClass.setText(characterManager.getClass_());
        mCharacterName.setText(characterManager.getName());
        mCharacterRace.setText(characterManager.getRace());
        // Currency
        int[] currency = characterManager.getCurrency();
        updateCurrency(String.format("%s", currency[3]), String.format("%s", currency[2]), String.format("%s", currency[1]), String.format("%s", currency[0]));
    }

    private void modifyIdentityDialog(final View v) {
        logger.debug("Showing Modify Identity Dialog");
        final CharacterManager characterManager = CharacterManager.getInstance();
        String fillVal = "";
        String title = " ";
        switch (v.getId()) {
            case R.id.text_character_alignment:
                fillVal = characterManager.getAlign();
                title = getString(R.string.hint_character_alignment);
                break;
            case R.id.text_character_background:
                fillVal = characterManager.getBackground();
                title = getString(R.string.hint_character_background);
                break;
            case R.id.text_character_class:
                fillVal = characterManager.getClass_();
                title = getString(R.string.hint_character_class);
                break;
            case R.id.text_character_name:
                fillVal = characterManager.getName();
                title = getString(R.string.hint_character_name);
                break;
            case R.id.text_character_race:
                fillVal = characterManager.getRace();
                title = getString(R.string.hint_character_race);
                break;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.modify_element_layout, null);
        final EditText modify_val = (EditText) dialogView.findViewById(R.id.edit_modify_value);
        modify_val.setText(fillVal);
        modify_val.setInputType(InputType.TYPE_CLASS_TEXT);
        DialogInterface.OnClickListener syncNewData = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (v.getId()) {
                    case R.id.text_character_alignment:
                        characterManager.setAlignment(modify_val.getText().toString());
                        break;
                    case R.id.text_character_background:
                        characterManager.setBackground(modify_val.getText().toString());
                        break;
                    case R.id.text_character_class:
                        characterManager.setClass_(modify_val.getText().toString());
                        break;
                    case R.id.text_character_name:
                        characterManager.setName(modify_val.getText().toString());
                        break;
                    case R.id.text_character_race:
                        characterManager.setRace(modify_val.getText().toString());
                        break;
                }
                updateHamburgerMenuInfo();
            }
        };
        AlertDialog mModifyIdentityDialog = new AlertDialog.Builder(this)
                .setTitle("Modify " + title)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, syncNewData)
                .setNegativeButton(android.R.string.cancel, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        hideKeyboard(true);
                    }
                })
                .create();
        mModifyIdentityDialog.show();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_FORCED);
    }

    /* End Private Helper Methods */

    /* Setup Helper Methods */

    private void setupMenuHUD() {
        LinearLayout mLinearLayoutMenuHUD = (LinearLayout) findViewById(R.id.include_menu_hud);
        assert mLinearLayoutMenuHUD != null;
        mTextViewTitle = (TextView) mLinearLayoutMenuHUD.findViewById(R.id.text_screen_title);
        mButtonHamburgerMenu = (LinearLayout) mLinearLayoutMenuHUD.findViewById(R.id.layout_hamburger_menu);
        mButtonHamburgerMenu.setOnClickListener(this);
    }

    private void setupCharacterInfo() {
        mCharacterInfoLayout = (RelativeLayout) mHamburgerLayout.findViewById(R.id.include_character_info);
        /* Identity */
        View.OnTouchListener identityModifier = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        modifyIdentityDialog(v);
                        break;
                }
                return false;
            }
        };
        mCharacterAlignment = (TextView) mCharacterInfoLayout.findViewById(R.id.text_character_alignment);
        mCharacterAlignment.setOnTouchListener(identityModifier);
        mCharacterBackground = (TextView) mCharacterInfoLayout.findViewById(R.id.text_character_background);
        mCharacterBackground.setOnTouchListener(identityModifier);
        mCharacterClass = (TextView) mCharacterInfoLayout.findViewById(R.id.text_character_class);
        mCharacterClass.setOnTouchListener(identityModifier);
        mCharacterName = (TextView) mCharacterInfoLayout.findViewById(R.id.text_character_name);
        mCharacterName.setOnTouchListener(identityModifier);
        mCharacterRace = (TextView) mCharacterInfoLayout.findViewById(R.id.text_character_race);
        mCharacterRace.setOnTouchListener(identityModifier);
        /* Currency */
        LinearLayout mCurrencyLayout = (LinearLayout) mCharacterInfoLayout.findViewById(R.id.include_currency_layout);
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
                getString(R.string.text_inventory_screen),
                getString(R.string.text_notes_screen)};
        mHamburgerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, osArray);
        mHamburgerList.setAdapter(mHamburgerAdapter);
    }

    private void setupHamburgerMenu() {
        mHamburgerLayout = (DrawerLayout) findViewById(R.id.layout_main);
        mHamburgerToggle = new ActionBarDrawerToggle(this, mHamburgerLayout, R.string.but_open_hamburger, R.string.but_close_hamburger) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // This calls the method ten bazillion times but it works so w.e
                hideKeyboard();
            }

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
                logger.debug("Setting page manually to position [" + position + "]");
                mPager.setCurrentItem(position, false);
                mHamburgerLayout.closeDrawers();
            }
        });
    }

    private void switchTab(int tab) {
        switch (tab) {
            case 0:
                switchTab(Tab.character);
                break;
            case 1:
                switchTab(Tab.combat);
                break;
            case 2:
                switchTab(Tab.inventory);
                break;
            case 3:
                switchTab(Tab.notes);
                break;
            default:
                logger.error("Unacceptable tab selected");
                break;
        }
    }

    private void switchTab(Tab tab) {
        switch (tab) {
            case character:
                mCurrentTab = Tab.character;
                mTextViewTitle.setText(getString(R.string.text_character_title));
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

    private Tab getNextTab() {
        return getNextTab(true);
    }

    private Tab getNextTab(boolean forward) {
        if (mCurrentTab == null) return Tab.character;
        switch (mCurrentTab) {
            case character:
                return forward ? Tab.combat : Tab.notes;
            case combat:
                return forward ? Tab.inventory : Tab.character;
            case inventory:
                return forward ? Tab.notes : Tab.combat;
            case notes:
                return forward ? Tab.character : Tab.inventory;
            default:
                logger.error("Unacceptable tab selected");
                return Tab.character;
        }
    }

    private void hideKeyboard() {
        hideKeyboard(false);
    }

    private void hideKeyboard(boolean force) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!imm.isAcceptingText() && !force) {
            return;
        }
        View view = getCurrentFocus();
        if (view == null) {
            logger.warn("Current focus was null, cannot hide keyboard");
            return;
        }
        logger.debug("Closing keyboard on view [" + view.getId() + "]");
        ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                logger.debug("Keyboard closing receiver result: " + resultCode);
                switch (resultCode) {
                    case InputMethodManager.RESULT_HIDDEN:
                    case InputMethodManager.RESULT_UNCHANGED_HIDDEN:
                        break;
                }
            }
        };
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0, resultReceiver);
    }

    /* End Setup Helper Methods */

    /* Screen Slider */

    private ViewPager.OnPageChangeListener pagerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switchTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            logger.debug("Getting item [" + position + "]");
            Fragment fragmentToChange = null;
            switch (position) {
                case 0:
                    fragmentToChange = new CharacterSheetFragment();
                    break;
                case 1:
                    fragmentToChange = new CombatFragment();
                    break;
                case 2:
                    fragmentToChange = new InventoryFragment();
                    break;
                case 3:
                    fragmentToChange = new NotesFragment();
                    break;
                default:
                    logger.error("Unacceptable tab selected");
                    break;
            }
            return fragmentToChange;
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }
    }

    /* End Screen Slider */
}
