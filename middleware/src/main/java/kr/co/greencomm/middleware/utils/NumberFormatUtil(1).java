package kr.co.greencomm.middleware.utils;


public final class NumberFormatUtil { 
	public static float convertPoint(float input) {
		return Float.parseFloat(String.format("%.2f",input)); 
	}
	
	public static double convertPoint(double input) {
		return Double.parseDouble(String.format("%.2f",input)); 
	}
}