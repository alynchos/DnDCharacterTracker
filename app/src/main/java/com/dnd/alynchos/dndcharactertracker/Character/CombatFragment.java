package com.dnd.alynchos.dndcharactertracker.Character;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.alynchos.dndcharactertracker.Debug.Logger;
import com.dnd.alynchos.dndcharactertracker.DnDTabBarActivity;
import com.dnd.alynchos.dndcharactertracker.Items.Item;
import com.dnd.alynchos.dndcharactertracker.Items.Weapons.Damage;
import com.dnd.alynchos.dndcharactertracker.Items.Weapons.Weapon;
import com.dnd.alynchos.dndcharactertracker.R;
import com.dnd.alynchos.dndcharactertracker.Utils.AreYouSureDialog;
import com.dnd.alynchos.dndcharactertracker.Utils.ExpandedListView;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class CombatFragment extends Fragment {

    /* Debugging */
    private static final String TAG = CombatFragment.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    /* UI elements */
    private TextView mModifyArmorText;
    private TextView mModifyHealthText;
    private TextView mModifyInitiativeText;
    private TextView mModifySpeedText;
    private FloatingActionButton mAddFab;
    private RadioButton mRadioButtonAddCombatWeapon;
    private RadioButton mRadioButtonAddCombatAmmo;
    private ExpandedListView mWeaponsListView;
    private ExpandedListView mAmmoListView;
    private Button mAddWeaponButton;
    private Button mAddAmmoButton;

    /* Modify Attr elements */
    private AlertDialog mModifyElementDialog;
    private EditText mModifyElementEdit;
    private View mSelectedView;
    private View mActiveView;

    /* Modify Weapon / Ammo elements */
    private ListView mInventoryList;
    private BaseAdapter mInventoryArrayAdapter;
    private Weapon selectedWeapon;
    private Item selectedAmmo;

    /* Timer */
    CountDownTimer mDoubleClickTimer = null;
    public static final int DOUBLE_TAP_TIME = 500; // (ms)

    public enum UpdateUIIds {
        ARMOR, HEALTH, INITIATIVE, SPEED, WEAPON, AMMO, ALL
    }

    public CombatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_combat_layout, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /* Init Methods */
    private void initUI(View view) {
        View currView = view.findViewById(R.id.include_armor_layout);
        currView.setOnTouchListener(viewTouched);
        mModifyArmorText = (TextView) currView.findViewById(R.id.value_armor);

        currView = view.findViewById(R.id.include_health_layout);
        currView.setOnTouchListener(viewTouched);
        mModifyHealthText = (TextView) currView.findViewById(R.id.value_health);
        currView.findViewById(R.id.button_add_health).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementHealth();
            }
        });
        currView.findViewById(R.id.button_subtract_health).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementHealth();
            }
        });

        currView = view.findViewById(R.id.include_initiative_layout);
        currView.setOnTouchListener(viewTouched);
        mModifyInitiativeText = (TextView) currView.findViewById(R.id.value_initiative);

        currView = view.findViewById(R.id.include_speed_layout);
        currView.setOnTouchListener(viewTouched);
        mModifySpeedText = (TextView) currView.findViewById(R.id.value_speed);

        mWeaponsListView = (ExpandedListView) view.findViewById(R.id.list_combat_weapons);
        mAddWeaponButton = (Button) view.findViewById(R.id.but_add_weapon);
        mAddWeaponButton.setOnClickListener(buttonClick);
        refreshEquippedWeaponsList();
        mAmmoListView = (ExpandedListView) view.findViewById(R.id.list_combat_ammo);
        mAddAmmoButton = (Button) view.findViewById(R.id.but_add_ammo);
        mAddAmmoButton.setOnClickListener(buttonClick);
        refreshAmmoList();
        mAddFab = (FloatingActionButton) view.findViewById(R.id.fab_add_to_combat);
        mAddFab.setOnClickListener(buttonClick);
    }

    private void initAddCombatItemView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mActiveView = inflater.inflate(R.layout.add_combat_item_layout, null);
        RadioGroup rg = (RadioGroup) mActiveView.findViewById(R.id.radio_group_add_combat_item);
        mRadioButtonAddCombatWeapon = (RadioButton) rg.findViewById(R.id.radio_button_add_combat_weapon);
        mRadioButtonAddCombatWeapon.setOnClickListener(selectedCombatRadioItem);
        mRadioButtonAddCombatAmmo = (RadioButton) rg.findViewById(R.id.radio_button_add_combat_ammo);
        mRadioButtonAddCombatAmmo.setOnClickListener(selectedCombatRadioItem);
    }

    private void initModifyElementView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mActiveView = inflater.inflate(R.layout.modify_element_layout, null);
        mModifyElementEdit = (EditText) mActiveView.findViewById(R.id.edit_modify_value);
    }

    private void initSelectCombatItemView(boolean isWeapon) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mActiveView = inflater.inflate(R.layout.select_weapon_layout, null);
        mInventoryList = (ListView) mActiveView.findViewById(R.id.inventory_list);
        updateInventoryList(isWeapon);
    }

    /* End Init Methods */

    /* Update Methods */
    public void updateUI(UpdateUIIds updateID) {
        CharacterManager characterManager = CharacterManager.getInstance();
        // Fill in numbers
        switch (updateID) {
            case ARMOR:
                mModifyArmorText.setText("" + characterManager.getArmor());
                break;
            case HEALTH:
                mModifyHealthText.setText("" + characterManager.getHealth());
                break;
            case INITIATIVE:
                int initiative = characterManager.getInitiative();
                mModifyInitiativeText.setText("" + (initiative >= 0 ? "+" + initiative : initiative));
                break;
            case SPEED:
                mModifySpeedText.setText("" + characterManager.getSpeed());
                break;
            case WEAPON:
                refreshEquippedWeaponsList();
                break;
            case AMMO:
                refreshAmmoList();
                break;
            case ALL:
                logger.debug("Updating combat UI");
                updateUI(UpdateUIIds.ARMOR);
                updateUI(UpdateUIIds.HEALTH);
                updateUI(UpdateUIIds.INITIATIVE);
                updateUI(UpdateUIIds.SPEED);
                updateUI(UpdateUIIds.WEAPON);
                updateUI(UpdateUIIds.AMMO);
                break;
        }
    }

    /* End Update Methods */

    /* Add new combat item listener */
    View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CharacterManager characterManager = CharacterManager.getInstance();
            switch (v.getId()) {
                case R.id.but_add_ammo:
                    characterManager.createTestAmmo();
                    refreshAmmoList();
                    break;
                case R.id.but_add_weapon:
                    characterManager.createTestWeapons();
                    refreshEquippedWeaponsList();
                    break;
                case R.id.fab_add_to_combat:
                    showAddCombatItemDialog();
                    break;
            }
        }
    };

    View.OnClickListener selectedCombatRadioItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            StringBuilder title = new StringBuilder();
            switch (v.getId()) {
                case R.id.radio_button_add_combat_weapon:
                    initSelectCombatItemView(true);
                    title.append(getActivity().getString(R.string.text_weapon));
                    break;
                case R.id.radio_button_add_combat_ammo:
                    initSelectCombatItemView(false);
                    title.append(getActivity().getString(R.string.text_ammunition_title));
                    break;
            }
            AlertDialog selectCombatItem = new AlertDialog.Builder(getActivity())
                    .setTitle("Add new " + title.toString())
                    .setView(mActiveView)
                    .setPositiveButton(android.R.string.ok, null)
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
            selectCombatItem.show();
        }
    };

    private void showAddCombatItemDialog() {
        initAddCombatItemView();
        AlertDialog addCombatItemAlertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.text_add_combat_item)
                .setView(mActiveView)
                .setNegativeButton(android.R.string.cancel, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        getActivity().sendBroadcast(new Intent(CharacterManager.HIDE_KEYBOARD));
                    }
                })
                .create();
        addCombatItemAlertDialog.show();
    }

    /* Double click modify listener */
    View.OnTouchListener viewTouched = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (mDoubleClickTimer != null) {
                        mSelectedView = v;
                        showModifyElementDialog(v);
                    } else {
                        mDoubleClickTimer = new CountDownTimer(DOUBLE_TAP_TIME, DOUBLE_TAP_TIME) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                mDoubleClickTimer = null;
                            }
                        };
                        mDoubleClickTimer.start();
                    }
                    break;
            }
            return true; // continue on for swipe check at main view
        }
    };

    /* Element Clicked */
    private void showModifyElementDialog(final View v) {
        logger.debug("Showing Element Modify Dialog");
        initModifyElementView();
        final CharacterManager characterManager = CharacterManager.getInstance();
        String fillVal = "";
        String title = " ";
        switch (v.getId()) {
            case R.id.include_armor_layout:
                fillVal = Integer.toString(characterManager.getArmor());
                title = getString(R.string.text_armor_class);
                break;
            case R.id.include_health_layout:
                fillVal = Integer.toString(characterManager.getHealth());
                title = getString(R.string.text_current_health_title);
                break;
            case R.id.include_initiative_layout:
                fillVal = Integer.toString(characterManager.getInitiative());
                title = getString(R.string.text_initiative);
                break;
            case R.id.include_speed_layout:
                fillVal = Integer.toString(characterManager.getSpeed());
                title = getString(R.string.text_afoot_speed);
                break;
        }
        mModifyElementDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Modify " + title)
                .setView(mActiveView)
                .setPositiveButton(android.R.string.ok, modifyElement)
                .setNegativeButton(android.R.string.cancel, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        getActivity().sendBroadcast(new Intent(CharacterManager.HIDE_KEYBOARD));
                    }
                })
                .create();
        mModifyElementEdit.setText(fillVal);
        mModifyElementDialog.show();
        //mModifyElementEdit.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(getActivity().getCurrentFocus(), InputMethodManager.SHOW_FORCED);
    }

    /* Confirm Element Modified */
    DialogInterface.OnClickListener modifyElement = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            CharacterManager characterManager = CharacterManager.getInstance();
            try {
                switch (mSelectedView.getId()) {
                    case R.id.include_armor_layout:
                        characterManager.setArmor(Integer.parseInt(mModifyElementEdit.getText().toString()));
                        updateUI(UpdateUIIds.ARMOR);
                        break;
                    case R.id.include_health_layout:
                        characterManager.setHealth(Integer.parseInt(mModifyElementEdit.getText().toString()));
                        updateUI(UpdateUIIds.HEALTH);
                        break;
                    case R.id.include_initiative_layout:
                        characterManager.setInitiative(Integer.parseInt(mModifyElementEdit.getText().toString()));
                        updateUI(UpdateUIIds.INITIATIVE);
                        break;
                    case R.id.include_speed_layout:
                        characterManager.setSpeed(Integer.parseInt(mModifyElementEdit.getText().toString()));
                        updateUI(UpdateUIIds.SPEED);
                    default:
                        break;
                }
            } catch (Exception e) {
                logger.debug("Could not parse the given modify value");
                e.printStackTrace();
            }
        }
    };

    AdapterView.OnItemClickListener inventoryOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CharacterManager characterManager = CharacterManager.getInstance();
            String name = (String) parent.getItemAtPosition(position);
            Item retrieved = characterManager.getItem(name);
            if (retrieved instanceof Weapon) {
                selectedWeapon = (Weapon) retrieved;
            } else {
                selectedAmmo = retrieved;
                if (selectedAmmo == null) {
                    logger.debug("Could not find item!");
                }
            }
            if ((mModifyElementEdit != null && mModifyElementEdit.getText().length() > 0) ||
                    !(retrieved instanceof Weapon)) {
                mModifyElementDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
            } else {
                for (int i = 0; i < mInventoryList.getCount(); i++) {
                    View curr = mInventoryList.getChildAt(i);
                    curr.setBackgroundColor(getResources().getColor(R.color.white));
                }
                view.setBackgroundColor(getResources().getColor(R.color.light_grey));
            }
        }
    };

    /* Private Helpers */
    private void updateInventoryList(boolean isWeapon) {
        CharacterManager characterManager = CharacterManager.getInstance();
        if (isWeapon) {
            mInventoryArrayAdapter = new WeaponListAdapter(getActivity(), characterManager.getInventoryWeapons());
        } else {
            mInventoryArrayAdapter = new WeaponListAdapter(getActivity(), characterManager.getInventoryWeapons());
        }
        mInventoryList.setAdapter(mInventoryArrayAdapter);
        mInventoryList.setOnItemClickListener(inventoryOnClickListener);
    }

    private void equipWeapon(Weapon weapon) {
        CharacterManager characterManager = CharacterManager.getInstance();
        characterManager.equipWeapon(weapon);
        refreshEquippedWeaponsList();
        Toast.makeText(getActivity(), R.string.text_weapon_equipped, Toast.LENGTH_SHORT).show();
    }

    private void unequipWeapon(int position) {
        CharacterManager characterManager = CharacterManager.getInstance();
        characterManager.unequipWeapon(position);
        refreshEquippedWeaponsList();
        Toast.makeText(getActivity(), R.string.text_weapon_unequipped, Toast.LENGTH_SHORT).show();
    }

    private void incrementAmmo(int position) {
        adjustAmmo(position, 1);
    }

    private void decrementAmmo(int position) {
        adjustAmmo(position, -1);
    }

    private void adjustAmmo(int position, int amount) {
        CharacterManager characterManager = CharacterManager.getInstance();
        characterManager.incrementAmmo(position);
        View currAmmoView = mAmmoListView.getChildAt(position);
        try {
            TextView ammoValText = (TextView) currAmmoView.findViewById(R.id.text_ammo_amount);
            int currAmount = Integer.parseInt(ammoValText.getText().toString());
            ammoValText.setText(String.valueOf(currAmount + amount));
        }
        catch (Exception e) {
            logger.error("Could not retrieve the ammo amount, not an integer");
            Toast.makeText(getActivity(), R.string.text_invalid_ammo_amount, Toast.LENGTH_SHORT).show();
        }
    }

    private void incrementHealth() {
        adjustHealth(1);
    }

    private void decrementHealth() {
        adjustHealth(-1);
    }

    private void adjustHealth( int amount) {
        CharacterManager characterManager = CharacterManager.getInstance();
        characterManager.setHealth(characterManager.getHealth() + amount);
        try {
            int currAmount = Integer.parseInt(mModifyHealthText.getText().toString());
            mModifyHealthText.setText(String.valueOf(currAmount + amount));
        }
        catch (Exception e) {
            logger.error("Could not retrieve the health amount, not an integer");
            Toast.makeText(getActivity(), R.string.text_invalid_health_amount, Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshEquippedWeaponsList() {
        CharacterManager characterManager = CharacterManager.getInstance();
        mWeaponsListView.setAdapter(new WeaponListAdapter(getActivity(), characterManager.getEquipedWeapons()));
        if (mWeaponsListView.getAdapter().getCount() > 0) {
            mAddWeaponButton.setVisibility(View.GONE);
            mWeaponsListView.setVisibility(View.VISIBLE);
        } else {
            mAddWeaponButton.setVisibility(View.VISIBLE);
            mWeaponsListView.setVisibility(View.GONE);
        }
    }

    private void refreshAmmoList() {
        CharacterManager characterManager = CharacterManager.getInstance();
        mAmmoListView.setAdapter(new AmmoListAdapter(getActivity(), characterManager.getEquipedAmmo()));
        if (mAmmoListView.getAdapter().getCount() > 0) {
            mAddAmmoButton.setVisibility(View.GONE);
            mAmmoListView.setVisibility(View.VISIBLE);
        } else {
            mAddAmmoButton.setVisibility(View.VISIBLE);
            mAmmoListView.setVisibility(View.GONE);
        }
    }

    /* Private class list adapters */
    private class AmmoListAdapter extends BaseAdapter {

        Context context;
        Item[] data;
        private LayoutInflater inflater = null;

        public AmmoListAdapter(Context context, Item[] data) {
            // TODO Auto-generated constructor stub
            this.context = context;
            this.data = data;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return data[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            if (vi == null) {
                vi = inflater.inflate(R.layout.ammo_layout, null);
            }
            ((TextView) vi.findViewById(R.id.text_ammo_name)).setText(data[position].name);
            ((TextView) vi.findViewById(R.id.text_ammo_amount)).setText(String.valueOf(data[position].amount));
            vi.findViewById(R.id.button_add_ammo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    incrementAmmo(position);
                }
            });
            vi.findViewById(R.id.button_subtract_ammo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decrementAmmo(position);
                }
            });
            return vi;
        }
    }

    private class WeaponListAdapter extends BaseAdapter {

        Context context;
        Weapon[] data;
        private LayoutInflater inflater = null;

        public WeaponListAdapter(Context context, Weapon[] data) {
            // TODO Auto-generated constructor stub
            this.context = context;
            this.data = data;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return data[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            if (vi == null) {
                vi = inflater.inflate(R.layout.weapon_equip_layout, null);
            }
            ((TextView) vi.findViewById(R.id.text_weapon1_name)).setText(data[position].name);
            int hit_total = data[position].hit + data[position].hit_bonus;
            ((TextView) vi.findViewById(R.id.text_weapon1_atk_bns)).setText(String.format(Locale.getDefault(), "+%d", hit_total));
            StringBuilder damage_list = new StringBuilder();
            for (Damage damage : data[position].damages) {
                damage_list.append(damage);
            }
            ((TextView) vi.findViewById(R.id.text_weapon1_damage)).setText(damage_list.toString());
            vi.findViewById(R.id.but_remove_equipped_weapon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AreYouSureDialog(DnDTabBarActivity.getInstance(),
                            "Unequip this weapon?",
                            (new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    unequipWeapon(position);
                                }
                            }),
                            null)
                            .show();
                }
            });
            return vi;
        }
    }
}
