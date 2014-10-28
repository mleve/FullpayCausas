package cl.fullpay.causas.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cl.fullpay.causas.data.FullpayContract.*;

/**
 * Created by mario on 12-10-14.
 */
public class FullpayDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "fullpay.db";

    public FullpayDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_COURT_TABLE =
                String.format("CREATE TABLE %s " +
                        "(%s INTEGER PRIMARY KEY, " +
                        "%s TEXT NOT NULL, " +
                        "%s INTEGER )",
                        CourtEntry.TABLE_NAME,
                        CourtEntry._ID,
                        CourtEntry.COLUMN_NAME,
                        CourtEntry.COLUMN_PARENT_COURT_KEY);

        final String SQL_CREATE_STAGE_TABLE =
                String.format("CREATE TABLE %s "+
                "(%s INTEGER PRIMARY KEY, "+
                "%s TEXT NOT NULL )",
                        StageEntry.TABLE_NAME,
                        StageEntry._ID,
                        StageEntry.COLUMN_NAME);

        final String SQL_CREATE_ATTORNEY_TABLE =
                String.format("CREATE TABLE %s "+
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT NOT NULL, "+
                "%s TEXT NOT NULL, "+
                "%s TEXT NOT NULL, "+
                "UNIQUE ( %s ) ON CONFLICT REPLACE);",
                        AttorneyEntry.TABLE_NAME,
                        AttorneyEntry._ID,
                        AttorneyEntry.COLUMN_USERNAME,
                        AttorneyEntry.COLUMN_PASSWORD,
                        AttorneyEntry.COLUMN_TOKEN,
                        AttorneyEntry.COLUMN_USERNAME
                );

        final String SQL_CREATE_CAUSE_TABLE =
                String.format("CREATE TABLE %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "%s INTEGER, "+
                "%s TEXT, "+
                "%s TEXT NOT NULL, "+
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL, "+
                "%s TEXT NOT NULL, "+
                "%s TEXT NOT NULL, "+
                "%s INTEGER NOT NULL, "+
                "%s INTEGER NOT NULL, "+
                "%s INTEGER NOT NULL, "+
                "FOREIGN KEY ( %s ) REFERENCES %s (%s) , "+
                "FOREIGN KEY ( %s ) REFERENCES %s (%s) , "+
                "FOREIGN KEY ( %s ) REFERENCES %s (%s) );",
                        CauseEntry.TABLE_NAME,
                        CauseEntry.COLUMN_ROL_NUM,
                        CauseEntry.COLUMN_ROL_DATE,
                        CauseEntry.COLUMN_RUT,
                        CauseEntry.COLUMN_NAMES,
                        CauseEntry.COLUMN_LAST_NAME,
                        CauseEntry.COLUMN_COMMENT,
                        CauseEntry.COLUMN_CHANGE_DATE,
                        CauseEntry.COLUMN_WARRANT,
                        CauseEntry.COLUMN_COURT_KEY,
                        CauseEntry.COLUMN_STAGE_KEY,
                        CauseEntry.COLUMN_ATTORNEY_KEY,
                        CauseEntry.COLUMN_COURT_KEY,
                        CourtEntry.TABLE_NAME,
                        CourtEntry._ID,
                        CauseEntry.COLUMN_STAGE_KEY,
                        StageEntry.TABLE_NAME,
                        StageEntry._ID,
                        CauseEntry.COLUMN_ATTORNEY_KEY,
                        AttorneyEntry.TABLE_NAME,
                        AttorneyEntry._ID
                       );

        sqLiteDatabase.execSQL(SQL_CREATE_COURT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_STAGE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ATTORNEY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CAUSE_TABLE);



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
