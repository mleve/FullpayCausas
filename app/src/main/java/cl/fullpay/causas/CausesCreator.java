package cl.fullpay.causas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.fullpay.causas.data.FullpayContract;
import cl.fullpay.causas.data.FullpayContract.CauseEntry;

/**
 * Created by mario on 29-10-14.
 *
 * Esta clase ocupa los HTTPTask para pedir al servidor las causas y guardarlas en la Base de
 * datos, se debe realizar por pasos, pidiendo primero las etapas y cortes,y luego las causas
 *
 */
public class CausesCreator {
    private final String LOG_TAG = CausesCreator.class.getSimpleName();

    private Context context;
    public CausesCreator(Context ctx){
        context = ctx;
    }

    public Boolean createStages(String response){
        if(response == null)
            return false;

        try {
            JSONObject parsedResponse = new JSONObject(response);
            int responseCode = parsedResponse.getInt("response");
            if(responseCode != 0)
                return false;
            JSONArray stages = parsedResponse.getJSONArray("etapas");

            for(int i =0; i< stages.length();i++){
                insertStage(stages.getJSONObject(i));
            }
        }
        catch (JSONException e){
            Log.d(LOG_TAG,"Fallo al intentar crear etapas");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void insertStage(JSONObject stage){
        String id = null;
        try {
            id = stage.getString("id");
            String name = stage.getString("nombre");

            ContentValues values = new ContentValues();
            values.put(FullpayContract.StageEntry._ID, id);
            values.put(FullpayContract.StageEntry.COLUMN_NAME, name);

            context.getContentResolver().insert(
                    FullpayContract.StageEntry.CONTENT_URI,
                    values
            );
        }catch (Exception e){
            Log.d(LOG_TAG,"Fallo al intentar ingresar etapa: "+id);
        }
    }

    public Boolean createCourts(String response) {
        if(response == null)
            return false;

        try {
            JSONObject parsedResponse = new JSONObject(response);
            int responseCode = parsedResponse.getInt("response");
            if(responseCode != 0)
                return false;
            JSONArray stages = parsedResponse.getJSONArray("tribunales");

            for(int i =0; i< stages.length();i++){
                insertCourt(stages.getJSONObject(i));
            }
        }
        catch (JSONException e){
            Log.d(LOG_TAG,"Fallo al intentar crear cortes");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void insertCourt(JSONObject stage){
        String id = null;
        try {
            id = stage.getString("id");
            String name = stage.getString("nombre");
            String parent = stage.getString("id_tribunal_padre");

            ContentValues values = new ContentValues();
            values.put(FullpayContract.CourtEntry._ID, id);
            values.put(FullpayContract.CourtEntry.COLUMN_NAME, name);
            values.put(FullpayContract.CourtEntry.COLUMN_PARENT_COURT_KEY, parent);

            context.getContentResolver().insert(
                    FullpayContract.CourtEntry.CONTENT_URI,
                    values
            );
        }catch (Exception e){
            Log.d(LOG_TAG,"Fallo al intentar ingresar corte: "+id);
        }
    }

    public Boolean createCauses(String response){
        if(response == null)
            return false;

        //En este punto no deberia fallar
        Cursor attorneyCursor = context.getContentResolver().query(
                FullpayContract.AttorneyEntry.CONTENT_URI,
                null,
                FullpayContract.AttorneyEntry.COLUMN_IS_ACTIVE+" = ?",
                new String[]{"1"},
                null
        );
        int attorneyId;
        if(attorneyCursor.moveToFirst()){
           attorneyId = attorneyCursor.getInt(
                   attorneyCursor.getColumnIndex(
                           FullpayContract.AttorneyEntry._ID
                   )
           );
        }
        else{
            //Esto no deberia pasar
            Log.e(LOG_TAG,"Se intenta crear causas sin un usuario activo");
            return false;
        }

        try {
            JSONObject parsedResponse = new JSONObject(response);
            int responseCode = parsedResponse.getInt("response");
            if(responseCode == 23){
                //TODO Token de session expirado, volver a logear
                return false;
            }
            else if(responseCode == 0) {

                JSONArray stages = parsedResponse.getJSONArray("causas");

                for (int i = 0; i < stages.length(); i++) {
                    insertCause(stages.getJSONObject(i),attorneyId);
                }
            }
            else
                return false;
        }
        catch (JSONException e){
            Log.d(LOG_TAG,"Fallo al intentar crear Causas");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void insertCause(JSONObject cause ,int attorneyId) {
        String id = null;
        try {

            id = cause.getString("id_cuenta");
            String rut= cause.getString("rut");
            String names = cause.getString("nombres");
            String last_name = cause.getString("ap_pat");
            String warrant = cause.getString("exorto");
            String stageId = cause.getString("id_etapa");
            String courtName = cause.getString("tribunal");
            String comment = cause.getString("observaciones");
            String changeDate = cause.getString("fecha_etapa");

            if(comment == null || comment == "null")
                comment = "";

            String[] rol = cause.getString("rol").split("-");

            int courtId;
            Cursor CourtCursor = context.getContentResolver().query(
                    FullpayContract.CourtEntry.buildCourtWithName(courtName),
                    null,
                    null,
                    null,
                    null
            );

            ContentValues values = new ContentValues();
            if(CourtCursor.moveToFirst()) {
                courtId = CourtCursor.getInt(
                        CourtCursor.getColumnIndex(
                                CauseEntry._ID
                        )
                );
                values.put(CauseEntry.COLUMN_COURT_KEY,courtId);
            }
            else{
                values.putNull(CauseEntry.COLUMN_COURT_KEY);
            }


            values.put(CauseEntry.COLUMN_CAUSE_ID,id);
            values.put(CauseEntry.COLUMN_RUT,rut);
            values.put(CauseEntry.COLUMN_NAMES,names);
            values.put(CauseEntry.COLUMN_LAST_NAME,last_name);
            values.put(CauseEntry.COLUMN_WARRANT,warrant);
            values.put(CauseEntry.COLUMN_ROL_NUM,rol[0]);
            values.put(CauseEntry.COLUMN_ROL_DATE,rol[1]);
            values.put(CauseEntry.COLUMN_COMMENT,comment);
            values.put(CauseEntry.COLUMN_CHANGE_DATE,changeDate);
            values.put(CauseEntry.COLUMN_STAGE_KEY,stageId);
            values.put(CauseEntry.COLUMN_ATTORNEY_KEY,attorneyId);


            CourtCursor.close();
            context.getContentResolver().insert(
                    CauseEntry.CONTENT_URI,
                    values
            );
        }catch (Exception e){
            Log.d(LOG_TAG,"Fallo al intentar ingresar causa: "+id);
        }

    }
}
