package kr.co.greencomm.engine;

/**
 * Created by BLOOD on 2017. 11. 8..
 */


import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import libsvm.*;

public class svm_train  implements Serializable
{

    public svm_train()
    {
    }

    private static void exit_with_help()
    {
        System.out.print("Usage: svm_train [options] training_set_file [model_file]\noptions:\n-s svm_type : set type of SVM (default 0)\n\t0 -- C-SVC\t\t(multi-class classification)\n\t1 -- nu-SVC\t\t(multi-class classification)\n\t2 -- one-class SVM\n\t3 -- epsilon-SVR\t(regression)\n\t4 -- nu-SVR\t\t(regression)\n-t kernel_type : set type of kernel function (default 2)\n\t0 -- linear: u'*v\n\t1 -- polynomial: (gamma*u'*v + coef0)^degree\n\t2 -- radial basis function: exp(-gamma*|u-v|^2)\n\t3 -- sigmoid: tanh(gamma*u'*v + coef0)\n\t4 -- precomputed kernel (kernel values in training_set_file)\n-d degree : set degree in kernel function (default 3)\n-g gamma : set gamma in kernel function (default 1/num_features)\n-r coef0 : set coef0 in kernel function (default 0)\n-c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)\n-n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)\n-p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)\n-m cachesize : set cache memory size in MB (default 100)\n-e epsilon : set tolerance of termination criterion (default 0.001)\n-h shrinking : whether to use the shrinking heuristics, 0 or 1 (default 1)\n-b probability_estimates : whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)\n-wi weight : set the parameter C of class i to weight*C, for C-SVC (default 1)\n-v n : n-fold cross validation mode\n-q : quiet mode (no outputs)\n");
        System.exit(1);
    }

    private void do_cross_validation()
    {
        int total_correct = 0;
        double total_error = 0.0D;
        double sumv = 0.0D;
        double sumy = 0.0D;
        double sumvv = 0.0D;
        double sumyy = 0.0D;
        double sumvy = 0.0D;
        double target[] = new double[prob.l];
        svm.svm_cross_validation(prob, param, nr_fold, target);
        if(param.svm_type == 3 || param.svm_type == 4)
        {
            for(int i = 0; i < prob.l; i++)
            {
                double y = prob.y[i];
                double v = target[i];
                total_error += (v - y) * (v - y);
                sumv += v;
                sumy += y;
                sumvv += v * v;
                sumyy += y * y;
                sumvy += v * y;
            }

            System.out.print((new StringBuilder("Cross Validation Mean squared error = ")).append(total_error / (double)prob.l).append("\n").toString());
            System.out.print((new StringBuilder("Cross Validation Squared correlation coefficient = ")).append((((double)prob.l * sumvy - sumv * sumy) * ((double)prob.l * sumvy - sumv * sumy)) / (((double)prob.l * sumvv - sumv * sumv) * ((double)prob.l * sumyy - sumy * sumy))).append("\n").toString());
        } else
        {
            for(int i = 0; i < prob.l; i++)
                if(target[i] == prob.y[i])
                    total_correct++;

            System.out.print((new StringBuilder("Cross Validation Accuracy = ")).append((100D * (double)total_correct) / (double)prob.l).append("%\n").toString());
        }
    }

    private void run(String argv[])
            throws IOException
    {
        parse_command_line(argv);
        read_problem();
        error_msg = svm.svm_check_parameter(prob, param);
        if(error_msg != null)
        {
            System.err.print((new StringBuilder("ERROR: ")).append(error_msg).append("\n").toString());
            System.exit(1);
        }
        if(cross_validation != 0)
        {
            do_cross_validation();
        } else
        {
            model = svm.svm_train(prob, param);
            svm.svm_save_model(model_file_name, model);
        }
    }

    public static void main(String argv[])
            throws IOException
    {
        svm_train t = new svm_train();
        t.run(argv);
    }

    private static double atof(String s)
    {
        double d = Double.valueOf(s).doubleValue();
        if(Double.isNaN(d) || Double.isInfinite(d))
        {
            System.err.print("NaN or Infinity in input\n");
            System.exit(1);
        }
        return d;
    }

    private static int atoi(String s)
    {
        return Integer.parseInt(s);
    }

    private void parse_command_line(String argv[])
    {
        svm_print_interface print_func = null;
        param = new svm_parameter();
        param.svm_type = 0;
        param.kernel_type = 2;
        param.degree = 3;
        param.gamma = 0.0D;
        param.coef0 = 0.0D;
        param.nu = 0.5D;
        param.cache_size = 100D;
        param.C = 1.0D;
        param.eps = 0.001D;
        param.p = 0.10000000000000001D;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];
        cross_validation = 0;
        int i;
        for(i = 0; i < argv.length; i++)
        {
            if(argv[i].charAt(0) != '-')
                break;
            if(++i >= argv.length)
                exit_with_help();
            switch(argv[i - 1].charAt(1))
            {
                case 115: // 's'
                    param.svm_type = atoi(argv[i]);
                    break;

                case 116: // 't'
                    param.kernel_type = atoi(argv[i]);
                    break;

                case 100: // 'd'
                    param.degree = atoi(argv[i]);
                    break;

                case 103: // 'g'
                    param.gamma = atof(argv[i]);
                    break;

                case 114: // 'r'
                    param.coef0 = atof(argv[i]);
                    break;

                case 110: // 'n'
                    param.nu = atof(argv[i]);
                    break;

                case 109: // 'm'
                    param.cache_size = atof(argv[i]);
                    break;

                case 99: // 'c'
                    param.C = atof(argv[i]);
                    break;

                case 101: // 'e'
                    param.eps = atof(argv[i]);
                    break;

                case 112: // 'p'
                    param.p = atof(argv[i]);
                    break;

                case 104: // 'h'
                    param.shrinking = atoi(argv[i]);
                    break;

                case 98: // 'b'
                    param.probability = atoi(argv[i]);
                    break;

                case 113: // 'q'
                    print_func = svm_print_null;
                    i--;
                    break;

                case 118: // 'v'
                    cross_validation = 1;
                    nr_fold = atoi(argv[i]);
                    if(nr_fold < 2)
                    {
                        System.err.print("n-fold cross validation: n must >= 2\n");
                        exit_with_help();
                    }
                    break;

                case 119: // 'w'
                    param.nr_weight++;
                    int old[] = param.weight_label;
                    param.weight_label = new int[param.nr_weight];
                    System.arraycopy(old, 0, param.weight_label, 0, param.nr_weight - 1);
                    for (int j=0; j<param.weight_label.length;j++) {
                        old[j] = (int)param.weight[j];
                    }
                    param.weight = new double[param.nr_weight];
                    System.arraycopy(old, 0, param.weight, 0, param.nr_weight - 1);
                    param.weight_label[param.nr_weight - 1] = atoi(argv[i - 1].substring(2));
                    param.weight[param.nr_weight - 1] = atof(argv[i]);
                    break;

                case 102: // 'f'
                case 105: // 'i'
                case 106: // 'j'
                case 107: // 'k'
                case 108: // 'l'
                case 111: // 'o'
                case 117: // 'u'
                default:
                    System.err.print((new StringBuilder("Unknown option: ")).append(argv[i - 1]).append("\n").toString());
                    exit_with_help();
                    break;
            }
        }

        svm.svm_set_print_string_function(print_func);
        if(i >= argv.length)
            exit_with_help();
        input_file_name = argv[i];
        if(i < argv.length - 1)
        {
            model_file_name = argv[i + 1];
        } else
        {
            int p = argv[i].lastIndexOf('/');
            p++;
            model_file_name = (new StringBuilder(String.valueOf(argv[i].substring(p)))).append(".model").toString();
        }
    }

    private void read_problem()
            throws IOException
    {
        BufferedReader fp = new BufferedReader(new FileReader(input_file_name));
        Vector vy = new Vector();
        Vector vx = new Vector();
        int max_index = 0;
        do
        {
            String line = fp.readLine();
            if(line == null)
                break;
            StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");
            vy.addElement(Double.valueOf(atof(st.nextToken())));
            int m = st.countTokens() / 2;
            svm_node x[] = new svm_node[m];
            for(int j = 0; j < m; j++)
            {
                x[j] = new svm_node();
                x[j].index = atoi(st.nextToken());
                x[j].value = atof(st.nextToken());
            }

            if(m > 0)
                max_index = Math.max(max_index, x[m - 1].index);
            vx.addElement(x);
        } while(true);
        prob = new svm_problem();
        prob.l = vy.size();
        if(param.gamma == 0.0D && max_index > 0)
            param.gamma = 1.0D / (double)max_index;
        if(param.kernel_type == 4)
        {
            for(int i = 0; i < prob.l; i++)
            {
                if(prob.x[i][0].index != 0)
                {
                    System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
                    System.exit(1);
                }
                if((int)prob.x[i][0].value <= 0 || (int)prob.x[i][0].value > max_index)
                {
                    System.err.print("Wrong input format: sample_serial_number out of range\n");
                    System.exit(1);
                }
            }

        }
        fp.close();
    }

    svm_parameter param;
    svm_problem prob;
    svm_model model;
    String input_file_name;
    String model_file_name;
    String error_msg;
    int cross_validation;
    int nr_fold;
    private static svm_print_interface svm_print_null = new svm_print_interface() {

        public void print(String s1)
        {
        }

    }
            ;

}
