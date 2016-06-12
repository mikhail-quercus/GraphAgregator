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
import android.widget.Button;
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
    String FILENAME = "test_file.csv";
    String DIR_SD = "QUERCUS";
    String FILENAME_SD = "test_file.csv";
    String KEY_ADD_XXX = "TomatoTimer";

    final String LOG_TAG = "myLogs";

    private static final int PICKFILE_RESULT_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_import_csv, container, false);

        button_add_file = (Button)rootView.findViewById(R.id.import_csv_add_file);
        temp_text = (TextView)rootView.findViewById(R.id.test_name_file);

        button_add_file.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onClick(View v) {

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
                    String FilePath = data.getData().getPath();
                    temp_text.setText(FilePath);

                    readFileSD(FilePath);
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

    void readFileSD(String path) {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }

        // Открываем БД для взаимодействия
        GraphDatabaseHelper dbHelper = new GraphDatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();




        /*
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, FILENAME_SD);
        */

        File sdFile = new File(path);
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String strReadFileRow = "";
            // читаем содержимое


            int numberRow = 0;

            // Создадим новый столбец
            boolean isOk =  dbHelper.addNewColumnHowInteger(db, KEY_ADD_XXX);

            while ((strReadFileRow = br.readLine()) != null) {
                //Log.d(LOG_TAG, strReadFileRow);

                // Начиная со второй строчки:
                // Найти время начальное, последнее
                // Разбить строку - на массив чисел

                // Очистим строчку от пробелом
                strReadFileRow = strReadFileRow.replace(" ", "");
                String strDelimetr = ",";

                String[] arrayReadFileRow = strReadFileRow.split(strDelimetr);

                // Указывает пользователь
                int DateInColumn = 0;
                int ValueInColumn = 0;



                // В первой строке обычно текстовая информация, поэтому пропустим
                if(numberRow != 0){
                    Log.d(LOG_TAG, String.valueOf(numberRow) );
                    //Long RowDate = Long.parseLong(arrayReadFileRow[ValueInColumn]);
                    //String RowDate = arrayReadFileRow[5];

                    // Полученна дата  в значение UTC

                    /*
                    // Получение времени UTC
                    String RowDate_raw  = arrayReadFileRow[5];
                    Calendar RowDate = Calendar.getInstance();
                    RowDate.setTimeInMillis(Long.parseLong(RowDate_raw));
                    */

                    String YYYY = arrayReadFileRow[0];
                    String MM = arrayReadFileRow[1];
                    String DD = arrayReadFileRow[2];

                    String str_HH_MM_SS_raw = arrayReadFileRow[3];
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
                    String RowValues = arrayReadFileRow[4];

                    // Отладочный вывод
                    //String answer = RowDate + RowDate_raw + " " + RowValues;
                    String answer = "";
                    for(int i = 0 ; i < arrayReadFileRow.length ; i++)
                        answer += arrayReadFileRow[i] + " ";
                    answer = answer + " === " + CalendarComplement.toStringYYYYMMDDHHMM(RowDate);


                    // Теперь внесем изменения в БД
                    // Реализуем пока только один вариант, если запись есть + 1



                    if(isOk){
                        // Очистим от минут и прочего мусора
                        Calendar writeCalendar = CalendarComplement.clearTrash(RowDate);
                        // TODO: Тут обработка возможной ошибки

                        int writeValue = 666;

                        int isWriteInTableSacsesful =  dbHelper.insertData(db, writeCalendar, KEY_ADD_XXX, writeValue );


                        Log.d(LOG_TAG, String.valueOf(isWriteInTableSacsesful));
                        Log.d(LOG_TAG, writeCalendar + " "  +writeValue);

                    }
                    else
                    {
                        Log.d(LOG_TAG, "Не получилось создать новый столбец");
                    }




                }
                else {
                    Log.d(LOG_TAG, "Инициализация первых значений");
                    // Сработает в первой строчке
                    // Должен указать пользователь
                    DateInColumn = 5;
                    ValueInColumn = 4;
                }

                numberRow++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


