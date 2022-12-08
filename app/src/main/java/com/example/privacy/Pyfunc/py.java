package com.example.privacy.Pyfunc;

import java.math.BigInteger;

public class py {
    public static void print(String a){
        System.out.println(a);
    }
    public static byte[] bytesAppend(byte[] x,byte a){
        int len = x.length;
        byte[] res = new byte[len + 1];
        for(int i = 0;i<len;i++){
            res[i] = x[i];
        }
        res[len] = a;
        return res;
    }

    public static byte[] bytesAdd(byte[] x,byte[] y){
        int LEN = x.length + y.length;
        byte[] res = new byte[LEN];
        for(int i = 0 ; i < LEN;i++){
            if(i < x.length){
                res[i] = x[i];
            }else{
                res[i] = y[i - x.length];
            }
        }
        return res;
    }
    public static BigInteger b2l(byte[] m){
        return new BigInteger(m);
    }
    public static byte[] l2b(BigInteger m){
        byte[] res = {} ;
        String num = m.toString(16);
        num = (num.length() % 2 == 1 ? "0" + num: num);
        char arr[] = num.toCharArray();
        int i = 0;
        while(i < num.length()){
            res = bytesAppend(res, (byte)Integer.parseInt(String.valueOf(arr[i]) + String.valueOf(arr[i+1]),16));
            i += 2;
        }
        return res;
    }
}

