package com.quercus.graphagregator;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

    // Получить количество строк в БД
    public int getCountRows(SQLiteDatabase db){
        Cursor cursor = db.query(GraphDatabaseHelper.DB_TABLE_NAME, null, null, null, null, null, null);
        int countRows = cursor.getCount();
        cursor.close();
        return countRows;
    }

    // Очистка таблицы
    public void clearData(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + DB_TABLE_NAME);
        onCreate(db);
    }

    // Получить время последней записи
    public Calendar getLastDate(SQLiteDatabase db){
        Cursor cursor = db.query(GraphDatabaseHelper.DB_TABLE_NAME,
                new String[] {"MAX(" + GraphDatabaseHelper.KEY_DATE + ")"},
                null, null, null, null, null);

        cursor.moveToFirst();
        long dateLast = cursor.getLong(0);
        Calendar answer;
        answer = Calendar.getInstance();
        answer.setTimeInMillis(dateLast);

        cursor.close();

        return answer;
    }


    // Добавить новый Column
    public boolean addNewColumnHowInteger(SQLiteDatabase db, String strNewColum ){

        // Исправим возможные ошибки в таблице
        strNewColum = strNewColum.replace(" ", "");

        // TODO: Вылетает при передаче символов
        // Очистим название Column от первый цифр
        while (strNewColum.isEmpty() == false) {
            char firstChar = strNewColum.charAt(0);
            if (Character.isLetterOrDigit(firstChar) == true)
                // Стерем первую цифру
                strNewColum = strNewColum.substring(1);
            else
                break;
        }


        if(strNewColum.isEmpty() == true) {
            return false;
        }

        // Уже существующие столбцы
        Cursor cursor = db.query(GraphDatabaseHelper.DB_TABLE_NAME, null, null, null, null, null, null);
        String[] ColumnNamesAll = cursor.getColumnNames();
        int ColumnNamesAmt = cursor.getColumnCount();

        // Проверка что название нового столбца будет уникальное
        boolean isUniqueStr = true;
        for(int i = 0 ; i < ColumnNamesAmt; i++){
            if(ColumnNamesAll[i].equals(strNewColum))
                isUniqueStr = false;
        }

        // Добавим новый столбец если он уникальный
        if(isUniqueStr){
            db.execSQL("ALTER TABLE " + GraphDatabaseHelper.DB_TABLE_NAME + " ADD COLUMN " + strNewColum + " " + "NUMERIC");
        }
        cursor.close();

        return  isUniqueStr;
    }


    // TODO: Удалить таблицу полностью - метод
    // TODO: Переименовать Column в таблице - метод


    // TODO: Дату нужно обрабатывать отдельно
    public ArrayList getArrayCalendarHour(SQLiteDatabase db, Calendar startDate, Calendar finishDate){
        ArrayList<Calendar> answer = new ArrayList<Calendar>();

        Cursor cursor = db.query(GraphDatabaseHelper.DB_TABLE_NAME,
                new String[]{GraphDatabaseHelper.KEY_DATE},
                String.valueOf(GraphDatabaseHelper.KEY_DATE) + " >= ? " + "AND " +  GraphDatabaseHelper.KEY_DATE + " < ?",
                new String[]{Long.toString(startDate.getTimeInMillis()), Long.toString(finishDate.getTimeInMillis())},
                null, null, null);


        if (cursor.moveToFirst()) {
            int dateIndex = cursor.getColumnIndex(GraphDatabaseHelper.KEY_DATE);
            do {
                long dateRow_raw = cursor.getLong(dateIndex);

                Calendar dateTemp = Calendar.getInstance();
                dateTemp.setTimeInMillis(dateRow_raw);

                answer.add(dateTemp);
            } while (cursor.moveToNext());

        }

        cursor.close();
        return  answer;
    }


    // Получить данные в виде массива с одной даты на другую
    public ArrayList getArrayIntHour(SQLiteDatabase db, String KEY_XXX, Calendar startDate, Calendar finishDate){
        ArrayList<Integer> answer = new ArrayList<Integer>();

        // Проверка что ключ подходящий
        String[] columnsArray = getNameColumns(db);
        Arrays.sort(columnsArray);
        int indexArray = Arrays.binarySearch(columnsArray, KEY_XXX);

        // Значит такого ключа нет - вернем пустой массив
        if(indexArray < 0) {
            return answer;
        }


        Cursor cursor = db.query(GraphDatabaseHelper.DB_TABLE_NAME,
                new String[]{GraphDatabaseHelper.KEY_DATE, KEY_XXX},
                String.valueOf(GraphDatabaseHelper.KEY_DATE) + " >= ? " + "AND " +  GraphDatabaseHelper.KEY_DATE + " < ?",
                new String[]{Long.toString(startDate.getTimeInMillis()), Long.toString(finishDate.getTimeInMillis())},
                null, null, null);


        if (cursor.moveToFirst()) {
            int xxxIndex = cursor.getColumnIndex(KEY_XXX);
            do {
                int xxx = cursor.getInt(xxxIndex);
                answer.add(xxx);
            } while (cursor.moveToNext());

        }

        cursor.close();
        return  answer;
    }

    public String[] getNameColumns(SQLiteDatabase db){
        Cursor cursor = db.query(GraphDatabaseHelper.DB_TABLE_NAME, null, null, null, null, null, null);
        String[] ColumnNamesAll = cursor.getColumnNames();

        cursor.close();
        return  ColumnNamesAll;
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
            int step = 0;
            int money = 0;

            if (i.get(Calendar.HOUR_OF_DAY) > 0 && i.get(Calendar.HOUR_OF_DAY) < (random.nextInt(4) + 7) ) {
                sleep = 1;
            }
            else{
                step = Math.abs(random.nextInt(800)+200);

                if(random.nextInt(8) == 0 )
                    money = random.nextInt(100000);
                else
                    money = 0;
            }
            insertData(db, i, step, money, sleep);
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
