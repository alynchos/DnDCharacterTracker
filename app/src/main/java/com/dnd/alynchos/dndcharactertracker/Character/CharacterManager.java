package com.dnd.alynchos.dndcharactertracker.Character;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dnd.alynchos.dndcharactertracker.Debug.Logger;
import com.dnd.alynchos.dndcharactertracker.Items.Item;
import com.dnd.alynchos.dndcharactertracker.Items.Weapons.Weapon;
import com.dnd.alynchos.dndcharactertracker.SaveData.FeedReaderDbHelper;

import java.util.LinkedList;

/**
 * Created by Alex Lynchosky on 12/22/2014.
 */
public class CharacterManager {

    /* Debugging */
    private static final String TAG = CharacterManager.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    // Intent Broadcasts
    public static final String UPDATE_UI = "com.dnd.alynchos.UPDATE_UI";
    public static final String UPDATE_INV_UI = "com.dnd.alynchos.UPDATE_INV_UI";
    public static final String CLEAR_CHAR = "com.dnd.alynchos.CLEAR_CHAR";
    public static final String CLEAR_INV = "com.dnd.alynchos.CLEAR_INV";
    public static final String HIDE_KEYBOARD = "com.dnd.alynchos.HIDE_KEYBOARD";
    public static final String SHOW_KEYBOARD = "com.dnd.alynchos.HIDE_KEYBOARD";

    // Intent Extras
    public static final String KEYBOARD_VIEW = "viewToHideFrom";

    // Constants
    public static final int ACR = 0, ANI = 1, ARC = 2, ATH = 3, DEC = 4, HIS = 5, INS = 6,
            INTI = 7, INV = 8, MED = 9, NAT = 10, PER = 11, PERF = 12, PERS = 13,
            REL = 14, SLE = 15, STE = 16, SUR = 17;

    public enum CurrencyType {
        copper,
        silver,
        gold,
        platinum
    }

    // Keep track of level steps
    public static final int[] LEVEL_CAPS = {300, 900, 2700, 6500};

    private BaseCharacter mCharacter;
    static CharacterManager mCharacterManager;

    public CharacterManager() {
        mCharacterManager = this;
    }

    public static CharacterManager getInstance() {
        if (mCharacterManager == null) {
            mCharacterManager = new CharacterManager();
        }
        return mCharacterManager;
    }

    /**
     * *************************************************************
     * ***************** Public Setter Functions *********************
     * **************************************************************
     */

    public void addExperience(int val) {
        mCharacter.experience += val;
    }

    public void setAbilities(int abilities[]) {
        if (abilities.length != 6) {
            logger.debug("Incorrect ability array length: " + abilities.length);
            return;
        }
        mCharacter.str = abilities[0];
        mCharacter.dex = abilities[1];
        mCharacter.con = abilities[2];
        mCharacter.intel = abilities[3];
        mCharacter.wis = abilities[4];
        mCharacter.chr = abilities[5];
    }

    public void setAmmo(String name) {
        mCharacter.ammo = name;
    }

    public void setArmor(int armor) {
        mCharacter.armor = armor;
    }

    public void setCharacter(BaseCharacter set) {
        mCharacter = set;
    }

    public void setExperience(int val) {
        mCharacter.experience = val;
    }

    public void setHealth(int health) {
        mCharacter.health = health;
    }

    public void setAlignment(String val) {
        mCharacter.align = val;
    }

    public void setBackground(String val) {
        mCharacter.background = val;
    }

    public void setClass_(String val) {
        mCharacter.class_ = val;
    }

    public void setName(String val) {
        mCharacter.name = val;
    }

    public void setRace(String val) {
        mCharacter.race = val;
    }

    public void setInitiative(int initiative) {
        mCharacter.initiative = initiative;
    }

    public void setNotes(String set){mCharacter.notes = set;}

    public void setProficiency(int proficiency) {
        mCharacter.proficiency = proficiency;
    }

    public void setSavingThrows(int savingThrows[]) {
        if (savingThrows.length != 6) {
            logger.debug("Incorrect saving throws array length: " + savingThrows.length);
            return;
        }
        for (int i = 0; i < savingThrows.length; i++) {
            mCharacter.saveProf[i] = savingThrows[i];
        }
    }

    public void setSkillProf(BaseCharacter.Skill skill, int val) {
        mCharacter.setSkillProf(skill, val);
    }

    public void setSkillsProfs(int skillProfs[]) {
        mCharacter.setSkillProfs(skillProfs);
    }

    public void setSpeed(int speed) {
        mCharacter.speed = speed;
    }

    public void setWeapon(String name, int atk_bns, int num) {
        mCharacter.attack_bonus[num] = atk_bns;
        mCharacter.weapons[num] = name;
    }

    /**
     * *************************************************************
     * ***************** Public Getter Functions *********************
     * **************************************************************
     */

    // Combat
    public int getArmor() {
        return mCharacter.armor;
    }

    public String getAmmo() {
        return mCharacter.ammo;
    }

    public int getAttackBonus(int num) {
        return mCharacter.attack_bonus[num];
    }

    public int getHealth() {
        return mCharacter.health;
    }

    public int getInitiative() {
        return mCharacter.initiative;
    }

    public int getSpeed() {
        return mCharacter.speed;
    }

    public String getWeaponName(int num) {
        return mCharacter.weapons[num];
    }

    public BaseCharacter getCharacter() {
        return mCharacter;
    }

    // Identity
    public String getName() {
        if(mCharacter.name == null) mCharacter.name = " ";
        return mCharacter.name;
    }

    public String getRace() {
        if(mCharacter.race == null) mCharacter.race = " ";
        return mCharacter.race;
    }

    public String getClass_() {
        if(mCharacter.class_ == null) mCharacter.class_ = " ";
        return mCharacter.class_;
    }

    public String getAlign() {
        if(mCharacter.align == null) mCharacter.align = " ";
        return mCharacter.align;
    }

    public String getBackground() {
        if(mCharacter.background == null) mCharacter.background = " ";
        return mCharacter.background;
    }

    public String getNotes(){
        if(mCharacter.notes == null) mCharacter.notes = " ";
        return mCharacter.notes;
    }

    // Abilities
    public int[] getAbilities() {
        int stats[] = new int[6];
        stats[0] = mCharacter.str;
        stats[1] = mCharacter.dex;
        stats[2] = mCharacter.con;
        stats[3] = mCharacter.intel;
        stats[4] = mCharacter.wis;
        stats[5] = mCharacter.chr;
        return stats;
    }

    public int[] getAbilityModifiers() {
        int mods[] = new int[6];
        mods[0] = mCharacter.getAbilityModifier(BaseCharacter.Stat.Strength);
        mods[1] = mCharacter.getAbilityModifier(BaseCharacter.Stat.Dexterity);
        mods[2] = mCharacter.getAbilityModifier(BaseCharacter.Stat.Constitution);
        mods[3] = mCharacter.getAbilityModifier(BaseCharacter.Stat.Intelligence);
        mods[4] = mCharacter.getAbilityModifier(BaseCharacter.Stat.Wisdom);
        mods[5] = mCharacter.getAbilityModifier(BaseCharacter.Stat.Charisma);
        return mods;
    }

    // Skills
    public int[] getSkillModifiers() {
        int mods[] = new int[18];
        mods[0] = mCharacter.getSkillMod(BaseCharacter.Stat.Dexterity, BaseCharacter.Skill.Acrobatics);
        mods[1] = mCharacter.getSkillMod(BaseCharacter.Stat.Wisdom, BaseCharacter.Skill.AnimalHandling);
        mods[2] = mCharacter.getSkillMod(BaseCharacter.Stat.Intelligence, BaseCharacter.Skill.Arcana);
        mods[3] = mCharacter.getSkillMod(BaseCharacter.Stat.Strength, BaseCharacter.Skill.Athletics);
        mods[4] = mCharacter.getSkillMod(BaseCharacter.Stat.Charisma, BaseCharacter.Skill.Deception);
        mods[5] = mCharacter.getSkillMod(BaseCharacter.Stat.Intelligence, BaseCharacter.Skill.History);
        mods[6] = mCharacter.getSkillMod(BaseCharacter.Stat.Wisdom, BaseCharacter.Skill.Insight);
        mods[7] = mCharacter.getSkillMod(BaseCharacter.Stat.Charisma, BaseCharacter.Skill.Intimidation);
        mods[8] = mCharacter.getSkillMod(BaseCharacter.Stat.Intelligence, BaseCharacter.Skill.Investigation);
        mods[9] = mCharacter.getSkillMod(BaseCharacter.Stat.Wisdom, BaseCharacter.Skill.Medicine);
        mods[10] = mCharacter.getSkillMod(BaseCharacter.Stat.Intelligence, BaseCharacter.Skill.Nature);
        mods[11] = mCharacter.getSkillMod(BaseCharacter.Stat.Wisdom, BaseCharacter.Skill.Perception);
        mods[12] = mCharacter.getSkillMod(BaseCharacter.Stat.Charisma, BaseCharacter.Skill.Performance);
        mods[13] = mCharacter.getSkillMod(BaseCharacter.Stat.Charisma, BaseCharacter.Skill.Persuasion);
        mods[14] = mCharacter.getSkillMod(BaseCharacter.Stat.Intelligence, BaseCharacter.Skill.Religion);
        mods[15] = mCharacter.getSkillMod(BaseCharacter.Stat.Dexterity, BaseCharacter.Skill.SleightOfHand);
        mods[16] = mCharacter.getSkillMod(BaseCharacter.Stat.Dexterity, BaseCharacter.Skill.Stealth);
        mods[17] = mCharacter.getSkillMod(BaseCharacter.Stat.Wisdom, BaseCharacter.Skill.Survival);
        return mods;
    }

    public int[] getSkillProfs() {
        return mCharacter.skillProf;
    }

    public int getProficiency() {
        return mCharacter.proficiency;
    }

    public int[] getSaveProfs() {
        return mCharacter.saveProf;
    }

    public int[] getSaveMods() {
        int mods[] = new int[6];
        mods[0] = mCharacter.getSavingThrow(BaseCharacter.Stat.Strength);
        mods[1] = mCharacter.getSavingThrow(BaseCharacter.Stat.Dexterity);
        mods[2] = mCharacter.getSavingThrow(BaseCharacter.Stat.Constitution);
        mods[3] = mCharacter.getSavingThrow(BaseCharacter.Stat.Intelligence);
        mods[4] = mCharacter.getSavingThrow(BaseCharacter.Stat.Wisdom);
        mods[5] = mCharacter.getSavingThrow(BaseCharacter.Stat.Charisma);
        return mods;
    }

    // Experience
    public int getExperience() {
        return mCharacter.experience;
    }

    // Progress towards next level
    public int getExperienceProgress() {
        int prog = mCharacter.experience;
        int level = mCharacter.level;
        if (level > 1) prog -= LEVEL_CAPS[level - 2];
        return prog;
    }

    public int getLevel() {
        return mCharacter.level;
    }

    // Returns the amount of experience needed for the next level
    // Also raises the level of the character!
    public int getNextLevelCap() {
        int cap = 0;
        mCharacter.level++;
        int index = mCharacter.level - 1;
        if (index >= LEVEL_CAPS.length) {
            logger.debug("You reached the max level in my code. gg");
            return cap;
        }
        if (index > 0) {
            cap = LEVEL_CAPS[index] - LEVEL_CAPS[index - 1];
        } else {
            cap = LEVEL_CAPS[index];
        }
        return cap;
    }


    /*
     *  Inventory Management
     */
    public void addItemToInventory(final Item item) {
        mCharacter.addItemToInventory(item);
        AsyncTask<Void, Void, Integer> task = (new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
                String table = FeedReaderDbHelper.FeedEntry.TABLE_INVENTORY;
                int array_size = 5;
                Weapon weapon = new Weapon();
                if (item instanceof Weapon) {
                    logger.debug("New item is a weapon");
                    weapon = (Weapon) item;
                    array_size = 10;
                }
                String columns[] = new String[array_size];
                Object data[] = new Object[array_size];
                columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_NAME;
                data[0] = item.name;
                columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_AMOUNT;
                data[1] = new Integer(item.amount);
                columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_GOLD;
                data[2] = new Double(item.gold_value);
                columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_WEIGHT;
                data[3] = new Double(item.weight);
                columns[4] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_DESC;
                data[4] = item.desc;
                if (array_size > 5) {
                    columns[5] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_DICE_NUM;
                    data[5] = new Integer(weapon.dice_num);
                    columns[6] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_DICE_SIZE;
                    data[6] = new Integer(weapon.dice_size);
                    columns[7] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_DAM_FLAT;
                    data[7] = new Integer(weapon.flat_damage);
                    columns[8] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_RANGE;
                    data[8] = new Integer(weapon.range);
                    columns[9] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_DAM_TYPE;
                    data[9] = weapon.damage_type;
                }
                feedReaderDbHelper.insertData(table, columns, data);
                return 1;
            }

            @Override
            protected void onPostExecute(Integer count) {
                return;
            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setCurrency(int amount, CurrencyType type) {
        switch (type) {
            case copper:
                if (amount < 0) mCharacter.copper = 0;
                else mCharacter.copper = amount;
                break;
            case silver:
                if (amount < 0) mCharacter.silver = 0;
                else mCharacter.silver = amount;
                break;
            case gold:
                if (amount < 0) mCharacter.gold = 0;
                else mCharacter.gold = amount;
                break;
            case platinum:
                if (amount < 0) mCharacter.plat = 0;
                else mCharacter.plat = amount;
                break;
            default:
                logger.error("Improper currency type: " + type);
                break;
        }
    }

    public void deleteInventory() {
        mCharacter.deleteInventory();
    }

    public void deleteItem(String name) {
        deleteItemDatabase(name);
        mCharacter.deleteItem(name);
    }

    public int getCarryWeightMax() {
        return mCharacter.getCarryCap();
    }

    public int getCarryWeight() {
        return mCharacter.getCarryWeight();
    }

    public int[] getCurrency() {
        return new int[]{mCharacter.copper, mCharacter.silver, mCharacter.gold, mCharacter.plat};
    }

    public String[] getInventoryItemNames() {
        String names[] = new String[mCharacter.inventory.size()];
        String set[] = mCharacter.inventory.keySet().toArray(new String[names.length]);
        for (int i = 0; i < names.length; i++) {
            names[i] = set[i];
        }
        return names;
    }

    // Return either the list of weapons, or the list of everything except weapons
    public String[] getInventoryWeaponNames(boolean wantWeapons) {
        LinkedList<Weapon> weapons = new LinkedList<>();
        LinkedList<Item> items = new LinkedList<>();
        Item list[] = mCharacter.inventory.values().toArray(new Item[mCharacter.inventory.size()]);
        // Find the weapons
        for (int i = 0; i < list.length; i++) {
            if(list[i] instanceof Weapon){
                if (wantWeapons) {
                    weapons.add((Weapon)list[i]);
                } else {
                    continue;
                }
            }
            else{
                if (wantWeapons) {
                    continue;
                } else {
                    items.add(list[i]);
                }
            }
        }
        String names[];
        if(wantWeapons){
            names = new String[weapons.size()];
            for (int i = 0; i < names.length; i++) {
                names[i] = weapons.get(i).name;
            }
        }
        else{
            names = new String[items.size()];
            for (int i = 0; i < names.length; i++) {
                names[i] = items.get(i).name;
            }
        }
        return names;
    }


    public Item getItem(String name) {
        return mCharacter.getItem(name);
    }

    public void modItem(String name, Item item) {
        modifyItemDatabase(name, item);
        mCharacter.modItem(name, item);
    }

    /**
     * ***************************************************
     * ****************** Saving Data ********************
     * ***************************************************
     * *****Call all these functions in an Async Task*****
     */

    private void deleteItemDatabase(final String name) {
        AsyncTask<Void, Void, Integer> task = (new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
                feedReaderDbHelper.deleteItemData(FeedReaderDbHelper.FeedEntry.TABLE_INVENTORY, name);
                return 1;
            }

            @Override
            protected void onPostExecute(Integer count) {
                logger.debug("Item successfully deleted");
            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void modifyItemDatabase(final String name, final Item item) {
        AsyncTask<Void, Void, Integer> task = (new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
                int array_length;
                if (item instanceof Weapon) {
                    array_length = 10;
                } else {
                    array_length = 5;
                }
                Object data[] = new Object[array_length];
                String columns[] = new String[array_length];
                columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_WEIGHT;
                data[0] = new Double(item.weight);
                columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_GOLD;
                data[1] = new Double(item.gold_value);
                columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_DESC;
                data[2] = item.desc;
                columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_NAME;
                data[3] = item.name;
                columns[4] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_AMOUNT;
                data[4] = new Integer(item.amount);
                if (array_length > 5) {
                    Weapon weapon = (Weapon) item;
                    columns[5] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_DICE_NUM;
                    data[5] = new Integer(weapon.dice_num);
                    columns[6] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_DICE_SIZE;
                    data[6] = new Integer(weapon.dice_size);
                    columns[7] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_DAM_FLAT;
                    data[7] = new Integer(weapon.flat_damage);
                    columns[8] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_RANGE;
                    data[8] = new Integer(weapon.range);
                    columns[9] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_DAM_TYPE;
                    data[9] = weapon.damage_type;
                }
                String whereColumns[] = new String[]{FeedReaderDbHelper.FeedEntry.COLUMN_INV_NAME};
                Object whereArgs[] = new Object[]{name};
                feedReaderDbHelper.updateData(FeedReaderDbHelper.FeedEntry.TABLE_INVENTORY, columns, data, whereColumns, whereArgs);
                return 1;
            }

            @Override
            protected void onPostExecute(Integer count) {
                logger.debug("Item successfully modified!");
            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void syncWithDatabase(final Activity mActivity) {
        AsyncTask<Void, Void, Integer> task = (new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                syncCharacterData();
                syncInventoryData();
                return 1;
            }

            @Override
            protected void onPostExecute(Integer count) {
                Intent intent = new Intent();
                intent.setAction(UPDATE_UI);
                mActivity.sendBroadcast(intent);
            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void saveData(final Activity mActivity) {
        AsyncTask<Void, Void, Integer> task = (new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                saveAbilities();
                saveCombat();
                saveExperience();
                saveCurrency();
                saveIdentity();
                saveNotes();
                saveProficiency();
                saveSavingThrows();
                saveSkillProf();
                return 1;
            }

            @Override
            protected void onPostExecute(Integer count) {
                Toast.makeText(mActivity, "Data Saved", Toast.LENGTH_SHORT).show();
            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // Syncs all the character data
    private void syncCharacterData() {
        FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
        Cursor retrieve;
        String columns[];
        // Identity
        columns = new String[5];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_NAME;
        columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_RACE;
        columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_CLASS;
        columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_ALIGN;
        columns[4] = FeedReaderDbHelper.FeedEntry.COLUMN_BACKGROUND;
        assert feedReaderDbHelper != null;
        retrieve = feedReaderDbHelper.queryData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns);
        retrieve.moveToFirst();
        mCharacter.name = retrieve.getString(0);
        mCharacter.race = retrieve.getString(1);
        mCharacter.class_ = retrieve.getString(2);
        mCharacter.align = retrieve.getString(3);
        logger.debug("DB Identity: " + mCharacter.name + " " + mCharacter.race + " " + mCharacter.class_ + " " + mCharacter.align);
        // Abilities
        columns = new String[6];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_STR;
        columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_DEX;
        columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_CON;
        columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_INT;
        columns[4] = FeedReaderDbHelper.FeedEntry.COLUMN_WIS;
        columns[5] = FeedReaderDbHelper.FeedEntry.COLUMN_CHR;
        retrieve = feedReaderDbHelper.queryData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns);
        retrieve.moveToFirst();
        mCharacter.str = retrieve.getInt(0);
        mCharacter.dex = retrieve.getInt(1);
        mCharacter.con = retrieve.getInt(2);
        mCharacter.intel = retrieve.getInt(3);
        mCharacter.wis = retrieve.getInt(4);
        mCharacter.chr = retrieve.getInt(5);
        logger.debug("DB Abilities: "
                + mCharacter.str + " "
                + mCharacter.dex + " "
                + mCharacter.con + " "
                + mCharacter.intel + " "
                + mCharacter.wis + " "
                + mCharacter.chr);
        // Skills
        columns = new String[18];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_ACR;
        columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_ANI;
        columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_ARC;
        columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_ATH;
        columns[4] = FeedReaderDbHelper.FeedEntry.COLUMN_DEC;
        columns[5] = FeedReaderDbHelper.FeedEntry.COLUMN_HIS;
        columns[6] = FeedReaderDbHelper.FeedEntry.COLUMN_INS;
        columns[7] = FeedReaderDbHelper.FeedEntry.COLUMN_INTI;
        columns[8] = FeedReaderDbHelper.FeedEntry.COLUMN_INV;
        columns[9] = FeedReaderDbHelper.FeedEntry.COLUMN_MED;
        columns[10] = FeedReaderDbHelper.FeedEntry.COLUMN_NAT;
        columns[11] = FeedReaderDbHelper.FeedEntry.COLUMN_PER;
        columns[12] = FeedReaderDbHelper.FeedEntry.COLUMN_PERF;
        columns[13] = FeedReaderDbHelper.FeedEntry.COLUMN_PERS;
        columns[14] = FeedReaderDbHelper.FeedEntry.COLUMN_REL;
        columns[15] = FeedReaderDbHelper.FeedEntry.COLUMN_SLE;
        columns[16] = FeedReaderDbHelper.FeedEntry.COLUMN_STE;
        columns[17] = FeedReaderDbHelper.FeedEntry.COLUMN_SUR;
        retrieve = feedReaderDbHelper.queryData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns);
        retrieve.moveToFirst();
        String debug = "DB Skill Profs: ";
        for (int i = 0; i < mCharacter.skillProf.length; i++) {
            mCharacter.skillProf[i] = retrieve.getInt(i);
            debug += mCharacter.skillProf[i] + " ";
        }
        logger.debug(debug);
        // Saving Throws
        columns = new String[6];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_SAVE_STR;
        columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_SAVE_DEX;
        columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_SAVE_CON;
        columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_SAVE_INT;
        columns[4] = FeedReaderDbHelper.FeedEntry.COLUMN_SAVE_WIS;
        columns[5] = FeedReaderDbHelper.FeedEntry.COLUMN_SAVE_CHR;
        retrieve = feedReaderDbHelper.queryData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns);
        retrieve.moveToFirst();
        debug = "DB Saving Throws: ";
        for (int i = 0; i < mCharacter.saveProf.length; i++) {
            mCharacter.saveProf[i] = retrieve.getInt(i);
            debug += mCharacter.saveProf[i] + " ";
        }
        logger.debug(debug);
        // Proficiency
        columns = new String[1];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_PROFICIENCY;
        retrieve = feedReaderDbHelper.queryData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns);
        retrieve.moveToFirst();
        mCharacter.proficiency = retrieve.getInt(0);
        logger.debug("DB Proficiency: " + mCharacter.proficiency);
        // Experience
        columns = new String[1];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_EXPERIENCE;
        retrieve = feedReaderDbHelper.queryData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns);
        retrieve.moveToFirst();
        mCharacter.experience = retrieve.getInt(0);
        logger.debug("DB Experience: " + mCharacter.experience);
        // Combat
        columns = new String[4];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_HEALTH;
        columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_ARMOR;
        columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_INITIATIVE;
        columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_SPEED;
        retrieve = feedReaderDbHelper.queryData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns);
        retrieve.moveToFirst();
        mCharacter.health = retrieve.getInt(0);
        mCharacter.armor = retrieve.getInt(1);
        mCharacter.initiative = retrieve.getInt(2);
        mCharacter.speed = retrieve.getInt(3);
        columns = new String[7];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_ATTACK_BNS1;
        columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_ATTACK_BNS2;
        columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_ATTACK_BNS3;
        columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_WEAPON1;
        columns[4] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_WEAPON2;
        columns[5] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_WEAPON3;
        columns[6] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_AMMO;
        retrieve = feedReaderDbHelper.queryData(FeedReaderDbHelper.FeedEntry.TABLE_INVENTORY, columns);
        retrieve.moveToFirst();
        try {
            for (int i = 0; i < 3; i++) {
                mCharacter.attack_bonus[i] = retrieve.getInt(i);
            }
            for (int i = 3; i < 6; i++) {
                mCharacter.weapons[i - 3] = retrieve.getString(i);
            }
            mCharacter.ammo = retrieve.getString(6);
        } catch (Exception e) {
            logger.error("There are no items in inventory yet!");
        }
        logger.debug("DB Health: " + mCharacter.health + " Armor: " + mCharacter.armor + " Ini: " +
                mCharacter.initiative + " Speed: " + mCharacter.speed + " AtkBns: +" + mCharacter.attack_bonus[0] +
                " +" + mCharacter.attack_bonus[1] + " +" + mCharacter.attack_bonus[2] + " Weapons: " +
                mCharacter.weapons[0] + " " + mCharacter.weapons[1] + mCharacter.weapons[2] + " Ammo: " +
                mCharacter.ammo);
        // Currency
        columns = new String[4];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_COPPER;
        columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_SILVER;
        columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_GOLD;
        columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_PLATINUM;
        retrieve = feedReaderDbHelper.queryData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns);
        retrieve.moveToFirst();
        mCharacter.copper = retrieve.getInt(0);
        mCharacter.silver = retrieve.getInt(1);
        mCharacter.gold = retrieve.getInt(2);
        mCharacter.plat = retrieve.getInt(3);
        logger.debug("DB Currency: [" + mCharacter.copper + ", " + mCharacter.silver + ", " +mCharacter.gold + ", " +mCharacter.plat + "]");
        // Notes
        columns = new String[1];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_NOTES;
        retrieve = feedReaderDbHelper.queryData(FeedReaderDbHelper.FeedEntry.TABLE_INVENTORY, columns);
        retrieve.moveToFirst();
        try {
            mCharacter.notes = retrieve.getString(0);
            logger.debug("DB Notes: " + mCharacter.notes);
        }
        catch(Exception e){
            logger.error("There are no notes yet!");
        }
    }

    // Syncs all the inventory data
    private void syncInventoryData() {
        FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
        Cursor retrieve;
        String columns[] = new String[]{"*"};
        retrieve = feedReaderDbHelper.queryData(FeedReaderDbHelper.FeedEntry.TABLE_INVENTORY, columns);
        LinkedList<Item> item_list = new LinkedList<Item>();
        while (retrieve.moveToNext()) {
            Item curr = new Item();
            try {
                curr.weight = retrieve.getDouble(1);
            } catch (Exception e) {
                logger.debug("Failed retrieving weight");
                curr.weight = 0;
            }
            try {
                curr.gold_value = retrieve.getDouble(2);
            } catch (Exception e) {
                logger.debug("Failed retrieving gold");
                curr.gold_value = 0;
            }
            try {
                curr.desc = retrieve.getString(3);
            } catch (Exception e) {
                logger.debug("Failed retrieving desc");
                curr.desc = " ";
            }
            try {
                curr.name = retrieve.getString(4);
            } catch (Exception e) {
                logger.debug("Failed retrieving name");
                curr.name = " ";
            }
            try {
                curr.amount = retrieve.getInt(5);
            } catch (Exception e) {
                logger.debug("Failed retrieving amount");
                curr.amount = 0;
            }
            boolean isWeapon = false;
            Weapon weapon = new Weapon(curr);
            try {
                weapon.dice_num = retrieve.getInt(6);
                isWeapon = true;
                if (weapon.dice_num == 0) {
                    isWeapon = false;
                }
            } catch (Exception e) {
                logger.debug("Failed retrieving dice_num");
                isWeapon = false;
            }
            if (isWeapon) {
                try {
                    weapon.dice_size = retrieve.getInt(7);
                } catch (Exception e) {
                    logger.debug("Failed retrieving dice_size");
                    weapon.dice_size = 0;
                }
                try {
                    weapon.flat_damage = retrieve.getInt(8);
                } catch (Exception e) {
                    logger.debug("Failed retrieving flat damage");
                    weapon.flat_damage = 0;
                }
                try {
                    weapon.range = retrieve.getInt(9);
                } catch (Exception e) {
                    logger.debug("Failed retrieving range");
                    weapon.range = 0;
                }
                try {
                    weapon.damage_type = retrieve.getString(10);
                } catch (Exception e) {
                    logger.debug("Failed retrieving damage type");
                    weapon.damage_type = " ";
                }
                logger.debug(weapon.toString());
                item_list.add(weapon);
            } else {
                logger.debug(curr.toString());
                item_list.add(curr);
            }
        }
        for (int i = 0; i < item_list.size(); i++) {
            mCharacter.inventory.put(item_list.get(i).name, item_list.get(i));
        }
    }

    private void saveAbilities() {
        FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
        Object save[] = new Object[6];
        save[0] = new Integer(mCharacter.str);
        save[1] = new Integer(mCharacter.dex);
        save[2] = new Integer(mCharacter.con);
        save[3] = new Integer(mCharacter.intel);
        save[4] = new Integer(mCharacter.wis);
        save[5] = new Integer(mCharacter.chr);
        String columns[] = new String[6];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_STR;
        columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_DEX;
        columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_CON;
        columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_INT;
        columns[4] = FeedReaderDbHelper.FeedEntry.COLUMN_WIS;
        columns[5] = FeedReaderDbHelper.FeedEntry.COLUMN_CHR;
        assert feedReaderDbHelper != null;
        feedReaderDbHelper.updateData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns, save);
    }

    private void saveCombat() {
        FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
        Object save[] = new Object[4];
        save[0] = new Integer(mCharacter.health);
        save[1] = new Integer(mCharacter.armor);
        save[2] = new Integer(mCharacter.initiative);
        save[3] = new Integer(mCharacter.speed);
        String columns[] = new String[4];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_HEALTH;
        columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_ARMOR;
        columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_INITIATIVE;
        columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_SPEED;
        assert feedReaderDbHelper != null;
        feedReaderDbHelper.updateData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns, save);
        save = new Object[7];
        for (int i = 0; i < 3; i++) {
            save[i] = new Integer(mCharacter.attack_bonus[i]);
        }
        save[3] = mCharacter.weapons[0];
        save[4] = mCharacter.weapons[1];
        save[5] = mCharacter.weapons[2];
        save[6] = mCharacter.ammo;
        columns = new String[7];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_ATTACK_BNS1;
        columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_ATTACK_BNS2;
        columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_ATTACK_BNS3;
        columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_WEAPON1;
        columns[4] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_WEAPON2;
        columns[5] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_WEAPON3;
        columns[6] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_AMMO;
        feedReaderDbHelper.updateData(FeedReaderDbHelper.FeedEntry.TABLE_INVENTORY, columns, save);
    }

    private void saveExperience() {
        FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
        Object save[] = new Object[1];
        save[0] = mCharacter.experience;
        String columns[] = new String[1];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_EXPERIENCE;
        assert feedReaderDbHelper != null;
        feedReaderDbHelper.updateData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns, save);
    }

    private void saveCurrency() {
        FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
        Object save[] = new Object[4];
        save[0] = mCharacter.copper;
        save[1] = mCharacter.silver;
        save[2] = mCharacter.gold;
        save[3] = mCharacter.plat;
        String columns[] = new String[4];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_COPPER;
        columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_SILVER;
        columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_GOLD;
        columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_PLATINUM;
        assert feedReaderDbHelper != null;
        feedReaderDbHelper.updateData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns, save);
    }

    private void saveIdentity() {
        FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
        Object save[] = new Object[5];
        save[0] = mCharacter.name;
        save[1] = mCharacter.race;
        save[2] = mCharacter.class_;
        save[3] = mCharacter.align;
        save[4] = mCharacter.background;
        String columns[] = new String[5];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_NAME;
        columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_RACE;
        columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_CLASS;
        columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_ALIGN;
        columns[4] = FeedReaderDbHelper.FeedEntry.COLUMN_BACKGROUND;
        assert feedReaderDbHelper != null;
        feedReaderDbHelper.updateData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns, save);
    }

    private void saveNotes() {
        FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
        Object save[] = new Object[1];
        if(mCharacter.notes == null){
            save[0] = new String(" ");
        }
        else{
            save[0] = mCharacter.notes;
        }
        String columns[] = new String[1];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_INV_NOTES;
        feedReaderDbHelper.updateData(FeedReaderDbHelper.FeedEntry.TABLE_INVENTORY, columns, save);
    }

    private void saveProficiency() {
        FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
        Object save[] = new Object[1];
        save[0] = new Integer(mCharacter.proficiency);
        String columns[] = new String[1];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_PROFICIENCY;
        feedReaderDbHelper.updateData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns, save);
    }

    private void saveSavingThrows() {
        FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
        Object save[] = new Object[6];
        for (int i = 0; i < save.length; i++) {
            save[i] = new Integer(mCharacter.saveProf[i]);
        }
        String columns[] = new String[6];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_SAVE_STR;
        columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_SAVE_DEX;
        columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_SAVE_CON;
        columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_SAVE_INT;
        columns[4] = FeedReaderDbHelper.FeedEntry.COLUMN_SAVE_WIS;
        columns[5] = FeedReaderDbHelper.FeedEntry.COLUMN_SAVE_CHR;
        feedReaderDbHelper.updateData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns, save);
    }

    private void saveSkillProf() {
        FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
        Object save[] = new Object[18];
        for (int i = 0; i < save.length; i++) {
            save[i] = new Integer(mCharacter.skillProf[i]);
        }
        String columns[] = new String[18];
        columns[0] = FeedReaderDbHelper.FeedEntry.COLUMN_ACR;
        columns[1] = FeedReaderDbHelper.FeedEntry.COLUMN_ANI;
        columns[2] = FeedReaderDbHelper.FeedEntry.COLUMN_ARC;
        columns[3] = FeedReaderDbHelper.FeedEntry.COLUMN_ATH;
        columns[4] = FeedReaderDbHelper.FeedEntry.COLUMN_DEC;
        columns[5] = FeedReaderDbHelper.FeedEntry.COLUMN_HIS;
        columns[6] = FeedReaderDbHelper.FeedEntry.COLUMN_INS;
        columns[7] = FeedReaderDbHelper.FeedEntry.COLUMN_INTI;
        columns[8] = FeedReaderDbHelper.FeedEntry.COLUMN_INV;
        columns[9] = FeedReaderDbHelper.FeedEntry.COLUMN_MED;
        columns[10] = FeedReaderDbHelper.FeedEntry.COLUMN_NAT;
        columns[11] = FeedReaderDbHelper.FeedEntry.COLUMN_PER;
        columns[12] = FeedReaderDbHelper.FeedEntry.COLUMN_PERF;
        columns[13] = FeedReaderDbHelper.FeedEntry.COLUMN_PERS;
        columns[14] = FeedReaderDbHelper.FeedEntry.COLUMN_REL;
        columns[15] = FeedReaderDbHelper.FeedEntry.COLUMN_SLE;
        columns[16] = FeedReaderDbHelper.FeedEntry.COLUMN_STE;
        columns[17] = FeedReaderDbHelper.FeedEntry.COLUMN_SUR;
        feedReaderDbHelper.updateData(FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER, columns, save);
    }

}
