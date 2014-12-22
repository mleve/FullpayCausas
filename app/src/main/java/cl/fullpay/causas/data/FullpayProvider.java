package cl.fullpay.causas.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import cl.fullpay.causas.data.FullpayContract.*;
/**
 * Created by mario on 15-10-14.
 */
public class FullpayProvider extends ContentProvider{

    //Variables estaticas para facilitar los switchs, 1 valor por URI
    private static final int COURT = 100;
    private static final int COURT_ID = 101;
    private static final int COURT_WITH_NAME = 102;
    private static final int COURTS_WITH_CAUSES= 103;
    private static final int STAGE = 200;
    private static final int STAGE_ID = 201;
    private static final int ATTORNEY = 300;
    private static final int ATTORNEY_WITH_NAME = 301;
    private static final int CAUSE= 400;


    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private FullpayDbHelper mOpenHelper;

    @Override
    public boolean onCreate() {

        mOpenHelper = new FullpayDbHelper(getContext());
        return true;
    }


    public static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FullpayContract.CONTENT_AUTHORITY;
        matcher.addURI(authority,FullpayContract.PATH_COURT,COURT);
        matcher.addURI(authority,FullpayContract.PATH_COURT + "/#",COURT_ID);
        matcher.addURI(authority,FullpayContract.PATH_COURT + "/*",COURT_WITH_NAME);
        matcher.addURI(authority,FullpayContract.PATH_STAGE,STAGE);
        matcher.addURI(authority,FullpayContract.PATH_ATTORNEY,ATTORNEY);
        matcher.addURI(authority,FullpayContract.PATH_ATTORNEY +"/*",ATTORNEY_WITH_NAME);
        matcher.addURI(authority,FullpayContract.PATH_CAUSE,CAUSE);
        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case COURT:
                return CourtEntry.CONTENT_TYPE;
            case COURT_ID:
                return CourtEntry.CONTENT_ITEM_TYPE;
            case COURT_WITH_NAME:
                return CourtEntry.CONTENT_ITEM_TYPE;
            case STAGE:
                return StageEntry.CONTENT_TYPE;
            case ATTORNEY:
                return AttorneyEntry.CONTENT_TYPE;
            case ATTORNEY_WITH_NAME:
                return AttorneyEntry.CONTENT_ITEM_TYPE;
            case CAUSE:
                return CauseEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknow uri: "+uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        //Solo hacer match a las rutas raiz de las Uri, para que se notifiquen a todos sus
        //decendientes
        switch (match){
            case COURT:{
                long _id = db.insert(CourtEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = CourtEntry.buildCourtUri(_id);
                else
                    throw new SQLException("Failed to insert row into "+ uri);
                break;
            }
            case STAGE:{
                long _id = db.insert(StageEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = StageEntry.buildStageUri(_id);
                else
                    throw new SQLException("Failed to insert row into "+ uri);
                break;
            }
            case ATTORNEY:{
                long _id = db.insert(AttorneyEntry.TABLE_NAME,null,values);
                if(_id> 0)
                    returnUri = AttorneyEntry.buildAttorneyUri(_id);
                else
                    throw new SQLException("failed to insert row into "+uri);
                break;
            }
            case CAUSE:{
                long _id = db.insert(CauseEntry.TABLE_NAME,null,values);
                if (_id > 0)
                    returnUri = CauseEntry.buildCauseUri(_id);
                else
                    throw new SQLException("failed to insert row into "+uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null,false);
        return returnUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case COURT_WITH_NAME:{
                retCursor = getCourtByName(uri,projection,sortOrder);
                break;
            }
            case COURT_ID:{
                retCursor = getCourtById(uri, projection, sortOrder);
                break;
            }
            case COURT:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CourtEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case COURTS_WITH_CAUSES:{
                retCursor = getCourtsWithCauses(uri,projection,sortOrder);
                break;
            }
            case STAGE:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        StageEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ATTORNEY:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AttorneyEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ATTORNEY_WITH_NAME:{
                retCursor = getAttorneyByUsername(uri,projection,sortOrder);
                break;
            }
            case CAUSE:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CauseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("unknown uri: "+uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);

        return retCursor;
    }



    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[]
                      selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match){
            case ATTORNEY:
                rowsUpdated = db.update(AttorneyEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case COURT:
                rowsUpdated = db.update(CourtEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case STAGE:
                rowsUpdated =db.update(StageEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case CAUSE:
                rowsUpdated = db.update(CauseEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+ uri);
        }
        if(rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri,null,false);

        return rowsUpdated;
    }

    //querys Strings

    private static final SQLiteQueryBuilder sCourtQueryBuilder;
    private static final SQLiteQueryBuilder sAttorneyQueryBuilder;

    static{
        sCourtQueryBuilder = new SQLiteQueryBuilder();
        sCourtQueryBuilder.setTables(CourtEntry.TABLE_NAME);

        sAttorneyQueryBuilder = new SQLiteQueryBuilder();
        sAttorneyQueryBuilder.setTables(AttorneyEntry.TABLE_NAME);


    }


    private static final String sCourtByName =
            CourtEntry.TABLE_NAME+"."+CourtEntry.COLUMN_NAME + " = ? ";
    private static final String sCourtById =
            CourtEntry.TABLE_NAME+"."+CourtEntry._ID + " = ? ";

    private static final String sAttorneyByUsername =
            AttorneyEntry.TABLE_NAME+"."+AttorneyEntry.COLUMN_USERNAME +" = ? ";





    private Cursor getCourtByName(Uri uri, String[] projection, String sortOrder) {
        String courtName = CourtEntry.getNameFromUri(uri);
        return sCourtQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sCourtByName,
                new String[]{courtName},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getCourtById(Uri uri, String[] projection, String sortOrder){
        long courtId = ContentUris.parseId(uri);
        return sCourtQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sCourtById,
                new String[]{""+courtId},
                null,
                null,
                sortOrder
        );

    }

    private Cursor getAttorneyByUsername(Uri uri, String[] projection, String sortOrder) {
        String username = AttorneyEntry.getUsernameFromUri(uri);
        return sAttorneyQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sAttorneyByUsername,
                new String[]{username},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getCourtsWithCauses(Uri uri, String[] projection, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        return  db.rawQuery(
                String.format(
                        "SELECT DISTINCT %s FROM %s.%s ",
                        CauseEntry.COLUMN_COURT_KEY,
                        CauseEntry.TABLE_NAME,
                        CauseEntry.COLUMN_COURT_KEY
                ),
                null
        );
    }

}
