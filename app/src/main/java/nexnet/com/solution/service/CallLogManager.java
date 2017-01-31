package nexnet.com.solution.service;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import nexnet.com.solution.database.AppDB;
import nexnet.com.solution.database.DBCallLog;
import nexnet.com.solution.database.DBCallLogTable;
import com.m800.msme.api.M800Call;
import com.m800.sdk.M800SDK;
import com.m800.sdk.rate.IM800RateManager;
import nexnet.com.solution.service.DateUtil;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class CallLogManager {

    private static final String TAG = CallLogManager.class.getSimpleName();

    private static final String PUSH_CALL_BUNDLE_CALL_ID = "callId";
    private static final String PUSH_CALL_BUNDLE_CALLER = "caller";
    private static final String PUSH_CALL_BUNDLE_TYPE = "type";
    private static final String PUSH_CALL_BUNDLE_TIME = "com.maaii.notification.delay";

    public static final String BROADCAST_ACTION_NEW_CALL_LOG = CallLogManager.class.getSimpleName() + ".NewCallLog";

    private volatile static CallLogManager _instance = null;

    private final LinkedBlockingDeque<DBCallLog> mQueue = new LinkedBlockingDeque<>();
    private Future<?> mProcessFuture = null;
    private final Runnable mUpdateTask = new UpdateTask();
    private static ExecutorService mExecutorService = null;

    public static CallLogManager getInstance() {
        if (_instance == null) {
            _instance = new CallLogManager();
        }
        return _instance;
    }

    public void saveCallLog(M800Call call) {
        String currentUserJID = M800SDK.getInstance().getUserJID();
        if (!TextUtils.isEmpty(currentUserJID) && call != null) {
            M800Call.M800CallDirection callDirection = call.direction();
            M800Call.M800CallType callType = call.callType();
            String remoteCarrier = call.remoteCarrier();
            String remoteUserId;
            if (TextUtils.isEmpty(remoteCarrier)) {
                remoteUserId = call.remotePhoneNumber();
            } else {
                remoteUserId = call.remotePhoneNumber() + "@" + remoteCarrier;
            }

            String pushCallId = call.getPushCallId();
            String callId;
            if (!TextUtils.isEmpty(pushCallId)) {
                callId = pushCallId;
            } else {
                callId = call.callID();
            }

            DBCallLog dbCallLog = new DBCallLog();
            dbCallLog.setCallId(callId);
            dbCallLog.setCallDirection(callDirection);
            dbCallLog.setCallType(callType);
            dbCallLog.setCallStartTime(call.startTime().getTime());
            dbCallLog.setCallEstablishedTime(call.establishedTime().getTime());
            dbCallLog.setCallEndTime(call.endTime().getTime());
            dbCallLog.setResultCode((long) call.statusCode());

            if (callDirection == M800Call.M800CallDirection.Outgoing) {
                dbCallLog.setCaller(currentUserJID);
                dbCallLog.setCallee(remoteUserId);
            } else {
                dbCallLog.setCaller(remoteUserId);
                dbCallLog.setCallee(currentUserJID);
            }

            saveCallLog(dbCallLog);
        }
    }

    public void saveMissCallLog(Bundle missCallData) {
        String currentUserJID = M800SDK.getInstance().getUserJID();
        if (!TextUtils.isEmpty(currentUserJID) && missCallData != null) {
            String notificationType = null;
            if (missCallData.containsKey(PUSH_CALL_BUNDLE_TYPE)) {
                notificationType = missCallData.getString(PUSH_CALL_BUNDLE_TYPE);
            }

            if ("com.maaii.notification.missed.call".equals(notificationType)) {
                M800Call.M800CallDirection callDirection = M800Call.M800CallDirection.Incoming;
                String callId = missCallData.getString(PUSH_CALL_BUNDLE_CALL_ID, "");
                String caller = missCallData.getString(PUSH_CALL_BUNDLE_CALLER, "");

                if (!TextUtils.isEmpty(callId) && !TextUtils.isEmpty(caller)) {
                    M800Call.M800CallType callType;
                    if (caller.contains("@")) {
                        callType = M800Call.M800CallType.Onnet;
                    } else {
                        callType = M800Call.M800CallType.Offnet;
                    }

                    long endTime = DateUtil.fromISO8601Date(missCallData.getString(PUSH_CALL_BUNDLE_TIME), System.currentTimeMillis());

                    DBCallLog dbCallLog = new DBCallLog();
                    dbCallLog.setCallId(callId);
                    dbCallLog.setCallDirection(callDirection);
                    dbCallLog.setCallType(callType);
                    dbCallLog.setCallEndTime(endTime);
                    dbCallLog.setCaller(caller);
                    dbCallLog.setCallee(currentUserJID);

                    saveCallLog(dbCallLog);
                }
            }
        }
    }

    private void saveCallLog(DBCallLog dbCallLog) {
        if (dbCallLog != null) {
            if (!mQueue.contains(dbCallLog)) {
                mQueue.offerLast(dbCallLog);
            }

            if (mExecutorService == null) {
                mExecutorService = Executors.newSingleThreadExecutor();
            }

            if (mProcessFuture == null || mProcessFuture.isDone()) {
                mProcessFuture = mExecutorService.submit(mUpdateTask);
            }
        }
    }

    private class UpdateTask implements Runnable {
        @Override
        public void run() {
            do {
                DBCallLog dbCallLog;
                try {
                    dbCallLog = mQueue.pollFirst(5, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.toString(), e);
                    dbCallLog = null;
                }

                if (dbCallLog != null) {
                    String callId = dbCallLog.getCallId();
                    String caller = dbCallLog.getCaller();
                    String callee = dbCallLog.getCallee();

                    M800Call.M800CallDirection callDirection = dbCallLog.getCallDirection();
                    M800Call.M800CallType callType = dbCallLog.getCallType();

                    if (
                            callDirection != null &&
                            callType != null &&
                            !TextUtils.isEmpty(callId) &&
                            !TextUtils.isEmpty(caller) &&
                            !TextUtils.isEmpty(callee) &&
                            dbCallLog.getCallEndTime() != null &&
                            !isDuplicatedCallIdInToday(callId)
                    ) {
                        long endTime = dbCallLog.getCallEndTime();
                        long establishedTime = dbCallLog.getCallEstablishedTime((long) 0);
                        float cost = 0;
                        long duration = 0;

                        if (establishedTime > 0 && endTime > establishedTime) {
                            duration = endTime - establishedTime;
                        }

                        if (callDirection == M800Call.M800CallDirection.Outgoing && callType == M800Call.M800CallType.Offnet && duration > 0) {
                            long durationInSec = (long) Math.ceil(duration / 1000d);
                            IM800RateManager rateManager = M800SDK.getInstance().getRateManager();
                            if (rateManager != null) {
                                cost = rateManager.getCostForCall(callee, durationInSec);
                            }
                        }

                        dbCallLog.setDuration(duration);
                        dbCallLog.setCallCost(Double.parseDouble(Float.toString(cost)));
                        dbCallLog.save();

                        Context context = AppDB.getContext();
                        if (context != null) {
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BROADCAST_ACTION_NEW_CALL_LOG));
                        }
                    }
                } else {
                    break;
                }
            } while (mQueue.size() > 0);


            mProcessFuture = null;
        }
    }

    //region database query
    private boolean isDuplicatedCallIdInToday(String callId) {
        boolean result = false;

        if (!TextUtils.isEmpty(callId)) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            long beginOfToday = calendar.getTimeInMillis();

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);

            long endOfToday = calendar.getTimeInMillis();

            String sql = "SELECT " + DBCallLogTable.COLUMN_ID + " FROM " + DBCallLogTable.TABLE_NAME +
                    " WHERE " + DBCallLogTable.COLUMN_CALL_ID + " =?" +
                    " AND " + DBCallLogTable.COLUMN_CALL_END_TIME + " BETWEEN ? AND ?";

            String[] selectionArgs = new String[]{
                    callId,
                    String.valueOf(beginOfToday),
                    String.valueOf(endOfToday)
            };

            Cursor cursor = AppDB.rawQuery(sql, selectionArgs);
            if (cursor != null) {
                result = (cursor.getCount() > 0);
                cursor.close();
            }
        }

        return result;
    }
    //endregion
}
