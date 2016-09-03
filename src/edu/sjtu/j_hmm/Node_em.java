package edu.sjtu.j_hmm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jkron1989 on 16/8/16.
 */
class Node_em extends StateNode {

    String name_node_em;
    Integer len;
    ArrayList<Double> _em;

    Node_em(String name_o, ArrayList<Double> em){
            this.name_node_em=name_o;
            this.len=em.size();
            this._em=em;
    }
    @Override
    public String getName() {
        return this.name_node_em;
    }

    @Override
    public ArrayList getVectors() {
        return this._em;
    }

    Double em(Integer i){
        return ((Number)this._em.get(i)).doubleValue();
    }



}
