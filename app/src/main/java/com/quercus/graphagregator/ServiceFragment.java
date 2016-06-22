package com.quercus.graphagregator;

import android.os.Bundle;
import android.app.ListFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class ServiceFragment extends ListFragment {


    static interface ServiceFragmentListener {
        void itemClicked(long id);
    };
    String allService[] = new String[] { "Google Fit", "ResqueTime"};

    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_sync:
                Toast.makeText(getActivity(), "Синхронизация выполниться сейчас", Toast.LENGTH_SHORT).show();

                // Do Activity menu item stuff here
                return true;

            case R.id.action_alarm:
                Toast.makeText(getActivity(), "Переход к установке оповещений", Toast.LENGTH_SHORT).show();

                // Not implemented here
                return true;

            default:
                break;
        }
        return false;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, allService);
        setListAdapter(adapter);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if(position == 0){
            coonectGoogleFit();
        }
        else if(position == 1){
            coonectResqueTime();
        }

    }

    void coonectGoogleFit(){
        Toast.makeText(getActivity(), "Подключение 1", Toast.LENGTH_SHORT).show();
    }
    void coonectResqueTime(){
        Toast.makeText(getActivity(), "Подключение 2", Toast.LENGTH_SHORT).show();
    }


}
