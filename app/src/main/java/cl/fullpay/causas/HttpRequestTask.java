package cl.fullpay.causas;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mario on 24-09-14.
 */
public class HttpRequestTask extends AsyncTask<Void, Void, String> {

    private final String LOG_TAG = HttpRequestTask.class.getSimpleName();

    private String username;
    private String mPassword;
    private Context context;
    private String responseObj;
    private String[] response;

    private OnPostExecuteListener mPostExecuteListener = null;

    public static interface OnPostExecuteListener{
        void onPostExecute(String result);
    }

    HttpRequestTask(String username, String password, Context context,
                    OnPostExecuteListener postExecuteListener) {
        this.username = username;
        mPassword = password;
        this.context = context;
        mPostExecuteListener = postExecuteListener;
    }

    @Override
    protected String doInBackground(Void... params) {



        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String responseStr = null;

        Log.d(LOG_TAG, "iniciando la wea");

        //TODO encode password in sha1
        mPassword = "910d7d0bd429f9c101d067fc9c2d995c9e416f54";

        //TODO encode token in base64
        String token = "UHllWXRUcnB4MkZHZGp5UEFMclBhZEpm";

        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://dev.empchile.net/forseti/index.php/admin/api/auth");

            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("password", mPassword));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

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

        Log.d(LOG_TAG,"Respuesta: "+responseStr);

        return responseStr;
    }

    @Override
    protected void onPostExecute(final String result) {
        if (mPostExecuteListener != null){
            mPostExecuteListener.onPostExecute(result);
        }


    }

    @Override
    protected void onCancelled() {
       responseObj = null;
    }
}