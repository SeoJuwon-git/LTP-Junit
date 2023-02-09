package test.ltp;

import ltp.LTPField;
import ltp.LTPParam;
import ltp.LTPException;
import test.ltp.TestNAS;

public class TestLTPParam extends TestNAS {
    
    public TestLTPParam(String name) {
        super(name);
    }

    public void setUp() {

    }

    public void tearDown() {

    }

    public void testSize() {
        try {
            LTPParam p = new LTPParam();
            assertEquals(0, p.size());

            p.addIntParam(2147483647);
            p.addStrParam(new String("XYZ"));
            p.addOpqParam(new String("XXX").getBytes());
            assertEquals(3, p.size());

            LTPParam p1 = new LTPParam();
            p1.catParam(p);
            assertEquals(3, p1.size());
        } catch(LTPException le) {
            assertNull(le);
        }

        try {
            LTPParam p1 = new LTPParam();
            LTPParam p2 = new LTPParam();
            p1.catParam(p2);
        } catch(LTPException le) {
            assertNotNull(le);
        }
    }

    public void testConstructor() {
        try {
            LTPParam p = new LTPParam();
            p.addIntParam(2147483647);
            p.addStrParam(new String("XYZ"));
            p.addOpqParam(new String("XXX").getBytes());

            byte[] buf = p.getBytes();
            LTPParam p1 = new LTPParam(buf, 4);
            assertEquals(3, p1.size());

            LTPField[] farr = p.toArray();
            LTPParam p2 = new LTPParam(farr);
            assertEquals(3, p2.size());

            assertTrue(p.isequal(p1));
            assertTrue(p.isequal(p2));

            assertTrue(p1.isequal(p));
            assertTrue(p1.isequal(p2));

            assertTrue(p2.isequal(p));
            assertTrue(p2.isequal(p1));
        } catch(LTPException le) {
            assertNull(le);
        }
    }
}
