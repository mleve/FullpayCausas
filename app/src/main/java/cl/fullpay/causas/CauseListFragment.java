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
import cl.fullpay.causas.adapters.CauseCursorAdapter;
import cl.fullpay.causas.data.FullpayContract;
import cl.fullpay.causas.data.FullpayContract.CauseEntry;
import cl.fullpay.causas.syncAdapter.SyncAdapter;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CauseListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String COURT_NAME_BUNDLE = "bundle_court_name";

    private final String LOG_TAG = CauseListFragment.class.getSimpleName();
    private CauseCursorAdapter causesAdapter;
    private static final int CAUSES_LOADER = 0;
    private String courtName;
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

        //Por defecto quiero que entre a la corte de SANTIAGO
        courtName = "SANTIAGO";
        if(getArguments() != null){
            courtName = getArguments().getString(COURT_NAME_BUNDLE);

        }

        //Cursor mCursor = buildCausesCursor(courtName);

        causesAdapter = new CauseCursorAdapter(
                getActivity(),
                null, //mCursor,
                0
        );
        ListView causeList = (ListView) rootView.findViewById(R.id.listview_cause);
        causeList.setAdapter(causesAdapter);


        return rootView;


    }



    private Cursor buildCausesCursor(String courtName) {

        if(courtName == null) {
            return  getActivity().getContentResolver().query(
                    CauseEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    CauseEntry.COLUMN_ROL_DATE + " ASC, " + CauseEntry.COLUMN_ROL_NUM + " ASC"
            );
        }else{
            Cursor courtCursor = getActivity().getContentResolver().query(
                    FullpayContract.CourtEntry.CONTENT_URI,
                    null,
                    FullpayContract.CourtEntry.COLUMN_NAME+"= ? ",
                    new String[]{courtName},
                    null
            );

            courtCursor.moveToFirst();
            String courtId = courtCursor.getString(
                    courtCursor.getColumnIndex(FullpayContract.CourtEntry._ID)
            );
            return  getActivity().getContentResolver().query(
                    CauseEntry.CONTENT_URI,
                    null,
                    CauseEntry.COLUMN_COURT_KEY+"= ?",
                    new String[]{courtId},
                    CauseEntry.COLUMN_ROL_DATE + " ASC, " + CauseEntry.COLUMN_ROL_NUM + " ASC"
            );

        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri causesUri = FullpayContract.CauseEntry.CONTENT_URI;
        Cursor courtCursor = getActivity().getContentResolver().query(
                FullpayContract.CourtEntry.CONTENT_URI,
                null,
                FullpayContract.CourtEntry.COLUMN_NAME+"= ? ",
                new String[]{courtName},
                null
        );

        courtCursor.moveToFirst();
        String courtId = courtCursor.getString(
                courtCursor.getColumnIndex(FullpayContract.CourtEntry._ID)
        );
        courtCursor.close();

        return  new CursorLoader(
                getActivity(),
                causesUri,
                null,
                CauseEntry.COLUMN_COURT_KEY+"= ?",
                new String[]{courtId},
                CauseEntry.COLUMN_ROL_DATE + " ASC, " + CauseEntry.COLUMN_ROL_NUM + " ASC"
        );


        /*
        if(courtName == null) {
            return new CursorLoader(
                    getActivity(),
                    causesUri,
                    null,
                    null,
                    null,
                    CauseEntry.COLUMN_ROL_DATE + " ASC, " + CauseEntry.COLUMN_ROL_NUM + " ASC"
            );
        }else{
            Cursor courtCursor = getActivity().getContentResolver().query(
                    FullpayContract.CourtEntry.CONTENT_URI,
                    null,
                    FullpayContract.CourtEntry.COLUMN_NAME+"= ? ",
                    new String[]{courtName},
                    null
            );

            courtCursor.moveToFirst();
            String courtId = courtCursor.getString(
                    courtCursor.getColumnIndex(FullpayContract.CourtEntry._ID)
            );
            courtCursor.close();

            return  new CursorLoader(
                    getActivity(),
                    causesUri,
                    null,
                    CauseEntry.COLUMN_COURT_KEY+"= ?",
                    new String[]{courtId},
                    CauseEntry.COLUMN_ROL_DATE + " ASC, " + CauseEntry.COLUMN_ROL_NUM + " ASC"
            );

        }
        */

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
