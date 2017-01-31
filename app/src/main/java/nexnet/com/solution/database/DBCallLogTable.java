package nexnet.com.solution.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBCallLogTable {

    private static final String TAG = DBCallLogTable.class.getSimpleName();
    public static final String TABLE_NAME = "CallLog";

    public static final String COLUMN_ID = AppDB.DefinedColumn.COLUMN_ID.getColumnName();
    public static final String COLUMN_CALL_ID = "CALL_ID";
    public static final String COLUMN_CALL_DIRECTION = "CALL_DIRECTION";
    public static final String COLUMN_CALL_TYPE = "CALL_TYPE";
    public static final String COLUMN_CALL_COST = "CALL_COST";
    public static final String COLUMN_CALLER = "CALLER";
    public static final String COLUMN_CALLEE = "CALLEE";
    public static final String COLUMN_CALL_START_TIME = "CALL_START_TIME";
    public static final String COLUMN_CALL_ESTABLISHED_TIME = "CALL_ESTABLISHED_TIME";
    public static final String COLUMN_CALL_END_TIME = "CALL_END_TIME";
    public static final String COLUMN_CALL_DURATION = "CALL_DURATION";
    public static final String COLUMN_CALL_RESULT_CODE = "CALL_RESULT_CODE";

    public static final String[] COLUMNS = new String[]{
            COLUMN_ID,
            COLUMN_CALL_ID,
            COLUMN_CALL_DIRECTION,
            COLUMN_CALL_TYPE,
            COLUMN_CALL_COST,
            COLUMN_CALLER,
            COLUMN_CALLEE,
            COLUMN_CALL_START_TIME,
            COLUMN_CALL_ESTABLISHED_TIME,
            COLUMN_CALL_END_TIME,
            COLUMN_CALL_DURATION,
            COLUMN_CALL_RESULT_CODE
    };

    public static final DBObject.DataType[] DATA_TYPES = new DBObject.DataType[]{
            DBObject.DataType.INTEGER,
            DBObject.DataType.TEXT,
            DBObject.DataType.TEXT,
            DBObject.DataType.TEXT,
            DBObject.DataType.REAL,
            DBObject.DataType.TEXT,
            DBObject.DataType.TEXT,
            DBObject.DataType.INTEGER,
            DBObject.DataType.INTEGER,
            DBObject.DataType.INTEGER,
            DBObject.DataType.INTEGER,
            DBObject.DataType.INTEGER
    };

    protected static void createTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " +
                TABLE_NAME +
                " ("
                + COLUMN_ID 		            + " " + DATA_TYPES[0] + " PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CALL_ID 			    + " " + DATA_TYPES[1] + ","
                + COLUMN_CALL_DIRECTION 		+ " " + DATA_TYPES[2] + ","
                + COLUMN_CALL_TYPE 			    + " " + DATA_TYPES[3] + ","
                + COLUMN_CALL_COST 			    + " " + DATA_TYPES[4] + ","
                + COLUMN_CALLER        			+ " " + DATA_TYPES[5] + ","
                + COLUMN_CALLEE      			+ " " + DATA_TYPES[6] + ","
                + COLUMN_CALL_START_TIME 		+ " " + DATA_TYPES[7] + ","
                + COLUMN_CALL_ESTABLISHED_TIME 	+ " " + DATA_TYPES[8] + ","
                + COLUMN_CALL_END_TIME 			+ " " + DATA_TYPES[9] + ","
                + COLUMN_CALL_DURATION 			+ " " + DATA_TYPES[10] + ","
                + COLUMN_CALL_RESULT_CODE		+ " " + DATA_TYPES[11] + ""
                + ");";

        try {
            db.execSQL(sql);
        } catch (Exception e) {
            Log.e(TAG, "Error on createTable", e);
        }
    }

    protected static void dropTable(SQLiteDatabase db) {
        String sql = "DROP TABLE " + TABLE_NAME;

        try {
            db.execSQL(sql);
        } catch (Exception e) {
            Log.e(TAG, "Error on dropTable", e);
        }
    }

    // TODO
}
