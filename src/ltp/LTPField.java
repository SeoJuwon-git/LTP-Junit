package ltp;

import java.nio.ByteOrder;
import ltp.LTPException;

public class LTPField {
    public final static int PARAM_INT = 1;
    public final static int PARAM_STR = 2;
    public final static int PARAM_OPQ = 3;

    public final static byte INT_TYPE = 0x49;   // 'I'
    public final static byte STR_TYPE = 0x53;   // 'S'
    public final static byte OPQ_TYPE = 0x4F;   // 'O'

    int type;
    int num;
    byte[] o;

    public LTPField(int num) {
        this.type = PARAM_INT;
        this.num = num;
    }

    public LTPField(String s) throws LTPException {
        byte[] tmp = null;

        if(s != null && s.length() > 0) {
            tmp = s.getBytes();
            this.type = PARAM_STR;
            this.o = new byte[tmp.length];
            System.arraycopy(tmp, 0, this.o, 0, tmp.length);
        } else {
            throw new LTPException("invalid string");
        }
    }

    public LTPField(byte[] opq) throws LTPException {
        if(opq != null && opq.length > 0) {
            this.type = PARAM_OPQ;
            this.o = new byte[opq.length];
            System.arraycopy(opq, 0, this.o, 0, opq.length);
        } else {
            throw new LTPException("invalid opaque");
        }
    }

    public LTPField(byte[] opq, int offset, int len) throws LTPException {
        if(opq != null && opq.length > 0 && offset >= 0 && len > 0) {
            this.type = PARAM_OPQ;
            this.o = new byte[len];
            System.arraycopy(opq, offset, this.o, 0, len);
        } else {
            throw new LTPException("Invalid opaque,offset=" + offset + ",len=" + len);
        }
    }

    public boolean isequal(LTPField f) {
        boolean ret = false;
        int type = f.getType();

        if(this.type == PARAM_INT && type == PARAM_INT){
            if(this.num == f.getInt())
                ret = true;
        } else if(this.type == PARAM_STR && type == PARAM_STR){
            String str = new String(o);
            if(str.compareTo(f.getStr()) == 0)
                ret = true;
        } else if(this.type == PARAM_OPQ && type == PARAM_OPQ) {
            byte[] buf = f.getOpq();
            if(o.length == buf.length) {
                ret = true;
                for(int i = 0; i < o.length; i++) {
                    if(o[i] != buf[i]) {
                        ret = false;
                        break;
                    }
                }
            } 
        }

        return ret;
    }

    public static int byte2int(byte[] buf, int offset) {
        int ret = 0;    //int형은 32비트=4byte

        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {  //빅 엔디안 낮은 주소에 데이터의 높은 바이트부터 저장하는 방식.
            ret  = (int)(buf[offset] & 0xFF) << 24;     //11111111과 & 연산으로 buf[offset]의 값이 그대로 나옴. 8비트가 한 자리?
            ret |= (int)(buf[offset+1] & 0xFF) << 16;   //각 자리수끼리 or 연산 0xFF는 1의 8자리
            ret |= (int)(buf[offset+2] & 0xFF) << 8;
            ret |= (int)(buf[offset+3] & 0xFF);  
        } else {    //반대인 리틀 엔디안의 경우 거꾸로 읽어야 하며 대부분의 인텔 CPU 계열에서 이 방식으로 데이터 저장.
            ret  = (int)(buf[offset] & 0xFF);
            ret |= (int)(buf[offset+1] & 0xFF) << 8;   //각 자리수끼리 or 연산
            ret |= (int)(buf[offset+2] & 0xFF) << 16;
            ret |= (int)(buf[offset+3] & 0xFF) << 24;  
        }

        return ret;
    }

    public static void int2byte(byte[] buf, int offset, int num) {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {  //빅 엔디안 낮은 주소에 데이터의 높은 바이트부터 저장하는 방식.
            buf[offset] = (byte)((num >> 24) & 0xFF);
            buf[offset+1] = (byte)((num >> 16) & 0xFF);
            buf[offset+2] = (byte)((num >> 8) & 0xFF);
            buf[offset+3] = (byte)((num) & 0xFF);
        } else {    //반대인 리틀 엔디안의 경우 거꾸로 읽어야 하며 대부분의 인텔 CPU 계열에서 이 방식으로 데이터 저장.
            buf[offset] = (byte)(num & 0xFF);
            buf[offset+1] = (byte)((num >> 8) & 0xFF);
            buf[offset+2] = (byte)((num >> 16) & 0xFF);
            buf[offset+3] = (byte)((num >> 24) & 0xFF);
        }
    }

    public static int decodeLength(byte[] buf, int offset) throws LTPException {
        int len = 0;

        if(buf[offset] == INT_TYPE) {
            len = 1 + 4;
        } else if(buf[offset] == STR_TYPE || buf[offset] == OPQ_TYPE) {
            len = 1 + 4 + byte2int(buf, offset+1);
        } else {
            throw new LTPException("invalid type=" + buf[offset]);
        }

        return len;
    }

    public static LTPField decodeField(byte[] buf, int offset) throws LTPException {
        int len = 0;
        LTPField f= null;

        len = byte2int(buf, offset+1);

        if(buf[offset] == INT_TYPE) {   // 'I'
            f = new LTPField(len);
        } else if(buf[offset] == STR_TYPE) {    // 'S'
            f = new LTPField(new String(buf, offset+5, len));
        } else if(buf[offset] == OPQ_TYPE) {    // 'O'
            f = new LTPField(buf, offset+5, len);
        } else {
            throw new LTPException("invalid type=" + buf[offset]);
        }

        return f;
    }

    public int getType() {
        return type;
    }

    public int getInt() {
        return this.num;
    }

    public String getStr() {
        return new String(o);
    }

    public byte[] getOpq() {
        return o;
    }

    public int length() throws LTPException {
        int len = 0;

        if(type == PARAM_INT) {
            len = 5;
        } else if(type == PARAM_STR || type == PARAM_OPQ) {
            len = 5 + o.length;
        } else {
            throw new LTPException("invlaid type=" + type);
        }

        return len;
    }

    public byte[] getBytes() throws LTPException {
        byte[] buf;

        if(type == PARAM_INT) {   // 'I'
            buf = new byte[5];
            buf[0] = INT_TYPE;
            int2byte(buf, 1, this.num);
        } else if(type == PARAM_STR) {
            buf = new byte[5+o.length];
            buf[0] = STR_TYPE;
            int2byte(buf, 1, o.length);
            System.arraycopy(this.o, 0, buf, 5, o.length);
        } else if(type == PARAM_OPQ) {
            buf = new byte[5+o.length];
            buf[0] = OPQ_TYPE;
            int2byte(buf, 1, o.length);
            System.arraycopy(this.o, 0, buf, 5, o.length);
        } else {
            throw new LTPException("invalid type=" + type);
        }

        return buf;
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        if(type == PARAM_INT) {
            str.append(new String("I["));
            str.append(Integer.toString(this.num));
            str.append(new String("]"));
        } else if(type == PARAM_STR) {
            str.append(new String("S["));
            str.append(new String(o));
            str.append(new String("]"));
        } else if(type == PARAM_OPQ) {
            str.append(new String("O["));
            str.append(Integer.toString(o.length));
            str.append(new String("]"));
            str.append(new String("["));
            str.append(new String(o));
            str.append(new String("]"));
        
        }

        return str.toString();
    }
}
