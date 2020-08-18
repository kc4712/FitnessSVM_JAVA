package kr.co.greencomm.ibody24.coach.data;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.util.Locale;

import kr.co.greencomm.ibody24.coach.base.CoachBaseActivity;
import kr.co.greencomm.ibody24.coach.utils.ProgramCode;
import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.webs.QueryParser;
import kr.co.greencomm.ibody24.coach.webs.QueryStatus;
import kr.co.greencomm.ibody24.coach.webs.QueryThread;
import kr.co.greencomm.ibody24.coach.webs.WebDownListener;
import kr.co.greencomm.ibody24.coach.webs.WebDownLoad;
import kr.co.greencomm.ibody24.coach.utils.Utils;
import kr.co.greencomm.ibody24.coach.webs.QueryListener;
import kr.co.greencomm.middleware.db.Preference;
import kr.co.greencomm.middleware.utils.Convert;
import kr.co.greencomm.middleware.utils.FileManager;

/**
 * Created by jeyang on 16. 9. 28..
 */
public class VersionService implements QueryParser, WebDownListener {
    private static final String tag = VersionService.class.getSimpleName();

    private String m_uri = "";

    private WebDownLoad downloader;

    private String m_dest = "";

    private String m_main = "1";
    private String m_sub = "0";
    private String m_build = "1";
    private String m_rev = "1";

    private String new_version = "";

    private int queryCode;

    private Context mContext;

    public VersionService(Context context, int queryCode) {
        mContext = context;
        this.queryCode = queryCode;
        checkVersion();
    }

    private String getStoreVersion() {
        String version = "";
        switch (CoachBaseActivity.program) {
            case Full_Body:
                version = Preference.getXmlVersion002(mContext);
                break;
            case Mat:
                version = Preference.getXmlVersion003(mContext);
                break;
            case Active:
                version = Preference.getXmlVersion004(mContext);
                break;
            case Abs:
                version = Preference.getXmlVersion101(mContext);
                break;
            case Back:
                version = Preference.getXmlVersion102(mContext);
                break;
            case TBT1:
                version = Preference.getXmlVersion201(mContext);
                break;
            case TBT2:
                version = Preference.getXmlVersion202(mContext);
                break;
            case Baby1:
                version = Preference.getXmlVersion301(mContext);
                break;
            case Baby2:
                version = Preference.getXmlVersion302(mContext);
                break;
            default:
                break;
        }

        return version;
    }

    private void setStoreVersion(String version) {
        switch (CoachBaseActivity.program) {
            case Full_Body:
                Preference.putXmlVersion002(mContext, version);
                break;
            case Mat:
                Preference.putXmlVersion003(mContext, version);
                break;
            case Active:
                Preference.putXmlVersion004(mContext, version);
                break;
            case Abs:
                Preference.putXmlVersion101(mContext, version);
                break;
            case Back:
                Preference.putXmlVersion102(mContext, version);
                break;
            case TBT1:
                Preference.putXmlVersion201(mContext, version);
                break;
            case TBT2:
                Preference.putXmlVersion202(mContext, version);
                break;
            case Baby1:
                Preference.putXmlVersion301(mContext, version);
                break;
            case Baby2:
                Preference.putXmlVersion302(mContext, version);
                break;
        }
    }

    private void checkVersion() {
        String version = getStoreVersion();
        if (version != null) {
            String[] arr_version = version.split(".");
            m_main = arr_version[0];
            m_sub = arr_version[1];
            m_build = arr_version[2];
            m_rev = arr_version[3];
        }
    }

    /**
     * 웹 쿼리 요청 문자열을 생성한다.
     *
     * @return
     */
    private String makeVersionRequest() {
        StringBuilder sb = new StringBuilder(QueryThread.VERSION_URL);
        sb.append(QueryCode.CheckVersion.name());
        sb.append("?code=").append(queryCode);
        sb.append("&main=").append(m_main);
        sb.append("&sub=").append(m_sub);
        sb.append("&build=").append(m_build);
        sb.append("&rev=").append(m_rev);
        return sb.toString();
    }

    public synchronized void runQuery(QueryCode queryCode) {
        String request = makeVersionRequest();
        QueryThread thread = new QueryThread(queryCode, request, this, null);
        thread.start();
    }

    @Override
    public QueryStatus onParse(QueryCode queryCode, String request, String result, QueryListener listener) {
        String str;
        try {
            JSONObject json = new JSONObject(result);
            Log.d(tag, "웹 쿼리 반환 값 파싱");
            Log.d(tag, "Query Result Json: " + json);

            if ((str = Utils.getJsonValue(json, "Result")) == null) {
                return QueryStatus.ERROR_Result_Parse;
            }

            if ("1".equals(str) == false) {
                return QueryStatus.Non_Upgrade;
            } else {
                if ((str = Utils.getJsonValue(json, "UpdateStat")) != null) {
                    if (Boolean.parseBoolean(str)) {
                        new_version = String.format(Locale.getDefault(), "%d.%d.%d.%d", Convert.getInt(json, "VersionMain"), Convert.getInt(json, "VersionSub"),
                                Convert.getInt(json, "VersionBuild"), Convert.getInt(json, "VersionRevision"));
                        Log.d(tag, "new version : " + new_version);
                        if ((str = Utils.getJsonValue(json, "Uri")) != null) {
                            downloader = new WebDownLoad(str, FileManager.getMainPath(mContext), this, null);
                            downloader.start();
                        }
                    } else {
                        return QueryStatus.Non_Upgrade;
                    }
                }
            }

            return QueryStatus.OK;
        } catch (Exception e) {
            Log.d(tag, "onQueryParse catch error: " + e.toString());
            e.printStackTrace();
            return QueryStatus.Catch_Error;
        }
    }

    private void moveFile(String url) {
        String[] tmpArray = url.split("\\/");
        String url_name = tmpArray[tmpArray.length - 1];

        if (FileManager.isExistFile(mContext, url_name)) {
            String name = CoachBaseActivity.getXmlFileName();
            FileManager.moveFileName(FileManager.getMainPath(mContext), url_name, name);
        }
    }

    @Override
    public void OnQuerySuccess(QueryCode queryCode) {

    }

    @Override
    public void OnQueryFail(QueryStatus queryStatus) {

    }

    @Override
    public void success(String url) {
        moveFile(url);

        setStoreVersion(new_version);
    }

    @Override
    public void fail() {

    }
}
