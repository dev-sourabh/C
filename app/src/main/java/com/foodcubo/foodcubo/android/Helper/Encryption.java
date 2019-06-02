package com.foodcubo.foodcubo.android.Helper;

public abstract interface Encryption
{
  public abstract String encrypt(String paramString1, String paramString2)
    throws Exception;
  
  public abstract String decrypt(String paramString1, String paramString2)
    throws Exception;
}
