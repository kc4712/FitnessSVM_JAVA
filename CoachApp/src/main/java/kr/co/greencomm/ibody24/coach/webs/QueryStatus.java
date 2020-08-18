package kr.co.greencomm.ibody24.coach.webs;

/**
 * Created by young on 2015-09-04.
 */
public enum QueryStatus {
    // 성공
    OK,
	Success,
    // 오류
	Service_Error,

    ERROR_Query,
    ERROR_Web_Read,
    ERROR_Result_Parse,
    ERROR_Exists_Email,
    ERROR_Account_Not_Match,
    ERROR_Non_Information,

	ERROR_Exists_Action,

	Catch_Error,
	Non_Upgrade, Next_Upgrade;

    public static String convertQueryError(QueryStatus nErrorCode) {
        String errMessage = "";
        switch (nErrorCode) {
            case ERROR_Account_Not_Match:
                errMessage = "이메일 또는 비밀번호가 일치하지 않음";
                break;
            case ERROR_Exists_Email:
                errMessage = "이미 등록된 이메일";
                break;
            case ERROR_Query:
                errMessage = "웹 쿼리 과정에 오류";
                break;
            case ERROR_Result_Parse:
                errMessage = "쿼리 결과 파싱 과정에 오류";
                break;
            case ERROR_Web_Read:
                errMessage = "웹의 응답을 읽어들이는데 오류";
                break;
            case ERROR_Exists_Action:
                errMessage = "이미 등록된 행동 데이터";
                break;
            case ERROR_Non_Information:
                errMessage = "확인되지 않은 오류";
                break;
            default:
                errMessage = "자세한 내용을 정의하지 않은 오류";
                break;
        }

        return errMessage;
    }
}
