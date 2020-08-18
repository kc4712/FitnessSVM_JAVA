package kr.co.greencomm.ibody24.coach.data;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.webs.QueryParser;
import kr.co.greencomm.ibody24.coach.utils.Utils;
import kr.co.greencomm.ibody24.coach.webs.QueryListener;
import kr.co.greencomm.ibody24.coach.webs.QueryStatus;
import kr.co.greencomm.ibody24.coach.webs.QueryThread;
import kr.co.greencomm.middleware.wrapper.MWControlCenter;

/**
 * 활동 내역 그래프
 */
public class FirmInfo
	extends Thread
    implements QueryParser, QueryListener {
    private static final String tag = FirmInfo.class.getSimpleName();

    private QueryThread thread;

    private FirmUpListener m_listener;

    /** variable **/
    private int[] version;
    private String path;
    private int productCode;
    private final WeakReference<Context> WContext;
    
    public FirmInfo(Context context, String path, String version, int productCode) {
    	this.path = path;
        String[] tmp = version.trim().split("\\.");
        if (tmp.length >= 4) {
            this.version = new int[]{Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]),
                    Integer.parseInt(tmp[2]), Integer.parseInt(tmp[3])};
        } else {
            this.version = new int[]{0,0,0,0};
        }
        this.productCode = productCode;
    	WContext = new WeakReference<>(context);
    }
    
    public FirmInfo(Context context, String path, int[] version, int productCode) {
    	this.path = path;
    	this.version = version;
        this.productCode = productCode;
    	WContext = new WeakReference<>(context);
    }

    public void setListener(FirmUpListener listener) {
        m_listener = listener;
    }

    /**
     * 웹 쿼리 요청 문자열을 생성한다.
     * @return
     */
    private String makeVersionRequest(int[] version) {
        StringBuilder sb = new StringBuilder(QueryThread.VERSION_URL);
        sb.append(QueryCode.CheckVersion.name());
        sb.append("?code=").append(productCode);
        sb.append("&main=").append(version[0]);
        sb.append("&sub=").append(version[1]);
        sb.append("&build=").append(version[2]);
        sb.append("&rev=").append(version[3]);
        return sb.toString();
    }

    private void requestData() {
        try {
            String request = makeVersionRequest(version);
            thread = new QueryThread(QueryCode.CheckVersion, request, this, this);
            thread.start();
        }
        catch (Exception e) {
        	this.onQueryError(QueryCode.CheckVersion, QueryStatus.ERROR_Query, e.toString());
        }
    }
    
    @Override
    public void run() {
    	requestData();
    }

    @Override
    public QueryStatus onParse(QueryCode queryCode, String request, String result, QueryListener listener) {
        String str;
        if ( MWControlCenter.getInstance(WContext.get()).getBattery().getVoltage() < 50 ){
            Log.i(tag,"배터리 용량부족으로 펌웨어 업데이트 취소:"+MWControlCenter.getInstance(WContext.get()).getBattery().getVoltage());
            return QueryStatus.Non_Upgrade;
        }
        try {
            JSONObject json = new JSONObject(result);
            Log.d(tag, "웹 쿼리 반환 값 파싱");
            Log.d(tag, "Query Result Json: " + json);

            if ((str = Utils.getJsonValue(json, "Result")) == null) {
                return QueryStatus.ERROR_Result_Parse;
            }
            
            if (!"1".equals(str)) {
                return QueryStatus.Non_Upgrade;
            } else {
            	if ((str = Utils.getJsonValue(json, "UpdateStat")) != null) {
            		if(Boolean.parseBoolean(str)) {
            			if ((str = Utils.getJsonValue(json, "Uri")) != null) {
            				FirmDownload firm = new FirmDownload(WContext.get(),str, path, this);
            				firm.start();
            			}
            		} else {
            			//BluetoothLEManager.getInstance(WContext.get()).setUpgradeStatus(UpgradeStatus.ALREADY_NEW_VERSION);
            			
                        return QueryStatus.Non_Upgrade;
            		}
            	}
            }
            
            return QueryStatus.Success;
        }
        catch (Exception e) {
            Log.d(tag, "onQueryParse catch error: " + e.toString());
            e.printStackTrace();
            return QueryStatus.Catch_Error;
        }
    }

    @Override
    public void OnQuerySuccess(QueryCode queryCode) {
        Log.i(tag, "queryCode:" + queryCode);
    }

    @Override
    public void OnQueryFail(QueryStatus queryStatus) {

    }

    @Override
    public void onQueryResult(QueryCode queryCode, String request, String result) {
    	if(queryCode == QueryCode.GetFirmware && result != null && result.equals(QueryStatus.Success.name())) {
            if (m_listener != null) {
                m_listener.firmUp(true, request);

                // 기존 펌웨어 업데이트 코드 주석처리.
                //m_listener.firmUp(true);
            }
        }
    }

    @Override
    public void onQueryError(QueryCode queryCode, final QueryStatus nErrorCode, String szErrMessage) {
    	String message = QueryStatus.convertQueryError(nErrorCode);
    	Log.d(tag,"Err message : "+message + "code : "+queryCode);
        // Web read 에러인 경우 에러 처리 필요.
        if(queryCode == QueryCode.GetFirmware && nErrorCode == QueryStatus.ERROR_Web_Read) {
            if (m_listener != null) {
                m_listener.firmUp(false, null);

                // 기존 펌웨어 업데이트 코드 주석처리.
                //m_listener.firmUp(false);
            }
        }
    }
}
