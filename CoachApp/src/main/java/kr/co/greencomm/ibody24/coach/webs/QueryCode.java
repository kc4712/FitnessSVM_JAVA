package kr.co.greencomm.ibody24.coach.webs;

/**
 * Created by young on 2015-09-04.
 */
public enum QueryCode {
    CreateUser,
    LoginUser,
    SetDevice,
    SetProfile,
    SetTarget,

    InsertExercise, InsertExercise2, ExerciseToday, WeekData, YearData,
    ListExercise, ListCalorieToday,

    ListStep, InsertStep,

    CheckVersion,
    // 이하는 실제 쿼리에 들어가는 코드는 아니고, 내부에서 구분하기 위한 코드.
    InsertCoach, InsertFitness,
    GetFirmware,
    DownLoadFile
}

enum QueryResult {
    success,
    fail,
    invalid
}