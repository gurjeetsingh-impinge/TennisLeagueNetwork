package com.tennisdc.tennisleaguenetwork.modules.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tln.common.App;

import org.w3c.dom.Text;

public class FragRegisterStep1 extends Fragment {

    private Context mContext;

    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private EditText edtZipCode;
    private CheckBox mCheckBox;
    private TextView txtPolicy;
    private TextView linkPolicy;
    private TextView txtShowPwd;
    private TextView txtConfirmShowPwd;

    private int passwordNotVisible=1;
    private int confirmPasswordNotVisible=1;

    private View.OnClickListener ShowTextClickListenser = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(passwordNotVisible == 1){
                txtShowPwd.setText(R.string.txt_hide_pwd);
                edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordNotVisible = 0 ;
            }
            else {
                txtShowPwd.setText(R.string.txt_show_pwd);
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordNotVisible = 1 ;
            }
        }
    };

    private View.OnClickListener ConfirmShowTextClickListenser = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(confirmPasswordNotVisible == 1){
                txtConfirmShowPwd.setText(R.string.txt_hide_pwd);
                edtConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                confirmPasswordNotVisible = 0 ;
            }
            else {
                txtConfirmShowPwd.setText(R.string.txt_show_pwd);
                edtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confirmPasswordNotVisible = 1 ;
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
            String strZip = edtZipCode.getText().toString().trim();

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

            if (TextUtils.isEmpty(strZip)) {
                edtZipCode.setError("Provide Zip Code");
                if (firstErrorView == null) firstErrorView = edtZipCode;
                isError = true;
            } else if (!(TextUtils.isDigitsOnly(strZip) && strZip.length() == 5)) {
                edtZipCode.setError("Invalid Zip Code");
                if (firstErrorView == null) firstErrorView = edtZipCode;
                isError = true;
            }

            if (!mCheckBox.isChecked()) {
                Toast.makeText(mContext, "Please accept 'Privacy Policy'.", Toast.LENGTH_SHORT).show();
                isError = true;
            }

            if (!isError) {
                startActivity(SingleFragmentActivity.getIntent(mContext, FragRegisterStep2.class, FragRegisterStep2.getFragArgumentBundle(strFName, strLName, strEmail, strPass, strZip)));
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

        edtFirstName = (EditText) v.findViewById(R.id.edtFName);
        edtLastName = (EditText) v.findViewById(R.id.edtLName);
        edtEmail = (EditText) v.findViewById(R.id.edtEmail);
        edtPassword = (EditText) v.findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) v.findViewById(R.id.edtConfPassword);
        edtZipCode = (EditText) v.findViewById(R.id.edtZipCode);
        txtShowPwd = (TextView) v.findViewById(R.id.txtView_Show) ;
        txtConfirmShowPwd = (TextView) v.findViewById(R.id.txtView_ConfirmShow);

        txtShowPwd.setOnClickListener(ShowTextClickListenser);
        txtConfirmShowPwd.setOnClickListener(ConfirmShowTextClickListenser);

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

    @Override
    public void onResume() {
        super.onResume();

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

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
