package kr.co.greencomm.ibody24.coach.webs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

/**
 * Created by young on 2015-09-04.
 */
public class QueryThread
    extends Thread
{
    private final static String tag = "[웹쿼리 Service]";

    public final static String VERSION_URL = "http://ibody24.com/service/Version.svc/";
    public final static String SERVER_URL = "http://ibody24.com/service/body.svc/";

    private QueryCode m_queryCode;
    private String m_request;
    private QueryParser m_parser;
    private QueryListener m_listener;

    public static final int QUERY_CODE_CreateUser = 1;
    public static final int QUERY_CODE_LoginUser = 2;

    public QueryThread(QueryCode queryCode, String request, QueryParser parser, QueryListener listener) {
        m_queryCode = queryCode;
        m_request = request;
        m_parser = parser;
        m_listener = listener;
        this.setName("WebQuery");
        Log.d(tag, "요청: " + m_request);
    }

    static String readingStream(InputStream in) {
        String ret = "";
        try {
            //스트림을 직접읽으면 느리고 비효율 적이므로 버퍼를 지원하는 BufferedReader 객체를 사용합니다.
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String lineStr;
            while ((lineStr = bufferReader.readLine()) != null) {
                ret += lineStr;
            }
            bufferReader.close();
            bufferReader = null;
        } catch (Exception e) {
            ret = null;
        }
        return ret;
    }

    public void raiseError(final QueryStatus stat, final String msg) {
        Log.d(tag, "오류발생: " + msg);
        if (m_listener != null) {
            //Log.d(TAG, "오류콜백 ***************");
            m_listener.onQueryError(m_queryCode, stat, msg);
        }
    }

    public void raiseResult(final String result) {
        Log.d(tag, "처리완료: " + result);
        if (m_listener != null) {
            //Log.d(TAG, "정상콜백 ***************");
            m_listener.onQueryResult(m_queryCode, m_request, result);
        }
    }

    public boolean execBlock() {
        QueryStatus ret = QueryStatus.OK;
        try {
            // Log.d(TAG, "시작");
            URL url = new URL(m_request);
            // URL에 접속 하여 양방향 통신이 가능한 컨넥션을 얻습니다.
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn == null) return false;
            // 컨넥션 연결 제한 시간을 20초로 합니다.
            conn.setConnectTimeout(20000);
            // 컨넥션의 읽기 제한 시간을 15초로 합니다.
            conn.setReadTimeout(15000);
            //캐쉬 사용여부를 지정합니다.
            conn.setUseCaches(false);
            // http 연결의 경우 요청방식을 지정할수 있습니다.
            // 지정하지 않으면 디폴트인 GET 방식이 적용됩니다.
            // conn.setRequestMethod("GET" | "POST");
            // 서버에 요청을 보내 응답 결과를 받아옵니다.
            int resCode = conn.getResponseCode();
            // 요청이 정상적으로 전달되엇으면 HTTP_OK(200)이 리턴됩니다.
            // URL이 발견되지 않으면 HTTP_NOT_FOUND(404)가 리턴됩니다.
            // 인증에 실패하면 HTTP_UNAUTHORIZED(401)가 리턴됩니다.
            if( resCode == HttpURLConnection.HTTP_OK ) {
                // 요청에 성공했으면 getInputStream 메서드로 입력 스트림을 얻어 서버로부터 전송된 결과를 읽습니다.
                InputStream inputStream = conn.getInputStream();
                String result = readingStream(inputStream);
                inputStream.close();
                inputStream = null;
                Log.d(tag, "읽음: " + result);
                if (m_parser != null) {
                    ret = m_parser.onParse(m_queryCode, m_request, result, m_listener);
                    if (ret == QueryStatus.Success) {
                        m_parser.OnQuerySuccess(m_queryCode);
                        raiseResult(ret.name());
                    }
                    else {
                        m_parser.OnQueryFail(ret);
                        raiseError(ret, null);
                    }
                }
            }
            else {
                raiseError(QueryStatus.ERROR_Web_Read, null);
            }
            conn.disconnect();
            conn = null;
        } catch (Exception e) {
            raiseError(QueryStatus.ERROR_Web_Read, e.toString());
            e.printStackTrace();
        }
        return ret == QueryStatus.Success;
    }

    @Override
    public void run() {
        execBlock();
    }
}