package com.illuzor.sag.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager {

    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_SCORE = "persons_name";

    private static final String DATABASE_NAME = "sagDB";
    private static final String DATABASE_TABLE = "scoresTable";
    private static final int DATABASE_VERSION = 1;

    private DBHelper dbHelper;
    private final Context context;
    private SQLiteDatabase database;

    private static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                    KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_SCORE + " TEXT NOT NULL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public DatabaseManager(Context context) {
            this.context = context;
    }

    public DatabaseManager open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long createEntry(String score) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_SCORE, score);
        return database.insert(DATABASE_TABLE, null, cv);
    }

    public String getData() throws SQLException {

        String[] columns = new String[]{KEY_ROW_ID, KEY_SCORE};
        Cursor cursor = database.query(DATABASE_TABLE, columns, null, null, null, null, null);
        String result = "";

        int iRow = cursor.getColumnIndex(KEY_ROW_ID);
        int iNAme = cursor.getColumnIndex(KEY_SCORE);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            result += "SCORE #" + cursor.getString(iRow) + ": " + cursor.getString(iNAme) + '\n';
        }

        return result;
    }

    /*public void updateEntry(long lRow, String mName, String mHotness) throws SQLException {
        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put(KEY_SCORE, mName);
        database.update(DATABASE_TABLE, cvUpdate, KEY_ROW_ID + "=" + lRow, null);
    }

    public void deleteEntry(long lRow1) throws SQLException {
        database.delete(DATABASE_TABLE, KEY_ROW_ID + "=" + lRow1, null);
    }*/

}
