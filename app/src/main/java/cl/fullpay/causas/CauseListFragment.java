package cl.fullpay.causas;



import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
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
import cl.fullpay.causas.data.FullpayContract;
import cl.fullpay.causas.data.FullpayContract.CauseEntry;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CauseListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = CauseListFragment.class.getSimpleName();
    private SimpleCursorAdapter causesAdapter;
    private static final int CAUSES_LOADER = 0;
    CauseAdapter adapter;
    private HttpGetTask getCauseTask;

    public CauseListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CAUSES_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cause_list_2, container, false);


        causesAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.cause_list_item,
                null,
                //Nombre de las columnas a las que se mapean los campos (En orden)
                new String[]{
                        CauseEntry.COLUMN_ROL_NUM,
                        CauseEntry.COLUMN_RUT,
                        CauseEntry.COLUMN_NAMES,
                        CauseEntry.COLUMN_STAGE_KEY,
                        CauseEntry.COLUMN_COMMENT,
                        CauseEntry.COLUMN_WARRANT,
                        CauseEntry.COLUMN_CHANGE_DATE
                },
                new int[]{
                        R.id.cause_rol,
                        R.id.cause_rut,
                        R.id.cause_name,
                        R.id.cause_stage,
                        R.id.cause_comment,
                        R.id.cause_exhorto,
                        R.id.cause_last_change
                },
                0
        );

        ListView causeList = (ListView) rootView.findViewById(R.id.listview_cause);
        causeList.setAdapter(causesAdapter);


        return rootView;


    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //TODO recibir como parametro la id de tribunal y cargar solo esas causas

        //TODO entregar las causas ordenadas por rol

        Uri causesUri = FullpayContract.CauseEntry.CONTENT_URI;
        return new CursorLoader(
                getActivity(),
                causesUri,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> objectLoader, Cursor data) {
        causesAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> objectLoader) {
        causesAdapter.swapCursor(null);
    }
}
