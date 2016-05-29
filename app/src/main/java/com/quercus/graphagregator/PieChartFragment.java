package com.quercus.graphagregator;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

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

    // Данные для графика <int, i>
    ArrayList<Entry> entries = new ArrayList<>();
    // Лэйблы графика
    ArrayList<String> labels = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pie_chart, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    // Сформируем вывод строки в меню переключателя     < str >
    private String changeArrowTextAdd(int day_week_mounth) {

        // Сформируем ответ
        String answer = null;

        // Название недели и месяца - будут браться для нужного языка
        String[] name_week = getActivity().getResources().getStringArray(R.array.name_week);
        String[] name_month = getActivity().getResources().getStringArray(R.array.name_month);

        switch (position_calculation_system) {
            case 0:
                // Работаем с листанением дня
                int dateMaxDayInYear = date.getActualMaximum(Calendar.DAY_OF_YEAR);

                if (date.get(Calendar.DAY_OF_YEAR) == dateMaxDayInYear && day_week_mounth == 1) {
                    date.add(Calendar.YEAR, 1);
                    date.set(Calendar.DAY_OF_YEAR, 1);
                }
                else if(date.get(Calendar.DAY_OF_YEAR) == 1 && day_week_mounth == -1){
                    date.add(Calendar.YEAR, -1);

                    // Текущий год уже сменился - новое количество дней в году может быть новым
                    date.set(Calendar.DAY_OF_YEAR, date.getActualMaximum(Calendar.DAY_OF_YEAR));
                }
                else {
                    date.add(Calendar.DAY_OF_YEAR, day_week_mounth);
                }

                // Настройка форматирования текста
                // EX: Saturday, 3 September 2016
                answer = name_week[date.get(Calendar.DAY_OF_WEEK)-1] + ", " + date.get(Calendar.DAY_OF_MONTH) + " " + name_month[date.get(Calendar.MONTH)] + " " + date.get(Calendar.YEAR);

                break;
            case 1:
                // Работаем с листанием недели
                int dateMaxWeekInYear = date.getActualMaximum(Calendar.WEEK_OF_YEAR);

                if (date.get(Calendar.WEEK_OF_YEAR) == dateMaxWeekInYear && day_week_mounth == 1) {
                    date.add(Calendar.YEAR, 1);
                    date.set(Calendar.WEEK_OF_YEAR, 1);
                }
                else if(date.get(Calendar.WEEK_OF_YEAR) == 1 && day_week_mounth == -1){
                    date.add(Calendar.YEAR, -1);

                    // Текущая год уже сменилась - количество недель в году может быть новым
                    date.set(Calendar.WEEK_OF_YEAR, date.getActualMaximum(Calendar.WEEK_OF_YEAR));
                }
                else {
                    date.add(Calendar.WEEK_OF_YEAR, day_week_mounth);
                }

                // EX: 1 неделя 2016
                // EX: 2 мая - 9 мая 2016

                long dateInMillins  = date.getTimeInMillis();

                Calendar dateStartWeek = date;
                dateStartWeek.setTimeInMillis(dateInMillins);
                while (dateStartWeek.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
                    dateStartWeek.add(Calendar.DAY_OF_YEAR, -1);
                }

                Calendar dateEndWeek = Calendar.getInstance();
                dateEndWeek.setTimeInMillis(dateInMillins);
                while (dateEndWeek.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
                    dateEndWeek.add(Calendar.DAY_OF_YEAR, 1);
                }

                //answer = date.get(Calendar.WEEK_OF_YEAR) + ", week" + date.get(Calendar.YEAR);
                answer = dateStartWeek.get(Calendar.DAY_OF_MONTH) + " " + name_month[dateStartWeek.get(Calendar.MONTH)] + " - " +
                        dateEndWeek.get(Calendar.DAY_OF_MONTH) + " " + name_month[dateEndWeek.get(Calendar.MONTH)] + " " + dateEndWeek.get(Calendar.YEAR);

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

                // EX: May 2016
                answer = name_month[date.get(Calendar.MONTH)] + " " + date.get(Calendar.YEAR);
                break;
        }
        redrawChart();

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

                arrow_text.setText(str_new_date);

            }
        });

        // Обработка нажатия влево
        button_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str_new_date = changeArrowTextAdd(1);

                arrow_text.setText(str_new_date);
            }
        });


        // Обработка выпадающего списка
        spiner_calculation_data.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

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



        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Обработка самого графика

        // Получили ссылка на макет графика
    }

    // Изменение данных для графика
    private void redrawChart() {
        GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Начальная дата
        /*
        long dateNow = date.getTimeInMillis();
        Calendar date_1  = Calendar.getInstance();
        date_1.setTimeInMillis(dateNow);

        date_1.set(Calendar.HOUR_OF_DAY, 0);
        date_1.clear(Calendar.MINUTE);
        date_1.clear(Calendar.SECOND);
        date_1.clear(Calendar.MILLISECOND);
        */

        long dateNow = date.getTimeInMillis();
        Calendar date_1  = Calendar.getInstance();
        date_1.setTimeInMillis(dateNow);
        date_1.add(Calendar.DAY_OF_YEAR, -1);


        // Очистка от прошлых итераций
        labels.clear();
        entries.clear();


        ArrayList<Calendar> labels_row = dbHelper.getArrayCalendarHour(db, date_1, date);

        for(int i = 0 ; i < labels_row.size() ; i++){
            String str = labels_row.get(i).get(Calendar.HOUR_OF_DAY) + ":" + labels_row.get(i).get(Calendar.MINUTE);
            labels.add(str);
        }

        // Массив необработанных графиков

        ArrayList<Integer> dataRow = dbHelper.getArrayIntHour(db, GraphDatabaseHelper.KEY_STEP, date_1, date);

        for(int i = 0 ; i < dataRow.size() ; i++){
            entries.add(new Entry(dataRow.get(i), i));
        }



        View view = getView(); // Получение корневого объекта View фрагмента
        PieChart pieChart = (PieChart)view.findViewById(R.id.pie_chart);

        // данные dataset
        PieDataSet dataset = new PieDataSet(entries, "# of Calls");

        // Постройка графика
        PieData data = new PieData(labels, dataset);
        pieChart.setData(data);

        // Цвета
        dataset.setColors(ColorTemplate.PASTEL_COLORS); //
        pieChart.setDescription("Описание");
    }


    void RedawChart(){

    }

}
