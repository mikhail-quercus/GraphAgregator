package com.quercus.graphagregator;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;

// Для выдвижной пенели
import android.widget.ArrayAdapter;
import android.widget.ListView;

// Для слушателя выдвижной панели
import android.view.View;
import android.widget.AdapterView;

// Для обработки щелчков выдвижной пенели
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

// Управление состоянием выдвижной панели
import android.support.v7.app.ActionBarDrawerToggle;

// Для onConfigurationChanged()
import android.content.res.Configuration;

// Прочее
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class MainActivity extends Activity implements TopFragment.OnSelectedButtonGraphListener{

    // Слушатель кнопок в классе TopFragment
    @Override
    public void onButtonGraphSelected(int buttonIndex) {
        int position = buttonIndex + 10;
        selectItem(position);
    }

    // Слушатель для выдвижной панели, ожидание щелчка на списке
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }


    private ShareActionProvider shareActionProvider;
    private String[] titles;    // список активностей в боковой панели
    private ListView drawerList;    // боковая панель
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int currentPosition = 0;    // Используем для корректного отображенеи информации в фрагментах и панели действий


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Заполнение массива для выдвижной пенели
        // simple_list_item_activated_1 - подсветка выбранного варианта
        titles = getResources().getStringArray(R.array.titles_drawer);
        drawerList = (ListView)findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                                                       android.R.layout.simple_list_item_activated_1, titles));

        // Слушать щелчка на списке в боковой панели
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Востановление фрагмента
        if (savedInstanceState != null) {
            // Если не первое использование фрагмента, отобразим корректную информацию где сейчас пользователь
            currentPosition = savedInstanceState.getInt("position");
            setActionBarTitle(currentPosition);
            //setActionBartTitile("!!!");
        } else {
            // При базовом создание MainActivity отобразить первый фрагмент
            selectItem(0);
        }

        // Создание ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                                                 R.string.open_drawer, R.string.close_drawer) {
                // Вызывается при переходе выдвижной панели в полностью закрытое состояние
                @Override
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);

                    // Приказ Android заново создать команды меню
                    invalidateOptionsMenu();
                }
                // Вызывается при переходе выдвижной панели в полностью открытое состояние.
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);


                    invalidateOptionsMenu();
                }
            };

        // Назначить слушателя для DrawerLayout
        drawerLayout.setDrawerListener(drawerToggle);

        // Включить кнопку вверх
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Для корректного дейсвия для кнопки назад
        getFragmentManager().addOnBackStackChangedListener(
                                                           new FragmentManager.OnBackStackChangedListener() {
                                                               public void onBackStackChanged() {
                                                                   // Найдем фрагмент который сейчас связан с MainActivity, его мы пометили тэгом visible_fragment
                                                                   FragmentManager fragMan = getFragmentManager();
                                                                   Fragment fragment = fragMan.findFragmentByTag("visible_fragment");

                                                                   // Проверка к какому типу относиться фрагмент и присвоим верное значение currentPosition
                                                                   if (fragment instanceof TopFragment) {
                                                                       currentPosition = 0;
                                                                   }
                                                                   if (fragment instanceof ServiceFragment) {
                                                                       currentPosition = 1;
                                                                   }
                                                                   if (fragment instanceof SettingsFragment) {
                                                                       currentPosition = 2;
                                                                   }
                                                                   if (fragment instanceof ReferenceFragment) {
                                                                       currentPosition = 3;
                                                                   }




                                                                   // Если позиция больше 10 то это кнока графика - установим заголовок
                                                                   if(currentPosition > 10){
                                                                       setActionBartTitile("График");
                                                                   }
                                                                   else
                                                                   {
                                                                       // Вывести текст на панели действий и выделить правильный вариант в списке на выдвижной панели
                                                                       setActionBarTitle(currentPosition);
                                                                   }


                                                                   drawerList.setItemChecked(currentPosition, true);
                                                               }
                                                           }
                                                           );
    }

    // Обработка щелчков в выжвижной панели + кнопки в TopActivity
    private void selectItem(int position) {

        // Обновить информацию заменой фрагмента
        currentPosition = position;
        Fragment fragment;

        switch(position) {
        case 1:
            fragment = new ServiceFragment();
            break;
        case 2:
            fragment = new SettingsFragment();
            break;
        case 3:
            fragment = new ReferenceFragment();
            break;
        case 4:
            fragment = new SQL_work();
            break;
        case 10:
            Toast.makeText(this, "Создание нового графика", Toast.LENGTH_SHORT).show();
            fragment = new TopFragment();
            break;
        case 10+1:
            // TODO: Текущая работа
            fragment = new PieChartFragment();
            break;
        case 10+2:
            Toast.makeText(this, "Не реализовано", Toast.LENGTH_SHORT).show();
            fragment = new TopFragment();
            break;
        case 10+3:
            Toast.makeText(this, "Не реализовано", Toast.LENGTH_SHORT).show();
            fragment = new TopFragment();
            break;
        default:
            fragment = new TopFragment();
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        // !!! Передаем тэг visible_fragment - он используется для того что бы знать какой фрагмент сейчас выводиться
        ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        /*
        // Назначение заголовка панели действий
        setActionBarTitle(position);
        if( getActionBar().getTitle() == "null" )
            setActionBartTitile("Обновленный");
        */

        // Задвинуть боковую панель для удобства пользователя
        drawerLayout.closeDrawer(drawerList);
    }

    // Вызывается при каждом вызове invalidateOptionsMenu()
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Если выдвижная панель открыта - спрятать кнопки на панели действий
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_mode).setVisible(!drawerOpen);
        menu.findItem(R.id.action_done).setVisible(!drawerOpen);
        menu.findItem(R.id.action_delete).setVisible(!drawerOpen);
        menu.findItem(R.id.action_sync).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Синхронизация состояние после onRestoreInstanceState
        drawerToggle.syncState();
    }

    @Override
    // При изменение конфигурации - передать новые данные для ActionBarDrawerToggle
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    // Исправление проблемы при переворачивание экрана - сохранение состояния
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", currentPosition);
    }

    // Корректный вывод надписи в меню действий
    private void setActionBarTitle(int position) {
        String title;
        // Если пользователь выбирает первый вариант - Home, используем название приложения
        if (position == 0) {
            title = getResources().getString(R.string.app_name);
        } else {

            // Иначе получим из массива элемент соответсующий пощиции выбранного элемента
            try {
                title = titles[position];
            }
            catch (Exception ex)
            {
                // TODO: Проблема с выходом за область массива - решить
                // TODO: Получать корректное имя
                title = "null";
            }
        }
        // Вывести заголовок на панели действий
        getActionBar().setTitle(title);
    }

    // Вывод надписи текстом
    public void setActionBartTitile(String str){
        getActionBar().setTitle(str);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Прочиать файл menu_main и создать меню действия
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) menuItem.getActionProvider();
        setIntent("This is example text");
        return super.onCreateOptionsMenu(menu);
    }

    // Назначит действию Share интент для передачи информации
    private void setIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }

    @Override
    // Этот метод вызывается когда пользователь шелкает на элементе на панели действий
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
        /*
            TODO: Полезный фрагмент
            case R.id.action_create_order:
            //Code to run when the Create Order item is clicked
            Intent intent = new Intent(this, OrderActivity.class);
            startActivity(intent);
            return true;
            */
        case R.id.action_settings:
            //Code to run when the settings item is clicked
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
