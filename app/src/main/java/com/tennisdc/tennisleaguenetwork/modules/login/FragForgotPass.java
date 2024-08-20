package com.tennisdc.tennisleaguenetwork.modules.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tennisdc.tennisleaguenetwork.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;

public class FragForgotPass extends Fragment {

    private Context mContext;

    private EditText edtEmail;
    private ProgressDialog mProgressDialog;

    //private AsyncTask<String, Void, JSONObject> mAsyncTask;

    private View.OnClickListener SubmitForgotPAssButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isError = false;

            String strEmail = edtEmail.getText().toString().trim();

            if (TextUtils.isEmpty(strEmail)) {
                edtEmail.setError("Provide Email");
                isError = true;
            } else if (!App.isValidEmail(strEmail)) {
                edtEmail.setError("Invalid Email");
                isError = true;
            }

            if (!isError) {
                if (App.isNetworkOnline(mContext)) {
                    //mAsyncTask = new ForgotPassAsyncTask();
                    //mAsyncTask.execute(strEmail);
                    mProgressDialog = ProgressDialog.show(mContext, null, "Please wait...");
                    WSHandle.Login.forgotPass(strEmail, new VolleyHelper.IRequestListener<String, String>() {
                        @Override
                        public void onFailureResponse(String response) {
                            mProgressDialog.dismiss();
                            edtEmail.setError("Email not found");
                        }

                        @Override
                        public void onSuccessResponse(String response) {
                            mProgressDialog.dismiss();
                            BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, "Reset instructions sent to your Email.");
                            dialog.setPositiveButton(getString(android.R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getActivity().onBackPressed();
                                    getActivity().finish();
                                }
                            });
                            dialog.show(getChildFragmentManager(), "dlg-frag");
                        }

                        @Override
                        public void onError(VolleyError error) {
                            mProgressDialog.dismiss();
                            Toast.makeText(mContext, "Network error occurred", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    BaseDialog.SimpleDialog.getDialogInstance(null, 0, "Internet not working?").show(getChildFragmentManager(), "dlg-frg");
                }
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_forgot_password, container, false);

        edtEmail = (EditText) v.findViewById(R.id.edtEmail);

        v.findViewById(R.id.btnSubmit).setOnClickListener(SubmitForgotPAssButtonClickListener);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDetach() {
        mContext = null;
        super.onDetach();
    }

    /*private class ForgotPassAsyncTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mContext, null, "Please wait...", true, false);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            return WSHandle.Login.forgotPass(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            mProgressDialog.dismiss();
            if (jsonObject != null) {
                try {
                    if (jsonObject.get("responseCode").equals("200")) {
                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, "Reset instructions sent to your Email.");
                        dialog.setPositiveButton(getString(android.R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().onBackPressed();
                                getActivity().finish();
                            }
                        });
                        dialog.show(getChildFragmentManager(), "dlg-frag");
                    } else {
                        edtEmail.setError("Email not found");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

}
