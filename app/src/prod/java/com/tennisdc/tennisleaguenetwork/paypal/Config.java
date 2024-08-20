package com.tennisdc.tennisleaguenetwork.paypal;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;

/**
 * Created  on 2015-05-01.
 */
public class Config {

    public static final String PAYPAL_CLIENT_ID = "AawoaraYp-AbyhXH44Ip5jAisJSjRAbdu6kZlzQvRByM4SSCUmtDB5mhLPwMllT-JHYAQ_-pKro9B_dB";
    public static final String PAYPAL_CLIENT_SECRET = "EI9S5ALqIF4hJpkRX74-czjHDLbiG4-cPmwx3l7O-nrLdFwvK9u6h5WhzJHAjiRRepGO6XQuLfGyK1ez";

    public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
    public static final String PAYMENT_INTENT = PayPalPayment.PAYMENT_INTENT_SALE;
    public static final String DEFAULT_CURRENCY = "USD";

}
