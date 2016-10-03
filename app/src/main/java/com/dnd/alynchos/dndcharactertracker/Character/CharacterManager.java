package com.dnd.alynchos.dndcharactertracker.Character;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dnd.alynchos.dndcharactertracker.Debug.Logger;
import com.dnd.alynchos.dndcharactertracker.Items.Item;
import com.dnd.alynchos.dndcharactertracker.Items.Weapons.Weapon;
import com.dnd.alynchos.dndcharactertracker.SaveData.FeedReaderDbHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;

/**
 * Created by Alex Lynchosky on 12/22/2014.
 * The main character manager. Handles saving and retrieving data.
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

    // Preferences
    public static final String PREF_CHARACTER_UUID = "character_uuid";

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
    private long mCurrentCharacterUUID = 1;

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

    public void setNotes(String set) {
        mCharacter.notes = set;
    }

    public void setProficiency(int proficiency) {
        mCharacter.proficiency = proficiency;
    }

    public void setSavingThrows(int savingThrows[]) {
        if (savingThrows.length != 6) {
            logger.debug("Incorrect saving throws array length: " + savingThrows.length);
            return;
        }
        System.arraycopy(savingThrows, 0, mCharacter.saveProf, 0, savingThrows.length);
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

    /**
     * *************************************************************
     * ***************** Public Getter Functions *********************
     * **************************************************************
     */

    // UUID
    public long getCharacterUUID() {
        return mCurrentCharacterUUID;
    }

    // Combat
    public int getArmor() {
        return mCharacter.armor;
    }

    public String getAmmo() {
        return " ";
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
        if (num > mCharacter.weapons.size()) {
            logger.error("Invalid index for weapons: " + num);
            return null;
        }
        return mCharacter.weapons.get(num);
    }

    public BaseCharacter getCharacter() {
        return mCharacter;
    }

    // Identity
    public String getName() {
        if (mCharacter.name == null) mCharacter.name = " ";
        return mCharacter.name;
    }

    public String getRace() {
        if (mCharacter.race == null) mCharacter.race = " ";
        return mCharacter.race;
    }

    public String getClass_() {
        if (mCharacter.class_ == null) mCharacter.class_ = " ";
        return mCharacter.class_;
    }

    public String getAlign() {
        if (mCharacter.align == null) mCharacter.align = " ";
        return mCharacter.align;
    }

    public String getBackground() {
        if (mCharacter.background == null) mCharacter.background = " ";
        return mCharacter.background;
    }

    public String getNotes() {
        if (mCharacter.notes == null) mCharacter.notes = " ";
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
                return 1;
            }

            @Override
            protected void onPostExecute(Integer count) {
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
        System.arraycopy(set, 0, names, 0, names.length);
        return names;
    }

    // Return either the list of weapons, or the list of everything except weapons
    public String[] getInventoryWeaponNames(boolean wantWeapons) {
        LinkedList<Weapon> weapons = new LinkedList<>();
        LinkedList<Item> items = new LinkedList<>();
        Item list[] = mCharacter.inventory.values().toArray(new Item[mCharacter.inventory.size()]);
        // Find the weapons
        for (Item aList : list) {
            if (aList instanceof Weapon) {
                if (wantWeapons) {
                    weapons.add((Weapon) aList);
                }
            } else {
                if (!wantWeapons) {
                    items.add(aList);
                }
            }
        }
        String names[];
        if (wantWeapons) {
            names = new String[weapons.size()];
            for (int i = 0; i < names.length; i++) {
                names[i] = weapons.get(i).name;
            }
        } else {
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
                assert feedReaderDbHelper != null;
                //TODO: DELETE ITEM
                //feedReaderDbHelper.deleteItemData(FeedReaderDbHelper.FeedEntry.TABLE_INVENTORY, name);
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
        AsyncTask<Void, Void, Long> task = (new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                mCurrentCharacterUUID = mActivity
                        .getPreferences(Context.MODE_PRIVATE)
                        .getLong(PREF_CHARACTER_UUID, 1);
                logger.debug("Character UUID Retrieved: " + mCurrentCharacterUUID);
                return syncCharacterData(mCurrentCharacterUUID);
            }

            @Override
            protected void onPostExecute(Long uuid) {
                Intent intent = new Intent();
                intent.setAction(UPDATE_UI);
                mActivity.sendBroadcast(intent);
            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void saveData(final Activity mActivity) {
        AsyncTask<Void, Void, Long> task = (new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                return saveCharacter(mCurrentCharacterUUID);
            }

            @Override
            protected void onPostExecute(Long uuid) {
                logger.debug("Character saved with uuid: " + uuid);
                mCurrentCharacterUUID = uuid;
                mActivity.getPreferences(Context.MODE_PRIVATE)
                        .edit()
                        .putLong(PREF_CHARACTER_UUID, mCurrentCharacterUUID)
                        .apply();
                Toast.makeText(mActivity, "Data Saved", Toast.LENGTH_SHORT).show();
            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private long saveCharacter(long character_uuid) {
        Gson gson = new Gson();
        Type type = new TypeToken<BaseCharacter>() {
        }.getType();
        String json = gson.toJson(mCharacter, type);
        logger.debug("Saving character[" + character_uuid + "]: " + json);
        FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
        assert feedReaderDbHelper != null;
        Object save[] = new Object[]{json};
        String[] where_columns = new String[]{FeedReaderDbHelper.FeedEntry._ID};
        Object[] where_args = new Object[]{character_uuid};
        // Check to see if the character exists in the database, if not, create a new one
        try {
            Cursor queryData = feedReaderDbHelper.queryData(
                    FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER,
                    new String[]{FeedReaderDbHelper.FeedEntry.COLUMN_CHARACTER},
                    (FeedReaderDbHelper.FeedEntry._ID + " = ?"),
                    new String[]{"" + character_uuid});
            queryData.moveToFirst();
            logger.debug(queryData.getString(0));
            logger.debug("Character found, updating data.");
            feedReaderDbHelper.updateData(
                    FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER,
                    new String[]{FeedReaderDbHelper.FeedEntry.COLUMN_CHARACTER},
                    save,
                    where_columns,
                    where_args);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("The character does not exist yet, creating new entry.");
            return feedReaderDbHelper.addRow(
                    FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER,
                    new String[]{FeedReaderDbHelper.FeedEntry.COLUMN_CHARACTER},
                    save);
        }
        return character_uuid;
    }

    // Syncs all the character data
    private long syncCharacterData(long character_uuid) {
        FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
        assert feedReaderDbHelper != null;
        try {
            Cursor retrieve = feedReaderDbHelper.queryData(
                    FeedReaderDbHelper.FeedEntry.TABLE_CHARACTER,
                    new String[]{FeedReaderDbHelper.FeedEntry.COLUMN_CHARACTER},
                    (FeedReaderDbHelper.FeedEntry._ID + " = ?"),
                    new String[]{"" + character_uuid});
            retrieve.moveToFirst();
            Gson gson = new Gson();
            Type type = new TypeToken<BaseCharacter>() {
            }.getType();
            String json = retrieve.getString(0);
            logger.debug("Loading character[" + character_uuid + "]: " + json);
            mCharacter = gson.fromJson(json, type);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("Unable to retrieve character from database!");
        }
        return mCurrentCharacterUUID;
    }

}
