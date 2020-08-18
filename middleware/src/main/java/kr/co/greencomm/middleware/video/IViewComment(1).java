package kr.co.greencomm.middleware.video;


public interface IViewComment{
	/**
	 * KIST 엔진에서 나온 결과물의 callback interface.
	 */
    void onStressInform();
    void onMainUi();

    void onTopComment();
    void onBottomComment();

    void onWarnning();

    void onTotalScore();
    void onExerData();

    void onShowUI();
}