package com.tennisdc.tennisleaguenetwork.paypal;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;

/**
 * Created  on 2015-05-01.
 */
public class Config {

    public static final String PAYPAL_CLIENT_ID = "AWpDz0T-X460LZeJ0nXHBKw6CFn-rSFe4fvc3pzrLjc2xa6493zIcVsnFodc8qNNHLTmOvFbM5wLJ2ri";
    public static final String PAYPAL_CLIENT_SECRET = "EIpRtSqSOzDtMSG_zMORgKxL1QIuIYlgwHQIOz3CAS37BO_2D25advJo-1gkvAAS_TaYVMRR_kemv5Uw";

    public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    public static final String PAYMENT_INTENT = PayPalPayment.PAYMENT_INTENT_SALE;
    public static final String DEFAULT_CURRENCY = "USD";

}
