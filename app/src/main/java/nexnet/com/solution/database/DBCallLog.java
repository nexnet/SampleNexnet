package nexnet.com.solution.database;

import android.os.Parcel;
import android.os.Parcelable;

import com.m800.msme.api.M800Call;

public class DBCallLog extends DBObject {

    public DBCallLog() {
        super();
    }

    public DBCallLog(Parcel in) {
        super(in);
    }

    public void setCallId(String callId) {
        setStringValue(DBCallLogTable.COLUMN_CALL_ID, callId);
    };

    public String getCallId() {
        return data().getString(DBCallLogTable.COLUMN_CALL_ID);
    }

    public void setCallDirection(M800Call.M800CallDirection callDirection) {
        String value;
        if (callDirection != null) {
            value = callDirection.name();
        } else {
            value = null;
        }
        setStringValue(DBCallLogTable.COLUMN_CALL_DIRECTION, value);
    };

    public M800Call.M800CallDirection getCallDirection() {
        String value = data().getString(DBCallLogTable.COLUMN_CALL_DIRECTION);
        M800Call.M800CallDirection callDirection;
        try {
            callDirection = M800Call.M800CallDirection.valueOf(value);
        } catch (Exception e) {
            callDirection = null;
        }
        return callDirection;
    }

    public void setCallType(M800Call.M800CallType callType) {
        String value;
        if (callType != null) {
            value = callType.name();
        } else {
            value = null;
        }
        setStringValue(DBCallLogTable.COLUMN_CALL_TYPE, value);
    }

    public M800Call.M800CallType getCallType() {
        String value = data().getString(DBCallLogTable.COLUMN_CALL_TYPE);
        M800Call.M800CallType callType;
        try {
            callType = M800Call.M800CallType.valueOf(value);
        } catch (Exception e) {
            callType = null;
        }
        return callType;
    }

    public void setCallCost(Double cost) {
        setDoubleValue(DBCallLogTable.COLUMN_CALL_COST, cost);
    }

    public Double getCallCost() {
        return getCallCost(null);
    }

    public Double getCallCost(Double defaultValue) {
        return getDoubleValue(DBCallLogTable.COLUMN_CALL_COST, defaultValue);
    }

    public void setCaller(String caller) {
        setStringValue(DBCallLogTable.COLUMN_CALLER, caller);
    };

    public String getCaller() {
        return data().getString(DBCallLogTable.COLUMN_CALLER);
    }

    public void setCallee(String callee) {
        setStringValue(DBCallLogTable.COLUMN_CALLEE, callee);
    };

    public String getCallee() {
        return data().getString(DBCallLogTable.COLUMN_CALLEE);
    }

    public void setCallStartTime(Long time) {
        setLongValue(DBCallLogTable.COLUMN_CALL_START_TIME, time);
    };

    public Long getCallStartTime() {
        return getCallStartTime(null);
    }

    public Long getCallStartTime(Long defaultValue) {
        return getLongValue(DBCallLogTable.COLUMN_CALL_START_TIME, defaultValue);
    }

    public void setCallEstablishedTime(long time) {
        setLongValue(DBCallLogTable.COLUMN_CALL_ESTABLISHED_TIME, time);
    };

    public Long getCallEstablishedTime() {
        return getCallEstablishedTime(null);
    }

    public Long getCallEstablishedTime(Long defaultValue) {
        return getLongValue(DBCallLogTable.COLUMN_CALL_ESTABLISHED_TIME, defaultValue);
    }

    public void setCallEndTime(long time) {
        setLongValue(DBCallLogTable.COLUMN_CALL_END_TIME, time);
    };

    public Long getCallEndTime() {
        return getCallEndTime(null);
    }

    public Long getCallEndTime(Long defaultValue) {
        return getLongValue(DBCallLogTable.COLUMN_CALL_END_TIME, defaultValue);
    }

    public void setDuration(long time) {
        setLongValue(DBCallLogTable.COLUMN_CALL_DURATION, time);
    };

    public Long getDuration() {
        return getDuration(null);
    }

    public Long getDuration(Long defaultValue) {
        return getLongValue(DBCallLogTable.COLUMN_CALL_DURATION, defaultValue);
    }

    public void setResultCode(Long code) {
        setLongValue(DBCallLogTable.COLUMN_CALL_RESULT_CODE, code);
    };

    public Long getResultCode() {
        return getResultCode(null);
    }

    public Long getResultCode(Long defaultValue) {
        return getLongValue(DBCallLogTable.COLUMN_CALL_RESULT_CODE, defaultValue);
    }

    @Override
    public String getTableName() {
        return DBCallLogTable.TABLE_NAME;
    }

    @Override
    public String[] getColumns() {
        return DBCallLogTable.COLUMNS;
    }

    @Override
    public DataType[] getDataTypes() {
        return DBCallLogTable.DATA_TYPES;
    }

    public static final Parcelable.Creator<DBCallLog> CREATOR = new Parcelable.Creator<DBCallLog>() {
        public DBCallLog createFromParcel(Parcel in) {
            return new DBCallLog(in);
        }

        public DBCallLog[] newArray(int size) {
            return new DBCallLog[size];
        }
    };
}
