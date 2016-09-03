package edu.sjtu.j_hmm;

import java.util.ArrayList;

/**
 * Created by guanyongqing on 2016/8/24.
 */
public class TR_OBJ {
    ArrayList scale;
    ArrayList prob;
    ArrayList eMat;
    ArrayList labels;
    ArrayList seq;
    ArrayList f;
    ArrayList b;
    String name;
    int len;

    TR_OBJ(ArrayList<String> seq,ArrayList<String> labels){
        this.seq = seq;
        this.len = seq.size(); //length of the sequence
        this.labels = labels;

        //TODO
        this.f = new ArrayList<>();//the forward matrix
        this.b = new ArrayList<>();//the backward matrix
        this.eMat = new ArrayList<>(); //# the precalculate emission matrix
        this.scale = new ArrayList<>();//the scale factors
        this.prob = new ArrayList<>();//the probability of the sequence
        this.name = ""; //TODO


    }
}
