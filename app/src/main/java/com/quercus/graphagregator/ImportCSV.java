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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

public class ImportCSV extends Fragment implements View.OnClickListener {

    Button button_add_file;
    TextView temp_text;
    TextView view_text_file;
    RadioGroup radioGroup_method_write_row;
    Spinner spinner_select_value;
    Spinner spinner_select_YYYY;
    Spinner spinner_select_MM;
    Spinner spinner_select_DD;
    Spinner spinner_select_hh_mm_ss;
    EditText edit_text_new_column;
    Button button_update_database;

    String DIR_SD = "QUERCUS";
    String FILENAME_SD = "test_file.csv";
    String FilePath = "";

    final String LOG_TAG = "myLogs";


    int position_values = -1;
    int position_method_write_values = -1;
    int position_YYYY = -1;
    int position_MM = -1;
    int position_DD = -1;
    int position_hh_mm_ss = -1;


    private static final int PICKFILE_RESULT_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_import_csv, container, false);

        button_add_file = (Button)rootView.findViewById(R.id.import_csv_add_file);
        temp_text = (TextView)rootView.findViewById(R.id.test_name_file);
        view_text_file = (TextView)rootView.findViewById(R.id.import_file_read_example);
        radioGroup_method_write_row = (RadioGroup)rootView.findViewById(R.id.import_select_method_write_row);
        spinner_select_value = (Spinner)rootView.findViewById(R.id.import_select_values);
        spinner_select_YYYY = (Spinner)rootView.findViewById(R.id.import_select_YYYY);
        spinner_select_MM = (Spinner)rootView.findViewById(R.id.import_select_MM);
        spinner_select_DD = (Spinner)rootView.findViewById(R.id.import_select_DD);
        spinner_select_hh_mm_ss = (Spinner)rootView.findViewById(R.id.import_select_hh_mm_ss);

        edit_text_new_column = (EditText)rootView.findViewById(R.id.import_new_column);
        button_update_database = (Button)rootView.findViewById(R.id.import_button_update_database);


        button_add_file.setOnClickListener(this);
        button_update_database.setOnClickListener(this);


        return rootView;
    }


    @Override
    public void onStart(){
        super.onStart();

        // Обработка выпадающего списка
        spinner_select_value.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                // Сохраним состояние для использование в дальнейшем
                position_values = selectedItemPosition;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        spinner_select_YYYY.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                // Сохраним состояние для использование в дальнейшем
                position_YYYY = selectedItemPosition;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        spinner_select_MM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                // Сохраним состояние для использование в дальнейшем
                position_MM = selectedItemPosition;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        spinner_select_DD.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                // Сохраним состояние для использование в дальнейшем
                position_DD = selectedItemPosition;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        spinner_select_hh_mm_ss.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                // Сохраним состояние для использование в дальнейшем
                position_hh_mm_ss = selectedItemPosition;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        radioGroup_method_write_row.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch (checkedId) {
                    case R.id.radio_button_1_num:
                        position_method_write_values = 0;
                        break;
                    case R.id.radio_button_2_sum:
                        position_method_write_values = 1;
                        break;
                    case R.id.radio_button_3_max:
                        position_method_write_values = 2;
                        break;
                }
            }
        });

        // Тут выполним итоговую загрузку
        button_update_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(position_YYYY < 0 || position_MM < 0 || position_DD < 0 || position_hh_mm_ss < 0 || position_hh_mm_ss < 0 || position_method_write_values < 0 || position_values < 0) {
                    Toast toast = Toast.makeText(getActivity(), "???", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
                    SQLiteDatabase db = dbHelper.getReadableDatabase();

                    // Проверка на введенный столбец
                    String nameNewColumn = String.valueOf(edit_text_new_column.getText());
                    boolean isCreateNewColumn = dbHelper.addNewColumnHowInteger(db, nameNewColumn);

                    if(isCreateNewColumn == true){
                        Toast toast = Toast.makeText(getActivity(), "!!!", Toast.LENGTH_SHORT);
                        toast.show();

                        importCSVtoDataBase(FilePath, nameNewColumn);

                    }
                    else {
                        Toast toast = Toast.makeText(getActivity(), R.string.error_not_correct_name_column, Toast.LENGTH_LONG);
                        toast.show();
                    }



                }


            }
        });

    }

    @Override
    public void onClick(View v) {
        // Получим входной файл
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch(requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode == Activity.RESULT_OK){
                    FilePath = data.getData().getPath();
                    temp_text.setText(FilePath);

                    // Выведем пробный файл
                    String view_file = readFile_from_view(FilePath, 10);

                    view_text_file.setText(view_file);

                    // Выбор из элементов значений
                    String[] columnsCsvFile = getColumnsCsvFile(FilePath, ",");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, columnsCsvFile);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_select_value.setAdapter(adapter);


                    // Настройка даты
                    spinner_select_YYYY.setPrompt("Select years");
                    spinner_select_YYYY.setAdapter(adapter);

                    spinner_select_MM.setPrompt("Select months");
                    spinner_select_MM.setAdapter(adapter);

                    spinner_select_DD.setPrompt("Select days");
                    spinner_select_DD.setAdapter(adapter);

                    spinner_select_DD.setPrompt("Select hours:minutes:seconds");
                    spinner_select_hh_mm_ss.setAdapter(adapter);


                }
                else {
                    String message = getString(R.string.error_not_find_file_manager);
                    Toast toast = Toast.makeText(getActivity(), message , Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
    }


    void writeFileSD() {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные
            bw.write("Содержимое файла на SD");
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void importCSVtoDataBase(String path, String KEY_XXX) {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }

        // Открываем БД для взаимодействия
        GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        File sdFile = new File(path);

        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String strReadFileRow = "";

            int numberRow = 0;


            while ((strReadFileRow = br.readLine()) != null) {

                // Очистим строчку от пробелом
                strReadFileRow = strReadFileRow.replace(" ", "");
                String strDelimetr = ",";

                String[] arrayReadFileRow = strReadFileRow.split(strDelimetr);


                // В первой строке обычно текстовая информация, поэтому пропустим
                if(numberRow != 0){
                    Log.d(LOG_TAG, String.valueOf(numberRow) );

                    String YYYY = arrayReadFileRow[position_YYYY];
                    String MM = arrayReadFileRow[position_MM];
                    String DD = arrayReadFileRow[position_DD];

                    String str_HH_MM_SS_raw = arrayReadFileRow[position_hh_mm_ss];
                    String[] arrayReadFileRow_HH_MM_SS = str_HH_MM_SS_raw.split(":");

                    String hh = "";
                    String mm = "";
                    String ss = "";

                    if(arrayReadFileRow_HH_MM_SS.length == 3) {
                        hh = arrayReadFileRow_HH_MM_SS[0];
                        mm = arrayReadFileRow_HH_MM_SS[1];
                        ss = arrayReadFileRow_HH_MM_SS[2];
                    }
                    else{
                        hh = String.valueOf(arrayReadFileRow_HH_MM_SS.length);
                        mm = String.valueOf(arrayReadFileRow_HH_MM_SS.length);
                        ss = String.valueOf(arrayReadFileRow_HH_MM_SS.length);
                    }

                    Calendar RowDate = Calendar.getInstance();
                    RowDate = CalendarComplement.convertStringToCalendar(YYYY, MM, DD, hh, mm, ss);

                    // Получили значения
                    String RowValues = arrayReadFileRow[position_values];

                    // Отладочный вывод
                    String answer = "";
                    for(int i = 0 ; i < arrayReadFileRow.length ; i++)
                        answer += arrayReadFileRow[i] + " ";
                    answer = answer + " === " + CalendarComplement.toStringYYYYMMDDHHMM(RowDate);


                    // Теперь внесем изменения в БД
                    // Реализуем пока только один вариант, если запись есть + 1



                    // 1 значение - +1 к соответствующей дате
                    if(position_method_write_values == 0){
                        // Очистим от минут и прочего мусора
                        Calendar writeCalendar = CalendarComplement.clearTrash(RowDate);

                        int writeValue = 1;

                        boolean isWriteInTableSacsesful =  dbHelper.updateDataAndSum(db, writeCalendar, KEY_XXX, writeValue);

                        Log.d(LOG_TAG, String.valueOf(isWriteInTableSacsesful));
                        Log.d(LOG_TAG, writeCalendar + " "  +writeValue);

                    }
                    // SUM
                    if(position_method_write_values == 1){
                        // Очистим от минут и прочего мусора
                        Calendar writeCalendar = CalendarComplement.clearTrash(RowDate);

                        int writeValue = 1;

                        boolean isWriteInTableSacsesful =  dbHelper.updateDataAndSum(db, writeCalendar, KEY_XXX, writeValue);

                        Log.d(LOG_TAG, String.valueOf(isWriteInTableSacsesful));
                        Log.d(LOG_TAG, writeCalendar + " "  +writeValue);

                    }
                    // MAX
                    if(position_method_write_values == 2){
                        // Очистим от минут и прочего мусора
                        Calendar writeCalendar = CalendarComplement.clearTrash(RowDate);

                        int writeValue = 1;

                        boolean isWriteInTableSacsesful =  dbHelper.updateDataAndSum(db, writeCalendar, KEY_XXX, writeValue);

                        Log.d(LOG_TAG, String.valueOf(isWriteInTableSacsesful));
                        Log.d(LOG_TAG, writeCalendar + " "  +writeValue);

                    }


                }
                else {
                    // При анализе первых строк
                }

                numberRow++;
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String readFile_from_view(String path, int max_row) {
        String answer = "";

        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return answer;
        }


        File sdFile = new File(path);

        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String strReadFileRow = "";

            int numberRow = 0;

            while ( (strReadFileRow = br.readLine()) != null) {
                answer += strReadFileRow + "\n";

                if(numberRow == max_row){
                    br.close();
                    answer += "...";
                    return answer;
                }

                numberRow++;
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return answer;
    }

    String[] getColumnsCsvFile(String path, String strDelimetr) {
        String[] answer = new String[0];

        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return answer;
        }


        File sdFile = new File(path);

        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String strReadFileRow = "";

            while ( (strReadFileRow = br.readLine()) != null) {

                // Очистим строчку от пробелом
                strReadFileRow = strReadFileRow.replace(" ", "");

                // Разбили первую строчку на столбцы
                String[] arrayReadFileRow = strReadFileRow.split(strDelimetr);

                br.close();
                return  arrayReadFileRow;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return answer;
    }

}


