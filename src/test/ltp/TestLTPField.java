package test.ltp;

import ltp.LTPField;
import ltp.LTPException;
import test.ltp.TestNAS;

public class TestLTPField extends TestNAS {
    String str;
    LTPField f1;
    LTPField f2;
    LTPField f3;
    LTPField f4;

    public TestLTPField(String name) {
        super(name);
    }

    public void setUp() {
        try {
            str = new String("XYZ");

            f1 = new LTPField(2147483647);
            f2 = new LTPField(str);
            f3 = new LTPField(str.getBytes());
        } catch(LTPException le) {
            System.out.println("le="+le.getMessage());
        } catch(Exception e) {
            System.out.println("e="+e.getMessage());
        }
    }

    public void tearDown() {

    }

    boolean isequal(int num) {
        byte[] buf = new byte[4];

        LTPField.int2byte(buf, 0, num);

        return (num == LTPField.byte2int(buf, 0));
    }

    public void testInt() {
        assertTrue(isequal(0));
        assertTrue(isequal(1));
        assertTrue(isequal(127));
        assertTrue(isequal(128));
        assertTrue(isequal(255));
        assertTrue(isequal(256));
        assertTrue(isequal(32766));
        assertTrue(isequal(32767));
        assertTrue(isequal(2147483646));
        assertTrue(isequal(2147483647));
    }

    public void testEqual() {
        LTPField f = null;

        try {
            assertFalse(f1.isequal(f2));
            assertFalse(f1.isequal(f3));
            assertFalse(f2.isequal(f1));
            assertFalse(f2.isequal(f3));
            assertFalse(f3.isequal(f1));
            assertFalse(f3.isequal(f2));

            f = new LTPField(0);
            assertFalse(f1.isequal(f));

            f = new LTPField(new String("XXX"));
            assertFalse(f2.isequal(f));

            f = new LTPField(new String("XXX").getBytes());
            assertFalse(f3.isequal(f));

            f = new LTPField(2147483647);
            assertTrue(f1.isequal(f));

            f = new LTPField(str);
            assertTrue(f2.isequal(f));

            f = new LTPField(str.getBytes());
            assertTrue(f3.isequal(f));

        } catch(LTPException le) {
            System.out.println("le="+le.getMessage());
        } catch(Exception e) {
            System.out.println("e="+e.getMessage());
        }
    }

    public void testEncoding() {
        byte[] buf = null;

        try {
            buf = f1.getBytes();
            assertEquals(5, buf.length);
            assertEquals(LTPField.INT_TYPE, buf[0]);
            assertEquals(2147483647, LTPField.byte2int(buf, 1));
            assertEquals(5, LTPField.decodeLength(buf, 0));

            buf = f2.getBytes();
            assertEquals(8, buf.length);
            assertEquals(LTPField.STR_TYPE, buf[0]);
            assertEquals(3, LTPField.byte2int(buf, 1));
            assertEquals(8, LTPField.decodeLength(buf, 0));

            buf = f3.getBytes();
            assertEquals(8, buf.length);
            assertEquals(LTPField.OPQ_TYPE, buf[0]);
            assertEquals(3, LTPField.byte2int(buf, 1));
            assertEquals(8, LTPField.decodeLength(buf, 0));    
                  
        } catch(LTPException le) {
            System.out.println("le="+ le.getMessage());
        } catch(Exception e) {
            System.out.println("e="+ e.getMessage());
        }
    }

    public void testContents() {
        byte[] buf1 = null;
        byte[] buf2 = null;

        try {
            assertEquals(2147483647, f1.getInt());
            assertEquals(str, f2.getStr());

            buf1 = str.getBytes();
            buf2 = f3.getOpq();

            assertEquals(buf1.length, buf2.length);

            for(int i = 0; i < buf1.length; i++) {
                assertEquals(buf1[i], buf2[i]);
            }
        } catch(Exception e) {
            System.out.println("e=>"+ e.getMessage());
        }
    }

    public void testException() {
        byte[] buf = null;
        String msg = null;
        LTPField f = null;

        try {
            f = new LTPField(msg);
        } catch(LTPException le) {
            assertNotNull(le);
        }

        
        try {
            f = new LTPField(buf);
        } catch(LTPException le) {
            assertNotNull(le);
        }

        
        try {
            buf = new byte[5];
            f = new LTPField(buf, -1, -1);
        } catch(LTPException le) {
            assertNotNull(le);
        }
    }
}
