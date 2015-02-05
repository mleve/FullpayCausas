package cl.fullpay.causas.syncAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cl.fullpay.causas.R;
import cl.fullpay.causas.data.FullpayContract;
import cl.fullpay.causas.data.FullpayContract.*;
import cl.fullpay.causas.data.FullpayDbHelper;
import cl.fullpay.causas.parsers.Attorney;
import cl.fullpay.causas.parsers.Cause;
import cl.fullpay.causas.parsers.Court;
import cl.fullpay.causas.parsers.ParserInterface;
import cl.fullpay.causas.parsers.Stage;

/**
 * Created by mario on 17-12-14.
 */
public class Helper {
    private Context mContext;
    private static final String LOG_TAG = Helper.class.getSimpleName();

    public Helper(Context ctx){
        mContext = ctx;
    }

    public Boolean createCauses(String response) {
        try {
            JSONObject parsedResponse = new JSONObject(response);
            JSONArray causes = parsedResponse.getJSONArray("causas");
            Cursor cursor = mContext.getContentResolver().query(
                    AttorneyEntry.CONTENT_URI,
                    null,
                    AttorneyEntry.COLUMN_IS_ACTIVE+"= ?",
                    new String[]{""+1},
                    null
            );
            int attorneyId;
            if(cursor.moveToFirst()){
                attorneyId = cursor.getInt(
                        cursor.getColumnIndex(AttorneyEntry._ID)
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
                        FullpayContract.CourtEntry.CONTENT_URI,
                        null,
                        FullpayContract.CourtEntry.COLUMN_NAME+"=?",
                        new String[]{obj.getString("tribunal")},
                        null
                );
                if(CourtCursor.moveToFirst()){
                    courtId = CourtCursor.getString(
                            CourtCursor.getColumnIndex(CourtEntry._ID)
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
            Log.e(LOG_TAG, "Fallo al intentar insertar cortes, error: " + e);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Boolean createCourts(String response) {
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

    public Boolean createStages(String response) {
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
                Stage stage = new Stage(id, name, code, successor, type);

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

    public int getResponseCode(String rawResponse){
        try {
            JSONObject response = new JSONObject(rawResponse);
            int responseCode = response.getInt("response");
            return responseCode;

        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }

    }

    public int getErrorCode(String rawResponse){
        try {
            JSONObject response = new JSONObject(rawResponse);
            int responseCode = response.getInt("error");
            return responseCode;

        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }

    }

    protected boolean logInUser(String baseUrl, ArrayList<NameValuePair> mParams){
        String responseStr = httpGetRequest(baseUrl +"/auth",mParams);
        Log.d(LOG_TAG,"respuesta autenticacion: "+responseStr);

        int responseCode = getResponseCode(responseStr);

        if (responseCode != 0)
            return false;



        String userToken = getUserToken(responseStr);
        if (userToken == null)
            return false;

        //obtener token de sesion
        Log.d(LOG_TAG,"intentando obtener token de sesion");

        responseStr = httpGetRequest(baseUrl +"/getAuthSession/"+userToken,null);


        String auth_session = getSessionToken(responseStr);
        Log.d(LOG_TAG,"token de session: "+auth_session);
        if(auth_session == null)
            return false;

        Attorney attorney = new Attorney();
        attorney.setUsername(mParams.get(0).getValue());
        attorney.setPassword(mParams.get(1).getValue());
        attorney.setToken(auth_session);
        return insertUpdate(attorney);
    }

    public String getUserToken(String responseStr) {
        try {
            JSONObject aux = new JSONObject(responseStr);
            return aux.getString("user_token");
        }
        catch (JSONException e){
            Log.e(LOG_TAG,"error al procesar token de usuario, response: "+responseStr);
            return null;
        }
    }

    public String getSessionToken(String responseStr) {
        try{
            JSONObject aux = new JSONObject(responseStr);
            return aux.getString("auth_session");

        }catch (JSONException e){
            Log.e(LOG_TAG,"error al procesar token de session, response: "+responseStr);
            return null;
        }

    }


    public boolean insertUpdate(ParserInterface object){
        if(object.exists(mContext)){
            //Ya existia este registro en la Bd, revisar si es necesario actualizar
            object.update(mContext);
        }

        else{
            object.save(mContext);
        }
        return true;


    }

    public String httpGetRequest(String mUrl,ArrayList<NameValuePair> mParams){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String responseStr = null;

        Log.d(LOG_TAG, "Intentando http a "+mUrl);
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


    public String getUserToken(Context context){
        //Chequear que existe procurador o llevarlo a login
        Cursor attorneyCursor = context.getContentResolver().query(
                AttorneyEntry.CONTENT_URI,
                null,
                AttorneyEntry.COLUMN_IS_ACTIVE+" = ?",
                new String[]{"1"},
                null
        );


        String token;
        if(attorneyCursor.moveToFirst()){
            token = attorneyCursor.getString(
                    attorneyCursor.getColumnIndex(AttorneyEntry.COLUMN_TOKEN)
            );
            return token;
        }
        else{
            return null;
        }

    }

    public void sendUpdatesToServer(Context context,String token) {

        Cursor changedCausesCursor = context.getContentResolver().query(
                CauseEntry.CONTENT_URI,
                null,
                CauseEntry.COLUMN_HAS_CHANGED+"=?",
                new String[]{"1"},
                null
        );

        while(changedCausesCursor.moveToNext()){
            String idCause = changedCausesCursor.getString(
                    changedCausesCursor.getColumnIndex(CauseEntry.COLUMN_CAUSE_ID)
            );
            String idStage   = changedCausesCursor.getString(
                    changedCausesCursor.getColumnIndex(CauseEntry.COLUMN_STAGE_KEY)
            );
            String changeDate = changedCausesCursor.getString(
                    changedCausesCursor.getColumnIndex(CauseEntry.COLUMN_CHANGE_DATE)
            );
            String comments   = changedCausesCursor.getString(
                    changedCausesCursor.getColumnIndex(CauseEntry.COLUMN_COMMENT)
            );

            sendChanges(token,idCause,idStage,changeDate,comments);
            setCauseUnchanged(idCause,context);
        }
        changedCausesCursor.close();
    }

    private void setCauseUnchanged(String idCause, Context ctx) {
        ContentValues data = new ContentValues();
        data.put(CauseEntry.COLUMN_HAS_CHANGED,"0");
        ctx.getContentResolver().update(
                CauseEntry.CONTENT_URI,
                data,
                CauseEntry.COLUMN_CAUSE_ID+"=?",
                new String[]{""+idCause}
        );
    }

    private void sendChanges(String token, String idCause, String idStage, String changeDate, String comments) {

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("id_cuenta",idCause));
        params.add(new BasicNameValuePair("id_etapa",idStage));
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date;
        try{
            date = formatter.parse(changeDate);
        }catch(ParseException e){
            date = Calendar.getInstance().getTime();
        }
        formatter.applyPattern("yyyy-MM-dd");
        String formatDate = formatter.format(date);
        params.add(new BasicNameValuePair("fecha_etapa",formatDate));
        params.add(new BasicNameValuePair("observaciones",comments));

        Log.d(LOG_TAG,
                String.format("Se envian a guardar: id_cuenta: %s , id_etapa %s , fecha_etapa: %s , observaciones: %s ",
                        idCause,idStage,formatDate,comments)
            );
        String baseUrl = mContext.getString(R.string.api_base_url);
        String responseStr = httpGetRequest(baseUrl+"/insertCausa/"+token,params);

        int responseCode = getResponseCode(responseStr);

        if (responseCode != 0)
            Log.d(LOG_TAG,"fallo al enviar causa: "+idCause+" response: "+responseStr);
    }

    public Cursor getCourtsForAttorney(){
        FullpayDbHelper dbHelper = new FullpayDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = String.format(
                "SELECT %s.%s, %s, %s " +
                        "FROM %s, %s " +
                        "WHERE %s.%s = %s.%s " +
                        "GROUP BY %s",
                CauseEntry.TABLE_NAME,
                CauseEntry._ID,
                CauseEntry.COLUMN_COURT_KEY,
                FullpayContract.CourtEntry.COLUMN_NAME,
                CauseEntry.TABLE_NAME,
                FullpayContract.CourtEntry.TABLE_NAME,
                CauseEntry.TABLE_NAME,
                CauseEntry.COLUMN_COURT_KEY,
                FullpayContract.CourtEntry.TABLE_NAME,
                FullpayContract.CourtEntry._ID,
                CauseEntry.COLUMN_COURT_KEY
        );
        Log.d(LOG_TAG, "query para courts: "+query);

        return db.rawQuery(
                query,
                null
        );
    }
}
