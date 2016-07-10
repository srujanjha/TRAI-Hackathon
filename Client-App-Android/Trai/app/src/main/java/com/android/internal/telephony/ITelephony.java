package com.android.internal.telephony;

/**
 * Created by Srujan Jha on 7/9/2016.
 */
public interface ITelephony {
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}
