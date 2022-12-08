package com.example.privacy.Crypto.Util;


import java.math.BigInteger;

// y^2 = x^3 + 7
public class secp256k1 {
    public BigInteger p ;
    public BigInteger o ;
    public point G ;
    public point I;
    public BigInteger t;
    public BigInteger s;
    public secp256k1(){
        this.p = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F",16);
        BigInteger G_x = new BigInteger("79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798",16);
        BigInteger G_y = new BigInteger("483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8",16);
        this.o = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141",16);
        this.I = new point(number.ZERO, number.ZERO);
        this.G = new point(G_x,G_y);
    }

    public point add(point P,point Q){
        BigInteger s;
        point R = new point(number.ZERO,number.ZERO);

        // 0点的相加
        if(P.x.longValue() == 0 && P.y.longValue() == 0 && isPoint(Q)){
            return Q;
        }
        if(Q.x.longValue() == 0 && Q.y.longValue() == 0 && isPoint(P)){
            return P;
        }
        if(!(isPoint(P)&&isPoint(Q))) return R;
        if(P.x.longValue() == Q.x.longValue()){
            // 同个点
            if(P.y.longValue() == Q.y.longValue()){
                s = number.THREE.multiply((P.x).pow(2)).multiply(inverse(P.y.multiply(number.TWO)));
                R.x = s.pow(2).subtract(P.x.multiply(number.TWO));
            }else{
                return R;
            }
        }else{
            // 不同点
            s = (Q.y.subtract(P.y)).multiply(inverse(Q.x.subtract(P.x)));
            R.x = s.pow(2).subtract(P.x).subtract(Q.x);
        }
        R.y = s.multiply(P.x.subtract(R.x)).subtract(P.y);
        R.x = R.x.mod(this.p);
        R.y = R.y.mod(this.p);
        return R;
    }
    
    public point mul(point P,BigInteger scalar){
        point R = this.I;
        point current = P;
        while(scalar.longValue() != 0){
            if(scalar.mod(number.TWO).longValue() == 1){
                R = this.add(R,current);
            }
            current = this.add(current, current);
            scalar = scalar.divide(number.TWO);
        }
        return R;
    }

    public point negativePoint(point P){
        return new point(P.x, P.y.multiply(new BigInteger("-1")));
    }
    
    public BigInteger calY2(BigInteger x){
        return (x.pow(3).add(number.SEVEN)).mod(this.p);
    }
    
    
    private boolean isPoint(point A){
        return calY2(A.x).longValue() == (A.y.pow(2)).mod(this.p).longValue();
    }

    private BigInteger inverse(BigInteger x){
        EX_GCD(x,this.p);
        return this.s;
    }


    private BigInteger EX_GCD(BigInteger a,BigInteger b){
        if(b.longValue() == 0){
            this.s = number.ONE;
            this.t = number.ZERO;
            return a;
        }
        BigInteger g = EX_GCD(b, a.mod(b));
        BigInteger _t = this.s;
        this.s = this.t;
        this.t = _t.subtract(a.divide(b).multiply(this.t));
        return g;
    }
}

/*
只引入的大数库，其他的函数都是自己写的
包括扩展欧几里得，曲线点判断等等辅助用的函数
*/