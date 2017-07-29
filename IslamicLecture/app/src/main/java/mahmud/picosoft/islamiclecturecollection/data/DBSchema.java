package mahmud.picosoft.islamiclecturecollection.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mahmud Basunia on 7/24/2017.
 */

public class DBSchema extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "islamic_lectures.db";

    public DBSchema(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    //Tables
    public static final String TABLE_LECTURES = "lectures";

    private static final String COMMA_SPACE = ", ";
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String PRIMARY_KEY = "PRIMARY KEY ";
    private static final String UNIQUE = "UNIQUE ";
    private static final String TYPE_TEXT = " TEXT ";
    private static final String TYPE_DATE = " DATETIME ";
    private static final String TYPE_INT = " INTEGER ";
    private static final String DEFAULT = "DEFAULT ";
    private static final String AUTOINCREMENT = "AUTOINCREMENT ";
    private static final String NOT_NULL = "NOT NULL ";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";

    public static final class TB_LECTURES {
        public static final String ID = "_id";
        public static final String LECTURE = "lecture";
        public static final String ARTIST = "date";
        public static final String LINK = "date";
        public static final String IS_FAVORITE = "date";
        public static final String IS_DOWNLOAD = "date";
        public static final String DATE = "date";

    }

    private static final String CREATE_TABLE_LECTURES =
            CREATE_TABLE + TABLE_LECTURES + " ( " +
                    TB_LECTURES.ID + TYPE_INT + NOT_NULL + PRIMARY_KEY + AUTOINCREMENT + COMMA_SPACE +
                    TB_LECTURES.LECTURE + TYPE_TEXT + NOT_NULL + COMMA_SPACE +
                    TB_LECTURES.ARTIST + TYPE_TEXT + NOT_NULL + COMMA_SPACE +
                    TB_LECTURES.LINK + TYPE_TEXT + NOT_NULL + COMMA_SPACE +
                    TB_LECTURES.IS_FAVORITE + TYPE_INT + NOT_NULL + COMMA_SPACE +
                    TB_LECTURES.IS_DOWNLOAD + TYPE_INT + NOT_NULL + COMMA_SPACE +
                    TB_LECTURES.DATE + TYPE_TEXT + NOT_NULL +
                    ")";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LECTURES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATE_TABLE_LECTURES);
    }
}