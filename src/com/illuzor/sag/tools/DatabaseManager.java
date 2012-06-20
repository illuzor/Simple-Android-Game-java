package com.illuzor.sag.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author illuzor
 *
 * Класс для управления базой данных, которая используется для хранения таблицы результатов
 */

public class DatabaseManager {

    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_SCORE = "score";

    private static final String DATABASE_NAME = "sagDB";
    private static final String DATABASE_TABLE = "scoresTable";
    private static final int DATABASE_VERSION = 1;

    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private final Context context;

    // класс хелпера
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

    // конструктор
    public DatabaseManager(Context context) {
        this.context = context;
    }

    // открытие хелпера
    public DatabaseManager open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    // закрытие хелпера
    public void close() {
        dbHelper.close();
    }

    // запись значения в таблицу
    public long createEntry(String score) {
        ContentValues values = new ContentValues();
        values.put(KEY_SCORE, score);
        return database.insert(DATABASE_TABLE, null, values);
    }

    // получение данных из базы
    public String getData() throws SQLException {

        String[] columns = new String[]{ KEY_ROW_ID, KEY_SCORE };
        Cursor cursor = database.query(DATABASE_TABLE, columns, null, null, null, null, null);
        String scoresString = "";

        int iRow = cursor.getColumnIndex(KEY_ROW_ID);
        int iName = cursor.getColumnIndex(KEY_SCORE);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            scoresString += "SCORE #" + cursor.getString(iRow) + ": " + cursor.getString(iName) + '\n';
        }

        return scoresString;
    }

}
