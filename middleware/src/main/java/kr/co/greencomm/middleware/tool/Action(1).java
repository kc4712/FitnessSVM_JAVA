package kr.co.greencomm.middleware.tool;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.KIST.kistAART.IActionData;

public class Action extends XClass {
	// 공개 속성 및 메서드

	public double Start;
	public double End;
	public String Remark;

	public ArrayList<Function> PreFunctions;
	public ArrayList<CheckBlock> ActionChecks;
	public Accuracy Accuracy;

	public double getDuration() {
		return End - Start;
	}

	public void SubmitFunctions(IActionData action) {
		for (Function func : PreFunctions) {
			func.Submit(action);
		}
	}

	public boolean GetCheckResult(IActionData action) {
		if (Accuracy == null) return false;
		Accuracy.IncrementFrequency();
		if (Accuracy.ValidFrequency() == false) return false;
		
		for (CheckBlock item : ActionChecks) {
			if (item.GetResult(action) == false)
				return false;
		}
		return true;
	}

	// 생성자 및 복사
	public Action(Element ele) {
		super(ele);
		Start = Double.parseDouble(ele.getAttribute("Start"));
		End = Double.parseDouble(ele.getAttribute("End"));
		Remark = ele.getAttribute("Remark");

		PreFunctions = new ArrayList<Function>();
		NodeList nl1 = ele.getElementsByTagName("PreFunctions");
		if (nl1.getLength() > 0) {
			Node nn1 = nl1.item(0);
			NodeList ns1 = nn1.getChildNodes();
			if (ns1.getLength() > 0) {
				for (int i = 0; i < ns1.getLength(); i++) {
					Node mm1 = ns1.item(i);
					if (mm1.getNodeType() == Node.ELEMENT_NODE) {
						Element ele1 = (Element) mm1;
						PreFunctions.add(new Function(ele1));
					}
				}
			}
		}

		ActionChecks = new ArrayList<CheckBlock>();
		NodeList nl2 = ele.getElementsByTagName("ActionChecks");
		if (nl2.getLength() > 0) {
			Node nn2 = nl2.item(0);
			NodeList ns2 = nn2.getChildNodes();
			if (ns2.getLength() > 0) {
				for (int i = 0; i < ns2.getLength(); i++) {
					Node mm2 = ns2.item(i);
					if (mm2.getNodeType() == Node.ELEMENT_NODE) {
						Element ele2 = (Element) mm2;
						ActionChecks.add(new CheckBlock(ele2));
					}
				}
			}
		}

		NodeList ns3 = ele.getElementsByTagName("Accuracy");
		if (ns3.getLength() > 0) {
			Accuracy = new Accuracy((Element) ns3.item(0));
		}
	}
}
