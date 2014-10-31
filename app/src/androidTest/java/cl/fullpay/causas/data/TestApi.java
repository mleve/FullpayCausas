package cl.fullpay.causas.data;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.test.AndroidTestCase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import cl.fullpay.causas.CausesCreator;
import cl.fullpay.causas.HttpTasks.HttpGetTask;
import cl.fullpay.causas.HttpTasks.LoginTask;

import cl.fullpay.causas.Login;
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


    /*
    public void testLogin(){


        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("username", "javi"));
        nameValuePairs.add(new BasicNameValuePair("password", "javiera"));
        nameValuePairs.add(new BasicNameValuePair("token", token));

        final CountDownLatch signal = new CountDownLatch(1);
        LoginTask task = new LoginTask(nameValuePairs,
                baseApiUrl,
                mContext){
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
    */



    public void testStages(){

        final CausesCreator creator = new CausesCreator(mContext);

        final CountDownLatch signal = new CountDownLatch(1);

        HttpGetTask task = new HttpGetTask(null,
                baseApiUrl+"/getEtapas"){
            @Override
            public void onPostExecute(String s){
                super.onPostExecute(s);

                //Se recibio respuesta?
                assertTrue(s != null);

                Boolean result = creator.createStages(s);

                //Se crearon las stages?
                assertTrue(result);

                //Verificar creacion

                Cursor stageCursor = mContext.getContentResolver().query(
                        StageEntry.CONTENT_URI,
                        null,
                        StageEntry._ID+"= ?",
                        new String[]{"78"},
                        null
                );

                if(stageCursor.moveToFirst()){
                    assertEquals("Notificacion Martillero",
                            stageCursor.getString(
                                    stageCursor.getColumnIndex(StageEntry.COLUMN_NAME)
                            )
                    );
                }
                else{
                    fail("No se insertaron bien las etapas");
                }


                stageCursor.close();
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

        final CausesCreator creator = new CausesCreator(mContext);

        final CountDownLatch signal = new CountDownLatch(1);

        HttpGetTask task = new HttpGetTask(null,
                baseApiUrl+"/getTribunales"){
            @Override
            public void onPostExecute(String s){
                super.onPostExecute(s);

                //Se recibio respuesta?
                assertTrue(s != null);

                Boolean result = creator.createCourts(s);

                //Se crearon las stages?
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

    public void testCauses(){

        final CausesCreator creator = new CausesCreator(mContext);


        ContentValues attorney =TestDb.createAttorney("javi","javiera","aa37ffd189403f587ede71234c3b50a4");

        Uri uri = mContext.getContentResolver().insert(
                AttorneyEntry.CONTENT_URI,
                attorney
        );

        assertTrue(uri != null);


        final CountDownLatch signal = new CountDownLatch(1);



        //chequear que exista un usuario logeado o llevarlo a login
        Cursor attorneyCursor = mContext.getContentResolver().query(
                FullpayContract.AttorneyEntry.CONTENT_URI,
                null,
                FullpayContract.AttorneyEntry.COLUMN_IS_ACTIVE+" = ?",
                new String[]{"1"},
                null
        );
        if(!attorneyCursor.moveToFirst()){
            //no hay usuario activo
           fail("no hay abogado registrado");
        }

        //Insertar cortes

        HttpGetTask task = new HttpGetTask(null,
                baseApiUrl+"/getTribunales"){
            @Override
            public void onPostExecute(String s){
                super.onPostExecute(s);

                //Se recibio respuesta?
                assertTrue(s != null);

                Boolean result = creator.createCourts(s);

                //Se crearon las stages?
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





        String token = attorneyCursor.getString(
                attorneyCursor.getColumnIndex(AttorneyEntry.COLUMN_TOKEN));


        final CountDownLatch signal2 = new CountDownLatch(1);
        HttpGetTask causeTask = new HttpGetTask(null,
                baseApiUrl+"/getCausas/"+token){
            @Override
            public void onPostExecute(String s){
                super.onPostExecute(s);

                //Se recibio respuesta?
                assertTrue(s != null);

                Boolean result = creator.createCauses(s);

                //Se crearon las causas?
                assertTrue(result);

                //Verificar creacion

                Cursor causeCursor = mContext.getContentResolver().query(
                        CauseEntry.CONTENT_URI,
                        null,
                        CauseEntry.COLUMN_CAUSE_ID+"= ?",
                        new String[]{"94"},
                        null
                );

                if(causeCursor.moveToFirst()){
                    assertEquals("15987309-9",
                            causeCursor.getString(
                                    causeCursor.getColumnIndex(CauseEntry.COLUMN_RUT)
                            )
                    );
                }
                else{
                    fail("No se insertaron bien las Causas");
                }


                causeCursor.close();
                signal2.countDown();

            }
        };

        causeTask.execute();

        try {
            signal2.await();
        }
        catch (Exception e){

        }

    }

}
