package cl.fullpay.causas;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import cl.fullpay.causas.HttpTasks.HttpGetTask;
import cl.fullpay.causas.data.FullpayContract;

/**
 * Created by mario on 30-10-14.
 */
public class Main extends Activity {

    private String baseApiUrl  =  "http://dev.empchile.net/forseti/index.php/admin/api";
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //chequear que exista un usuario logeado o llevarlo a login
        Cursor attorneyCursor = getContentResolver().query(
                FullpayContract.AttorneyEntry.CONTENT_URI,
                null,
                FullpayContract.AttorneyEntry.COLUMN_IS_ACTIVE+" = ?",
                new String[]{"1"},
                null
        );
        if(!attorneyCursor.moveToFirst()){
            //no hay usuario activo
            startActivity(new Intent(getApplicationContext(),Login.class));
        }
        else{
            token = attorneyCursor.getString(
                    attorneyCursor.getColumnIndex(FullpayContract.AttorneyEntry.COLUMN_TOKEN)
            );

            //TODO chequear que existan causas o ejecutar primera carga

            setContentView(R.layout.first_init);

            final CausesCreator creator = new CausesCreator(getApplicationContext());

            HttpGetTask task = new HttpGetTask(null,
                    baseApiUrl+"/getEtapas"){
                @Override
                public void onPostExecute(String s){
                    super.onPostExecute(s);
                    Boolean result = creator.createStages(s);
                    createCourts(creator);


                }
            };
            task.execute();
        }


    }

    public void createCourts(final CausesCreator creator){

        HttpGetTask task = new HttpGetTask(null,
                baseApiUrl+"/getTribunales"){
            @Override
            public void onPostExecute(String s){
                Boolean result = creator.createCourts(s);
                createCauses(creator);
            }

        };

        task.execute();

    }

    private void createCauses(final CausesCreator creator) {
        HttpGetTask task = new HttpGetTask(null,
                baseApiUrl+"/getCausas/"+token){
            @Override
            public void onPostExecute(String s){
                Boolean result = creator.createCauses(s);
                startApp();
            }

        };

        task.execute();

    }

    private void startApp() {
        startActivity(new Intent(getApplicationContext(),Init.class));
    }
}
