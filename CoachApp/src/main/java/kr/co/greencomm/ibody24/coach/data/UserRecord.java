package kr.co.greencomm.ibody24.coach.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

import kr.co.greencomm.ibody24.coach.webs.QueryCode;
import kr.co.greencomm.ibody24.coach.webs.QueryParser;
import kr.co.greencomm.middleware.utils.Convert;
import kr.co.greencomm.ibody24.coach.utils.Utils;
import kr.co.greencomm.ibody24.coach.webs.QueryListener;
import kr.co.greencomm.ibody24.coach.webs.QueryStatus;
import kr.co.greencomm.ibody24.coach.webs.QueryThread;

/**
 * 사용자 정보 레코드
 */
public class UserRecord
    implements QueryParser, Parcelable
{
    private static final String tag = UserRecord.class.getSimpleName();

    public final static int SEX_MAN = 1;
    public final static int SEX_WOMAN = 2;
    public final static int EMPTY = -1;

    private String m_email;
    private String m_password;
    private UUID m_code;

    private String m_name;
    private Calendar m_birthday;
    private int m_gender;

    private int m_height; //height
    private int m_job;

    private float m_weight;//weight
    private float m_goalWeight;//weight
    private int m_dietPeriod;

    private String deviceName;
    private String deviceAddress;
    
    private QueryThread thread;
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.d(tag, "finalize");
        m_email = null;
        m_password = null;
        m_code = null;
        m_name = null;
        m_birthday = null;
        deviceName = null;
        deviceAddress = null;
    }

    void empty() {
        m_email = "";
        m_password = "";
        m_code = UUID.randomUUID();

        m_birthday = null;
        m_height = 0;
        m_weight = 0;
        //m_age = 0;
        m_gender = 0;
        m_job = EMPTY;
        m_goalWeight = 0;
        m_dietPeriod = 0;

        deviceName = "";
        deviceAddress = "";
    }

    //----------------------------------------------------------------------------------------------
    // 이메일 주소
    //----------------------------------------------------------------------------------------------

    /**
     * 이메일 주소를 설정한다.
     * @param email 이메일 주소
     */
    public void setEmail(String email) {
        m_email = email.trim();
    }

    /**
     * 이메일 주소를 반환한다.
     * @return 이메일 주소
     */
    public String getEmail() {
        return m_email;
    }

    //----------------------------------------------------------------------------------------------
    // 비밀번호
    //----------------------------------------------------------------------------------------------

    /**
     * 비밀번호를 설정한다.
     * @param password
     */
    public void setPassword(String password) {
        m_password = password.trim();
    }

    /**
     * 비밀번호를 반환한다.
     * @return 비밀번호
     */
    public String getPassword() {
        return m_password;
    }

    //----------------------------------------------------------------------------------------------
    // 사용자 코드
    //----------------------------------------------------------------------------------------------

    /**
     * 사용자 코드를 설정한다.
     * @param code 사용자 코드
     */
    public void setCode(UUID code) {
        m_code = code;
    }

    /**
     * 사용자 코드를 반환한다.
     * @return 사용자 코드
     */
    public UUID getCode() {
        return m_code;
    }
    
    //----------------------------------------------------------------------------------------------
    // 사용자 이름
    //----------------------------------------------------------------------------------------------

	public void setName(String name) {
        m_name = name;
    }

    public String getName() {
        //return Utils.encodeString(m_name);
        String ret = m_name;
        try {
            ret = new String(m_name.getBytes("UTF-8"));
        }
        catch (Exception e) {}
        return ret;
    }

    //----------------------------------------------------------------------------------------------
    // 장치 이름
    //----------------------------------------------------------------------------------------------

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    //----------------------------------------------------------------------------------------------
    // 장치 주소
    //----------------------------------------------------------------------------------------------

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    //----------------------------------------------------------------------------------------------
    // 생년월일
    //----------------------------------------------------------------------------------------------

    /**
     * 생일을 설정한다.
     * @param birthday 생일
     */
    public void setBirthday(Calendar birthday) {
        m_birthday = birthday;
    }

    public void setBirthday(long milli) {
        m_birthday = Calendar.getInstance();
        m_birthday.setTimeInMillis(milli);
    }

    /**
     * 생일을 설정한다.
     *
     * @param year 년
     * @param month 월
     * @param day 일
     */
    public void setBirthday(int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        m_birthday = calendar;
    }

    /**
     * 생일을 반환한다.
     * @return 생일
     */
    public Calendar getBirthday() {
        return m_birthday;
    }

    public String getBirthString() {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        return sdFormat.format(m_birthday.getTime());
    }

    //----------------------------------------------------------------------------------------------
    // 성별
    //----------------------------------------------------------------------------------------------

    /**
     * 사용자 정보의 성별을 설정한다.
     * @param gender 성별 (SEX_MAN:남자, SEX_WOMAN:여자)
     * @return 성공 'true', 'false' 파라메터 값이 잘못되었을 경우
     */
    public boolean setGender(int gender) {
        if (gender == SEX_MAN || gender == SEX_WOMAN) {
            m_gender = gender;
            return true;
        }
        return false;
    }

    /**
     * 사용자 정보의 성별을 가져온다.
     * @return 성별 (SEX_MAN:남자, SEX_WOMAN:여자)
     */
    public int getGender() {
        return m_gender;
    }

    /**
     * 사용자 정보의 신장을 설정한다.
     * @param height 신장 - 단위는 setHeightUnit에 설정된 값 (default: cm)
     */
    public void setHeight(int height) {
        m_height = height;
    }

    /**
     * 사용자 정보의 신장을 가져온다.
     * @return 신장 - 단위는 setHeightUnit에 설정된 값 (default: cm)
     */
    public int getHeight() {
        return m_height;
    }

    /**
     * 사용자 정보의 체중을 설정한다.
     * @param weight 체중 - 단위는 setWeightUnit에 설정된 값 (default: kg)
     */
    public void setWeight(float weight) {
        m_weight = weight;
    }

    /**
     * 사용자 정보의 체중을 가져온다.
     * @return 체중 - 단위는 setWeightUnit에 설정된 값 (default: kg)
     */
    public float getWeight() {
        return m_weight;
    }

    public int getAge() {
        if (m_birthday == null) {
            return 1;
        }
        int m_now_year = Calendar.getInstance().get(Calendar.YEAR);
        int m_birth_year = m_birthday.get(Calendar.YEAR);
        return m_now_year - m_birth_year + 1;
    }

    /**
     * 사용자 정보의 직업을 설정한다.
     * @param job
     * 직업(직업군에 디파인된 값)
     */
    public void setJob(int job) {
        m_job = job;
    }
    /*
     * 사용자 정보의 직업을 가져온다.
     *
     * 리턴 :  직업(직업군에 디파인된 값)
     *
     */
    public int getJob() {
        return m_job;
    }

    /*
     * 사용자 정보의 목표 체중을 설정한다.
     *
     * 파라메터 : goalWeight  = 목표 체중(setWeightUnit에 설정된 값의 단위(default: kg))
     *
     */
    public void setGoalWeight(float goalWeight) {
        m_goalWeight = goalWeight;
    }

    /*
     * 사용자 정보의 목표 체중을 가져온다.
     *
     * 리턴 : 목표 체중(setWeightUnit에 설정된 값의 단위(default: kg))
     *
     */
    public float getGoalWeight() {
        return m_goalWeight;
    }
    /*
     * 사용자 정보의 감량 기간을 설정한다.
     *
     * 파라메터 : v = 감량 기간.(주 단위)
     *
     */
    public void setDietPeriod(int v) {
        m_dietPeriod = v;
    }
    /*
     * 사용자 정보의 감량 기간을 가져온다.
     *
     * 리턴 : 감량 기간.(주 단위)
     *
     */
    public int getDietPeriod() {
        return m_dietPeriod;
    }


    public boolean isEmpty(String strValue) {
        if (strValue == null || strValue.length() == 0 || strValue.trim().length() == 0) {
            return true;
        }
        return false;
    }

    public boolean isDeviceValid() {
        if (isEmpty(deviceAddress)) {
            return false;
        }
        return true;
    }

    public boolean isProfileValid() {
        if (isEmpty(m_email)) return false;
        if (isEmpty(m_password)) return false;
        if (isEmpty(m_name)) return false;
        if (m_birthday == null) return false;
        if (m_gender == 0) return false;
        if (m_height == 0) return false;
        return true;
    }

    public boolean isTargetValid() {
        if (m_weight == 0) return false;
        if (m_goalWeight == 0) return false;
        if (m_dietPeriod == 0) return false;
        return true;
    }

    public boolean isValid() {
        return isDeviceValid() || isProfileValid() || isTargetValid();
    }

    public void readFromJSON(JSONObject json) throws Exception {
        String szValue;

        if ((szValue = Utils.getJsonValue(json, "Code")) == null) {
            throw new Exception("사용자 정보 파싱 오류");
        }

        this.m_code = UUID.fromString(szValue);
        Log.d(tag, "Code: " + m_code.toString() + " <= " + szValue);

        if ((szValue = Utils.getJsonValue(json, "Email")) != null) {
            this.m_email = szValue;
        }
        if ((szValue = Utils.getJsonValue(json, "Password")) != null) {
            this.m_password = szValue;
        }
        if ((szValue = Utils.getJsonValue(json, "Name")) != null) {
            this.m_name = szValue;
        }
        /*
        if ((szValue = Utils.getJsonValue(json, "CreatDate")) != null) {
            this.m_user_create = Utils.convertDate(szValue);
        }
        */
        if ((szValue = Utils.getJsonValue(json, "Birthday")) != null) {
            this.m_birthday = Utils.convertDate(szValue);
        }
        if ((szValue = Utils.getJsonValue(json, "Gender")) != null) {
            this.m_gender = Integer.parseInt(szValue);
        }
        if ((szValue = Utils.getJsonValue(json, "Height")) != null) {
            this.m_height = Integer.parseInt(szValue);
        }
        if ((szValue = Utils.getJsonValue(json, "CurrentWeight")) != null) {
            this.m_weight = Float.parseFloat(szValue);
        }
        if ((szValue = Utils.getJsonValue(json, "TargetWeight")) != null) {
            this.m_goalWeight = Float.parseFloat(szValue);
        }
        if ((szValue = Utils.getJsonValue(json, "DietPeriod")) != null) {
            this.m_dietPeriod = Integer.parseInt(szValue);
        }
        if ((szValue = Convert.getJsonValue(json, "DeviceAddress")) != null) {
            if (szValue.contains(",") == true) {
                String[] infos = szValue.split(",");
                for (String info : infos) {
                    if (info.contains(":") == true) {
                        deviceAddress = info;
                    }
                    else {
                        deviceName = info;
                    }
                }
            }
            else {
                if (szValue.contains(":") == true) {
                    this.deviceAddress = szValue;
                }
                else {
                    this.deviceName = szValue;
                }
            }
        }
    }

    public int getBirthYear() { return (m_birthday == null ? Calendar.getInstance() : m_birthday).get(Calendar.YEAR); }
    public int getBirthMonth() { return (m_birthday == null ? Calendar.getInstance() : m_birthday).get(Calendar.MONTH) + 1; }
    public int getBirthDay() { return (m_birthday == null ? Calendar.getInstance() : m_birthday).get(Calendar.DAY_OF_MONTH); }

    public String makeRequest(QueryCode queryCode) {
        StringBuilder sb = new StringBuilder(QueryThread.SERVER_URL);
        sb.append(queryCode.name());
        try {
            switch (queryCode) {
                case CreateUser:
                    sb.append("?id=").append(getEmail());
                    sb.append("&pw=").append(getPassword());
                    break;
                case LoginUser:
                    sb.append("?id=").append(getEmail());
                    sb.append("&pw=").append(getPassword());
                    break;
                case SetDevice:
                    sb.append("?code=").append(getCode().toString());
                    sb.append("&dev=").append(getDeviceName());
                    sb.append("&mac=").append(getDeviceAddress() + "," + getDeviceName());
                    break;
                case SetProfile:
                    sb.append("?code=").append(getCode().toString());
                    sb.append("&name=").append(URLEncoder.encode(getName(), "UTF-8"));
                    sb.append("&birthday=").append(getBirthString());
                    sb.append("&gender=").append(getGender());
                    sb.append("&height=").append(getHeight());
                    sb.append("&job=").append(getJob());
                    break;
                case SetTarget:
                    sb.append("?code=").append(getCode().toString());
                    sb.append("&current=").append(getWeight());
                    sb.append("&target=").append(getGoalWeight());
                    sb.append("&period=").append(getDietPeriod());
                    break;
            }
        }
        catch (Exception e) {}
        return sb.toString();
    }

    @Override
    public QueryStatus onParse(QueryCode queryCode, String request, String result, QueryListener listener) {
        try {
            JSONObject json = new JSONObject(result);

            Log.d(tag, "=====================================================");
            Log.d(tag, "웹 반환 값 파싱");
            Log.d(tag, "=====================================================");
            Log.d(tag, "Query Request: " + request);
            Log.d(tag, "Query Result: " + result);
            Log.d(tag, "Query Result Json: " + json);

            String szResult;

            if ((szResult = Utils.getJsonValue(json, "Result")) == null) {
                return QueryStatus.ERROR_Result_Parse;
            }
            if (szResult.equals("2") == true) {
                return QueryStatus.ERROR_Exists_Email;
            }
            if (szResult.equals("3") == true) {
                return QueryStatus.ERROR_Account_Not_Match;
            }
            if ((szResult = Utils.getJsonValue(json, "UserInfo")) == null) {
                return QueryStatus.ERROR_Non_Information;
            }

            readFromJSON(json.getJSONObject("UserInfo"));
            return QueryStatus.Success;
        }
        catch (Exception e) {
            Log.d(tag, "userParser Error: " + e);
            return QueryStatus.ERROR_Result_Parse;
        }
    }

    @Override
    public void OnQuerySuccess(QueryCode queryCode) {

    }

    @Override
    public void OnQueryFail(QueryStatus queryStatus) {

    }

    public synchronized void runQuery(QueryCode queryCode, QueryListener listener) {
        try {
            thread = new QueryThread(queryCode, makeRequest(queryCode), this, listener);
            thread.start();
        }
        catch (Exception e) {
        	thread.raiseError(QueryStatus.ERROR_Query, e.toString());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(m_email);                      // private String m_email;
        dest.writeString(m_password);                   // private String m_password;
        dest.writeString(m_code.toString());            // private UUID m_code;
        dest.writeString(m_name);                       // private String m_name;
        long milli = (m_birthday == null ? 0 : m_birthday.getTimeInMillis());
        dest.writeLong(milli);                          // private Calendar m_birthday;
        dest.writeInt(m_gender);                        // private int m_gender;
        dest.writeInt(m_height);                        // private int m_height; //height
        dest.writeInt(m_job);                           // private int m_job;
        dest.writeFloat(m_weight);                      // private float m_weight;//weight
        dest.writeFloat(m_goalWeight);                  // private float m_goalWeight;//weight
        dest.writeInt(m_dietPeriod);                    // private int m_dietPeriod;
        dest.writeString(deviceName);                   // private String deviceName;
        dest.writeString(deviceAddress);                // private String deviceAddress;
    }

    public void readFromParcel(Parcel in) {
        setEmail(in.readString());                      // private String m_email;
        setPassword(in.readString());                   // private String m_password;
        setCode(UUID.fromString(in.readString()));      // private UUID m_code;
        setName(in.readString());                       // private String m_name;
        // private Calendar m_birthday;
        long milli = in.readLong();
        if (milli == 0) {
            m_birthday = null;
        }
        else {
            setBirthday(milli);
        }
        setGender(in.readInt());                        // private int m_gender;
        setHeight(in.readInt());                        // private int m_height;
        setJob(in.readInt());                           // private int m_job;
        setWeight(in.readFloat());                      // private float m_weight;
        setGoalWeight(in.readFloat());                  // private float m_goalWeight;
        setDietPeriod(in.readInt());                    // private int m_dietPeriod;
        setDeviceName(in.readString());                 // private String deviceName;
        setDeviceAddress(in.readString());              // private String deviceAddress;
    }

    @SuppressWarnings("rawtypes")
    public static final Creator CREATOR = new Creator() {

        @Override
        public UserRecord createFromParcel(Parcel in) {
            return new UserRecord(in);
        }

        @Override
        public UserRecord[] newArray(int size) {
            return new UserRecord[size];
        }
    };

    public UserRecord() {
        empty();
    }

    public UserRecord(String email) {
        empty();
        m_email = email;
    }

    public UserRecord(Parcel in) {
        readFromParcel(in);
    }
}
