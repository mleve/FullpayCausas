package cl.fullpay.causas.data;

import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import cl.fullpay.causas.AsyncTasks.BaseTask;

import cl.fullpay.causas.AsyncTasks.CausesTask;
import cl.fullpay.causas.AsyncTasks.CourtsTask;
import cl.fullpay.causas.AsyncTasks.StagesTask;
import cl.fullpay.causas.data.FullpayContract.*;

/**
 * Created by mario on 28-10-14.
 */
public class TestApi extends AndroidTestCase {
    public static final String LOG_TAG = TestApi.class.getSimpleName();

    private static String baseApiUrl  =  "http://dev.empchile.net/forseti/index.php/admin/api";

    private String token = "UHllWXRUcnB4MkZHZGp5UEFMclBhZEpm";

    public void testDeleteDb(){
        mContext.deleteDatabase(FullpayDbHelper.DATABASE_NAME);
    }



    public void testLogin(){


        final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("username", "javi"));
        nameValuePairs.add(new BasicNameValuePair("password", "javiera"));
        nameValuePairs.add(new BasicNameValuePair("token", token));

        final CountDownLatch signal = new CountDownLatch(1);
        BaseTask task = new BaseTask(mContext){

            @Override
            protected Boolean doInBackground(Void... voids) {
                return logInUser(nameValuePairs);
            }

            @Override
            public void onPostExecute(Boolean s){
                super.onPostExecute(s);

                //Se recibio session_token?
                assertTrue(s);


                //Se guardo el nuevo registro en ls BD?
                Cursor attorneyCursor = mContext.getContentResolver().query(
                        FullpayContract.AttorneyEntry.buildAttorneyWithName("javi"),
                        null,
                        null,
                        null,
                        null
                );

                if(attorneyCursor.moveToFirst()){
                    String username = attorneyCursor.getString(attorneyCursor.getColumnIndex(
                            AttorneyEntry.COLUMN_USERNAME));
                    String password = attorneyCursor.getString(attorneyCursor.getColumnIndex(
                            AttorneyEntry.COLUMN_PASSWORD));
                    String token = attorneyCursor.getString(attorneyCursor.getColumnIndex(
                            AttorneyEntry.COLUMN_TOKEN));

                    assertEquals("javi",username);
                    assertEquals("javiera",password);
                    assertTrue(token != null);
                }
                else
                    fail("no se creo nada en la BD :(");

                //TestDb.validateCursor(attorneyCursor,attorney);
                attorneyCursor.close();

                signal.countDown();

            }
        };

        task.execute();

        try {
            signal.await();
        }
        catch (Exception e){

        }


    }


    public void testCourts(){


        final CountDownLatch signal = new CountDownLatch(1);

        CourtsTask task = new CourtsTask(getContext()){
            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                //Se recibio respuesta?
                assertTrue(result);


                //Verificar creacion

                Cursor courtCursor = mContext.getContentResolver().query(
                        CourtEntry.CONTENT_URI,
                        null,
                        CourtEntry._ID+"= ?",
                        new String[]{"43"},
                        null
                );

                if(courtCursor.moveToFirst()){
                    assertEquals("4 San Miguel",
                            courtCursor.getString(
                                    courtCursor.getColumnIndex(CourtEntry.COLUMN_NAME)
                            )
                    );
                }
                else{
                    fail("No se insertaron bien las cortes");
                }

                courtCursor.close();


                signal.countDown();
            }
        };

        task.execute();

        try {
            signal.await();
        }
        catch (Exception e){

        }

    }


    public void testStages(){

        final CountDownLatch signal = new CountDownLatch(1);

        StagesTask task = new StagesTask(getContext()){
            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                //Se recibio respuesta?
                assertTrue(result);


                //Verificar creacion

                Cursor stagesCursor = mContext.getContentResolver().query(
                        StageEntry.CONTENT_URI,
                        null,
                        StageEntry._ID+"= ?",
                        new String[]{"63"},
                        null
                );

                if(stagesCursor.moveToFirst()){
                    assertEquals("Apela",
                            stagesCursor.getString(
                                    stagesCursor.getColumnIndex(StageEntry.COLUMN_NAME)
                            )
                    );
                }
                else{
                    fail("No se insertaron bien las etapas");
                }

                stagesCursor.close();


                signal.countDown();
            }
        };

        task.execute();

        try {
            signal.await();
        }
        catch (Exception e){

        }

    }

    public void testCauses(){
        final CountDownLatch signal = new CountDownLatch(1);

        CausesTask task = new CausesTask(getContext()){
            @Override
            protected Boolean doInBackground(Void... voids) {

                final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", "javi"));
                nameValuePairs.add(new BasicNameValuePair("password", "javiera"));
                nameValuePairs.add(new BasicNameValuePair("token", token));

                assertTrue(logInUser(nameValuePairs));


                String responseStr = httpGetRequest(baseUrl+"/getEtapas",null);
                Log.d(LOG_TAG,"respuesta de Etapas: "+responseStr);

                int responseCode = getResponseCode(responseStr);

                if (responseCode != 0)
                    return false;

                assertTrue(createStages(responseStr));

                responseStr = httpGetRequest(baseUrl+"/getTribunales",null);
                Log.d(LOG_TAG,"respuesta de Tribunales: "+responseStr);

                responseCode = getResponseCode(responseStr);

                if (responseCode != 0)
                    return false;

                assertTrue(createCourts(responseStr));

                Cursor attorneyCursor = mContext.getContentResolver().query(
                        FullpayContract.AttorneyEntry.CONTENT_URI,
                        null,
                        FullpayContract.AttorneyEntry.COLUMN_IS_ACTIVE+"= ?",
                        new String[]{"1"},
                        null
                );

                String token;
                if(attorneyCursor.moveToFirst()){
                    token = attorneyCursor.getString(
                            attorneyCursor.getColumnIndex(
                                    FullpayContract.AttorneyEntry.COLUMN_TOKEN
                            )
                    );
                }
                else{
                    return false;
                }

                responseStr = httpGetRequest(baseUrl+"/getCuentaEtapasProcurador/"+token,null);
                Log.d(LOG_TAG, "respuesta de causas: " + responseStr);

                responseCode = getResponseCode(responseStr);

                if (responseCode != 0)
                    return false;

                return createCauses(responseStr);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                //Se recibio respuesta?
                assertTrue(result);


                //Verificar creacion

                Cursor causesCursor = mContext.getContentResolver().query(
                        CauseEntry.CONTENT_URI,
                        null,
                        CauseEntry.COLUMN_CAUSE_ID+"= ?",
                        new String[]{"56"},
                        null
                );

                if(causesCursor.moveToFirst()){
                    assertEquals("16285832-7",
                            causesCursor.getString(
                                    causesCursor.getColumnIndex(CauseEntry.COLUMN_RUT)
                            )
                    );
                }
                else{
                    fail("No se insertaron bien las causas");
                }

                causesCursor.close();


                signal.countDown();
            }
        };

        task.execute();

        try {
            signal.await();
        }
        catch (Exception e){

        }
    }


}
