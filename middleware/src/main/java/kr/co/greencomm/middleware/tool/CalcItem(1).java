package kr.co.greencomm.middleware.tool;

import com.KIST.kistAART.IActionData;

import org.w3c.dom.Element;

/// <summary>
/// 값 검사 항목
/// 지정된 인덱스를 사용하여 지정된 메서드의 값을 읽어서 지정된 값과 지정된 방법으로 비교하여 결과를 반환한다.
/// </summary>
public class CalcItem extends CalcBase {
	// 공개 속성 및 메서드

	public ValueSource Source;
	public int Index;

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
	protected double GetValue(IActionData action) {
		if (action == null)
			return 0;
		double ret = 0;

		switch (Source) {
		case Value:
			ret = action.GetValue(Index);
			break;
		case Flag:
			ret = action.GetFlag(Index);
			break;
		case Counter:
			ret = action.GetCounter(Index);
			break;
		case Summary:
			ret = action.GetSummary(Index);
			break;
		case Extend:
			ret = action.GetExtend(Index);
			break;
		default:
			return 0;
		}
		return ret;
	}

	// 생성자 및 복사

	public CalcItem(Element ele) {
		super(ele);

		Source = Converter.ToSource(ele.getAttribute("Source"));
		Index = Integer.parseInt(ele.getAttribute("Index"));
		Add = Double.parseDouble(ele.getAttribute("Add"));
		Mul = Double.parseDouble(ele.getAttribute("Mul"));
		Div = Double.parseDouble(ele.getAttribute("Div"));
	}

}
