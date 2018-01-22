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
 * Talks to the database. All data is saved in JSON format.
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
                    FeedEntry.COLUMN_CHARACTER + TEXT_TYPE +
                    // Any other options for the CREATE command
                    " )";

    private static final String SQL_DELETE_TABLE_CHARACTER =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_CHARACTER;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "FeedReader.db";


    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        createCharacterTable(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for character data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_TABLE_CHARACTER);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, newVersion, oldVersion);
    }

    public void deleteDataBase() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_DELETE_TABLE_CHARACTER);
        onCreate(db);
    }

    public long addRow(String table, String[] columns, Object[] data) {
        SQLiteDatabase db = getReadableDatabase();
        return insertData(db, table, columns, data);
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

    // Retrieve data from the desired table
    public Cursor queryData(String table, String columns[], String selection, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                table,                                    // The table to query
                columns,                                  // The columns to return
                selection,                                // The selection string for the WHERE clause (can input ?s to use next field)
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );
        cursor.moveToFirst();
        return cursor;
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
            } else if (data[i] instanceof Long) {
                logger.debug("Updating long: " + data[i].toString());
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
                } else if (whereArgs[i] instanceof Long) {
                    logger.debug("Updating long: " + whereArgs[i].toString());
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
        insertData(db, FeedEntry.TABLE_CHARACTER, new String[]{FeedEntry.COLUMN_CHARACTER}, new Object[]{"empty_character"});
    }

    /**
     * ***********************************************************
     */

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        // Character Table
        public static final String TABLE_CHARACTER = "character";
        // Character
        public static final String COLUMN_CHARACTER = "character_info_column";
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
