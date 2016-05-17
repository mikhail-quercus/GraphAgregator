package com.hfad.graphagregator;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
        Button button_graph3 = (Button)rootView.findViewById(R.id.graph3);
        Button button_graph_new = (Button)rootView.findViewById(R.id.graph_new);

        button_graph1.setOnClickListener(this);
        button_graph2.setOnClickListener(this);
        button_graph3.setOnClickListener(this);
        button_graph_new.setOnClickListener(this);



        return rootView;
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
            case R.id.graph3:
                index = 3;
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
