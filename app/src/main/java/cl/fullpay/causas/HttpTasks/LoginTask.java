package cl.fullpay.causas.HttpTasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import cl.fullpay.causas.data.FullpayContract;

/**
 * Created by mario on 24-09-14.
 */
public class LoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String LOG_TAG = HttpGetTask.class.getSimpleName();

    private String mUrl=null;
    private ArrayList<NameValuePair> mParams;
    private String auth_session;
    private Context mContext;

    public static interface OnPostExecuteListener{
        void onPostExecute(String result);
    }


    public LoginTask(ArrayList<NameValuePair> params,
                       String url, Context ctx){
        mContext = ctx;
        mParams = params;
        mUrl = url;
    }


    @Override
    protected Boolean doInBackground(Void... params) {



        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String responseStr = null;

        Log.d(LOG_TAG, "Intentando autentificar");

        //Autentificar
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(mUrl+"/auth");
            if(mParams != null) {
                // Add your data
                httppost.setEntity(new UrlEncodedFormEntity(mParams));
            }
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);



            // Read the input stream into a String
            InputStream inputStream = response.getEntity().getContent();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            responseStr = buffer.toString();


        } catch (Exception e) {
            Log.e(LOG_TAG, "error", e);
            responseStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        Log.d(LOG_TAG,"respuesta autenticacion: "+responseStr);

        String userToken = parseLogin(responseStr);
        if (userToken == null)
            return false;

        //obtener token de sesion
        Log.d(LOG_TAG,"intentando obtener token de sesion");
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(mUrl+"/getAuthSession/"+userToken);

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpget);



            // Read the input stream into a String
            InputStream inputStream = response.getEntity().getContent();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            responseStr = buffer.toString();


        } catch (Exception e) {
            Log.e(LOG_TAG, "error", e);
            responseStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        auth_session = parseSession(responseStr);

        return insertUpdateUser();
    }


    private boolean insertUpdateUser(){
        Cursor AttorneyCursor = mContext.getContentResolver().query(
                FullpayContract.AttorneyEntry.buildAttorneyWithName(mParams.get(0).getValue()),
                null,
                null,
                null,
                null
        );

        if(AttorneyCursor.moveToFirst()){
            //Tengo que actualizar sessionToken y por si acaso la password
            AttorneyCursor.close();
            ContentValues values = new ContentValues();
            values.put(FullpayContract.AttorneyEntry.COLUMN_TOKEN,auth_session);
            values.put(FullpayContract.AttorneyEntry.COLUMN_PASSWORD,mParams.get(1).getValue());
            mContext.getContentResolver().update(
                    FullpayContract.AttorneyEntry.CONTENT_URI,
                    values,
                    FullpayContract.AttorneyEntry.COLUMN_USERNAME+"= ?",
                    new String[]{mParams.get(0).getValue()}
            );
        }
        else{
            AttorneyCursor.close();
            ContentValues values = new ContentValues();
            values.put(FullpayContract.AttorneyEntry.COLUMN_USERNAME,mParams.get(0).getValue());
            values.put(FullpayContract.AttorneyEntry.COLUMN_PASSWORD,mParams.get(1).getValue());
            values.put(FullpayContract.AttorneyEntry.COLUMN_TOKEN,auth_session);
            mContext.getContentResolver().insert(
                    FullpayContract.AttorneyEntry.CONTENT_URI,
                    values
            );
        }
        return true;


    }

    private String parseSession(String responseStr) {
        try{
            JSONObject aux = new JSONObject(responseStr);
            return aux.getString("auth_session");

        }catch (JSONException e){
            return null;
        }

    }

    private String parseLogin(String responseStr) {
        try {
            JSONObject aux = new JSONObject(responseStr);
            int response = aux.getInt("response");
            if (response != 0)
                return null;
            else
                return aux.getString("user_token");
        }
        catch (JSONException e){
            return null;
        }
    }


    @Override
    protected void onCancelled() {

    }
}