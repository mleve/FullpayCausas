package cl.fullpay.causas.adapters;

import android.content.ContentValues;
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


import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    public void bindView(View view, final Context context, final Cursor cursor) {

        final TextView rolView = (TextView)view.findViewById(R.id.cause_rol);
        TextView rutView = (TextView)view.findViewById(R.id.cause_rut);
        TextView namesView = (TextView)view.findViewById(R.id.cause_name);
        TextView warrantView = (TextView)view.findViewById(R.id.cause_exhorto);
        final TextView lastChangeView = (TextView)view.findViewById(R.id.cause_last_change);
        final EditText commentView= (EditText)view.findViewById(R.id.cause_comment);

        Spinner stageSpinner = (Spinner)view.findViewById(R.id.cause_stage);



        final long id = cursor.getInt(
                cursor.getColumnIndex(CauseEntry._ID)
        );

        final String rolNum= cursor.getString(
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

        final String comment= cursor.getString(
                cursor.getColumnIndex(CauseEntry.COLUMN_COMMENT)
        );

        final int stage = cursor.getInt(
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
                    String newComment = commentView.getText().toString();
                    if(!newComment.equals(comment)){
                        saveComment(newComment, id, rolNum,context);
                        updateDate(lastChangeView,id);
                    }
                }
            }
        });

        stageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long spinnerElemId) {
                //Log.d(LOG_TAG,"spinner, i = "+i+" id= "+id+" , stage= "+stage);
                if(stage != spinnerElemId) {
                    saveStage(context, id, spinnerElemId);
                    updateDate(lastChangeView,id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




    }



    private void updateDate(TextView lastChangeView, long causeId) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String newDate = formatter.format(Calendar.getInstance().getTime());
        lastChangeView.setText(newDate);
    }

    private void saveComment(String newComment, long id, String rolNum, Context context) {


        //TODO guardar commentario nuevo en el log
        Log.d(LOG_TAG,"cambio la causa "+id+" ,rol: "+rolNum+" ,se comento: "+newComment);
        ContentValues data = new ContentValues();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String newDate = formatter.format(Calendar.getInstance().getTime());
        data.put(CauseEntry.COLUMN_COMMENT,newComment);
        data.put(CauseEntry.COLUMN_CHANGE_DATE,newDate);
        data.put(CauseEntry.COLUMN_HAS_CHANGED,"1");
        context.getContentResolver().update(
                CauseEntry.CONTENT_URI,
                data,
                CauseEntry._ID+"= "+id,
                null
        );

    }

    private void saveStage(Context context, long id, long newStageId) {
        Log.d(LOG_TAG,"cambio la causa "+id+" , a nueva etapa: "+newStageId);
        ContentValues data = new ContentValues();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String newDate = formatter.format(Calendar.getInstance().getTime());
        data.put(CauseEntry.COLUMN_STAGE_KEY,newStageId);
        data.put(CauseEntry.COLUMN_CHANGE_DATE,newDate);
        data.put(CauseEntry.COLUMN_HAS_CHANGED,"1");
        context.getContentResolver().update(
                CauseEntry.CONTENT_URI,
                data,
                CauseEntry._ID+"= "+id,
                null
        );

    }



}
