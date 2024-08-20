package com.tennisdc.tln.modules;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.PlayerDetail;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created  on 05-02-2015.
 */
public class DlgCommunicationSettings extends BaseDialog {

    @BindView(R.id.chkBxCallSetting)
    CheckBox CallSettingCheckBox;

    @BindView(R.id.chkBxTextSetting)
    CheckBox TextSettingCheckBox;

    @BindView(R.id.chkBxPushNotificationSetting)
    CheckBox PushNotificationSettingCheckBox;

    PlayerDetail mCurrentPlayer;

    public static DlgCommunicationSettings getInstance() {
        return new DlgCommunicationSettings();
    }

    @Override
    protected void preCreateViewSetup() {
        super.preCreateViewSetup();

        mCurrentPlayer = new Prefs.AppData(getActivity()).getPlayer();

        setTitle("Communication Settings");

        setPositiveButton("Save", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCurrentPlayer.CanCall != CallSettingCheckBox.isChecked() || mCurrentPlayer.CanText != TextSettingCheckBox.isChecked()
                        || mCurrentPlayer.CanPushNotification != PushNotificationSettingCheckBox.isChecked()) {
                    mCurrentPlayer.CanCall = CallSettingCheckBox.isChecked();
                    mCurrentPlayer.CanText = TextSettingCheckBox.isChecked();
                    mCurrentPlayer.CanPushNotification = PushNotificationSettingCheckBox.isChecked();

                    final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Saving settings...");
                    WSHandle.Settings.communicationSettings(mCurrentPlayer.CanCall, mCurrentPlayer.CanText,mCurrentPlayer.CanPushNotification, new VolleyHelper.IRequestListener<String, String>() {
                        @Override
                        public void onFailureResponse(String response) {
                            Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onSuccessResponse(String response) {
                            progressDialog.dismiss();
                            new Prefs.AppData(getActivity()).setPlayer(mCurrentPlayer);
                            getDialog().dismiss();
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    dismiss();
                }
            }
        });

        setNegativeButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    protected void postCreateViewSetup(View v) {
        super.postCreateViewSetup(v);
        ButterKnife.bind(this, v);

        CallSettingCheckBox.setChecked(mCurrentPlayer.CanCall);
        TextSettingCheckBox.setChecked(mCurrentPlayer.CanText);
        PushNotificationSettingCheckBox.setChecked(mCurrentPlayer.CanPushNotification);
    }

    @Override
    protected int getCustomContentLayout() {
        return R.layout.dlg_communication_settings;
    }

    @Override
    protected boolean isForceWrap() {
        return true;
    }

}
