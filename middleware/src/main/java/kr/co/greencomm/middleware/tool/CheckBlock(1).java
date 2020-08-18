package kr.co.greencomm.middleware.tool;

import com.KIST.kistAART.IActionData;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/// <summary>
/// 논리 연산 블럭
/// 하위의 논리 검사 항목을 AND 또는 OR 조건으로 결합한다.
/// </summary>
public class CheckBlock extends XClass {
	// 공개 속성 및 메서드

	public ArrayList<CheckItem> List;
	public LogicalCombine Combine;
	

	public boolean GetResult(IActionData act) {
		for (CheckItem item : List) {
			if (Combine == LogicalCombine.AND && item.Check(act) == false)
				return false;
			if (Combine == LogicalCombine.OR && item.Check(act) == true)
				return true;
		}

		return (Combine == LogicalCombine.AND ? true : false);
	}

	// 생성자 및 복사

	public CheckBlock(Element ele) {
		super(ele);
		List = new ArrayList<CheckItem>();
		Combine = ConverterLC.ToBlockType(ele.getAttribute("Combine"));
		NodeList ns = ele.getElementsByTagName("CheckItem");
		for (int i = 0; i < ns.getLength(); i++) {
			Node node1 = ns.item(i);
			if (node1.getNodeType() == Node.ELEMENT_NODE) {
				List.add(new CheckItem((Element) node1));
			}
		}
	}
}
