package com.tennisdc.tln.modules.profile;

import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created on 28-Sep-17.
 */

public class DlgUpdateProfileEmail extends BaseDialog {

    @BindView(R.id.edt_Old_Email)
    EditText mOldEmail_Edt;

    @BindView(R.id.edt_New_Email)
    EditText mNewEmail_Edt;

    @BindView(R.id.edt_Confirm_Email)
    EditText mConfirmEmail_Edt;

    public static int UPDATE_USER_DETAILS = 111;

    @Override
    protected int getCustomContentLayout() {
        return R.layout.dlg_update_email;
    }

    @Override
    protected void preCreateViewSetup() {
        super.preCreateViewSetup();

        setPositiveButton("Update", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmail();
            }
        });
        setNegativeButton("Cancel", null);
    }

    @Override
    protected void postCreateViewSetup(View v) {
        super.postCreateViewSetup(v);
        ButterKnife.bind(this, v);

        setTitle("Update Email");
    }

    private void updateEmail(){
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
                    final BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                    dialog.setNeutralButton("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            dismiss();
                            getActivity().startActivityForResult(getActivity().getIntent(), UPDATE_USER_DETAILS);
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
}
