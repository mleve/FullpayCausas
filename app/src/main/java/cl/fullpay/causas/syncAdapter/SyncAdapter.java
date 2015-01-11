package cl.fullpay.causas.syncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cl.fullpay.causas.AsyncTasks.CausesTask;
import cl.fullpay.causas.R;
import cl.fullpay.causas.data.FullpayContract.*;
import cl.fullpay.causas.parsers.Cause;

/**
 * Created by mario on 16-12-14.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String LOG_TAG = SyncAdapter.class.getSimpleName();

    // Interval to sync, in seconds.
    // 60 seconds * 60 * 4 = 4 hours
    public static final int SYNC_INTERVAL = 60 * 60 * 4;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        //TODO realizar el envio de información al servidor antes de pedir sus datos

        Log.d(LOG_TAG,"hola, deberia estar sincronizando");
        String baseUrl = getContext().getString(R.string.api_base_url);

        Cursor attorneyCursor = getContext().getContentResolver().query(
                AttorneyEntry.CONTENT_URI,
                null,
                AttorneyEntry.COLUMN_IS_ACTIVE+" = ?",
                new String[]{"1"},
                null
        );

        final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

        String username;
        String password;
        String api_token = getContext().getString(R.string.api_token);
        if(attorneyCursor.moveToFirst()){
            username = attorneyCursor.getString(
                    attorneyCursor.getColumnIndex(AttorneyEntry.COLUMN_USERNAME)
            );
            password = attorneyCursor.getString(
                    attorneyCursor.getColumnIndex(AttorneyEntry.COLUMN_PASSWORD)
            );
        }
        else{
            Log.d(LOG_TAG,"fallo al obtener el token desde la bd");
            return;
        }
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        nameValuePairs.add(new BasicNameValuePair("token", api_token));



        Helper helper = new Helper(getContext());

        boolean login_result = helper.logInUser(baseUrl,nameValuePairs);

        if(!login_result) {
            Log.d(LOG_TAG,"fallo al tratar de logear al usuario");
            return;
        }

        String token = helper.getUserToken(getContext());

        helper.sendUpdatesToServer(getContext(),token);

        String responseStr = helper.httpGetRequest(baseUrl + "/getEtapas", null);

        int responseCode = helper.getResponseCode(responseStr);

        if (responseCode != 0){
            Log.d(LOG_TAG,"la carga de etapas fallo");
            return;
        }

        helper.createStages(responseStr);

        responseStr = helper.httpGetRequest(baseUrl + "/getTribunales", null);

        responseCode = helper.getResponseCode(responseStr);

        if (responseCode != 0) {
            Log.d(LOG_TAG,"la carga de stages fallo");
            return;
        }



        if(token == null){
            Log.d(LOG_TAG,"error interno al tratar de obtener el session_token de la db");
        }

        helper.createCourts(responseStr);

        responseStr = helper.httpGetRequest(baseUrl + "/getCuentaEtapasProcurador/" + token, null);


        responseCode = helper.getResponseCode(responseStr);

        if (responseCode != 0) {
            Log.d(LOG_TAG,"fallo al obtener las causas");
            return;
        }

        helper.createCauses(responseStr);
    }



    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount,context);


        }

        return newAccount;
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
