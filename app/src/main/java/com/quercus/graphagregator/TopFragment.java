package com.quercus.graphagregator;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class TopFragment extends Fragment implements View.OnClickListener {

    // Интерфейс что бы MainActivity знала на какую кнопку был нажат график
    public interface OnSelectedButtonGraphListener{
        void onButtonGraphSelected(int buttonIndex);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_top, container, false);

        Button button_graph1 = (Button)rootView.findViewById(R.id.graph1);
        Button button_graph2 = (Button)rootView.findViewById(R.id.graph2);
        Button button_graph_new = (Button)rootView.findViewById(R.id.graph_new);
        TextView textViewTopDay = (TextView)rootView.findViewById(R.id.top_day_text);

        button_graph1.setOnClickListener(this);
        button_graph2.setOnClickListener(this);
        button_graph_new.setOnClickListener(this);


        Calendar now = Calendar.getInstance();
        String strStatistic = getStatisticOfDay(now);
        textViewTopDay.setText(strStatistic);


        return rootView;
    }

    String getStatisticOfDay(Calendar date){
        String answer = "";
        answer += CalendarComplement.toStringDay(date) + "\n";

        GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] allColumns = dbHelper.getNameColumns(db);

        for(int i = 0 ; i < allColumns.length; i++){
            if(!allColumns[i].equals(GraphDatabaseHelper.KEY_DATE) && !allColumns[i].equals(GraphDatabaseHelper.KEY_ID) ){
                ArrayList valueOfDay = dbHelper.getArrayIntDay(db, allColumns[i], CalendarComplement.getStartDay(date), CalendarComplement.getFinishDay(date));

                // В массиве должно быть только одно значение и это сумма за день
                if(valueOfDay.size() == 1){
                    answer += "  " + valueOfDay.get(0) + " " + allColumns[i] + "\n";
                }
                else if(valueOfDay.size() == 0){
                    answer += "  " + 0 + " " + allColumns[i] + "\n";
                }
                else{
                    answer += "ERROR: Uncorrect date in TopFragment" + "\n";
                }

            }
        }


        return  answer;
    }


    // Определение какая кнопка была нажата
    int translateIdToIndex(int id){
        int index = -1;
        switch (id){
            case R.id.graph1:
                index = 1;
                break;
            case R.id.graph2:
                index = 2;
                break;
            case R.id.graph_new:
                index = 0;
                break;
        }
        return  index;
    }

    @Override
    public void onClick(View v) {
        int buttonIndex = translateIdToIndex(v.getId());

        OnSelectedButtonGraphListener listener = (OnSelectedButtonGraphListener) getActivity();
        listener.onButtonGraphSelected(buttonIndex);
    }
}
