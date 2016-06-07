package com.quercus.graphagregator;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
    String KEY_XXX = "";
    String name_graph = "";

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
        // Можно выполнить работу не связанную с интерфейсами
        super.onCreate(savedInstanceState);

        Bundle bundle_get = getArguments();
        if(bundle_get != null) {
            KEY_XXX = bundle_get.getString("key_xxx");
            name_graph = bundle_get.getString("name_graph");
        }
    }


    // Сформируем вывод строки в меню переключателя     < str >
    private String changeArrowTextAdd(int day_week_mounth) {

        // Сформируем ответ
        String answer = null;

        switch (position_calculation_system) {
            case 0:
                // Листание дней
                if(day_week_mounth == 1)
                    date = CalendarComplement.incrementDay(date);
                else
                    date = CalendarComplement.decrementDay(date);
                answer = CalendarComplement.toStringDay(date);

                break;
            case 1:
                // Работаем с листанием недели
                if(day_week_mounth == 1)
                    date = CalendarComplement.incrementWeek(date);
                else
                    date = CalendarComplement.decrementWeek(date);
                answer = CalendarComplement.toStringWeek(date);
                break;
            case 2:
                // Работаем с листанием месяца
                if(day_week_mounth == 1)
                    date = CalendarComplement.incrementMonth(date);
                else
                    date = CalendarComplement.decrementMonth(date);
                answer = CalendarComplement.toStringMonth(date);
                break;
        }

        updateChart();
        return answer;
    }

    private void updateChart(){
        updateArrayEntryAndLabels();

        View view = getView(); // Получение корневого объекта View фрагмента

        // Макет графика
        final PieChart pieChart = (PieChart)view.findViewById(R.id.pie_chart);

        // данные dataset
        PieDataSet dataset_new = new PieDataSet(entries, "Легенда графика");

        // Цвета
        dataset_new.setColors(ColorTemplate.PASTEL_COLORS); //
        //pieChart.setDescription("Описание2");

        // Вывести в центре круга - день сегодня + название графика
        pieChart.setCenterText(name_graph);
        pieChart.setDrawSliceText(true);

        // Постройка графика
        PieData data_new = new PieData(labels, dataset_new);
        pieChart.setData(data_new);

        // Необъодимо что бы отрисовка была сразу-же измененна, иначе необходим клик на область
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }


    // Обновление данных в классе
    private void updateArrayEntryAndLabels(){
        GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();



        // Очистка от старых данных
        labels.clear();
        entries.clear();

        switch (position_calculation_system) {
            case 0:
                // Листание дней

                // Начальная дата
                Calendar date_1 = CalendarComplement.getStartDay(date);
                Calendar date_2 = CalendarComplement.getFinishDay(date);

                ArrayList<Calendar> labels_row = dbHelper.getArrayCalendarHour(db, date_1, date_2);

                for(int i = 0 ; i < labels_row.size() ; i++){
                    String str = CalendarComplement.toStringHour(labels_row.get(i));
                    labels.add(str);
                }

                // Массив необработанных графиков

                ArrayList<Integer> dataRow = dbHelper.getArrayIntHour(db, KEY_XXX, date_1, date_2);

                for(int i = 0 ; i < dataRow.size() ; i++){
                    entries.add(new Entry(dataRow.get(i), i));
                }

                break;
            case 1:
                // Работаем с листанием недели

                // Начальная дата
                Calendar dateStartWeek = CalendarComplement.getStartWeekDay(date);

                // Массив необработанных графиков


                break;

            case 2:
                // Работаем с листанием месяца

                break;
        }



        dbHelper.close();
    }


    @Override
    public void onStart(){
        super.onStart();
        View view = getView(); // Получение корневого объекта View фрагмента

        // Макет графика
        final PieChart pieChart = (PieChart)view.findViewById(R.id.pie_chart);

        // Получим кнопки с макета
        ImageButton button_left = (ImageButton)view.findViewById(R.id.arrow_left);
        ImageButton button_right = (ImageButton)view.findViewById(R.id.arrow_right);

        final TextView arrow_text = (TextView)view.findViewById(R.id.arrow_text);
        arrow_text.setText( changeArrowTextAdd(0));

        // Выпадающий список - выбор масштаба анализа  день/неделя/месяц
        final Spinner spiner_calculation_data = (Spinner)view.findViewById(R.id.calculation_system_data);

        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // =================================================================
        // Обработка самого графика



        // =====================================================================================



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

    }


}
