package com.tennisdc.tln.modules.profile;

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
import android.widget.TextView;
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
 * Created 10-Jul-17.
 */

public class FragUpdateProfileName extends Fragment {

    @BindView(R.id.edt_curr_name)
    TextView mCurrName_Edt;

    @BindView(R.id.edt_proposed_fname)
    EditText mNew_FName_Edt;

    @BindView(R.id.edt_proposed_lname)
    EditText mNew_LName_Edt;

    @BindView(R.id.btnUpdateName)
    Button mUpdate_Btn;

    private String mFullName;

    public static Bundle getFragArgsBundle(String name) {
        Bundle args = new Bundle();
        args.putString("Name", name);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mFullName = args.getString("Name");
        } else {
            throw new RuntimeException("missing arguments");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_update_name, container, false);
        App.LogFacebookEvent(getActivity(),this.getClass().getName());
        ButterKnife.bind(this, view);

        mCurrName_Edt.setText(mFullName);
        mUpdate_Btn.setOnClickListener(UpdateNameClickListener);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Update Name");
    }

    private View.OnClickListener UpdateNameClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
                WSHandle.Profile.updateProfileName(fname, lname,new VolleyHelper.IRequestListener<String, String>() {
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
                               // startActivity(SingleFragmentActivity.MyProfileActivity.getIntent(getActivity()));
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
