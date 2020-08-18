package kr.co.greencomm.middleware.tool;

public enum ValueSource {
	None, Value, Flag, Counter, Summary, Extend,
}

class Converter {
	public static ValueSource ToSource(String attr) {
		if (attr == null || attr.isEmpty())
			return ValueSource.None;
		// if (string.IsNullOrEmpty(str)) return ValueSource.None;
		switch (attr.toUpperCase()) {
		case "VALUE":
			return ValueSource.Value;
		case "FLAG":
			return ValueSource.Flag;
		case "COUNTER":
			return ValueSource.Counter;
		case "SUMMARY":
			return ValueSource.Summary;
		case "EXTEND":
			return ValueSource.Extend;
		default:
			return ValueSource.None;
		}
	}

	public static boolean ConvertBool(String attr) {
		boolean ret = false;
		if (attr != null) {
			ret = Boolean.parseBoolean(attr);
		}
		return ret;
	}
}