package ltp;

import java.util.ArrayList;
import java.nio.ByteBuffer;
import ltp.LTPField;
import ltp.LTPException;

public class LTPParam {
    ArrayList param;

    public LTPParam() {
        param = new ArrayList();
    }

    public LTPParam(LTPField[] farr) {
        param = new ArrayList();

        for(int i = 0; i < farr.length; i++)
            param.add(farr[i]);
    }

    public LTPParam(byte[] buf) throws LTPException {
        param = new ArrayList();
        
        int total = buf.length;
        int nread = 0;

        while(nread < total) {
            addParam(LTPField.decodeField(buf, nread));
            nread += LTPField.decodeLength(buf, nread);
        }
    }

    public LTPParam(byte[] buf, int offset) throws LTPException {
        param = new ArrayList();
        
        int total = buf.length;
        int nread = offset;

        while(nread < total) {
            addParam(LTPField.decodeField(buf, nread));
            nread += LTPField.decodeLength(buf, nread);
        }
    }

    public boolean isequal(LTPParam p) {
        LTPField[] farr1 = this.toArray();
        LTPField[] farr2 = p.toArray();

        if(farr1.length != farr2.length) {
            return false;
        }

        for(int i = 0; i < farr1.length; i++) {
            if(farr1[i].isequal(farr2[i]) == false)
                return false;
        }

        return true;
    }

    public int size() {
        return param.size();
    }

    public void addIntParam(int num) {
        param.add(new LTPField(num));
    }

    public void addStrParam(String str) throws LTPException {
        param.add(new LTPField(str));
    }

    public void addOpqParam(byte[] opq) throws LTPException {
        param.add(new LTPField(opq));
    }

    public void addParam(LTPField f) throws LTPException {
        param.add(f);
    }

    public void catParam(LTPParam p)throws LTPException {
        LTPField[] farr = p.toArray();

        if(farr == null)
            throw new LTPException("empty array");

        for(int i = 0; i < farr.length; i++)
            addParam(farr[i]);
    }

    public byte[] getBytes() throws LTPException {
        int total = 0;
        int nread = 0;
        Object[] objs = param.toArray();
        LTPField f = null;

        if(objs == null)
            throw new LTPException("empty array");

        for(int i = 0; i < objs.length; i++) {  //get total bytes
            f = (LTPField)objs[i];
            total += f.length();
        }

        byte[] buf = new byte[total+4];

        for(int i= 0; i < objs.length; i++) {
            f = (LTPField)objs[i];
            byte[] tmp = f.getBytes();

            System.arraycopy(tmp, 0, buf, 4+nread, tmp.length);

            nread += tmp.length;
        }

        LTPField.int2byte(buf, 0, total);   //set total bytes

        return buf;
    }

    public LTPField[] toArray() {
        Object[] objs = param.toArray();
        LTPField[] farr = null;

        if(objs.length > 0) {
            farr = new LTPField[objs.length];
            
            for(int i = 0; i < objs.length; i++)
                farr[i] = (LTPField)objs[i];
        }

        return farr;
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        Object[] objs = param.toArray();
        LTPField f = null;

        for(int i = 0; i < objs.length; i++) {
            f = (LTPField)objs[i];
            str.append(f.toString());
        }

        return str.toString();
    }

    public void pirntln() {
        StringBuffer str = new StringBuffer();  //사용 안되는 문제?
        Object[] objs = param.toArray();
        LTPField f= null;

        for(int i = 0; i < objs.length; i++) {
            f = (LTPField)objs[i];
            System.out.println(f.toString());
        }
    }
}
