package kr.co.greencomm.middleware.tool;

public enum CompareOperator {
	None, Equal, NotEqual, LessThan, LessThanOrEqualTo, GreaterThan, GreaterThanOrEqualTo,
}

class ConverterCO {
	/// <summary>
	/// 비교 연산자를 문자열로 변환하는 메서드
	/// Enum 형식에는 별도의 메서드 작성이 불가능하기에
	/// 확장 메서드 형식으로 메서드를 추가한다.
	/// </summary>

	/// <summary>
	/// 문자열로 표현된 비교 연산자를 Enum 형식의 비교 연산자로 변환하는 메서드
	/// Enum 형식에는 별도의 메서드 작성이 불가능하기에
	/// 확장 메서드 형식으로 메서드를 추가한다.
	/// </summary>
	public static CompareOperator ToConvert(String coStr) {
		switch (coStr.toUpperCase()) {
		case "EQ":
			return CompareOperator.Equal;
		case "NE":
			return CompareOperator.NotEqual;
		case "LT":
			return CompareOperator.LessThan;
		case "LE":
			return CompareOperator.LessThanOrEqualTo;
		case "GT":
			return CompareOperator.GreaterThan;
		case "GE":
			return CompareOperator.GreaterThanOrEqualTo;
		default:
			return CompareOperator.None;
		}
	}

	/// <summary>
	/// 문자열로 표현된 비교 연산자를 Enum 형식의 비교 연산자로 변환하는 메서드
	/// Enum 형식에는 별도의 메서드 작성이 불가능하기에
	/// 확장 메서드 형식으로 메서드를 추가한다.
	/// </summary>
	public static CompareOperator ToOperator(String attr) {
		if (attr == null || attr.isEmpty())
			return CompareOperator.None;
		switch (attr.toUpperCase()) {
		case "EQ":
			return CompareOperator.Equal;
		case "NE":
			return CompareOperator.NotEqual;
		case "LT":
			return CompareOperator.LessThan;
		case "LE":
			return CompareOperator.LessThanOrEqualTo;
		case "GT":
			return CompareOperator.GreaterThan;
		case "GE":
			return CompareOperator.GreaterThanOrEqualTo;
		default:
			return CompareOperator.None;
		}
	}
}
