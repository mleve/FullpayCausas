package cl.fullpay.causas.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import cl.fullpay.causas.Cause;
import cl.fullpay.causas.data.FullpayContract.*;

/**
 * Created by mario on 15-10-14.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void testDeleteDb(){
        mContext.deleteDatabase(FullpayDbHelper.DATABASE_NAME);
    }

    public void testGetType(){

        String courtType = mContext.getContentResolver().getType(CourtEntry.CONTENT_URI);
        assertEquals(CourtEntry.CONTENT_TYPE,courtType);

        String courtTypeItem = mContext.getContentResolver().getType(CourtEntry.buildCourtUri(1));
        assertEquals(CourtEntry.CONTENT_ITEM_TYPE,courtTypeItem);

        String courtTypeItem2 = mContext.getContentResolver().getType(
                CourtEntry.buildCourtWithName("1 civil"));
        assertEquals(CourtEntry.CONTENT_ITEM_TYPE,courtTypeItem2);


    }

    public void testInsertReadCourt(){

        FullpayDbHelper dbHelper = new FullpayDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues courtValues = TestDb.createCourt("1","1 civil",null);


        Uri courtInsertUri = mContext.getContentResolver()
                .insert(CourtEntry.CONTENT_URI,courtValues);

        assertTrue(courtInsertUri != null);

        long courtRowId = ContentUris.parseId(courtInsertUri);
        assertTrue(courtRowId != -1);

        Cursor courtCursor = mContext.getContentResolver().query(
                CourtEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(courtCursor,courtValues);
        courtCursor.close();

    }

    public void testReadCourtWithMultipleRecords(){
        FullpayDbHelper dbHelper = new FullpayDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues court4 = TestDb.createCourt("4","4 civil",null);
        ContentValues court2 = TestDb.createCourt("2","2 civil",null);
        ContentValues court3 = TestDb.createCourt("3","3 civil",null);


        Uri courtInsertUri = mContext.getContentResolver()
                .insert(CourtEntry.CONTENT_URI,court4);

        courtInsertUri = mContext.getContentResolver()
                .insert(CourtEntry.CONTENT_URI,court2);
        courtInsertUri = mContext.getContentResolver()
                .insert(CourtEntry.CONTENT_URI,court3);

        assertTrue(courtInsertUri != null);


        //QueryById
        Cursor courtCursor = mContext.getContentResolver().query(
                CourtEntry.buildCourtUri(court2.getAsLong(CourtEntry._ID)),
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(courtCursor,court2);
        courtCursor.close();

        //QueryByName
        courtCursor = mContext.getContentResolver().query(
                CourtEntry.buildCourtWithName(court3.getAsString(CourtEntry.COLUMN_NAME)),
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(courtCursor,court3);
        courtCursor.close();

    }

    public void testInsertReadStage(){
        FullpayDbHelper dbHelper = new FullpayDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues stageValues = TestDb.createStage("1","etapa 1");


        Uri stageInsertUri = mContext.getContentResolver()
                .insert(StageEntry.CONTENT_URI,stageValues);

        assertTrue(stageInsertUri != null);

        long stageRowId = ContentUris.parseId(stageInsertUri);
        assertTrue(stageRowId != -1);

        Cursor stageCursor = mContext.getContentResolver().query(
                StageEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(stageCursor,stageValues);
        stageCursor.close();
    }

    public void testInsertReadAttorney(){
        FullpayDbHelper dbHelper = new FullpayDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues attorneyValues = TestDb.createAttorney("mleve", "password");


        Uri attorneyInsertUri = mContext.getContentResolver()
                .insert(AttorneyEntry.CONTENT_URI, attorneyValues);

        assertTrue(attorneyInsertUri != null);

        long attorneyRowId = ContentUris.parseId(attorneyInsertUri);
        assertTrue(attorneyRowId != -1);

        Cursor attorneyCursor = mContext.getContentResolver().query(
                AttorneyEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(attorneyCursor,attorneyValues);
        attorneyCursor.close();
    }

    public void testInsertReadCause(){
        FullpayDbHelper dbHelper = new FullpayDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues causeValues = TestDb.createCause(
                "10212", "2014", "Mario Estefano", "Leverone", "ah si si", "20141009", "C", "1",
                "1", "1"
        );

        Uri causeInserturi = mContext.getContentResolver()
                .insert(CauseEntry.CONTENT_URI,causeValues);

        assertTrue(causeInserturi != null);

        long causeRowId = ContentUris.parseId(causeInserturi);
        assertTrue(causeRowId != -1);

        Cursor causeCursor = mContext.getContentResolver().query(
                CauseEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestDb.validateCursor(causeCursor,causeValues);
        causeCursor.close();
    }
}
