package com.quercus.graphagregator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

// Библиотека для работы с графиками
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PieChartFragment extends Fragment {

    Calendar date = Calendar.getInstance(); // Установим время сейчас
    int position_calculation_system = 0; // день / неделя / месяц
    //String start_arrow_text = changeArrowTextAdd(0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pie_chart, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    // TODO: Сделать что бы отображалось текущая дата при запуске
    // Сформируем вывод строки в меню переключателя     < str >
    private String changeArrowTextAdd(int day_week_mounth) {

        // Сформируем ответ
        String answer = null;

        String[] name_week = getActivity().getResources().getStringArray(R.array.name_week);
        String[] name_month = getActivity().getResources().getStringArray(R.array.name_month);

        // TODO: Должны быть проблемы с пограничным состоянием листания даты и недели
        switch (position_calculation_system) {
            case 0:
                // Работаем с листанением дня
                date.add(Calendar.DAY_OF_YEAR, day_week_mounth);
                answer = name_week[date.get(Calendar.DAY_OF_WEEK)-1] + ", " + date.get(Calendar.DAY_OF_MONTH) + " " + name_month[date.get(Calendar.MONTH)];
                break;
            case 1:
                // Работаем с листанием недели
                // TODO: Реализоовать переключение недели
                date.add(Calendar.WEEK_OF_YEAR, day_week_mounth);
                answer = date.get(Calendar.WEEK_OF_YEAR) + ", week";
                break;
            case 2:
                // Работаем с листанием месяца
                // Нужно отлавливать состояние для граничных месяцев для перелистывание года
                if (date.get(Calendar.MONTH) == Calendar.DECEMBER && day_week_mounth == 1) {
                    date.add(Calendar.YEAR, 1);
                    date.set(Calendar.MONTH, Calendar.JANUARY);
                }
                else if(date.get(Calendar.MONTH) == Calendar.JANUARY && day_week_mounth == -1){
                    date.add(Calendar.YEAR, -1);
                    date.set(Calendar.MONTH, Calendar.DECEMBER);
                }
                else{
                    date.add(Calendar.MONTH, day_week_mounth);
                }
                answer = name_month[date.get(Calendar.MONTH)] + ", " + date.get(Calendar.YEAR);



        }


        return answer;
    }


    @Override
    public void onStart(){
        super.onStart();
        View view = getView(); // Получение корневого объекта View фрагмента

        // Получим кнопки с макета
        ImageButton button_left = (ImageButton)view.findViewById(R.id.arrow_left);
        ImageButton button_right = (ImageButton)view.findViewById(R.id.arrow_right);
        final TextView arrow_text = (TextView)view.findViewById(R.id.arrow_text);
        arrow_text.setText( changeArrowTextAdd(0));

        // Выпадающий список - выбор масштаба анализа  день/неделя/месяц
        final Spinner spiner_calculation_data = (Spinner)view.findViewById(R.id.calculation_system_data);


        // Обработка нажатия влево
        button_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_new_date = changeArrowTextAdd(-1);

                arrow_text.setText( str_new_date );

            }
        });

        // Обработка нажатия влево
        button_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str_new_date = changeArrowTextAdd(1);

                arrow_text.setText( str_new_date );
            }
        });


        // Обработка выпадающего списка
        spiner_calculation_data.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                /*
                String[] choose = getResources().getStringArray(R.array.calculation_system_data);
                Toast toast = Toast.makeText(getActivity(),
                        "Ваш выбор: " + choose[selectedItemPosition], Toast.LENGTH_SHORT);
                toast.show();
                */

                // Сохраним состояние для использование в дальнейшем
                position_calculation_system = selectedItemPosition;

                // Изменение текста в верхнем меню
                String arrow_new = changeArrowTextAdd(0);
                arrow_text.setText(arrow_new);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });








        // Получили ссылка на макет графика
        PieChart pieChart = (PieChart)view.findViewById(R.id.pie_chart);


        // Массив необработанных графиков
        ArrayList<Entry> entries = new ArrayList<>();

        // Лэйблы графика
        ArrayList<String> labels = new ArrayList<String>();


        GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Calendar date_1 = Calendar.getInstance();
        date_1.set(Calendar.HOUR_OF_DAY, 0);

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
                int i = 0;

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
                df = new SimpleDateFormat("HH:mm");
                String strDate = df.format(data_time_raw);

                // Запищем в массив для отображение графика
                entries.add(new Entry(step, i));
                labels.add(strDate);


                i++;
            } while (cursor.moveToNext());
        } else
            Log.d("mLog","0 rows");

        cursor.close();
        db.close();




        // данные dataset
        PieDataSet dataset = new PieDataSet(entries, "# of Calls");



        // Постройка графика
        PieData data = new PieData(labels, dataset);
        pieChart.setData(data);

        // Цвета
        dataset.setColors(ColorTemplate.PASTEL_COLORS); //
        pieChart.setDescription("Описание");
    }

}
