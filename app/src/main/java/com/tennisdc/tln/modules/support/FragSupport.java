package com.tennisdc.tln.modules.support;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tennisdc.tln.R;
import com.tennisdc.tln.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.PlayerDetail;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created  on 2015-05-12.
 */
public class FragSupport extends Fragment {

    @BindView(R.id.txtName)
    TextView mNameTextView;

    @BindView(R.id.txtEmail)
    TextView mEmailTextView;

    @BindView(R.id.edtSubject)
    EditText mSubjectEditText;

    @BindView(R.id.edtMessage)
    EditText mMessageEditText;

    @BindView(R.id.btnSubmit)
    Button mSubmitButton;

    /* data */
    private String mSupportEmail;
    private String mSupportPhone;
    private PlayerDetail mPlayer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_customer_support, container, false);
        App.LogFacebookEvent(getActivity(),this.getClass().getName());

        ButterKnife.bind(this, view);

        Prefs.AppData appData = new Prefs.AppData(getActivity());

        mSupportEmail = appData.getSupportEmail();
        mSupportPhone = appData.getSupportPhone();

        mPlayer = appData.getPlayer();

        mNameTextView.setText(mPlayer.Name);
        mEmailTextView.setText(mPlayer.Email);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = mSubjectEditText.getText().toString();
                String message = mMessageEditText.getText().toString();

                Spanned emailBodySpanned = Html.fromHtml(getActivity().getString(R.string.email_support_body, mPlayer.Name, mPlayer.CityName, mPlayer.Email, subject, message));

                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mSupportEmail, null));
                intent.putExtra(Intent.EXTRA_SUBJECT, getActivity().getString(R.string.email_support_subject, mPlayer.Name));
                intent.putExtra(Intent.EXTRA_TEXT, emailBodySpanned);

                startActivity(Intent.createChooser(intent, "Send Email"));
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle("Customer Support");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_FIRST_USER) {
            startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
