package cl.fullpay.causas;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mario on 25-09-14.
 */
public class CauseAdapter extends ArrayAdapter<Cause> {

    private Context context;

    public CauseAdapter(Context context, int textViewResourceId, ArrayList<Cause> items) {
        super(context, textViewResourceId, items);
        this.context = context;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.cause_list_item, null);
        }

        Cause item = getItem(position);
        if (item!= null) {
            //si
            TextView rolView = (TextView) view.findViewById(R.id.cause_rol);
            if (rolView != null) {
                rolView.setText(item.getRol());
            }
            TextView rutView = (TextView) view.findViewById(R.id.cause_rut);
            if (rutView != null) {
                rutView.setText(item.getRut());
            }
            TextView nameView = (TextView) view.findViewById(R.id.cause_name);
            if (nameView != null) {
                nameView.setText(item.getName());
            }
            TextView stageView = (TextView) view.findViewById(R.id.cause_stage);
            if (stageView != null) {
                stageView.setText(item.getStage());
            }
            TextView commentView = (TextView) view.findViewById(R.id.cause_comment);
            if (commentView != null) {
                commentView.setText(item.getComment());
            }
            TextView exhortoView = (TextView) view.findViewById(R.id.cause_exhorto);
            if (exhortoView != null) {
                exhortoView.setText(item.getExhorto());
            }
            TextView dateView = (TextView) view.findViewById(R.id.cause_last_change);
            if (dateView != null) {
                dateView.setText(item.getDate());
            }
        }

        return view;
    }
}