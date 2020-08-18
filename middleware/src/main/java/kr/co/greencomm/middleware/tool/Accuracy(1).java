package kr.co.greencomm.middleware.tool;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.KIST.kistAART.IActionData;

public class Accuracy extends XClass {
	// 공개 속성 및 메서드

	public boolean Multiply = false;

	public ArrayList<CalcBlock> CalcBlocks;

	public int Minimum = 0;
	public int Frequency = 0;
	private int m_now_freq = 0;

	public int Grade5 = 0;
	public int Grade4 = 0;
	public int Grade3 = 0;
	public int Grade2 = 0;
	public int Grade1 = 0;

	public boolean OutFlag5 = false;
	public boolean OutFlag4 = false;
	public boolean OutFlag3 = false;
	public boolean OutFlag2 = false;
	public boolean OutFlag1 = false;

	public int MsgNum5 = 0;
	public int MsgNum4 = 0;
	public int MsgNum3 = 0;
	public int MsgNum2 = 0;
	public int MsgNum1 = 0;
	
	public void ResetFrequency() {
		m_now_freq = 0;
	}
	
	public void IncrementFrequency() {
		if (Frequency > 0)
	    {
	        m_now_freq++;
	    }
	}
	
	public boolean ValidFrequency() {
	    if (Frequency == 0) return true;
	    if (m_now_freq >= Frequency) return true;
	    return false;
	}

	public double GetResult(IActionData action) {
		double ret = (Multiply ? 1 : 0);
		for (CalcBlock item : CalcBlocks) {
			if (Multiply) {
				ret *= item.GetResult(action);
			} else {
				ret += item.GetResult(action);
			}
		}
		return ret >= Minimum ? ret : 0;
	}
	
	private double GetResultNoMinimum(IActionData action) {
		double ret = (Multiply ? 1 : 0);
		for (CalcBlock item : CalcBlocks) {
			if (Multiply) {
				ret *= item.GetResult(action);
			} else {
				ret += item.GetResult(action);
			}
		}
		return ret;
	}

	public int GetGrade(IActionData action) {
		double acc = GetResultNoMinimum(action);
		if (Grade5 > 0 && acc >= Grade5)
			return 5;
		if (Grade4 > 0 && acc >= Grade4)
			return 4;
		if (Grade3 > 0 && acc >= Grade3)
			return 3;
		if (Grade2 > 0 && acc >= Grade2)
			return 2;
		if (Grade1 > 0 && acc >= Grade1)
			return 1;
		return 0;
	}

	// 생성자 및 복사

	public Accuracy(Element ele) {
		super(ele);
		Multiply = Boolean.parseBoolean(ele.getAttribute("Multiply"));
		Grade5 = Integer.parseInt(ele.getAttribute("Grade5"));
		Grade4 = Integer.parseInt(ele.getAttribute("Grade4"));
		Grade3 = Integer.parseInt(ele.getAttribute("Grade3"));
		Grade2 = Integer.parseInt(ele.getAttribute("Grade2"));
		Grade1 = Integer.parseInt(ele.getAttribute("Grade1"));
		Minimum = Integer.parseInt(ele.getAttribute("Minimum"));
		Frequency = Integer.parseInt(ele.getAttribute("Frequency"));
		OutFlag5 = Boolean.parseBoolean(ele.getAttribute("OutFlag5"));
		OutFlag4 = Boolean.parseBoolean(ele.getAttribute("OutFlag4"));
		OutFlag3 = Boolean.parseBoolean(ele.getAttribute("OutFlag3"));
		OutFlag2 = Boolean.parseBoolean(ele.getAttribute("OutFlag2"));
		OutFlag1 = Boolean.parseBoolean(ele.getAttribute("OutFlag1"));
		MsgNum5 = Integer.parseInt(ele.getAttribute("MsgNum5"));
		MsgNum4 = Integer.parseInt(ele.getAttribute("MsgNum4"));
		MsgNum3 = Integer.parseInt(ele.getAttribute("MsgNum3"));
		MsgNum2 = Integer.parseInt(ele.getAttribute("MsgNum2"));
		MsgNum1 = Integer.parseInt(ele.getAttribute("MsgNum1"));
		CalcBlocks = new ArrayList<CalcBlock>();
		NodeList ns = ele.getElementsByTagName("CalcBlock");
		for (int i = 0; i < ns.getLength(); i++) {
			CalcBlocks.add(new CalcBlock((Element) ns.item(i)));
		}
	}
}
