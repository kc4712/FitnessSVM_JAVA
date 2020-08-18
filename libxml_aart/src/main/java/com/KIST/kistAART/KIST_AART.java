///////////////////////////////////////////////////////////
// KIST pilates API development by Beomju Shin
// 3. 28. 2015 

package com.KIST.kistAART;

public class KIST_AART  {

	public int function_get_num = 0;
	boolean before_jumping = false;


	// Variabels
	int data_smoothing_window = 10 ;

	//////////////////////////////////////
	// Local Peak related
	int chk_window_LP = 5;
	double acc_var_x = 0;
	double acc_var_y = 0;
	double acc_var_z = 0;
	double acc_var_norm = 0;
	double [][] buffer_LP = new double[2*chk_window_LP + 1][4];
	int buffer_index_in_LP = 1;
	int buffer_length_LP = 2*chk_window_LP + 1;
	
	double [][] diff_buffer = new double[buffer_length_LP][4];
	double [][] smooth_buffer = new double[buffer_length_LP][4];
	double [][] var_buffer = new double[buffer_length_LP][4];
	int buffer_index = buffer_length_LP-4;
	int buffer_index2 = buffer_index-2;
	double dt=0.05; 
	
	double noise_level_threshold = 0.03;

	double threshold = 10.3;

	

	/////////////////////////////////////////////////////
	peak_detect_output m_peak_detect_output = new peak_detect_output();
	peak_analysis_output m_peak_analysis_output = new peak_analysis_output();

	double [] acc_raw = new double[3];
	double acc_norm = 0;
	double [] acc_raw_smoothing = new double[3];
	double acc_norm_smoothing = 0;

	double acc_mean = 0;
	double acc_var = 0;

	double []avg_acc_noise_threshold = new double[2];

	double [] prev_local_upper_peak_x = new double[4];
	double [] prev_local_lower_peak_x = new double[4];
	double [] prev_local_upper_peak_y = new double[4];
	double [] prev_local_lower_peak_y = new double[4];
	double [] prev_local_upper_peak_z = new double[4];
	double [] prev_local_lower_peak_z = new double[4];
	double [] prev_local_upper_peak_norm = new double[4];
	double [] prev_local_lower_peak_norm = new double[4];


	fn_peak_detect m_fn_peak_detect = new fn_peak_detect();
	fn_Peak_Analysis m_fn_peak_Analysis = new fn_Peak_Analysis();

	KIST_AART_output m_KIST_AART_output = new KIST_AART_output();

	/////////////////////////////////////////////////////////////////////////////////////////////////
	//Function start
	public KIST_AART_output fn_AART_Cal_parameter(double[] acc_data) {

		// TODO Auto-generated method stub

		function_get_num++;


		acc_raw[0] = acc_data[0];
		acc_raw[1] = acc_data[1];
		acc_raw[2] = acc_data[2];

		//Acc. Norm Calculation
		acc_norm = Math.sqrt(acc_raw[0]*acc_raw[0] + acc_raw[1]*acc_raw[1] + acc_raw[2]*acc_raw[2]);

		//Acc. data smoothing
		if(function_get_num < data_smoothing_window){
			
			acc_raw_smoothing[0] =  acc_raw_smoothing[0]*(function_get_num-1)/function_get_num + acc_raw[0]/function_get_num ;
			acc_raw_smoothing[1] =  acc_raw_smoothing[1]*(function_get_num-1)/function_get_num + acc_raw[1]/function_get_num ;
			acc_raw_smoothing[2] =  acc_raw_smoothing[2]*(function_get_num-1)/function_get_num + acc_raw[2]/function_get_num ;
			
			acc_norm_smoothing =  acc_norm_smoothing*(function_get_num-1)/function_get_num + acc_norm/function_get_num ;
			
			
			
			
		}else{
			
			acc_raw_smoothing[0] =  acc_raw_smoothing[0]*(data_smoothing_window-1)/data_smoothing_window + acc_raw[0]/data_smoothing_window ;
			acc_raw_smoothing[1] =  acc_raw_smoothing[1]*(data_smoothing_window-1)/data_smoothing_window + acc_raw[1]/data_smoothing_window ;
			acc_raw_smoothing[2] =  acc_raw_smoothing[2]*(data_smoothing_window-1)/data_smoothing_window + acc_raw[2]/data_smoothing_window ;
			
			acc_norm_smoothing =  acc_norm_smoothing*(data_smoothing_window-1)/data_smoothing_window + acc_norm/data_smoothing_window ;	
		}
		
		

		//Acc mean and variance
		acc_mean =  acc_mean*(buffer_length_LP-1)/buffer_length_LP + acc_norm/buffer_length_LP ;

		acc_var = acc_var*(buffer_length_LP-1)/buffer_length_LP +  ( (acc_mean-acc_norm)*(acc_mean-acc_norm) ) / buffer_length_LP;

		
		
		//Update buffer index

		if( buffer_index_in_LP>buffer_length_LP){

			buffer_index_in_LP = 1 ;
		}

		//data buffer save
		buffer_LP[ buffer_index_in_LP-1 ][0] = acc_raw_smoothing[0];
		buffer_LP[ buffer_index_in_LP-1 ][1] = acc_raw_smoothing[1];
		buffer_LP[ buffer_index_in_LP-1 ][2] = acc_raw_smoothing[2];
		buffer_LP[ buffer_index_in_LP-1 ][3] = acc_norm_smoothing;

		if( buffer_index>buffer_length_LP){

			buffer_index = 1 ;
		}
		
		if(  buffer_index-2<0){

			buffer_index2 = buffer_length_LP-1 ;
		}
		else{

			buffer_index2 = buffer_index-2 ;
		}
		
		//buffer var		
		var_buffer[buffer_index_in_LP-1][0] = acc_var_x*(buffer_length_LP-1)/buffer_length_LP +  ( (acc_raw_smoothing[0]-acc_raw[0])*(acc_raw_smoothing[0]-acc_raw[0]) ) / buffer_length_LP;
		var_buffer[buffer_index_in_LP-1][1] = acc_var_y*(buffer_length_LP-1)/buffer_length_LP +  ( (acc_raw_smoothing[1]-acc_raw[1])*(acc_raw_smoothing[1]-acc_raw[1]) ) / buffer_length_LP;
		var_buffer[buffer_index_in_LP-1][2] = acc_var_z*(buffer_length_LP-1)/buffer_length_LP +  ( (acc_raw_smoothing[2]-acc_raw[2])*(acc_raw_smoothing[2]-acc_raw[2]) ) / buffer_length_LP;
		var_buffer[buffer_index_in_LP-1][3] = acc_var_norm*(buffer_length_LP-1)/buffer_length_LP +  ( (acc_norm_smoothing-acc_norm)*(acc_norm_smoothing-acc_norm) ) / buffer_length_LP;
				
		//buffer smooth
		smooth_buffer[buffer_index_in_LP-1][0] = acc_raw_smoothing[0];
		smooth_buffer[buffer_index_in_LP-1][1] = acc_raw_smoothing[1];
		smooth_buffer[buffer_index_in_LP-1][2] = acc_raw_smoothing[2];
		smooth_buffer[buffer_index_in_LP-1][3] = acc_norm_smoothing;
			
		if(function_get_num>4)
		{
		//data var
			m_KIST_AART_output.acc_var_x = var_buffer[buffer_index-1][0];
			m_KIST_AART_output.acc_var_y = var_buffer[buffer_index-1][1];
			m_KIST_AART_output.acc_var_z = var_buffer[buffer_index-1][2];
			m_KIST_AART_output.acc_var_norm =var_buffer[buffer_index-1][3];
		//data smooth
			m_KIST_AART_output.acc_smooth_x = smooth_buffer[buffer_index-1][0];
			m_KIST_AART_output.acc_smooth_y = smooth_buffer[buffer_index-1][1];
			m_KIST_AART_output.acc_smooth_z = smooth_buffer[buffer_index-1][2];
			m_KIST_AART_output.acc_smooth_norm =smooth_buffer[buffer_index-1][3];
		//data diff
		
			m_KIST_AART_output.acc_diff_x = (smooth_buffer[buffer_index-1][0]-smooth_buffer[buffer_index2][0])/dt;
			m_KIST_AART_output.acc_diff_y = (smooth_buffer[buffer_index-1][1]-smooth_buffer[buffer_index2][1])/dt;
			m_KIST_AART_output.acc_diff_z = (smooth_buffer[buffer_index-1][2]-smooth_buffer[buffer_index2][2])/dt;
			m_KIST_AART_output.acc_diff_norm = (smooth_buffer[buffer_index-1][3]-smooth_buffer[buffer_index2][3])/dt;
			
		}		
		

		
		noise_level_threshold = 0.01;

		//after specific delay	
		if ( function_get_num > buffer_length_LP) {


			/////////////////////////////////////////////////////////////////////////////////////
			//Upper peak related START
			/////////////////////////////////////////////////////////////////////////////////////


			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//initialization of peak detection X case
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

			m_peak_detect_output = m_fn_peak_detect.peak_detect(function_get_num, buffer_LP,  buffer_index_in_LP, chk_window_LP, 0 , noise_level_threshold);
			
			
			m_peak_analysis_output = m_fn_peak_Analysis.Peak_Analysis(m_peak_detect_output.peak, m_peak_detect_output.peak_value, m_peak_detect_output.time_index,
					prev_local_upper_peak_x, prev_local_lower_peak_x);

			if (m_peak_detect_output.peak==1){
				prev_local_upper_peak_x[2] = prev_local_upper_peak_x[0];
				prev_local_upper_peak_x[3] = prev_local_upper_peak_x[1];
				
				prev_local_upper_peak_x[0] = m_peak_detect_output.time_index;
				prev_local_upper_peak_x[1] = m_peak_detect_output.peak_value;
			}
			else if (m_peak_detect_output.peak==2){
				prev_local_lower_peak_x[2] = prev_local_lower_peak_x[0];
				prev_local_lower_peak_x[3] = prev_local_lower_peak_x[1] ;
				
				prev_local_lower_peak_x[0] = m_peak_detect_output.time_index;
				prev_local_lower_peak_x[1] = m_peak_detect_output.peak_value;

			}

			m_KIST_AART_output.peak_x  =  m_peak_analysis_output.peak;
			m_KIST_AART_output.time_index_x =  function_get_num-chk_window_LP;
			m_KIST_AART_output.peak_value_x=  m_peak_analysis_output.peak_value;
			m_KIST_AART_output.peak_to_peak_x = m_peak_analysis_output.peak_to_peak;
			m_KIST_AART_output.surface_x   = m_peak_analysis_output.surface;
			m_KIST_AART_output.amplitude_x  = m_peak_analysis_output.amplitude;
			m_KIST_AART_output.cross_peak_to_peak_x = m_peak_analysis_output.cross_peak_to_peak;
			m_KIST_AART_output.surface_x2   = m_peak_analysis_output.surface2;

			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//initialization of peak detection Y case
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

			m_peak_detect_output = m_fn_peak_detect.peak_detect(function_get_num, buffer_LP,  buffer_index_in_LP, chk_window_LP, 1 , noise_level_threshold);
			m_peak_analysis_output = m_fn_peak_Analysis.Peak_Analysis(m_peak_detect_output.peak, m_peak_detect_output.peak_value, m_peak_detect_output.time_index,
					prev_local_upper_peak_y, prev_local_lower_peak_y);

			if (m_peak_detect_output.peak==1){
				prev_local_upper_peak_y[2] = prev_local_upper_peak_y[0];
				prev_local_upper_peak_y[3] = prev_local_upper_peak_y[1];
				
				prev_local_upper_peak_y[0] = m_peak_detect_output.time_index;
				prev_local_upper_peak_y[1] = m_peak_detect_output.peak_value;
			}
			else if (m_peak_detect_output.peak==2){
				prev_local_lower_peak_y[2] = prev_local_lower_peak_y[0];
				prev_local_lower_peak_y[3] = prev_local_lower_peak_y[1] ;
				
				prev_local_lower_peak_y[0] = m_peak_detect_output.time_index;
				prev_local_lower_peak_y[1] = m_peak_detect_output.peak_value;
			}

			m_KIST_AART_output.peak_y  =  m_peak_analysis_output.peak;
			m_KIST_AART_output.time_index_y =  function_get_num-chk_window_LP;
			m_KIST_AART_output.peak_value_y=  m_peak_analysis_output.peak_value;
			m_KIST_AART_output.peak_to_peak_y = m_peak_analysis_output.peak_to_peak;
			m_KIST_AART_output.surface_y   = m_peak_analysis_output.surface;
			m_KIST_AART_output.amplitude_y  = m_peak_analysis_output.amplitude;
			m_KIST_AART_output.cross_peak_to_peak_y =m_peak_analysis_output.cross_peak_to_peak;
			m_KIST_AART_output.surface_y2   = m_peak_analysis_output.surface2;

			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//initialization of peak detection Z case
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

			m_peak_detect_output = m_fn_peak_detect.peak_detect(function_get_num, buffer_LP,  buffer_index_in_LP, chk_window_LP, 2 , noise_level_threshold);
			m_peak_analysis_output = m_fn_peak_Analysis.Peak_Analysis(m_peak_detect_output.peak, m_peak_detect_output.peak_value, m_peak_detect_output.time_index,
					prev_local_upper_peak_z, prev_local_lower_peak_z);

			if (m_peak_detect_output.peak==1){
				prev_local_upper_peak_z[2] = prev_local_upper_peak_z[0];
				prev_local_upper_peak_z[3] = prev_local_upper_peak_z[1];
				
				prev_local_upper_peak_z[0] = m_peak_detect_output.time_index;
				prev_local_upper_peak_z[1] = m_peak_detect_output.peak_value;
			}
			else if (m_peak_detect_output.peak==2){
				prev_local_lower_peak_z[2] = prev_local_lower_peak_z[0];
				prev_local_lower_peak_z[3] = prev_local_lower_peak_z[1] ;
				
				prev_local_lower_peak_z[0] = m_peak_detect_output.time_index;
				prev_local_lower_peak_z[1] = m_peak_detect_output.peak_value;
			}

			m_KIST_AART_output.peak_z  =  m_peak_analysis_output.peak;
			m_KIST_AART_output.time_index_z = function_get_num-chk_window_LP;
			m_KIST_AART_output.peak_value_z=  m_peak_analysis_output.peak_value;
			m_KIST_AART_output.peak_to_peak_z = m_peak_analysis_output.peak_to_peak;
			m_KIST_AART_output.surface_z   = m_peak_analysis_output.surface;
			m_KIST_AART_output.amplitude_z  = m_peak_analysis_output.amplitude;
			m_KIST_AART_output.cross_peak_to_peak_z =m_peak_analysis_output.cross_peak_to_peak;
			m_KIST_AART_output.surface_z2   = m_peak_analysis_output.surface2;

			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//initialization of peak detection Norm case
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

			m_peak_detect_output = m_fn_peak_detect.peak_detect(function_get_num, buffer_LP,  buffer_index_in_LP, chk_window_LP, 3 , noise_level_threshold);
			m_peak_analysis_output = m_fn_peak_Analysis.Peak_Analysis(m_peak_detect_output.peak, m_peak_detect_output.peak_value, m_peak_detect_output.time_index,
					prev_local_upper_peak_norm, prev_local_lower_peak_norm);

			if (m_peak_detect_output.peak==1){
				prev_local_upper_peak_norm[2] = prev_local_upper_peak_norm[0];
				prev_local_upper_peak_norm[3] = prev_local_upper_peak_norm[1];
				
				prev_local_upper_peak_norm[0] = m_peak_detect_output.time_index;
				prev_local_upper_peak_norm[1] = m_peak_detect_output.peak_value;
			}
			else if (m_peak_detect_output.peak==2){
				prev_local_lower_peak_norm[2] = prev_local_lower_peak_norm[0];
				prev_local_lower_peak_norm[3] = prev_local_lower_peak_norm[1] ;
				
				prev_local_lower_peak_norm[0] = m_peak_detect_output.time_index;
				prev_local_lower_peak_norm[1] = m_peak_detect_output.peak_value;
			}

			m_KIST_AART_output.peak_norm  =  m_peak_analysis_output.peak;
			m_KIST_AART_output.time_index_norm =  function_get_num-chk_window_LP;
			m_KIST_AART_output.peak_value_norm=  m_peak_analysis_output.peak_value;
			m_KIST_AART_output.peak_to_peak_norm = m_peak_analysis_output.peak_to_peak;
			m_KIST_AART_output.surface_norm   = m_peak_analysis_output.surface;
			m_KIST_AART_output.amplitude_norm  = m_peak_analysis_output.amplitude;
			m_KIST_AART_output.cross_peak_to_peak_norm = m_peak_analysis_output.cross_peak_to_peak;
			m_KIST_AART_output.surface_norm2   = m_peak_analysis_output.surface2;
			
		}//GetFeaturedata End

		
		buffer_index_in_LP = buffer_index_in_LP + 1 ;
		buffer_index = buffer_index + 1;
		buffer_index2 = buffer_index + 1 ;
		
		return m_KIST_AART_output;	

	}
	
}










