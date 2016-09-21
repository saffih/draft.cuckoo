/**
 * Created by saffi on 21/09/16.
 */

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestStreamHelper {
    @Test
    public void testGoodMessage() throws IOException {
String example=        "{{{{{ \"event_type\": \"baz\", \"data\": \"ipsum\", \"timestamp\": 1474449973 }\n" +
                "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }\n" +
                "{ \"%�\\~��\n" +
                "{ \"event_type\": \"bar\", \"data\": \"dolor\", \"timestamp\": 1474449973 }\n" +
                "H�+��\n" +
                "{ \"event_type\": \"bar\", \"data\": \"dolor\", \"timestamp\": 1474449980 }\n" +
                "{ \"event_type\": \"foo\", \"data\": \"ipsum\", \"timestamp\": 1474449980 }\n" +
                "{ \"s    ��\n" +
                "{ \"event_type\": \"bar\", \"data\": \"ipsum\", \"timestamp\": 1474449991 }\n" +
                "{ \"event_type\": \"baz\", \"data\": \"ipsum\", \"timestamp\": 1474449994 }\n" +
                "{ \"event_type\": \"bar\", \"d";
        InputStream stream = new ByteArrayInputStream(example.getBytes(StandardCharsets.UTF_8));
        StreamHelper sth = new StreamHelper(stream);
        int cnt =0;
        for( String st; (st=sth.getString())!=null; ){
            Assert.assertEquals(st.charAt(0), '{');
            Assert.assertEquals(st.charAt(st.length()-1), '}');
        }

    }

}
