package kr.co.greencomm.middleware.tool;

import com.KIST.kistAART.IActionData;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/// <summary>
/// 유효한 행동인지 확인하는 체커
/// </summary>
public class Function extends BlockList {
	// 공개 속성 및 메서드

	public FunctionTarget Target;
	public int Index;
	public int Source;

	/// <summary>
	/// 처리 결과를 저장할 항목의 유형
	/// Flag : void SetFlag(int index, bool flag) 메서드로 저장
	/// Counter : void IncCounter(int index) 메서드로 저장
	/// </summary>

	/// <summary>
	/// 조건에 따라 사용사 함수를 실행하고 결과를 처리한다.
	/// </summary>
	public void Submit(IActionData action) {
		if (action == null)
			return;
		switch (Target) {
		case Counter:
			if (GetResult(action)) {
				action.IncCounter(Index);
			}
			return;
		case Flag:
			action.SetFlag(Index, GetResult(action));
			return;
		case Summary:
			if (GetResult(action)) {
				double val = action.GetValue(Source);
				action.Summary(Index, val);
			}
			return;
		default:
			return;
		}
	}

	/// <summary>
	/// 논리 연산 블럭의 내용으로 XML 요소 생성
	/// </summary>

	// 생성자

	public Function(Element ele) {
		super(ele);
		Target = ConverterFT.ToTarget(ele.getAttribute("Target"));
		Index = Integer.parseInt(ele.getAttribute("Index"));
		Source = Integer.parseInt(ele.getAttribute("Source"));
		CheckBlocks = new ArrayList<CheckBlock>();
		NodeList ns = ele.getElementsByTagName("CheckBlock");
		for (int i = 0; i < ns.getLength(); i++) {
			CheckBlocks.add(new CheckBlock((Element) ns.item(i)));
		}
	}
}
