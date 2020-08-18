package kr.co.greencomm.ibody24.coach.webs;

/**
 * Created by young on 2015-09-04.
 */
public interface QueryListener {
    void onQueryResult(QueryCode queryCode, String request, String result);
    void onQueryError(QueryCode queryCode, QueryStatus nErrorCode, String szErrMessage);
}
