import java.io.*;

/**
 * Created by saffi on 21/09/16.
 */
public class StreamHelper {

    InputStream stream;
    BufferedReader br ;
    StringBuilder sb=new StringBuilder();
    public StreamHelper(InputStream stream)  {
        this.stream = stream;

        try {
            this.br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    String getString() throws IOException {
        String found = null;
        // todo use char []
        while (br.ready()){
            sb.append((char)br.read());
        }
        final int iEnd= sb.indexOf("}");
        if (iEnd ==-1){
            return null;
        }
        final int iStart= sb.lastIndexOf("{",iEnd);
        if (iStart!=-1){
            found=sb.substring(iStart, iEnd+1);
        }
        sb.delete(0,iEnd+1);
        return found;
    }
}
