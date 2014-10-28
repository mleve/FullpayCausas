package cl.fullpay.causas.HttpTasks;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by mario on 24-09-14.
 */
public class HttpGetTask extends AsyncTask<Void, Void, String> {

    private final String LOG_TAG = HttpGetTask.class.getSimpleName();

    private String mUrl=null;
    private ArrayList<NameValuePair> mParams;

    private OnPostExecuteListener mPostExecuteListener = null;

    public static interface OnPostExecuteListener{
        void onPostExecute(String result);
    }

    public HttpGetTask(ArrayList<NameValuePair> params,
                       String url,
                       OnPostExecuteListener postExecuteListener) {
        mParams = params;
        mPostExecuteListener = postExecuteListener;
        mUrl = url;
    }


    @Override
    protected String doInBackground(Void... params) {



        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String responseStr = null;

        Log.d(LOG_TAG, "iniciando la wea");


        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpGet = new HttpPost(mUrl);
            if(mParams != null) {
                // Add your data
                httpGet.setEntity(new UrlEncodedFormEntity(mParams));
            }
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpGet);



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

    }
}