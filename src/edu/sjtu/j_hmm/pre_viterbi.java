package edu.sjtu.j_hmm;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by guanyongqing on 2016/8/24.
 */
public class pre_viterbi {
    public static void main(String[] args) throws IOException, JSONException {
        if (args.length<2){
            System.out.println("请使用正确参数，现在参数个数："+args.length);
            if (args.length>0){
            System.out.println(args[0]);
            System.out.println(args[1]);}
            System.exit(1);
        }
        ArrayList<String> prot = new ArrayList<>();
        ArrayList<String> seq = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            String tmp_string;
            while ((tmp_string=br.readLine())!=null){
                    prot.add(tmp_string);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace(); }

        for (String line :
                    prot) {
                String[] a = line.split(",");
                seq.add(a[0]);  //数组的使用
                labels.add(a[1]);
            }
        //System.out.println(seq);
        TR_OBJ obj = new TR_OBJ(seq,labels);
        ArrayList<TR_OBJ> trobjlist =new ArrayList(){};
        trobjlist.add(obj);
        HMM_IO.Build_HMM tmp_hmm = new HMM_IO.Build_HMM(args[1]);
        HMM hmm =tmp_hmm.get_HMM();

        for (TR_OBJ o : trobjlist) {
            String[] result = algo_HMM.viterbi(hmm,o.seq);
            System.out.println(Arrays.toString(result));
        }

    }
}
