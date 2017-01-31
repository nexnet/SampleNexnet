package nexnet.com.solution.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;

public abstract class DBObject implements Parcelable {

    enum DataType {
        TEXT,
        INTEGER,
        REAL,
        BLOB
    }

    private final static String TAG = DBCallLog.class.getSimpleName();

    private Bundle mData;

    public DBObject() {
        mData = new Bundle();
    }

    public DBObject(Parcel in) {
        mData = in.readBundle(getClass().getClassLoader());
    }

    public Bundle data() {
        if (mData == null) {
            mData = new Bundle();
        }
        return mData;
    }

    public void load(Cursor cursor) {
        Bundle data = new Bundle();
        for (int i=0; i<cursor.getColumnCount(); i++) {
            String columnName = cursor.getColumnName(i);
            DataType dataType = getDataType(columnName);

            if (dataType != null) {
                switch (dataType) {
                    case TEXT: {
                        String value = cursor.getString(i);
                        if (value != null) {
                            data.putString(columnName, value);
                        }
                        break;
                    }
                    case INTEGER: {
                        data.putLong(columnName, cursor.getLong(i));
                        break;
                    }
                    case REAL: {
                        data.putDouble(columnName, cursor.getDouble(i));
                        break;
                    }
                    case BLOB: {
                        data.putByteArray(columnName, cursor.getBlob(i));
                        break;
                    }
                    default: {
                        String value = cursor.getString(i);
                        if (value != null) {
                            data.putString(columnName, value);
                        }
                        break;
                    }
                }
            } else {
                String value = cursor.getString(i);
                if (value != null) {
                    data.putString(columnName, value);
                }
            }
        }
        load(data);
    }

    public void load(Bundle data) {
        if (data != null) {
            mData = data;
        } else {
            mData = new Bundle();
        }
    }

    public Long getID() {
        return getLongValue(AppDB.DefinedColumn.COLUMN_ID.getColumnName(), (long) -1);
    }

    private void setID(Long id) {
        setLongValue(AppDB.DefinedColumn.COLUMN_ID.getColumnName(), id);
    }

    protected void setStringValue(String key, String value) {
        if (!TextUtils.isEmpty(key)) {
            if (value != null) {
                data().putString(key, value);
            } else {
                data().remove(key);
            }
        }
    }

    protected String getStringValue(String key, String defaultValue) {
        String value = defaultValue;

        if (!TextUtils.isEmpty(key) && data().containsKey(key)) {
            DataType dataType = getDataType(key);
            if (dataType != null) {
                switch (dataType) {
                    case TEXT: {
                        value = data().getString(key, defaultValue);
                        break;
                    }
                    case INTEGER: {
                        value = String.valueOf(data().getLong(key));
                        break;
                    }
                    case REAL: {
                        value = String.valueOf(data().getDouble(key));
                        break;
                    }
                    case BLOB: {
                        byte[] bytes = data().getByteArray(key);
                        if (bytes != null) {
                            value = Arrays.toString(bytes);
                        }
                        break;
                    }
                    default: {
                        value = data().getString(key, defaultValue);
                        break;
                    }
                }
            } else {
                value = data().getString(key, defaultValue);
            }
        }

        return value;
    }

    protected void setDoubleValue(String key, Double value) {
        if (!TextUtils.isEmpty(key)) {
            if (value != null) {
                data().putDouble(key, value);
            } else {
                data().remove(key);
            }
        }
    }

    protected Double getDoubleValue(String key, Double defaultValue) {
        Double value = defaultValue;
        if (!TextUtils.isEmpty(key) && data().containsKey(key)) {
            value = data().getDouble(key);
        }
        return value;
    }

    protected void setLongValue(String key, Long value) {
        if (!TextUtils.isEmpty(key)) {
            if (value != null) {
                data().putLong(key, value);
            } else {
                data().remove(key);
            }
        }
    }

    protected Long getLongValue(String key, Long defaultValue) {
        Long value = defaultValue;
        if (!TextUtils.isEmpty(key) && data().containsKey(key)) {
            value = data().getLong(key);
        }
        return value;
    }

    public abstract String getTableName();

    public abstract String[] getColumns();

    public abstract DataType[] getDataTypes();

    public DataType getDataType(String columnName) {
        int index = Arrays.asList(getColumns()).indexOf(columnName);
        return getDataType(index);
    }

    public DataType getDataType(int columnIndex) {
        DataType dataType = null;
        DataType[] dataTypes = getDataTypes();
        if (columnIndex >= 0 && columnIndex < dataTypes.length) {
            dataType = dataTypes[columnIndex];
        }
        return dataType;
    }

    public SQLiteStatement buildInsertStatement(SQLiteDatabase writableDatabase) {
        SQLiteStatement insertStatement = null;

        String tableName = getTableName();
        String[] columns = getColumns();
        if (writableDatabase != null && !TextUtils.isEmpty(tableName) && columns != null && columns.length > 1) {
            String sql = "INSERT INTO " + tableName + "(";
            for (int i=1; i<columns.length; i++) {
                if (i > 1) {
                    sql += ",";
                }
                sql += "'" + columns[i] + "'";
            }
            sql += ") VALUES (";
            for (int i=1; i<columns.length; i++) {
                if (i > 1) {
                    sql += ",";
                }
                sql += "?";
            }
            sql += ")";

            insertStatement = writableDatabase.compileStatement(sql);
        }

        return insertStatement;
    }

    public SQLiteStatement buildUpdateStatement(SQLiteDatabase writableDatabase) {
        SQLiteStatement updateStatement = null;

        String tableName = getTableName();
        String[] columns = getColumns();
        if (writableDatabase != null && !TextUtils.isEmpty(tableName) && columns != null && columns.length > 1) {
            String sql = "UPDATE " + tableName + " SET ";
            for (int i=1; i<columns.length; i++) {
                if (i > 1) {
                    sql += ",";
                }
                sql += "'" + columns[i] + "'=?";
            }
            sql += " WHERE " + columns[0] + "=?";

            updateStatement = writableDatabase.compileStatement(sql);
        }

        return updateStatement;
    }

    public SQLiteStatement buildDeleteStatement(SQLiteDatabase writableDatabase) {
        SQLiteStatement deleteStatement = null;

        String tableName = getTableName();
        String[] columns = getColumns();
        if (writableDatabase != null && !TextUtils.isEmpty(tableName) && columns != null && columns.length > 0) {
            String sql = "DELETE FROM " + tableName + " WHERE " + columns[0] + "=?";

            deleteStatement = writableDatabase.compileStatement(sql);
        }

        return deleteStatement;
    }

    private void bindData(SQLiteStatement statement, int bindIndex, String columnName, DataType dataType) {
        if (statement != null) {
            if (!TextUtils.isEmpty(columnName) && dataType != null && data().containsKey(columnName)) {
                switch (dataType) {
                    case TEXT: {
                        statement.bindString(bindIndex, data().getString(columnName, ""));
                        break;
                    }
                    case INTEGER: {
                        statement.bindLong(bindIndex, data().getLong(columnName, 0));
                        break;
                    }
                    case REAL: {
                        statement.bindDouble(bindIndex, data().getDouble(columnName, 0));
                        break;
                    }
                    case BLOB: {
                        statement.bindBlob(bindIndex, data().getByteArray(columnName));
                        break;
                    }
                    default: {
                        statement.bindNull(bindIndex);
                        break;
                    }
                }
            } else {
                statement.bindNull(bindIndex);
            }
        }
    }

    public boolean bindDataForInsert(SQLiteStatement insertStatement) {
        boolean result;

        String[] columns = getColumns();
        DataType[] dataTypes = getDataTypes();
        if (insertStatement != null && columns != null && columns.length > 1) {
            try {
                for (int i=1; i<columns.length; i++) {
                    bindData(insertStatement, i, columns[i], dataTypes[i]);
                }

                long id = insertStatement.executeInsert();
                setID(id);
                result = true;
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
                result = false;
            }

            insertStatement.clearBindings();
        } else {
            result = false;
        }

        return result;
    }

    public boolean bindDataForUpdate(SQLiteStatement updateStatement) {
        boolean result;

        String[] columns = getColumns();
        DataType[] dataTypes = getDataTypes();
        if (updateStatement != null && columns != null && columns.length > 1) {
            try {
                int bindIndex = 0;
                for (int i=1; i<columns.length; i++) {
                    bindIndex = i;
                    bindData(updateStatement, bindIndex, columns[i], dataTypes[i]);
                }
                bindData(updateStatement, bindIndex + 1, columns[0], dataTypes[0]);

                updateStatement.executeUpdateDelete();
                result = true;
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
                result = false;
            }

            updateStatement.clearBindings();
        } else {
            result = false;
        }

        return result;
    }

    public boolean bindDataForDelete(SQLiteStatement deleteStatement) {
        boolean result;

        String[] columns = getColumns();
        DataType[] dataTypes = getDataTypes();
        if (deleteStatement != null && columns != null && columns.length > 1) {
            try {
                bindData(deleteStatement, 1, columns[0], dataTypes[0]);

                deleteStatement.executeUpdateDelete();
                setID(null);
                result = true;
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
                result = false;
            }

            deleteStatement.clearBindings();
        } else {
            result = false;
        }

        return result;
    }

    public boolean save() {
        boolean result;

        SQLiteDatabase writableDatabase = AppDB.getDatabase(AppDB.DataBaseType.WRITABLE);
        if (writableDatabase != null) {
            writableDatabase.beginTransaction();

            try {
                if (getID() > 0) {
                    result = bindDataForUpdate(buildUpdateStatement(writableDatabase));
                } else {
                    result = bindDataForInsert(buildInsertStatement(writableDatabase));
                }

                if (result) {
                    writableDatabase.setTransactionSuccessful();
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
                result = false;
            }

            writableDatabase.endTransaction();
        } else {
            result = false;
        }

        return result;
    }

    public boolean delete() {
        boolean result;

        if (getID() > 0) {
            SQLiteDatabase writableDatabase = AppDB.getDatabase(AppDB.DataBaseType.WRITABLE);
            if (writableDatabase != null) {
                writableDatabase.beginTransaction();

                try {
                    result = bindDataForDelete(buildDeleteStatement(writableDatabase));

                    if (result) {
                        writableDatabase.setTransactionSuccessful();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString(), e);
                    result = false;
                }

                writableDatabase.endTransaction();
            } else {
                result = false;
            }
        } else {
            result = true;
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Bundle data = data();
        for (String key : data.keySet()) {
            sb.append("Key:").append(key).append(" Value:").append(getStringValue(key, "")).append("\n");
        }

        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(data());
    }
}
