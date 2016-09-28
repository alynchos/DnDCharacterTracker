package com.dnd.alynchos.dndcharactertracker.Character;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dnd.alynchos.dndcharactertracker.Debug.Logger;
import com.dnd.alynchos.dndcharactertracker.DnDTabBarActivity;
import com.dnd.alynchos.dndcharactertracker.Items.Item;
import com.dnd.alynchos.dndcharactertracker.Items.Weapons.Weapon;
import com.dnd.alynchos.dndcharactertracker.R;

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

    /* Modify Attr elements */
    private AlertDialog mModifyElementDialog;
    private EditText mModifyElementEdit;
    private View mSelectedView;
    private View mActiveView;

    /* Modify Weapon / Ammo elements */
    private ListView mInventoryList;
    private ArrayAdapter<String> mInventoryArrayAdapter;
    private Weapon selectedWeapon;
    private Item selectedAmmo;

    /* Timer */
    CountDownTimer mDoubleClickTimer = null;
    public static final int DOUBLE_TAP_TIME = 500; // (ms)

    private enum UpdateUIIds {
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /* Init Methods */

    private void initUI(View view){
        View currView = view.findViewById(R.id.include_armor_layout);
        currView.setOnTouchListener(viewTouched);
        mModifyArmorText = (TextView) currView.findViewById(R.id.value_armor);

        currView = view.findViewById(R.id.include_health_layout);
        currView.setOnTouchListener(viewTouched);
        mModifyHealthText = (TextView) currView.findViewById(R.id.value_health);

        currView = view.findViewById(R.id.include_initiative_layout);
        currView.setOnTouchListener(viewTouched);
        mModifyInitiativeText = (TextView) currView.findViewById(R.id.value_initiative);

        currView = view.findViewById(R.id.include_speed_layout);
        currView.setOnTouchListener(viewTouched);
        mModifySpeedText = (TextView) currView.findViewById(R.id.value_speed);
    }

    private void initModifyElementView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mActiveView = inflater.inflate(R.layout.modify_element_layout, null);
        mModifyElementEdit = (EditText) mActiveView.findViewById(R.id.edit_modify_value);
    }

    /* End Init Methods */

    /* Update Methods */

    private void updateUI(UpdateUIIds updateID) {
        CharacterManager characterManager = CharacterManager.getInstance();
        // Fill in numbers
        switch(updateID) {
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
                // Weapons
                selectedWeapon = (Weapon) characterManager.getItem(characterManager.getWeaponName(0));
                if (selectedWeapon != null) {
//                    mModifyElementText = (TextView) findViewById(R.id.text_weapon1_name);
//                    mModifyElementText.setText("" + selectedWeapon.name);
//                    mModifyElementText = (TextView) findViewById(R.id.text_weapon1_atk_bns);
//                    mModifyElementText.setText("+" + characterManager.getAttackBonus(0));
//                    mModifyElementText = (TextView) findViewById(R.id.text_weapon1_damage);
//                    mModifyElementText.setText("" + selectedWeapon.dice_num + "d" + selectedWeapon.dice_size +
//                            " + " + selectedWeapon.flat_damage + " " + selectedWeapon.damage_type);
                }
                break;
            case AMMO:
                // Ammo
                selectedAmmo = characterManager.getItem(characterManager.getAmmo());
                if (selectedAmmo != null) {
//                    mModifyElementText = (TextView) findViewById(R.id.value_ammo);
//                    mModifyElementText.setText("x" + selectedAmmo.amount);
//                    mModifyElementText = (TextView) findViewById(R.id.type_ammo);
//                    mModifyElementText.setText(" " + selectedAmmo.name);
                }
                break;
            case ALL:
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

    /* OnTouch Listener */
    View.OnTouchListener viewTouched = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if(mDoubleClickTimer != null) {
                        mSelectedView = v;
                        showModifyElementDialog(v);
                    }
                    else{
                        mDoubleClickTimer = new CountDownTimer(DOUBLE_TAP_TIME,DOUBLE_TAP_TIME) {
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
}
