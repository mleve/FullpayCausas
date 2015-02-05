package cl.fullpay.causas;



import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import cl.fullpay.causas.data.FullpayDbHelper;
import cl.fullpay.causas.syncAdapter.SyncAdapter;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CauseListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String COURT_NAME_BUNDLE = "bundle_court_name";
    public static final String QUERY_ROL = "bundle_query_rol";
    public static final String QUERY_COURT_NAME = "query_court";

    private final String LOG_TAG = CauseListFragment.class.getSimpleName();
    private CauseCursorAdapter causesAdapter;
    private static final int CAUSES_LOADER = 0;
    private String courtName;
    private String query;
    private boolean isSearch= false;
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


        if(getArguments() != null){
            if(getArguments().containsKey(QUERY_ROL)){
                isSearch = true;
                query = getArguments().getString(QUERY_ROL);
            }
            else if (getArguments().containsKey(COURT_NAME_BUNDLE)){
                courtName = getArguments().getString(COURT_NAME_BUNDLE);
                SharedPreferences settings = getActivity().getPreferences(0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(QUERY_COURT_NAME,courtName);
                editor.commit();
            }
        }
        else{

            getCourtName();
            /*
            SharedPreferences settings = getActivity().getPreferences(0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(QUERY_COURT_NAME,courtName);
            editor.commit();
            */
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



    private void getCourtName(){
        FullpayDbHelper dbHelper = new FullpayDbHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = String.format(
                "SELECT %s.%s, %s, %s " +
                        "FROM %s, %s " +
                        "WHERE %s.%s = %s.%s " +
                        "GROUP BY %s",
                CauseEntry.TABLE_NAME,
                CauseEntry._ID,
                CauseEntry.COLUMN_COURT_KEY,
                FullpayContract.CourtEntry.COLUMN_NAME,
                CauseEntry.TABLE_NAME,
                FullpayContract.CourtEntry.TABLE_NAME,
                CauseEntry.TABLE_NAME,
                CauseEntry.COLUMN_COURT_KEY,
                FullpayContract.CourtEntry.TABLE_NAME,
                FullpayContract.CourtEntry._ID,
                CauseEntry.COLUMN_COURT_KEY
        );

        Cursor courtCursor = db.rawQuery(
                query,
                null
        );

        courtCursor.moveToFirst();
        courtName= courtCursor.getString(2);
        courtCursor.close();
        db.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri causesUri = FullpayContract.CauseEntry.CONTENT_URI;
        SharedPreferences settings = getActivity().getPreferences(0);
        String court = settings.getString(QUERY_COURT_NAME,"");

        Cursor courtCursor = getActivity().getContentResolver().query(
                FullpayContract.CourtEntry.CONTENT_URI,
                null,
                FullpayContract.CourtEntry.COLUMN_NAME+"= ? ",
                new String[]{court},
                null
        );
        courtCursor.moveToFirst();
        String courtId = courtCursor.getString(
                courtCursor.getColumnIndex(FullpayContract.CourtEntry._ID)
        );
        Log.d(LOG_TAG,"court name : "+court+", court id: "+courtId);
        courtCursor.close();
        if (isSearch){
            return new CursorLoader(
                    getActivity(),
                    causesUri,
                    null,
                    CauseEntry.COLUMN_COURT_KEY+"=? AND "+
                    CauseEntry.COLUMN_ROL_NUM+" LIKE '"+query+"%'",
                    new String[]{courtId},
                    CauseEntry.COLUMN_ROL_DATE + " ASC, " + CauseEntry.COLUMN_ROL_NUM + " ASC"
            );
        }
        else{
            return  new CursorLoader(
                    getActivity(),
                    causesUri,
                    null,
                    CauseEntry.COLUMN_COURT_KEY+"= ?",
                    new String[]{courtId},
                    CauseEntry.COLUMN_ROL_DATE + " ASC, " + CauseEntry.COLUMN_ROL_NUM + " ASC"
            );
        }

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
