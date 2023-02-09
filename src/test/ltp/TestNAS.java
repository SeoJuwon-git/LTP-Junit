package test.ltp;

import junit.framework.*;

public class TestNAS extends TestCase {
    
    public TestNAS(String name) {
        super(name);
    }

    public void setUp() {

    }

    public void tearDown() {

    }

    public void AssertFail(long answer) {
        if(answer != -1)
            fail();
    }

    public void AssertFail(int answer) {
        if(answer != -1)
            fail();
    }

    public void AssertEquals(long answer, long sample) {
        try{
            assertEquals(answer, sample);
        }catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void AssertEquals(int answer, int sample) {
        try{
            assertEquals(answer, sample);
        }catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void AssertEquals(Object answer, Object sample) {
        try{
            if(answer instanceof byte[] && sample instanceof byte[])
                assertEquals(new String((byte[])answer), new String((byte[])sample));
            else
                if(answer instanceof char[] && sample instanceof char[])
                    assertEquals(new String((char[])answer), new String((char[])sample));
                else 
                    assertEquals(answer, sample);
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void AssertTrue(boolean b) {
        try {
            assertTrue(b);
        }catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void AssertFalse(boolean b) {
        try {
            assertFalse(b);
        }catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void AssertNotNull(Object obj) {
        try {
            assertNotNull(obj);
        }catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void AssertNull(Object obj) {
        try {
            assertNull(obj);
        }catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
