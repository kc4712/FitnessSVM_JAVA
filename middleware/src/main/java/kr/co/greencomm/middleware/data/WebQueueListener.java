package kr.co.greencomm.middleware.data;


/**
 * Created by young on 2015-10-28.
 */
public interface WebQueueListener {
    void onSuccess(long time, int act_index);
}
