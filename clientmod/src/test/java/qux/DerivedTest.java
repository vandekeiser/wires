package qux;

import api.Base;
import baz.BaseTest;
import org.junit.Test;

public class DerivedTest extends BaseTest {

    @Test
    public void should_pass(){
        new Base();
        System.out.println("DerivedTest OK");
        //if(true) throw new Error("Should see this: DerivedTest");
    }

}
