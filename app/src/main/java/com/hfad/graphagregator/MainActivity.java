package com.hfad.graphagregator;

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

public class MainActivity extends Activity {
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    };

    private ShareActionProvider shareActionProvider;
    private String[] titles;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int currentPosition = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Заполнение массива для выдвижной пенели
        // simple_list_item_activated_1 - подсветка выбранного варианта
        titles = getResources().getStringArray(R.array.titles);
        drawerList = (ListView)findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                                                       android.R.layout.simple_list_item_activated_1, titles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        //TODO: Что это?
        //Display the correct fragment.
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("position");
            setActionBarTitle(currentPosition);
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
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getFragmentManager().addOnBackStackChangedListener(
                                                           new FragmentManager.OnBackStackChangedListener() {
                                                               public void onBackStackChanged() {
                                                                   FragmentManager fragMan = getFragmentManager();
                                                                   Fragment fragment = fragMan.findFragmentByTag("visible_fragment");
                                                                   if (fragment instanceof TopFragment) {
                                                                       currentPosition = 0;
                                                                   }
                                                                   if (fragment instanceof PizzaFragment) {
                                                                       currentPosition = 1;
                                                                   }
                                                                   if (fragment instanceof PastaFragment) {
                                                                       currentPosition = 2;
                                                                   }
                                                                   if (fragment instanceof StoresFragment) {
                                                                       currentPosition = 3;
                                                                   }
                                                                   setActionBarTitle(currentPosition);
                                                                   drawerList.setItemChecked(currentPosition, true);
                                                               }
                                                           }
                                                           );
    }

    // Обработка щелчков в выжвижной панели
    private void selectItem(int position) {
        // update the main content by replacing fragments
        currentPosition = position;
        Fragment fragment;
        switch(position) {
        case 1:
            fragment = new PizzaFragment();
            break;
        case 2:
            fragment = new PastaFragment();
            break;
        case 3:
            fragment = new StoresFragment();
            break;
        default:
            fragment = new TopFragment();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        //Set the action bar title
        setActionBarTitle(position);
        //Close drawer
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Если выдвижная панель открыта - спрятать кнопки на панели действий
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_share).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Синхронизация состояние после onRestoreInstanceState
        drawerToggle.syncState();
    }

    @Override
    // При изменение конфигурации - передать новые данные
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    // Исправление проблемы при переворачивание экрана
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", currentPosition);
    }

    // Корректный вывод надписи в меню действий
    private void setActionBarTitle(int position) {
        String title;
        if (position == 0) {
            title = getResources().getString(R.string.app_name);
        } else {
            title = titles[position];
        }
        getActionBar().setTitle(title);
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

    // TODO: Похоже на код для кнопки SHARE
    private void setIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }

    @Override
    // TODO: ХЗ что это
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
        case R.id.action_create_order:
            //Code to run when the Create Order item is clicked
            Intent intent = new Intent(this, OrderActivity.class);
            startActivity(intent);
            return true;
        case R.id.action_settings:
            //Code to run when the settings item is clicked
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
