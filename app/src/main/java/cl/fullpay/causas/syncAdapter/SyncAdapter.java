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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.fullpay.causas.AsyncTasks.CausesTask;
import cl.fullpay.causas.R;
import cl.fullpay.causas.data.FullpayContract;
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
        String baseUrl = "http://dev.empchile.net/forseti/index.php/admin/api";

        //Chequear que existe procurador o llevarlo a login
        Cursor attorneyCursor = getContext().getContentResolver().query(
                FullpayContract.AttorneyEntry.CONTENT_URI,
                null,
                FullpayContract.AttorneyEntry.COLUMN_IS_ACTIVE+" = ?",
                new String[]{"1"},
                null
        );

        String token;
        if(attorneyCursor.moveToFirst()){
            token = attorneyCursor.getString(
                    attorneyCursor.getColumnIndex(FullpayContract.AttorneyEntry.COLUMN_TOKEN)
            );
        }
        else{
            Log.d(LOG_TAG,"fallo al obtener el token desde la bd");
            return;
        }

        Helper helper = new Helper(getContext());
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
        Log.d(LOG_TAG,"hola, me mandaron a sincronizar inmediatamente :(");
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
        Log.d(LOG_TAG,"callback de onAccountCreated");
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
