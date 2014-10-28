package cl.fullpay.causas.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mario on 12-10-14.
 */
public class FullpayContract {

    public static final String CONTENT_AUTHORITY = "cl.fullpay.causas";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_COURT = "court";
    public static final String PATH_STAGE = "stage";
    public static final String PATH_ATTORNEY = "attorney";
    public static final String PATH_CAUSE = "cause";

    public static class CauseEntry implements BaseColumns {

     //Definicion de la tabla

        public static final String TABLE_NAME = "cause";

        public static final String COLUMN_ROL_NUM = "rol_num";

        public static final String COLUMN_ROL_DATE = "rol_date";

        public static final String COLUMN_RUT = "rut";

        public static final String COLUMN_NAMES = "names";

        public static final String COLUMN_LAST_NAME = "last_name";

        public static final String COLUMN_COMMENT = "comment";

        public static final String COLUMN_WARRANT = "warrant";

        public static final String COLUMN_CHANGE_DATE = "change_date";

        //Llaves foraneas

        public static final String COLUMN_COURT_KEY = "court_id";
        public static final String COLUMN_STAGE_KEY = "stage_id";
        public static final String COLUMN_ATTORNEY_KEY = "attorney_id";

        //ContentProvider cosas
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CAUSE).build();

        public static final String CONTENT_TYPE =
                String.format("vnd.android.cursor.dir/%s/%s",
                        CONTENT_AUTHORITY,
                        PATH_CAUSE);

        public static final String CONTENT_ITEM_TYPE =
                String.format("vnd.android.cursor.item/%s/%s",
                        CONTENT_AUTHORITY,
                        PATH_CAUSE);

        public static Uri buildCauseUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

    }

    public static class CourtEntry implements BaseColumns {

        //Definicion de la tabla

        public static final String TABLE_NAME = "court";

        public static final String COLUMN_NAME = "name";


        //Llaves foraneas

        public static final String COLUMN_PARENT_COURT_KEY = "parent_court_id";


        //ContentProvider cosas
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COURT).build();

        public static final String CONTENT_TYPE =
                String.format("vnd.android.cursor.dir/%s/%s",
                        CONTENT_AUTHORITY,
                        PATH_COURT);

        public static final String CONTENT_ITEM_TYPE =
                String.format("vnd.android.cursor.item/%s/%s",
                        CONTENT_AUTHORITY,
                        PATH_COURT);

        public static Uri buildCourtUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildCourtWithName(String name){
            return CONTENT_URI.buildUpon().appendPath(name).build();
        }


        public static String getNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class StageEntry implements BaseColumns {

        //Definicion de la tabla

        public static final String TABLE_NAME = "stage";

        public static final String COLUMN_NAME = "name";

        //ContentProvider cosas
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STAGE).build();

        public static final String CONTENT_TYPE =
                String.format("vnd.android.cursor.dir/%s/%s",
                        CONTENT_AUTHORITY,
                        PATH_STAGE);

        public static final String CONTENT_ITEM_TYPE =
                String.format("vnd.android.cursor.item/%s/%s",
                        CONTENT_AUTHORITY,
                        PATH_STAGE);

        public static Uri buildStageUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildStageWithName(String name){
            return CONTENT_URI.buildUpon().appendPath(name).build();
        }


        public static String getNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class AttorneyEntry implements BaseColumns {

        //Definicion de la tabla

        public static final String TABLE_NAME = "attorney";

        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_TOKEN = "session_token";

        //ContentProvider cosas
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ATTORNEY).build();

        public static final String CONTENT_TYPE =
                String.format("vnd.android.cursor.dir/%s/%s",
                        CONTENT_AUTHORITY,
                        PATH_ATTORNEY);

        public static final String CONTENT_ITEM_TYPE =
                String.format("vnd.android.cursor.item/%s/%s",
                        CONTENT_AUTHORITY,
                        PATH_ATTORNEY);

        public static Uri buildAttorneyUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildAttorneyWithName(String name){
            return CONTENT_URI.buildUpon().appendPath(name).build();
        }

        public static String getUsernameFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }


    }



}
