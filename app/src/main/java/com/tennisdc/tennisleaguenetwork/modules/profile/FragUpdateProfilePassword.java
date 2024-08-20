package com.tennisdc.tennisleaguenetwork.modules.profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tennisdc.tennisleaguenetwork.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created on 13-Jul-17.
 */

public class FragUpdateProfilePassword  extends Fragment {

    @BindView(R.id.edt_Curr_Password)
    EditText mCurrPasswd;
    @BindView(R.id.edt_New_Password)
    EditText mNewPasswd;
    @BindView(R.id.edt_Confirm_Password)
    EditText mConfirmPasswd;
    @BindView(R.id.btnUpdatePassword)
    Button mUpdate;
    @BindView(R.id.txtView_New_Show)
    TextView txtShowPwd;
    @BindView(R.id.txtView_Confirm_Show)
    TextView txtConfirmShowPwd;

    private int passwordNotVisible=1;
    private int confirmPasswordNotVisible=1;

    private View.OnClickListener ShowTextClickListenser = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(passwordNotVisible == 1){
                txtShowPwd.setText(R.string.txt_hide_pwd);
                mNewPasswd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordNotVisible = 0 ;
            }
            else {
                txtShowPwd.setText(R.string.txt_show_pwd);
                mNewPasswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordNotVisible = 1 ;
            }
        }
    };

    private View.OnClickListener ConfirmShowTextClickListenser = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(confirmPasswordNotVisible == 1){
                txtConfirmShowPwd.setText(R.string.txt_hide_pwd);
                mConfirmPasswd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                confirmPasswordNotVisible = 0 ;
            }
            else {
                txtConfirmShowPwd.setText(R.string.txt_show_pwd);
                mConfirmPasswd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confirmPasswordNotVisible = 1 ;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_update_password, container, false);
        ButterKnife.bind(this, view);

        txtShowPwd.setOnClickListener(ShowTextClickListenser);
        txtConfirmShowPwd.setOnClickListener(ConfirmShowTextClickListenser);
        mUpdate.setOnClickListener(UpdatePasswordClickListener);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Update Password");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private View.OnClickListener UpdatePasswordClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isError = false;
            View firstErrorView = null;

            String currPasswd = mCurrPasswd.getText().toString().trim();
            String newPasswd = mNewPasswd.getText().toString().trim();
            String confirmPasswd = mConfirmPasswd.getText().toString().trim();

            if (TextUtils.isEmpty(currPasswd)) {
                isError = true;
                firstErrorView = mCurrPasswd;
                mCurrPasswd.setError("Provide Current Password");
            }
            if (TextUtils.isEmpty(newPasswd)) {
                isError = true;
                if (firstErrorView == null) firstErrorView = mNewPasswd;
                mNewPasswd.setError("Provide New Password");
            }
            if (TextUtils.isEmpty(confirmPasswd)) {
                isError = true;
                if (firstErrorView == null) firstErrorView = mConfirmPasswd;
                mConfirmPasswd.setError("Provide Confirm Password");
            }

            if (isError) {
                if (firstErrorView != null) {
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    firstErrorView.requestFocus();
                    imm.showSoftInput(firstErrorView, 0);
                }
            } else {

                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "Please wait...");
                WSHandle.Profile.updateProfilePassword(currPasswd, newPasswd, confirmPasswd,new VolleyHelper.IRequestListener<String, String>() {
                    @Override
                    public void onFailureResponse(String response) {
                        progressDialog.dismiss();
                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                        dialog.show(getChildFragmentManager(), "dlg-frag");
                    }

                    @Override
                    public void onSuccessResponse(String response) {
                        progressDialog.dismiss();
                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                        dialog.setNeutralButton("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                              //  startActivity(SingleFragmentActivity.MyProfileActivity.getIntent(getActivity()));
                                getActivity().finish();
                            }
                        });
                        dialog.show(getChildFragmentManager(), "dlg-frag");
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    };
}
