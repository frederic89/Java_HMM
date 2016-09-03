package edu.sjtu.j_hmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jkron1989 on 16/8/15.
 */
class State extends StateNode {
    String name_state;
    Node_em _node_em;
    Node_tr _node_tr;
    List<String> out_links;
    List<String> in_links;
    List<String> em_letters;
    Integer end_state;
    String label;
    HashMap<String,Integer> _idxtr;
    HashMap<String,Integer> _idxem;


    State(String name_o, Node_tr n_tr, Node_em n_em, List<String> out_s, List<String> in_s, List em_let, Integer end_s, String label) {
        /**
         * name的实际值是隐状态的命名
         * n_tr对应状态本身的转移属性，链接到node_tr读取转移向量
         * n_em对应状态本身的发射属性，链接到node_em读取发射关系向量
         * out_s是隐状态的出边节点
         * in_s是隐状态的入边节点
         * em_let是隐状态对应的发射节点
         * end_s是可终止标签
         * label是隐状态标签（可供外部观察时使用）
         */
        this.name_state = name_o;
        this._node_tr =  n_tr;
        this._node_em =  n_em; //
        this.out_links = out_s;
        this.in_links = in_s;
        this.em_letters = em_let;
        this.end_state = end_s;
        this.label = label;
        this._idxtr= new HashMap<>();
        this._idxem= new HashMap<>();
        for (String name_temp:out_links) {
            _idxtr.put(name_temp,out_links.indexOf(name_temp));
        }
        for (String symbol:em_letters) {
            _idxem.put(symbol,em_letters.indexOf(symbol));
        }
    }

    String get_tr_name() {
        return _node_tr.getName();

    }

    String get_em_name() {
        return _node_em.getName();
    }

    ArrayList get_transitions(){
        return this._node_tr.getVectors();

    }

    ArrayList get_emissions(){
        return _node_em.getVectors();
    }


    double a(State state){
        if(_idxtr.containsKey(state.name_state)){
            int x =  _idxtr.get(state.name_state);
            ArrayList<Double> _tr_list = this._node_tr._tr;
            double r = ((Number) _tr_list.get(x)).doubleValue();
            return r;

        }
        else{return  0.0;}
    }


    double e(String symbol){
        if (_idxem.containsKey(symbol)){
            return this._node_em.em(_idxem.get(symbol));
        }
        else{
            return 0.0;
        }
    }






}


