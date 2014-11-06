package cl.fullpay.causas.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.fullpay.causas.parsers.Attorney;
import cl.fullpay.causas.parsers.Court;

/**
 * Created by mario on 05-11-14.
 */
public class CourtsTask extends BaseTask{
    private static String LOG_TAG = CourtsTask.class.getSimpleName();

    protected CourtsTask(Context ctx) {
        super(ctx);
    }


    protected Boolean doInBackground(Void... voids) {
        String responseStr = httpGetRequest(baseUrl+"/getTribunales",null);
        Log.d(LOG_TAG,"respuesta de tribunales: "+responseStr);

        int responseCode = getResponseCode(responseStr);

        if (responseCode != 0)
            return false;

        return createCourts(responseStr);

    }

    private Boolean createCourts(String response) {
      try {
            JSONObject parsedResponse = new JSONObject(response);
            JSONArray courts = parsedResponse.getJSONArray("tribunales");

            for(int i =0; i< courts.length();i++){
                JSONObject obj = courts.getJSONObject(i);
                int id = obj.getInt("id");
                String name = obj.getString("nombre");
                int parentId = obj.getInt("id_tribunal_padre");
                Court court = new Court(id,name,parentId);
                insertUpdate(court);
            }
        }
        catch (JSONException e){
            Log.e(LOG_TAG, "Fallo al intentar insertar cortes, error: "+e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
