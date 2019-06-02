package com.foodcubo.foodcubo.android.Helper;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
import java.util.Base64;

public class AesEncryption
  implements com.foodcubo.foodcubo.android.Helper.Encryption
{
  //private final BASE64Encoder base64Encoder = new BASE64Encoder();
  //private final BASE64Decoder base64Decoder = new BASE64Decoder();
  private final byte[] ivParamBytes = { 64, 64, 64, 64, 
    38, 38, 38, 38, 
    35, 35, 35, 35, 
    36, 36, 36, 36 };
  
  public String encrypt(String toEncrypt, String key)
    throws Exception
  {
    String encryptedValue = "";
    
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
    cipher.init(1, new SecretKeySpec(key.getBytes(), "AES"), new IvParameterSpec(this.ivParamBytes));
    encryptedValue = android.util.Base64.encodeToString(cipher.doFinal(toEncrypt.getBytes()), android.util.Base64.DEFAULT);
    return encryptedValue;
  }
  
  public String decrypt(String toDecrypt, String key)
    throws Exception
  {
    String decryptedValue = "";
    
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
    cipher.init(2, new SecretKeySpec(key.getBytes(), "AES"), new IvParameterSpec(this.ivParamBytes));
    decryptedValue = new String(cipher.doFinal(android.util.Base64.decode(toDecrypt, android.util.Base64.DEFAULT)));
    
    return decryptedValue;
  }
}
