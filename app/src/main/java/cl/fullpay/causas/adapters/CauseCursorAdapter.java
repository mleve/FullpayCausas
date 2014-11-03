package cl.fullpay.causas.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;


import cl.fullpay.causas.R;
import cl.fullpay.causas.data.FullpayContract;
import cl.fullpay.causas.data.FullpayContract.CauseEntry;
/**
 * Created by mario on 02-11-14.
 */
public class CauseCursorAdapter  extends CursorAdapter{
    private LayoutInflater cursorInflater;

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

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView rolView = (TextView)view.findViewById(R.id.cause_rol);
        TextView rutView = (TextView)view.findViewById(R.id.cause_rut);
        TextView namesView = (TextView)view.findViewById(R.id.cause_name);
        TextView warrantView = (TextView)view.findViewById(R.id.cause_exhorto);
        TextView lastChangeView = (TextView)view.findViewById(R.id.cause_last_change);
        EditText commentView= (EditText)view.findViewById(R.id.cause_comment);


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



        rolView.setText(rolNum+" - "+rolDate);
        rutView.setText(rut);
        namesView.setText(last_name+", "+names);
        warrantView.setText(warrant);
        lastChangeView.setText(last_change);
        commentView.setText(comment);



    }
}
