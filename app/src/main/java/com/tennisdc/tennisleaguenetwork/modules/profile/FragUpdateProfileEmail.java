package com.tennisdc.tennisleaguenetwork.modules.profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tennisdc.tennisleaguenetwork.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created on 13-Jul-17.
 */

public class FragUpdateProfileEmail extends Fragment {

    @BindView(R.id.edt_Old_Email)
    EditText mOldEmail_Edt;

    @BindView(R.id.edt_New_Email)
    EditText mNewEmail_Edt;

    @BindView(R.id.edt_Confirm_Email)
    EditText mConfirmEmail_Edt;

    @BindView(R.id.btnUpdateEmail)
    Button mUpdate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_update_email, container, false);
        ButterKnife.bind(this, view);

        mUpdate.setOnClickListener(UpdateEmailClickListener);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Update Email");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private View.OnClickListener UpdateEmailClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isError = false;
            View firstErrorView = null;

            String oldEmail = mOldEmail_Edt.getText().toString().trim();
            String newEmail = mNewEmail_Edt.getText().toString().trim();
            String confirmEmail = mConfirmEmail_Edt.getText().toString().trim();

            if (TextUtils.isEmpty(oldEmail)) {
                isError = true;
                firstErrorView = mOldEmail_Edt;
                mOldEmail_Edt.setError("Provide Current Email");
            } else if (!App.isValidEmail(oldEmail)) {
                mOldEmail_Edt.setError("Invalid Email");
                firstErrorView = mOldEmail_Edt;
                isError = true;
            }

            if (TextUtils.isEmpty(newEmail)) {
                isError = true;
                if (firstErrorView == null) firstErrorView = mNewEmail_Edt;
                mNewEmail_Edt.setError("Provide New Email");
            }else if (!App.isValidEmail(newEmail)) {
                mNewEmail_Edt.setError("Invalid Email");
                if (firstErrorView == null) firstErrorView = mNewEmail_Edt;
                isError = true;
            }

            if (TextUtils.isEmpty(confirmEmail)) {
                isError = true;
                if (firstErrorView == null) firstErrorView = mConfirmEmail_Edt;
                mConfirmEmail_Edt.setError("Provide Confirm Email");
            } else if (!App.isValidEmail(confirmEmail)) {
                mConfirmEmail_Edt.setError("Invalid Email");
                if (firstErrorView == null) firstErrorView = mConfirmEmail_Edt;
                isError = true;
            }

            if (isError) {
                if (firstErrorView != null) {
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    firstErrorView.requestFocus();
                    imm.showSoftInput(firstErrorView, 0);
                }
            } else {

                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "Please wait...");
                WSHandle.Profile.updateProfileEmail(oldEmail,newEmail,confirmEmail,new VolleyHelper.IRequestListener<String, String>() {
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
                                //startActivity(SingleFragmentActivity.MyProfileActivity.getIntent(getActivity()));
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
