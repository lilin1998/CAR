package util.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import sun.security.rsa.RSAPrivateCrtKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;




public class CryptographicHelper
{
    private static CryptographicHelper cryptographicHelper = null;        
    
    private static final String DEFAULT_CHARSET_NAME = "ISO-8859-1";
    private static final String DEFAULT_CERTIFICATE_ALGORITHM_NAME = "SHA256withRSA";
    private static final String DEFAULT_ASYMMETRIC_ENCRYPTION_ALGORITHM_NAME = "RSA";
    private static final String SHA1_ALGORITHM_NAME = "SHA-1";

    
    
    public CryptographicHelper() 
    {
    }
    
    
    
    public static CryptographicHelper getInstance()
    {
        if (cryptographicHelper == null)
        {
            cryptographicHelper = new CryptographicHelper();
        }

        return cryptographicHelper;
    }
    
    
    
    public X509Certificate generateX509Certificate(String distinguishedName, KeyPair keyPair, int days)
    {
        try
        {
            X509CertInfo x509CertInfo = new X509CertInfo();
            Date fromDate = new Date();
            Date toDate = new Date(fromDate.getTime() + days * 86400000l);
            CertificateValidity interval = new CertificateValidity(fromDate, toDate);
            BigInteger serialNumber = new BigInteger(64, new SecureRandom());
            X500Name x500Name = new X500Name(distinguishedName);
            
            x509CertInfo.set(X509CertInfo.VALIDITY, interval);
            x509CertInfo.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(serialNumber));
            x509CertInfo.set(X509CertInfo.SUBJECT, x500Name);
            x509CertInfo.set(X509CertInfo.ISSUER, x500Name);
            x509CertInfo.set(X509CertInfo.KEY, new CertificateX509Key(keyPair.getPublic()));
            x509CertInfo.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
            AlgorithmId algorithmId = new AlgorithmId(AlgorithmId.sha256WithRSAEncryption_oid);
            x509CertInfo.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algorithmId));
            
            X509CertImpl x509CertImpl = new X509CertImpl(x509CertInfo);
            x509CertImpl.sign(keyPair.getPrivate(), DEFAULT_CERTIFICATE_ALGORITHM_NAME);

            algorithmId = (AlgorithmId)x509CertImpl.get(X509CertImpl.SIG_ALG);
            x509CertInfo.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algorithmId);
            x509CertImpl = new X509CertImpl(x509CertInfo);
            x509CertImpl.sign(keyPair.getPrivate(), DEFAULT_CERTIFICATE_ALGORITHM_NAME);
            
            return x509CertImpl;
        }
        catch(IOException | CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException ex)
        {
            ex.printStackTrace();
            
            return null;
        }
    }
    
    
    
    public String getRSAPrivateKeyAsHexString(RSAPrivateCrtKeyImpl rsaPrivateCrtKeyImpl)
    {
        try
        {
            byte[] byteArray = rsaPrivateCrtKeyImpl.encode();            
            return byteArrayToHexString(byteArray);
        }
        catch(InvalidKeyException ex)
        {
            return null;
        }
    }
    
    
    
    public String getRSAPublicKeyAsHexString(RSAPublicKeyImpl rsaPublicKeyImpl)
    {
        return byteArrayToHexString(rsaPublicKeyImpl.getEncoded());
    }
    
    
    
    public RSAPublicKeyImpl getRSAPublicKeyFromHexString(String publicKeyHexString)
    {
        try
        {
            byte[] publicKeyByteArray = hexStringToByteArray(publicKeyHexString);

            return new RSAPublicKeyImpl(publicKeyByteArray);
        }
        catch(InvalidKeyException ex)
        {
            ex.printStackTrace();
            
            return null;
        }
    }
    
    
    
    public byte[] doRSAEncryption(String stringToEncrypt, Key key)
    {
        try
        {
            Cipher cipher = Cipher.getInstance(DEFAULT_ASYMMETRIC_ENCRYPTION_ALGORITHM_NAME);                        
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedByteArray = cipher.doFinal(stringToEncrypt.getBytes(DEFAULT_CHARSET_NAME));
            
            return encryptedByteArray;
        }
        catch (NoSuchAlgorithmException ex)
        {
            ex.printStackTrace();
            
            return null;
        }
        catch (IllegalBlockSizeException | InvalidKeyException | UnsupportedEncodingException | BadPaddingException | NoSuchPaddingException ex)
        {
            ex.printStackTrace();
            
            return null;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            
            return null;
        }
    }
    
    
    
    public String doRSADecryption(String stringToDecrypt, Key key)
    {
        try
        {
            return doRSADecryption(stringToDecrypt.getBytes(DEFAULT_CHARSET_NAME), key);
        } 
        catch (UnsupportedEncodingException ex) 
        {   
            return null;
        }
    }
    
    
    
    public String doRSADecryption(byte[] byteArrayToDecrypt, Key key)
    {
        String decryptedString = null;

        try
        {
            Cipher cipher = Cipher.getInstance(DEFAULT_ASYMMETRIC_ENCRYPTION_ALGORITHM_NAME);                        
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decryptedBuffer = cipher.doFinal(byteArrayToDecrypt);
            decryptedString = new String(decryptedBuffer, DEFAULT_CHARSET_NAME);
            
            return decryptedString;
        }
        catch (NoSuchAlgorithmException ex)
        {
            ex.printStackTrace();
            
            return null;
        }
        catch (IllegalBlockSizeException | InvalidKeyException | UnsupportedEncodingException | BadPaddingException | NoSuchPaddingException ex)
        {
            ex.printStackTrace();
            
            return null;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            
            return null;
        }
    }
    
    
    
    public byte[] doSHA1Hashing(String stringToHash)
    {
        MessageDigest md = null;
        
        try
        {
            md = MessageDigest.getInstance(SHA1_ALGORITHM_NAME);
            return md.digest(stringToHash.getBytes());
        }
        catch (Exception ex)
        {
            return null;
        }
    }
    

    
    public byte[] reverseByteArray(byte[] byteArray)
    {
        if(byteArray != null)
        {
            byte[] reverseByteArray = new byte[byteArray.length];
            
            for(int i = 0; i < byteArray.length; i++)
            {
                reverseByteArray[i] = byteArray[byteArray.length - 1 - i];
            }
            
            return reverseByteArray;
        }
        else
        {
            return null;
        }
    }
    
    
    
    public String byteArrayToHexString(byte[] bytes)
    {
        int lo = 0;
        int hi = 0;
        String hexString = "";

        
        
        for (int i = 0; i < bytes.length; i++)
        {
            lo = bytes[i];
            lo = lo & 0xff;
            hi = lo >> 4;
            lo = lo & 0xf;
            
            hexString += numToString(hi);
            hexString += numToString(lo);                                    
        }
        
        return hexString;
    }
    
        
    
    public byte[] hexStringToByteArray(String hexString)
    {
        int lo = 0;
        int hi = 0;
        byte[] bytes;
        
        
        
        if((hexString != null) && (hexString.length() > 1) && ((hexString.length() % 2) == 0))
        {
            bytes = new byte[hexString.length() / 2];
            
            for(int i = 0; i < hexString.length(); i += 2)
            {
                hi = stringToNum(hexString.charAt(i));
                lo = stringToNum(hexString.charAt(i + 1));
                hi = hi & 0xff;
                hi = hi << 4;
                hi = hi | lo;
                
                bytes[i / 2] = (byte)hi;
            }
            
            return bytes;
        }
        else
        {
            return null;
        }
    }
    
    
    
    private String numToString(int num)
    {
        if (num == 0) return "0";
        else if (num == 1) return "1";
        else if (num == 2) return "2";
        else if (num == 3) return "3";
        else if (num == 4) return "4";
        else if (num == 5) return "5";
        else if (num == 6) return "6";
        else if (num == 7) return "7";
        else if (num == 8) return "8";
        else if (num == 9) return "9";
        else if (num == 10) return "a";
        else if (num == 11) return "b";
        else if (num == 12) return "c";
        else if (num == 13) return "d";
        else if (num == 14) return "e";
        else if (num == 15) return "f";
        else return "";            
    }
    
    
    
    private int stringToNum(String str)
    {
        if (str.equals("0")) return 0;
        else if (str.equals("1")) return 1;
        else if (str.equals("2")) return 2;
        else if (str.equals("3")) return 3;
        else if (str.equals("4")) return 4;
        else if (str.equals("5")) return 5;
        else if (str.equals("6")) return 6;
        else if (str.equals("7")) return 7;
        else if (str.equals("8")) return 8;
        else if (str.equals("9")) return 9;
        else if (str.equals("a")) return 10;
        else if (str.equals("b")) return 11;
        else if (str.equals("c")) return 12;
        else if (str.equals("d")) return 13;
        else if (str.equals("e")) return 14;
        else if (str.equals("f")) return 15;
        else return 0;            
    }
    
    
    
    private int stringToNum(char chr)
    {
        return stringToNum(String.valueOf(chr));
    }
    
    
    
    public static String getDEFAULT_CHARSET_NAME() {
        return DEFAULT_CHARSET_NAME;
    }
}