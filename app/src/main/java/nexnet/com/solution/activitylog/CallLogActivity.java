package nexnet.com.solution.activitylog;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.m800.msme.api.M800Call;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import nexnet.com.solution.R;
import nexnet.com.solution.call.DialNumberActivity;
import nexnet.com.solution.database.AppDB;
import nexnet.com.solution.database.DBCallLog;
import nexnet.com.solution.database.DBCallLogTable;


public class CallLogActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String DEBUG_TAG=CallLogActivity.class.getSimpleName();
    private View view;
    private FloatingActionButton dialButton;
    private SwipeRefreshLayout mSwipeLayout;
    private LoadCallLogTask mLoadCallLogTask = null;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> CallList;
    private ListView listViewCallLog;
    public CallLogActivity() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        loadCallLogs();
        // setContentView(R.layout.activity_contact);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.activity_call_log, container,false);
        dialButton=(FloatingActionButton) view.findViewById(R.id.dialButton);
        CallList = new ArrayList<String>();
        CallList.add(" ");

        arrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.contact_item,R.id.Itemname,CallList);

        listViewCallLog=(ListView)view.findViewById(R.id.listViewCallLog);
        listViewCallLog.setAdapter(arrayAdapter);

        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeLayout.setOnRefreshListener(this);

        dialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent( view.getContext(), DialNumberActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
        return view;
    }


    @Override
    public void onRefresh() {
        loadCallLogs();
        Log.d(DEBUG_TAG,"OnRefresh");
    }

    public void loadCallLogs() {
        if (mLoadCallLogTask == null || mLoadCallLogTask.isDone()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadCallLogTask = new LoadCallLogTask();
                    mLoadCallLogTask.execute();
                }
            });
        }
    }

    private class LoadCallLogTask extends AsyncTask<Void, Void, List<CallLogGroup>> {
        private boolean mDone = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (mSwipeLayout != null) {
                mSwipeLayout.setRefreshing(true);
            }
        }

        @Override
        protected List<CallLogGroup> doInBackground(Void... params) {
            Hashtable<String, CallLogGroup> callLogGroupHashtable = new Hashtable<>();

            String sql = "SELECT * FROM " + DBCallLogTable.TABLE_NAME + " ORDER BY " + DBCallLogTable.COLUMN_CALL_END_TIME + " DESC";
            Log.d(DEBUG_TAG, "SQL: " + sql);
            Cursor cursor = AppDB.rawQuery(sql, new String[0]);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    DBCallLog item = new DBCallLog();
                    item.load(cursor);

                    M800Call.M800CallDirection callDirection = item.getCallDirection();
                    if (callDirection != null) {
                        String remoteUserId;
                        if (callDirection == M800Call.M800CallDirection.Outgoing) {
                            remoteUserId = item.getCallee();
                        } else {
                            remoteUserId = item.getCaller();
                        }

                        if (!TextUtils.isEmpty(remoteUserId)) {
                            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                            calendar.setTimeInMillis(item.getCallEndTime());
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);

                            String key = remoteUserId + "_" + calendar.getTimeInMillis();
                            CallLogGroup callLogGroup;
                            if (!callLogGroupHashtable.containsKey(key)) {
                                callLogGroup = new CallLogGroup(remoteUserId);
                                callLogGroupHashtable.put(key, callLogGroup);
                            } else {
                                callLogGroup = callLogGroupHashtable.get(key);
                            }
                            callLogGroup.addChildItem(item);
                        }
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }

            List<CallLogGroup> updatedData = new ArrayList<>();
            Log.d(DEBUG_TAG, "SQL: " + updatedData.size());
            if (callLogGroupHashtable.size() > 0) {
                for (String key : callLogGroupHashtable.keySet()) {
                    CallLogGroup callLogGroup = callLogGroupHashtable.get(key);
                    updatedData.add(callLogGroup);
                }
            }

            Collections.sort(updatedData, new Comparator<CallLogGroup>() {
                @Override
                public int compare(CallLogGroup lhs, CallLogGroup rhs) {
                    long lTime = lhs.getLastCallEndTime();
                    long rTime = rhs.getLastCallEndTime();

                    if (lTime > rTime) {
                        return -1;
                    } else if (lTime < rTime) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });

            return updatedData;
        }

        @Override
        protected void onPostExecute(List<CallLogGroup> updatedData) {
            CallList.clear();
            String caller="";
            for(int i=0; i<updatedData.size(); i++){

                CallLogGroup callLogGroup = updatedData.get(i);
                for(int ii=0; ii<callLogGroup.items.size(); ii++) {
                    Log.d(DEBUG_TAG, "callee:" + callLogGroup.getChildItem(ii).getCallee());
                    Log.d(DEBUG_TAG, "caller:" + callLogGroup.getChildItem(ii).getCaller());
                    Log.d(DEBUG_TAG, "call id:" + callLogGroup.getChildItem(ii).getCallId());
                    Log.d(DEBUG_TAG, "time and date:" + getDate(callLogGroup.getChildItem(ii).getCallEndTime()));
                    caller=callLogGroup.getChildItem(ii).getCaller() + " - Last " + getDate(callLogGroup.getChildItem(ii).getCallEndTime());
                    CallList.add(caller);
                }

            }
           /* updatedData.get(0);
            for (CallLogGroup mydata : updatedData) {
                Log.d(DEBUG_TAG,"Contacts Count:" +  mydata.get(0).getCallee());
                CallList.add(mydata.getChildItem(0).getCallee());
            }*/
            arrayAdapter.notifyDataSetChanged();

            if (mSwipeLayout != null) {
                mSwipeLayout.setRefreshing(false);
            }

            mDone = true;
        }

        public boolean isDone() {
            return mDone;
        }
    }

    private String getDate(long time) {
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        Log.d(DEBUG_TAG, "time and date now:" +  c.getTime());
        Calendar cal2 = Calendar.getInstance(Locale.ENGLISH);
        cal2.setTimeInMillis(time);
        Log.d(DEBUG_TAG, "time and date call:" +  cal2.getTime());
        long diffInMillisec = c.getTimeInMillis() - cal2.getTimeInMillis();
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMillisec);
        long seconds = diffInSec % 60;
        diffInSec/= 60;
        long  minutes =diffInSec % 60;
        diffInSec /= 60;
        long  hours = diffInSec % 24;
        diffInSec /= 24;
        long  days = diffInSec;

        Log.d(DEBUG_TAG, " seconds: " + seconds + " minutes: " + minutes + " hours: " + hours + " days: " + days);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM", cal).toString();
        String toreturn= "";
        if(days>1){
            toreturn="Last " + DateFormat.format("dd-MM", cal).toString();
        }else if(days==1){
            toreturn="Yesterday " + DateFormat.format("h:m", cal).toString();

        }else{
            toreturn=hours + " hours ago";

        }
        return date;
    }


    private class CallLogGroup {

        private String mRemoteUserId;
        private List<DBCallLog> items;
        private long lastCallEndTime = 0;
        private int numOfIncomingCall = 0;
        private int numOfOutgoingCall = 0;

        public CallLogGroup(String remoteUserId) {
            mRemoteUserId = remoteUserId;
            items = new ArrayList<>();
        }

        public void addChildItem(DBCallLog dbCallLog) {
            if (dbCallLog != null && dbCallLog.getID() > 0) {
                M800Call.M800CallDirection callDirection = dbCallLog.getCallDirection();
                M800Call.M800CallType callType = dbCallLog.getCallType();
                if (callDirection != null && callType != null) {
                    lastCallEndTime = Math.max(lastCallEndTime, dbCallLog.getCallEndTime());

                    switch (callDirection) {
                        case Incoming: {
                            numOfIncomingCall++;
                            break;
                        }
                        case Outgoing: {
                            numOfOutgoingCall++;
                            break;
                        }
                        default: {
                            break;
                        }
                    }

                    items.add(dbCallLog);
                }
            }
        }

        public long getLastCallEndTime() {
            return lastCallEndTime;
        }

        public String getRemoteUserId() {
            return mRemoteUserId;
        }

        public int getNumOfIncomingCall() {
            return numOfIncomingCall;
        }

        public int getNumOfOutgoingCall() {
            return numOfOutgoingCall;
        }

        public DBCallLog getChildItem(int index) {
            return items.get(index);
        }

        public int getChildCount() {
            return items.size();
        }
    }

    @Override
    public void onResume() {
        Log.e(DEBUG_TAG, "onResume of ContactFragment");
        loadCallLogs();
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e(DEBUG_TAG, "OnPause of ContactFragment");
        super.onPause();
    }
}