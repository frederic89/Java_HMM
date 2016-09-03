package edu.sjtu.j_hmm;

import java.util.List;

/**
 * Created by jkron1989 on 16/8/17.
 */
abstract class StateNode {
    String getName(){
        return null;
    };
    List<Double> getVectors(){
        return null;
    };
}

