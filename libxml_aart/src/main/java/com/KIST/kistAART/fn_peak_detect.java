package com.KIST.kistAART;

public class fn_peak_detect{

	//peak detection output
	

	public double acc_var_threshold = 0.3;	

	//m_peak_detect_output = fn_peak_detect.peak_detect(function_get_num, buffer_LP,  buffer_index_in_LP, chk_window_LP, 0 , noise_level_threshold);

	public peak_detect_output peak_detect(int function_get_num,
			double[][] buffer, int buffer_index_in, int chk_window, int acc_order, double noise_level_threshold) {

		//chk_window = 5;

		int peak = 0;
		double peak_value = 0;
		int time_index = 0;
		int  buffer_length = 0;
		int peak_candidate = 0;
		
		buffer_length = 2*chk_window+1 ;
		
		peak_candidate = buffer_index_in - chk_window;

		if (peak_candidate<1){
			
			peak_candidate = peak_candidate + 2*chk_window + 1 ;
			
		}


		// Check upper peaks
		for (int jj=0; jj<buffer_length; jj++){
			if (buffer[peak_candidate-1][acc_order] < buffer[jj][acc_order] )
			{
				break ;
			}

			if (jj==buffer_length-1){
				peak = 1 ; 
				peak_value = buffer[peak_candidate-1][acc_order] ;
				time_index = function_get_num - chk_window ;
			}
		}

		// Check lower peaks
		for (int jj=0; jj<buffer_length; jj++){
			if (buffer[peak_candidate-1][acc_order] > buffer[jj][acc_order] )
			{
				break ;
			}

			if (jj==buffer_length-1){
				peak = 2 ; 
				peak_value = buffer[peak_candidate-1][acc_order] ;
				time_index = function_get_num - chk_window ;
			}
		}
		
		peak_detect_output peak_detect_output_in_fn = new peak_detect_output();
		
		peak_detect_output_in_fn.peak = peak;
		peak_detect_output_in_fn.peak_value = peak_value;
		peak_detect_output_in_fn.time_index = time_index;

		return peak_detect_output_in_fn;

	}


}