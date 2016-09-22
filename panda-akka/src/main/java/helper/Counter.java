package helper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by saffi on 22/09/16.
 */
public class Counter {

    // rate is slow enough - we do not need long.
    HashMap<String, Integer>  counter=new HashMap<String, Integer>();

    public void add(String st){
        if (st==null){
            return ;
        }
        counter.put(st, counter.getOrDefault(st, 0)+1);
    }

    public Integer get(String st){
        return counter.getOrDefault(st,0);
    }

    public Iterator<Map.Entry<String, Integer>> entryIterator(){
        return Collections.unmodifiableMap(counter).entrySet().iterator();
    }

}
