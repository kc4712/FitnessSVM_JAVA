package com.KIST.kistAART;

public abstract class Property implements IActionData {
	protected static final int len = 100;
	protected static int counter[] = new int[len];
	protected static boolean flag[] = new boolean[len];
	protected static double summary[] = new double[len];
	protected static double global[] = new double[len];

	public final class X {
		public static final int peak = 1;
		public static final int time_index = peak + 1;
		public static final int peak_value = time_index + 1;
		public static final int peak_to_peak = peak_value + 1;
		public static final int surface = peak_to_peak + 1;
		public static final int amplitude = surface + 1;
		public static final int cross_peak_to_peak = amplitude + 1;
		public static final int acc_var = cross_peak_to_peak + 1;
		public static final int acc_smooth = acc_var + 1;
		public static final int acc_diff = acc_smooth + 1;
		public static final int surface_2 = acc_diff + 1;
	}

	public final class Y {
		public static final int peak = 21;
		public static final int time_index = peak + 1;
		public static final int peak_value = time_index + 1;
		public static final int peak_to_peak = peak_value + 1;
		public static final int surface = peak_to_peak + 1;
		public static final int amplitude = surface + 1;
		public static final int cross_peak_to_peak = amplitude + 1;
		public static final int acc_var = cross_peak_to_peak + 1;
		public static final int acc_smooth = acc_var + 1;
		public static final int acc_diff = acc_smooth + 1;
		public static final int surface_2 = acc_diff + 1;
	}

	public final class Z {
		public static final int peak = 41;
		public static final int time_index = peak + 1;
		public static final int peak_value = time_index + 1;
		public static final int peak_to_peak = peak_value + 1;
		public static final int surface = peak_to_peak + 1;
		public static final int amplitude = surface + 1;
		public static final int cross_peak_to_peak = amplitude + 1;
		public static final int acc_var = cross_peak_to_peak + 1;
		public static final int acc_smooth = acc_var + 1;
		public static final int acc_diff = acc_smooth + 1;
		public static final int surface_2 = acc_diff + 1;
	}

	public final class N {
		public static final int peak = 61;
		public static final int time_index = peak + 1;
		public static final int peak_value = time_index + 1;
		public static final int peak_to_peak = peak_value + 1;
		public static final int surface = peak_to_peak + 1;
		public static final int amplitude = surface + 1;
		public static final int cross_peak_to_peak = amplitude + 1;
		public static final int acc_var = cross_peak_to_peak + 1;
		public static final int acc_smooth = acc_var + 1;
		public static final int acc_diff = acc_smooth + 1;
		public static final int surface_2 = acc_diff + 1;
	}
	
	public final class Calculate {
		public final class Div {
			
		}
		
		public final class Sub {
			public static final int amplitudeX_amplitudeZ = 141;
		}
		
		public final class Add {
			public static final int amplitudeX_amplitudeZ = 161;
			public static final int peak_valueX_peak_valueZ = amplitudeX_amplitudeZ + 1;
		}
		
		public final class Mul {
			public static final int sqrt_amplitudeX_pow2_amplitudeZ_pow2 = 201;
			public static final int sqrt_acc_smoothX_pow2_acc_smoothZ_pow2 = sqrt_amplitudeX_pow2_amplitudeZ_pow2 + 1;
		}
		
		public final class Special {
			public static final int V_6_2 = 501;
			public static final int V_8_3_1 = V_6_2 + 1;
			public static final int V_8_3_2 = V_8_3_1 + 1;
			public static final int V_8_5 = V_8_3_2 + 1;
			public static final int V_8_7_1 = V_8_5 + 1;
			public static final int V_8_7_2 = V_8_7_1 + 1;
			public static final int V_9_7 = V_8_7_2 + 1;
			public static final int bufferY = V_9_7 + 1;
			
			public static final int V_6_2_PRE = bufferY + 1;
			public static final int V_8_3_PRE = V_6_2_PRE + 1;
			public static final int V_8_5_PRE = V_8_3_PRE + 1;
			public static final int V_8_7_PRE = V_8_5_PRE + 1;
			public static final int V_9_7_PRE = V_8_7_PRE + 1;
		}
	}
}
