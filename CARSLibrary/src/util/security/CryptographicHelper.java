package util.security;

import java.security.MessageDigest;

public class CryptographicHelper
{
    private static CryptographicHelper cryptographicHelper = null;        
    
    private static final String DEFAULT_CHARSET_NAME = "ISO-8859-1";
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
    
    
    
    public static String getDEFAULT_CHARSET_NAME() 
    {
        return DEFAULT_CHARSET_NAME;
    }
}