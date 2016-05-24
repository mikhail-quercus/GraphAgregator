package com.quercus.graphagregator;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

// Библиотека для работы с графиками
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

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

        /*
        answer = name_week[date.get(Calendar.DAY_OF_WEEK) - 1] + ", "
                + date.get(Calendar.DAY_OF_MONTH) + name_month[date.get(Calendar.MONTH) - 1];
                */

        switch (position_calculation_system) {
            case 0:
                // Работаем с листанением дня
                date.add(Calendar.DAY_OF_YEAR, day_week_mounth);
                answer = name_week[date.get(Calendar.DAY_OF_WEEK)-1] + ", " + date.get(Calendar.DAY_OF_MONTH) + " " + name_month[date.get(Calendar.MONTH)];
                break;
            case 1:
                // Работаем с листанием недели
                // TODO: Пограничные состояния - начало в одном месяце, конец в другом.
                Calendar date_start = date;
                date_start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

                answer = date_start.get(Calendar.DAY_OF_MONTH) + " - " + date.get(Calendar.DAY_OF_MONTH) + name_month[date.get(Calendar.MONTH)];
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
        Button button_left = (Button)view.findViewById(R.id.arrow_left);
        Button button_right = (Button)view.findViewById(R.id.arrow_right);
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

                // Отрисуем заново отображение верхнего меню
                //changeArrowTextAdd(0);
                arrow_text.setText(String.valueOf(position_calculation_system));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        // Получили ссылка на макет графика
        PieChart pieChart = (PieChart)view.findViewById(R.id.pie_chart);


        // Массив необработанных графиков
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(4f, 0));
        entries.add(new Entry(8f, 1));
        entries.add(new Entry(6f, 2));
        entries.add(new Entry(2f, 3));
        entries.add(new Entry(18f, 4));
        entries.add(new Entry(9f, 5));


        // данные dataset
        PieDataSet dataset = new PieDataSet(entries, "# of Calls");

        // Лэйблы графика
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");

        // Постройка графика
        PieData data = new PieData(labels, dataset);
        pieChart.setData(data);

        // Цвета
        dataset.setColors(ColorTemplate.PASTEL_COLORS); //
        pieChart.setDescription("Описание");
    }

}
