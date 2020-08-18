package kr.co.greencomm.middleware.tool;

import com.KIST.kistAART.IActionData;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/// <summary>
/// 하위 조건 검사 블럭을 AND 연산으로 묶는 최상의 불럭
/// </summary>
public abstract class BlockList extends XClass {
	// 공개 속성 및 메서드

	public ArrayList<CheckBlock> CheckBlocks;

	public boolean GetResult(IActionData action) {
		for (CheckBlock item : CheckBlocks) {
			if (item.GetResult(action) == false)
				return false;
		}
		return true;
	}

	// 생성자 및 복사

	public BlockList(Element ele) {
		super(ele);

		CheckBlocks = new ArrayList<CheckBlock>();
		NodeList ns = ele.getElementsByTagName("CheckBlock");
		for (int i = 0; i < ns.getLength(); i++) {
			CheckBlocks.add(new CheckBlock((Element) ns.item(i)));
		}
	}
}
