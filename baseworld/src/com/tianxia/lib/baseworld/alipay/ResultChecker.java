package com.tianxia.lib.baseworld.alipay;

import org.json.JSONObject;

public class ResultChecker
{
	public static final int RESULT_INVALID_PARAM = 0;
	public static final int RESULT_CHECK_SIGN_FAILED = 1;
	public static final int RESULT_CHECK_SIGN_SUCCEED = 2;
	
	String mContent;
	ResultChecker(String content)
	{
		this.mContent = content;
	}
	
	String getSuccess()
	{
		String success = null;
		
		try
		{
			JSONObject objContent = BaseHelper.string2JSON(this.mContent, ";");
			String result = objContent.getString("result");
			result = result.substring(1, result.length()-1);
			
			JSONObject objResult = BaseHelper.string2JSON(result, "&");
			success = objResult.getString("success");
			success = success.replace("\"", "");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return success;
	}
	
	int checkSign()
	{
		int retVal = RESULT_CHECK_SIGN_SUCCEED;
		
		try
		{
			JSONObject objContent = BaseHelper.string2JSON(this.mContent, ";");
			String result = objContent.getString("result");
			result = result.substring(1, result.length()-1);
			
			int iSignContentEnd = result.indexOf("&sign_type=");
			String signContent = result.substring(0, iSignContentEnd);
			
			JSONObject objResult = BaseHelper.string2JSON(result, "&");
			String signType = objResult.getString("sign_type");
			signType = signType.replace("\"", "");
			
			String sign = objResult.getString("sign");
			sign = sign.replace("\"", "");
			
			if( signType.equalsIgnoreCase("RSA") )
			{
				if (!Rsa.doCheck(signContent, sign, PartnerConfig.RSA_ALIPAY_PUBLIC))
					retVal = RESULT_CHECK_SIGN_FAILED;
			}
		}
		catch (Exception e)
		{
			retVal = RESULT_INVALID_PARAM;
			e.printStackTrace();
		}

		return retVal;
	}
	
	boolean isPayOk()
	{
		boolean isPayOk = false;
		
		String success = getSuccess();
		if( success.equalsIgnoreCase("true") && checkSign() == RESULT_CHECK_SIGN_SUCCEED)
			isPayOk = true;
		
		return isPayOk;
	}
}