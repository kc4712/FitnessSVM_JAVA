package kr.co.greencomm.middleware.tool;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/// <summary>
/// 유효한 행동인지 확인하는 체커
/// </summary>
public class Program extends XClass {
    // 공개 속성 및 메서드
    private static final String tag = Program.class.getSimpleName();

    public int Code;
    public String Name;
    public ArrayList<Course> CourseList;
    public ArrayList<Message> MessageList;

    // 생성자 및 복사

    public Program(Element ele) {
        super(ele);
        Code = Integer.parseInt(ele.getAttribute("Code"));
        Name = ele.getAttribute("Name");

        CourseList = new ArrayList<Course>();
        NodeList ns = ele.getElementsByTagName("Course");
        for (int i = 0; i < ns.getLength(); i++) {
            CourseList.add(new Course((Element) ns.item(i)));
        }

        MessageList = new ArrayList<>();
        NodeList ns2 = ele.getElementsByTagName("Message");
        for (int i = 0; i < ns2.getLength(); i++) {
            MessageList.add(new Message((Element) ns2.item(i)));
        }
    }

    public Course find(double sec) {
        Course ret = null;
        for (Course item : CourseList) {
            if (item.getStart() <= sec && item.getEnd() >= sec) {
                return item;
            }
        }
        return ret;
    }

    public UiDisplay getUiDisplay(int sec) {
        for (Course c : CourseList) {
            if ((c.getStart() + c.InfoOn) <= sec && (c.getEnd() + c.InfoOff) >= sec) {
                return new UiDisplay(true, (c.getStart() + 5) >= sec ? c.Name : "");
            }
        }

        return new UiDisplay(false, "");
    }

    public TotalScoreDisplay getToTalScoreDisplay(int sec) {
        for (Course c: CourseList) {
            if ((c.getEnd() + c.OverallStart) == sec) {
                return new TotalScoreDisplay(true, c.OverallDuration);
            }
        }

        return new TotalScoreDisplay(false, 0d);
    }
}
