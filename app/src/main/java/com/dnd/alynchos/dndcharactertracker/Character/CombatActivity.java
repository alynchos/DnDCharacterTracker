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
        mButtonInit = (Button) findViewById(R.id.button_modify_armor);
        mButtonInit.setOnClickListener(this);
        mButtonInit = (Button) findViewById(R.id.button_modify_health);
        mButtonInit.setOnClickListener(this);
        mButtonInit = (Button) findViewById(R.id.button_modify_initiative);
        mButtonInit.setOnClickListener(this);
        mButtonInit = (Button) findViewById(R.id.button_modify_speed);
        mButtonInit.setOnClickListener(this);
        mButtonInit = (Button) findViewById(R.id.button_modify_weapon1);
        mButtonInit.setOnClickListener(this);
        mButtonInit = (Button) findViewById(R.id.button_modify_weapon2);
        mButtonInit.setOnClickListener(this);
        mButtonInit = (Button) findViewById(R.id.button_modify_weapon3);
        mButtonInit.setOnClickListener(this);
        mButtonInit = (Button) findViewById(R.id.button_modify_ammo);
        mButtonInit.setOnClickListener(this);
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
            case R.id.button_modify_armor:
            case R.id.button_modify_health:
            case R.id.button_modify_initiative:
            case R.id.button_modify_speed:
                mPressedButton = (Button) view;
                showModifyElementDialog();
                break;
            case R.id.button_modify_weapon1:
            case R.id.button_modify_weapon2:
            case R.id.button_modify_weapon3:
                mPressedButton = (Button) view;
                showModifyWeaponDialog();
                break;
            case R.id.button_modify_ammo:
                mPressedButton = (Button) view;
                showModifyAmmoDialog();
                break;
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
        CharacterManager characterManager = CharacterManager.getInstance();
        // Fill in numbers
        switch(updateID) {
            case ARMOR:
                mModifyElementText = (TextView) findViewById(R.id.value_armor);
                mModifyElementText.setText("" + characterManager.getArmor());
                break;
            case HEALTH:
                mModifyElementText = (TextView) findViewById(R.id.value_health);
                mModifyElementText.setText("" + characterManager.getHealth());
                break;
            case INITIATIVE:
                mModifyElementText = (TextView) findViewById(R.id.value_initiative);
                int initiative = characterManager.getInitiative();
                mModifyElementText.setText("" + (initiative >= 0 ? "+" + initiative : initiative));
                break;
            case SPEED:
                mModifyElementText = (TextView) findViewById(R.id.value_speed);
                mModifyElementText.setText("" + characterManager.getSpeed());
                break;
            case WEAPON_1:
                // Weapons
                selectedWeapon = (Weapon) characterManager.getItem(characterManager.getWeaponName(0));
                if (selectedWeapon != null) {
                    mModifyElementText = (TextView) findViewById(R.id.text_weapon1_name);
                    mModifyElementText.setText("" + selectedWeapon.name);
                    mModifyElementText = (TextView) findViewById(R.id.text_weapon1_atk_bns);
                    mModifyElementText.setText("+" + characterManager.getAttackBonus(0));
                    mModifyElementText = (TextView) findViewById(R.id.text_weapon1_damage);
                    mModifyElementText.setText("" + selectedWeapon.dice_num + "d" + selectedWeapon.dice_size +
                            " + " + selectedWeapon.flat_damage + " " + selectedWeapon.damage_type);
                }
                break;
            case WEAPON_2:
                selectedWeapon = (Weapon) characterManager.getItem(characterManager.getWeaponName(1));
                if (selectedWeapon != null) {
                    mModifyElementText = (TextView) findViewById(R.id.text_weapon2_name);
                    mModifyElementText.setText("" + selectedWeapon.name);
                    mModifyElementText = (TextView) findViewById(R.id.text_weapon2_atk_bns);
                    mModifyElementText.setText("+" + characterManager.getAttackBonus(1));
                    mModifyElementText = (TextView) findViewById(R.id.text_weapon2_damage);
                    mModifyElementText.setText("" + selectedWeapon.dice_num + "d" + selectedWeapon.dice_size +
                            " + " + selectedWeapon.flat_damage + " " + selectedWeapon.damage_type);
                }
                break;
            case WEAPON_3:
                selectedWeapon = (Weapon) characterManager.getItem(characterManager.getWeaponName(2));
                if (selectedWeapon != null) {
                    mModifyElementText = (TextView) findViewById(R.id.text_weapon3_name);
                    mModifyElementText.setText("" + selectedWeapon.name);
                    mModifyElementText = (TextView) findViewById(R.id.text_weapon3_atk_bns);
                    mModifyElementText.setText("+" + characterManager.getAttackBonus(2));
                    mModifyElementText = (TextView) findViewById(R.id.text_weapon3_damage);
                    mModifyElementText.setText("" + selectedWeapon.dice_num + "d" + selectedWeapon.dice_size +
                            " + " + selectedWeapon.flat_damage + " " + selectedWeapon.damage_type);
                }
                break;
            case AMMO:
                // Ammo
                selectedAmmo = characterManager.getItem(characterManager.getAmmo());
                if (selectedAmmo != null) {
                    mModifyElementText = (TextView) findViewById(R.id.value_ammo);
                    mModifyElementText.setText("x" + selectedAmmo.amount);
                    mModifyElementText = (TextView) findViewById(R.id.type_ammo);
                    mModifyElementText.setText(" " + selectedAmmo.name);
                }
                break;
            case ALL:
                updateUI(UpdateUIIds.ARMOR);
                updateUI(UpdateUIIds.HEALTH);
                updateUI(UpdateUIIds.INITIATIVE);
                updateUI(UpdateUIIds.SPEED);
                updateUI(UpdateUIIds.WEAPON_1);
                updateUI(UpdateUIIds.WEAPON_2);
                updateUI(UpdateUIIds.WEAPON_3);
                updateUI(UpdateUIIds.AMMO);
                break;
        }
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
        switch (mPressedButton.getId()) {
            case R.id.button_modify_armor:
                mModifyElementText = (TextView) findViewById(R.id.value_armor);
                break;
            case R.id.button_modify_health:
                mModifyElementText = (TextView) findViewById(R.id.value_health);
                break;
            case R.id.button_modify_initiative:
                mModifyElementText = (TextView) findViewById(R.id.value_initiative);
                break;
            case R.id.button_modify_speed:
                mModifyElementText = (TextView) findViewById(R.id.value_speed);
                break;
        }
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
        mModifyElementEdit = (EditText) mActiveView.findViewById(R.id.edit_atk_bns);
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

    /* Add Experience */
    DialogInterface.OnClickListener modifyElement = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            CharacterManager characterManager = CharacterManager.getInstance();
            try {
                switch (mPressedButton.getId()) {
                    case R.id.button_modify_armor:
                        characterManager.setArmor(Integer.parseInt(mModifyElementEdit.getText().toString()));
                        updateUI(UpdateUIIds.ARMOR);
                        break;
                    case R.id.button_modify_health:
                        characterManager.setHealth(Integer.parseInt(mModifyElementEdit.getText().toString()));
                        updateUI(UpdateUIIds.HEALTH);
                        break;
                    case R.id.button_modify_initiative:
                        characterManager.setInitiative(Integer.parseInt(mModifyElementEdit.getText().toString()));
                        updateUI(UpdateUIIds.INITIATIVE);
                        break;
                    case R.id.button_modify_speed:
                        characterManager.setSpeed(Integer.parseInt(mModifyElementEdit.getText().toString()));
                        updateUI(UpdateUIIds.SPEED);
                        break;
                    case R.id.button_modify_weapon1:
                        characterManager.setWeapon(selectedWeapon.name, Integer.parseInt(mModifyElementEdit.getText().toString()), 0);
                        updateUI(UpdateUIIds.WEAPON_1);
                        break;
                    case R.id.button_modify_weapon2:
                        characterManager.setWeapon(selectedWeapon.name, Integer.parseInt(mModifyElementEdit.getText().toString()), 1);
                        updateUI(UpdateUIIds.WEAPON_2);
                        break;
                    case R.id.button_modify_weapon3:
                        characterManager.setWeapon(selectedWeapon.name, Integer.parseInt(mModifyElementEdit.getText().toString()), 2);
                        updateUI(UpdateUIIds.WEAPON_3);
                        break;
                    case R.id.button_modify_ammo:
                        characterManager.setAmmo(selectedAmmo.name);
                        updateUI(UpdateUIIds.AMMO);
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
