package test.ltp;

import test.ltp.TestNAS;

import ltp.LTPCon;
import ltp.LTPField;
import ltp.LTPParam;
import ltp.LTPException;

import java.util.Random;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.io.IOException;

public class TestLTPCon extends TestNAS {

    class LTPListener extends Thread {
        int port;

        public LTPListener(int port) {
            this.port = port;
        }

        public void run() {
            try {
                InetSocketAddress lp = new InetSocketAddress(port);
                ServerSocket ss = new ServerSocket();
                ss.setReuseAddress(true);

                ss.bind(lp);

                Socket s = null;
                LTPCon con = null;

                while(true) {
                    try {
                        s = ss.accept();
                        s.setSoLinger(true, 0);

                        con = new LTPCon(s);

                        LTPParam p  = con.LTPRead();
                        LTPField[] f = p.toArray();

                        System.out.print("[");
                        for(int i = 0; i < f.length; i++) {
                            switch(f[i].getType()) {
                                case LTPField.PARAM_INT:System.out.print("I");break;
                                case LTPField.PARAM_STR:System.out.print("S");break;
                                case LTPField.PARAM_OPQ:System.out.print("O");break;
                            }
                        }
                        System.out.println("]");

                        LTPParam r = new LTPParam(f);

                        con.LTPWrite(r);

                    } catch(IOException ioe) {
                        System.out.println(ioe.getMessage());
                        assertNull(ioe);
                    } catch(Exception e) {
                        System.out.println(e.getMessage());
                        assertNull(e);
                    } finally {
                        try {
                            con.LTPClose();
                        } catch(Exception x) {
                            System.out.println(x.getMessage());
                        }
                    }
                }
            } catch(IOException ioe) {
                assertNull(ioe);
            } catch(Exception e) {
                assertNull(e);
            }
        }
    }

    LTPCon con; //ltp 연결
    LTPParam p; //요청
    LTPParam r; //응답

    LTPListener server;

    static boolean running = false;

    public TestLTPCon(String name) {
        super(name);

        try {
            if(running == false) {
                server = new LTPListener(9876);
                server.start();
                running = true;

                Thread.sleep(2000);
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void setUp() {

    }

    public void tearDown() {

    }

    boolean echo(LTPField f) throws LTPException {
        try {
            con = new LTPCon("localhost", 9876, 5);

            p = new LTPParam();
            p.addParam(f);

            con.LTPWrite(p);
            r = con.LTPRead();

            return r.isequal(p);    //return (r == p)

        } catch(LTPException le) {
            assertNull(le);
        } catch(Exception e) {
            assertNull(e);
        } finally {
            try {
                con.LTPClose();
            } catch(Exception ee) {
                System.err.println(ee.getMessage());
            }
        }

        return false;
    }

    public void testLTPInt() {
        try {
            assertTrue(echo(new LTPField(-2147483648)));
            assertTrue(echo(new LTPField(-32768)));
            assertTrue(echo(new LTPField(-32767)));
            assertTrue(echo(new LTPField(-256)));
            assertTrue(echo(new LTPField(-255)));
            assertTrue(echo(new LTPField(-128)));
            assertTrue(echo(new LTPField(-127)));
            assertTrue(echo(new LTPField(0)));
            assertTrue(echo(new LTPField(127)));
            assertTrue(echo(new LTPField(128)));
            assertTrue(echo(new LTPField(255)));
            assertTrue(echo(new LTPField(256)));
            assertTrue(echo(new LTPField(32767)));
            assertTrue(echo(new LTPField(32768)));
            assertTrue(echo(new LTPField(2147483647)));
        } catch(LTPException le) {
            System.out.println("int: le="+ le.getMessage());
            le.printStackTrace();
        } catch(Exception e) {
            System.out.println("int: e="+ e.getMessage());
            e.printStackTrace();
        }
    }

    public void testLTPStr() {
        try {
            assertTrue(echo(new LTPField(" ")));
            assertTrue(echo(new LTPField("X")));
            assertTrue(echo(new LTPField("XY")));
            assertTrue(echo(new LTPField("X Y")));
            assertTrue(echo(new LTPField("XYZ")));
            assertTrue(echo(new LTPField("X Y Z")));
            assertTrue(echo(new LTPField("X Y Z")));
            assertTrue(echo(new LTPField("!@#$%^&*()_dfjkl\"dfjlvbckjeir")));
        } catch(LTPException le) {
            System.out.println("str: le="+ le.getMessage());
            le.printStackTrace();
        } catch(Exception e) {
            System.out.println("str: e="+ e.getMessage());
            e.printStackTrace();
        }
    }

    public void testLTPOpq() {
        try {
            assertTrue(echo(new LTPField(new String(" ").getBytes())));
            assertTrue(echo(new LTPField(new String("X").getBytes())));
            assertTrue(echo(new LTPField(new String("XY").getBytes())));
            assertTrue(echo(new LTPField(new String("X Y").getBytes())));
            assertTrue(echo(new LTPField(new String("XYZ").getBytes())));
            assertTrue(echo(new LTPField(new String("X Y Z").getBytes())));
            assertTrue(echo(new LTPField(new String("X Y Z").getBytes())));
            assertTrue(echo(new LTPField(new String("!@#$%^&*()_dfjkl\"dfjlvbckjeir").getBytes())));
        } catch(LTPException le) {
            System.out.println("opq: le="+ le.getMessage());
            le.printStackTrace();
        } catch(Exception e) {
            System.out.println("opq: e="+ e.getMessage());
            e.printStackTrace();
        }
    }

    boolean echo(int loops) throws LTPException {
        try {
            int type;
            int val;
            int len;
            String str;
            byte[] msg;

            con = new LTPCon("127.0.0.1", 9876, 5);

            Random rand = new Random();
            p = new LTPParam();

            for(int i = 0; i < loops; i++) {
                type = rand.nextInt(3) + 1; //1~3 중 랜덤값

                switch(type) {
                    case 1:
                        val = rand.nextInt(2147483647) + 1;
                        p.addIntParam(val);
                        break;
                    case 2:
                        len = rand.nextInt(255) + 1;
                        msg = new byte[len];

                        for(int j = 0; j < len; j++)
                            msg[j] = (byte)(rand.nextInt(127) + 1);

                        p.addStrParam(new String(msg));
                        break;
                    case 3:
                        len = rand.nextInt(255) + 1;
                        msg = new byte[len];

                        for(int j = 0; j < len; j++)
                            msg[j] = (byte)(rand.nextInt(127) + 1);

                        p.addOpqParam(new String(msg).getBytes());
                        break;
                }
            }

            System.out.println("p.size() = " + p.size());

            con.LTPWrite(p);
            r = con.LTPRead();

            return r.isequal(p);    // return (r == p)

        } catch(LTPException le) {
            System.err.println(le.getMessage());
            assertNull(r);
        } catch(Exception e) {
            System.err.println(e.getMessage());
            assertNull(r);
        } finally {
            try {
                con.LTPClose();
            } catch(Exception ee) {
                System.err.println(ee.getMessage());
            }
        }

        return false;
    }

    public void testLTPCombo() {
        try {
            Random rand = new Random();
            int loops = rand.nextInt(50) + 1;

            for(int i = 0; i < loops; i++) {
                int len = rand.nextInt(255) + 1;
                assertTrue(echo(len));
            }
        } catch(LTPException le) {
            assertNull(le);
        } catch(Exception e) {
            assertNull(e);
        }
    }
}
