package com.dnd.alynchos.dndcharactertracker.Items;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dnd.alynchos.dndcharactertracker.Character.CharacterManager;
import com.dnd.alynchos.dndcharactertracker.Debug.Logger;
import com.dnd.alynchos.dndcharactertracker.Items.Weapons.Weapon;
import com.dnd.alynchos.dndcharactertracker.R;
import com.dnd.alynchos.dndcharactertracker.SaveData.FeedReaderDbHelper;

public class InventoryFragment extends Fragment implements View.OnClickListener {

    /* Debugging */
    private static final String TAG = InventoryFragment.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    /* View UI */
    private ListView mInventoryList;
    private Button mButtonInventoryAdd;
    private Button mButtonInventoryDelete;
    private TextView mTextViewCarryCurrent, mTextViewCarryMax;

    private ArrayAdapter<String> mInventoryArrayAdapter;

    private AlertDialog mActiveAlertDialog;

    // Add/Modify/Delete Inventory Item Stuff
    private View mInventoryAddView;
    private EditText mAddInvName, mAddInvAmount, mAddInvWeight, mAddInvGold, mAddInvDiceNum, mAddInvDiceSize, mAddInvFlatDam, mAddInvRange, mAddInvDamType;
    private Item itemBeingModified;

    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // List
        CharacterManager characterManager = CharacterManager.getInstance();
        String names[] = characterManager.getInventoryItemNames();
        mInventoryArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                android.R.id.text1, names);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_layout, container, false);
        // Header
        mTextViewCarryCurrent = (TextView) view.findViewById(R.id.inv_carry_curr);
        mTextViewCarryMax = (TextView) view.findViewById(R.id.inv_carry_max);

        // Buttons
        mButtonInventoryAdd = (Button) view.findViewById(R.id.button_inventory_add);
        mButtonInventoryAdd.setOnClickListener(this);
        mButtonInventoryDelete = (Button) view.findViewById(R.id.button_inventory_delete);
        mButtonInventoryDelete.setOnClickListener(this);

        mInventoryList = (ListView) view.findViewById(R.id.inventory_list);
        mInventoryList.setAdapter(mInventoryArrayAdapter);
        mInventoryList.setOnItemClickListener(inventoryOnClickListener);
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

    @Override
    public void onResume() {
        super.onResume();
        updateInventoryList();
        updateHeader();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_inventory_add:
                showInventoryAddAlertDialog();
                break;
            case R.id.button_inventory_delete:
                showInventoryDeleteAlertDialog();
                break;
            case R.id.button_delete_item:
                showDeleteItemAlertDialog();
                break;
        }
    }

    /* Private Helpers */

    private void deleteItem(String name) {
        CharacterManager characterManager = CharacterManager.getInstance();
        characterManager.deleteItem(name);
    }

    /* End Private Helpers */

    /* Dialogs */

    /* Dialog Inits */

    private void initInventoryAddView() {
        mAddInvName = (EditText) mInventoryAddView.findViewById(R.id.add_inventory_name);
        mAddInvAmount = (EditText) mInventoryAddView.findViewById(R.id.add_inventory_amount);
        mAddInvWeight = (EditText) mInventoryAddView.findViewById(R.id.add_inventory_weight);
        mAddInvGold = (EditText) mInventoryAddView.findViewById(R.id.add_inventory_gold);
        mAddInvDiceNum = (EditText) mInventoryAddView.findViewById(R.id.add_inventory_dice_num);
        mAddInvDiceSize = (EditText) mInventoryAddView.findViewById(R.id.add_inventory_dice_size);
        mAddInvFlatDam = (EditText) mInventoryAddView.findViewById(R.id.add_inventory_flat_dam);
        mAddInvRange = (EditText) mInventoryAddView.findViewById(R.id.add_inventory_range);
        mAddInvDamType = (EditText) mInventoryAddView.findViewById(R.id.add_inventory_dam_type);
        final LinearLayout isWeaponView = (LinearLayout) mInventoryAddView.findViewById(R.id.inventory_weapon_list);
        final CheckBox isWeaponCheckBox = (CheckBox) mInventoryAddView.findViewById(R.id.inventory_is_weapon_checkbox);
        isWeaponCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isWeaponView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void fillInventoryModView() {
        mAddInvName.setText("" + itemBeingModified.name);
        mAddInvAmount.setText("" + itemBeingModified.amount);
        mAddInvWeight.setText("" + itemBeingModified.weight);
        mAddInvGold.setText("" + itemBeingModified.gold_value);
        if (itemBeingModified instanceof Weapon) {
            logger.debug("item to mod is a weapon");
            Weapon weapon = (Weapon) itemBeingModified;
            //TODO: FIX THIS WEAPON LOADING WITH NEW JSON FORMAT
//            mAddInvDiceNum.setText("" + weapon.dice_num);
//            mAddInvDiceSize.setText("" + weapon.dice_size);
//            mAddInvFlatDam.setText("" + weapon.flat_damage);
//            mAddInvRange.setText("" + weapon.range);
//            mAddInvDamType.setText("" + weapon.damage_type);
        }
        final Button buttonDeleteItem = (Button) mInventoryAddView.findViewById(R.id.button_delete_item);
        buttonDeleteItem.setOnClickListener(this);
    }

    /* End Dialog Inits */

    /* Dialog Show */

    private void showInventoryAddAlertDialog() {
        logger.debug("Showing Add Inventory Dialog");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mInventoryAddView = inflater.inflate(R.layout.add_inventory_item_layout, null);
        initInventoryAddView();
        mActiveAlertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Add Item")
                .setView(mInventoryAddView)
                .setPositiveButton(android.R.string.ok, addInventoryItem)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mActiveAlertDialog.show();
    }

    private void showInventoryModAlertDialog() {
        logger.debug("Showing Add Inventory Dialog");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mInventoryAddView = inflater.inflate(R.layout.mod_inventory_item_layout, null);
        initInventoryAddView();
        fillInventoryModView();
        mActiveAlertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.inventory_modify_item_title)
                .setView(mInventoryAddView)
                .setPositiveButton(android.R.string.ok, modifyItem)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mActiveAlertDialog.show();
    }

    private void showDeleteItemAlertDialog() {
        logger.debug("Showing Delete Item Dialog");
        AlertDialog deleteItemDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.inventory_delete_item_title)
                .setMessage(R.string.inventory_delete_item_message)
                .setPositiveButton(android.R.string.ok, deleteItem)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        deleteItemDialog.show();
    }

    private void showInventoryDeleteAlertDialog() {
        logger.debug("Showing Delete Inventory Dialog");
        mActiveAlertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Delete Inventory?!")
                .setPositiveButton(android.R.string.ok, deleteInventory)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mActiveAlertDialog.show();
    }

    /* End Dialog Show */

    /* Add inventory item */
    DialogInterface.OnClickListener addInventoryItem = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            final String addInvName = mAddInvName.getText().toString();
            final String addInvAmount = mAddInvAmount.getText().toString();
            final String addInvWeight = mAddInvWeight.getText().toString();
            final String addInvGold = mAddInvGold.getText().toString();
            final String addInvDiceNum = mAddInvDiceNum.getText().toString();
            final String addInvDiceSize = mAddInvDiceSize.getText().toString();
            final String addInvFlatDamage = mAddInvFlatDam.getText().toString();
            final String addInvRange = mAddInvRange.getText().toString();
            final String addInvDamType = mAddInvDamType.getText().toString();
            AsyncTask<Void, Void, Integer> task = (new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... voids) {
                    CharacterManager characterManager = CharacterManager.getInstance();
                    Item item = new Item();

                    // Extract data from dialog
                    item.name = addInvName;
                    try {
                        item.amount = Integer.parseInt(addInvAmount);
                    } catch (Exception e) {
                        logger.debug("Could not parse amount");
                        item.amount = 0;
                    }
                    try {
                        item.weight = Double.parseDouble(addInvWeight);
                    } catch (Exception e) {
                        logger.debug("Could not parse weight");
                        item.weight = 0;
                    }
                    try {
                        item.gold_value = Double.parseDouble(addInvGold);
                    } catch (Exception e) {
                        logger.debug("Could not parse gold");
                        item.gold_value = 0;
                    }
                    // Below if for weapon only
                    if (addInvDiceNum.length() > 0) {
                        Weapon weapon = new Weapon();
                        weapon.name = item.name;
                        weapon.amount = item.amount;
                        weapon.weight = item.weight;
                        weapon.gold_value = item.gold_value;
                        //TODO: FIX THIS WEAPON LOADING WITH NEW JSON FORMAT
//                        try {
//                            weapon.dice_num = Integer.parseInt(addInvDiceNum);
//                        } catch (Exception e) {
//                            logger.debug("Could not parse dice num");
//                            weapon.dice_num = 0;
//                        }
//                        try {
//                            weapon.dice_size = Integer.parseInt(addInvDiceSize);
//                        } catch (Exception e) {
//                            logger.debug("Could not parse dice size");
//                            weapon.dice_size = 0;
//                        }
//                        try {
//                            weapon.flat_damage = Integer.parseInt(addInvFlatDamage);
//                        } catch (Exception e) {
//                            logger.debug("Could not parse flat damage");
//                            weapon.flat_damage = 0;
//                        }
//                        try {
//                            weapon.range = Integer.parseInt(addInvRange);
//                        } catch (Exception e) {
//                            logger.debug("Could not parse range");
//                            weapon.range = 0;
//                        }
//                        try {
//                            weapon.damage_type = addInvDamType;
//                        } catch (Exception e) {
//                            logger.debug("Could not parse damage type");
//                            weapon.damage_type = "";
//                        }
                        characterManager.addItemToInventory(weapon);
                    } else {
                        characterManager.addItemToInventory(item);
                    }
                    return 1;
                }

                @Override
                protected void onPostExecute(Integer count) {
                    Intent intent = new Intent();
                    intent.setAction(CharacterManager.UPDATE_INV_UI);
                    getActivity().sendBroadcast(intent);
                }
            });
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };

    /* Modify inventory item */
    DialogInterface.OnClickListener modifyItem = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            CharacterManager characterManager = CharacterManager.getInstance();
            Item newItem = new Item();
            newItem.name = mAddInvName.getText().toString();
            try {
                newItem.amount = Integer.parseInt(mAddInvAmount.getText().toString());
            } catch (Exception e) {
                logger.debug("Could not parse amount");
                newItem.amount = 0;
            }
            try {
                newItem.weight = Double.parseDouble(mAddInvWeight.getText().toString());
            } catch (Exception e) {
                logger.debug("Could not parse weight");
                newItem.weight = 0;
            }
            try {
                newItem.gold_value = Double.parseDouble(mAddInvGold.getText().toString());
            } catch (Exception e) {
                logger.debug("Could not parse gold");
                newItem.gold_value = 0;
            }
            if (itemBeingModified instanceof Weapon) {
                Weapon weapon = new Weapon(newItem);
                //TODO: FIX THIS WEAPON LOADING WITH NEW JSON FORMAT
//                try {
//                    weapon.dice_num = Integer.parseInt(mAddInvDiceNum.getText().toString());
//                } catch (Exception e) {
//                    logger.debug("Could not parse dice_num");
//                    newItem.gold_value = 0;
//                }
//                try {
//                    weapon.dice_size = Integer.parseInt(mAddInvDiceSize.getText().toString());
//                } catch (Exception e) {
//                    logger.debug("Could not parse dice_size");
//                    newItem.gold_value = 0;
//                }
//                try {
//                    weapon.flat_damage = Integer.parseInt(mAddInvFlatDam.getText().toString());
//                } catch (Exception e) {
//                    logger.debug("Could not parse flat_damage");
//                    newItem.gold_value = 0;
//                }
//                try {
//                    weapon.range = Integer.parseInt(mAddInvRange.getText().toString());
//                } catch (Exception e) {
//                    logger.debug("Could not parse range");
//                    newItem.gold_value = 0;
//                }
//                weapon.damage_type = mAddInvDamType.getText().toString();

                characterManager.modItem(itemBeingModified.name, weapon);
            } else {
                characterManager.modItem(itemBeingModified.name, newItem);
            }
            updateInventoryList();
            updateHeader();
        }
    };

    /* Delete Item */
    DialogInterface.OnClickListener deleteItem = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AsyncTask<Void, Void, Integer> task = (new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... voids) {
                    deleteItem(itemBeingModified.name);
                    itemBeingModified = null;
                    return 1;
                }

                @Override
                protected void onPostExecute(Integer count) {
                    Intent intent = new Intent();
                    intent.setAction(CharacterManager.UPDATE_INV_UI);
                    getActivity().sendBroadcast(intent);
                    mActiveAlertDialog.cancel();
                }
            });
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };

    /* Delete Inventory */
    DialogInterface.OnClickListener deleteInventory = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            CharacterManager characterManager = CharacterManager.getInstance();
            characterManager.deleteInventory();
            Intent intent = new Intent();
            intent.setAction(CharacterManager.CLEAR_INV);
            getActivity().sendBroadcast(intent);
        }
    };

    // On click listener for the Inventory List
    AdapterView.OnItemClickListener inventoryOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CharacterManager characterManager = CharacterManager.getInstance();
            String name = (String) parent.getItemAtPosition(position);
            itemBeingModified = characterManager.getItem(name);
            if (itemBeingModified == null) {
                logger.debug("Could not find item!");
                return;
            }
            showInventoryModAlertDialog();
        }
    };

    /* End Dialogs */

    /* Update UI Components */

    public void updateHeader() {
        CharacterManager characterManager = CharacterManager.getInstance();
        // Carry Weight
        mTextViewCarryMax.setText("/ " + characterManager.getCarryWeightMax());
        mTextViewCarryCurrent.setText("" + characterManager.getCarryWeight());
    }

    public void updateInventoryList() {
        CharacterManager characterManager = CharacterManager.getInstance();
        String names[] = characterManager.getInventoryItemNames();

        mInventoryArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                android.R.id.text1, names);

        mInventoryList.setAdapter(mInventoryArrayAdapter);
    }

    /* End Update UI Components  */
}
