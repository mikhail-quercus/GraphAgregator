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

    // Получить время последней записи
    public Calendar getFirstDate(SQLiteDatabase db){
        Cursor cursor = db.query(GraphDatabaseHelper.DB_TABLE_NAME,
                new String[] {"MIN(" + GraphDatabaseHelper.KEY_DATE + ")"},
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
    public boolean addNewColumnHowInteger(SQLiteDatabase db, String KEY_XXX_ROW ){


        String KEY_XXX = "";

        // Исправим возможные ошибки в название строки
        for (int i = 0; i < KEY_XXX_ROW.length(); i++) {
            if (Character.isLetterOrDigit(KEY_XXX_ROW.charAt(i))) {
                KEY_XXX += KEY_XXX_ROW.charAt(i);
            }

        }

        // Если исправленный от всего лишнего столбец пустой - выходим
        if(KEY_XXX == "")
            return false;

        // Если название начинается с цифры - выходим
        if (Character.isDigit(KEY_XXX.charAt(0)) == true)
            return false;



        // Уникальный столбец?
        boolean isUniqueStr = (isThisNameColumnExist(db, KEY_XXX) == false);

        // Добавим новый столбец если он уникальный
        if(isUniqueStr){
            db.execSQL("ALTER TABLE " + GraphDatabaseHelper.DB_TABLE_NAME + " ADD COLUMN " + KEY_XXX + " " + "NUMERIC");
        }


        return  isUniqueStr;
    }


    public int getIntToDate(SQLiteDatabase db, String KEY_XXX, Calendar date){
        int answer = 0;

        // Проверка что ключ подходящий
        if(isThisNameColumnExist(db, KEY_XXX) == false)
            return answer;

        Cursor cursor = db.query(GraphDatabaseHelper.DB_TABLE_NAME,
                new String[]{GraphDatabaseHelper.KEY_DATE, KEY_XXX},
                String.valueOf(GraphDatabaseHelper.KEY_DATE) + " == ? ",
                new String[]{Long.toString(date.getTimeInMillis())},
                null, null, null);




        if (cursor.moveToFirst()) {
            int xxxIndex = cursor.getColumnIndex(KEY_XXX);
            do {
                int xxx = cursor.getInt(xxxIndex);
                answer = xxx;
                cursor.close();
                return answer;
            } while (cursor.moveToNext());

        }

        cursor.close();
        return  answer;
    }


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
    public ArrayList getArrayCalendarDay(SQLiteDatabase db, Calendar startDate, Calendar finishDate){
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

                if(dateTemp.get(Calendar.HOUR_OF_DAY) == 0) {
                    answer.add(dateTemp);
                }

            } while (cursor.moveToNext());

        }

        cursor.close();
        return  answer;
    }
    public ArrayList getArrayCalendarMonth(SQLiteDatabase db, Calendar startDate, Calendar finishDate){
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

                if(dateTemp.get(Calendar.HOUR_OF_DAY) == 0 && dateTemp.get(Calendar.DAY_OF_MONTH) == dateTemp.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    answer.add(dateTemp);
                }

            } while (cursor.moveToNext());

        }

        cursor.close();
        return  answer;
    }

    public ArrayList getArrayIntHour(SQLiteDatabase db, String KEY_XXX, Calendar startDate, Calendar finishDate){
        ArrayList<Integer> answer = new ArrayList<Integer>();

        // Проверка что ключ подходящий
        if(isThisNameColumnExist(db, KEY_XXX) == false)
            return answer;

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
    public ArrayList getArrayIntDay(SQLiteDatabase db, String KEY_XXX, Calendar startDate, Calendar finishDate){
        ArrayList<Integer> answer = new ArrayList<Integer>();

        // Проверка что ключ подходящий
        if(isThisNameColumnExist(db, KEY_XXX) == false)
            return answer;

        Cursor cursor = db.query(GraphDatabaseHelper.DB_TABLE_NAME,
                new String[]{GraphDatabaseHelper.KEY_DATE, KEY_XXX},
                String.valueOf(GraphDatabaseHelper.KEY_DATE) + " >= ? " + "AND " +  GraphDatabaseHelper.KEY_DATE + " < ?",
                new String[]{Long.toString(startDate.getTimeInMillis()), Long.toString(finishDate.getTimeInMillis())},
                null, null, null);



        int sum_of_day = 0;
        if (cursor.moveToFirst()) {
            int xxxIndex = cursor.getColumnIndex(KEY_XXX);
            int dateIndex = cursor.getColumnIndex(GraphDatabaseHelper.KEY_DATE);
            do {
                int xxx = cursor.getInt(xxxIndex);

                long dateRow_raw = cursor.getLong(dateIndex);
                Calendar dateTemp = Calendar.getInstance();
                dateTemp.setTimeInMillis(dateRow_raw);

                // В последний час дня - запишем сумму результатов
                if(dateTemp.get(Calendar.HOUR_OF_DAY) == 23) {
                    answer.add(sum_of_day);
                    sum_of_day = 0;
                }
                else
                    sum_of_day += xxx;

            } while (cursor.moveToNext());
        }

        // Если не нулевой результат - значит день еще не закончился - запишем что есть
        if(sum_of_day != 0)
            answer.add(sum_of_day);

        cursor.close();
        return  answer;
    }
    public ArrayList getArrayIntMonth(SQLiteDatabase db, String KEY_XXX, Calendar startDate, Calendar finishDate){
        ArrayList<Integer> answer = new ArrayList<Integer>();

        if(isThisNameColumnExist(db, KEY_XXX) == false)
            return answer;

        Cursor cursor = db.query(GraphDatabaseHelper.DB_TABLE_NAME,
                new String[]{GraphDatabaseHelper.KEY_DATE, KEY_XXX},
                String.valueOf(GraphDatabaseHelper.KEY_DATE) + " >= ? " + "AND " +  GraphDatabaseHelper.KEY_DATE + " < ?",
                new String[]{Long.toString(startDate.getTimeInMillis()), Long.toString(finishDate.getTimeInMillis())},
                null, null, null);



        int sum_of_month = 0;
        if (cursor.moveToFirst()) {
            int xxxIndex = cursor.getColumnIndex(KEY_XXX);
            int dateIndex = cursor.getColumnIndex(GraphDatabaseHelper.KEY_DATE);
            do {
                int xxx = cursor.getInt(xxxIndex);

                long dateRow_raw = cursor.getLong(dateIndex);
                Calendar dateTemp = Calendar.getInstance();
                dateTemp.setTimeInMillis(dateRow_raw);

                // В последний час дня в последнем дне месяца- запишем сумму результатов
                if(dateTemp.get(Calendar.HOUR_OF_DAY) == 23 && dateTemp.get(Calendar.DAY_OF_MONTH) == dateTemp.getActualMaximum(Calendar.DAY_OF_MONTH) ) {
                    answer.add(sum_of_month);
                    sum_of_month = 0;
                }
                else
                    sum_of_month += xxx;

            } while (cursor.moveToNext());
        }

        // Если не нулевой результат - значит день еще не закончился - запишем что есть
        if(sum_of_month != 0)
            answer.add(sum_of_month);

        cursor.close();
        return  answer;
    }

    public String[] getNameColumns(SQLiteDatabase db){
        Cursor cursor = db.query(GraphDatabaseHelper.DB_TABLE_NAME, null, null, null, null, null, null);
        String[] ColumnNamesAll = cursor.getColumnNames();

        cursor.close();
        return  ColumnNamesAll;
    }

    public boolean isThisNameColumnExist(SQLiteDatabase db, String KEY_XXX){
        String[] columnsArray = getNameColumns(db);
        Arrays.sort(columnsArray);
        int indexArray = Arrays.binarySearch(columnsArray, KEY_XXX);

        // Проверка что такой ключ есть
        if(indexArray < 0) {
            return false;
        }
        else
            return true;
    }

    // Записать тестовые данные
    public void writeData(SQLiteDatabase db) {
        final Random random = new Random();

        Calendar start_year = Calendar.getInstance();
        start_year.set(Calendar.YEAR, 2016);
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

            if (i.get(Calendar.HOUR_OF_DAY) > 0 && i.get(Calendar.HOUR_OF_DAY) < (random.nextInt(4) + 3) ) {
                sleep = 1;
                step = Math.abs(random.nextInt(800)+200);
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

    public boolean updateDataAndClear(SQLiteDatabase db, Calendar data_time, String KEY_XXX ,int xxx){


        // Это запись пытается добавиться раньше чем была создана эта таблица
        Calendar firstDateInTable = getFirstDate(db);
        if(data_time.getTimeInMillis() < firstDateInTable.getTimeInMillis() )
            return false;

        ContentValues contentValues = new ContentValues();


        // Перевод в UNIX-time для записи в БД времени
        contentValues.put(KEY_DATE, data_time.getTimeInMillis());
        contentValues.put(KEY_XXX, xxx);

        //db.insert(DB_TABLE_NAME, null, contentValues);
        int numberUpdateRecords  = db.update(DB_TABLE_NAME, contentValues, KEY_DATE + " = ?", new String[]{String.valueOf(data_time.getTimeInMillis())} );

        return true;
    }


    public boolean updateDataAndSum(SQLiteDatabase db, Calendar data_time, String KEY_XXX ,int xxx){


        // Это запись пытается добавиться раньше чем была создана эта таблица
        Calendar firstDateInTable = getFirstDate(db);
        if(data_time.getTimeInMillis() < firstDateInTable.getTimeInMillis() )
            return false;

        ContentValues contentValues = new ContentValues();
        ContentValues contentValues_old = new ContentValues();

        int xxx_old = getIntToDate(db, KEY_XXX, data_time);

        // Перевод в UNIX-time для записи в БД времени
        contentValues.put(KEY_DATE, data_time.getTimeInMillis());
        contentValues.put(KEY_XXX, xxx + xxx_old);



        //db.insert(DB_TABLE_NAME, null, contentValues);
        int numberUpdateRecords  = db.update(DB_TABLE_NAME, contentValues, KEY_DATE + " = ?", new String[]{String.valueOf(data_time.getTimeInMillis())} );

        return true;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + DB_TABLE_NAME);

        onCreate(db);

    }
}
