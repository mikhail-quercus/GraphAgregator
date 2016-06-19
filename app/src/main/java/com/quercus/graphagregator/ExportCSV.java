package com.quercus.graphagregator;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class ExportCSV extends Fragment {

    private static final String LOG_TAG = "myLog";
    Spinner spinnerSelectDirectory;
    EditText editTextWriteFileName;
    Button buttonStartExport;
    Spinner spinnerColumns;
    Button buttonAddColumn;
    Button buttonDelColumn;
    TextView viewSelectedColumns;
    TextView textViewtDateStart;
    TextView textViewDateFinish;


    String fileName = "";
    String pathDirectory = "";
    String pathDirectoryChild = "";
    String[] allFolderInHomeFolder;

    Calendar date_start = Calendar.getInstance();
    Calendar date_finish = Calendar.getInstance();

    int positionSelectedColumns = -1;

    String[] columnsAll;
    ArrayList<String> columnSelected = new ArrayList<String>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_export_csv, container, false);

        spinnerSelectDirectory = (Spinner)rootView.findViewById(R.id.export_path_to_dir);
        editTextWriteFileName = (EditText)rootView.findViewById(R.id.export_file_name);
        buttonStartExport = (Button)rootView.findViewById(R.id.export_start);
        spinnerColumns = (Spinner)rootView.findViewById(R.id.export_select_columns);
        buttonAddColumn = (Button)rootView.findViewById(R.id.export_select_column_add);
        buttonDelColumn = (Button)rootView.findViewById(R.id.export_select_column_del);
        viewSelectedColumns = (TextView) rootView.findViewById(R.id.export_view_column);
        textViewtDateStart = (TextView)rootView.findViewById(R.id.export_date_start);
        textViewDateFinish = (TextView)rootView.findViewById(R.id.export_date_finish);


        // Настройка Spinner
        GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        columnsAll = dbHelper.getNameColumns(db);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, columnsAll);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColumns.setAdapter(adapter);
        spinnerColumns.setPrompt("Select columns");

        date_start = dbHelper.getFirstDate(db);
        date_finish = dbHelper.getLastDate(db);

        textViewtDateStart.setText(CalendarComplement.toStringYYYYMMDDHHMM(date_start));
        textViewDateFinish.setText(CalendarComplement.toStringYYYYMMDDHHMM(date_finish));



        dbHelper.close();

        // Предоставим выбор пути к папке
        File sdPath = Environment.getExternalStorageDirectory();
        allFolderInHomeFolder = sdPath.list();
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, allFolderInHomeFolder);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelectDirectory.setAdapter(adapter2);
        spinnerSelectDirectory.setPrompt(sdPath.toString());





        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        spinnerColumns.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                positionSelectedColumns = selectedItemPosition;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        spinnerSelectDirectory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                pathDirectoryChild = allFolderInHomeFolder[selectedItemPosition];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        buttonAddColumn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Добавим элемент
                columnSelected.add(columnsAll[positionSelectedColumns]);

                // Выведем новое поле на экран
                String answer = "";
                for (int i = 0; i < columnSelected.size(); i++) {
                    answer += columnSelected.get(i) + "\n";
                }

                viewSelectedColumns.setText(answer);
            }
        });

        textViewtDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        textViewDateFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getActivity(), "2", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        buttonDelColumn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!columnSelected.isEmpty())
                    columnSelected.remove(columnSelected.size()-1);

                // Выведем новое поле на экран
                String answer = "";
                for (int i = 0; i < columnSelected.size(); i++) {
                    answer += columnSelected.get(i) + "\n";
                }

                viewSelectedColumns.setText(answer);
            }
        });



        buttonStartExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                fileName = String.valueOf(editTextWriteFileName.getText()) + ".csv";

                if(fileName != "" && positionSelectedColumns != -1 && !columnSelected.isEmpty()){

                    String answer = "";
                    for(int h = 0 ; h < columnSelected.size() ; h++)
                        answer += String.valueOf(columnSelected.get(h)) + "\n";

                    Toast toast = Toast.makeText(getActivity(), answer, Toast.LENGTH_LONG);
                    toast.show();

                    // проверяем доступность SD
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
                    }
                    else {
                        // получаем путь к SD
                        File sdPath = Environment.getExternalStorageDirectory();
                        // добавляем свой каталог к пути
                        // Сохраним
                        pathDirectory = sdPath.getAbsolutePath();

                        sdPath = new File(sdPath.getAbsolutePath() + "/" + pathDirectoryChild);
                        // формируем объект File, который содержит путь к файлу
                        File sdFile = new File(sdPath, fileName);


                        ArrayList<Calendar> dateWriteFromFile = dbHelper.getArrayCalendarHour(db, date_start, date_finish);

                        ArrayList<Long> allData = new ArrayList<Long>();

                        for(int i = 0 ; i < columnSelected.size() ; i++){
                            ArrayList<Integer> temp = dbHelper.getArrayIntHour(db, columnSelected.get(i),date_start, date_finish);

                            if(columnSelected.get(i).equals(GraphDatabaseHelper.KEY_DATE))
                            {
                                for(int j = 0 ; j < dateWriteFromFile.size() ; j++){
                                    allData.add(dateWriteFromFile.get(j).getTimeInMillis());
                                }
                            }
                            else
                            {
                                for(int j = 0 ; j < temp.size() ; j++){
                                    allData.add(Long.valueOf(temp.get(j)));
                                }
                            }

                        }



                        try {
                            // открываем поток для записи
                            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));

                            // Запишем пока не закончаться даты
                            for(int d = 0 ; d < dateWriteFromFile.size() ; d++) {

                                // пишем данные
                                String oneRowFromFile = "";


                                if(d == 0){
                                    // Запишем шапку файла
                                    String answerTitle = "";
                                    for(int i = 0 ; i < columnSelected.size() ; i++) {
                                        answerTitle += columnSelected.get(i);

                                        // Если не последний агрумент, запишем
                                        if(i != columnSelected.size()-1)
                                            answerTitle += ", ";
                                    }

                                    bw.write(answerTitle);
                                    bw.newLine();
                                }



                                for (int c = 0 ; c < columnSelected.size(); c++)
                                {
                                    int temp_index = d + (c * dateWriteFromFile.size());


                                    if(temp_index < allData.size())
                                        oneRowFromFile += allData.get(temp_index);
                                    else
                                        oneRowFromFile += "<NULL>";


                                    // Если не последний агрумент, запишем
                                    if(c != columnSelected.size()-1)
                                        oneRowFromFile += ", ";
                                }



                                bw.write(oneRowFromFile);
                                bw.newLine();
                            }

                            // закрываем поток
                            bw.close();
                            Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());

                            Toast toast2 = Toast.makeText(getActivity(), "MESSANGE: File write " + pathDirectory + "/" + pathDirectoryChild + "/" + fileName, Toast.LENGTH_LONG);
                            toast2.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                else
                {
                    Toast toast = Toast.makeText(getActivity(), R.string.error_not_correct_name_file, Toast.LENGTH_LONG);
                    toast.show();
                }


            }
        });

    }

}
