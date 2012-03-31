package com.tianxia.lib.baseworld.alipay;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tianxia.lib.baseworld.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

public class AlixPay {

    static String TAG = "AlixPay";

    private Activity mActivity;

    public AlixPay(Activity activity) {
        mActivity = activity;
    }

    private ProgressDialog mProgress = null;

    // the handler use to receive the pay result.
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                String strRet = (String) msg.obj;

                switch (msg.what) {
                case AlixId.RQF_PAY: {

                    closeProgress();

                    BaseHelper.log(TAG, strRet);

                    try {
                        String memo = "memo=";
                        int imemoStart = strRet.indexOf("memo=");
                        imemoStart += memo.length();
                        int imemoEnd = strRet.indexOf(";result=");
                        memo = strRet.substring(imemoStart, imemoEnd);

                        ResultChecker resultChecker = new ResultChecker(strRet);

                        int retVal = resultChecker.checkSign();
                        if (retVal == ResultChecker.RESULT_CHECK_SIGN_FAILED) {
                            BaseHelper.showDialog(
                                    mActivity,
                                    "提示",
                                    mActivity.getResources().getString(
                                            R.string.check_sign_failed),
                                    android.R.drawable.ic_dialog_alert);
                        } else {
                            BaseHelper.showDialog(mActivity, "提示", memo,
                                    R.drawable.infoicon);
                        }
                        
                    } catch (Exception e) {
                        e.printStackTrace();

                        BaseHelper.showDialog(mActivity, "提示", strRet,
                                R.drawable.infoicon);
                    }
                }
                    break;
                }

                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // close the progress bar
    void closeProgress() {
        try {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pay() {
        MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(mActivity);
        boolean isMobile_spExist = mspHelper.detectMobile_sp();
        if (!isMobile_spExist)
            return;

        if (!checkInfo()) {
            BaseHelper.showDialog(mActivity, "提示",
                    "缺少partner或者seller，", R.drawable.infoicon);
            return;
        }

        try {
            // prepare the order info.
            String orderInfo = getOrderInfo();
            String signType = getSignType();
            String strsign = sign(signType, orderInfo);
            strsign = URLEncoder.encode(strsign);
            String info = orderInfo + "&sign=" + "\"" + strsign + "\"" + "&"
                    + getSignType();
            
            // start the pay.
            MobileSecurePayer msp = new MobileSecurePayer();
            boolean bRet = msp.pay(info, mHandler, AlixId.RQF_PAY, mActivity);
            
            if (bRet) {
                // show the progress bar to indicate that we have started
                // paying.
                closeProgress();
                mProgress = BaseHelper.showProgress(mActivity, null, "正在支付", false,
                        true);
            } else
                ;
        } catch (Exception ex) {
            Toast.makeText(mActivity, R.string.remote_call_failed,
                    Toast.LENGTH_SHORT).show();
        }
        
    }

    private boolean checkInfo() {
        String partner = PartnerConfig.PARTNER;
        String seller = PartnerConfig.SELLER;
        if (partner == null || partner.length() <= 0 || seller == null
                || seller.length() <= 0)
            return false;

        return true;
    }


    // get the selected order info for pay.
    String getOrderInfo() {
        String strOrderInfo = "partner=" + "\"" + PartnerConfig.PARTNER + "\"";
        strOrderInfo += "&";
        strOrderInfo += "seller=" + "\"" + PartnerConfig.SELLER + "\"";
        strOrderInfo += "&";
        strOrderInfo += "out_trade_no=" + "\"" + getOutTradeNo() + "\"";
        strOrderInfo += "&";
        strOrderInfo += "subject=" + "\"" + "感谢捐赠" + "\"";
        strOrderInfo += "&";
        strOrderInfo += "body=" + "\"" + "您对开源项目花界的支持，让我们做的更好！" + "\"";
        strOrderInfo += "&";
        strOrderInfo += "total_fee=" + "\"" + "10.00" + "\"";
        strOrderInfo += "&";
        strOrderInfo += "notify_url=" + "\""
                + "http://notify.java.jpxx.org/index.jsp" + "\"";

        return strOrderInfo;
    }

    // get the out_trade_no for an order.
    String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
        Date date = new Date();
        String strKey = format.format(date);

        java.util.Random r = new java.util.Random();
        strKey = strKey + r.nextInt();
        strKey = strKey.substring(0, 15);
        return strKey;
    }

    // get the sign type we use.
    String getSignType() {
        String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
        return getSignType;
    }

    // sign the order info.
    String sign(String signType, String content) {
        return Rsa.sign(content, PartnerConfig.RSA_PRIVATE);
    }

    // the OnCancelListener for lephone platform.
    static class AlixOnCancelListener implements
            DialogInterface.OnCancelListener {
        Activity mcontext;

        AlixOnCancelListener(Activity context) {
            mcontext = context;
        }

        public void onCancel(DialogInterface dialog) {
            mcontext.onKeyDown(KeyEvent.KEYCODE_BACK, null);
        }
    }
}
