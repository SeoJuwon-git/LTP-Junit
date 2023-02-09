package test.ltp;

import ltp.*;
import test.ltp.TestNAS;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestLTP extends TestNAS {

    public TestLTP(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(TestLTPField.class);
        suite.addTestSuite(TestLTPParam.class);
        suite.addTestSuite(TestLTPCon.class);

        return suite;
    
    }
}
