package cl.fullpay.causas;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import cl.fullpay.causas.data.FullpayContract;
import cl.fullpay.causas.syncAdapter.Helper;


public class Init extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_init);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        String courtName;
        SharedPreferences prefs = getPreferences(0);
        courtName =prefs.getString(CauseListFragment.COURT_NAME_BUNDLE,"-1");
        if(courtName.equals("-1")){
            //Primer uso de la app
            Helper helper = new Helper(getApplicationContext());
            Cursor cursor = helper.getCourtsForAttorney();
            cursor.moveToFirst();
            courtName= cursor.getString(2);
            cursor.close();
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CauseListFragment.COURT_NAME_BUNDLE,courtName);
        editor.commit();
        mTitle = "Tribunal: "+courtName;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        handleIntent(getIntent());




    }

    private void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this,"buscando: "+query,Toast.LENGTH_LONG).show();
            Bundle bundle = new Bundle();
            bundle.putString(CauseListFragment.QUERY_ROL,query);
            Fragment causeList = new CauseListFragment();
            causeList.setArguments(bundle);
            SharedPreferences settings = getPreferences(0);
            String court = settings.getString(CauseListFragment.COURT_NAME_BUNDLE,"");
            mTitle = "Resultado de busqueda para: "+query+" en tribunal: "+court;

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container,causeList)
                    .commit();


        }
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {

        String courtName = null;

        if(mNavigationDrawerFragment != null){
            ListView lw = mNavigationDrawerFragment.mDrawerListView;
            SQLiteCursor cursor = (SQLiteCursor)lw.getItemAtPosition(position);
            courtName = cursor.getString(2);
        }


        Fragment causeList = new CauseListFragment();

        /* Si no se ha seleccionado corte (primer uso o apertura de aplicacion luego de cerrarla)
         recuperar la ultima corte que se selecciono (guardada en preferences), si no hay, escoger
         primera corte del listado (primer uso de la aplicacion)
        * */

        SharedPreferences prefs = getPreferences(0);
        if(courtName == null){
            courtName =prefs.getString(CauseListFragment.COURT_NAME_BUNDLE,"-1");
        }

        mTitle = "Tribunal: "+courtName;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CauseListFragment.COURT_NAME_BUNDLE,courtName);
        editor.commit();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, causeList)
                .commit();


    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.init, menu);
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
