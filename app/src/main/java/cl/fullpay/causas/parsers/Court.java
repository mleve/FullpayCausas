package cl.fullpay.causas.parsers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import cl.fullpay.causas.data.FullpayContract.CourtEntry;

import org.json.JSONObject;

/**
 * Created by mario on 05-11-14.
 */
public class Court implements ParserInterface {

    private int id;
    private String name;
    private int parent;


    public Court(int id, String name, int parentId) {
        this.id = id;
        this.name = name;
        this.parent = parentId;
    }


    @Override
    public void update(Context ctx) {
        ContentValues values = new ContentValues();
        values.put(CourtEntry.COLUMN_NAME,name);
        values.put(CourtEntry.COLUMN_PARENT_COURT_KEY,parent);

        ctx.getContentResolver().update(
                CourtEntry.CONTENT_URI,
                values,
                CourtEntry._ID+"= ?",
                new String[]{""+id}
        );



    }

    @Override
    public void save(Context mContext) {
        ContentValues values = new ContentValues();
        values.put(CourtEntry._ID,id);
        values.put(CourtEntry.COLUMN_NAME,name);
        values.put(CourtEntry.COLUMN_PARENT_COURT_KEY,parent);

        mContext.getContentResolver().insert(
                CourtEntry.CONTENT_URI,
                values
        );
    }

    @Override
    public boolean exists(Context ctx) {
        Cursor c =ctx.getContentResolver().query(
                CourtEntry.CONTENT_URI,
                null,
                CourtEntry._ID+"= ?",
                new String[]{""+id},
                null
        );
        boolean response = c.moveToFirst();
        c.close();
        return response;
    }
}
