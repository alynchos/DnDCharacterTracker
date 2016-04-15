package com.dnd.alynchos.dndcharactertracker.Items;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
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

import com.dnd.alynchos.dndcharactertracker.Character.BaseCharacter;
import com.dnd.alynchos.dndcharactertracker.Character.CharacterManager;
import com.dnd.alynchos.dndcharactertracker.Debug.Logger;
import com.dnd.alynchos.dndcharactertracker.Items.Weapons.Weapon;
import com.dnd.alynchos.dndcharactertracker.R;
import com.dnd.alynchos.dndcharactertracker.SaveData.FeedReaderDbHelper;

public class InventoryActivity extends Activity implements View.OnClickListener {

    /* Debugging */
    private static final String TAG = InventoryActivity.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    /* View UI */
    private ListView mInventoryList;
    private Button mButtonInventoryAdd;
    private Button mButtonInventoryDelete;
    private Button mButtonAddGold;
    private TextView mTextViewMoney[] = new TextView[5];
    private TextView mTextViewCarryCurrent, mTextViewCarryMax;

    private Activity mActivity;

    private ArrayAdapter<String> mInventoryArrayAdapter;

    private AlertDialog mActiveAlertDialog;

    // Add/Modify/Delete Inventory Item Stuff
    private View mInventoryAddView;
    private EditText mAddInvName, mAddInvAmount, mAddInvWeight, mAddInvGold, mAddInvDiceNum, mAddInvDiceSize, mAddInvFlatDam, mAddInvRange, mAddInvDamType;
    private Item itemBeingModified;
    private Button mButtonDeleteItem;

    // Add/Remove Moneyyyzzz
    private EditText mMoney[] = new EditText[5];
    private View mAddMoneyView;

    boolean updateUIRegistered = false;

    /**
     * Broadcast receiver listening to Personal Status updates from VPTabBarActivity
     */
    private BroadcastReceiver updateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            if (intent.getAction().equals(CharacterManager.UPDATE_INV_UI)) {
                logger.debug("Update Inventory UI from broadcast");
                updateInventoryList();
                updateHeader();
                Toast.makeText(mActivity, "Inventory Updated", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(CharacterManager.CLEAR_INV)) {
                logger.debug("Clear Inventory broadcast");
                updateInventoryList();
                updateHeader();
                Toast.makeText(mActivity, "Inventory Deleted", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.debug("onCreate");
        setContentView(R.layout.inventory_layout);
        mActivity = this;
        CharacterManager characterManager = CharacterManager.getInstance();

        // Header
        mTextViewMoney[0] = (TextView) findViewById(R.id.inv_c_amount);
        mTextViewMoney[1] = (TextView) findViewById(R.id.inv_s_amount);
        mTextViewMoney[2] = (TextView) findViewById(R.id.inv_e_amount);
        mTextViewMoney[3] = (TextView) findViewById(R.id.inv_g_amount);
        mTextViewMoney[4] = (TextView) findViewById(R.id.inv_p_amount);
        mTextViewCarryCurrent = (TextView) findViewById(R.id.inv_carry_curr);
        mTextViewCarryMax = (TextView) findViewById(R.id.inv_carry_max);

        // Buttons
        mButtonInventoryAdd = (Button) findViewById(R.id.button_inventory_add);
        mButtonInventoryAdd.setOnClickListener(this);
        mButtonInventoryDelete = (Button) findViewById(R.id.button_inventory_delete);
        mButtonInventoryDelete.setOnClickListener(this);
        mButtonAddGold = (Button) findViewById(R.id.button_add_gold);
        mButtonAddGold.setOnClickListener(this);

        // List
        String names[] = characterManager.getInventoryItemNames();
        mInventoryArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, names);

        mInventoryList = (ListView) findViewById(R.id.inventory_list);
        mInventoryList.setAdapter(mInventoryArrayAdapter);
        mInventoryList.setOnItemClickListener(inventoryOnClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        logger.debug("onResume");
        updateInventoryList();
        updateHeader();
        // Register the Ps update intent listener
        if (!updateUIRegistered) {
            registerReceiver(updateUIReceiver, new IntentFilter(CharacterManager.UPDATE_INV_UI));
            registerReceiver(updateUIReceiver, new IntentFilter(CharacterManager.CLEAR_INV));
            updateUIRegistered = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        logger.debug("onPause");
        if (updateUIRegistered) {
            unregisterReceiver(updateUIReceiver);
            updateUIRegistered = false;
        }
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
                deleteItem(itemBeingModified.name);
                updateHeader();
                updateInventoryList();
                itemBeingModified = null;
                mActiveAlertDialog.cancel();
                break;
            case R.id.button_add_gold:
                showAddGoldAlertDialog();
                break;
        }
    }

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

    private void updateHeader() {
        CharacterManager characterManager = CharacterManager.getInstance();
        // Carry Weight
        mTextViewCarryMax.setText("/ " + characterManager.getCarryWeightMax());
        mTextViewCarryCurrent.setText("" + characterManager.getCarryWeight());
        // Moneyyyyz
        int temp[] = characterManager.getCurrentMoney();
        for (int i = 0; i < temp.length; i++) {
            if (i < (temp.length - 1))
                mTextViewMoney[i].setText(temp[i] + ".");
            else
                mTextViewMoney[i].setText("" + temp[i]);
        }
    }

    private void updateInventoryList() {
        CharacterManager characterManager = CharacterManager.getInstance();
        String names[] = characterManager.getInventoryItemNames();

        mInventoryArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, names);

        mInventoryList.setAdapter(mInventoryArrayAdapter);
    }

    private void deleteItem(String name) {
        CharacterManager characterManager = CharacterManager.getInstance();
        characterManager.deleteItem(name);
    }

    private void showInventoryModAlertDialog() {
        logger.debug("Showing Add Inventory Dialog");
        LayoutInflater inflater = getLayoutInflater();
        mInventoryAddView = inflater.inflate(R.layout.mod_inventory_item_layout, null);
        mButtonDeleteItem = (Button) mInventoryAddView.findViewById(R.id.button_delete_item);
        mButtonDeleteItem.setOnClickListener(this);
        initInventoryAddView();
        fillInventoryModView();
        mActiveAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Modify Item")
                .setView(mInventoryAddView)
                .setPositiveButton(android.R.string.ok, modifyItem)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mActiveAlertDialog.show();
    }

    private void showAddGoldAlertDialog() {
        logger.debug("Showing Add Money Dialog");
        LayoutInflater inflater = getLayoutInflater();
        mAddMoneyView = inflater.inflate(R.layout.add_money_layout, null);
        initAddMoneyView();
        mActiveAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Modify Gold")
                .setView(mAddMoneyView)
                .setPositiveButton("Add", addGold)
                .setNegativeButton("Remove", removeGold)
                .create();
        mActiveAlertDialog.show();
    }

    private void showInventoryAddAlertDialog() {
        logger.debug("Showing Add Inventory Dialog");
        LayoutInflater inflater = getLayoutInflater();
        mInventoryAddView = inflater.inflate(R.layout.add_inventory_item_layout, null);
        initInventoryAddView();
        mActiveAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Add Item")
                .setView(mInventoryAddView)
                .setPositiveButton(android.R.string.ok, addInventoryItem)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mActiveAlertDialog.show();
    }

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
    }

    private void initAddMoneyView(){
        mMoney[0] = (EditText) mAddMoneyView.findViewById(R.id.add_money_copper);
        mMoney[1] = (EditText) mAddMoneyView.findViewById(R.id.add_money_silver);
        mMoney[2] = (EditText) mAddMoneyView.findViewById(R.id.add_money_electrum);
        mMoney[3] = (EditText) mAddMoneyView.findViewById(R.id.add_money_gold);
        mMoney[4] = (EditText) mAddMoneyView.findViewById(R.id.add_money_plat);
    }

    private void fillInventoryModView() {
        mAddInvName.setText("" + itemBeingModified.name);
        mAddInvAmount.setText("" + itemBeingModified.amount);
        mAddInvWeight.setText("" + itemBeingModified.weight);
        mAddInvGold.setText("" + itemBeingModified.gold_value);
        if (itemBeingModified instanceof Weapon) {
            logger.debug("item to mod is a weapon");
            Weapon weapon = (Weapon) itemBeingModified;
            mAddInvDiceNum.setText("" + weapon.dice_num);
            mAddInvDiceSize.setText("" + weapon.dice_size);
            mAddInvFlatDam.setText("" + weapon.flat_damage);
            mAddInvRange.setText("" + weapon.range);
            mAddInvDamType.setText("" + weapon.damage_type);
        }
    }

    private void showInventoryDeleteAlertDialog() {
        logger.debug("Showing Delete Inventory Dialog");
        mActiveAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Inventory?!")
                .setPositiveButton(android.R.string.ok, deleteInventory)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mActiveAlertDialog.show();
    }

    // Only call after initializing the add money view!!
    private void addMoney(boolean isPos){
        CharacterManager characterManager = CharacterManager.getInstance();
        int money[] = new int[5];
        for(int i =0; i < money.length; i++) {
            try {
                money[i] = Integer.parseInt(mMoney[i].getText().toString());
            } catch (Exception e) {
                logger.debug("could not parse add money: " + i);
                money[i] = 0;
            }
            if(isPos) characterManager.addMoney(money[i], i);
            else characterManager.addMoney(-money[i], i);
        }
    }

    /* Add an inventory item */
    DialogInterface.OnClickListener addInventoryItem = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AsyncTask<Void, Void, Integer> task = (new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... voids) {
                    CharacterManager characterManager = CharacterManager.getInstance();
                    Item item = new Item();
                    Weapon weapon = new Weapon();
                    // Extract data from dialog
                    item.name = mAddInvName.getText().toString();
                    try {
                        item.amount = Integer.parseInt(mAddInvAmount.getText().toString());
                    } catch (Exception e) {
                        logger.debug("Could not parse amount");
                        item.amount = 0;
                    }
                    try {
                        item.weight = Double.parseDouble(mAddInvWeight.getText().toString());
                    } catch (Exception e) {
                        logger.debug("Could not parse weight");
                        item.weight = 0;
                    }
                    try {
                        item.gold_value = Double.parseDouble(mAddInvGold.getText().toString());
                    } catch (Exception e) {
                        logger.debug("Could not parse gold");
                        item.gold_value = 0;
                    }
                    // Below if for weapon only
                    String dice_num = mAddInvDiceNum.getText().toString();
                    if (dice_num.length() > 0) {
                        weapon.name = item.name;
                        weapon.amount = item.amount;
                        weapon.weight = item.weight;
                        weapon.gold_value = item.gold_value;
                        try {
                            weapon.dice_num = Integer.parseInt(dice_num);
                        } catch (Exception e) {
                            logger.debug("Could not parse dice num");
                            weapon.dice_num = 0;
                        }
                        try {
                            weapon.dice_size = Integer.parseInt(mAddInvDiceSize.getText().toString());
                        } catch (Exception e) {
                            logger.debug("Could not parse dice size");
                            weapon.dice_size = 0;
                        }
                        try {
                            weapon.flat_damage = Integer.parseInt(mAddInvFlatDam.getText().toString());
                        } catch (Exception e) {
                            logger.debug("Could not parse flat damage");
                            weapon.flat_damage = 0;
                        }
                        try {
                            weapon.range = Integer.parseInt(mAddInvRange.getText().toString());
                        } catch (Exception e) {
                            logger.debug("Could not parse range");
                            weapon.range = 0;
                        }
                        try {
                            weapon.damage_type = mAddInvDamType.getText().toString();
                        } catch (Exception e) {
                            logger.debug("Could not parse damage type");
                            weapon.damage_type = "";
                        }
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
                    mActivity.sendBroadcast(intent);
                    return;
                }
            });
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };

    /* Adding Gold */
    DialogInterface.OnClickListener addGold = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            addMoney(true);
            updateInventoryList();
            updateHeader();
        }
    };

    /* Removing Gold */
    DialogInterface.OnClickListener removeGold = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            addMoney(false);
            updateInventoryList();
            updateHeader();
        }
    };

    /* Modify an item */
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
                try {
                    weapon.dice_num = Integer.parseInt(mAddInvDiceNum.getText().toString());
                } catch (Exception e) {
                    logger.debug("Could not parse dice_num");
                    newItem.gold_value = 0;
                }
                try {
                    weapon.dice_size = Integer.parseInt(mAddInvDiceSize.getText().toString());
                } catch (Exception e) {
                    logger.debug("Could not parse dice_size");
                    newItem.gold_value = 0;
                }
                try {
                    weapon.flat_damage = Integer.parseInt(mAddInvFlatDam.getText().toString());
                } catch (Exception e) {
                    logger.debug("Could not parse flat_damage");
                    newItem.gold_value = 0;
                }
                try {
                    weapon.range = Integer.parseInt(mAddInvRange.getText().toString());
                } catch (Exception e) {
                    logger.debug("Could not parse range");
                    newItem.gold_value = 0;
                }
                weapon.damage_type = mAddInvDamType.getText().toString();

                characterManager.modItem(itemBeingModified.name, weapon);
            } else {
                characterManager.modItem(itemBeingModified.name, newItem);
            }
            updateInventoryList();
            updateHeader();
        }
    };

    /* Delete Inventory */
    DialogInterface.OnClickListener deleteInventory = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AsyncTask<Void, Void, Integer> task = (new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... voids) {
                    FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
                    feedReaderDbHelper.deleteInventoryDataBase();
                    CharacterManager characterManager = CharacterManager.getInstance();
                    characterManager.deleteInventory();
                    return 1;
                }

                @Override
                protected void onPostExecute(Integer count) {
                    Intent intent = new Intent();
                    intent.setAction(CharacterManager.CLEAR_INV);
                    mActivity.sendBroadcast(intent);
                    return;
                }
            });
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };
}
