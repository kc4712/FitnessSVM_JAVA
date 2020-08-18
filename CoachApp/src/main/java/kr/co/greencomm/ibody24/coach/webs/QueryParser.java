package kr.co.greencomm.ibody24.coach.webs;

/**
 * Created by young on 2015-09-04.
 */
public interface QueryParser {
    QueryStatus onParse(QueryCode queryCode, String request, String result, QueryListener listener);
    void OnQuerySuccess(QueryCode queryCode);
    void OnQueryFail(QueryStatus queryStatus);
}
