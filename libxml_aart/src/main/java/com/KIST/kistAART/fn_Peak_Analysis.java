package com.KIST.kistAART;

public class fn_Peak_Analysis{


	public double acc_var_threshold = 0.3;	

	//m_peak_detect_output = fn_peak_detect.peak_detect(function_get_num, buffer_LP,  buffer_index_in_LP, chk_window_LP, 0 , noise_level_threshold);

	public peak_analysis_output Peak_Analysis(int peak, double peak_value, int time_index, double[] prev_local_upper_peak, double [] prev_local_lower_peak){

		
		
		
		int peak_to_peak = 0 ;
	    int cross_peak_to_peak = 0 ;
	    double surface = 0 ;
	    double amplitude = 0 ;
	    double surface2 = 0 ;


	    // Upper Peak case
	    if (peak==1){
	    
	        // Calulate peak-to-peak time
	        peak_to_peak = time_index - (int)prev_local_upper_peak[0] ;
	        
	        // Calculate cross peak-to-peak time
	        cross_peak_to_peak = (time_index - (int)prev_local_lower_peak[0]) ;
	        
	        // Calculate Surface
	        surface = Math.abs( ( time_index - prev_local_lower_peak[0] )*( peak_value - prev_local_lower_peak[1] )/2 ) ;
	        surface2 = Math.abs( ( time_index - prev_local_lower_peak[2] )*( peak_value - prev_local_lower_peak[3] )/2 ) ;
	        
	        // Calculate Amplitude from previous lower peak
	        amplitude = Math.abs( peak_value - prev_local_lower_peak[1] ) ;
	        
//	        // update previous local upper peak info.
//	        prev_local_upper_peak[0]= time_index;
//    		prev_local_upper_peak[1]= peak_value;        

	        
	    // Lower Peak case
	    }
	    else if (peak==2){

	        // Calulate peak-to-peak time
	        peak_to_peak = -(time_index - (int)prev_local_lower_peak[0]) ;
	        
	        // Calculate cross peak-to-peak time
	        cross_peak_to_peak = -(time_index - (int)prev_local_upper_peak[0] );
	        
	        // Calculate Surface
	        surface = -Math.abs( ( time_index - prev_local_upper_peak[0] )*( prev_local_upper_peak[1] - peak_value )/2 ) ;
	        surface2 = -Math.abs( ( time_index - prev_local_upper_peak[2] )*( prev_local_upper_peak[3] - peak_value )/2 ) ;
	                    
	        // Calculate Amplitude from previous lower peak
	        amplitude = -Math.abs( peak_value - prev_local_upper_peak[1] ) ;
	        
	        // update previous local upper peak info.
//	        prev_local_lower_peak = [ time_index   peak_value ]' ;        
	        
	    }
		
		peak_analysis_output peak_analysis_output_in = new peak_analysis_output();
		
		peak_analysis_output_in.amplitude = amplitude;
		peak_analysis_output_in.cross_peak_to_peak = cross_peak_to_peak;
		peak_analysis_output_in.peak = peak;
		peak_analysis_output_in.peak_to_peak = peak_to_peak;
		peak_analysis_output_in.peak_value = peak_value;
		peak_analysis_output_in.surface = surface;
		peak_analysis_output_in.time_index = time_index;
		peak_analysis_output_in.surface2 = surface2;
		
		return peak_analysis_output_in;
	}
}