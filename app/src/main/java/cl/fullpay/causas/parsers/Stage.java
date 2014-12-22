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
    private String code;
    private String successor;
    private String type;


    public Stage(int id, String name, String code, String successor, String type) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.successor = successor;
        this.type = type;
    }


    @Override
    public void update(Context ctx) {
        ContentValues values = new ContentValues();
        values.put(StageEntry.COLUMN_NAME,name);
        values.put(StageEntry.COLUMN_CODE,code);
        values.put(StageEntry.COLUMN_SUCCESSORS,successor);
        values.put(StageEntry.COLUMN_TYPE,type);

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
        values.put(StageEntry.COLUMN_CODE,code);
        values.put(StageEntry.COLUMN_SUCCESSORS,successor);
        values.put(StageEntry.COLUMN_TYPE,type);
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
