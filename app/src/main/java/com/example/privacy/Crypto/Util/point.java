package com.example.privacy.Crypto.Util;
import java.math.BigInteger;

public class point{
    public BigInteger p;
    public BigInteger x;
    public BigInteger y;
    
    public point(BigInteger x,BigInteger y){
        this.p = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F",16);
        this.x = x.mod(this.p);
        this.y = y.mod(this.p);
    }
}

