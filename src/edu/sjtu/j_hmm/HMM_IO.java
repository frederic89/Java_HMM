package edu.sjtu.j_hmm;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.*;

/**
 * Created by jkron1989 on 16/8/12.
 */
public class HMM_IO {
    //主程序
    public static void main(String args[]) throws IOException, JSONException {
        Build_HMM tmp_hmm = new Build_HMM("/Users/jkron1989/IdeaProjects/Java_HMM/src/edu/sjtu/j_hmm/test2.json");
        //Build_HMM tmp_hmm = new Build_HMM("C:\\Users\\guanyongqing\\IdeaProjects\\Java_HMM\\src\\edu\\sjtu\\j_hmm\\2.json");
        HMM hmm = tmp_hmm.get_HMM();
        for (int i =0; i<hmm.hmm_states.size();i++){
            State a = hmm.hmm_states.get(i);
            System.out.println(a.in_links+" -> "+a.name_state+" -> "+a.out_links);
        }
        System.out.println();
        System.out.println("成功读取json，算法输入端测试：");
        System.out.println("◆状态个数：");
        System.out.println(hmm.num_states);
        System.out.println("◆沉默状态：");
        System.out.println(hmm.nulls);
        System.out.println("◆待拓扑状态：");
        for (int i :  hmm.topo_order) {
            System.out.print("序号："+i+" "+hmm.state_names.get(i)+"；\t");
        }
        System.out.println("\n◆具有发射状态的状态序号（即有发射状态的待拓扑序列）  emits：");
        System.out.println(hmm.emits);
        System.out.println("\n");
        System.out.println("拓扑图的出入边:");
        System.out.print("out_s:");
        System.out.println(hmm.out_s);
        /*System.out.print("out_s_e: ");
        System.out.println(hmm.out_s_e);
        System.out.print("out_s_n：");
        System.out.println(hmm.out_s_n);*/
        System.out.print(" in_s:");
        System.out.println(hmm.in_s);
        /*System.out.print("in_s_e: ");
        System.out.println(hmm.in_s_e);
        System.out.print("in_s_n：");
        System.out.println(hmm.in_s_n);*/
        System.out.print("\n可以终止的状态：");
        System.out.println(hmm.end_s);
        System.out.print("\n读取发射概率（int 行号，String 显状态符号),如‘System.out.println(hmm.e(1,\"B\"));’）：");
        System.out.println("\n"+hmm.e(1,"B"));
        System.out.print("\n显状态种类：");
        System.out.println(hmm.hmm_emission_alphabet);

        /*System.out.println();
        System.out.println("发射概率ArrayList的访问：");
        for (int i=1;i<hmm.num_states;i++) {
            System.out.println(hmm.hmm_states.get(i).get_emissions());
        }*/

        System.out.println();
        System.out.println("从L1（序号1）到序号0显状态的发射概率ArrayList的访问：");
        System.out.println(hmm.hmm_states.get(1)._node_em.em(0));
        System.out.println();
        System.out.println("发射矩阵");
        System.out.println(hmm.mE);
        System.out.println("转移矩阵");
        System.out.println(hmm.mA);


    }

    /*正则表达式检验函数*/
    public  boolean startCheck(String reg,String string)
    {
        boolean tem=false;

        Pattern pattern = Pattern.compile(reg);
        Matcher matcher=pattern.matcher(string);

        tem=matcher.matches();
        return tem;
    }


    static class Build_HMM{
        ArrayList<State> states;
        HMM hmm;
        Build_HMM(String fileName) throws IOException, JSONException {
            this.states = new ArrayList<>();
            JSONObject jsonobj = new JSONObject(new JSONTokener(new FileReader(new File(fileName))));
            Map<String, Object> info = jsonToMap.jsonToMap(jsonobj);//将JSON转为Map
            //System.out.println(info);

            //json中TRANSITION_ALPHABET（tr_al）定义着（隐）状态的种类，不代表外部观察的顺序
            //json中EMISSION_ALPHABET定义着观察状态的种类，不代表外部观察的顺序

            ArrayList<String> tr_al = (ArrayList<String>) info.get("TRANSITION_ALPHABET");
            //System.out.println(tr_al);
            ArrayList<String> em_al = new ArrayList<>();
            String a = ((ArrayList<String>) info.get("EMISSION_ALPHABET")).get(0);
            if (Objects.equals(a, "range")) {
                ArrayList<String> b = (ArrayList<String>) info.get("EMISSION_ALPHABET");
                int j = Integer.parseInt(b.get(1));
                for (int i = 0; i < j; i++) {
                    em_al.add(String.valueOf(i));
                }
            }
            else{
                em_al = (ArrayList<String>) info.get("EMISSION_ALPHABET");  // info.get("EMISSION_ALPHABET")的返回值类型
            }


            // TODO 一堆未检验的引用新建
            //Map tied_t = new HashMap();
            //Map tied_e = new HashMap();
            Map <String,Node_tr> links_TR= new HashMap<>();
            Map <String,Node_em> links_EM= new HashMap<>();
            Map label = new HashMap();
            Map endstate = new HashMap();
            Map<String,List<String>> in_links = new HashMap<>();
            Map fix_tr = new HashMap();
            Map fix_em = new HashMap();


            Node_tr empty_tr = new Node_tr("_EMPTY_TR_", new ArrayList());
            //初始节点没有转移矩阵
            Node_em empty_em = new Node_em("_EMPTY_EM_", new ArrayList());


            for (String name : tr_al){

                List b = new ArrayList();
                in_links.put(name,b);
            }
            for (String name : tr_al){
                List<String> name4link = (ArrayList<String>) ((Map) info.get(name)).get("LINK");
                //System.out.println(name4link);
                for(String in_name : name4link){
                    if(!((in_links.get(in_name)).contains(name))){
                        in_links.get(in_name).add(name);
                    }
                }
            }

            int serial = 0;
            for (String name : tr_al) {
                ArrayList<String> name4link = (ArrayList<String>) ((Map) info.get(name)).get("LINK");
                ArrayList<Double> name4trans = (ArrayList<Double>) ((Map) info.get(name)).get("TRANS");

                //System.out.println(name4trans);
                if (!(Objects.equals(name4trans.get(0), "None"))) {

                    if (Objects.equals(name4trans.get(0), "uniform")) {
                        double i = name4link.size();
                        double d = 1 / i;
                        name4trans.clear();
                        for (int j = 0; j < (int) i; j++) {
                            name4trans.add(d);
                        }
                    }
                    String s = "_TR_" + String.valueOf(serial);
                    Node_tr obj_tr = new Node_tr(s, name4trans);
                    serial++;
                    links_TR.put(name, obj_tr); //_TR_是隐状态序号，开始节点是_TR_0

                }
                if (Objects.equals(name4trans.get(0), "None")){
                    links_TR.put(name,empty_tr);
                }
            }
            int serial1 = 0;
            for (String name : tr_al) {
                ArrayList<Double> name4EMISSION = (ArrayList<Double>) ((Map) info.get(name)).get("EMISSION");//混淆矩阵（发射矩阵）
                ArrayList name4em_list = (ArrayList) ((Map) info.get(name)).get("EM_LIST");
                if (!(Objects.equals(name4EMISSION.get(0), "None"))) {
                    if (Objects.equals(name4EMISSION.get(0), "uniform")) {
                        double i = name4em_list.size();
                        double d = 1.0 / i;
                        name4EMISSION.clear(); //非常重要的一句重置,因为是替换uniform为平均值
                        for (int j = 0; j < (int) i; j++) {
                            name4EMISSION.add(d);
                        }
                    }
                    String s = "_EM_" + String.valueOf(serial1);
                    Node_em obj_em = new Node_em(s, name4EMISSION);
                    serial1++;
                    links_EM.put(name, obj_em);
                }
                if (Objects.equals(name4EMISSION.get(0), "None")){
                    links_EM.put(name,empty_em);
                }
            }
            for (String name : tr_al) {
                List<String> name4link = (ArrayList<String>) ((Map) info.get(name)).get("LINK");

                List name4em_list = (ArrayList) ((Map) info.get(name)).get("EM_LIST");

                String name4label = (String) ((Map) info.get(name)).get("LABEL");
                label.put(name,name4label);

                Integer name4endstate = (Integer) ((Map) info.get(name)).get("ENDSTATE");
                String name4endstate_s = name4endstate.toString();
                if (!(Objects.equals(name4endstate_s, "None"))) {
                    endstate.put(name, name4endstate);//加入为数字,以后解析成boolean
                }
                else{
                    endstate.put(name, null);
                }
                /*打印转移矩阵向量
                List<Double> name4EMISSION = (ArrayList<Double>) ((Map) info.get(name)).get("EMISSION");
                System.out.println(name4EMISSION);
                */

                //System.out.println(links_TR.get(name));//代替links[name][0]
                //System.out.println(links_EM.get(name));//代替links[name][1]
                //System.out.println(name4link);//代替info[name]['LINK']  节点的出口节点
                //System.out.println(in_links.get(name));//代替in_links[name]  节点的入节点
                //System.out.println(name4em_list);//代替info[name]['EM_LIST']
                //System.out.println(name4endstate);//代替info[name]['ENDSTATE']
                //System.out.println(name4label);//代替info[name]['LABEL']

                /*name4link，name4trans读取并定义了转移矩阵的有意义的行/列，
                （隐）状态的发射矩阵和转移矩阵被按观察状态分解成向量后，保存在Node_tr(obj_tr),Node_em(obj_em)中，
                也就是说obj_tr,obj_em本质上保存着是转移概率向量、发射概率向量
                state中保存着到node_tr，node_tr的引用（指针、指向）
                Topo_order输出是图的预备遍历的节点及其种类序号*/

                State state = new State(name,links_TR.get(name),links_EM.get(name),name4link,in_links.get(name),name4em_list,name4endstate,name4label);
                //注意：tr_al的序列长度可以多于json配置文件中State节点种类定义的个数，但这是冗余且没有意义的。
                // 观察顺序在学习类中从文件中读取，json仅仅是配置文件，解耦了外部观察顺序。
                //state里面保存的仍是隐藏状态的种类
                states.add(state);
            }
        this.hmm = new HMM(states,em_al);

        }

        HMM get_HMM(){
            return this.hmm;
        }
    }




}

