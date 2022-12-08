package com.example.privacy.Crypto.Util;

import java.math.BigInteger;
import com.example.privacy.Pyfunc.py;

public class EcElgamal{
    public BigInteger d;
    public BigInteger k;
    public secp256k1 E;
    public point P;
    public point G;
    public number n;
    public EcElgamal(BigInteger d){
        this.d = d;
        this.k = new BigInteger("46");
        this.E = new secp256k1();
        this.G = E.G;
        this.P = E.mul(this.G,d);
        this.n = new number();
    }

    public point2 Encrypt(BigInteger m){
        BigInteger r = this.n.Bigrand(this.E.p);
        point R = E.mul(this.G, r);
        point S = E.mul(this.P, r);
        return new point2(R, E.add(S, this.mapPoint(m)));
    }

    public BigInteger Decrypt(point2 C){
        point R = C.P1;
        point MS = C.P2;
        return this.E.add(MS, this.E.negativePoint(this.E.mul(R, this.d))).x.divide(this.k);
    }

    public point mapPoint(BigInteger m){
        BigInteger j = number.ONE;
        point M = new point(number.ZERO, number.ZERO);
        while(j.longValue() < this.k.longValue()){
            M.x = m.multiply(this.k).add(j);
            M.y = this.n.POW(this.E.calY2(M.x), this.E.p.add(number.ONE).divide(number.FOUR), this.E.p).mod(this.E.p);
            if(this.E.calY2(M.x).subtract(M.y.pow(2)).mod(this.E.p).longValue() == 0){ 
                py.print("mapPoint succeed");
                break;
            }
            j = j.add(number.ONE);
        }
        return M;
    }

}

/*
参考
信息明文嵌入椭圆曲线的改进算法及实现  用于进行point map
A Secure Multiple Elliptic Curves Digital Signature
Algorithm for Blockchain - https://arxiv.org/ftp/arxiv/papers/1808/1808.02988.pdf 
https://en.bitcoin.it/wiki/Secp256k1 
选择scep256k1的原因是，这条曲线的运算效率比普通的曲线要快30％，区块链所用的椭圆曲线算法中的曲线也是这条
椭圆曲线算法实现参考https://onyb.gitbook.io/secp256k1-python/point-addition-in-python


改进
在进行point map的时候采用的参数k不是上述论文中提供的值，而是自己实验得到的效率最高的46
java提供的随机数函数是没办法产生大数随机数的，改进了随机数生成写了自己的大数随机数生成用于加密时生成大数随机数
*/