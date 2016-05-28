package com.quercus.graphagregator;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class GraphDatabaseHelper  extends SQLiteOpenHelper {

    public static final int DB_VERSION = 2;
    public static final String DB_NAME = "GraphDb";
    public static final String DB_TABLE_NAME = "Data";

    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_STEP = "step";
    public static final String KEY_MONEY = "money";
    public static final String KEY_SLEEP = "sleep";

    public GraphDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DB_TABLE_NAME + "("
                + KEY_ID + " integer primary key,"
                + KEY_DATE + " integer,"
                + KEY_MONEY + " integer,"
                + KEY_SLEEP + " integer,"
                + KEY_STEP + " integer" + ")");

    }


    public void clearData(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + DB_TABLE_NAME);
        onCreate(db);
    }


    public void writeData(SQLiteDatabase db) {
        final Random random = new Random();

        Calendar start_year = Calendar.getInstance();
        //start_year.set(YEAR, 2016);
        start_year.set(Calendar.MONTH, 0);
        start_year.set(Calendar.DAY_OF_MONTH, 0);
        start_year.set(Calendar.HOUR, 0);
        start_year.set(Calendar.MINUTE, 0);
        start_year.set(Calendar.SECOND, 0);
        start_year.set(Calendar.MILLISECOND, 0);

        Calendar now = Calendar.getInstance();

        // Заполним нашу таблицу данными за последний год
        // TODO: Очень долгий процесс распараллелить его
        // TODO: Хранить сон в bool или придумать что-то другое
        for (Calendar i = start_year; i.getTimeInMillis() < now.getTimeInMillis(); i.add(Calendar.HOUR, 1)) {

            int sleep = 0;
            if (i.get(Calendar.HOUR) > 0 && i.get(Calendar.HOUR) < 9) {
                sleep = 1;
            }
            insertData(db, i, Math.abs(random.nextInt(10000)), random.nextInt(500000), sleep);
        }

    }

    private void insertData(SQLiteDatabase db, Calendar data_time, int step, int money, int sleep){

        ContentValues contentValues = new ContentValues();

        // Перевод в UNIX-time для записи в БД времени
        contentValues.put(KEY_DATE, data_time.getTimeInMillis());
        contentValues.put(KEY_STEP, step);
        contentValues.put(KEY_MONEY, money);
        contentValues.put(KEY_SLEEP, sleep);

        db.insert(DB_TABLE_NAME, null, contentValues);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + DB_TABLE_NAME);

        onCreate(db);

    }
}
