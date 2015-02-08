package cl.fullpay.causas.parsers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cl.fullpay.causas.data.FullpayContract;
import cl.fullpay.causas.data.FullpayContract.CauseEntry;

/**
 * Created by mario on 25-09-14.
 */
public class Cause implements ParserInterface {

    private int accountId;
    private String courtId;
    private String rolNum;
    private String rolDate;
    private String names;
    private String lastName;
    private String rut;
    private int stageId;
    private String stageDate;
    private String comments;
    private String status;
    private int warrant;
    private int attorneyId;




    public void setCourtId(String courtId) {
        this.courtId = courtId;
    }

    public void setAttorneyId(int attorneyId) {
        this.attorneyId = attorneyId;
    }

    public Cause(JSONObject obj) throws JSONException {
        accountId = obj.getInt("id_cuenta");
        stageId = obj.getInt("id_etapa");
        warrant = obj.getInt("exorto");
        String[] rol = obj.getString("rol").split("-");
        rolNum = rol[0];
        if(rol.length>=2)
            rolDate = rol[1];
        else
            rolDate="";
        names = obj.getString("nombres");
        rut = obj.getString("rut");
        lastName = obj.getString("ap_pat");
        stageDate = obj.getString("fecha_etapa");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try{
            date = formatter.parse(stageDate);
        }catch(ParseException e){
            date = Calendar.getInstance().getTime();
        }
        formatter.applyPattern("dd-MM-yyyy");
        stageDate = formatter.format(date);
        comments = (obj.getString("observaciones").equals("null")) ? "" : obj.getString("observaciones");
        status = obj.getString("estado_cuenta");

    }

    @Override
    public void update(Context ctx) {
        ContentValues values = new ContentValues();
        values.put(CauseEntry.COLUMN_STAGE_KEY,stageId);
        values.put(CauseEntry.COLUMN_WARRANT,warrant);
        values.put(CauseEntry.COLUMN_ROL_NUM,rolNum);
        values.put(CauseEntry.COLUMN_ROL_DATE,rolDate);
        values.put(CauseEntry.COLUMN_NAMES,names);
        values.put(CauseEntry.COLUMN_RUT,rut);
        values.put(CauseEntry.COLUMN_LAST_NAME,lastName);
        values.put(CauseEntry.COLUMN_CHANGE_DATE,stageDate);
        values.put(CauseEntry.COLUMN_COMMENT,comments);
        values.put(CauseEntry.COLUMN_COURT_KEY,courtId);
        values.put(CauseEntry.COLUMN_ATTORNEY_KEY,attorneyId);

        boolean hasChanged = hasChange(ctx,values);
        if (hasChanged) {
            ctx.getContentResolver().update(
                    CauseEntry.CONTENT_URI,
                    values,
                    CauseEntry.COLUMN_CAUSE_ID + "= ?",
                    new String[]{"" + accountId}
            );
        }

    }

    private boolean hasChange(Context ctx, ContentValues newValues) {
        Cursor actualCause = ctx.getContentResolver().query(
                CauseEntry.CONTENT_URI,
                null,
                CauseEntry.COLUMN_CAUSE_ID+"="+accountId,
                null,
                null
        );
        actualCause.moveToFirst();

        String columnName, dbValue,serverValue;
        for(int i=0; i<actualCause.getColumnCount();i++){
            columnName = actualCause.getColumnName(i);
            if(columnName.equals(CauseEntry._ID) ||
                    columnName.equals(CauseEntry.COLUMN_CAUSE_ID) ||
                    columnName.equals(CauseEntry.COLUMN_ATTORNEY_KEY) ||
                    columnName.equals(CauseEntry.COLUMN_HAS_CHANGED)){
                continue;
            }
            dbValue = actualCause.getString(i);
            serverValue = newValues.getAsString(columnName);
            //Log.d("Cause","valores para :"+columnName+" dbValue: "+dbValue+" serverValue: "+serverValue);
            if(serverValue ==null){
                if(dbValue != null){
                    actualCause.close();
                    return true;
                }
            }
            else if(!serverValue.equals(dbValue)){
                //Log.d("Cause","hay valores distintos en col "+columnName+": "+dbValue+" y "+serverValue);
                actualCause.close();
                return true;
            }
        }
        actualCause.close();
        return false;


    }

    @Override
    public void save(Context mContext) {
        if(status.equals("Terminado"))
            return;
        ContentValues values = new ContentValues();
        values.put(CauseEntry.COLUMN_CAUSE_ID,accountId);
        values.put(CauseEntry.COLUMN_STAGE_KEY,stageId);
        values.put(CauseEntry.COLUMN_WARRANT,warrant);
        values.put(CauseEntry.COLUMN_ROL_NUM,rolNum);
        values.put(CauseEntry.COLUMN_RUT,rut);
        values.put(CauseEntry.COLUMN_ROL_DATE,rolDate);
        values.put(CauseEntry.COLUMN_NAMES,names);
        values.put(CauseEntry.COLUMN_LAST_NAME,lastName);
        values.put(CauseEntry.COLUMN_CHANGE_DATE,stageDate);
        values.put(CauseEntry.COLUMN_COMMENT,comments);
        values.put(CauseEntry.COLUMN_ATTORNEY_KEY,attorneyId);
        values.put(CauseEntry.COLUMN_COURT_KEY,courtId);


        mContext.getContentResolver().insert(
                CauseEntry.CONTENT_URI,
                values
        );

    }

    @Override
    public boolean exists(Context ctx) {
        if(status.equals("Terminado"))
                return false;
        Cursor cursor = ctx.getContentResolver().query(
                CauseEntry.CONTENT_URI,
                null,
                CauseEntry.COLUMN_CAUSE_ID+"= ?",
                new String[]{""+accountId},
                null
        );
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }
}
