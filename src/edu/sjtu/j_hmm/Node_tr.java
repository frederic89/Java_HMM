package edu.sjtu.j_hmm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jkron1989 on 16/8/16.
 */
class Node_tr extends StateNode{
    String name_node_tr;
    int len;
    ArrayList<Double> _tr;

    Node_tr(String name_o, ArrayList<Double> tr) {
        this.name_node_tr = name_o;
        this.len = tr.size();
        this._tr = tr;
    }


    @Override
    public String getName() {
        return this.name_node_tr;
    }

    @Override
    public ArrayList<Double> getVectors(){
        return this._tr;
    }


}
