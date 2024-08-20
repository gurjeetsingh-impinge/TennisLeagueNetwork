package com.tennisdc.tln.modules.profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created on 25-Sep-17.
 */

public class DlgUpdateProfileName extends BaseDialog {


    public static int UPDATE_USER_DETAILS = 111;
    @BindView(R.id.edt_curr_name)
    TextView mCurrName_Edt;
    @BindView(R.id.edt_proposed_fname)
    EditText mNew_FName_Edt;
    @BindView(R.id.edt_proposed_lname)
    EditText mNew_LName_Edt;
    private String mFullName;

    @Override
    protected int getCustomContentLayout() {
        return R.layout.dlg_update_name;
    }

    @Override
    protected void preCreateViewSetup() {
        super.preCreateViewSetup();

        setPositiveButton("Update", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateName();
            }
        });

        setNegativeButton("Cancel", null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void postCreateViewSetup(View v) {
        super.postCreateViewSetup(v);
        ButterKnife.bind(this, v);

        setTitle("Update Name");
        mCurrName_Edt.setText(mFullName);
    }

    private void updateName() {
        boolean isError = false;
        View firstErrorView = null;

        String fname = mNew_FName_Edt.getText().toString().trim();
        String lname = mNew_LName_Edt.getText().toString().trim();

        if (TextUtils.isEmpty(fname)) {
            isError = true;
            firstErrorView = mNew_FName_Edt;
            mNew_FName_Edt.setError("Provide Proposed First Name");
        }
        if (TextUtils.isEmpty(lname)) {
            isError = true;
            if (firstErrorView == null) firstErrorView = mNew_LName_Edt;
            mNew_LName_Edt.setError("Provide Proposed Last Name");
        }

        if (isError) {
            if (firstErrorView != null) {
                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                firstErrorView.requestFocus();
                imm.showSoftInput(firstErrorView, 0);
            }
        } else {
            final ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "Please wait...");
            WSHandle.Profile.updateProfileName(fname, lname, new VolleyHelper.IRequestListener<String, String>() {
                @Override
                public void onFailureResponse(String response) {
                    progressDialog.dismiss();
                    BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                    dialog.show(getFragmentManager(), "dlg-frag");
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
                    dialog.show(getFragmentManager(), "dlg-frag");
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void setUserName(String username) {
        this.mFullName = username;
    }
}

