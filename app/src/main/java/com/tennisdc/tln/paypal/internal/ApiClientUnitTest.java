package com.tennisdc.tln.paypal.internal;

import com.tennisdc.tln.paypal.models.Settings;
import com.tennisdc.tln.paypal.models.ClientToken;
import com.tennisdc.tln.paypal.models.Transaction;


import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static junit.framework.Assert.*;

public class ApiClientUnitTest {

    private CountDownLatch mCountDownLatch;
    private ApiClient mApiClient;

    @Before
    public void setup() {
        mCountDownLatch = new CountDownLatch(1);
        mApiClient = new RestAdapter.Builder()
                .setEndpoint(Settings.getSandboxUrl())
                .setRequestInterceptor(new ApiClientRequestInterceptor())
                .setLogLevel(LogLevel.FULL)
                .build()
                .create(ApiClient.class);
    }

    @Test(timeout = 30000)
    public void getClientToken_returnsAClientToken() throws InterruptedException {
        mApiClient.getClientToken(null, null, new Callback<ClientToken>() {
            @Override
            public void success(ClientToken clientToken, Response response) {
                assertNotNull(clientToken.getClientToken());
                mCountDownLatch.countDown();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                fail(retrofitError.getMessage());
            }
        });

        mCountDownLatch.await();
    }

    @Test(timeout = 30000)
    public void getClientToken_returnsAClientTokenForACustomer() throws InterruptedException {
        mApiClient.getClientToken("customer", null, new Callback<ClientToken>() {
            @Override
            public void success(ClientToken clientToken, Response response) {
                assertNotNull(clientToken.getClientToken());
                mCountDownLatch.countDown();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                fail(retrofitError.getMessage());
            }
        });

        mCountDownLatch.await();
    }

    @Test(timeout = 30000)
    public void getClientToken_returnsAClientTokenForAMerchantAccount() throws InterruptedException {
        mApiClient.getClientToken(null, "fake_switch_usd", new Callback<ClientToken>() {
            @Override
            public void success(ClientToken clientToken, Response response) {
                assertNotNull(clientToken.getClientToken());
                mCountDownLatch.countDown();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                fail(retrofitError.getMessage());
            }
        });

        mCountDownLatch.await();
    }

    @Test(timeout = 30000)
    public void createTransaction_createsATransaction() throws InterruptedException {
        mApiClient.createTransaction("fake-valid-nonce", new Callback<Transaction>() {
            @Override
            public void success(Transaction transaction, Response response) {
                assertTrue(transaction.getMessage().contains("created") && transaction.getMessage().contains("authorized"));
                mCountDownLatch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(error.getMessage());
            }
        });

        mCountDownLatch.await();
    }

    @Test(timeout = 30000)
    public void createTransaction_createsATransactionWhenMerchantAccountIsNull() throws InterruptedException {
        mApiClient.createTransaction("fake-valid-nonce", null, new Callback<Transaction>() {
            @Override
            public void success(Transaction transaction, Response response) {
                assertTrue(transaction.getMessage().contains("created") && transaction.getMessage().contains("authorized"));
                mCountDownLatch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(error.getMessage());
            }
        });

        mCountDownLatch.await();
    }

    @Test(timeout = 30000)
    public void createTransaction_createsATransactionWhenMerchantAccountIsEmpty() throws InterruptedException {
        mApiClient.createTransaction("fake-valid-nonce", "", new Callback<Transaction>() {
            @Override
            public void success(Transaction transaction, Response response) {
                assertTrue(transaction.getMessage().contains("created") && transaction.getMessage().contains("authorized"));
                mCountDownLatch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(error.getMessage());
            }
        });

        mCountDownLatch.await();
    }

    @Test(timeout = 30000)
    public void createTransaction_failsWhenNonceIsAlreadyConsumed() throws InterruptedException {
        mApiClient.createTransaction("fake-consumed-nonce", new Callback<Transaction>() {
            @Override
            public void success(Transaction transaction, Response response) {
                assertEquals("Cannot use a payment_method_nonce more than once.", transaction.getMessage());
                mCountDownLatch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(error.getMessage());
            }
        });

        mCountDownLatch.await();
    }

    @Test(timeout = 30000)
    public void createTransaction_failsWhenThreeDSecureIsRequired() throws InterruptedException {
        mApiClient.createTransaction("fake-valid-nonce", null, true, new Callback<Transaction>() {
            @Override
            public void success(Transaction transaction, Response response) {
                assertEquals("Gateway Rejected: three_d_secure", transaction.getMessage());
                mCountDownLatch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail(error.getMessage());
            }
        });

        mCountDownLatch.await();
    }

    @Test(timeout = 30000)
    public void createTransaction_createsAUnionPayTransaction() throws InterruptedException {
        mApiClient.createTransaction("fake-valid-unionpay-credit-nonce", "fake_switch_usd",
                new Callback<Transaction>() {
                    @Override
                    public void success(Transaction transaction, Response response) {
                        assertTrue(transaction.getMessage().contains("created") && transaction.getMessage().contains("authorized"));
                        mCountDownLatch.countDown();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        fail(error.getMessage());
                    }
                });

        mCountDownLatch.await();
    }
}
