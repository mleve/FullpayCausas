package cl.fullpay.causas.AsyncTasks;

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
import cl.fullpay.causas.parsers.Attorney;
import cl.fullpay.causas.parsers.ParserInterface;

/**
 * Created by mario on 05-11-14.
 *
 * Clase base para AsyncTasks, contiene los metodos de login pues en cualquier solicitud
 * el servidor puede responder que el token expiro, en cuyo caso se debe intentar automaticamente
 * el relogin.
 *
 *
 */
public abstract class BaseTask extends AsyncTask<Void, Void, Boolean> {

    private static String LOG_TAG = BaseTask.class.getSimpleName();

    protected String baseUrl = "http://dev.empchile.net/forseti/index.php/admin/api";

    protected  Context mContext;


    protected BaseTask(Context ctx){
        mContext = ctx;
    }

    protected String httpGetRequest(String mUrl,ArrayList<NameValuePair> mParams){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String responseStr = null;

        Log.d(LOG_TAG, "Intentando get a "+mUrl);
        //Autentificar
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(mUrl);
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

        return responseStr;

    }

    protected int getResponseCode(String rawResponse){
        //TODO relogear si responseCode = 23
        try {
            JSONObject response = new JSONObject(rawResponse);
            int responseCode = response.getInt("response");
            return responseCode;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }



    protected boolean logInUser(ArrayList<NameValuePair> mParams){
        String responseStr = httpGetRequest(baseUrl+"/auth",mParams);
        Log.d(LOG_TAG,"respuesta autenticacion: "+responseStr);

        int responseCode = getResponseCode(responseStr);

        if (responseCode != 0)
            return false;



        String userToken = getUserToken(responseStr);
        if (userToken == null)
            return false;

        //obtener token de sesion
        Log.d(LOG_TAG,"intentando obtener token de sesion");

        responseStr = httpGetRequest(baseUrl+"/getAuthSession/"+userToken,null);


        String auth_session = getSessionToken(responseStr);
        if(auth_session == null)
            return false;

        Attorney attorney = new Attorney();
        attorney.setUsername(mParams.get(0).getValue());
        attorney.setPassword(mParams.get(1).getValue());
        attorney.setToken(auth_session);
        return insertUpdate(attorney);
    }

    protected String getUserToken(String responseStr) {
        try {
            JSONObject aux = new JSONObject(responseStr);
            return aux.getString("user_token");
        }
        catch (JSONException e){
            Log.e(LOG_TAG,"error al procesar token de usuario, response: "+responseStr);
            return null;
        }
    }

    protected String getSessionToken(String responseStr) {
        try{
            JSONObject aux = new JSONObject(responseStr);
            return aux.getString("auth_session");

        }catch (JSONException e){
            Log.e(LOG_TAG,"error al procesar token de session, response: "+responseStr);
            return null;
        }

    }


    protected boolean insertUpdate(ParserInterface object){
        if(object.exists(mContext)){
            //Ya existia este registro en la Bd, actualizar
            object.update(mContext);
        }

        else{
            object.save(mContext);
        }
        return true;


    }

}
