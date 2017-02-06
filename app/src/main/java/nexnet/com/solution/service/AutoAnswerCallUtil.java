package nexnet.com.solution.service;

import android.util.Log;

import com.google.common.collect.Maps;
import com.m800.msme.api.M800Call;

import java.util.Date;
import java.util.Map;

/**
 * Created by justinliu on 27/11/14.
 */
public class AutoAnswerCallUtil {

    private final static String TAG = "AutoAnswerCallUtil";

    private static AutoAnswerCallUtil _instance = null;
    public static AutoAnswerCallUtil getInstance() {

        if (null == _instance){
            _instance = new AutoAnswerCallUtil();
        }
        return _instance;
    }


    private Date mStartTime;
    private Date mEndTime;

    private int mCountNotificationReceived;
    private int mCountCallAnswering;
    private int mCountCallAnswered;
    private int mCountCallEndedWithoutError;
    private int mCountCallEndedWithError;

    private boolean mStarted;

    private final static String KeyNotificationReceivedTime = "NotificationReceivedTime";
    private final static String KeyCallAnsweringTime = "CallAnsweringTime";
    private final static String KeyCallAnsweredTime = "CallAnsweredTime";
    private final static String KeyCallEndedTime = "CallEndedTime";

    private Map<String, Map<String, Long>> mCallStats = Maps.newHashMap();

    private AutoAnswerCallUtil() {

    }

    public void start() {
        mCountNotificationReceived = 0;
        mCountCallAnswered = 0;
        mCountCallAnswered = 0;
        mCountCallEndedWithoutError = 0;
        mCountCallEndedWithError = 0;

        mStarted = true;

        mStartTime = new Date();

        Log.d(TAG, "started");
    }

    public void stop() {
        mStarted = false;
        mEndTime = new Date();

        Log.d(TAG, "stopped:\n" + this.report());
    }

    public boolean isStarted() {
        return mStarted;
    }

    private String report() {

        String report = "";

         report += "Received Incoming Calls :" + mCountNotificationReceived + "\n";

        report += "Attempted to answer Calls:" + mCountCallAnswering + "\n";

        report += "Answered Calls           :" + mCountCallAnswered + "\n";

        report += "Ended Without error      :" + mCountCallEndedWithoutError + "\n";

        report += "Ended with error         :" + mCountCallEndedWithError + "\n";

        report += "Average of answering     :" + this.averageAnsweringTime() + " ms\n";

        report += "Average of talking       :" + this.averageTalingTime() + " ms";
        
        return report;
    }

    private long totalAnsweringTime() {
        long sum = 0;

        for (Map<String, Long> callDict : mCallStats.values()){
            Long answeringTime = callDict.get(KeyCallAnsweringTime);
            Long answeredTime = callDict.get(KeyCallAnsweredTime);
            if (null != answeredTime && null != answeringTime){
                sum += answeredTime - answeringTime;
            }
        }

        return sum;
    }

    private long averageAnsweringTime() {
        if (0 != mCountCallAnswered) {
            return this.totalAnsweringTime() / mCountCallAnswered;
        }
        return 0;
    }

    private long totalTalkingTime() {
        long sum = 0;

        for (Map<String, Long> callDict : mCallStats.values()){

            Long answeredTime = callDict.get(KeyCallAnsweredTime);
            Long endedTime = callDict.get(KeyCallEndedTime);
            if (null != answeredTime && null != endedTime){
                sum += endedTime - answeredTime;
            }
        }

        return sum;
    }

    private long averageTalingTime() {
        if (0 != mCountCallAnswered){
            return this.totalTalkingTime()/mCountCallAnswered;
        }
        return 0;
    }

    public void trackNotificationReceivedForCallId(String callId){

        if(!mStarted){
            return;
        }

        Long now = System.currentTimeMillis();
        ++mCountNotificationReceived;

        Map<String, Long> callDict = mCallStats.get(callId);
        if (null == callDict){
            callDict = Maps.newHashMap();
            mCallStats.put(callId, callDict);
        }
        callDict.put(KeyNotificationReceivedTime, now);
        Log.d(TAG, "trackNotificationReceivedForCallId(" + callId + ")");
    }

    public void trackCallAnswering(M800Call call){

        if(!mStarted){
            return;
        }

        Long now = System.currentTimeMillis();
        ++mCountCallAnswering;

        Map<String,Long> callDict = mCallStats.get(call.callID());
        if (null != callDict){
            callDict.put(KeyCallAnsweringTime, now);
            Long pushtime = callDict.get(KeyNotificationReceivedTime);
            long intval = now - pushtime;
            Log.d(TAG, "trackCallAnswering(" + call.callID() + ") - push->answering used " + intval + " ms");
        }
        else {
            Log.d(TAG, "trackCallAnswering(" + call.callID() + ") - ERROR: Call not exists");
        }
    }

    public void trackCallAnswered(M800Call call){

        if(!mStarted){
            return;
        }

        Long now = System.currentTimeMillis();
        ++mCountCallAnswered;

        Map<String,Long> callDict = mCallStats.get(call.callID());
        if (null != callDict){
            callDict.put(KeyCallAnsweredTime, now);
            Long anweringTime = callDict.get(KeyCallAnsweringTime);
            long intval = now - anweringTime;
            Log.d(TAG, "trackCallAnswered(" + call.callID() + ") - answering->answered used " + intval + " ms");
        }
        else {
            Log.d(TAG, "trackCallAnswered(" + call.callID() + ") - ERROR: Call not exists");
        }
    }

    public void trackCallEnded(M800Call call, boolean hasError){

        if(!mStarted){
            return;
        }

        Long now = System.currentTimeMillis();
        if (hasError){
            ++mCountCallEndedWithError;
        }
        else {
            ++mCountCallEndedWithoutError;
        }

        Map<String,Long> callDict = mCallStats.get(call.callID());
        if (null != callDict){
            callDict.put(KeyCallEndedTime, now);
            Long anweredTime = callDict.get(KeyCallAnsweredTime);
            long intval = now - anweredTime;
            Log.d(TAG, "trackCallAnswered(" + call.callID() + ") - answered->ended used " + intval + " ms");
        }
        else {
            Log.d(TAG, "trackCallAnswered(" + call.callID() + ") - ERROR: Call not exists");
        }
    }

}
