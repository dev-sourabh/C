package com.foodcubo.foodcubo.android.Helper;

import com.foodcubo.foodcubo.android.Helper.CryptoUtils;
import com.foodcubo.foodcubo.android.Helper.Encryption;
import com.foodcubo.foodcubo.android.Helper.EncryptionFactory;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class CheckSumServiceHelper
{
  private static CheckSumServiceHelper checkSumServiceHelper;
  
  public static String getVersion()
  {
    return "1.0";
  }
  
  public static CheckSumServiceHelper getCheckSumServiceHelper()
  {
    if (checkSumServiceHelper == null) {
      checkSumServiceHelper = new CheckSumServiceHelper();
    }
    return checkSumServiceHelper;
  }
  
  public String genrateCheckSum(String Key, TreeMap<String, String> paramap)
    throws Exception
  {
    StringBuilder response = checkSumServiceHelper.getCheckSumString(paramap);
    String checkSumValue = null;
    try
    {
      Encryption encryption = EncryptionFactory.getEncryptionInstance("AES");
      
      String randomNo = CryptoUtils.generateRandomString(4);
      response.append(randomNo);
      
      String checkSumHash = CryptoUtils.getSHA256(response.toString());
      checkSumHash = checkSumHash.concat(randomNo);
      
      checkSumValue = encryption.encrypt(checkSumHash, Key);
      if (checkSumValue != null)
      {
        checkSumValue = checkSumValue.replaceAll("\r\n", "");
        checkSumValue = checkSumValue.replaceAll("\r", "");
        checkSumValue = checkSumValue.replaceAll("\n", "");
      }
    }
    catch (SecurityException e)
    {
      e.printStackTrace();
    }
    return checkSumValue;
  }
  
  public StringBuilder getCheckSumString(TreeMap<String, String> paramMap)
    throws Exception
  {
    Set<String> keys = paramMap.keySet();
    
    StringBuilder checkSumStringBuffer = new StringBuilder("");
    
    TreeSet<String> parameterSet = new TreeSet();
    for (String key : keys) {
      if (!"CHECKSUMHASH".equalsIgnoreCase(key)) {
        parameterSet.add(key);
      }
    }
    for (String paramName : parameterSet)
    {
      String value = (String)paramMap.get(paramName);
      if ((value == null) || (value.trim().equalsIgnoreCase("NULL"))) {
        value = "";
      }
      checkSumStringBuffer.append(value.trim()).append("|");
    }
    return checkSumStringBuffer;
  }
  
  public boolean verifycheckSum(String masterKey, TreeMap<String, String> paramap, String responseCheckSumString)
    throws Exception
  {
    boolean isValidChecksum = false;
    StringBuilder response = checkSumServiceHelper.getCheckSumString(paramap);
    Encryption encryption = EncryptionFactory.getEncryptionInstance("AES");
    
    String responseCheckSumHash = encryption.decrypt(responseCheckSumString, masterKey);
    
    String randomStr = getLastNChars(responseCheckSumHash, 4);
    String payTmCheckSumHash = calculateRequestCheckSum(randomStr, response.toString());
    if ((responseCheckSumHash != null) && (payTmCheckSumHash != null) && 
      (responseCheckSumHash.equals(payTmCheckSumHash))) {
      isValidChecksum = true;
    }
    return isValidChecksum;
  }
  
  private String calculateRequestCheckSum(String randomStr, String checkSumString)
    throws Exception
  {
    String reqCheckSumValue = checkSumString;
    
    String checkSumHash = CryptoUtils.getSHA256(reqCheckSumValue.concat(randomStr));
    checkSumHash = checkSumHash.concat(randomStr);
    return checkSumHash;
  }
  
  public static String getLastNChars(String inputString, int subStringLength)
  {
    if ((inputString != null) && (inputString.length() > 0))
    {
      int length = inputString.length();
      if (length <= subStringLength) {
        return inputString;
      }
      int startIndex = length - subStringLength;
      return inputString.substring(startIndex);
    }
    return "";
  }
}
