package kr.co.greencomm.middleware.video;

import java.util.HashMap;

import kr.co.greencomm.middleware.utils.MessageIndex;

/**
 * Created by jeyang on 2016-08-26.
 */
public class TotalScoreBase {
    protected HashMap<Integer, String> TotalScoreDic;

    protected TotalScoreBase() {
        TotalScoreDic = new HashMap<>();
    }

    protected String getCommentSection(int count_percent, int accuracy_percent) {
        String comment = "";
        int set;

        if (count_percent == 100 && accuracy_percent == 100) {
            return TotalScoreDic.get(MessageIndex.ALL_100.getIndex());
        }

        if (count_percent >= 90) { // 100~90
            set = 0x01;
        } else if (count_percent >= 60) { // 90~60
            set = 0x02;
        } else if (count_percent >= 30) { // 60~30
            set = 0x04;
        } else if (count_percent >= 20) { // 30~20
            set = 0x08;
        } else if (count_percent >= 10) { // 20~10
            set = 0x10;
        } else { // 10~
            set = 0x20;
        }

        if (accuracy_percent >= 90) { // 100~90
            set |= 0x40;
        } else if (accuracy_percent >= 60) { // 90~60
            set |= 0x80;
        } else if (accuracy_percent >= 30) { // 60~30
            set |= 0x100;
        } else if (accuracy_percent >= 20) { // 30~20
            set |= 0x200;
        } else if (accuracy_percent >= 10) { // 20~10
            set |= 0x400;
        } else { // 10~
            set |= 0x800;
        }
        set &= 0xffff;

        switch (set) {
            case (0x41): // 양쪽다 90 이상.
                return TotalScoreDic.get(MessageIndex.ALL_90_OVER.getIndex());
            case (0x82): // 양쪽다 90~60.
                return TotalScoreDic.get(MessageIndex.ALL_90_60_BETWEEN.getIndex());
            case (0x104): // 양쪽다 60~30.
                return TotalScoreDic.get(MessageIndex.ALL_60_30_BETWEEN.getIndex());
            case (0x208): // 양쪽다 30~20, 20~10
            case (0x210):
            case (0x408):
            case (0x410):
                return TotalScoreDic.get(MessageIndex.ALL_30_UNDER.getIndex());
            case (0x820): // 양쪽다 10미만
                return TotalScoreDic.get(MessageIndex.ALL_10_UNDER.getIndex());
            case (0x101): // 횟수 90이상, 정확도 60미만.
            case (0x201):
                return TotalScoreDic.get(MessageIndex.COUNT_90_OVER_ACC_20_UNDER.getIndex());
            case (0x81): // 횟수 90이상, 정확도 90~60.
                return TotalScoreDic.get(MessageIndex.COUNT_90_OVER_ACC_90_60_BETWEEN.getIndex());
            case (0x401): // 횟수 90이상, 정확도 20미만.
            case (0x801):
                return TotalScoreDic.get(MessageIndex.COUNT_90_OVER_ACC_20_UNDER.getIndex());
            case (0x102): // 횟수 90~60, 정확도 60~30.
                return TotalScoreDic.get(MessageIndex.COUNT_90_60_BETWEEN_ACC_60_30_BETWEEN.getIndex());
            case (0x202):// 횟수 90~60, 정확도 30미만.
            case (0x402):
            case (0x802):
                return TotalScoreDic.get(MessageIndex.COUNT_90_60_BETWEEN_ACC_30_UNDER.getIndex());
            case (0x204):// 횟수 60~30, 정확도 30미만.
            case (0x404):
            case (0x804):
                return TotalScoreDic.get(MessageIndex.COUNT_60_UNDER_ACC_30_UNDER.getIndex());
            case (0x808): // 횟수 30~10, 정확도 10미만.
            case (0x810):
                return TotalScoreDic.get(MessageIndex.COUNT_30_10_BETWEEN_ACC_10_UNDER.getIndex());
            case (0x44): // 정확도 90이상, 횟수 60미만.
            case (0x48):
                return TotalScoreDic.get(MessageIndex.ACC_90_OVER_COUNT_60_UNDER.getIndex());
            case (0x42): // 정확도 90이상, 횟수 90~60.
                return TotalScoreDic.get(MessageIndex.ACC_90_OVER_COUNT_90_60_BETWEEN.getIndex());
            case (0x50): // 정확도 90이상, 횟수 20미만.
            case (0x60):
                return TotalScoreDic.get(MessageIndex.ACC_90_OVER_COUNT_20_UNDER.getIndex());
            case (0x84): // 정확도 90~60, 횟수 60~30.
                return TotalScoreDic.get(MessageIndex.ACC_90_60_BETWEEN_COUNT_60_30_BETWEEN.getIndex());
            case (0x88): // 정확도 90~60, 횟수 30미만.
            case (0x90):
            case (0xa0):
                return TotalScoreDic.get(MessageIndex.ACC_90_60_BETWEEN_COUNT_30_UNDER.getIndex());
            case (0x108):// 정확도 60~30, 횟수 30미만.
            case (0x110):
            case (0x120):
                return TotalScoreDic.get(MessageIndex.ACC_60_UNDER_COUNT_30_UNDER.getIndex());
            case (0x220):// 정확도 30~10, 횟수 10미만.
            case (0x420):
                return TotalScoreDic.get(MessageIndex.ACC_30_10_BETWEEN_COUNT_10_UNDER.getIndex());
            default:
                break;
        }

        return comment;
    }
}
