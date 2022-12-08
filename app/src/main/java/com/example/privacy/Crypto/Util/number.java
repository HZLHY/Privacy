package com.example.privacy.Crypto.Util;

import java.math.BigInteger;
import java.util.Random;

import com.example.privacy.Pyfunc.py;


public class number {
    public static BigInteger ZERO = new BigInteger("0");
    public static BigInteger ONE = new BigInteger("1");
    public static BigInteger TWO = new BigInteger("2");
    public static BigInteger THREE = new BigInteger("3");
    public static BigInteger FOUR = new BigInteger("4");
    public static BigInteger FIVE = new BigInteger("5");
    public static BigInteger SIX = new BigInteger("6");
    public static BigInteger SEVEN = new BigInteger("7");
    public static BigInteger EIGHT = new BigInteger("8");
    public static BigInteger NINE = new BigInteger("9");

    public number(){

    }
    public BigInteger Bigrand(BigInteger bound){
        // 返回一个0~bound的BigInteger类型的变量
        Random rand = new Random();
        BigInteger times = this.bits(bound);
        String bin = "";
        while(times.longValue() != 1){
            bin = bin + String.valueOf(rand.nextInt(2));
            times = times.subtract(number.ONE);
        }
        return new BigInteger(bin,2);
    }
    public BigInteger bits(BigInteger x){
        // 返回输入的数的位数
        BigInteger res = number.ZERO;
        while(x.longValue() != 0){
            x = x.divide(number.TWO);
            res = res.add(number.ONE);
        }
        return res;
    }

    public BigInteger POW(BigInteger x,BigInteger y,BigInteger p){
        // 计算x的y次方
        BigInteger result = number.ONE;
        BigInteger current = x;
        if(p.longValue() == 0){
            while(y.longValue() != 0){
                if(y.mod(number.TWO).longValue() == 1){
                    result = result.multiply(current);
                }
                current = current.multiply(current);
                y = y.divide(number.TWO);
            }
        }else{
            while(y.longValue() != 0){
                if(y.mod(number.TWO).longValue() == 1){
                    result = result.multiply(current).mod(p);
                }
                current = current.multiply(current).mod(p);
                y = y.divide(number.TWO);
            }
        }
        return result;
    }

    public BigInteger sqrt(BigInteger x){
        BigInteger i = x.divide(number.TWO);
        BigInteger li = i;
        while(i.pow(2).subtract(x).longValue() != 0){
            i = i.subtract(i.pow(2).subtract(x).divide(i.multiply(number.TWO)));
            if(li.subtract(i).longValue() == 0) break;
            li = i;
            py.print(String.valueOf(i));
        }
        return i;
    }
}

// 克服了JAVA中大数处理的困难
// 但是只有简单的加减乘除的操作，需要我们自己去写一些算法
// 包括快速幂，大数随机数，牛顿迭代开方等