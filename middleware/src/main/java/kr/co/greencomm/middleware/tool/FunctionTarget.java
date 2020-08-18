package kr.co.greencomm.middleware.tool;

public enum FunctionTarget {
	None, Flag, Counter, ResetCounter, Summary,
}

class ConverterFT {
	public static FunctionTarget ToTarget(String attr) {
		if (attr == null || attr.isEmpty())
			return FunctionTarget.None;
		switch (attr.toUpperCase()) {
		case "FLAG":
			return FunctionTarget.Flag;
		case "COUNTER":
			return FunctionTarget.Counter;
		case "RESETCOUNTER":
			return FunctionTarget.ResetCounter;
		case "SUMMARY":
			return FunctionTarget.Summary;
		default:
			return FunctionTarget.None;
		}
	}
}
