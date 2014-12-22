package cl.fullpay.causas.AsyncTasks;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.fullpay.causas.data.FullpayContract;
import cl.fullpay.causas.parsers.Attorney;
import cl.fullpay.causas.parsers.Cause;
import cl.fullpay.causas.parsers.Court;
import cl.fullpay.causas.parsers.Stage;

/**
 * Created by mario on 05-11-14.
 */
public class CausesTask extends BaseTask{
    private static String LOG_TAG = CausesTask.class.getSimpleName();

    //TODO consolidar tasks en una, solo necesito eso al final :/
    protected CausesTask(Context ctx) {
        super(ctx);
    }

    //Por defecto, intenta obtener causas asumiendo que existe lo demas
    protected Boolean doInBackground(Void... voids) {


        Cursor attorneyCursor = mContext.getContentResolver().query(
                FullpayContract.AttorneyEntry.CONTENT_URI,
                null,
                FullpayContract.AttorneyEntry.COLUMN_IS_ACTIVE+"= ?",
                new String[]{"1"},
                null
        );
        String token;
        if(attorneyCursor.moveToFirst()){
            token = attorneyCursor.getString(
                    attorneyCursor.getColumnIndex(
                            FullpayContract.AttorneyEntry.COLUMN_TOKEN
                    )
            );
        }
        else{
            return false;
        }

        String responseStr = httpGetRequest(baseUrl+"/getCuentaEtapasProcurador/"+token,null);
        Log.d(LOG_TAG,"respuesta de causas: "+responseStr);

        int responseCode = getResponseCode(responseStr);

        if (responseCode != 0)
            return false;

        return createCauses(responseStr);

    }

    protected Boolean createCauses(String response) {
      try {
            JSONObject parsedResponse = new JSONObject(response);
            JSONArray causes = parsedResponse.getJSONArray("causas");
            Cursor cursor = mContext.getContentResolver().query(
                  FullpayContract.AttorneyEntry.CONTENT_URI,
                  null,
                  FullpayContract.AttorneyEntry.COLUMN_IS_ACTIVE+"= ?",
                  new String[]{""+1},
                  null
             );
            int attorneyId;
            if(cursor.moveToFirst()){
              attorneyId = cursor.getInt(
                      cursor.getColumnIndex(FullpayContract.AttorneyEntry._ID)
              );
              cursor.close();
            }
            else{
              cursor.close();
              return false;
            }
            for(int i =0; i< causes.length();i++){
                JSONObject obj = causes.getJSONObject(i);
                Cause cause= new Cause(obj);
                cause.setAttorneyId(attorneyId);
                String courtId;
                Cursor CourtCursor = mContext.getContentResolver().query(
                        FullpayContract.CourtEntry.buildCourtWithName(obj.getString("tribunal")),
                        null,
                        null,
                        null,
                        null
                );
                if(CourtCursor.moveToFirst()){
                    courtId = CourtCursor.getString(
                            CourtCursor.getColumnIndex(FullpayContract.CourtEntry._ID)
                    );
                }
                else
                    courtId = null;
                CourtCursor.close();
                cause.setCourtId(courtId);

                insertUpdate(cause);
            }
        }
        catch (JSONException e){
            Log.e(LOG_TAG, "Fallo al intentar insertar cortes, error: "+e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    protected Boolean createCourts(String response) {
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

    protected Boolean createStages(String response) {
        try {
            JSONObject parsedResponse = new JSONObject(response);
            JSONArray courts = parsedResponse.getJSONArray("etapas");


            for(int i =0; i< courts.length();i++){
                JSONObject obj = courts.getJSONObject(i);
                int id = obj.getInt("id");
                String name = obj.getString("nombre");
                String code = obj.getString("codigo");
                String successor = obj.getString("sucesor");
                String type = obj.getString("tipo");
                Stage stage = new Stage(id,name,code,successor,type);

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
