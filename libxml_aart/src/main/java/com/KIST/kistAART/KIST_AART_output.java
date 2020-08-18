package com.KIST.kistAART;

public class KIST_AART_output extends Property {
	public static final String tag = KIST_AART_output.class.getSimpleName();

	public int peak_x = 0;
	public int time_index_x = 0;
	public double peak_value_x = 0;
	public int peak_to_peak_x = 0;
	public double surface_x = 0;
	public double amplitude_x = 0;
	public int cross_peak_to_peak_x = 0;
	public double acc_var_x = 0;
	public double acc_smooth_x = 0;
	public double acc_diff_x = 0;
	public double surface_x2 = 0;

	public int peak_y = 0;
	public int time_index_y = 0;
	public double peak_value_y = 0;
	public int peak_to_peak_y = 0;
	public double surface_y = 0;
	public double amplitude_y = 0;
	public int cross_peak_to_peak_y = 0;
	public double acc_var_y = 0;
	public double acc_smooth_y = 0;
	public double acc_diff_y = 0;
	public double surface_y2 = 0;

	public int peak_z = 0;
	public int time_index_z = 0;
	public double peak_value_z = 0;
	public int peak_to_peak_z = 0;
	public double surface_z = 0;
	public double amplitude_z = 0;
	public int cross_peak_to_peak_z = 0;
	public double acc_var_z = 0;
	public double acc_smooth_z = 0;
	public double acc_diff_z = 0;
	public double surface_z2 = 0;

	public int peak_norm = 0;
	public int time_index_norm = 0;
	public double peak_value_norm = 0;
	public int peak_to_peak_norm = 0;
	public double surface_norm = 0;
	public double amplitude_norm = 0;
	public int cross_peak_to_peak_norm = 0;
	public double acc_var_norm = 0;
	public double acc_smooth_norm = 0;
	public double acc_diff_norm = 0;
	public double surface_norm2 = 0;

	/** 저작도구 작업 **/
	@Override
	public double GetValue(int index) {
		double value = 0;
		switch (index) {
		case X.peak:
			value = peak_x;
			break;
		case X.time_index:
			value = time_index_x;
			break;
		case X.peak_value:
			value = peak_value_x;
			break;
		case X.peak_to_peak:
			value = peak_to_peak_x;
			break;
		case X.surface:
			value = surface_x;
			break;
		case X.amplitude:
			value = amplitude_x;
			break;
		case X.cross_peak_to_peak:
			value = cross_peak_to_peak_x;
			break;
		case X.acc_var:
			value = acc_var_x;
			break;
		case X.acc_smooth:
			value = acc_smooth_x;
			break;
		case X.acc_diff:
			value = acc_diff_x;
			break;
		case X.surface_2:
			value = surface_x2;
			break;

		case Y.peak:
			value = peak_y;
			break;
		case Y.time_index:
			value = time_index_y;
			break;
		case Y.peak_value:
			value = peak_value_y;
			break;
		case Y.peak_to_peak:
			value = peak_to_peak_y;
			break;
		case Y.surface:
			value = surface_y;
			break;
		case Y.amplitude:
			value = amplitude_y;
			break;
		case Y.cross_peak_to_peak:
			value = cross_peak_to_peak_y;
			break;
		case Y.acc_var:
			value = acc_var_y;
			break;
		case Y.acc_smooth:
			value = acc_smooth_y;
			break;
		case Y.acc_diff:
			value = acc_diff_y;
			break;
		case Y.surface_2:
			value = surface_y2;
			break;

		case Z.peak:
			value = peak_z;
			break;
		case Z.time_index:
			value = time_index_z;
			break;
		case Z.peak_value:
			value = peak_value_z;
			break;
		case Z.peak_to_peak:
			value = peak_to_peak_z;
			break;
		case Z.surface:
			value = surface_z;
			break;
		case Z.amplitude:
			value = amplitude_z;
			break;
		case Z.cross_peak_to_peak:
			value = cross_peak_to_peak_z;
			break;
		case Z.acc_var:
			value = acc_var_z;
			break;
		case Z.acc_smooth:
			value = acc_smooth_z;
			break;
		case Z.acc_diff:
			value = acc_diff_z;
			break;
		case Z.surface_2:
			value = surface_z2;
			break;

		case N.peak:
			value = peak_norm;
			break;
		case N.time_index:
			value = time_index_norm;
			break;
		case N.peak_value:
			value = peak_value_norm;
			break;
		case N.peak_to_peak:
			value = peak_to_peak_norm;
			break;
		case N.surface:
			value = surface_norm;
			break;
		case N.amplitude:
			value = amplitude_norm;
			break;
		case N.cross_peak_to_peak:
			value = cross_peak_to_peak_norm;
			break;
		case N.acc_var:
			value = acc_var_norm;
			break;
		case N.acc_smooth:
			value = acc_smooth_norm;
			break;
		case N.acc_diff:
			value = acc_diff_norm;
			break;
		case N.surface_2:
			value = surface_norm2;
			break;
			
		case Calculate.Sub.amplitudeX_amplitudeZ:
			value = amplitude_x - amplitude_z;
			break;
		case Calculate.Add.amplitudeX_amplitudeZ:
			value = amplitude_x + amplitude_z;
			break;
		case Calculate.Add.peak_valueX_peak_valueZ:
			value = peak_value_x + peak_value_z;
			break;
		case Calculate.Mul.sqrt_acc_smoothX_pow2_acc_smoothZ_pow2:
			value = Math.sqrt(acc_smooth_x * acc_smooth_x + acc_smooth_z * acc_smooth_z);
			break;
		case Calculate.Mul.sqrt_amplitudeX_pow2_amplitudeZ_pow2:
			value = Math.sqrt(amplitude_x * amplitude_x + amplitude_z * amplitude_z);
			break;
		case Calculate.Special.V_6_2:
			value = v_6_2();
			break;
		case Calculate.Special.V_8_3_1:
			value = v_8_3_1();
			break;
		case Calculate.Special.V_8_3_2:
			value = v_8_3_2();
			break;
		case Calculate.Special.V_8_5:
			value = v_8_5();
			break;
		case Calculate.Special.V_8_7_1:
			value = v_8_7_1();
			break;
		case Calculate.Special.V_8_7_2:
			value = v_8_7_2();
			break;
		case Calculate.Special.V_9_7:
			value = v_9_7();
			break;
		case Calculate.Special.bufferY:
			value = bufferY();
			break;
		case Calculate.Special.V_6_2_PRE:
			v_6_2_pre();
			break;
		case Calculate.Special.V_8_3_PRE:
			v_8_3_pre();
			break;
		case Calculate.Special.V_8_5_PRE:
			v_8_5_pre();
			break;
		case Calculate.Special.V_8_7_PRE:
			v_8_7_pre();
			break;
		case Calculate.Special.V_9_7_PRE:
			v_9_7_pre();
			break;
		}

		return value;
	}

	/** 이하 메서드들은 나중에 정리가 필요함. 삭제하거나, 수정하여 재사용가능한 형태로 만들거나...**/
	public void v_6_2_pre() {
//		System.out.println(String.format("6_2-> var z[%f] var x[%f] diffy[%f] smoothz[%f] smoothx[%f]",
//				acc_var_z, acc_var_x, acc_diff_y, acc_smooth_z, acc_smooth_x));
		if ((acc_var_z + acc_var_x <= 100) && (acc_diff_y < 0) && (acc_smooth_z + acc_smooth_x > 6)) {
			Property.global[0] = (Property.global[1] + 1);
		} else if ((acc_smooth_z + acc_smooth_x <= 0) && (Property.global[2] + acc_smooth_x > 0)) {
			Property.global[0] = 0;
		} else {
			Property.global[0] = Property.global[1];
		}

		if ((Property.global[0] == 0) && (Property.global[1] > 0)) {
			Property.global[3] = Property.global[1];
		}

		Property.global[1] = Property.global[0];
		Property.global[2] = acc_smooth_z + acc_smooth_x;
	}
	
	public double v_6_2() {
		return Property.global[3];
	}
	
	/** 8_3_1 호출 뒤, 8_3_2 써야함 **/
	public void v_8_3_pre() {
		if (acc_smooth_z > 9) {
			Property.global[0] = Property.global[1] + 0.9;
			Property.global[2] = Property.global[3] + 1;
		} else if (acc_smooth_z > 8) {
			Property.global[0] = Property.global[1] + 0.8;
			Property.global[2] = Property.global[3] + 1;
		} else if (acc_smooth_z > 7) {
			Property.global[0] = Property.global[1] + 0.7;
			Property.global[2] = Property.global[3] + 1;
		} else if (acc_smooth_z > 6) {
			Property.global[0] = Property.global[1] + 0.6;
			Property.global[2] = Property.global[3] + 1;
		} else if ((acc_smooth_z > 0) && (Property.global[4] < 0)) {
			Property.global[0] = 0;
			Property.global[2] = 0;
		} else {
			Property.global[0] = Property.global[1];
			Property.global[2] = Property.global[3];
		}

		if ((Property.global[0] == 0) && (Property.global[1] > 0)) {
			Property.global[5] = Property.global[1];
			Property.global[6] = Property.global[3];
		}
		Property.global[1] = Property.global[0];
		Property.global[3] = Property.global[0];
		Property.global[4] = acc_smooth_z;
	}
	
	public double v_8_3_1() {
		return Property.global[5];
	}
	
	public double v_8_3_2() {
		return Property.global[6];
	}
	
	public void v_8_5_pre() {
		if ((acc_var_z <= 50) && (acc_diff_y < 0) && (acc_smooth_z > 3)) {
			Property.global[0] = (Property.global[1] + 1);
//			System.out.println(String.format("count[%f] var z[%f] diffy[%f] smoothz[%f]",
//					Property.global[0], acc_var_z, acc_diff_y, acc_smooth_z));
		} else if ((acc_smooth_z <= 0) && (Property.global[2] > 0)) {
//			System.out.println(String.format("count[%f] glo2[%f] smoothz[%f]",
//					Property.global[0], Property.global[2], acc_smooth_z));
			Property.global[0] = 0;
		} else {
			Property.global[0] = Property.global[1];
		}

		if ((Property.global[0] == 0) && (Property.global[1] > 0)) {
			Property.global[3] = Property.global[1];
		}

		Property.global[1] = Property.global[0];
		Property.global[2] = acc_smooth_z;
	}
	
	public double v_8_5() {
		return Property.global[3];		
	}
	
	public void v_8_7_pre() {
		if ((Math.sqrt(acc_smooth_x * acc_smooth_x + acc_smooth_z * acc_smooth_z) > 6) && (acc_smooth_y > 4)) {
			Property.global[0] = (Property.global[1] + 1);
		} else if (acc_smooth_y < 0) {
			Property.global[0] = 0;
		} else {
			Property.global[0] = Property.global[1];
		}

		if ((Property.global[0] == 0) && (Property.global[1] > 0))
			Property.global[2] = Property.global[1];
		else {
			Property.global[2] = Property.global[3];
		}
		Property.global[3] = Property.global[2];
		Property.global[1] = Property.global[0];

		if (peak_y == 1 && amplitude_y >= 6) {
			Property.global[4] = amplitude_y;
		}
	}

	public double v_8_7_1() {
		/** 로직을 그냥 넣음 **/
		return Property.global[4] == 0 ? 5 : Property.global[4] / 20;		
	}
	
	public double v_8_7_2() {
		return Property.global[2];
	}
	
	public void v_9_7_pre() {
		if (acc_smooth_y > 2) {
			Property.global[0] = Property.global[1] + 1;
		} else {
			Property.global[0] = 0;
		}
		if (Property.global[0] == 0 && Property.global[1] > 0) {
			Property.global[2] = Property.global[1];
		}
		Property.global[1] = Property.global[0];
	}
	
	public double v_9_7() {
		return Property.global[2];		
	}
	
	public double bufferY() {
		double pre_acc_smooth_y = Property.global[99];
		Property.global[99] = acc_smooth_y;
		return pre_acc_smooth_y;
	}
	
	@Override
	public void ResetGlobal() {
		for (int i = 0; i < len; i++) {
			Property.global[i] = 0;
		}
	}
	/** 메서드 끝 **/

	private boolean valid(int index) {
		if (index > len - 1 || index < 0)
			return false;

		return true;
	}

	@Override
	public void SetFlag(int index, boolean flag) {
		if (!valid(index))
			return;
		
		Property.flag[index] = flag;
	}

	@Override
	public double GetFlag(int index) {
		if (!valid(index))
			return 0;

		return Property.flag[index] == true ? 1 : 0;
	}

	@Override
	public void IncCounter(int index) {
		if (!valid(index))
			return;

		Property.counter[index]++;
	}

	@Override
	public double GetCounter(int index) {
		if (!valid(index))
			return 0;

		return Property.counter[index];
	}

	@Override
	public double GetExtend(int index) {
		/*
		 * if(!valid(index)) return 0;
		 */
		return 0;
	}

	@Override
	public void ResetFlag() {
		for (int i = 0; i < len; i++) {
			Property.flag[i] = false;
		}
	}

	@Override
	public void ResetCounter() {
		for (int i = 0; i < len; i++) {
			Property.counter[i] = 0;
		}
	}

	@Override
	public void Reset() {
		ResetCounter();
		ResetFlag();
		ResetSummary();
		//ResetGlobal();
	}

	@Override
	public void ResetSummary() {
		for (int i = 0; i < len; i++) {
			Property.summary[i] = 0;
		}
	}

	@Override
	public void Summary(int index, double val) {
		if (!valid(index))
			return;

		Property.summary[index] += val;
	}

	@Override
	public double GetSummary(int index) {
		if (!valid(index))
			return 0;

		return Property.summary[index];
	}
}