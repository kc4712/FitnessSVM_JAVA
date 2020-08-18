package kr.co.greencomm.ibody24.coach.data;


/**
 * Created by young on 2015-10-28.
 */
public interface FirmUpListener {
    void firmUp(boolean success, String path);

    // 기존 펌웨어 업데이트 코드 주석처리.
    //void firmUp(boolean success);
}
