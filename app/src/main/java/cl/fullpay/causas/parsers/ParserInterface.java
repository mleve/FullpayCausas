package cl.fullpay.causas.parsers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by mario on 05-11-14.
 */
public interface ParserInterface {


    public void update(Context ctx);

    public void save(Context mContext);

    public boolean exists(Context ctx);
}
