package edu.sjtu.j_hmm;


import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by guanyongqing on 2016/8/25.
 */
public class algo_HMM {
    public static String[] viterbi(HMM hmm, ArrayList seq){
        double[][] trans_p = hmm.mA.toArray();
        double[][] emit_p = hmm.mE.toArray();

        Integer states[] = new Integer[hmm.topo_order.size()];
        states =  hmm.topo_order.toArray(states);
        Integer obs [] = new Integer[seq.size()];
        Map<String,Integer> em_alpabet = new HashMap();
        for(int i=0;i<hmm.hmm_emission_alphabet.size();i++){
            em_alpabet.put(hmm.hmm_emission_alphabet.get(i),i);
        }
        Map<Integer,String> tr_alpabet = new HashMap();
        for(int i=1;i<hmm.num_states;i++){
            tr_alpabet.put(i,hmm.state_names.get(i));
        }

        for(int i=0;i<seq.size();i++){
            obs[i]=em_alpabet.get(seq.get(i));
        }
        //System.out.println(Arrays.toString(obs));

        double[][] V = new double[hmm.num_states][obs.length];
        int[][] path = new int[obs.length][hmm.num_states];


        //开始状态 t=0
        for (int i=1;i<hmm.num_states;i++){
            V[i][0]=trans_p[0][i]*emit_p[i][obs[0]];
        }
        //正向递归 记录最大转移状态标签
        for (int t=1;t<obs.length;t++){
            for(int i=1;i<hmm.num_states;i++){
                double maxdelta_prob=0.0;
                double prob=0.0;  //bug1
                int maxdeltalabel=1;
                for (int j=1;j<hmm.num_states;j++){  //j代表上一个时刻的各种状态
                    prob = V[j][t-1]*trans_p[j][i];
                    if(prob>maxdelta_prob) {
                        maxdelta_prob = prob;
                        maxdeltalabel = j;
                        V[i][t] = prob * emit_p[i][obs[t]];  //bug2
                        path[t][i] = maxdeltalabel;
                    }
                }

            }
        }

        //System.out.println(Arrays.toString(V[3]));
        //System.out.println(Arrays.toString(path[3]));

        int[] q = new int[seq.size()];

        //计算结尾点 t=T(seq.size()-1)
        double prob_T = 0.0;
        q[obs.length-1]=1;
        for(int i=1;i<hmm.num_states;i++){

            if (V[i][obs.length-1]>prob_T){
                prob_T=V[i][obs.length-1];
                q[obs.length-1]=i;
            }
        }
        //从倒数第二位,进行路径回溯
        for(int t=obs.length-2;t>=0;t--){  //bug3
            q[t]=path[t+1][q[t+1]];
        }
        String[] result = new String[obs.length];
        //System.out.println(Arrays.toString(q));
        for(int i=0;i<obs.length;i++){
            result[i]=tr_alpabet.get(q[i]);
        }
        return result;



    }
}
