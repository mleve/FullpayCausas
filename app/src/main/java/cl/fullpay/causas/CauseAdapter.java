package cl.fullpay.causas;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mario on 25-09-14.
 */
public class CauseAdapter extends ArrayAdapter<Cause> {

    private Context context;
    private ArrayList<String> stages;

    public CauseAdapter(Context context, int textViewResourceId, ArrayList<Cause> items) {
        super(context, textViewResourceId, items);
        this.context = context;
        stages = new ArrayList<String>();
        stages.add("Carga en sistema");
        stages.add("Elaboración de demanda");
        stages.add("Demanda sin información");
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.cause_list_item, null);
            Spinner causeSpinner = (Spinner) view.findViewById(R.id.cause_stage);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_spinner_item,
                    stages); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            causeSpinner.setAdapter(spinnerArrayAdapter);
        }

        Cause item = getItem(position);
        if (item!= null) {
            //si
            TextView rolView = (TextView) view.findViewById(R.id.cause_rol);
            if (rolView != null) {
                rolView.setText(item.getRolNum()+"-"+item.getRolYear());
            }
            TextView rutView = (TextView) view.findViewById(R.id.cause_rut);
            if (rutView != null) {
                rutView.setText(item.getRut());
            }
            TextView nameView = (TextView) view.findViewById(R.id.cause_name);
            if (nameView != null) {
                nameView.setText( item.getLast_name() + ", "+ item.getName());
            }

            /*
            TextView stageView = (TextView) view.findViewById(R.id.cause_stage);
            if (stageView != null) {
                stageView.setText(item.getStage());
            }
            *//*
            TextView commentView = (TextView) view.findViewById(R.id.cause_comment);
            if (commentView != null) {
                commentView.setText(item.getComment());
            }
            */
            EditText commentView = (EditText) view.findViewById(R.id.cause_comment);
            if (commentView != null){
                commentView.setText(item.getComment());
            }
            TextView exhortoView = (TextView) view.findViewById(R.id.cause_exhorto);
            if (exhortoView != null) {
                if(item.getExhorto().equals("1"))
                    exhortoView.setText("C");
                else
                    exhortoView.setText("S");
            }
            TextView dateView = (TextView) view.findViewById(R.id.cause_last_change);
            if (dateView != null) {
                dateView.setText(item.getDate());
            }
        }

        return view;
    }
}