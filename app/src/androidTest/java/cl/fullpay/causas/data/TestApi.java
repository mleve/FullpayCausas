package cl.fullpay.causas.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Handler;
import android.test.AndroidTestCase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import cl.fullpay.causas.HttpTasks.HttpGetTask;
import cl.fullpay.causas.HttpTasks.LoginTask;

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

    public void testGetCourts(){



        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("username", "javi"));
        nameValuePairs.add(new BasicNameValuePair("password", "javiera"));
        nameValuePairs.add(new BasicNameValuePair("token", token));

        final CountDownLatch signal = new CountDownLatch(1);

        HttpGetTask task = new HttpGetTask(null,
                baseApiUrl+"/getTribunales"){
            @Override
            public void onPostExecute(String s){
                super.onPostExecute(s);

                //Se recibio session_token?
                assertTrue(s!=null);

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
