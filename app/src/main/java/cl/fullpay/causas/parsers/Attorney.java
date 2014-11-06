package cl.fullpay.causas.parsers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import cl.fullpay.causas.data.FullpayContract;

/**
 * Created by mario on 05-11-14.
 */
public class Attorney implements ParserInterface {

    private String username;
    private String password;
    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



    @Override
    public void update(Context mContext) {
        ContentValues values = new ContentValues();
        values.put(FullpayContract.AttorneyEntry.COLUMN_TOKEN,getToken());
        values.put(FullpayContract.AttorneyEntry.COLUMN_PASSWORD,getPassword());

        mContext.getContentResolver().update(
                FullpayContract.AttorneyEntry.CONTENT_URI,
                values,
                FullpayContract.AttorneyEntry.COLUMN_USERNAME+"= ?",
                new String[]{getUsername()}
        );
    }

    @Override
    public void save(Context mContext) {
        ContentValues values = new ContentValues();
        values.put(FullpayContract.AttorneyEntry.COLUMN_USERNAME,username);
        values.put(FullpayContract.AttorneyEntry.COLUMN_PASSWORD,password);
        values.put(FullpayContract.AttorneyEntry.COLUMN_TOKEN,token);
        mContext.getContentResolver().insert(
                FullpayContract.AttorneyEntry.CONTENT_URI,
                values
        );
    }

    @Override
    public boolean exists(Context mContext) {
        Cursor AttorneyCursor = mContext.getContentResolver().query(
                FullpayContract.AttorneyEntry.buildAttorneyWithName(getUsername()),
                null,
                null,
                null,
                null
        );
        boolean response = AttorneyCursor.moveToFirst();
        AttorneyCursor.close();
        return response;
    }
}
