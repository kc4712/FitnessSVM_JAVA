package kr.co.greencomm.ibody24.coach.data;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import kr.co.greencomm.ibody24.coach.activity.ActivityMain;
import kr.co.greencomm.ibody24.coach.broadcast.SendReceive;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.webs.QueryListener;
import kr.co.greencomm.ibody24.coach.webs.QueryParser;
import kr.co.greencomm.ibody24.coach.webs.QueryStatus;
import kr.co.greencomm.middleware.utils.FileManager;

/**
 * 활동 내역 그래프
 */
public class FirmDownload
        extends Thread
        implements QueryParser, QueryListener {
    private static final String tag = FirmDownload.class.getSimpleName();

    /**
     * variable
     **/
    private String name = "firmware";
    private String tmp_extension = ".green";

    private String path;
    private String url;
    private QueryListener m_listener;
    private static String Sucessfilename;

    private final WeakReference<Context> WContext;

    public FirmDownload(Context context, String url, String path, QueryListener m_listener) {
        WContext = new WeakReference<>(context);
        this.url = url;
        this.path = path;
        this.m_listener = m_listener;

    }


    public boolean execBlock() {
        boolean ret = false;
        try {
            Log.d(tag, "url->" + this.url);
            URL url = new URL(this.url);
            String[] tmpArray = this.url.split("\\/");
            String name = tmpArray[tmpArray.length - 1];
            // URL에 접속 하여 양방향 통신이 가능한 컨넥션을 얻습니다.
            //HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            URLConnection conn = url.openConnection();
            if (conn == null) return false;
            // 컨넥션 연결 제한 시간을 20초로 합니다.
            conn.setConnectTimeout(20000);
            // 컨넥션의 읽기 제한 시간을 15초로 합니다.
            conn.setReadTimeout(15000);
            //캐쉬 사용여부를 지정합니다.
            conn.setUseCaches(false);
            conn.connect();
            // http 연결의 경우 요청방식을 지정할수 있습니다.
            // 지정하지 않으면 디폴트인 GET 방식이 적용됩니다.
            // conn.setRequestMethod("GET" | "POST");
            // 서버에 요청을 보내 응답 결과를 받아옵니다.
            //int resCode = conn.getResponseCode();
            int len = conn.getContentLength();
            // 요청이 정상적으로 전달되엇으면 HTTP_OK(200)이 리턴됩니다.
            // URL이 발견되지 않으면 HTTP_NOT_FOUND(404)가 리턴됩니다.
            // 인증에 실패하면 HTTP_UNAUTHORIZED(401)가 리턴됩니다.
            //if( resCode == HttpURLConnection.HTTP_OK ) {
            // 요청에 성공했으면 getInputStream 메서드로 입력 스트림을 얻어 서버로부터 전송된 결과를 읽습니다.
            String[] extension = name.split("\\.");
            readingStream(new BufferedInputStream(url.openStream()), this.name, len);
            renameFile(this.name, extension[1]);
            if (m_listener != null) {
                raiseResult(QueryStatus.Success.name());
            }
            //}
            //else {
            // raiseError(QueryStatus.ERROR_Web_Read, null);
            // }
            //conn.disconnect();
            conn = null;
        } catch (UnknownHostException e) {
            raiseError(QueryStatus.ERROR_Web_Read, e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            raiseError(QueryStatus.ERROR_Web_Read, e.toString());
            e.printStackTrace();
        }
        return ret;
    }

    private boolean isExist(String name, int len) {
        File file = new File(path + name);
        return file.exists() && file.length() == len;
    }

    private void readingStream(InputStream in, String name, int len) {
        // 스트림 읽어서 파일까지 만들어 저장함.
        FileOutputStream fout = null;
        try {
            File tmp = new File(path);
            tmp.mkdirs();
            fout = new FileOutputStream(path + name + tmp_extension);
            Log.d(tag, path + name + tmp_extension);

            byte data[] = new byte[2048];

            int count = 0;
            long total = 0;
            int old_pst = -1;
            while ((count = in.read(data)) != -1) {
                total += count;
                fout.write(data, 0, count);
                int new_pst = (int) (total * 100 / len);
                if (new_pst != old_pst) {
                    old_pst = new_pst;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fout != null)
                    fout.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void renameFile(String name, String extension) {
        File path = new File(FileManager.getMainPath(ActivityMain.MainContext));
        File source = new File(path, name + tmp_extension);
        Log.i(tag, "+++++++++++++++++ name:"+name+ "extension:"+ tmp_extension);
        if (source.isFile()) {
            source.renameTo(new File(path, name + "." + extension));
            Log.i(tag, "+++++++++++++++++ name:"+name+ "extension:"+ extension);
            Sucessfilename = name + "." + extension;
            Log.i(tag, "+++++++++++++++++ name:"+name+ "extension:"+ extension);
            Log.i(tag, "+++++++++++++++++ Sucessfilename:"+ Sucessfilename);
        }
    }
    public static String getSucessfilename(){
        return Sucessfilename;
    }

    @Override
    public void run() {
        execBlock();
    }

    @Override
    public QueryStatus onParse(QueryCode queryCode, String request, String result, QueryListener listener) {
        return QueryStatus.OK;
    }

    @Override
    public void OnQuerySuccess(QueryCode queryCode) {

    }

    @Override
    public void OnQueryFail(QueryStatus queryStatus) {

    }

    public void raiseError(final QueryStatus stat, final String msg) {
        Log.d(tag, "오류발생: " + msg);
        if (m_listener != null) {
            //Log.d(TAG, "오류콜백 ***************");
            m_listener.onQueryError(QueryCode.GetFirmware, stat, msg);
        }
    }

    public void raiseResult(final String result) {
        Log.d(tag, "처리완료: " + result);
        if (m_listener != null) {
            //Log.d(TAG, "정상콜백 ***************");

            // 기존 펌웨어 업데이트 코드 주석처리.
            //m_listener.onQueryResult(QueryCode.GetFirmware, url, result);
            m_listener.onQueryResult(QueryCode.GetFirmware, path + FirmDownload.getSucessfilename(), result);
        }

        // 기존 펌웨어 업데이트 코드 주석처리.
        /*if (FirmDownload.getSucessfilename() != null){
            Log.i(tag, "FirmDownload.getSucessfilename():" + FirmDownload.getSucessfilename());
            SendReceive.sendFirmUpdateStart(WContext.get(), path + FirmDownload.getSucessfilename());
        }*/
    }

    @Override
    public void onQueryResult(QueryCode queryCode, String request, String result) {
    }

    @Override
    public void onQueryError(QueryCode queryCode, final QueryStatus nErrorCode, String szErrMessage) {
        String message = QueryStatus.convertQueryError(nErrorCode);
        Log.d(tag, "Err message : " + message);
    }
}
