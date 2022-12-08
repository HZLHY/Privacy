package com.example.privacy.Crypto.Util;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.example.privacy.Pyfunc.py;

public class AES {
    public SecretKey key;
    public IvParameterSpec IV;
    public char[] B64table;
    public AES(BigInteger k) throws NoSuchAlgorithmException{
        byte[] iv = "aaaaaaaaaaaaaaaa".getBytes();
        this.IV = new IvParameterSpec(iv);
        char[] table = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/'};
        this.B64table = table;

        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(py.l2b(k));
        keygen.init(128);
        SecretKey originalKey = keygen.generateKey();
        byte[] raw = originalKey.getEncoded();
        this.key = new SecretKeySpec(raw, "AES");
    }

    public String Encrypt(String plaintext) throws NoSuchAlgorithmException,NoSuchPaddingException,InvalidKeyException,IllegalBlockSizeException,BadPaddingException,InvalidAlgorithmParameterException{
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, this.key, this.IV);
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes());
        return this.b64encode(ciphertext);
    }

    public String Decrypt(String ciphertext) throws NoSuchAlgorithmException,NoSuchPaddingException,InvalidKeyException,InvalidAlgorithmParameterException,BadPaddingException,IllegalBlockSizeException{
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, this.key,this.IV);
        py.print(new String(this.b64decode(ciphertext)));
        byte[] plaintext = cipher.doFinal(this.b64decode(ciphertext));
        return new String(plaintext);
    }

    public String b64encode(byte[] plaintext){
        int padnum =  plaintext.length % 3 ;
        if(padnum != 0){
            byte[] padding = new byte[3 - padnum % 3];
            plaintext = py.bytesAdd(plaintext, padding);
        }
        String bin = "";
        for(int i = 0;i < plaintext.length;i++){
            String tmp = new BigInteger(String.valueOf(getUnsignedByte(plaintext[i]))).toString(2);
            if(tmp.length() % 8 != 0){
                for(int j = 0;j < tmp.length() % 8;j++){
                    tmp = "0" + tmp;
                }
            }
            bin += tmp;
        }
        String b64 = "";
        String res = "";
        int i = 0;
        while(i < bin.length()){
            String tmp = "";
            for(int j = 0; j<6;j++){
                tmp += bin.charAt(i+j);
            }
            b64 += this.B64table[new BigInteger(tmp,2).intValue()];
            i += 6;
        }
        if(padnum == 2){
            res = b64.substring(0,b64.length() -1) + '=';
        }else if(padnum == 1){
            res = b64.substring(0,b64.length() - 2) + "==";
        }
        return res;
    }

    public byte[] b64decode(String ciphertext){
        while(ciphertext.charAt(ciphertext.length() - 1) == '='){
            ciphertext = ciphertext.substring(0, ciphertext.length() - 1);
        }
        char[] ciplist = ciphertext.toCharArray();
        String bin = "";
        for(int i = 0;i<ciplist.length;i++){
            int index = new String(this.B64table).indexOf(String.valueOf(ciplist[i]));
            String tmp = new BigInteger(String.valueOf(index)).toString(2);
            if(tmp.length() % 6 != 0){
                for(int j = 0;j < tmp.length() % 6;j++){
                    tmp = "0" + tmp;
                }
            }
            bin += tmp;
        }
        int i = 0;
        byte[] plaintext = {};
        while(i + 8 < bin.length()){
            String tmp = "";
            for(int j = 0; j<8;j++){
                tmp += bin.charAt(i+j);
            }
            plaintext = py.bytesAppend(plaintext, (byte)(new BigInteger(tmp,2).intValue()));
            i += 8;
        }
        return plaintext;
    }
    private static int getUnsignedByte(byte b) {
        return b & 0x0FF;
    }
}

/*
需要一个大数BigInteger 作为key
Encrypt的参数是byte[]类型，传字符串的时候记得带上getbytes()
参考https://www.baeldung.com/java-aes-encryption-decryption


0	A	16	Q	32	g	48	w
1	B	17	R	33	h	49	x
2	C	18	S	34	i	50	y
3	D	19	T	35	j	51	z
4	E	20	U	36	k	52	0
5	F	21	V	37	i	53	1
6	G	22	W	38	m	54	2
7	H	23	X	39	n	55	3
8	I	24	Y	40	o	56	4
9	J	25	Z	41	p	57	5
10	K	26	a	42	q	58	6
11	L	27	b	43	r	59	7
12	M	28	c	44	s	60	8
13	N	29	d	45	t	61	9
14	O	30	e	46	u	62	+
15	P	31	f	47	v	63	/
*/