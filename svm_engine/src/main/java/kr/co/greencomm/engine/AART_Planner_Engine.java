package kr.co.greencomm.engine;

/**
 * Created by BLOOD on 2017. 11. 8..
 */


import android.os.Environment;
import java.io.*;
import libsvm.svm;
import libsvm.svm_node;

// Referenced classes of package com.example.android_svm_test_home:
//            svm_train

public class AART_Planner_Engine
{

    public AART_Planner_Engine()
    {
        threshold1_accVar = 0.00080000000000000004D;
        threshold_pres = 0.40000000000000002D;
        threshold2_accVar = 0.80000000000000004D;
        threshold3_accVar = 3D;
        threshold1_stepcount = 40D;
        threshold2_stepcount = 20D;
        threshold1_swingcount = 20D;
        large_swing_num_for_golf = 1;
        min_large_swing_num_for_golf = 1;
        max_large_swing_num_for_golf = 3;
        present_1m_activity = 0;
        root_folder = Environment.getExternalStorageDirectory().getAbsolutePath();
        file_pos_class_1 = (new StringBuilder(String.valueOf(root_folder))).append(String.format("/svm/KistracCalssifierV5_2_1.ser", new Object[0])).toString();
        file_pos_class_2 = (new StringBuilder(String.valueOf(root_folder))).append(String.format("/svm/KistracCalssifierV5_2_2.ser", new Object[0])).toString();
        file_pos_class_3 = (new StringBuilder(String.valueOf(root_folder))).append(String.format("/svm/KistracCalssifierV5_2_3.ser", new Object[0])).toString();
        s_train_1 = new svm_train();
        s_train_2 = new svm_train();
        s_train_3 = new svm_train();
        svm_estimated_label = 0.0D;
        step_parameter_scale = 3.5D;
        alpha = 0.32000000000000001D * step_parameter_scale;
        beta = 0.71999999999999997D * step_parameter_scale;
        n_ampl = 8.6999999999999993D;
        n_fre = 0.93999999999999995D;
        SL_0_walking = 68D;
        SL_0_hiking = 60D;
        SL_0_run = 98D;
    }

    public double[] ActivityOut(double feature_element_vector[])
    {
        double estimated_activity[] = new double[8];
        double feature_vector[] = feature_element_vector;
        double n_min = 9D;
        double n_max = 13D;
        double x_min1 = -2.2000000000000002D;
        double x_max1 = 3.1000000000000001D;
        double y_min1 = -8.1999999999999993D;
        double y_max1 = -4.2000000000000002D;
        double z_min1 = 5.5999999999999996D;
        double z_max1 = 9.5D;
        double x_min2 = 6.9000000000000004D;
        double x_max2 = 10.1D;
        double y_min2 = -7.2000000000000002D;
        double y_max2 = -3.2999999999999998D;
        double z_min2 = 0.0D;
        double z_max2 = 5D;
        double x_min3 = 3.7999999999999998D;
        double x_max3 = 6.2999999999999998D;
        double y_min3 = -8.6999999999999993D;
        double y_max3 = -6.5999999999999996D;
        double z_min3 = 2.6000000000000001D;
        double z_max3 = 6.4000000000000004D;
        if(feature_vector[3] < threshold1_accVar)
            present_1m_activity = 2;
        else
        if(feature_vector[3] < threshold2_accVar && feature_vector[11] == 0.0D)
            present_1m_activity = 4;
        else
        if(feature_vector[3] < threshold3_accVar && feature_vector[11] == 0.0D || feature_vector[3] < threshold3_accVar && Math.abs(feature_vector[12]) > 5D)
        {
            double svm_feature[] = new double[3];
            svm_feature[0] = feature_vector[4];
            svm_feature[1] = feature_vector[5];
            svm_feature[2] = feature_vector[6];
            svm_node x[] = new svm_node[3];
            for(int j = 0; j < 3; j++)
            {
                x[j] = new svm_node();
                x[j].index = j + 1;
                x[j].value = svm_feature[j];
            }

            svm_estimated_label = svm.svm_predict(s_train_1.model, x);
            if(svm_estimated_label == 1.0D)
                present_1m_activity = 4;
            else
            if(svm_estimated_label == 2D)
                present_1m_activity = 6;
        } else
        if(feature_vector[11] >= (double)large_swing_num_for_golf && feature_vector[10] <= 2D && feature_vector[9] == 0.0D)
        {
            present_1m_activity = 44;
        } else
        {
            double svm_feature[] = new double[9];
            svm_feature[0] = feature_vector[0] / 20D;
            svm_feature[1] = feature_vector[3] / 20D;
            svm_feature[2] = feature_vector[8] / 20D;
            svm_feature[3] = feature_vector[9] / 20D;
            svm_feature[4] = feature_vector[8] / (feature_vector[9] + 1.0D) / 20D;
            svm_feature[5] = feature_vector[4];
            svm_feature[6] = feature_vector[5];
            svm_feature[7] = feature_vector[6];
            svm_feature[8] = feature_vector[7];
            svm_node x[] = new svm_node[9];
            for(int j = 0; j < 9; j++)
            {
                x[j] = new svm_node();
                x[j].index = j + 1;
                x[j].value = svm_feature[j];
            }

            svm_estimated_label = svm.svm_predict(s_train_3.model, x);
            if(svm_estimated_label == 1.0D)
                present_1m_activity = 6;
            else
            if(svm_estimated_label == 2D)
                present_1m_activity = 6;
            else
            if(svm_estimated_label == 3D)
                present_1m_activity = 10;
            else
            if(svm_estimated_label == 4D)
                present_1m_activity = 12;
            else
            if(svm_estimated_label == 5D)
                present_1m_activity = 14;
            else
            if(svm_estimated_label == 6D)
                present_1m_activity = 16;
            else
            if(svm_estimated_label == 7D)
                present_1m_activity = 18;
            else
            if(svm_estimated_label == 8D)
                present_1m_activity = 20;
            else
            if(svm_estimated_label == 9D)
                present_1m_activity = 22;
            else
            if(svm_estimated_label == 10D)
                present_1m_activity = 24;
            else
            if(svm_estimated_label == 11D)
                present_1m_activity = 26;
            else
            if(svm_estimated_label == 12D)
                present_1m_activity = 28;
            else
            if(svm_estimated_label == 13D)
                present_1m_activity = 40;
            else
            if(svm_estimated_label == 14D)
                present_1m_activity = 42;
        }
        if(present_1m_activity == 6)
            if(feature_element_vector[4] >= x_min1 && feature_element_vector[4] <= x_max1 && feature_element_vector[5] >= y_min1 && feature_element_vector[5] <= y_max1 && feature_element_vector[6] >= z_min1 && feature_element_vector[6] <= z_max1 && feature_element_vector[7] >= n_min && feature_element_vector[7] <= n_max)
                present_1m_activity = 6;
            else
            if(feature_element_vector[4] >= x_min2 && feature_element_vector[4] <= x_max2 && feature_element_vector[5] >= y_min2 && feature_element_vector[5] <= y_max2 && feature_element_vector[6] >= z_min2 && feature_element_vector[6] <= z_max2 && feature_element_vector[7] >= n_min && feature_element_vector[7] <= n_max)
                present_1m_activity = 6;
            else
            if(feature_element_vector[4] >= x_min3 && feature_element_vector[4] <= x_max3 && feature_element_vector[5] >= y_min3 && feature_element_vector[5] <= y_max3 && feature_element_vector[6] >= z_min3 && feature_element_vector[6] <= z_max3 && feature_element_vector[7] >= n_min && feature_element_vector[7] <= n_max)
                present_1m_activity = 6;
            else
            if(feature_element_vector[8] < 10D && feature_element_vector[10] > 30D)
                present_1m_activity = 4;
            else
            if(feature_element_vector[8] > 20D)
                present_1m_activity = 12;
            else
                present_1m_activity = 4;
        if(present_1m_activity == 2)
            estimated_activity[0] = 0.0D;
        else
        if(present_1m_activity == 4)
            estimated_activity[0] = 1.0D;
        else
        if(present_1m_activity == 6)
            estimated_activity[0] = 2D;
        else
        if(present_1m_activity == 8)
            estimated_activity[0] = 2D;
        else
        if(present_1m_activity == 10)
            estimated_activity[0] = 3D;
        else
        if(present_1m_activity == 12)
            estimated_activity[0] = 4D;
        else
        if(present_1m_activity == 14)
            estimated_activity[0] = 4D;
        else
        if(present_1m_activity == 16)
            estimated_activity[0] = 4D;
        else
        if(present_1m_activity == 18)
            estimated_activity[0] = 4D;
        else
        if(present_1m_activity == 20)
            estimated_activity[0] = 4D;
        else
        if(present_1m_activity == 22)
            estimated_activity[0] = 4D;
        else
        if(present_1m_activity == 24)
            estimated_activity[0] = 4D;
        else
        if(present_1m_activity == 26)
            estimated_activity[0] = 5D;
        else
        if(present_1m_activity == 28)
            estimated_activity[0] = 5D;
        else
        if(present_1m_activity == 30)
            estimated_activity[0] = 6D;
        else
        if(present_1m_activity == 32)
            estimated_activity[0] = 7D;
        else
        if(present_1m_activity == 34)
            estimated_activity[0] = 7D;
        else
        if(present_1m_activity == 36)
            estimated_activity[0] = 7D;
        else
        if(present_1m_activity == 38)
            estimated_activity[0] = 7D;
        else
        if(present_1m_activity == 40)
            estimated_activity[0] = 21D;
        else
        if(present_1m_activity == 42)
            estimated_activity[0] = 20D;
        else
        if(present_1m_activity == 44)
            estimated_activity[0] = 10D;
        if(estimated_activity[0] == 1.0D && feature_vector[8] >= 60D)
            estimated_activity[0] = 4D;
        if(estimated_activity[0] == 4D && feature_vector[12] >= -3.5D && feature_vector[12] <= -1D && feature_vector[8] >= 40D)
            estimated_activity[0] = 7D;
        if(estimated_activity[0] == 2D && feature_vector[8] >= 20D)
            estimated_activity[0] = 4D;
        if(estimated_activity[0] == 3D && feature_vector[8] >= 80D)
            estimated_activity[0] = 4D;
        estimated_activity[1] = feature_vector[3];
        estimated_activity[2] = feature_vector[12];
        estimated_activity[3] = feature_vector[8];
        estimated_activity[4] = feature_vector[9];
        estimated_activity[5] = feature_vector[10];
        estimated_activity[6] = feature_vector[11];
        if(estimated_activity[3] > 0.0D)
        {
            if(estimated_activity[0] == 4D)
                estimated_activity[7] = alpha * (feature_vector[13] - n_ampl) + beta * (feature_vector[14] - n_fre) + SL_0_walking;
            else
            if(estimated_activity[0] == 6D && estimated_activity[0] == 7D)
                estimated_activity[7] = alpha * (feature_vector[13] - n_ampl) + beta * (feature_vector[14] - n_fre) + SL_0_hiking;
            else
            if(estimated_activity[0] == 5D && estimated_activity[4] > 0.0D)
                estimated_activity[7] = SL_0_run;
            else
                estimated_activity[7] = alpha * (feature_vector[13] - n_ampl) + beta * (feature_vector[14] - n_fre) + SL_0_walking;
        } else
        if(estimated_activity[0] == 5D && estimated_activity[4] > 0.0D)
            estimated_activity[7] = SL_0_run;
        else
            estimated_activity[7] = 0.0D;
        return estimated_activity;
    }

    public void getobject()
    {
        try
        {
            FileInputStream fileIn = new FileInputStream(file_pos_class_1);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            s_train_1 = (svm_train)in.readObject();
            fileIn = new FileInputStream(file_pos_class_2);
            in = new ObjectInputStream(fileIn);
            s_train_2 = (svm_train)in.readObject();
            fileIn = new FileInputStream(file_pos_class_3);
            in = new ObjectInputStream(fileIn);
            s_train_3 = (svm_train)in.readObject();
            in.close();
            fileIn.close();
        }
        catch(IOException i)
        {
            i.printStackTrace();
        }
        catch(ClassNotFoundException c)
        {
            c.printStackTrace();
        }
    }

    public void getobject(String path)
    {
        try
        {
            FileInputStream fileIn = new FileInputStream((new StringBuilder(String.valueOf(path))).append("/svm/KistracCalssifierV5_2_1.ser").toString());
            ObjectInputStream in = new ObjectInputStream(fileIn);
            s_train_1 = (svm_train)in.readObject();
            fileIn = new FileInputStream((new StringBuilder(String.valueOf(path))).append("/svm/KistracCalssifierV5_2_2.ser").toString());
            in = new ObjectInputStream(fileIn);
            s_train_2 = (svm_train)in.readObject();
            fileIn = new FileInputStream((new StringBuilder(String.valueOf(path))).append("/svm/KistracCalssifierV5_2_3.ser").toString());
            in = new ObjectInputStream(fileIn);
            s_train_3 = (svm_train)in.readObject();
            in.close();
            fileIn.close();
        }
        catch(IOException i)
        {
            i.printStackTrace();
        }
        catch(ClassNotFoundException c)
        {
            c.printStackTrace();
        }
    }

    double threshold1_accVar;
    double threshold_pres;
    double threshold2_accVar;
    double threshold3_accVar;
    double threshold1_stepcount;
    double threshold2_stepcount;
    double threshold1_swingcount;
    int large_swing_num_for_golf;
    int min_large_swing_num_for_golf;
    int max_large_swing_num_for_golf;
    int present_1m_activity;
    String root_folder;
    String file_pos_class_1;
    String file_pos_class_2;
    String file_pos_class_3;
    svm_train s_train_1;
    svm_train s_train_2;
    svm_train s_train_3;
    double svm_estimated_label;
    double step_parameter_scale;
    double alpha;
    double beta;
    double n_ampl;
    double n_fre;
    double SL_0_walking;
    double SL_0_hiking;
    double SL_0_run;
}

