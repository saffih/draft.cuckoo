package saffi;

/**
 * Created by saffi on 28/09/16.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

/**
 * The code is for experiments only, the initial value of 1 is intended for "getting" in trouble and see the rehash and
 * push in action.
 * using bad hash without safty net cause stack explosion.
 * The saftynet should be changed to simpler array scan.
 */
public class Cuckoo<K, T> {

    // a keyer is used for converting from arbitrary key to int[] used int the hash.

    private final Function<K, int[]> keyer;
    int datasize = 10;
    int size = 1;   // size must be big recommended to be prime.
    boolean closed = false;
    ArrayList<T> store = new ArrayList<>(datasize);
    ArrayList<K> kstore = new ArrayList<>(datasize);


    // todo use array list - for simpler code.
    Integer[] arr = new Integer[size];

    // safty not not for use just for speciel cases.
    // it cost on Miss. lookup - consider array scan since it so short for speeding.
    final int SAFTYNORMAL = 5;
    HashMap<K, Integer> saftyNet = new HashMap<>(SAFTYNORMAL);

    //bernstein - good hash
    public int hash1(int[] arr) {
        int hash = 5381;
        for (int i = 0; i < arr.length; ++i) {
            // hash = 33 * hash + key[i];
            hash += (hash << 5) + arr[i];
        }
        return Math.abs(hash % size);
    }

    public int hash2(int[] arr) {
        // return perfectAsIsIntHash(arr);
        return goodhash2(arr);
    }
    
    // just for fun bad hash.
    public int funhash(int[] arr) {
        int hash = 1999;
        for (int i = 0; i < arr.length; ++i) {
            // probably bad choice
            hash += (hash << 7) + arr[i];
        }
        return Math.abs(hash % size);
    }

    public int perfectAsIsIntHash(int[] arr) {
        int hash = 0;
        for (int i = 0; i < arr.length; ++i) {
            // probably bad choice
            hash += arr[i];
        }
        return Math.abs(hash % size);
    }


    public int goodhash2(int[] arr) {
        int hash = 0;
        for (int i = 0; i < arr.length; ++i) {
            // probably bad choice
            hash += 31*hash + arr[i];
        }
        return Math.abs(hash % size);
    }


    public Cuckoo(Function<K, int[]> keyer) {
        this.keyer = keyer;
    }


    public boolean put(K key, T value) {
        int cur = store.size();
        Integer already = getIndex(key);
        if (already != null) {
            store.set(already, value);
            return true;
        }
        store.add(value);
        kstore.add(key);
        boolean res = push1(cur);
        if (!res) {  // undo on fail
            if (closed) {
                store.remove(cur);
                kstore.remove(cur);
            } else {
                saftyNet.put(key, cur);
                rehashUpBig();
                return true;
            }

        }
        return res;
    }

    private void rehashUpBig() {
        if (saftyNet.size()< SAFTYNORMAL){
            // that ok
            return;
        }
        rehashUp();
    }

    private void rehashUp() {
        int newsize = (int) (size * 1.1);
        newsize += newsize % 2 + 1;
        rehash(newsize);
    }

    private void rehashDown(int toremove) {
        rehash(size - toremove);
    }

    private void rehash(int newsize) {
        // todo add code that find first prime bigger then that in some table.
        checkIntegrityBeforeRehash();
        this.arr = new Integer[newsize];
        saftyNet.clear();
        this.size = newsize;
        System.out.println("new size ="+ newsize);
        for (int i = 0; i < kstore.size(); i++) {
            boolean res = push1(i);
            if (!res) {
                // double collisions would cause rehash again.
                // rehashUp();
                // todo - take that one to a third speciel map
                saftyNet.put(kstore.get(i), i);
            }
        }
        return;
    }

    public Integer getIndex(K key) {
        int[] res = this.keyer.apply(key);
        int i1 = hash1(res);
        Integer cur = arr[i1];
        if (cur != null) {
            if (this.kstore.get(cur).equals(key)) {
                return cur;
            }
        }
        int i2 = hash2(res);
        cur = arr[i2];
        if (cur != null) {
            if (this.kstore.get(cur).equals(key)) {
                return cur;
            }
        }
        cur = saftyNet.get(key);
        return cur;
    }

    public T get(K key) {
        Integer found = getIndex(key);
        if (found == null) {
            return null;
        }

        return store.get(found);
    }


    public boolean push1(int cur) {
        final K t = kstore.get(cur);
        int[] res = this.keyer.apply(t);
        int i1 = hash1(res);
        Integer sit = arr[i1];
        if (sit != null) {
            // check the other sit.
            int i2 = hash2(res);
            Integer sit2 = arr[i2];
            if (sit2 == null) {
                arr[i2] = cur;
                return true;
            }
            // preparing the sit
            arr[i1] = null;
            if (!push1(sit)) {
                arr[i1] = sit;
                return false;
            }
            // check th sit is still empty
            sit = arr[i1];
            // loop would cause the cell to be full... and we need to expand.
            if (sit != null) {
                return false;
            }
        }
        arr[i1] = cur;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("{");
        for (int i = 0; i < kstore.size(); i++) {
            s.append("(" + kstore.get(i) + ", " + store.get(i) + "), ");
        }
        s.append("}");
        StringBuilder sarr = new StringBuilder("{");
        for (int i = 0; i < arr.length; i++) {
            sarr.append(arr[i] + ", ");
        }
        sarr.append("}");

        return "Cuckoo{" +
                "keyer=" + keyer +
                ", size=" + size +
                ", arr=" + sarr +
                ", " + s +
                ", safetyNet" + saftyNet +
                '}';
    }

    void checkIntegrityBeforeRehash(){
        // don't check last which is not yet in...
        for (int i=0;i<kstore.size()-1;i++){
            if (getIndex(kstore.get(i))==null){
                throw new RuntimeException("Failed integrity");
            }
        }
    }

    public static void main(String[] args) {
        Cuckoo<Integer, Integer> cackoo = new Cuckoo<Integer, Integer>(i -> {
            int[] res = {i};
            return res;
        });
        cackoo.put(1, 1);
        System.out.println(cackoo);
        cackoo.put(1, 1);
        System.out.println(cackoo);
        cackoo.put(2, 2);
        System.out.println(cackoo);
        cackoo.put(3, 3);
        System.out.println(cackoo);
        cackoo.put(1, 1);
        System.out.println(cackoo);
        cackoo.put(2, 2);
        System.out.println(cackoo);
        cackoo.put(4, 4);
        System.out.println(cackoo);
        cackoo.put(5, 5);
        System.out.println(cackoo);
        cackoo.put(6, 6);
        System.out.println(cackoo);
        cackoo.put(7, 7);
        System.out.println(cackoo);
        cackoo.put(8, 8);
        System.out.println(cackoo);
        cackoo.put(9, 9);
        System.out.println(cackoo);
        cackoo.put(10, 10);
        System.out.println(cackoo);
        cackoo.put(17, 17);
        System.out.println(cackoo);
        Integer o1 = cackoo.get(1);
        Integer o2 = cackoo.get(2);
        Integer o3 = cackoo.get(3);
        Integer o4 = cackoo.get(4);
        Integer o5 = cackoo.get(5);
        Integer o6 = cackoo.get(6);
        Integer o7 = cackoo.get(7);
        Integer o8 = cackoo.get(8);
        Integer o9 = cackoo.get(9);
        Integer o10 = cackoo.get(10);
        Integer o17 = cackoo.get(17);
        System.out.println(cackoo);
        int i;
        final int limit = 8000;
        for(i=0; i< limit; i++){
            cackoo.put(i,i);
        }
        for(int j=0; j<cackoo.arr.length; j++){
            if (cackoo.arr[j]!=null){
                i--;
            }

        }
        if (limit<10000) {
            System.out.println(cackoo);
        }
        System.out.print(i);

    }
}



