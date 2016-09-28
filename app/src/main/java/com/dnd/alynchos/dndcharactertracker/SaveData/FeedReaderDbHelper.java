package com.dnd.alynchos.dndcharactertracker.SaveData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.dnd.alynchos.dndcharactertracker.Debug.Logger;

/**
 * Created by Alex Lynchosky on 12/23/2014.
 */
public class FeedReaderDbHelper extends SQLiteOpenHelper {

    /* Debugging */
    private static final String TAG = FeedReaderDbHelper.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    private static FeedReaderDbHelper feedReaderDbHelper;
    private static Context mContext;

    private static final String DELETE = "DELETE ";
    private static final String UPDATE = "UPDATE ";
    private static final String WHERE = "WHERE ";
    private static final String SET = "SET ";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_CHARACTER =
            "CREATE TABLE " + FeedEntry.TABLE_CHARACTER + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    // Identity
                    FeedEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_RACE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_CLASS + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_ALIGN + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_BACKGROUND + TEXT_TYPE + COMMA_SEP +
                    // Abilities
                    FeedEntry.COLUMN_STR + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_DEX + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_CON + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INT + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_WIS + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_CHR + TEXT_TYPE + COMMA_SEP +
                    // Skills
                    FeedEntry.COLUMN_ACR + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_ANI + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_ARC + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_ATH + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_DEC + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_HIS + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INS + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INTI + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INV + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_MED + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAT + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_PER + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_PERF + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_PERS + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_REL + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_SLE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_STE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_SUR + TEXT_TYPE + COMMA_SEP +
                    // Saving Throws
                    FeedEntry.COLUMN_SAVE_STR + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_SAVE_DEX + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_SAVE_CON + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_SAVE_INT + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_SAVE_WIS + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_SAVE_CHR + TEXT_TYPE + COMMA_SEP +
                    // Experience
                    FeedEntry.COLUMN_EXPERIENCE + TEXT_TYPE + COMMA_SEP +
                    // Proficiency
                    FeedEntry.COLUMN_PROFICIENCY + TEXT_TYPE + COMMA_SEP +
                    // Combat
                    FeedEntry.COLUMN_HEALTH + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_ARMOR + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INITIATIVE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_SPEED + TEXT_TYPE + COMMA_SEP +
                    // Money
                    FeedEntry.COLUMN_COPPER + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_SILVER + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_GOLD + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_PLATINUM + TEXT_TYPE +
                    // Any other options for the CREATE command
                    " )";

    private static final String SQL_CREATE_INVENTORY =
            "CREATE TABLE " + FeedEntry.TABLE_INVENTORY + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_INV_WEIGHT + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INV_GOLD + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INV_DESC + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INV_NAME + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INV_AMOUNT + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INV_DICE_NUM + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INV_DICE_SIZE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INV_DAM_FLAT + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INV_RANGE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INV_DAM_TYPE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INV_WEAPONS + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INV_AMMO + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_INV_NOTES + TEXT_TYPE +
                    ")";
    private static final String SQL_DELETE_TABLE_CHARACTER =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_CHARACTER;

    private static final String SQL_DELETE_TABLE_INVENTORY =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_INVENTORY;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";


    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        createCharacterTable(db);
        createInventoryTable(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for character data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_TABLE_CHARACTER);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void deleteDataBase() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_DELETE_TABLE_CHARACTER);
        db.execSQL(SQL_DELETE_TABLE_INVENTORY);
        onCreate(db);
    }

    public void deleteCharacterDataBase() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_DELETE_TABLE_CHARACTER);
        createCharacterTable(db);
    }

    public void deleteInventoryDataBase() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_DELETE_TABLE_INVENTORY);
        createInventoryTable(db);
    }

    public void deleteItemData(String table, String row_name) {
        SQLiteDatabase db = getReadableDatabase();
        String exec = "";
        exec += DELETE + " FROM " + table + " ";
        exec += WHERE + " " + FeedEntry.COLUMN_INV_NAME + "='" + row_name + "'";
        logger.debug("\"" + exec + "\"");
        db.execSQL(exec);
    }

    public long insertData(String table, String columns[], Object data[]) {
        SQLiteDatabase db = getWritableDatabase();
        long rv = insertData(db, table, columns, data);
        return rv;
    }

    public long insertData(SQLiteDatabase db, String table, String columns[], Object data[]) {
        // Create a new map of values, where column name is the key
        ContentValues values = new ContentValues();
        for (int i = 0; i < data.length; i++) {
            if (data[i] instanceof String) {
                logger.debug("Inserting String");
                values.put(columns[i], (String) data[i]);
            } else if (data[i] instanceof Integer) {
                logger.debug("Inserting integer");
                values.put(columns[i], (Integer) data[i]);
            } else if (data[i] instanceof Double) {
                logger.debug("Inserting Double");
                values.put(columns[i], (Double) data[i]);
            } else {
                if (data[i] == null)
                    logger.debug("data at " + i + " is null");
                else
                    logger.debug("error inserting type: " + i + data[i].getClass().toString());
            }
        }
        // Insert the new row, returning the primary key value of the new row
        return db.insert(table, null, values);
    }

    // Right now this retrieves an entire column to make it easy on me
    public Cursor queryData(String table, String columns[]) {
        return queryData(table, columns, null);
    }

    // Right now this retrieves an entire column to make it easy on me
    public Cursor queryData(String table, String columns[], String selection) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                table,                                    // The table to query
                columns,                                  // The columns to return
                selection,                             // The selection string for the WHERE clause (can input ?s to use next field)
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );
        cursor.moveToFirst();
        return cursor;
    }

    public void updateData(String table, String columns[], Object data[]) {
        updateData(table, columns, data, null, null);
    }

    public void updateData(String table, String columns[], Object data[], String whereColumns[], Object whereArgs[]) {
        SQLiteDatabase db = getReadableDatabase();
        String exec = "";
        exec += UPDATE + table + " " + SET;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == null) {
                continue;
            }
            if (i > 0) {
                exec += COMMA_SEP;
            }
            exec += columns[i] + "=";
            if (data[i] instanceof String) {
                logger.debug("Updating String: " + data[i].toString());
                exec += "'" + data[i] + "'";
            } else if (data[i] instanceof Integer) {
                logger.debug("Updating integer: " + data[i].toString());
                exec += data[i];
            } else if (data[i] instanceof Double) {
                logger.debug("Updating double: " + data[i].toString());
                exec += data[i];
            } else {
                logger.error("Improper Object updated: " + data[i].getClass().getSimpleName());
                return;
            }
        }
        if (whereColumns != null && whereArgs != null && whereColumns.length == whereArgs.length) {
            exec += " " + WHERE;
            for (int i = 0; i < whereColumns.length; i++) {
                if (whereColumns[i] == null) continue;
                if (i > 0) {
                    exec += COMMA_SEP;
                }
                exec += whereColumns[i] + "=";
                if (whereArgs[i] instanceof String) {
                    logger.debug("Updating String: " + whereArgs[i].toString());
                    exec += "'" + whereArgs[i] + "'";
                } else if (whereArgs[i] instanceof Integer) {
                    logger.debug("Updating integer: " + whereArgs[i].toString());
                    exec += whereArgs[i];
                } else {
                    logger.debug("Improper Object updated: " + whereArgs[i].getClass().getSimpleName());
                    return;
                }
            }
        }
        logger.debug("\"" + exec + "\"");
        db.execSQL(exec);
    }


    /**
     * *************************************************************
     * *************** Private Helper Functions ********************
     * *************************************************************
     */

    private void createCharacterTable(SQLiteDatabase db) {
        // Character Table
        db.execSQL(SQL_CREATE_CHARACTER);
        Object defaultNum[] = new Object[1];
        defaultNum[0] = new Integer(0);
        // Identity
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_NAME}, new Object[]{"Name"});
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_RACE}, new Object[]{"Race"});
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_CLASS}, new Object[]{"Class"});
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_ALIGN}, new Object[]{"Alignment"});
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_BACKGROUND}, new Object[]{"Background"});
        // Abilities
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_STR}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_DEX}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_CON}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_INT}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_WIS}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_CHR}, defaultNum);
        // Skills}
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_ACR}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_ANI}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_ARC}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_ATH}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_DEC}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_HIS}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_INS}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_INTI}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_INV}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_MED}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_NAT}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_PER}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_PERF}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_PERS}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_REL}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_SLE}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_STE}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_SUR}, defaultNum);
        // Saving Throws
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_SAVE_STR}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_SAVE_DEX}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_SAVE_CON}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_SAVE_INT}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_SAVE_WIS}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_SAVE_CHR}, defaultNum);
        // Experience
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_EXPERIENCE}, defaultNum);
        // Proficiency
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_PROFICIENCY}, defaultNum);
        // Currency
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_COPPER}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_SILVER}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_GOLD}, defaultNum);
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_PLATINUM}, defaultNum);
    }

    private void createInventoryTable(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_INVENTORY);
    }

    /**
     * ***********************************************************
     */

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        // Character Table
        public static final String TABLE_CHARACTER = "character";
        // Identity
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_RACE = "race";
        public static final String COLUMN_CLASS = "class";
        public static final String COLUMN_ALIGN = "align";
        public static final String COLUMN_BACKGROUND = "background";
        // Abilities
        public static final String COLUMN_STR = "str";
        public static final String COLUMN_DEX = "dex";
        public static final String COLUMN_CON = "con";
        public static final String COLUMN_INT = "int";
        public static final String COLUMN_WIS = "wis";
        public static final String COLUMN_CHR = "chr";
        // Skills
        public static final String COLUMN_ACR = "acr";
        public static final String COLUMN_ANI = "ani";
        public static final String COLUMN_ARC = "arc";
        public static final String COLUMN_ATH = "ath";
        public static final String COLUMN_DEC = "dec";
        public static final String COLUMN_HIS = "his";
        public static final String COLUMN_INS = "ins";
        public static final String COLUMN_INTI = "inti";
        public static final String COLUMN_INV = "inv";
        public static final String COLUMN_MED = "med";
        public static final String COLUMN_NAT = "nat";
        public static final String COLUMN_PER = "per";
        public static final String COLUMN_PERF = "perf";
        public static final String COLUMN_PERS = "pers";
        public static final String COLUMN_REL = "rel";
        public static final String COLUMN_SLE = "sle";
        public static final String COLUMN_STE = "ste";
        public static final String COLUMN_SUR = "sur";
        // Saving Throws
        public static final String COLUMN_SAVE_STR = "save_str";
        public static final String COLUMN_SAVE_DEX = "save_dex";
        public static final String COLUMN_SAVE_CON = "save_con";
        public static final String COLUMN_SAVE_INT = "save_int";
        public static final String COLUMN_SAVE_WIS = "save_wis";
        public static final String COLUMN_SAVE_CHR = "save_chr";
        // Experience
        public static final String COLUMN_EXPERIENCE = "exp";
        public static final String COLUMN_PROFICIENCY = "prof";
        // Combat
        public static final String COLUMN_HEALTH = "health";
        public static final String COLUMN_ARMOR = "armor";
        public static final String COLUMN_INITIATIVE = "initiative";
        public static final String COLUMN_SPEED = "speed";
        // Money
        public static final String COLUMN_COPPER = "char_copper";
        public static final String COLUMN_SILVER = "char_silver";
        public static final String COLUMN_GOLD = "char_gold";
        public static final String COLUMN_PLATINUM = "char_platinum";

        // Inventory Table
        public static final String TABLE_INVENTORY = "inventory";
        // Inventory Columns
        public static final String COLUMN_INV_WEIGHT = "weight";
        public static final String COLUMN_INV_GOLD = "inv_gold";
        public static final String COLUMN_INV_DESC = "desc";
        public static final String COLUMN_INV_NAME = "name";
        public static final String COLUMN_INV_AMOUNT = "amount";
        public static final String COLUMN_INV_DICE_NUM = "dice_num";
        public static final String COLUMN_INV_DICE_SIZE = "dice_size";
        public static final String COLUMN_INV_DAM_FLAT = "dam_flat";
        public static final String COLUMN_INV_RANGE = "range";
        public static final String COLUMN_INV_DAM_TYPE = "dam_type";
        public static final String COLUMN_INV_WEAPONS = "weapons";
        public static final String COLUMN_INV_AMMO = "ammo";
        public static final String COLUMN_INV_NOTES = "notes";
    }

    /**
     * ****************************************************
     * ************* Non SQL Related Functions **************
     * *****************************************************
     */

    public static void setContext(Context context) {
        mContext = context;
    }

    public static FeedReaderDbHelper getInstance() {
        if (mContext == null) return null;
        if (feedReaderDbHelper == null) {
            feedReaderDbHelper = new FeedReaderDbHelper(mContext);
        }
        return feedReaderDbHelper;
    }
}
