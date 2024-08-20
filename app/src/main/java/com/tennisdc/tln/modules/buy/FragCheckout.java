package com.tennisdc.tln.modules.buy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.Json;
import com.braintreepayments.api.SamsungPay;
import com.braintreepayments.api.SamsungPayAvailability;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.dropin.utils.PaymentMethodType;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.braintreepayments.api.interfaces.SamsungPayCustomTransactionUpdateListener;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.GooglePaymentRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.android.gms.wallet.ShippingAddressRequirements;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.kcode.bottomlib.BottomDialog;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.samsung.android.sdk.samsungpay.v2.payment.CardInfo;
import com.samsung.android.sdk.samsungpay.v2.payment.CustomSheetPaymentInfo;
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AddressControl;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AmountBoxControl;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AmountConstants;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.CustomSheet;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.SheetItemType;
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.SheetUpdatedListener;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.Constants;
import com.tennisdc.tln.R;
import com.tennisdc.tln.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.DialogsUtil;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.interfaces.OnDialogButtonClickListener;
import com.tennisdc.tln.model.CouponDetails;
import com.tennisdc.tln.model.Discount;
import com.tennisdc.tennisleaguenetwork.model.Payment;
import com.tennisdc.tln.model.LeagueSwagItems;
import com.tennisdc.tln.model.PlayerDetail;
import com.tennisdc.tln.model.Program;
import com.tennisdc.tln.model.Swag_items;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.paypal.Config;
import com.tennisdc.tln.ui.paymentui.PaymentCheckOutActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.Unbinder;
import io.realm.Realm;

import static com.samsung.android.sdk.samsungpay.v2.SpaySdk.*;
import static java.sql.DriverManager.println;

public class FragCheckout extends Fragment {

    private static final String TAG = FragCheckout.class.getSimpleName();

    private static final String EXTRA_SELECTION = "selection";
    private static final String EXTRA_SELECTION_ITEM = "selection_item";
    private static final String EXTRA_SELECTION_SWAG_ITEM = "selection_swag_item";
    private static final String EXTRA_SELECTION_DESC = "selection_desc_item";

    private static final int REQUEST_CODE_PAYMENT = 1;
    // PayPal configuration
    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(Config.PAYPAL_ENVIRONMENT).clientId(Config.PAYPAL_CLIENT_ID);
    /* view */

    @BindView(R.id.containerPrice)
    LinearLayout mCheckoutItemContainer;

    @BindView(R.id.txtFinalCost)
    TextView mFinalCostTextView;

    /* Promo Code Views */
    @BindView(R.id.containerPromoDiscountInput)
    View mPromoCodeInputView;

    @BindView(R.id.edtCoupon)
    EditText mCouponEditText;

    @BindView(R.id.btnApplyCoupon)
    Button mApplyCouponButton;

    @BindView(R.id.containerPromoDiscount)
    View mPromoCodeDiscountView;

    @BindView(R.id.txtPromoCodeDiscount)
    TextView mPromoCodeDiscountTextView;

    @BindView(R.id.txtPromoDesc)
    TextView mPromoDescTextView;

    /* Veteran Discount Views */

    @BindView(R.id.containerVeteranDiscount)
    LinearLayout mVeteranDiscountView;

  /*  @BindView(R.id.txtVeteranDiscountName)
    TextView mVeteranDiscountNameTextView;

    @BindView(R.id.txtVeteranDiscount)
    TextView mVeteranDiscountTextView;

    @BindView(R.id.txtVeteranDesc)
    TextView mVeteranDescTextView;*/

    @BindView(R.id.btnSubmit)
    Button mSubmitButton;

    /* data */ List<Program> mPrograms;
    /* data */ List<Swag_items> mItems;
    /* data */ List<LeagueSwagItems.Response.Data.SwagItems> mSwagItems;
    /* data */ List<String> addDesc;
    // private RecyclerAdapter.RecyclerSelectionHelper<Discount> mRecyclerSelectionHelper;
    private double mFinalCost;
    private boolean mIsPaymentDone = false;
    private double mTotalDiscount = 0;
    private Unbinder unbinder;
    /* braintree*/
    BraintreeFragment mBraintreeFragment;
    private PaymentMethodType mPaymentMethodType;
    private PaymentMethodNonce mNonce;
    String mAuthorization = "";


    public static Bundle buildArgs(List<Program> selectedPrograms, List<Swag_items> selectedItems,String addDesc) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_SELECTION, Parcels.wrap(selectedPrograms));
        args.putParcelable(EXTRA_SELECTION_ITEM, Parcels.wrap(selectedItems));
        args.putString(EXTRA_SELECTION_DESC, addDesc);
        return args;
    }

    public static Bundle buildArgsForLeagueSwagItems(List<LeagueSwagItems.Response.Data.SwagItems> selectedItem) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_SELECTION_SWAG_ITEM, Parcels.wrap(selectedItem));
        return args;
    }

    public static Bundle buildArgs(List<Program> selectedPrograms) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_SELECTION, Parcels.wrap(selectedPrograms));
        return args;

        /* get client token for braintree */
    }





    protected void fetchAuthorization() {
        Prefs.AppData appData = new Prefs.AppData(getActivity());
        if (appData.getCustomerID().trim().isEmpty()) {
            mAuthorization = getActivity().getString(R.string.tokenization_key_value);
        } else {
            mAuthorization = appData.getCustomerID();
        }
//        try{
//        mBraintreeFragment = BraintreeFragment.newInstance(getActivity(),mAuthorization);
//        } catch (InvalidArgumentException e) {
//            e.printStackTrace();
//        }
         /*mAuthorization = getActivity().getString(R.string.tokenization_key_value);
       App.getApiClient(getActivity()).getClientToken(App.sOAuth,
                Settings.getMerchantAccountId(getActivity()), new Callback<ClientToken>() {
                    @Override
                    public void success(ClientToken clientToken, Response response) {
                        if (TextUtils.isEmpty(clientToken.getClientToken())) {
                            showDialog("Client token was empty");
                        } else {
                            try {
                                mAuthorization = clientToken.getClientToken();
                                if (com.braintreepayments.api.models.ClientToken.fromString(clientToken.getClientToken()) instanceof com.braintreepayments.api.models.ClientToken) {

                                    mBraintreeFragment = BraintreeFragment.newInstance(getActivity(), clientToken.getClientToken());
                                    DropInResult.fetchDropInResult((AppCompatActivity) getActivity(), clientToken.getClientToken(), new DropInResult.DropInResultListener() {
                                        @Override
                                        public void onError(Exception exception) {
                                            //showDialog("Error Payment");
                                        }

                                        @Override
                                        public void onResult(DropInResult result) {
                                            //showDialog("Success payment");
                                        }
                                    });
                                } else {
                                    try {
                                        mBraintreeFragment = BraintreeFragment.newInstance(getActivity(), getActivity().getString(R.string.tokenization_key_value));

                                        //            mBraintreeFragment = BraintreeFragment.newInstance(this, mAuthorization);
                                    } catch (InvalidArgumentException error) {
                                        //            onError(error);
                                        getActivity().setProgressBarIndeterminateVisibility(false);

                                        Log.d(getClass().getSimpleName(), "Error received (" + error.getClass() + "): " + error.getMessage());
                                        Log.d(getClass().getSimpleName(), error.toString());

                                        showDialog("An error occurred (" + error.getClass() + "): " + error.getMessage());
                                    }
                                }
                            } catch (InvalidArgumentException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        showDialog("Unable to get a client token. Response Code: " +
                                error.getResponse().getStatus() + " Response body: " +
                                error.getResponse().getBody());
                    }
                });*/

    }


    /**
     * Receiving the PalPay payment response
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (requestCode == 65537) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                verifyPaymentOnServerBrainTree( result.getPaymentMethodNonce().getNonce());
            }
        }else */
        if (requestCode == DROP_IN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                // use the result to update your UI and send the payment method nonce to your server
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                if (result != null) {
                    String deviceData = result.getDeviceData();
                    verifyPaymentOnServerBrainTree(result.getPaymentMethodNonce().getNonce());
                    if (result.getPaymentMethodNonce() instanceof CardNonce) {
                        ArrayList<CardNonce> mCardList = new ArrayList<>();
                        mCardList.add((CardNonce) result.getPaymentMethodNonce());
                    }
                }
            }
        }
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String confirmBundle = data.getStringExtra(Constants.EXTRA_RESULT_CONFIRMATION);
                if (confirmBundle != null) {
                    try {
                        JSONObject confirmJSon = new JSONObject(confirmBundle);
                        String paymentId = confirmJSon.getString("id");

                        // Now verify the payment on the server side
                        verifyPaymentOnServer(paymentId);

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } /*else if (resultCode == DROP_IN_REQUEST) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
            }*/ else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(TAG, "An invalid Payment or PayPalConfiguration was submitted.");
            }
        }
    }

    protected void showDialog(String message) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_buy_checkout, container, false);

        App.LogFacebookEvent(getActivity(), getActivity().getClass().getName());
        /*braintree*/

        unbinder = ButterKnife.bind(this, view);

        fetchAuthorization();
        Bundle arguments = getArguments();
        if (arguments == null) throw new RuntimeException("Argument missing");

        mPrograms = Parcels.unwrap(arguments.getParcelable(EXTRA_SELECTION));
        mItems = Parcels.unwrap(arguments.getParcelable(EXTRA_SELECTION_ITEM));
        mSwagItems = Parcels.unwrap(arguments.getParcelable(EXTRA_SELECTION_SWAG_ITEM));
        addDesc = Parcels.unwrap(arguments.getParcelable(EXTRA_SELECTION_DESC));

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

        double finalCost = 0;

        if (mPrograms==null)
            mPrograms = new ArrayList<>();

        for (Program program : mPrograms) {
            View checkoutView = layoutInflater.inflate(R.layout.view_buy_checkout_item, mCheckoutItemContainer, false);

            ((TextView) checkoutView.findViewById(R.id.txtItem)).setText(program.type);

            double unitPrice =0.0;
            //set cost (showing cost of every product after deducting veteran discount leads in that error we got and same issue is in PayPal Payment) solution is in in for loop make use of unit price for calculating the discounted price
            if(program.programPrices.get(program.selectedPriceIndex).priceAfterDiscount != null) {

                 unitPrice = program.programPrices.get(program.selectedPriceIndex).priceAfterDiscount;
            }else {
                 unitPrice = program.programPrices.get(program.selectedPriceIndex).price;

            }

            Prefs.AppData appData = new Prefs.AppData(getActivity());
            PlayerDetail player = appData.getPlayer();
            if (player != null && player.discount != null && !player.discount.isEmpty()) {
                int toatlDisocuntPercentage = 0;
                for (Discount discount : player.discount) {
                    toatlDisocuntPercentage = toatlDisocuntPercentage + discount.Percentage;
//					unitPrice = program.programPrices.get(program.selectedPriceIndex).price -
//							(0.01 * toatlDisocuntPercentage) * program.programPrices.get(program.selectedPriceIndex).price;
                }
                //  unitPrice = program.programPrices.get(program.selectedPriceIndex).price - (0.01 * toatlDisocuntPercentage) * unitPrice;
                ((TextView) checkoutView.findViewById(R.id.txtCost)).setText(String.format("%.2f", unitPrice));
            } else {
                ((TextView) checkoutView.findViewById(R.id.txtCost)).setText(String.format("%.2f", unitPrice));
            }

            /*if (program.programPrices != null && !program.programPrices.isEmpty()) {

                if (program.programPrices.get(program.selectedPriceIndex).priceAfterDiscount != null) {
//					((TextView) ch eckoutView.findViewById(R.id.txtCost)).setText(String.format("%.2f", program.programPrices.get(program.selectedPriceIndex).priceAfterDiscount));
                    finalCost += program.programPrices.get(program.selectedPriceIndex).priceAfterDiscount;
                } else {
//					((TextView) checkoutView.findViewById(R.id.txtCost)).setText(String.format("%.2f", program.programPrices.get(program.selectedPriceIndex).price));
                    finalCost += program.programPrices.get(program.selectedPriceIndex).price;
                }

            } else {
                ((TextView) checkoutView.findViewById(R.id.txtCost)).setText(String.format("%.2f", 0.0));
                // finalCost += program.programPrices.get(program.selectedPriceIndex).price;
            }*/

            finalCost = finalCost + unitPrice;
            checkoutView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mCheckoutItemContainer.addView(checkoutView);
        }
        if (mItems != null && mItems.size() != 0) {
            for (Swag_items mItem : mItems) {
//            View checkoutView = layoutInflater.inflate(R.layout.view_buy_checkout_item, mCheckoutItemContainer, false);
//
//            ((TextView) checkoutView.findViewById(R.id.txtItem)).setText(mItem.title);


                //set cost (showing cost of every product after deducting veteran discount leads in that error we got and same issue is in PayPal Payment) solution is in in for loop make use of unit price for calculating the discounted price
                double unitPrice = Double.valueOf(mItem.cost) + Double.valueOf(mItem.shipping_cost);
                finalCost = finalCost + unitPrice;
//            if (mItem.cost != null && !mItem.cost.isEmpty()) {
//
//                if (mItem.shipping_cost != null) {
//                    finalCost += Double.valueOf(mItem.shipping_cost);
//                }
//
//            } else {
//                ((TextView) checkoutView.findViewById(R.id.txtCost)).setText(String.format("%.2f", 0.0));
//                // finalCost += program.programPrices.get(program.selectedPriceIndex).price;
//            }


//            checkoutView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            mCheckoutItemContainer.addView(checkoutView);
            }
        } else if (mSwagItems != null && mSwagItems.size()!=0) {
            for (LeagueSwagItems.Response.Data.SwagItems mItem : mSwagItems) {
                //set cost (showing cost of every product after deducting veteran discount leads in that error we got and same issue is in PayPal Payment) solution is in in for loop make use of unit price for calculating the discounted price
                double unitPrice = Double.parseDouble(mItem.getCost()) + Double.parseDouble(mItem.getShipping_cost());
                finalCost = finalCost + unitPrice;
            }
        }
        setFinalCost(finalCost);
        // checkAndApplyVeteranDiscount();

        Prefs.AppData appData = new Prefs.AppData(getActivity());
        PlayerDetail player = appData.getPlayer();
        if (player != null && player.discount != null && !player.discount.isEmpty() && mPrograms!=null && mPrograms.size()!=0) {
            for (Discount discount : player.discount) {
                View discountView = layoutInflater.inflate(R.layout.view_discount_item, mVeteranDiscountView, false);
                double discountAmt = (0.01 * discount.Percentage) * mFinalCost;

                ((TextView) discountView.findViewById(R.id.txtDiscountName)).setText(discount.Name);
                ((TextView) discountView.findViewById(R.id.txDiscountDesc)).setText(discount.Description);
                ((TextView) discountView.findViewById(R.id.txtDiscount)).setText(String.format("- %.2f", discountAmt));

                mTotalDiscount += discountAmt;

                discountView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mVeteranDiscountView.addView(discountView);
            }
            setFinalCost(mFinalCost - mTotalDiscount);
        }

        applyPromoCode(App.sCouponDetails);

        mCouponEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mApplyCouponButton.setEnabled(s.length() > 0);
            }
        });

        mApplyCouponButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "validating code...");
                WSHandle.Buy.getPromoCodeDetails(mCouponEditText.getText().toString().trim(), new VolleyHelper.IRequestListener<CouponDetails, String>() {
                    @Override
                    public void onFailureResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccessResponse(CouponDetails response) {
                        applyPromoCode(response);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFinalCost > 0)
//                    verifyPaymentOnServer("123");
                    performPayment();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* start paypal service */
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        getActivity().startService(intent);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsPaymentDone) {
            showCompletionPopup();
        }

        getActivity().setTitle("Checkout");
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroy();
    }

    void setFinalCost(double finalCost) {
        mFinalCost = finalCost > 0 ? finalCost : 0;
        mFinalCostTextView.setText(String.format("%.2f", mFinalCost));
    }

    void checkAndApplyVeteranDiscount() {

       /* Prefs.AppData appData = new Prefs.AppData(getActivity());
        PlayerDetail player = appData.getPlayer();
        if(!player.discount.isEmpty()) {
            mDiscountRecyclerView.setVisibility(View.VISIBLE);
            mDiscountRecyclerView.setAdapter(new RecyclerAdapter<Discount, DiscountViewHolder>(player.discount) {
                @Override
                public DiscountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_discount_item, parent, false);
                    return new DiscountViewHolder(view);
                }

                @Override
                public void onBindViewHolder(DiscountViewHolder holder, int position) {
                    holder.bindData(getItem(position));
                }

            });
        }*/
            /*for(int i=0;i<player.discount.size();i++){
                double discount = (0.01 * player.discount.get(i).getPercentage()) * mFinalCost;

                mVeteranDiscountNameTextView.setText(player.discount.get(i).getName());
                mVeteranDescTextView.setText( player.discount.get(i).getDescription());
                mVeteranDiscountTextView.setText(String.valueOf(player.discount.get(i).getPercentage()));
                mVeteranDiscountView.setVisibility(View.VISIBLE);

                setFinalCost(mFinalCost - discount);
                mTotalDiscount += discount;
            }
        }*/

       /* if (player.VeteranDiscount > 0) {
            double discount = (0.01 * player.VeteranDiscount) * mFinalCost;

            mVeteranDescTextView.setText("You have played more than " + (player.VeteranDiscount == 10 ? "400" : "200") + " matches, a " + String.format("- %.2f", discount) + " % discount will be applied.");
            mVeteranDiscountTextView.setText(String.format("- %.2f", discount));
            mVeteranDiscountView.setVisibility(View.VISIBLE);

            setFinalCost(mFinalCost - discount);
            mTotalDiscount += discount;
        }*/
    }

    void applyPromoCode(CouponDetails couponDetails) {
        App.sCouponDetails = couponDetails;

        if (couponDetails != null && mPrograms!= null && mPrograms.size()!=0) {
            double discount = couponDetails.calculateDiscount(mFinalCost);

            mPromoCodeInputView.setVisibility(View.GONE);

            mPromoDescTextView.setText(couponDetails.message);
            mPromoCodeDiscountTextView.setText(String.format("- %.2f", discount));
            mPromoCodeDiscountView.setVisibility(View.VISIBLE);

            setFinalCost(mFinalCost - discount);
            mTotalDiscount += discount;
        }
    }

    /**
     * Launching PalPay payment activity to complete the payment
     */
    private static final int DROP_IN_REQUEST = 1;

    private void performPayment() {

        if (mFinalCost > 0) {
            println("Check123"+mFinalCost);
            println("FragCheck $"+ addDesc);



//            tokenize();
//            getPaymentOption();

            /* braintree */
//              startActivityForResult(getDropInRequest(prepareFinalCartCost()).getIntent(getActivity()), DROP_IN_REQUEST);

            /* simple Paypal */
            PayPalPayment thingsToBuy = prepareFinalCart();

//              Intent intent = new Intent(getActivity(),PaymentActivity.class);
            Intent intent = new Intent(getActivity(), PaymentCheckOutActivity.class);
            intent.putExtra("FinalCost",mFinalCost );
            intent.putStringArrayListExtra("addDesc" ,(ArrayList<String>) addDesc);
            Log.e("Check BAl", "BAv"+String.valueOf(mFinalCost));
            Log.e("Add", "abcd"+String.valueOf(addDesc));

            //intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);
            startActivityForResult(intent, REQUEST_CODE_PAYMENT);
        } else {
//            verifyPaymentOnServer("NO-ID-" + String.valueOf(new Date().getTime()));
            verifyPaymentOnServerBrainTree("NO-ID-" + String.valueOf(new Date().getTime()));
            //showCompletionPopup();
        }
    }

    private DropInRequest getDropInRequest(BigDecimal mAmount) {
        GooglePaymentRequest googlePaymentRequest = new GooglePaymentRequest()
                .transactionInfo(TransactionInfo.newBuilder()
                        .setCurrencyCode(PaypalSettings.getGooglePaymentCurrency(getActivity()))
                        .setTotalPrice(String.valueOf(mAmount))
                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                        .build())
                .allowPrepaidCards(PaypalSettings.areGooglePaymentPrepaidCardsAllowed(getActivity()))
                .billingAddressFormat(WalletConstants.BILLING_ADDRESS_FORMAT_FULL)
                .billingAddressRequired(PaypalSettings.isGooglePaymentBillingAddressRequired(getActivity()))
                .emailRequired(PaypalSettings.isGooglePaymentEmailRequired(getActivity()))
                .phoneNumberRequired(PaypalSettings.isGooglePaymentPhoneNumberRequired(getActivity()))
                .shippingAddressRequired(PaypalSettings.isGooglePaymentShippingAddressRequired(getActivity()))
                .shippingAddressRequirements(ShippingAddressRequirements.newBuilder()
                        .addAllowedCountryCodes(PaypalSettings.getGooglePaymentAllowedCountriesForShipping(getActivity()))
                        .build())
                .googleMerchantId(PaypalSettings.getGooglePaymentMerchantId(getActivity()));

        return new DropInRequest()
                .amount(String.format("%.2f", mAmount))
                .clientToken(mAuthorization)
//                .collectDeviceData(PaypalSettings.shouldCollectDeviceData(getActivity()))
                .googlePaymentRequest(googlePaymentRequest);
//                .maskCardNumber(true)
//                .maskSecurityCode(true)
//                .allowVaultCardOverride(true)
//                .vaultCard(true)
//                .vaultManager(true)
//                .cardholderNameStatus(PaypalSettings.getCardholderNameStatus(getActivity()));
    }

    //    public String mAuthorization;
    CustomSheetPaymentInfo paymentInfo;
    private PaymentManager mPaymentManager;
   /* public String mAuthorization = "sandbox_csmycvch_f7hwfdwccz5bpdsj";
//    public String mAuthorization = "sandbox_tmxhyf7d_dcpspy2brwdjr3qn";
    protected String mCustomerId;
    public BraintreeFragment mBraintreeFragment;*/

    /* initialization for the samsung pay */
    public void tokenize(BigDecimal mAmount) {
        SamsungPay.createPaymentManager(mBraintreeFragment, new BraintreeResponseListener<PaymentManager>() {
            @Override
            public void onResponse(PaymentManager paymentManager) {
                mPaymentManager = paymentManager;

                SamsungPay.createPaymentInfo(mBraintreeFragment, new BraintreeResponseListener<CustomSheetPaymentInfo.Builder>() {
                    @Override
                    public void onResponse(CustomSheetPaymentInfo.Builder builder) {
                        paymentInfo = builder
                                .setAddressInPaymentSheet(CustomSheetPaymentInfo.AddressInPaymentSheet.NEED_BILLING_AND_SHIPPING)
                                .setCustomSheet(getCustomSheet())
                                .setOrderNumber("order-number")
                                .build();


                        SamsungPay.requestPayment(mBraintreeFragment, mPaymentManager, paymentInfo, new SamsungPayCustomTransactionUpdateListener() {
                            @Override
                            public void onSuccess(CustomSheetPaymentInfo response, Bundle extraPaymentData) {
                                CustomSheet customSheet = response.getCustomSheet();
                                AddressControl billingAddressControl = (AddressControl) customSheet.getSheetControl("billingAddressId");
                                CustomSheetPaymentInfo.Address billingAddress = billingAddressControl.getAddress();

                                CustomSheetPaymentInfo.Address shippingAddress = response.getPaymentShippingAddress();

                                // displayAddresses(billingAddress, shippingAddress);
                            }

                            @Override
                            public void onCardInfoUpdated(@NonNull CardInfo cardInfo, @NonNull CustomSheet customSheet) {
                                AmountBoxControl amountBoxControl = (AmountBoxControl) customSheet.getSheetControl("amountID");
                                amountBoxControl.setAmountTotal(Double.valueOf(mAmount.toString()), AmountConstants.FORMAT_TOTAL_PRICE_ONLY);

                                customSheet.updateControl(amountBoxControl);
                                mPaymentManager.updateSheet(customSheet);
                            }
                        });

                    }
                });
            }
        });
    }

    private CustomSheet getCustomSheet() {
        CustomSheet sheet = new CustomSheet();

        final AddressControl billingAddressControl = new AddressControl("billingAddressId", SheetItemType.BILLING_ADDRESS);
        billingAddressControl.setAddressTitle("Billing Address");
        billingAddressControl.setSheetUpdatedListener(new SheetUpdatedListener() {
            @Override
            public void onResult(String controlId, final CustomSheet customSheet) {
                Log.d("billing sheet updated", controlId);

                mPaymentManager.updateSheet(customSheet);
            }
        });
        sheet.addControl(billingAddressControl);

        final AddressControl shippingAddressControl = new AddressControl("shippingAddressId", SheetItemType.SHIPPING_ADDRESS);
        shippingAddressControl.setAddressTitle("Shipping Address");
        shippingAddressControl.setSheetUpdatedListener(new SheetUpdatedListener() {
            @Override
            public void onResult(String controlId, final CustomSheet customSheet) {
                Log.d("shipping sheet updated", controlId);

                mPaymentManager.updateSheet(customSheet);
            }

        });
        sheet.addControl(shippingAddressControl);

        AmountBoxControl amountBoxControl = new AmountBoxControl("amountID", "USD");
        amountBoxControl.setAmountTotal(1.0, AmountConstants.FORMAT_TOTAL_PRICE_ONLY);
        sheet.addControl(amountBoxControl);

        return sheet;
    }

    /**
     * Preparing final cart amount that needs to be sent to PayPal for payment
     */
    private PayPalPayment prepareFinalCart() {

        PayPalItem[] items = new PayPalItem[mPrograms.size()];

        for (int i = 0; i < items.length; i++) {
            Program program = mPrograms.get(i);

            //set cost
            double unitPrice = program.programPrices.get(program.selectedPriceIndex).price;
            int totalDiscount = 0;
            Prefs.AppData appData = new Prefs.AppData(getActivity());
            PlayerDetail player = appData.getPlayer();
            if (player != null && player.discount != null && !player.discount.isEmpty()) {
                for (Discount discount : player.discount) {
                    totalDiscount += discount.Percentage;
                }
                unitPrice = unitPrice - (0.01 * totalDiscount) * unitPrice;
            }

            PayPalItem item = new PayPalItem(program.name, 1, //Quantity
                    new BigDecimal(Math.round(unitPrice * 100D) / 100D).setScale(2, BigDecimal.ROUND_HALF_EVEN), //price
                    Config.DEFAULT_CURRENCY, // currency
                    "sku-" + new Prefs.AppData(getActivity()).getUserID()); // stock keeping unit (SKU)
            items[i] = item;
        }


        if (App.sCouponDetails != null) {
            App.sCouponDetails.calculateDiscount(mFinalCost);
        }


        // Total amount
        BigDecimal subtotal = new BigDecimal(mFinalCost);

        // If you have shipping cost, add it here
        BigDecimal shipping = new BigDecimal("0.0");

        // If you have tax, add it here
        BigDecimal tax = new BigDecimal("0.0");

        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);

        BigDecimal amount = subtotal.add(shipping).add(tax);


        // Getting Purchased Programs type
        StringBuilder programsType = new StringBuilder();
        for (int i = 0; i < mPrograms.size(); i++) {
            if (i == mPrograms.size() - 1) {
                programsType.append(mPrograms.get(i).type);
            } else {
                programsType.append(mPrograms.get(i).type).append(",");
            }
        }

        PayPalPayment payment = new PayPalPayment(amount, Config.DEFAULT_CURRENCY, "Total Amount: "/*programsType.toString()*/, Config.PAYMENT_INTENT);

        // Testing
        paypalConfig.defaultUserEmail("d.errorfound@gmail.com");
        paypalConfig.acceptCreditCards(true);

        payment.items(items).paymentDetails(paymentDetails);
        try {
            mFinalCost = Double.valueOf(String.format("%.2f", amount));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Custom field like invoice_number etc.,
//		payment.custom("This is text that will be associated with the payment that the app can use.");
        return payment;
    }

    private BigDecimal prepareFinalCartCost() {

//        PayPalItem[] items = new PayPalItem[mPrograms.size()];

        for (int i = 0; i < mPrograms.size(); i++) {
            Program program = mPrograms.get(i);

            //set cost
            double unitPrice = program.programPrices.get(program.selectedPriceIndex).price;
            int totalDiscount = 0;
            Prefs.AppData appData = new Prefs.AppData(getActivity());
            PlayerDetail player = appData.getPlayer();
            if (player != null && player.discount != null && !player.discount.isEmpty()) {
                for (Discount discount : player.discount) {
                    totalDiscount += discount.Percentage;
                }
                unitPrice = unitPrice - (0.01 * totalDiscount) * unitPrice;
            }
//
//            PayPalItem item = new PayPalItem(program.name, 1, //Quantity
//                    new BigDecimal(Math.round(unitPrice * 100D) / 100D).setScale(2, BigDecimal.ROUND_HALF_EVEN), //price
//                    Config.DEFAULT_CURRENCY, // currency
//                    "sku-" + String.valueOf( new Prefs.AppData(getActivity()).getUserID())); // stock keeping unit (SKU)
//            items[i] = item;
        }


        if (App.sCouponDetails != null) {
            App.sCouponDetails.calculateDiscount(mFinalCost);
        }


        // Total amount
        BigDecimal subtotal = BigDecimal.valueOf(mFinalCost);

        // If you have shipping cost, add it here
        BigDecimal shipping = BigDecimal.valueOf(0.0);

        // If you have tax, add it here
        BigDecimal tax = BigDecimal.valueOf(0.0);

        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);

        BigDecimal amount = subtotal.add(shipping).add(tax);


        // Getting Purchased Programs type
        StringBuilder programsType = new StringBuilder();
        for (int i = 0; i < mPrograms.size(); i++) {
            if (i == mPrograms.size() - 1) {
                programsType.append(mPrograms.get(i).type);
            } else {
                programsType.append(mPrograms.get(i).type).append(",");
            }
        }
        return amount;
    }

    /* verify payment for old paypal integreation*/
    private void verifyPaymentOnServer(String paymentId) {

        /*create object*/
        final JSONObject paymentDetails = new JSONObject();
        try {
            paymentDetails.put("oauth_token", App.sOAuth);
            paymentDetails.put("payment_method", "paypal");
            paymentDetails.put("transaction_id", TextUtils.isEmpty(paymentId) ? "" : paymentId);
//            paymentDetails.put("nounce", TextUtils.isEmpty(paymentId) ? "" : paymentId);
            paymentDetails.put("user_promo_code", App.sCouponDetails != null);
            paymentDetails.put("promo_code_id", App.sCouponDetails != null ? App.sCouponDetails.id : "");
            paymentDetails.put("total_amount", mFinalCost);
//            paymentDetails.put("test", 1);

            JSONArray programJsonArray = new JSONArray();
            JSONObject programObject;
            Program program;
            String programType;
            int programsCount = mPrograms.size();
            for (int i = 0; i < programsCount; i++) {
                program = mPrograms.get(i);
                programObject = new JSONObject();

                if (program.type.equalsIgnoreCase("Non League Program")) {
                    programType = "partner";
                } else if (program.type.equalsIgnoreCase("Tennis Ladder")) {
                    programType = "ladder";
                } else {
                    programType = "";
                }

                programObject.put("non_league_type", programType);
                programObject.put("id", program.id);


                double unitPrice = program.programPrices.get(program.selectedPriceIndex).price;
                int totalDiscount = 0;
                Prefs.AppData appData = new Prefs.AppData(getActivity());
                PlayerDetail player = appData.getPlayer();
                if (player != null && player.discount != null && !player.discount.isEmpty()) {
                    for (Discount discount : player.discount) {
                        totalDiscount = totalDiscount + discount.Percentage;
                    }
                    unitPrice = unitPrice - (0.01 * totalDiscount) * unitPrice;
                }
                if (App.sCouponDetails != null) {
                    if (App.sCouponDetails.type.equals("%")) {
                        unitPrice = unitPrice - ((App.sCouponDetails.discount / 100) * program.programPrices.get(program.selectedPriceIndex).price);
                    }
                }
                // Put unit_price
                programObject.put("unit_price", Math.round(unitPrice * 100D) / 100D);

                // put actual_price
//				programObject.put("actual_price", "0");

                // put actual_price
                programObject.put("unit_price_before_discount", program.programPrices.get(program.selectedPriceIndex).price);


                long priceId = program.programPrices.get(program.selectedPriceIndex).id;
                programObject.put("non_league_price_option_id", priceId == 0 ? 0 : priceId);

                programJsonArray.put(programObject);
            }

            paymentDetails.put("programs", programJsonArray);

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        //store locally
        Realm realm = Realm.getInstance(getActivity());

        realm.beginTransaction();

        Payment payment = realm.createObject(Payment.class);
        payment.setId(paymentId);
        payment.setJsonString(paymentDetails.toString());

        realm.commitTransaction();
        realm.close();

        // start service
//        getActivity().startService(new Intent(getActivity().getApplicationContext(), SubmitPaymentService.class));

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
        WSHandle.Buy.submitPaymentDetails(paymentDetails, new VolleyHelper.IRequestListener<String, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed while submitting payment details : " + response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(String response) {
                progressDialog.dismiss();
                Realm realm = Realm.getInstance(getActivity());
                try {
                    realm.beginTransaction();
                    realm.where(Payment.class).equalTo("id", paymentDetails.get("transaction_id").toString()).findFirst().removeFromRealm();
                    realm.commitTransaction();
                    //  reportToAdwords();
                    showCompletionPopup();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    realm.close();
                }
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getActivity().getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    /* verify payment for new paypal integreation - BrainTree*/
    private void verifyPaymentOnServerBrainTree(String paymentId) {
//paymentId = "tokencc_bh_sqj6gq_v9mqcg_j6t68b_72gjs5_3qy";
        /*create object*/
        final JSONObject paymentDetails = new JSONObject();
        try {
            paymentDetails.put("oauth_token", App.sOAuth);
            paymentDetails.put("payment_method", "paypal");
            paymentDetails.put("nounce", TextUtils.isEmpty(paymentId) ? "" : paymentId);
            paymentDetails.put("user_promo_code", App.sCouponDetails != null);
            paymentDetails.put("promo_code_id", App.sCouponDetails != null ? App.sCouponDetails.id : "");
            paymentDetails.put("total_amount", String.format("%.2f", mFinalCost));
            /* test braintree */
            paymentDetails.put("sandbox", "1");

            JSONArray programJsonArray = new JSONArray();
            JSONObject programObject;
            Program program;
            String programType;
            int programsCount = mPrograms.size();
            for (int i = 0; i < programsCount; i++) {
                program = mPrograms.get(i);
                programObject = new JSONObject();

                if (program.type.equalsIgnoreCase("Non League Program")) {
                    programType = "partner";
                } else if (program.type.equalsIgnoreCase("Tennis Ladder")) {
                    programType = "ladder";
                } else {
                    programType = "";
                }

                programObject.put("non_league_type", programType);
                programObject.put("id", program.id);


                double unitPrice = program.programPrices.get(program.selectedPriceIndex).price;
                int totalDiscount = 0;
                Prefs.AppData appData = new Prefs.AppData(getActivity());
                PlayerDetail player = appData.getPlayer();
                if (player != null && player.discount != null && !player.discount.isEmpty()) {
                    for (Discount discount : player.discount) {
                        totalDiscount = totalDiscount + discount.Percentage;
                    }
                    unitPrice = unitPrice - (0.01 * totalDiscount) * unitPrice;
                }
                if (App.sCouponDetails != null) {
                    if (App.sCouponDetails.type.equals("%")) {
                        unitPrice = unitPrice - ((App.sCouponDetails.discount / 100) * program.programPrices.get(program.selectedPriceIndex).price);
                    }
                }
                // Put unit_price
                programObject.put("unit_price", Math.round(unitPrice * 100D) / 100D);

                // put actual_price
//			 	programObject.put("actual_price", "0");

                // put actual_price
                programObject.put("unit_price_before_discount", program.programPrices.get(program.selectedPriceIndex).price);


                long priceId = program.programPrices.get(program.selectedPriceIndex).id;
                programObject.put("non_league_price_option_id", priceId == 0 ? 0 : priceId);

                programJsonArray.put(programObject);
            }
            paymentDetails.put("programs", programJsonArray);

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        try {
            //store locally
            Realm realm = Realm.getInstance(getActivity());

            realm.beginTransaction();

            Payment payment = realm.createObject(Payment.class);
            payment.setId(paymentId);
            payment.setJsonString(paymentDetails.toString());

            realm.commitTransaction();
            realm.close();
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // start service
//        getActivity().startService(new Intent(getActivity().getApplicationContext(), SubmitPaymentService.class));

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
        WSHandle.Buy.submitPaymentDetailsBrainTree(paymentDetails, new VolleyHelper.IRequestListener<String, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed while submitting payment details : " + response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(String response) {
                progressDialog.dismiss();
                Realm realm = Realm.getInstance(getActivity());
                try {
                    realm.beginTransaction();
                    realm.where(Payment.class).equalTo("id", paymentDetails.get("transaction_id").toString()).findFirst().removeFromRealm();
                    realm.commitTransaction();
                    //  reportToAdwords();
                    showCompletionPopup();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    realm.close();
                }
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getActivity().getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    void showCompletionPopup() {
        /* navigate to previous page */

        BaseDialog.SimpleDialog.getDialogInstance("Thanks!!!", 0, "Your program is most likely already configured, but in some cases give our staff 24 hours. Please check your email for updates.")
                .setNeutralButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
                    }
                })
                .show(getFragmentManager(), "confirm-dlg");
    }

    private class DiscountViewHolder extends RecyclerView.ViewHolder {

        private TextView NameTextView;
        private TextView DescriptionTextView;
        private TextView PercentageTextView;

        private Discount mDiscount;

        public DiscountViewHolder(View itemView) {
            super(itemView);
            NameTextView = (TextView) itemView.findViewById(R.id.txtDiscountName);
            DescriptionTextView = (TextView) itemView.findViewById(R.id.txDiscountDesc);
            PercentageTextView = (TextView) itemView.findViewById(R.id.txtDiscount);
        }

        public void bindData(Discount discount) {
            mDiscount = discount;

            double discountAmt = (0.01 * mDiscount.Percentage) * mFinalCost;

            NameTextView.setText(mDiscount.Name);
            PercentageTextView.setText(String.format("- %.2f", discountAmt));
            DescriptionTextView.setText(mDiscount.Description);

            setFinalCost(mFinalCost - discountAmt);
            mTotalDiscount += discountAmt;
        }
    }

    void getPaymentOption() {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(mAuthorization);
        startActivityForResult(dropInRequest.getIntent(getActivity()), DROP_IN_REQUEST);
       /* ArrayList<String> mOptionList = new ArrayList<>();
        mOptionList.add(getActivity().getString(R.string.paypal));
        SamsungPay.isReadyToPay(mBraintreeFragment, new BraintreeResponseListener<SamsungPayAvailability>() {
            @Override
            public void onResponse(SamsungPayAvailability availability) {
                switch (availability.getStatus()) {
                    case SPAY_READY:
                        tokenize(prepareFinalCartCost());
                        mOptionList.add(getActivity().getString(R.string.samsung_pay));
//                        mTokenizeButton.setEnabled(true);
                        creatPopupPayment(mOptionList);
                        break;
                    case SPAY_NOT_READY:
                        creatPopupPayment(mOptionList);
//                        Integer reason = availability.getReason();
//                        if (reason == ERROR_SPAY_APP_NEED_TO_UPDATE) {
//                            creatPopupPayment(mOptionList);
//                        } else if (reason == ERROR_SPAY_SETUP_NOT_COMPLETED) {
//                            creatPopupPayment(mOptionList);
//                        } else if (reason == SamsungPay.SPAY_NO_SUPPORTED_CARDS_IN_WALLET) {
//                            creatPopupPayment(mOptionList);
//                        }
                        break;
                    case SPAY_NOT_SUPPORTED:
                        creatPopupPayment(mOptionList);
//                        mTokenizeButton.setEnabled(false);
                        break;
                }
            }
        });*/

    }

    void creatPopupPayment(ArrayList<String> mOptionList) {

        String[] mOptionListArray = new String[mOptionList.size()];
        for (int i = 0; i < mOptionList.size(); i++) {
            mOptionListArray[i] = mOptionList.get(i);
        }

        if (mOptionListArray.length == 1) {
            startActivityForResult(getDropInRequest(prepareFinalCartCost()).getIntent(getActivity()), DROP_IN_REQUEST);
        } else {
            BottomDialog dialog = BottomDialog.newInstance("", getActivity().getString(R.string.btn_cancel), mOptionListArray);
            dialog.show(getChildFragmentManager(), "dialog");
            //add item click listener
            dialog.setListener(new BottomDialog.OnClickListener() {
                @Override
                public void click(int position) {
                    if (position == 0) {
                        startActivityForResult(getDropInRequest(prepareFinalCartCost()).getIntent(getActivity()), DROP_IN_REQUEST);
                    } else {
                        SamsungPay.isReadyToPay(mBraintreeFragment, new BraintreeResponseListener<SamsungPayAvailability>() {
                            @Override
                            public void onResponse(SamsungPayAvailability availability) {
                                tokenize(prepareFinalCartCost());
                                switch (availability.getStatus()) {
                                    case SPAY_READY:

//                        mTokenizeButton.setEnabled(true);
                                        break;
                                    case SPAY_NOT_READY:
                                        Integer reason = availability.getReason();
                                        if (reason == ERROR_SPAY_APP_NEED_TO_UPDATE) {
                                            new DialogsUtil().openAlertDialog(getActivity(), "Need to update Samsung Pay app...", "Yes", "", new OnDialogButtonClickListener() {
                                                @Override
                                                public void onPositiveButtonClicked() {

                                                }

                                                @Override
                                                public void onNegativeButtonClicked() {

                                                }
                                            });
                                            SamsungPay.goToUpdatePage(mBraintreeFragment);
                                        } else if (reason == ERROR_SPAY_SETUP_NOT_COMPLETED) {
                                            new DialogsUtil().openAlertDialog(getActivity(), "Samsung Pay setup not completed...", "Yes", "", new OnDialogButtonClickListener() {
                                                @Override
                                                public void onPositiveButtonClicked() {

                                                }

                                                @Override
                                                public void onNegativeButtonClicked() {

                                                }
                                            });
                                            SamsungPay.activateSamsungPay(mBraintreeFragment);
                                        } else if (reason == SamsungPay.SPAY_NO_SUPPORTED_CARDS_IN_WALLET) {
                                            new DialogsUtil().openAlertDialog(getActivity(), "No supported cards in wallet", "Yes", "", new OnDialogButtonClickListener() {
                                                @Override
                                                public void onPositiveButtonClicked() {

                                                }

                                                @Override
                                                public void onNegativeButtonClicked() {

                                                }
                                            });
                                        }
                                        break;
                                    case SPAY_NOT_SUPPORTED:
                                        new DialogsUtil().openAlertDialog(getActivity(), "Samsung Pay is not supported", "Yes", "", new OnDialogButtonClickListener() {
                                            @Override
                                            public void onPositiveButtonClicked() {

                                            }

                                            @Override
                                            public void onNegativeButtonClicked() {

                                            }
                                        });
//                        mTokenizeButton.setEnabled(false);
                                        break;
                                }
                            }
                        });
                    }
                }
            });
        }
    }

}
