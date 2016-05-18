package com.quercus.graphagregator;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// Библиотекап для работы с графиками
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class PieChartFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pie_chart, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        View view = getView(); // Получение корневого объекта View фрагмента

        if(view != null){
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

}
