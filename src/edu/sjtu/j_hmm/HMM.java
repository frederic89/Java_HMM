package edu.sjtu.j_hmm;


import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Objects;

/**
 * Created by guanyongqing on 2016/8/17.
 */
public class HMM {
    DoubleMatrix2D mE;
    DoubleMatrix2D mA;
    HashMap<String, Double> hmm_tr;
    ArrayList<String> effective_tr;
    ArrayList<String> effective_em;
    HashMap effective_tr_pos;
    HashMap effective_em_pos;
    ArrayList<Integer> em_updatable;
    ArrayList<Integer> tr_updatable;
    ArrayList<Integer> topo_order;
    int num_states;
    ArrayList<State> hmm_states;
    ArrayList fix_tr; //待开发
    ArrayList fix_em; //待开发
    ArrayList<String> hmm_emission_alphabet;
    ArrayList<String> state_names;
    ArrayList<Integer> emits;
    ArrayList<Integer> nulls;
    ArrayList<ArrayList> out_s;
    ArrayList<ArrayList> in_s;
    ArrayList<ArrayList> out_s_e;
    ArrayList<ArrayList> out_s_n;
    ArrayList<ArrayList> in_s_e;
    ArrayList<ArrayList> in_s_n;
    ArrayList end_s;
    ArrayList end_s_e;
    ArrayList end_s_n;
    ArrayList<String> label_list;
    HashMap<String,DenseDoubleMatrix2D> labelMusk;

    HMM(ArrayList<State> states, ArrayList<String> emission_alphabet){
        this.num_states=states.size();
        this.hmm_states=states;
        this.hmm_emission_alphabet=emission_alphabet;
        this.state_names=new ArrayList();
        this.fix_tr=new ArrayList();
        this.fix_em=new ArrayList();
        this.tr_updatable=new ArrayList();

        for(int i=0;i<num_states;i++){
            State a =  states.get(i);
            state_names.add(a.name_state);
            fix_tr.add(null);
            fix_em.add(null);
        }
        //tolpological_sort_return.get("3")是sorted_emits<> this.emits

        HashMap<String,ArrayList> tolpological_sort_return = Sort_HMM.tolpological_sort(state_names,hmm_states);
        ArrayList<ArrayList> all_links = tolpological_sort_return.get("1");
        this.topo_order = tolpological_sort_return.get("2");
        this.emits = tolpological_sort_return.get("3");//sorted_emits
        this.nulls = tolpological_sort_return.get("4");//sorted_nulls
        HashMap<String,ArrayList> make_links_return = Sort_HMM.make_links(emits,nulls,all_links);
        this.out_s = make_links_return.get("1");
        this.in_s = make_links_return.get("2");
        this.out_s_e = make_links_return.get("3");
        this.out_s_n = make_links_return.get("4");
        this.in_s_e = make_links_return.get("5");
        this.in_s_n = make_links_return.get("6");
        HashMap<String,ArrayList> make_ends_return = Sort_HMM.make_ends(hmm_states,emits,nulls);
        this.end_s = make_ends_return.get("1");
        this.end_s_e = make_ends_return.get("2");
        this.end_s_n  = make_ends_return.get("3");

        //从nulls中移除掉0,因为开始不是空状态
        Integer i = nulls.indexOf(0);
        nulls.remove(i);

        ArrayList<String> tmp_label = new ArrayList<String>();
        this.label_list = tmp_label;
        for (State s :
                hmm_states) {
            if (!(s.label.equals("None")) & (!(label_list.contains(s.label)))){
                label_list.add(s.label);
            }
        }
        this.effective_tr = new ArrayList<>(); //有效的转移
        this.effective_em = new ArrayList<>(); //有效的转移
        for (int n=0;n<num_states;n++){
            State states_i = states.get(n);
            String name_tr = states_i.get_tr_name();
            if (!(effective_tr.contains(name_tr))){
                effective_tr.add(name_tr);
            }
            /*if (Objects.isNull(fix_tr.get(n))){
                tr_updatable.add(n);
            }*/
            String name_em = states_i.get_em_name();
            if (emits.contains(n) & !(effective_em.contains(name_em))){
                effective_em.add(name_em);
            }
            /*if (Objects.isNull(fix_em.get(n))){
                em_updatable.add(n);
            }*/
        }
        this.hmm_tr=new HashMap<>();
        for (Integer s : topo_order) {

            for (Integer k :  topo_order) {

                hmm_tr.put(s.toString()+","+k.toString(),hmm_states.get(s).a(hmm_states.get(k)));
            }
        }
        int a = hmm_emission_alphabet.size();
        int b = num_states;
        /*System.out.println("矩阵mE是:");
        System.out.println("列"+a);
        System.out.println("行"+b);*/
        DenseDoubleMatrix2D mE = new DenseDoubleMatrix2D(b,a); mE.assign(0.0);
        this.mE=mE; //初始化
        this.set_mE();
        DenseDoubleMatrix2D mA = new DenseDoubleMatrix2D(b,b); mA.assign(0.0);
        this.mA=mA;  //初始化
        this.set_mA();
        this.set_labelMusk();

    }
    Double a(Integer i,Integer j){
        return hmm_tr.get(i.toString()+","+j.toString());
    }

    Double e(Integer i,String x){
        return ((Number) (hmm_states.get(i).e(x))).doubleValue();
    }


    void set_mA() {
        for (Integer s : topo_order){
            for (Integer k : topo_order){
                mA.setQuick(s,k,this.a(s,k));
            }
        }
    }

    void set_mE() {
        for (Integer s : topo_order) {
            //System.out.println(s);
            Integer i = 0;
            for (String c :
                    hmm_emission_alphabet) {
                //System.out.println(i);
                double tmp_  = this.e(s, c); //
                mE.setQuick(s, i, tmp_);
                i+=1;
            }
        }
    }

    void set_labelMusk(){
        this.labelMusk = new HashMap<>();
        for (String lab : label_list
             ) {
            int b = num_states;
            DenseDoubleMatrix2D zeromatrix = new DenseDoubleMatrix2D(1,b); zeromatrix.assign(0.0);
            labelMusk.put(lab,zeromatrix);
            for (Integer s : topo_order){
                if (Objects.equals(hmm_states.get(s).label, lab)){
                    DenseDoubleMatrix2D temp_matrix2d = labelMusk.get(lab);
                    temp_matrix2d.setQuick(0,s,1.0);  // !!! 矩阵大小与序号是不对应的,行数1,其序号是0
                }
            }
        }
    }
}
