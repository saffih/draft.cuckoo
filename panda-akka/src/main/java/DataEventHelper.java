import com.google.gson.Gson;

/**
 * Created by saffi on 21/09/16.
 */
public class DataEventHelper {

    static DataEvent fromJson(String st){
        Gson g=new Gson();
        return g.fromJson(st, DataEvent.class);
    }
    static DataEvent fromJsonSilentFail(String st){
            try{
                return fromJson(st);
            }
            catch(RuntimeException e){
                // logger
                return null;
            }
    }
}
