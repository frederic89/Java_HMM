package edu.sjtu.j_hmm;

import javax.lang.model.type.ArrayType;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by guanyongqing on 2016/8/17.
 */
public class Sort_HMM {
    static HashMap tolpological_sort(ArrayList names, ArrayList<State> hmm_states){

        int num_states=names.size();
        ArrayList<Integer> sorted=new ArrayList(){{add(0);}};
        HashMap<Integer,ArrayList> to_sort = new HashMap<>();
        ArrayList<Integer> emits=new ArrayList();
        ArrayList nulls=new ArrayList();

        ArrayList<ArrayList> all_links=new ArrayList();
        for(int i=0;i<num_states;i++){
            ArrayList tmpnull = new ArrayList();
            all_links.add(i,tmpnull);
        }
        ArrayList tempa0=new ArrayList();
        all_links.set(0,tempa0);//可以删除?
        State begin = hmm_states.get(0);
        for (String name: begin.out_links) {
            tempa0.add(names.indexOf(name));
            all_links.set(0,tempa0);

        }

        for(int i=1;i<num_states;i++){
            State nonbegin = hmm_states.get(i);
            List<String> nbegin = nonbegin.em_letters;
            if(nbegin.isEmpty()){
                nulls.add(i);

                ArrayList to_sort_inner= new ArrayList();
                for (String s : hmm_states.get(i).out_links) {
                    int a = names.indexOf(s);
                    if (hmm_states.get(a).em_letters.isEmpty()){
                        to_sort_inner.add(a);
                        to_sort.put(i,to_sort_inner);
                    }
                }
            }else{
                emits.add(i);

            }
            ArrayList all_links_inner = new ArrayList();
            for (String s : hmm_states.get(i).out_links) {
                all_links_inner.add(names.indexOf(s));
                all_links.set(i,all_links_inner);

            }
        }
        Map visited = new HashMap();
        Set<Integer> x = to_sort.keySet();
        for (int k :x) {
            visited.put(k,null);
        }

        ArrayList<Integer>  sorted_emits = emits;
        ArrayList<Integer> sorted_nulls = new ArrayList();
        Set<Integer> y = to_sort.keySet();
        for (int v:y) {
            if (visited.get(v)==null){
                __topSort(v,sorted_nulls,visited,to_sort);
            }
        }
        sorted.addAll(sorted_emits);
        sorted.addAll(sorted_nulls);
        sorted_nulls.add(0,0);
        HashMap tolpological_sort_return =  new HashMap<String,ArrayList>(){{put("1",all_links);put("2",sorted);put("3",sorted_emits);put("4",sorted_nulls);}};
        return tolpological_sort_return;
    }

    static void __topSort(int v,ArrayList sorted, Map visited,HashMap to_sort){
        visited.put(v,"OK");
        ArrayList<Integer> list = (ArrayList) to_sort.get(v);  //TODO 待检查
        for (int w : list) {
            if (visited.get(w)==null){
                __topSort(w,sorted,visited,to_sort);
            }
        }
        sorted.add(0,v);
    }
    static HashMap make_links(ArrayList<Integer> emits,ArrayList<Integer> nulls,ArrayList<ArrayList> to_sort){
        Integer num_states = to_sort.size();
        ArrayList<ArrayList> out_s = new ArrayList();
        ArrayList<ArrayList> in_s = new ArrayList();
        ArrayList<ArrayList> out_s_n = new ArrayList();
        ArrayList<ArrayList> out_s_e = new ArrayList();
        ArrayList<ArrayList> in_s_n = new ArrayList();
        ArrayList<ArrayList> in_s_e = new ArrayList();
        ArrayList<ArrayList> inlinks = new ArrayList<>();
        for (int i = 0; i<num_states;i++){
            out_s.add(new ArrayList());
            in_s.add(new ArrayList());
            in_s_e.add(new ArrayList());
            in_s_n.add(new ArrayList());
            out_s_n.add(new ArrayList());
            out_s_e.add(new ArrayList());
            inlinks.add(new ArrayList());
        }
        for (int i = 0; i<num_states;i++){
            ArrayList<Integer> to_sorted_link = to_sort.get(i);
            if (!((to_sorted_link).isEmpty())){
                for (Integer k : emits ) {
                    if (to_sorted_link.contains(k)){
                        out_s_e.get(i).add(k); //返回角标
                        if (!((inlinks.get(k)).contains(i))){
                            inlinks.get(k).add(i);
                        }
                    }
                }
                for (Integer k : nulls ) {
                    if (to_sorted_link.contains(k)){
                        out_s_n.get(i).add(k);
                        if (!(inlinks.get(k).contains(i))){
                            inlinks.get(k).add(i);
                        }
                    }

                }
            }

            ArrayList temp_out_s1 =out_s_e.get(i);
            ArrayList temp_out_s2 =out_s_n.get(i);
            temp_out_s1.addAll(temp_out_s2);
            out_s.set(i,temp_out_s1);
        }
        for (int i = 0; i<num_states;i++){
            if (!((inlinks.get(i)).isEmpty())){
                for (Integer k : emits ){
                    if ((inlinks.get(i)).contains(k)){
                        in_s_e.get(i).add(k);
                    }
                }
                for (Integer k : nulls ){
                    if ((inlinks.get(i)).contains(k)){
                        in_s_n.get(i).add(k);
                    }
                }
            }

            ArrayList temp_in_s1 =in_s_e.get(i);
            ArrayList temp_in_s2 =in_s_n.get(i);
            temp_in_s1.addAll(temp_in_s2);
            in_s.set(i,temp_in_s1);
        }

        HashMap make_links_return =  new HashMap<String,ArrayList>(){{put("1",out_s);put("2",in_s);put("3",out_s_e);put("4",out_s_n);put("5",in_s_e);put("6",in_s_n);}};
        return make_links_return;
    }

    static HashMap make_ends(ArrayList<State> states, ArrayList<Integer> emits, ArrayList<Integer> nulls){
        ArrayList end_s = new ArrayList();
        ArrayList end_s_n = new ArrayList();
        ArrayList end_s_e = new ArrayList();

        for (Integer i : emits) {
            State state_i =  states.get(i);
            if (state_i.end_state==1) {
                end_s_e.add(i);
                end_s.add(i);
            }
        }
        for (Integer i :nulls){
            State state_i =  states.get(i);
            if (state_i.end_state==1){
                end_s_n.add(i);
                end_s.add(i);
            }

        }
        HashMap make_ends_return =  new HashMap<String,ArrayList>(){{put("1",end_s);put("2",end_s_e);put("3",end_s_n);}};
        return make_ends_return;
    }
}
