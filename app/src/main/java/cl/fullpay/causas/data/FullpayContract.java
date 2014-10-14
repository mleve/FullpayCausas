package cl.fullpay.causas.data;

import android.provider.BaseColumns;

/**
 * Created by mario on 12-10-14.
 */
public class FullpayContract {

    public static final String CONTENT_AUTHORITY = "cl.fullpay.causas";


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

    }

    public static class CourtEntry implements BaseColumns {

        //Definicion de la tabla

        public static final String TABLE_NAME = "court";

        public static final String COLUMN_NAME = "name";


        //Llaves foraneas

        public static final String COLUMN_PARENT_COURT_KEY = "parent_court_id";

    }

    public static class StageEntry implements BaseColumns {

        //Definicion de la tabla

        public static final String TABLE_NAME = "stage";

        public static final String COLUMN_NAME = "name";
    }

    public static class AttorneyEntry implements BaseColumns {

        //Definicion de la tabla

        public static final String TABLE_NAME = "attorney";

        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";

    }



}
