package cl.fullpay.causas.parsers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import cl.fullpay.causas.data.FullpayContract;
import cl.fullpay.causas.data.FullpayContract.StageEntry;

/**
 * Created by mario on 05-11-14.
 */
public class Stage implements ParserInterface {

    private int id;
    private String name;


    public Stage(int id, String name) {
        this.id = id;
        this.name = name;
    }


    @Override
    public void update(Context ctx) {
        ContentValues values = new ContentValues();
        values.put(StageEntry.COLUMN_NAME,name);

        ctx.getContentResolver().update(
                StageEntry.CONTENT_URI,
                values,
                StageEntry._ID+"= ?",
                new String[]{""+id}
        );



    }

    @Override
    public void save(Context mContext) {
        ContentValues values = new ContentValues();
        values.put(StageEntry._ID,id);
        values.put(StageEntry.COLUMN_NAME,name);
        mContext.getContentResolver().insert(
                StageEntry.CONTENT_URI,
                values
        );
    }

    @Override
    public boolean exists(Context ctx) {
        Cursor c =ctx.getContentResolver().query(
                StageEntry.CONTENT_URI,
                null,
                StageEntry._ID+"= ?",
                new String[]{""+id},
                null
        );
        boolean response = c.moveToFirst();
        c.close();
        return response;
    }
}
