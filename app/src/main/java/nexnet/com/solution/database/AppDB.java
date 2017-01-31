package nexnet.com.solution.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppDB extends SQLiteOpenHelper{

	private static final String TAG = AppDB.class.getSimpleName();

	public static final String DATABASE_NAME = "my_app.db";

	public static final int DATABASE_VERSION = 1;

	private static AppDB INSTANCE = null;

	private final Context mContext;

	enum DataBaseType {
		READABLE,
		WRITABLE
	}

	enum DefinedColumn {
		COLUMN_ID("_id"),
		COLUMN_JID("JID"),
		COLUMN_ROOM_ID("ROOM_ID"),
		COLUMN_MESSAGE_ID("MESSAGE_ID")
		;

		private String columnName;

		DefinedColumn(String columnName) {
			this.columnName = columnName;
		}

		public String getColumnName() {
			return columnName;
		}
	}

	protected AppDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	public synchronized static void initiate(Context context) {
		if (null == INSTANCE) {
			INSTANCE = new AppDB(context);
		}
	}

	public synchronized static void reInitiate(Context context) {
		if ( INSTANCE != null ) {
			try {
				INSTANCE.close();
			}catch (Exception e){
				//ignore
			}
		}
		INSTANCE = new AppDB(context);
	}

	public static boolean isInitiated() {
		return null != INSTANCE;
	}

	public static synchronized SQLiteDatabase getDatabase(DataBaseType type) {
		if (null == INSTANCE) {
			Log.e(TAG, "DATABASE NOT OPENED");
			throw new NullPointerException("Database is null");
		}

		SQLiteDatabase database;

		try {
			if (type == DataBaseType.WRITABLE) {
				database = INSTANCE.getWritableDatabase();
			} else {
				database = INSTANCE.getReadableDatabase();
			}
		} catch (SQLiteException e) {
			INSTANCE.close();
			if (type == DataBaseType.WRITABLE) {
				database = INSTANCE.getWritableDatabase();
			} else {
				database = INSTANCE.getReadableDatabase();
			}
		}

		return database;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//Added at version 1
		DBCallLogTable.createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "opUpgrade oldVersion:" + oldVersion + " to " + newVersion);
		if (oldVersion < newVersion) {
			switch (oldVersion) {
				case 1:
					break;
				default:
					// If version not support, re-create all tables
					deleteDB(mContext);
					onCreate(db);
					break;
			}
		}
	}

	public static void deleteDB(Context context) {
		try {
            if (INSTANCE != null) {
                INSTANCE.close();
            }

            context.deleteDatabase(DATABASE_NAME);

            INSTANCE = new AppDB(context);
		} catch (Exception e) {
			Log.e(TAG, "error on deleting DB", e);
		}
	}

	public static Context getContext() {
		return INSTANCE == null ? null : INSTANCE.mContext;
	}

	public static Cursor rawQuery(String sql, String[] selectionArgs) {
		Cursor cursor = null;

		SQLiteDatabase readableDatabase = AppDB.getDatabase(DataBaseType.READABLE);
		if (readableDatabase != null) {
			try {
				cursor = readableDatabase.rawQuery(sql, selectionArgs);
			} catch (Exception e) {
				Log.e(TAG, "error on deleting DB", e);
			}
		}

		return cursor;
	}
}
