package cl.fullpay.causas.parsers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

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
        comments = obj.getString("observaciones");
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

        ctx.getContentResolver().update(
                CauseEntry.CONTENT_URI,
                values,
                CauseEntry.COLUMN_CAUSE_ID+"= ?",
                new String[]{""+accountId}
        );


    }

    @Override
    public void save(Context mContext) {
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
