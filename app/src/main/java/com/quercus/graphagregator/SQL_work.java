package com.quercus.graphagregator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// Библиотека для работы с графиками

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SQL_work extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sql_work, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();
        View view = getView(); // Получение корневого объекта View фрагмента

        // Получим кнопки с макета
        Button button_create = (Button)view.findViewById(R.id.sql_button_create);
        Button button_read = (Button)view.findViewById(R.id.sql_button_read);
        Button button_clear = (Button)view.findViewById(R.id.sql_button_clear);

        Button button_getCount = (Button)view.findViewById(R.id.sql_getCount);
        Button button_addColumn = (Button)view.findViewById(R.id.sql_update);
        Button button_getLastDate = (Button)view.findViewById(R.id.sql_getLastDate);
        Button button_getColumns = (Button)view.findViewById(R.id.sql_getColums);

        final EditText editText_newColumn = (EditText)view.findViewById(R.id.sql_editNewColumn);

        // Заполнить таблицу
        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                dbHelper.writeData(db);
                dbHelper.close();
            }
        });


        // Добавить столбец
        button_addColumn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                String strNewColumn = String.valueOf(editText_newColumn.getText());

                boolean result = dbHelper.addNewColumnHowInteger(db, strNewColumn);

                Toast toast = Toast.makeText(getActivity(), String.valueOf(result) , Toast.LENGTH_SHORT);
                toast.show();

                dbHelper.close();
            }
        });


        // Получить все столбцы
        button_getColumns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                String[] ColumnNamesAll = dbHelper.getNameColumns(db);

                // сформируем выходную строку
                String strAnswer = null;
                for(int i = 0 ; i < ColumnNamesAll.length ; i++){
                    strAnswer += ColumnNamesAll[i];
                    strAnswer += "\n";
                }

                Toast toast = Toast.makeText(getActivity(), strAnswer  , Toast.LENGTH_LONG);
                toast.show();

                dbHelper.close();
            }
        });

        // Получить последнюю запись
        button_getLastDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
                SQLiteDatabase db = dbHelper.getReadableDatabase();


                Calendar dateLast = dbHelper.getLastDate(db);

                DateFormat df;
                df = new SimpleDateFormat("d MMM, y - HH:mm");
                String strDate = df.format(dateLast.getTimeInMillis());

                Toast toast = Toast.makeText(getActivity(), "Последняя запись в: " + strDate, Toast.LENGTH_SHORT);
                toast.show();

                dbHelper.close();
            }
        });



        // Количество строк в таблицу
        button_getCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                Toast toast = Toast.makeText(getActivity(), "Количество строк: " + String.valueOf(dbHelper.getCountRows(db)), Toast.LENGTH_SHORT);
                toast.show();

                dbHelper.close();
            }
        });



        button_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                // TODO: Временный вывод результатов за несколько дней
                Calendar date_1 = Calendar.getInstance();
                date_1.set(Calendar.HOUR_OF_DAY, 0);
                date_1.add(Calendar.DAY_OF_YEAR, -1);
                date_1.add(Calendar.DAY_OF_YEAR, -1);

                Cursor cursor = db.query(GraphDatabaseHelper.DB_TABLE_NAME,
                        new String[]{GraphDatabaseHelper.KEY_DATE, GraphDatabaseHelper.KEY_STEP},
                        String.valueOf(GraphDatabaseHelper.KEY_DATE) + " > ?",
                        new String[]{Long.toString(date_1.getTimeInMillis())},
                        null, null, null);



                if (cursor.moveToFirst()) {
                    //int idIndex = cursor.getColumnIndex(GraphDatabaseHelper.KEY_ID);
                    int dateIndex = cursor.getColumnIndex(GraphDatabaseHelper.KEY_DATE);
                    int stepIndex = cursor.getColumnIndex(GraphDatabaseHelper.KEY_STEP);
                    //int moneyIndex = cursor.getColumnIndex(GraphDatabaseHelper.KEY_MONEY);
                    //int sleepIndex = cursor.getColumnIndex(GraphDatabaseHelper.KEY_SLEEP);

                    do {

                        // Получим значение и преобразуем их
                        //int id = cursor.getInt(idIndex);
                        long data_time_raw = cursor.getLong(dateIndex);
                        int step = cursor.getInt(stepIndex);

                        //int money = cursor.getInt(moneyIndex);
                        //int sleep = cursor.getInt(sleepIndex);

                        int money = 0;
                        int sleep = 0;
                        int id = 0;

                        // Просто для красивого вывода
                        DateFormat df;
                        df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                        String strDate = df.format(data_time_raw);



                        // Выведем их на консоль
                        Log.d("mLog", "ID = " + id +
                                ", date = " + strDate +
                                ", money = " + money +
                                ", sleep = " + sleep +
                                ", step = " + step);
                    } while (cursor.moveToNext());
                } else
                    Log.d("mLog","0 rows");

                cursor.close();
                db.close();
            }
        });



        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                dbHelper.clearData(db);

                dbHelper.close();
            }
        });


        if(view != null){

        }
    }

}
