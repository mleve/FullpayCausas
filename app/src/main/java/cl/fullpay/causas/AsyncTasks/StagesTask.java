package cl.fullpay.causas.AsyncTasks;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.fullpay.causas.parsers.Court;
import cl.fullpay.causas.parsers.Stage;

/**
 * Created by mario on 05-11-14.
 */
public class StagesTask extends BaseTask{
    private static String LOG_TAG = StagesTask.class.getSimpleName();

    protected StagesTask(Context ctx) {
        super(ctx);
    }


    protected Boolean doInBackground(Void... voids) {
        String responseStr = httpGetRequest(baseUrl+"/getEtapas",null);
        Log.d(LOG_TAG,"respuesta de Etapas: "+responseStr);

        int responseCode = getResponseCode(responseStr);

        if (responseCode != 0)
            return false;

        return createStages(responseStr);

    }

    private Boolean createStages(String response) {
      try {
            JSONObject parsedResponse = new JSONObject(response);
            JSONArray courts = parsedResponse.getJSONArray("etapas");

            for(int i =0; i< courts.length();i++){
                JSONObject obj = courts.getJSONObject(i);
                int id = obj.getInt("id");
                String name = obj.getString("nombre");
                Stage stage = new Stage(id,name);
                insertUpdate(stage);
            }
        }
        catch (JSONException e){
            Log.e(LOG_TAG, "Fallo al intentar etapas , error: "+e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
