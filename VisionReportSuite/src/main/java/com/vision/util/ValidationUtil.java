
/* $Id: ValidationUtil.java 14818 2010-01-28 09:40:41Z kiran-kumar.karra $
 *
 */
package com.vision.util;


import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import jcifs.util.Base64;

public class ValidationUtil {
	
	static final String SECRET = "Spiral Architect";

	public static boolean isValid(Object pInput) {
		return !(pInput == null);
	}

	/**
	 * Checks whether pInput is a valid String and the pInput is a valid
	 * identifier
	 * 
	 * @param pInput
	 * @return 'False' if its not a valid String or valid identifier.
	 */
	public static boolean isValidId(String pInput) {
		if (!isValid(pInput)) {
			return false;
		}
		try {
			Integer.parseInt(pInput);
		} catch (NumberFormatException lNFE) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether pInput is a valid String. String considered to be invalid
	 * under following circumstances: 1: If it is 'null' 2: If it's length is 0
	 * 3: If it's value is "" (i.e. empty String)
	 * 
	 * @param pInput
	 *            string to be validated.
	 * @return 'false' if String is invalid, else 'true'
	 */
	public static boolean isValid(String pInput) {
		return !((pInput == null) || (pInput.trim().length() == 0) || ("".equals(pInput)) || ("null".equals(pInput)));
	}

	/**
	 * 
	 * @param pInput
	 * @return boolean
	 */
	public static boolean isNumericDecimal(String pInput) {
		int length = 0;
		if (pInput == null || (length = pInput.length()) == 0) {
			return false;
		}
		char ch = '\0';
		int decCount = 0;
		int signCount = 0;
		for (int i = 0; i < length; i++) {
			ch = pInput.charAt(i);
			if (ch == '-') {
				++signCount;
				if (signCount > 1) {
					return false;
				}
				continue;
			}else if (ch == '.') {
				++decCount;
				if (decCount > 1) {
					return false;
				}
				continue;
			} else if (!Character.isDigit(ch)) {
				return false;
			}
		}
		return true;
	}

	public static String replaceComma(String inputValue){
		if(isValid(inputValue) && inputValue.contains(",")){
			return inputValue.replaceAll(",", "");
		}
		return inputValue;
	}
	public static String encode(String input) {
        StringBuilder resultStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (isUnsafe(ch)) {
                resultStr.append('%');
                resultStr.append(toHex(ch / 16));
                resultStr.append(toHex(ch % 16));
            } else {
                resultStr.append(ch);
            }
        }
        return resultStr.toString();
    }

    private static char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    private static boolean isUnsafe(char ch) {
        if (ch > 128 || ch < 0)
            return true;
        return " %$&+,/:;=?@<>#%*[]-()^!".indexOf(ch) >= 0;
    }
    public static String replacePercentage(String inputValue){
		if(isValid(inputValue) && inputValue.contains("%")){
			return inputValue.replaceAll("%", "");
		}
		return inputValue;
	}
    public static String passwordEncrypt(String plaintext){
        try{
         byte[] secret = (SECRET.hashCode() + "").substring(0, 8).getBytes();
               Cipher des = Cipher.getInstance("DES");
               des.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secret, "DES"));
               byte[] ciphertext = des.doFinal(plaintext.getBytes());
               return Base64.encode(ciphertext);
        }catch(Exception e){
         e.printStackTrace();
        }
           return plaintext;
       }
       public static String passwordDecrypt(String ciphertext) {
        try{
         byte[] secret = (SECRET.hashCode() + "").substring(0, 8).getBytes();
               Cipher des = Cipher.getInstance("DES");
               des.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secret, "DES"));
               byte[] plaintext = des.doFinal(Base64.decode(ciphertext));
               return new String(plaintext);
        }catch(Exception e){
         e.printStackTrace();
        }
        return ciphertext;
       }
       
   	public static String toDisplayCaseNextToSymbol(String s, char symbol, boolean caseFlag){
   	    final String ACTIONABLE_DELIMITERS = (String.valueOf(symbol)=="'") ?"\\'":String.valueOf(symbol);
   	    StringBuilder sb = new StringBuilder();
   	    boolean capNext = false;
   	    for (char c : s.toCharArray()) {
   	        c = (capNext && caseFlag)? Character.toUpperCase(c) : (capNext && !caseFlag) ? Character.toLowerCase(c) : c ;
   	        sb.append(c);
   	        capNext = (ACTIONABLE_DELIMITERS.indexOf(c) != -1);
   	    }
   	    return sb.toString();
   	}
   	public static String toTitleCase(String plainText) {
    	StringBuilder titleCase = new StringBuilder();
    	boolean nextTitleCase = true;
    	for (char c : plainText.toLowerCase().toCharArray()) {
    		if (!Character.isLetterOrDigit(c)) {
    			nextTitleCase = true;
    		}else if(nextTitleCase){
    			c = Character.toTitleCase(c);
    			nextTitleCase = false;
    		}
    		titleCase.append(c);
    	}
    	return titleCase.toString();
    }
    public static int getRandomNumber(int min, int max) {
	    int randomNum =  min + (int)(Math.random() * ((max - min) + 1));
	    return randomNum;
	}
    
    public static String alterStandardStringForHtmlUsage(String standardText){
    	if(isValid(standardText)){
    		standardText = standardText.replaceAll("&", "&amp;");
    		standardText = standardText.replaceAll("<", "&lt;");
    		standardText = standardText.replaceAll(">", "&gt;");
    		standardText = standardText.replaceAll("\"", "&quot;");
    		standardText = standardText.replaceAll("\'", "&apos;");
    		standardText = standardText.replaceAll("\n", " ").replaceAll("\r", " ");
    		standardText = standardText.trim().replaceAll(" +", " ");
    		return standardText;
    	}else{
    		return "";
    	}
    }
    public static String reverseHtmlCompliantStringToStandardString(String standardText){
    	if(isValid(standardText)){
    		standardText = standardText.toLowerCase();
    		standardText = standardText.replaceAll("&amp;", "&");
	    	standardText = standardText.replaceAll("&lt;", "<");
			standardText = standardText.replaceAll("&gt;", ">");
			standardText = standardText.replaceAll("&quot;", "\"");
			standardText = standardText.replaceAll("&apos;", "\'");
			return standardText;
    	}else{
    		return "";
    	}
    }
    public static String generateRandomNumberForVcReport(){
    	/* This method will return a random number of length 10 as string */
		try{
			String sessionId1 = String.valueOf((new StringBuffer(System.currentTimeMillis() + "")).reverse());
			int randNum = getRandomNumber(0,4);
			/* Following disabled code will delay the process for the random number of seconds */
//			TimeUnit.SECONDS.sleep(randNum);
			String sessionId2 = String.valueOf(System.currentTimeMillis());
			char[] randomArray = sessionId1.toCharArray();
			int index=randNum;
			int randomCount = 0;
			int[] randomArrIndex = new int[10];
			while(index<sessionId2.length()){
				randomArray[index] = sessionId2.charAt(index);
				if(randomCount<10){
					randomArrIndex[randomCount] = getRandomNumber(0,index);
					randomCount++;
				}
				index+=2;
			}
			randomCount=0;
			StringBuffer returnStr = new StringBuffer("");
			while(randomCount<10){
				returnStr.append(String.valueOf(randomArray[randomArrIndex[randomCount]]));
				randomCount++;
			}
			return returnStr.toString();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
    public static String fnProperCaseWthUnderscore(String plainText){
    	String returnString = "";
    	if(isValid(plainText)){
    		if(plainText.indexOf('_')!=-1){
    			String[] strArray = plainText.split("_");
    			for(int strIndex=0;strIndex<strArray.length;strIndex++){
    				String partStr = strArray[strIndex].toLowerCase();
    				if(strIndex==0)
    					returnString = String.valueOf(partStr.charAt(0)).toUpperCase() + partStr.substring(1,partStr.length());
    				else
    					returnString = returnString + ' ' +String.valueOf(partStr.charAt(0)).toUpperCase() + partStr.substring(1,partStr.length());
    			}
    		}else{
    			returnString = String.valueOf(plainText.charAt(0)).toUpperCase() + plainText.substring(1,plainText.length()).toLowerCase();
    		}
    	}
    	return returnString;
    }
    public static String passwordEncryptWithUrlEncode(String plaintext) {
    	try{
			byte[] secret = (SECRET.hashCode() + "").substring(0, 8).getBytes();
	        Cipher des = Cipher.getInstance("DES");
	        des.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secret, "DES"));
	        byte[] ciphertext = des.doFinal(plaintext.getBytes());
	        String utfEncodedCipherText = URLEncoder.encode(Base64.encode(ciphertext), "UTF-8");
	        return utfEncodedCipherText;
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
    }
    public static boolean isValidList(List list) {
		return (list!=null && list.size()>0);
	}
    public static boolean isNumberDatatype(int columnType) {
		return (columnType == -6 || columnType == 5 || columnType == 4 || columnType == -5 || columnType == 6
				|| columnType == 7 || columnType == 8 || columnType == 2 || columnType == 3) ? true : false;
	}
    public static String convertQuery(String query, String orderBy){
		String finalQuery = "";
		finalQuery = "select ROW_NUMBER() OVER ( "+orderBy+" ) num, rowtemp.* from("+query+") rowtemp ";
		
		return finalQuery;
	}
}
