import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;


public class GeneratorsRealization extends Generators{
    private static int unsignedToBytes(byte b) { return b & 0xFF; }

    static List<Integer> buildedGenRealization(int length){
        List<Integer> list = new ArrayList<>();
        byte mass[] = buildedGen(length);
        for (byte a: mass){
            list.add(unsignedToBytes(a));
        }
        return list;
    }
    static List<Integer> lehmerHigh(int start,long length){
        List<Integer> list = new ArrayList<>();
        BigInteger a = BigInteger.valueOf(2).pow(16).add(BigInteger.ONE);
        BigInteger m = BigInteger.valueOf(2).pow(32);

        for (long i=0;i<length;i++){
            list.add(unsignedToBytes((byte)(start >>> 24)));
            start = lehmer(BigInteger.valueOf(start),a,m);
        }

        return list;
    }
    static List<Integer> lehmerLow(int start, long length){
        List<Integer> list = new ArrayList<>();
        BigInteger a = BigInteger.valueOf(2).pow(16).add(BigInteger.ONE);
        BigInteger m = BigInteger.valueOf(2).pow(32);
        for (long i=0;i<length;i++){
            list.add(unsignedToBytes((byte)(start & (0xff))));
            start = lehmer(BigInteger.valueOf(start),a,m);
        }
        for (Integer xe:list)
            System.out.println(xe);
        return list;

    }

    private static int giffeL11(int x){
        return ((((x ^ (x>>2)) & 1) << 10)  | (x >>1));
    }
    private static int giffeGetBitL11(int x){
        return ((((x ^ (x>>2)) & 1)));
    }

    private static int giffeL9(int y){
        return ((((y ^ (y>>4) ^ (y>>3) ^ (y>>1)) & 1) << 8)  | (y >>1));
    }
    private static int giffeGetBitL9(int y){
        return ((y ^ (y>>4) ^ (y>>3) ^ (y>>1)) & 1);
    }

    private static int giffeL10(int s){
        return  ((((s ^ (s>>3)) & 1) << 9)  | (s >>1));
    }
    private static int giffeGetBitL10(int s){
        return ((((s ^ (s>>3)) & 1)));
    }
    private static List<Integer> giffeRealization(int x, int y, int s, int length){
        List <Integer> xbits = new ArrayList<>();
        List <Integer> ybits = new ArrayList<>();
        List <Integer> sbits = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        for (long i=0;i<length;i++){
            xbits.add(giffeGetBitL11(x));
            x = giffeL11(x);

            ybits.add(giffeGetBitL9(y));
            y = giffeL9(y);

            sbits.add(giffeGetBitL10(s));
            s = giffeL10(s);

        }
        String bits = "";
        for (int i=0; i<xbits.size();i++){
            if (i%8==0 & i>0)  {
                list.add(unsignedToBytes((byte)Integer.parseInt(bits,2)));
                bits = "";
            }
            bits += Byte.toString(giffe(xbits.get(i),ybits.get(i),sbits.get(i)));

        }
        return list;
    }

    private static int L20Generator(int s){
        return  ((((s ^ (s>>17) ^(s>>15) ^ (s>>11)) & 1) << 19)  | (s >>1));
    }
    private static int L20GetBit(int s){
        return ((s ^ (s>>17) ^(s>>15) ^ (s>>11)) & 1);
    }
    static List<Integer> L20Realization(int start, int length){
        List <Integer> list = new ArrayList<>();
        String s= "";
        for (int i=0;i<length;i++){
            if (i%8==0 & i>0)  {
                list.add(unsignedToBytes((byte)Integer.parseInt(s,2)));
                s = "";
            }
            s += L20GetBit(start);
            start = L20Generator(start);
        }
        return list;
    }

    private static BigInteger L89Generator(BigInteger x){
        return (((x.xor(x.shiftRight(51))).and(BigInteger.ONE)).shiftLeft(88)).or(x.shiftRight(1));
    }
    private static BigInteger L89GetBit(BigInteger x){
        return (((x.xor(x.shiftRight(51))).and(BigInteger.ONE)));
    }

    static List<Integer> L89Realization(int x, int length){
        BigInteger start = BigInteger.valueOf(x);
        List <Integer> list = new ArrayList<>();
        String s= "";
        start = L89Generator(start);
        for (int i=0;i<length;i++){
            if (i%8==0 & i>0)  {
                list.add(unsignedToBytes((byte)Integer.parseInt(s,2)));
                s = "";
            }
            s += L89GetBit(start).intValue();
            start = L89Generator(start);
        }
        for (Integer xe: list)
            System.out.println(xe);
        return list;
    }

    static List<Integer> bibliotekarRealization(String FILE_NAME) throws IOException {
        String text = "";
        try(FileReader reader = new FileReader(FILE_NAME))
        {
            // читаем посимвольно
            int c;
            while((c=reader.read())!=-1){
                text+=(char)c;
            }
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
        return bibliotekar(text);
    }

    static List<Integer> volframRealization(long r0, int length){
        List<Integer> list = new ArrayList<>();
        String s = "";
        for (int i=0;i<length;i++){
            if (i%8==0 & i>0)  {

                list.add(unsignedToBytes((byte)Integer.parseInt(s,2)));
                s = "";
            }
            s+= r0%2;

            r0 = volfram(r0);

        }

        return list;
    }

    static List<Integer> BMRealization(int T0, int length){
        BigInteger Tstart = BigInteger.valueOf(T0);
        BigInteger p = new BigInteger("0CEA42B987C44FA642D80AD9F51F10457690DEF10C83D0BC1BCEE12FC3B6093E3", 16);
        BigInteger a = new BigInteger("5B88C41246790891C095E2878880342E88C79974303BD0400B090FE38A688356", 16);
        BigInteger q = new BigInteger("675215CC3E227D3216C056CFA8F8822BB486F788641E85E0DE77097E1DB049F1", 16);
        BigInteger res = (p.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2)));
        BigInteger T1 = BM(Tstart,a,p);
        String s = "";
        List<Integer> list = new ArrayList<>();
        for (int i=0;i<length;i++){
            if (i%8==0 & i>0)  {
                list.add(unsignedToBytes((byte)Integer.parseInt(s,2)));
                s="";
            }
        if (T1.compareTo(res) == -1) s+="1";
            else s+="0";
            T1 = BM(T1,a,p);
        }
        return list;
    }

    static List<Integer> BMBytesRealization(int T0, int length) {
        BigInteger Tstart = BigInteger.valueOf(T0);
        BigInteger p = new BigInteger("0CEA42B987C44FA642D80AD9F51F10457690DEF10C83D0BC1BCEE12FC3B6093E3", 16);
        BigInteger a = new BigInteger("5B88C41246790891C095E2878880342E88C79974303BD0400B090FE38A688356", 16);
        BigInteger q = new BigInteger("675215CC3E227D3216C056CFA8F8822BB486F788641E85E0DE77097E1DB049F1", 16);
        BigInteger T1 = BM(Tstart, a, p);
        BigInteger pMinus1 = p.subtract(BigInteger.ONE);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            BigInteger k = (BigInteger.ONE);
            while (true) {
                if (T1.compareTo(k.multiply(pMinus1).divide(BigInteger.valueOf(256))) == -1 &&
                        T1.compareTo(k.add(BigInteger.ONE).multiply(pMinus1).divide(BigInteger.valueOf(256))) == 1) {
                    list.add(k.intValue());
                    break;
                }
                k = k.add(BigInteger.ONE);
            }
        }
        return list;
    }

    static List<Integer> BBSRealization(int r0,int length){
        BigInteger rStart = BigInteger.valueOf(r0);
        BigInteger p = new BigInteger("0D5BBB96D30086EC484EBA3D7F9CAEB07", 16);
        BigInteger q = new BigInteger("425D2B9BFDB25B9CF6C416CC6E37B59C1F", 16);
        BigInteger pq = p.multiply(q);

        BigInteger r1 = BBS(rStart,pq);
        String s = "";
        List<Integer> list = new ArrayList<>();
        for (int i=0;i<length;i++){
            if (i%8==0 & i>0)  {
                list.add(unsignedToBytes((byte)Integer.parseInt(s,2)));
                s = "";
            }
            s += r1.mod(BigInteger.valueOf(2));
            r1 = BBS(r1,pq);
        }
        return list;
    }

    static List<Integer> BBSBytesRealization(int r0,int length){
        BigInteger rStart = BigInteger.valueOf(r0);
        BigInteger p = new BigInteger("0D5BBB96D30086EC484EBA3D7F9CAEB07", 16);
        BigInteger q = new BigInteger("425D2B9BFDB25B9CF6C416CC6E37B59C1F", 16);
        BigInteger pq = p.multiply(q);

        BigInteger r1 = BBS(rStart,pq);
        List<Integer> list = new ArrayList<>();
        for (int i=0;i<length;i++){
            list.add(unsignedToBytes((byte)r1.mod(BigInteger.valueOf(256)).intValue()));
            r1 = BBS(r1,pq);
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
     //  volframRealization(36,100);
    }
}
