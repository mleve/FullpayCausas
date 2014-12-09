package cl.fullpay.causas.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;


import cl.fullpay.causas.R;
import cl.fullpay.causas.data.FullpayContract;
import cl.fullpay.causas.data.FullpayContract.CauseEntry;
/**
 * Created by mario on 02-11-14.
 */
public class CauseCursorAdapter  extends CursorAdapter{
    private LayoutInflater cursorInflater;
    private static final String LOG_TAG = CauseCursorAdapter.class.getSimpleName();
    private SimpleCursorAdapter stageSpinnerAdapter;

    public CauseCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );


    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return cursorInflater.inflate(R.layout.cause_list_item,viewGroup,false);

    }

    private void initStageSpinner(Context context){
        if(stageSpinnerAdapter == null){
            Cursor stageCursor = context.getContentResolver().query(
                    FullpayContract.StageEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    FullpayContract.StageEntry._ID+" ASC"
            );

            stageSpinnerAdapter = new SimpleCursorAdapter(
                    context,
                    android.R.layout.simple_spinner_item,
                    stageCursor,
                    new String[]{FullpayContract.StageEntry.COLUMN_NAME},
                    new int[]{android.R.id.text1,
                            0}
            );

            Log.d(LOG_TAG,"creo un SpinnerAdapter :P");
        }
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        TextView rolView = (TextView)view.findViewById(R.id.cause_rol);
        TextView rutView = (TextView)view.findViewById(R.id.cause_rut);
        TextView namesView = (TextView)view.findViewById(R.id.cause_name);
        TextView warrantView = (TextView)view.findViewById(R.id.cause_exhorto);
        TextView lastChangeView = (TextView)view.findViewById(R.id.cause_last_change);
        EditText commentView= (EditText)view.findViewById(R.id.cause_comment);

        Spinner stageSpinner = (Spinner)view.findViewById(R.id.cause_stage);



        String rolNum= cursor.getString(
                cursor.getColumnIndex(CauseEntry.COLUMN_ROL_NUM)
        );

        String rolDate= cursor.getString(
                cursor.getColumnIndex(CauseEntry.COLUMN_ROL_DATE)
        );

        String rut= cursor.getString(
                cursor.getColumnIndex(CauseEntry.COLUMN_RUT)
        );

        String names= cursor.getString(
                cursor.getColumnIndex(CauseEntry.COLUMN_NAMES)
        );

        String last_name= cursor.getString(
                cursor.getColumnIndex(CauseEntry.COLUMN_LAST_NAME)
        );

        String warrant = cursor.getString(
                cursor.getColumnIndex(CauseEntry.COLUMN_WARRANT)
        );
        warrant = (warrant.equals("1")) ? "C" : "S" ;

        String last_change= cursor.getString(
                cursor.getColumnIndex(CauseEntry.COLUMN_CHANGE_DATE)
        );

        String comment= cursor.getString(
                cursor.getColumnIndex(CauseEntry.COLUMN_COMMENT)
        );

        int stage = cursor.getInt(
                cursor.getColumnIndex(CauseEntry.COLUMN_STAGE_KEY)
        );





        rolView.setText(rolNum+" - "+rolDate);
        rutView.setText(rut);
        namesView.setText(last_name+", "+names);
        warrantView.setText(warrant);
        lastChangeView.setText(last_change);
        commentView.setText(comment);

        //Setear adapter de etapas en Spinner, indicando etapa en la que esta la causa
        initStageSpinner(context);
        stageSpinner.setAdapter(stageSpinnerAdapter);
        stageSpinner.setSelection(stage-1);

        commentView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    String comment = ((EditText) view).getText().toString();
                    saveComment(cursor,comment);
                }
            }
        });

        stageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
                //Log.d(LOG_TAG,"hola, estoy escuchando los cambios de lspinner");
                saveStage(cursor,id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




    }

    private void saveStage(Cursor cursor, long id) {
        //TODO ver que pasa con las causas con etapa=null, se estan cambiando a etapa=1

        //TODO guardar cambio de etapa la Bd
        int stageId = cursor.getInt(
                cursor.getColumnIndex(CauseEntry.COLUMN_STAGE_KEY)
        );

        String causeId = cursor.getString(
                cursor.getColumnIndex(CauseEntry._ID)
        );

        String rol= cursor.getString(
                cursor.getColumnIndex(CauseEntry.COLUMN_ROL_NUM)
        );

        if(stageId != id){
            Log.d(LOG_TAG,"la etapa de la causa de rol "+rol+" cambio a "+id);
        }
    }

    private void saveComment(Cursor cursor, String newComment) {
        String id= cursor.getString(
                cursor.getColumnIndex(CauseEntry._ID)
        );

        String oldComment = cursor.getString(
                cursor.getColumnIndex(CauseEntry.COLUMN_COMMENT)
        );

        if(!newComment.equals(oldComment)){
            //TODO guardar commentario nuevo en el log
            Log.d(LOG_TAG,"cambio la causa "+id+" ,se comento: "+newComment);

        }

    }

    //TODO guardar cambio de fecha al cambiar etapa


}
