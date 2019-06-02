package com.foodcubo.foodcubo.android.Helper;

public class EncryptionFactory
{
  public static com.foodcubo.foodcubo.android.Helper.Encryption getEncryptionInstance(String algorithmType)
  {
    com.foodcubo.foodcubo.android.Helper.Encryption encryption = null;
    
    encryption = (Encryption) new AesEncryption();
    return encryption;
  }
}
