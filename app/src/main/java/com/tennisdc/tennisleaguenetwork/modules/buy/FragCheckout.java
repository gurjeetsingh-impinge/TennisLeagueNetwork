package com.tennisdc.tennisleaguenetwork.modules.buy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.ads.conversiontracking.AdWordsRemarketingReporter;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.tennisdc.tennisleaguenetwork.BaseDialog;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.common.Prefs;
import com.tennisdc.tennisleaguenetwork.model.CouponDetails;
import com.tennisdc.tennisleaguenetwork.model.Discount;
import com.tennisdc.tennisleaguenetwork.model.Payment;
import com.tennisdc.tennisleaguenetwork.model.PlayerDetail;
import com.tennisdc.tennisleaguenetwork.model.Program;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;
import com.tennisdc.tln.paypal.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.BindView;
import io.realm.Realm;
import com.tennisdc.tln.R;

public class FragCheckout extends Fragment {

    private static final String TAG = FragCheckout.class.getSimpleName();

    private static final String EXTRA_SELECTION = "selection";

//    private static final int REQUEST_CODE_PAYMENT = 1;
//    // PayPal configuration
//    private static PayPalConfiguration paypalConfig = new PayPalConfiguration().environment(Config.PAYPAL_ENVIRONMENT).clientId(Config.PAYPAL_CLIENT_ID);
//    /* view */
//    @BindView(R.id.containerPrice)
//    LinearLayout mCheckoutItemContainer;
//
//    @BindView(R.id.txtFinalCost)
//    TextView mFinalCostTextView;
//
//    /* Promo Code Views */
//    @BindView(R.id.containerPromoDiscountInput)
//    View mPromoCodeInputView;
//
//    @BindView(R.id.edtCoupon)
//    EditText mCouponEditText;
//
//    @BindView(R.id.btnApplyCoupon)
//    Button mApplyCouponButton;
//
//    @BindView(R.id.containerPromoDiscount)
//    View mPromoCodeDiscountView;
//
//    @BindView(R.id.txtPromoCodeDiscount)
//    TextView mPromoCodeDiscountTextView;
//
//    @BindView(R.id.txtPromoDesc)
//    TextView mPromoDescTextView;
//
//    /* Veteran Discount Views */
//
//    @BindView(R.id.containerVeteranDiscount)
//    LinearLayout mVeteranDiscountView;
//
//  /*  @BindView(R.id.txtVeteranDiscountName)
//    TextView mVeteranDiscountNameTextView;
//
//    @BindView(R.id.txtVeteranDiscount)
//    TextView mVeteranDiscountTextView;
//
//    @BindView(R.id.txtVeteranDesc)
//    TextView mVeteranDescTextView;*/
//
//    @BindView(R.id.btnSubmit)
//    Button mSubmitButton;
//
//    /* data */ List<Program> mPrograms;
//   // private RecyclerAdapter.RecyclerSelectionHelper<Discount> mRecyclerSelectionHelper;
//    private double mFinalCost;
//    private boolean mIsPaymentDone = false;
//    private double mTotalDiscount = 0;

    public static Bundle buildArgs(List<Program> selectedPrograms) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_SELECTION, Parcels.wrap(selectedPrograms));
        return args;
    }
//
//    /**
//     * Receiving the PalPay payment response
//     */
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_CODE_PAYMENT) {
//            if (resultCode == Activity.RESULT_OK) {
//                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
//                if (confirm != null) {
//                    try {
//                        //Log.e(TAG, confirm.toJSONObject().toString(4));
//                        //Log.e(TAG, confirm.getPayment().toJSONObject().toString(4));
//
//                        String paymentId = confirm.toJSONObject().getJSONObject("response").getString("id");
//
//                        //String payment_client = confirm.getPayment().toJSONObject().toString();
//
//                        //Log.e(TAG, "paymentId: " + paymentId + ", payment_json: " + payment_client);
//
//                        // Now verify the payment on the server side
//                        verifyPaymentOnServer(paymentId);
//
//                    } catch (JSONException e) {
//                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
//                    }
//                }
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Log.e(TAG, "The user canceled.");
//            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
//                Log.e(TAG, "An invalid Payment or PayPalConfiguration was submitted.");
//            }
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.frag_buy_checkout, container, false);
//
//        ButterKnife.bind(this, view);
//
//        Bundle arguments = getArguments();
//        if (arguments == null) throw new RuntimeException("Argument missing");
//
//        mPrograms = Parcels.unwrap(arguments.getParcelable(EXTRA_SELECTION));
//
//        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
//
//        double finalCost = 0;
//        for (Program program : mPrograms) {
//            View checkoutView = layoutInflater.inflate(R.layout.view_buy_checkout_item, mCheckoutItemContainer, false);
//
//            ((TextView) checkoutView.findViewById(R.id.txtItem)).setText(program.type);
//            if(program.programPrices != null && !program.programPrices.isEmpty()) {
//                ((TextView) checkoutView.findViewById(R.id.txtCost)).setText(String.format("%.2f", program.programPrices.get(program.selectedPriceIndex).price));
//                finalCost += program.programPrices.get(program.selectedPriceIndex).price;
//            }
//            else {
//                ((TextView) checkoutView.findViewById(R.id.txtCost)).setText(String.format("%.2f", 0.0));
//               // finalCost += program.programPrices.get(program.selectedPriceIndex).price;
//            }
//
//
//            checkoutView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            mCheckoutItemContainer.addView(checkoutView);
//        }
//
//        setFinalCost(finalCost);
//       // checkAndApplyVeteranDiscount();
//
//        Prefs.AppData appData = new Prefs.AppData(getActivity());
//        PlayerDetail player = appData.getPlayer();
//        if(player != null && player.discount != null && !player.discount.isEmpty()) {
//            for (Discount discount : player.discount) {
//                View discountView = layoutInflater.inflate(R.layout.view_discount_item, mVeteranDiscountView, false);
//                double discountAmt = (0.01 * discount.Percentage) * mFinalCost;
//
//                ((TextView) discountView.findViewById(R.id.txtDiscountName)).setText(discount.Name);
//                ((TextView) discountView.findViewById(R.id.txDiscountDesc)).setText(discount.Description);
//                ((TextView) discountView.findViewById(R.id.txtDiscount)).setText(String.format("- %.2f", discountAmt));
//
//                setFinalCost(mFinalCost - discountAmt);
//                mTotalDiscount += discountAmt;
//
//                discountView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                mVeteranDiscountView.addView(discountView);
//            }
//        }
//
//        applyPromoCode(App.sCouponDetails);
//
//        mCouponEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                mApplyCouponButton.setEnabled(s.length() > 0);
//            }
//        });
//
//        mApplyCouponButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "validating code...");
//                WSHandle.Buy.getPromoCodeDetails(mCouponEditText.getText().toString().trim(), new VolleyHelper.IRequestListener<CouponDetails, String>() {
//                    @Override
//                    public void onFailureResponse(String response) {
//                        progressDialog.dismiss();
//                        Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onSuccessResponse(CouponDetails response) {
//                        applyPromoCode(response);
//                        progressDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onError(VolleyError error) {
//                        progressDialog.dismiss();
//                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        });
//
//        mSubmitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mFinalCost > 0)
//                    performPayment();
//            }
//        });
//
//        return view;
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        /* start paypal service */
//        Intent intent = new Intent(getActivity(), PayPalService.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
//        getActivity().startService(intent);
//
//        reportToAdwords();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mIsPaymentDone) {
//            showCompletionPopup();
//        }
//
//        getActivity().setTitle("Checkout");
//    }
//
//    @Override
//    public void onDestroy() {
//        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
//        super.onDestroy();
//    }
//
//    void setFinalCost(double finalCost) {
//        mFinalCost = finalCost > 0 ? finalCost : 0;
//        mFinalCostTextView.setText(String.format("%.2f", mFinalCost));
//    }
//
//    void checkAndApplyVeteranDiscount() {
//
//       /* Prefs.AppData appData = new Prefs.AppData(getActivity());
//        PlayerDetail player = appData.getPlayer();
//        if(!player.discount.isEmpty()) {
//            mDiscountRecyclerView.setVisibility(View.VISIBLE);
//            mDiscountRecyclerView.setAdapter(new RecyclerAdapter<Discount, DiscountViewHolder>(player.discount) {
//                @Override
//                public DiscountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_discount_item, parent, false);
//                    return new DiscountViewHolder(view);
//                }
//
//                @Override
//                public void onBindViewHolder(DiscountViewHolder holder, int position) {
//                    holder.bindData(getItem(position));
//                }
//
//            });
//        }*/
//            /*for(int i=0;i<player.discount.size();i++){
//                double discount = (0.01 * player.discount.get(i).getPercentage()) * mFinalCost;
//
//                mVeteranDiscountNameTextView.setText(player.discount.get(i).getName());
//                mVeteranDescTextView.setText( player.discount.get(i).getDescription());
//                mVeteranDiscountTextView.setText(String.valueOf(player.discount.get(i).getPercentage()));
//                mVeteranDiscountView.setVisibility(View.VISIBLE);
//
//                setFinalCost(mFinalCost - discount);
//                mTotalDiscount += discount;
//            }
//        }*/
//
//       /* if (player.VeteranDiscount > 0) {
//            double discount = (0.01 * player.VeteranDiscount) * mFinalCost;
//
//            mVeteranDescTextView.setText("You have played more than " + (player.VeteranDiscount == 10 ? "400" : "200") + " matches, a " + String.format("- %.2f", discount) + " % discount will be applied.");
//            mVeteranDiscountTextView.setText(String.format("- %.2f", discount));
//            mVeteranDiscountView.setVisibility(View.VISIBLE);
//
//            setFinalCost(mFinalCost - discount);
//            mTotalDiscount += discount;
//        }*/
//    }
//
//    void applyPromoCode(CouponDetails couponDetails) {
//        App.sCouponDetails = couponDetails;
//
//        if (couponDetails != null) {
//            double discount = couponDetails.calculateDiscount(mFinalCost);
//
//            mPromoCodeInputView.setVisibility(View.GONE);
//
//            mPromoDescTextView.setText(couponDetails.message);
//            mPromoCodeDiscountTextView.setText(String.format("- %.2f", discount));
//            mPromoCodeDiscountView.setVisibility(View.VISIBLE);
//
//            setFinalCost(mFinalCost - discount);
//            mTotalDiscount += discount;
//        }
//    }
//
//    /**
//     * Launching PalPay payment activity to complete the payment
//     */
//    private void performPayment() {
//
//        if (mFinalCost > 0) {
//            PayPalPayment thingsToBuy = prepareFinalCart();
//
//            Intent intent = new Intent(getActivity(), PaymentActivity.class);
//            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
//            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);
//
//            startActivityForResult(intent, REQUEST_CODE_PAYMENT);
//        } else {
//            verifyPaymentOnServer("NO-ID-" + String.valueOf(new Date().getTime()));
//            //showCompletionPopup();
//        }
//    }
//
//    /**
//     * Preparing final cart amount that needs to be sent to PayPal for payment
//     */
//    private PayPalPayment prepareFinalCart() {
//
//        /*List<PayPalItem> productsInCart = new ArrayList<>();
//
//        for (Program program : mPrograms) {
//            PayPalItem item = new PayPalItem(program.type, 1, //Quantity
//                    new BigDecimal(program.programPrices.get(program.selectedPriceIndex).price), //price
//                    Config.DEFAULT_CURRENCY, // currency
//                    String.valueOf(program.id)); // stock keeping unit
//
//            productsInCart.add(item);
//        }
//
//        if(App.sCouponDetails != null )
//        {
//            App.sCouponDetails.calculateDiscount(mFinalCost)
//        }*/
//
//        //PayPalItem[] items = new PayPalItem[productsInCart.size()];
//        //items = productsInCart.toArray(items);
//
//        /*// Total amount
//        BigDecimal subtotal = PayPalItem.getItemTotal(items);
//
//        // If you have shipping cost, add it here
//        BigDecimal shipping = new BigDecimal("0.0");
//
//        // If you have tax, add it here
//        BigDecimal tax = new BigDecimal("0.0");
//
//        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
//
//        BigDecimal amount = subtotal.add(shipping).add(tax);*/
//
//        PayPalPayment payment = new PayPalPayment(new BigDecimal(mFinalCost), Config.DEFAULT_CURRENCY, "Total Amount", Config.PAYMENT_INTENT);
//
//        //payment.items(items).paymentDetails(paymentDetails);
//
//        // Custom field like invoice_number etc.,
//        //payment.custom("This is text that will be associated with the payment that the app can use.");
//
//        return payment;
//    }
//
//    private void verifyPaymentOnServer(String paymentId) {
//
//        /*create object*/
//        final JSONObject paymentDetails = new JSONObject();
//        try {
//            paymentDetails.put("oauth_token", App.sOAuth);
//            paymentDetails.put("payment_method", "paypal");
//            paymentDetails.put("transaction_id", TextUtils.isEmpty(paymentId) ? "" : paymentId);
//            paymentDetails.put("user_promo_code", App.sCouponDetails != null);
//            paymentDetails.put("promo_code_id", App.sCouponDetails != null ? App.sCouponDetails.id : "");
//            paymentDetails.put("total_amount", mFinalCost);
//
//            JSONArray programJsonArray = new JSONArray();
//            JSONObject programObject;
//            Program program;
//            int programsCount = mPrograms.size();
//            for (int i = 0; i < programsCount; i++) {
//                program = mPrograms.get(i);
//                programObject = new JSONObject();
//
//                programObject.put("id", program.id);
//                long priceId = program.programPrices.get(program.selectedPriceIndex).id;
//                programObject.put("non_league_price_option_id", priceId == 0 ? "" : priceId);
//
//                if (i == (programsCount - 1))
//                    programObject.put("unit_price", program.programPrices.get(program.selectedPriceIndex).price - mTotalDiscount);
//                else
//                    programObject.put("unit_price", program.programPrices.get(program.selectedPriceIndex).price);
//
//                programJsonArray.put(programObject);
//            }
//
//            paymentDetails.put("programs", programJsonArray);
//
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//        }
//
//        /* store locally */
//        Realm realm = Realm.getInstance(getActivity());
//
//        realm.beginTransaction();
//
//        Payment payment = realm.createObject(Payment.class);
//        payment.setId(paymentId);
//        payment.setJsonString(paymentDetails.toString());
//
//        realm.commitTransaction();
//        realm.close();
//
//        /* start service */
//        //getActivity().startService(new Intent(getActivity().getApplicationContext(), SubmitPaymentService.class));
//
//        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
//        WSHandle.Buy.submitPaymentDetails(paymentDetails, new VolleyHelper.IRequestListener<String, String>() {
//            @Override
//            public void onFailureResponse(String response) {
//                progressDialog.dismiss();
//                Toast.makeText(getActivity(), "Failed while submitting payment details : " + response, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onSuccessResponse(String response) {
//                progressDialog.dismiss();
//                Realm realm = Realm.getInstance(getActivity());
//                try {
//                    realm.beginTransaction();
//                    realm.where(Payment.class).equalTo("id", paymentDetails.get("transaction_id").toString()).findFirst().removeFromRealm();
//                    realm.commitTransaction();
//                  //  reportToAdwords();
//                    showCompletionPopup();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } finally {
//                    realm.close();
//                }
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                progressDialog.dismiss();
//                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    void showCompletionPopup() {
//        /* navigate to previous page */
//        BaseDialog.SimpleDialog.getDialogInstance("Thanks!!!", 0, "Your program is most likely already configured, but in some cases give our staff 24 hours. Please check your email for updates.").setNeutralButton("OK", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
//            }
//        }).show(getFragmentManager(), "confirm-dlg");
//    }
//
//    void reportToAdwords() {
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("action_type", "purchase");
//        params.put("value", "1");
//        AdWordsRemarketingReporter.reportWithConversionId(
//                getContext(),
//                "1027834051",
//                params);
//    /*  AdWordsConversionReporter.reportWithConversionId(this.getContext(),
//                "1038185027", "aqUCHIerhAgQw-SF7wM", "0", true);*/
//    }
//
//
//    private class DiscountViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView NameTextView;
//        private TextView DescriptionTextView;
//        private TextView PercentageTextView;
//
//        private Discount mDiscount;
//
//        public DiscountViewHolder(View itemView) {
//            super(itemView);
//            NameTextView = (TextView) itemView.findViewById(R.id.txtDiscountName);
//            DescriptionTextView = (TextView) itemView.findViewById(R.id.txDiscountDesc);
//            PercentageTextView = (TextView) itemView.findViewById(R.id.txtDiscount);
//        }
//
//        public void bindData(Discount discount) {
//            mDiscount = discount;
//
//            double discountAmt = (0.01 * mDiscount.Percentage) * mFinalCost;
//
//            NameTextView.setText(mDiscount.Name);
//            PercentageTextView.setText(String.format("- %.2f", discountAmt));
//            DescriptionTextView.setText(mDiscount.Description);
//
//            setFinalCost(mFinalCost - discountAmt);
//            mTotalDiscount += discountAmt;
//        }
//    }

}
