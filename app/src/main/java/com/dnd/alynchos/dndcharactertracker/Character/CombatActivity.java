package com.dnd.alynchos.dndcharactertracker.Character;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.alynchos.dndcharactertracker.Debug.Logger;
import com.dnd.alynchos.dndcharactertracker.Items.Item;
import com.dnd.alynchos.dndcharactertracker.Items.Weapons.Weapon;
import com.dnd.alynchos.dndcharactertracker.R;

public class CombatActivity extends Activity implements View.OnClickListener {

    /* Debugging */
    private static final String TAG = CombatActivity.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    /* UI elements */
    private Button mButtonInit;
    private TextView mModifyElementText;

    /* Modify Attr elements */
    private AlertDialog mModifyElementDialog;
    private EditText mModifyElementEdit;
    private Button mPressedButton;
    private View mActiveView;

    /* Modify Weapon / Ammo elements */
    private ListView mInventoryList;
    private ArrayAdapter<String> mInventoryArrayAdapter;
    private Weapon selectedWeapon;
    private Item selectedAmmo;

    private enum UpdateUIIds {
        ARMOR, HEALTH, INITIATIVE, SPEED, WEAPON_1, WEAPON_2, WEAPON_3, AMMO, ALL
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.combat_layout);

        // Buttons
        mButtonInit = (Button) findViewById(R.id.button_add_ammo);
        mButtonInit.setOnClickListener(this);
        mButtonInit = (Button) findViewById(R.id.button_subtract_ammo);
        mButtonInit.setOnClickListener(this);
        mButtonInit = (Button) findViewById(R.id.button_add_health);
        mButtonInit.setOnClickListener(this);
        mButtonInit = (Button) findViewById(R.id.button_subtract_health);
        mButtonInit.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI(UpdateUIIds.ALL);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_add_ammo:
                mPressedButton = (Button) view;
                incrementAmmo(1);
                updateUI(UpdateUIIds.AMMO);
                break;
            case R.id.button_subtract_ammo:
                mPressedButton = (Button) view;
                incrementAmmo(-1);
                updateUI(UpdateUIIds.AMMO);
                break;
            case R.id.button_add_health:
                mPressedButton = (Button) view;
                incrementHealth(1);
                updateUI(UpdateUIIds.HEALTH);
                break;
            case R.id.button_subtract_health:
                mPressedButton = (Button) view;
                incrementHealth(-1);
                updateUI(UpdateUIIds.HEALTH);
                break;
        }
    }

    private void updateUI(UpdateUIIds updateID) {
    }

    private void showModifyElementDialog() {
        logger.debug("Showing Element Modify Dialog");
        initModifyElementView();
        mModifyElementDialog = new AlertDialog.Builder(this)
                .setTitle("Modify Value")
                .setView(mActiveView)
                .setPositiveButton(android.R.string.ok, modifyElement)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mModifyElementDialog.show();
        mModifyElementEdit.requestFocus();
    }

    private void initModifyElementView() {
        LayoutInflater inflater = getLayoutInflater();
        mActiveView = inflater.inflate(R.layout.modify_element_layout, null);
        mModifyElementEdit = (EditText) mActiveView.findViewById(R.id.edit_modify_value);
    }

    private void showModifyWeaponDialog() {
        logger.debug("Showing Modify Weapon Dialog");
        initModifyWeaponView();
        mModifyElementDialog = new AlertDialog.Builder(this)
                .setTitle("Modify Weapon")
                .setView(mActiveView)
                .setPositiveButton(android.R.string.ok, modifyElement)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mModifyElementDialog.show();
    }

    private void initModifyWeaponView() {
        LayoutInflater inflater = getLayoutInflater();
        mActiveView = inflater.inflate(R.layout.select_weapon_layout, null);
        mInventoryList = (ListView) mActiveView.findViewById(R.id.inventory_list);
        updateInventoryList(true);
    }

    private void showModifyAmmoDialog() {
        logger.debug("Showing Modify Ammo Dialog");
        initModifyAmmoView();
        mModifyElementDialog = new AlertDialog.Builder(this)
                .setTitle("Modify Ammo")
                .setView(mActiveView)
                .setPositiveButton(android.R.string.ok, modifyElement)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mModifyElementDialog.show();
    }

    private void initModifyAmmoView() {
        LayoutInflater inflater = getLayoutInflater();
        mActiveView = inflater.inflate(R.layout.select_ammo_layout, null);
        mInventoryList = (ListView) mActiveView.findViewById(R.id.inventory_list);
        updateInventoryList(false);
    }

    private void incrementAmmo(int amount){
        CharacterManager characterManager = CharacterManager.getInstance();
        Item item = characterManager.getItem(characterManager.getAmmo());
        item.amount += amount;
        characterManager.modItem(item.name, item);
    }

    private void incrementHealth(int amount){
        CharacterManager characterManager = CharacterManager.getInstance();
        characterManager.setHealth(characterManager.getHealth() + amount);
    }

    private void updateInventoryList(boolean isWeapon) {
        CharacterManager characterManager = CharacterManager.getInstance();
        String names[];
        if (isWeapon) {
            names = characterManager.getInventoryWeaponNames(true);
        } else {
            names = characterManager.getInventoryWeaponNames(false);
        }

        mInventoryArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, names);

        mInventoryList.setAdapter(mInventoryArrayAdapter);
        mInventoryList.setOnItemClickListener(inventoryOnClickListener);
    }

    DialogInterface.OnClickListener modifyElement = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            CharacterManager characterManager = CharacterManager.getInstance();
            try {
                switch (mPressedButton.getId()) {
                    default:
                        //characterManager.setWeapon(selectedWeapon.name, Integer.parseInt(mModifyElementEdit.getText().toString()), 2);
                        updateUI(UpdateUIIds.WEAPON_3);
                        break;
                }
            } catch (Exception e) {
                logger.debug("Could not parse the given modify value");
                e.printStackTrace();
            }
        }
    };

    // On click listener for the Inventory List
    AdapterView.OnItemClickListener inventoryOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CharacterManager characterManager = CharacterManager.getInstance();
            String name = (String) parent.getItemAtPosition(position);
            Item retrieved = characterManager.getItem(name);
            if(retrieved instanceof  Weapon){
                selectedWeapon = (Weapon) retrieved;
                if (selectedWeapon == null) {
                    logger.debug("Could not find weapon!");
                }
            }
            else{
                selectedAmmo = retrieved;
                if (selectedAmmo == null) {
                    logger.debug("Could not find item!");
                }
            }
            if((mModifyElementEdit != null && mModifyElementEdit.getText().length() > 0) ||
                    !(retrieved instanceof Weapon)){
                mModifyElementDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
            }
            else{
                for(int i = 0; i < mInventoryList.getCount(); i++){
                    View curr = mInventoryList.getChildAt(i);
                    curr.setBackgroundColor(getResources().getColor(R.color.white));
                }
                view.setBackgroundColor(getResources().getColor(R.color.light_grey));
            }
        }
    };

}
