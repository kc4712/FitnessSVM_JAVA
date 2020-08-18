package kr.co.greencomm.middleware.tool;

import org.w3c.dom.Element;

import com.KIST.kistAART.IActionData;

/// <summary>
/// 값 검사 항목
/// 지정된 인덱스를 사용하여 지정된 메서드의 값을 읽어서 지정된 값과 지정된 방법으로 비교하여 결과를 반환한다.
/// </summary>
public class CheckItem extends XClass {
	// 공개 속성 및 메서드

	public ValueSource Source;
	public int Index;
	public CompareOperator Operator;
	public double Value;

	/// <summary>
	/// 연산에 사용될 항목의 유형
	/// Value : double GetValue(int index) 메서드의 반환값
	/// Flag : double GetFlag(int index) 메서드의 반환값
	/// Counter : double GetCounter(int index) 메서드의 반환값
	/// Extend : double GetExtend(int index) 메서드의 반환값
	/// </summary>

	/// <summary>
	/// 행동 및 확장 데이터를 가져온다.
	/// </summary>
	private double GetValue(IActionData action) {
		if (action == null)
			return 0;
		switch (Source) {
		case Value:
			return action.GetValue(Index);
		case Flag:
			return action.GetFlag(Index);
		case Counter:
			return action.GetCounter(Index);
		case Extend:
			return action.GetExtend(Index);
		default:
			return 0;
		}
	}

	/// <summary>
	/// 최종 확인 결과를 반환한다.
	/// </summary>
	public boolean Check(IActionData action) {
		double src = GetValue(action);
		switch (Operator) {
		case Equal:
			return (src == Value);
		case NotEqual:
			return (src != Value);
		case LessThan:
			return (src < Value);
		case LessThanOrEqualTo:
			return (src <= Value);
		case GreaterThan:
			return (src > Value);
		case GreaterThanOrEqualTo:
			return (src >= Value);
		default:
			return false;
		}
	}

	// 생성자

	/// <summary>
	/// XML 요소의 값을 채널 클래스로 변환
	/// </summary>
	/// <param name="ele"></param>
	/// <returns></returns>
	public CheckItem(Element ele) {
		super(ele);
		Source = Converter.ToSource(ele.getAttribute("Source"));
		Index = Integer.parseInt(ele.getAttribute("Index"));
		Operator = ConverterCO.ToOperator(ele.getAttribute("Operator"));
		Value = Double.parseDouble(ele.getAttribute("Value"));
	}
}
