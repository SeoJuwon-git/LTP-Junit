package ltp;

import ltp.LTPParam;
import ltp.LTPException;

import java.nio.ByteOrder;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class LTPCon {
    final static byte LTP_HEAD = (byte) 0xFF; 
    final static byte LTP_VERSION = (byte) 0x10;
    
    final static int LTP_VER_LEN = 1;
    final static int LTP_HEAD_LEN = 2;
    final static int LTP_LENGTH_LEN = 4;

    Socket s = null;
    InputStream in = null;
    OutputStream out = null;

    public LTPCon(Socket s) throws LTPException {
        try {
            this.s = s;
            this.in = s.getInputStream();
            this.out = s.getOutputStream();
        } catch(IOException ioe) {
            throw new LTPException(ioe.getMessage());
        } catch(Exception e) {
            throw new LTPException(e.getMessage());
        }
    }

    public LTPCon(String h, int port, int timeout) throws LTPException {
        try {
            s = new Socket(h, port);
            s.setSoTimeout(timeout * 1000);
            s.setSoLinger(true, 0);
            in = s.getInputStream();
            out = s.getOutputStream();
        } catch(IOException ioe) {
            throw new LTPException(ioe.getMessage());
        } catch(Exception e) {
            throw new LTPException(e.getMessage());
        }
    }

    byte[] read(int len) throws IOException {
        int tmp = 0;
        byte[] buf = new byte[len];

        for(int i = 0; i < len; i++) {
            try {
                tmp = in.read();
            } catch(IOException ioe) {
                throw ioe;
            }
            buf[i] = (byte)tmp;
        }

        return buf;
    }

    public LTPParam LTPRead() throws LTPException {
        byte[] buf;
        int len = -1;

        try {
            buf = read(3);  // 헤드 읽기

            if (buf[0] != LTP_HEAD || buf[1] != LTP_HEAD)
                throw new LTPException("LTPRead: invalid head=" + buf[0]);
            if (buf[2] != LTP_VERSION)
                throw new LTPException("LTPRead: invalid version=" + buf[2]);

            buf = read(4);  // body 길이 읽기

            len = LTPField.byte2int(buf, 0);

            if (len <= 0)
                throw new LTPException("LTPRead: invalid length=" + len);

            buf = read(len);    // body 읽기

            LTPParam param = new LTPParam(buf);

            return param;
        } catch(LTPException le) {
            throw le;
        } catch(IOException ioe) {
            throw new LTPException(ioe.getMessage());
        } catch(Exception e) {
            throw new LTPException(e.getMessage());
        }
    }

    public void LTPWrite(LTPParam p) throws LTPException {
        byte[] head = new byte[3];

        try {
            head[0] = LTP_HEAD;
            head[1] = LTP_HEAD;
            head[2] = LTP_VERSION;

            out.write(head);
            out.write(p.getBytes());
            out.flush();
        } catch(LTPException le) {
            throw le;
        } catch(IOException ioe) {
            throw new LTPException(ioe.getMessage());
        } catch(Exception e) {
            throw new LTPException(e.getMessage());
        }
    }

    public void LTPClose() throws LTPException {
        try {
            in.close();
            out.close();
            s.close();
        } catch(IOException ioe) {
            throw new LTPException(ioe.getMessage());
        } catch(Exception e) {
            throw new LTPException(e.getMessage());
        }
    }
}
