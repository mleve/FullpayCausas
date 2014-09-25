package cl.fullpay.causas;



import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CauseListFragment extends Fragment {


    CauseAdapter adapter;
    public CauseListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cause_list, container, false);


        ArrayList<Cause> causeList = new ArrayList<Cause>();

        Cause cause = new Cause("si","no","siii","no","sii","no","si");
        Cause cause2 = new Cause("si","no","siii","no","sii","no","si");
        Cause cause3= new Cause("si","no","siii","no","sii","no","si");

        causeList.add(cause);
        causeList.add(cause2);
        causeList.add(cause3);

        adapter = new CauseAdapter(getActivity(),
                R.layout.cause_list_item,
                causeList);

        ListView si = (ListView) rootView.findViewById(R.id.listview_cause);
        si.setAdapter(adapter);

        /*
        si.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String forecast = adapter.getItem(i);

                Intent DetailIntent = new Intent(getActivity(),DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT,forecast);
                startActivity(DetailIntent);
            }
        });
        */
        return rootView;


    }
}
