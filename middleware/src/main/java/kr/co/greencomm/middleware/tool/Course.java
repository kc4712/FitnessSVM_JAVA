package kr.co.greencomm.middleware.tool;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/// <summary>
/// 유효한 행동인지 확인하는 체커
/// </summary>
public class Course extends XClass {
	// 공개 속성 및 메서드

	public int Code;
	public String Name;
	public String Remark;
	public double InfoOn;
	public double InfoOff;
	public double OverallStart;
	public double OverallDuration;
	public int TotalCount;
	public ArrayList<Action> ActionList;


	public double getStart() {
		if (ActionList.size() == 0)
			return 0;
		double min = 9999;
		for (Action action : ActionList) {
			if (action.Start < min)
				min = action.Start;
		}

		return min;
	}

	public double getEnd() {
		if (ActionList.size() == 0)
			return 0;
		double max = 0;
		for (Action action : ActionList) {
			if (action.End > max)
				max = action.End;
		}

		return max;
	}

	public double getDuration() {
		return getEnd() - getStart();
	}

	public double getOverallEnd() {
		return OverallStart + OverallDuration;
	}

	public Action find(double sec) {
		Action ret = null;
		for (Action item2 : ActionList) {
			if (item2.Start <= sec && item2.End >= sec) {
				ret = item2;
				return ret;
			}
		}

		return ret;
	}
	// 생성자

	public Course(Element ele) {
		super(ele);
		Code = Integer.parseInt(ele.getAttribute("Code"));
		Name = ele.getAttribute("Name");
		Remark = ele.getAttribute("Remark");
		InfoOn = Double.parseDouble(ele.getAttribute("InfoOn"));
		InfoOff = Double.parseDouble(ele.getAttribute("InfoOff"));
		OverallStart = Double.parseDouble(ele.getAttribute("OverallStart"));
		OverallDuration = Double.parseDouble(ele.getAttribute("OverallDuration"));
		TotalCount = Integer.parseInt(ele.getAttribute("TotalCount"));

		ActionList = new ArrayList<Action>();
		NodeList ns = ele.getElementsByTagName("Action");
		for (int i = 0; i < ns.getLength(); i++) {
			ActionList.add(new Action((Element) ns.item(i)));
		}
	}
}
