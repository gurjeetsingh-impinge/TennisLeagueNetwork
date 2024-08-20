package com.tennisdc.tln.paypal;

//import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;

/**
 * Created  on 2015-05-01.
 */
public class Config {

//	public static final String PAYPAL_CLIENT_ID = "AWpDz0T-X460LZeJ0nXHBKw6CFn-rSFe4fvc3pzrLjc2xa6493zIcVsnFodc8qNNHLTmOvFbM5wLJ2ri"; //client sandbox account

    public static final String PAYPAL_CLIENT_ID = "AawoaraYp-AbyhXH44Ip5jAisJSjRAbdu6kZlzQvRByM4SSCUmtDB5mhLPwMllT-JHYAQ_-pKro9B_dB"; //Client Live Account

//	 public static final String PAYPAL_CLIENT_ID = "AdC3rvDTitB4sYtgkvJhi3U01XYxQI0x1jqs754vPhcpP3O5TumqhllRKw4jUUTm3R1ZhXelZrN8QF7B"; //testing  sandbox account

//	public static final String PAYPAL_CLIENT_ID = "AQ-a8_VxvY8u-CypHD8QUZWejXnGgSy37wt2N6V5Vajv9BuXnp1XOT2OxV0hoapDSg_4QZhBUEOghDl6"; //testing live account


    public static final String PAYPAL_CLIENT_SECRET = "EIpRtSqSOzDtMSG_zMORgKxL1QIuIYlgwHQIOz3CAS37BO_2D25advJo-1gkvAAS_TaYVMRR_kemv5Uw";

    public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
//    public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    public static final String PAYMENT_INTENT = PayPalPayment.PAYMENT_INTENT_SALE;
    public static final String DEFAULT_CURRENCY = "USD";

}
