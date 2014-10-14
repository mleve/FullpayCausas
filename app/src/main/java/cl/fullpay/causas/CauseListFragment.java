package cl.fullpay.causas;



import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import cl.fullpay.causas.HttpTasks.HttpGetTask;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CauseListFragment extends Fragment {

    private final String LOG_TAG = CauseListFragment.class.getSimpleName();
    CauseAdapter adapter;
    private HttpGetTask getCauseTask;

    public CauseListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cause_list_2, container, false);


        ArrayList<Cause> causeList = new ArrayList<Cause>();

        //Dummy content
        Cause cause = new Cause("012","2012","si","no","dummy","siii","no","sii","no");
        Cause cause2 = new Cause("123","si","no","dummy","siii","no","sii","no","si");
        Cause cause3= new Cause("424","si","no","dummy","siii","no","sii","no","si");

        causeList.add(cause);
        causeList.add(cause2);
        causeList.add(cause3);

        getCauseTask = new HttpGetTask(null,
                "http://dev.empchile.net/forseti/index.php/admin/api/getCausas",
                new HttpGetTask.OnPostExecuteListener() {
                    @Override
                    public void onPostExecute(String result) {
                        updateAdapter(result);
                    }
                });

        getCauseTask.execute((Void) null);


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

    private void updateAdapter(String result) {

        adapter.clear();

        JSONArray causeList = parseResponse(result);

        if(causeList != null){
            JSONObject aux = null;
            String rolAux =null;
            ArrayList<Cause> causes = new ArrayList<Cause>();
            for(int i=0; i<causeList.length();i++){
                try{
                    aux = causeList.getJSONObject(i);
                }catch (JSONException e){
                    Log.e(LOG_TAG,"error al intentar leer causa");
                    continue;
                }

                try{

                    rolAux = aux.getString("rol");

                    String[] rol =rolAux.split("-");
                    causes.add(new Cause(
                            rol[0],
                            rol[1],
                            aux.getString("rut"),
                            aux.getString("nombres"),
                            aux.getString("ap_pat"),
                            aux.getString("id_etapa"),
                            aux.getString("observaciones"),
                            aux.getString("exorto"),
                            aux.getString("fecha_etapa")));

                }catch (Exception e){
                    Log.e(LOG_TAG,"error al leer campo de causa, input:"+ rolAux);
                    e.printStackTrace();
                    continue;
                }

            }
            Collections.sort(causes);
            adapter.addAll(causes);
        }

    }

    private JSONArray parseResponse(String result) {
        JSONObject responseObj = null;
        JSONArray array = null;
        try {
            responseObj = new JSONObject(result);
            array = responseObj.getJSONArray("causas");

        }catch(JSONException e){
            Log.e(LOG_TAG, "cago el parseo del json :(");
            array = null;
        }

        return array;
    }


}
