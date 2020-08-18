package kr.co.greencomm.middleware.utils;

/**
 * Created by jeyang on 2016-09-08.
 */
public enum CourseLabel {
    Activity(9999910), Sleep(9999920), Daily(9999930), Stress(1111111);

    private final int label;
    CourseLabel(int label) {
        this.label = label;
    }

    public int getLabel() {
        return label;
    }

    public static int[] CoachCourse = new int[]{2, 3, 4, 101, 102, 201, 202, 301, 302};
}
