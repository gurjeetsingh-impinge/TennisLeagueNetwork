package com.tennisdc.tln.modules.register;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.ListPopupWindow;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.R;
import com.tennisdc.tln.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.model.Domain;
import com.tennisdc.tln.model.State;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragRegisterStep1 extends Fragment {

    private Context mContext;

    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private EditText edtCity;
    //	private EditText edtZipCode;
    private CheckBox mCheckBox;
    private TextView txtPolicy;
    private TextView tvStateCode;
    private TextView tvDomain, tvDomainText;
    private TextView linkPolicy;
    private TextView txtShowPwd;
    private ProgressDialog mProgressDialog;
    private TextView txtConfirmShowPwd;
    private TextView txtCity;
    private List<Domain> mDomainList;
    private ArrayList<State> stateCodeList;

    private int passwordNotVisible = 1;
    private String domainId = "-1";
    private int confirmPasswordNotVisible = 1;

    private View.OnClickListener ShowTextClickListenser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (passwordNotVisible == 1) {
                txtShowPwd.setText(R.string.txt_hide_pwd);
                edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordNotVisible = 0;
            } else {
                txtShowPwd.setText(R.string.txt_show_pwd);
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordNotVisible = 1;
            }
        }
    };

    private View.OnClickListener DomainClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mDomainList != null) {
                showDomainDropDownList(mDomainList, tvDomain);
            }
        }
    };

    private View.OnClickListener StateCodeListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (stateCodeList == null) {
                if (App.isNetworkOnline(mContext)) {
                    mProgressDialog = ProgressDialog.show(mContext, null, "Please wait...", true, false);
                    WSHandle.Login.getStateCodes(new VolleyHelper.IRequestListener<JSONObject, String>() {
                        @Override
                        public void onFailureResponse(String response) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccessResponse(JSONObject response) {
                            mProgressDialog.dismiss();
                            try {
                                stateCodeList = new Gson().fromJson(response.getJSONArray("states").toString(),
                                        new TypeToken<List<State>>() {
                                        }.getType());
                                showStateCodesDropDownList(stateCodeList, tvStateCode);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {
                            mProgressDialog.dismiss();
                            Toast.makeText(mContext, "Network error occurred", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                showStateCodesDropDownList(stateCodeList, tvStateCode);
            }
        }
    };

    private View.OnClickListener ConfirmShowTextClickListenser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (confirmPasswordNotVisible == 1) {
                txtConfirmShowPwd.setText(R.string.txt_hide_pwd);
                edtConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                confirmPasswordNotVisible = 0;
            } else {
                txtConfirmShowPwd.setText(R.string.txt_show_pwd);
                edtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confirmPasswordNotVisible = 1;
            }
        }
    };

    private View.OnClickListener NextButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isError = false;

            View firstErrorView = null;

            String strFName = edtFirstName.getText().toString().trim();
            String strLName = edtLastName.getText().toString().trim();
            String strEmail = edtEmail.getText().toString().trim();
            String strPass = edtPassword.getText().toString().trim();
            String strConfPass = edtConfirmPassword.getText().toString().trim();
            String strState = tvStateCode.getText().toString().trim();
            String strCity = edtCity.getText().toString().trim();
//			String strZip = edtZipCode.getText().toString().trim();

            if (TextUtils.isEmpty(strFName)) {
                edtFirstName.setError("Provide First Name");
                firstErrorView = edtFirstName;
                isError = true;
            }

            if (TextUtils.isEmpty(strLName)) {
                edtLastName.setError("Provide Last Name");
                if (firstErrorView == null) firstErrorView = edtLastName;
                isError = true;
            }

            if (TextUtils.isEmpty(strEmail)) {
                edtEmail.setError("Provide Email");
                if (firstErrorView == null) firstErrorView = edtEmail;
                isError = true;
            } else if (!App.isValidEmail(strEmail)) {
                edtEmail.setError("Invalid Email");
                if (firstErrorView == null) firstErrorView = edtEmail;
                isError = true;
            }

            if (TextUtils.isEmpty(strPass)) {
                edtPassword.setError("Provide Password");
                if (firstErrorView == null) firstErrorView = edtPassword;
                isError = true;
            }

            if (TextUtils.isEmpty(strConfPass)) {
                edtConfirmPassword.setError("Provide Confirm Password");
                if (firstErrorView == null) firstErrorView = edtConfirmPassword;
                isError = true;
            } else if (!strConfPass.equals(strPass)) {
                edtConfirmPassword.setError("Incorrect Confirm Password");
                if (firstErrorView == null) firstErrorView = edtConfirmPassword;
                isError = true;
            }

//			if (TextUtils.isEmpty(strZip)) {
//				edtZipCode.setError("Provide Zip Code");
//				if (firstErrorView == null) firstErrorView = edtZipCode;
//				isError = true;
//			} else if (!(TextUtils.isDigitsOnly(strZip) && strZip.length() == 5)) {
//				edtZipCode.setError("Invalid Zip Code");
//				if (firstErrorView == null) firstErrorView = edtZipCode;
//				isError = true;
//			}

            if (tvStateCode.getText().toString().trim().equalsIgnoreCase(getActivity().getString(R.string.hint_state_code))) {
                Toast.makeText(mContext, "Please choose your state code.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mDomainList != null && !mDomainList.isEmpty()){
                if(tvDomain.getText().toString().trim().equalsIgnoreCase(getActivity().getString(R.string.hint_domain))) {
                    Toast.makeText(mContext, "Please select a domain.", Toast.LENGTH_SHORT).show();
                    return;
                }else if(domainId.equalsIgnoreCase("-1")){
                    Toast.makeText(mContext, "Please enter city name", Toast.LENGTH_SHORT).show();
                    return;
                }
            }


                if (!mCheckBox.isChecked()) {
                    Toast.makeText(mContext, "Please accept 'Privacy Policy'.", Toast.LENGTH_SHORT).show();
                    isError = true;
                }

            if (!isError) {

                startActivity(SingleFragmentActivity.getIntent(mContext, FragRegisterStep2.class,
                        FragRegisterStep2.getFragArgumentBundle(strFName, strLName, strEmail, strPass, "", domainId,strState,strCity )));
            } else {
                if (firstErrorView != null) {
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    firstErrorView.requestFocus();
                    imm.showSoftInput(firstErrorView, 0);
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_register, container, false);
        App.LogFacebookEvent(getActivity(),this.getClass().getName());

        edtFirstName = (EditText) v.findViewById(R.id.edtFName);
        edtLastName = (EditText) v.findViewById(R.id.edtLName);
        edtEmail = (EditText) v.findViewById(R.id.edtEmail);
        edtPassword = (EditText) v.findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) v.findViewById(R.id.edtConfPassword);
        edtCity = (EditText) v.findViewById(R.id.edtCity);
//		edtZipCode = v.findViewById(R.id.edtZipCode);
        txtShowPwd = (TextView) v.findViewById(R.id.txtView_Show);
        txtConfirmShowPwd = (TextView) v.findViewById(R.id.txtView_ConfirmShow);
        tvStateCode = v.findViewById(R.id.tvStateCode);
        tvDomain = v.findViewById(R.id.tvDomain);
        tvDomainText = v.findViewById(R.id.tvDomainText);
        txtCity = v.findViewById(R.id.txtCity);

        tvDomain.setVisibility(View.GONE);
        tvDomainText.setVisibility(View.GONE);

        txtShowPwd.setOnClickListener(ShowTextClickListenser);
        txtConfirmShowPwd.setOnClickListener(ConfirmShowTextClickListenser);
        tvStateCode.setOnClickListener(StateCodeListner);
        tvDomain.setOnClickListener(DomainClickListener);

        v.findViewById(R.id.btnNext).setOnClickListener(NextButtonClickListener);

        mCheckBox = (CheckBox) v.findViewById(R.id.chk_policy);
        txtPolicy = (TextView) v.findViewById(R.id.txtPolicy);
        linkPolicy = (TextView) v.findViewById(R.id.link);
        txtPolicy.setText("I Accept ");

        linkPolicy.setPaintFlags(linkPolicy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        linkPolicy.setText("Privacy Policy.");
        linkPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlPolicy = "https://www.tennisdc.com/api/privacy_policy";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlPolicy));
                startActivity(intent);
            }
        });

        return v;
    }


    public void showStateCodesDropDownList(final List<State> stateList,
                                           final TextView textView) {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(mContext);

        listPopupWindow.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, stateList));

        listPopupWindow.setAnchorView(textView);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                    long l) {
                listPopupWindow.dismiss();
                tvDomain.setText(getActivity().getString(R.string.hint_domain));
                textView.setText(stateList.get(position).code);
                domainId = "-1";
                if (stateList.get(position).domains != null && stateList.get(position).domains.size() > 0) {
                    mDomainList = stateList.get(position).domains;
                    tvDomain.setVisibility(View.VISIBLE);
                    tvDomainText.setVisibility(View.VISIBLE);
                    txtCity.setVisibility(View.GONE);
                    edtCity.setVisibility(View.GONE);
                } else {
                    mDomainList = null;
                    tvDomain.setVisibility(View.GONE);
                    tvDomainText.setVisibility(View.GONE);
                    txtCity.setVisibility(View.VISIBLE);
                    edtCity.setVisibility(View.VISIBLE);
                }

            }
        });
        listPopupWindow.show();
    }

    public void showDomainDropDownList(final List<Domain> domainList,
                                       final TextView textView) {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(mContext);

        listPopupWindow.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1,
                domainList));

        listPopupWindow.setAnchorView(textView);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                    long l) {
                listPopupWindow.dismiss();
                textView.setText(domainList.get(position).name);
                domainId = domainList.get(position).id + "";
                if (domainId.equalsIgnoreCase("0")) {
                    txtCity.setVisibility(View.VISIBLE);
                    edtCity.setVisibility(View.VISIBLE);
                } else {
                    txtCity.setVisibility(View.GONE);
                    edtCity.setVisibility(View.GONE);
                }

            }
        });
        listPopupWindow.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);

        edtFirstName.postDelayed(new Runnable() {
            @Override
            public void run() {
                edtFirstName.requestFocus();
                imm.showSoftInput(edtFirstName, 0);
            }
        }, 100);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onDetach() {
        mContext = null;
        super.onDetach();
    }
}
