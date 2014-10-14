package cl.fullpay.causas.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import cl.fullpay.causas.data.FullpayContract.*;

/**
 * Created by mario on 12-10-14.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();
    private FullpayDbHelper dbHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dbHelper = new FullpayDbHelper(mContext);

    }

    public void testCreateDb()throws Throwable{
        mContext.deleteDatabase(FullpayDbHelper.DATABASE_NAME);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assertTrue(db.isOpen());

    }

    public void testInsertCourt(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        ContentValues values = createCourt("1","1 civil de santiago",null);
        long courtRowId = db.insert(CourtEntry.TABLE_NAME,null,values);

        assertTrue(courtRowId != -1);

        Cursor cursor = db.query(
                CourtEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        validateCursor(cursor,values);

    }

    public void testInsertStage(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String testId = "1";
        String testName = "Carga en el sistema";
        ContentValues values = new ContentValues();
        values.put(StageEntry._ID,testId);
        values.put(StageEntry.COLUMN_NAME,testName);

        long courtRowId = db.insert(StageEntry.TABLE_NAME,null,values);

        assertTrue(courtRowId != -1);

        Cursor cursor = db.query(
                StageEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        if(cursor.moveToFirst()){
            assertEquals(testId,
                    cursor.getString(cursor.getColumnIndex(StageEntry._ID)));
            assertEquals(testName,
                    cursor.getString(cursor.getColumnIndex(StageEntry.COLUMN_NAME)));
        }
        else{
            fail("No data were returned");
        }

    }


    private ContentValues createCourt(String id, String name, String parentId){
        ContentValues values = new ContentValues();
        values.put(CourtEntry._ID,id);
        values.put(CourtEntry.COLUMN_NAME,name);
        if(parentId != null)
            values.put(CourtEntry.COLUMN_PARENT_COURT_KEY,parentId);

        return values;

    }
    private ContentValues createStage(String id, String name){
        ContentValues values = new ContentValues();
        values.put(StageEntry._ID,id);
        values.put(StageEntry.COLUMN_NAME,name);

        return values;

    }

    private ContentValues createCause(String rolNum,String rolDate, String names,
                                      String lastName, String comment, String changeDate,
                                      String warrant, String courtKey, String stageKey,
                                      String attorneyKey){
        ContentValues values = new ContentValues();
        values.put(CauseEntry.COLUMN_ROL_NUM, rolNum);
        values.put(CauseEntry.COLUMN_ROL_DATE, rolDate);
        values.put(CauseEntry.COLUMN_NAMES, names);
        values.put(CauseEntry.COLUMN_LAST_NAME, lastName );
        values.put(CauseEntry.COLUMN_COMMENT,comment );
        values.put(CauseEntry.COLUMN_CHANGE_DATE, changeDate );
        values.put(CauseEntry.COLUMN_WARRANT, warrant);
        values.put(CauseEntry.COLUMN_COURT_KEY, courtKey);
        values.put(CauseEntry.COLUMN_STAGE_KEY, stageKey);
        values.put(CauseEntry.COLUMN_ATTORNEY_KEY, attorneyKey);

        return values;

    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

    public void testInsertWarrant(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String username = "mleve";
        String password = "pass";
        ContentValues attorney = new ContentValues();
        attorney.put(AttorneyEntry.COLUMN_USERNAME, username);
        attorney.put(AttorneyEntry.COLUMN_PASSWORD, password);

        long attorneyRowId = db.insert(AttorneyEntry.TABLE_NAME,null,attorney);

        assertTrue(attorneyRowId != -1);

        Cursor cursor = db.query(
                AttorneyEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        validateCursor(cursor,attorney);
    }

    public void testInsertCause(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues courtValues = createCourt("2","1 civil","0");
        long courtRowId = db.insert(CourtEntry.TABLE_NAME,null,courtValues);
        assertTrue(courtRowId != -1);

        ContentValues stageValues = createStage("2","Demanda ingresada a tribunal");
        long stageRowId = db.insert(StageEntry.TABLE_NAME,null,stageValues);
        assertTrue(stageRowId != -1);

        ContentValues causeValues = createCause(
                "10212","2014","Mario Estefano","Leverone","ah si si", "20141009","C","2",
                "2","2"
        );

        long causeRowId = db.insert(CauseEntry.TABLE_NAME,null,causeValues);
        assertTrue(causeRowId!=-1);

        String [] cols = {
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
        };

        Cursor cursor = db.query(
                CauseEntry.TABLE_NAME,
                cols,
                null,
                null,
                null,
                null,
                null,
                null);

        validateCursor(cursor,causeValues);

    }
}
