package kr.co.greencomm.middleware.tool;

public enum LogicalCombine {
	AND, OR,
}

class ConverterLC {
	public static LogicalCombine ToBlockType(String str) {
		if (str == null || str.isEmpty())
			return LogicalCombine.AND;
		switch (str.toUpperCase()) {
		case "OR":
			return LogicalCombine.OR;
		default:
			return LogicalCombine.AND;
		}
	}
}
