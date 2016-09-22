package helper; /**
 * Created by saffi on 21/09/16.
 */

import org.testng.Assert;
import org.testng.annotations.Test;

public class CounterTest {

    @Test
    public void testEmpty(){
        Counter counter = new Counter();
        counter.add(null);
    }


    @Test
    public void testWord(){
        Counter counter = new Counter();
        Assert.assertEquals(Math.toIntExact(counter.get("word")), 0);
        counter.add("word");
        Assert.assertEquals(Math.toIntExact(counter.get("word")), 1);
        counter.add("word");
        Assert.assertEquals(Math.toIntExact(counter.get("word")), 2);
    }
}
