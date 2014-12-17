package cl.fullpay.causas;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import cl.fullpay.causas.AsyncTasks.CausesTask;
import cl.fullpay.causas.HttpTasks.HttpGetTask;
import cl.fullpay.causas.data.FullpayContract;
import cl.fullpay.causas.data.FullpayProvider;
import cl.fullpay.causas.syncAdapter.SyncAdapter;

/**
 * Created by mario on 30-10-14.
 */
public class Main extends Activity {

    private String baseApiUrl  =  "http://dev.empchile.net/forseti/index.php/admin/api";
    private String token;


    Account mAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_init);

    }



    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Chequear que existe procurador o llevarlo a login
        Cursor attorneyCursor = getContentResolver().query(
                FullpayContract.AttorneyEntry.CONTENT_URI,
                null,
                FullpayContract.AttorneyEntry.COLUMN_IS_ACTIVE+" = ?",
                new String[]{"1"},
                null
        );

        if(!attorneyCursor.moveToFirst()){
            //no hay usuario activo
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        }
        else{
            token = attorneyCursor.getString(
                    attorneyCursor.getColumnIndex(FullpayContract.AttorneyEntry.COLUMN_TOKEN)
            );

            Cursor cursor = getContentResolver().query(
                    FullpayContract.CauseEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            if(cursor.moveToFirst() == true)
                startApp();
            else{

                CausesTask task = new CausesTask(getApplicationContext()){
                    @Override
                    protected Boolean doInBackground(Void... voids) {

                        String responseStr = httpGetRequest(baseUrl+"/getEtapas",null);

                        int responseCode = getResponseCode(responseStr);

                        if (responseCode != 0)
                            return false;

                        createStages(responseStr);

                        responseStr = httpGetRequest(baseUrl+"/getTribunales",null);

                        responseCode = getResponseCode(responseStr);

                        if (responseCode != 0)
                            return false;

                        createCourts(responseStr);

                        responseStr = httpGetRequest(baseUrl+"/getCuentaEtapasProcurador/"+token,null);


                        responseCode = getResponseCode(responseStr);

                        if (responseCode != 0)
                            return false;

                        return createCauses(responseStr);
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        startApp();
                    }
                };

                task.execute();

            }


        }

    }

    private void startApp() {
        startActivity(new Intent(getApplicationContext(),Init.class));
        SyncAdapter.initializeSyncAdapter(this);
        finish();
    }
}
