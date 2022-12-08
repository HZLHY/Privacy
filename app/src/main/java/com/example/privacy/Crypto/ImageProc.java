package com.example.privacy.Crypto;

import static com.example.privacy.Pyfunc.py.bytesAdd;


import com.example.privacy.Pyfunc.py;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import com.example.privacy.Crypto.Util.EcElgamal;


public class ImageProc {
    public String imagepath;
    public BigInteger key;
    public int seed; // 随机数序列的种子
    public byte a;

    public ImageProc(BigInteger k) {
        this.key = k;
        this.seed = Math.abs((new Random()).nextInt());
    }

    public void Encrypt(String srcFilePath, String destFilePath) {
        // Sten 加密强度
        try {
            int Sten = 128;
            byte flag = 0;
            EcElgamal Ecc = new EcElgamal(this.key);
            byte[] randlen = {};
            FileInputStream file = new FileInputStream(srcFilePath);
            byte[] i = myreadallbytes(file);
            file.close();
            BigInteger image_Int = py.b2l(i);
            py.print("[+]End1");
            // 生成序列
            Random r = new Random(this.seed);
            String randomBin_base = new BigInteger(String.valueOf(Math.abs(r.nextInt()))).toString(2);
            while (randomBin_base.length() < Sten) {
                randomBin_base += new BigInteger(String.valueOf(Math.abs(r.nextInt()))).toString(2);
            }
            py.print("[+]End2");
            randlen = py.l2b(new BigInteger(String.valueOf(py.l2b(new BigInteger(randomBin_base, 2)).length)));
            py.print("[+]End3");
            String randomBin = "0";
            while (randomBin.length() < py.b2l(i).toString(2).length()) {
                py.print("[+]End in While"+py.b2l(i).toString(2).length());
                randomBin += randomBin_base;
                py.print("[+]End in While");
            }

            // 需要调长度
            randomBin = randomBin.substring(0, py.b2l(i).toString(2).length());
            BigInteger rand = new BigInteger(randomBin, 2);
            BigInteger res = image_Int.not().multiply(new BigInteger("-1"));
            flag = res.longValue() != res.abs().longValue() ? (byte) 1 : (byte) 0;
            py.print("[+]End4");
            byte[] out = py.l2b(res.abs());

            out = bytesAdd(out, py.l2b(new BigInteger(randomBin_base, 2)));
            py.print("[+]End5");
            out = py.bytesAppend(out, flag);
            out = bytesAdd(out, randlen);
            // 写文件
            FileOutputStream output = new FileOutputStream(destFilePath);

            output.write(i);
            output.close();
            py.print("Encrypt succeed!flag: " + String.valueOf(flag));
        } catch (FileNotFoundException e) {
            py.print(e.toString());
        } catch (IOException e) {
            py.print(e.toString());
        }
    }
    /*
        加密图片结构：密文 || randBin_base || randlen || flag
    */

    public void Decrypt(String srcFilePath, String destFilePath) {
        try {
            FileInputStream file = new FileInputStream(srcFilePath);
            byte[] c = myreadallbytes(file);
            file.close();

            byte flag = 1;
            byte[] rlen = {c[c.length - 1]};
            byte[] random_base_bytes = {};


            String randlen = py.b2l(rlen).toString(10);
            for (int i = 128; i > 0; i--) {
                random_base_bytes = py.bytesAppend(random_base_bytes, c[c.length - 2 - i]);
            }
            String randomBin_base = py.b2l(random_base_bytes).toString(2);
            byte[] enc = new byte[c.length - 2 - Integer.parseInt(randlen)];

            String randomBin = "";
            while (randomBin.length() < py.b2l(enc).toString(2).length()) {
                randomBin += randomBin_base;
            }
            BigInteger rand = new BigInteger(randomBin, 2);
            BigInteger res = py.b2l(enc).multiply(new BigInteger("-1")).not();

            byte[] out = py.l2b(res);
            py.print(String.valueOf(out.length));
            FileOutputStream file2 = new FileOutputStream(destFilePath);
            file2.write(c);
            file2.close();
        } catch (FileNotFoundException e) {
            py.print(e.toString());
        } catch (IOException e) {
            py.print(e.toString());
        }
    }

    public byte[] myreadallbytes(FileInputStream file) throws IOException {
        byte[] res = new byte[0];
        byte[] tmp = new byte[1024 * 1024];
        int B = file.read(tmp);
        while (B != -1) {
            res = py.bytesAdd(res, tmp);
            B = file.read(tmp);
            py.print(String.valueOf(B));
        }

        return res;
    }
}

