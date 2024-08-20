package com.tennisdc.tln.modules.support;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created  on 2015-05-12.
 */
public class FragAppVersion extends Fragment {

    @BindView(R.id.tvCurrentAppVersion)
    TextView tvCurrentAppVersion;

    @BindView(R.id.tvLiveAppVersion)
    TextView tvLiveAppVersion;

    @BindView(R.id.tvUpdateText)
    TextView tvUpdateText;

    @BindView(R.id.btnUpdate)
    Button btnUpdate;


    public static final String BUNDLE_EXTRAS_CURRENT_LIVE_VERSION = "currentLiveVersion";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_app_version, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App.LogFacebookEvent(getActivity(),this.getClass().getName());
        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get arguments data
        if (null != getArguments()) {
            String currentAppVersion = getCurrentAppVersion();
            String currentLiveVersion = getArguments().getString(BUNDLE_EXTRAS_CURRENT_LIVE_VERSION,
                    currentAppVersion);

            tvCurrentAppVersion.setText(String
                    .format(getActivity().getString(R.string.current_app_version_placeholder),
                            getCurrentAppVersion()));
            tvLiveAppVersion.setText(String
                    .format(getActivity().getString(R.string.live_app_version_placeholder),
                            currentLiveVersion));

            if (currentAppVersion.equalsIgnoreCase(currentLiveVersion)) {
                btnUpdate.setVisibility(View.VISIBLE);
                tvUpdateText.setText(getActivity().getString(R.string.your_app_is_up_to_date));
            } else {
                btnUpdate.setVisibility(View.VISIBLE);
                tvUpdateText.setText(getActivity().getString(R.string.new_update_is_available));
            }
        }
    }

    private String getCurrentAppVersion() {

        String currentAppVersion = "";
        try {
            PackageInfo pInfo = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0);
            currentAppVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return currentAppVersion;
    }

    @OnClick(R.id.btnUpdate)
    public void onUpdateButtonClicked() {
        final String appPackageName = getActivity().getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(getActivity().getString(R.string.app_version));
    }

}
