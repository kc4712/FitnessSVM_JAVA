package kr.co.greencomm.ibody24.coach.webs;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import kr.co.greencomm.ibody24.coach.activity.ActivityMain;
import kr.co.greencomm.ibody24.coach.dialog.ProgressListener;
import kr.co.greencomm.middleware.utils.FileManager;

/**
 * Created by jeyang on 16. 9. 28..
 */
public class WebDownLoad extends Thread {
    private static final String tag = WebDownLoad.class.getSimpleName();

    private String tmp_extension = ".green";

    private String path;
    private String url;
    private WebDownListener m_listener;
    private ProgressListener m_progress;

    private boolean isLoading;

    public WebDownLoad(String url, String path, WebDownListener m_listener, ProgressListener m_progress) {
        this.url = url;
        this.path = path;
        this.m_listener = m_listener;
        this.m_progress = m_progress;
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
            readingStream(new BufferedInputStream(url.openStream()), extension[0], len);
            if (isLoading) {
                renameFile(extension[0], extension[1]);
                if (m_listener != null) {
                    raiseResult(QueryStatus.Success.name());
                }
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

    private void deleteFile() {
        // 폴더 안에는 최신 펌웨어 하나만 존재해야함.
        File file = new File(path);
        File[] fArray = file.listFiles();
        if (fArray == null)
            return;

        for (File f : fArray) {
            f.delete();
        }
    }

    private void readingStream(InputStream in, String name, int len) {
        // 스트림 읽어서 파일까지 만들어 저장함.
        FileOutputStream fout = null;
        isLoading = true;
        try {
            File tmp = new File(path);
            tmp.mkdirs();
            fout = new FileOutputStream(path + name + tmp_extension);
            Log.d(tag, path + name + tmp_extension);

            byte data[] = new byte[2048];

            int count = 0;
            long total = 0;
            int old_pst = -1;
            while ((count = in.read(data)) != -1 && isLoading) {
                total += count;
                fout.write(data, 0, count);
                int new_pst = (int) (total * 100 / len);
                if (new_pst != old_pst) {
                    old_pst = new_pst;
                    Log.d(tag, "Progress: " + new_pst + "%");
                    if (m_progress != null)
                        m_progress.onProgress(new_pst);
                }
            }
        } catch (Exception e) {
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

    private boolean isExist(String name, int len) {
        File file = new File(path + name);
        return file.exists() && file.length() == len;
    }

    protected void renameFile(String name, String extension) {
        Log.d(tag, "renameFile "+name);
        File path = new File(FileManager.getMainPath(ActivityMain.MainContext));
        File source = new File(path, name + tmp_extension);
        if (source.isFile()) {
            source.renameTo(new File(path, name + "." + extension));
        }
    }

    @Override
    public void run() {
        execBlock();
    }

    public void raiseError(final QueryStatus stat, final String msg) {
        Log.d(tag, "오류발생: " + msg);
        if (m_listener != null) {
            //Log.d(TAG, "오류콜백 ***************");
            m_listener.fail();
        }
    }

    public void raiseResult(final String result) {
        Log.d(tag, "처리완료: " + result);
        if (m_listener != null) {
            //Log.d(TAG, "정상콜백 ***************");
            m_listener.success(url);
        }
    }

    public void cancel() {
        try {
            m_listener = null;
            m_progress = null;
            isLoading = false;
            this.join();
            this.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
