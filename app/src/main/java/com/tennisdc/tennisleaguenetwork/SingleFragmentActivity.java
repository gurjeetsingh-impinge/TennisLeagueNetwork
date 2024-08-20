package com.tennisdc.tennisleaguenetwork;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.tennisdc.tln.FragHome;
import com.tennisdc.tln.R;

import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.common.Prefs;
import com.tennisdc.tennisleaguenetwork.modules.DlgCommunicationSettings;
import com.tennisdc.tennisleaguenetwork.modules.buy.FragProgramList;
import com.tennisdc.tennisleaguenetwork.modules.login.FragLogin;

public class SingleFragmentActivity extends HelperActivity {

    private final static String TAG = SingleFragmentActivity.class.getSimpleName();

    /* required for singleTask activities, so that fragment can be reloaded again */
    private final static String EXTRA_RELOAD_FRAG = "reload_fragment";

    protected final static String EXTRA_FRAGMENT_CLASS = "fragment_class";
    protected final static String EXTRA_FRAGMENT_ARGS = "fragment_params";

    private final static String EXTRA_ERROR = "error";

    public static Intent getIntent(Context context, Class fragmentClass, Bundle fragmentArgs) {
        Intent intent = new Intent(context, SingleFragmentActivity.class);

        intent.putExtra(EXTRA_FRAGMENT_CLASS, fragmentClass);
        if (fragmentArgs != null) {
            intent.putExtra(EXTRA_FRAGMENT_ARGS, fragmentArgs);
        }

        return intent;
    }

    public static Intent getIntent(Context context, Class fragmentClass, Bundle fragmentArgs, boolean reloadFrag) {
        Intent intent = getIntent(context, fragmentClass, fragmentArgs);

        intent.putExtra(EXTRA_RELOAD_FRAG, reloadFrag);

        return intent;
    }

    public static Intent getIntent(Context context, Class fragmentClass, Bundle fragmentArgs, String error) {
        Intent intent = new Intent(context, SingleFragmentActivity.class);

        intent.putExtra(EXTRA_FRAGMENT_CLASS, fragmentClass);
        if (fragmentArgs != null) {
            intent.putExtra(EXTRA_FRAGMENT_ARGS, fragmentArgs);
        }
        if (!TextUtils.isEmpty(error)) {
            intent.putExtra(EXTRA_ERROR, error);
        }
        return intent;
    }

    private boolean mReloadFrag;

    private String mError;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(getContentViewId());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) mReloadFrag = intent.getBooleanExtra(EXTRA_RELOAD_FRAG, false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!TextUtils.isEmpty(App.sOAuth)) setTitle(new Prefs.AppData(this).getDomainName());
        else  {
            String str = "  Tennis League Network, llc";
            setTitle(str);
        }

        FragmentManager fManager = getSupportFragmentManager();

        Fragment fragment = fManager.findFragmentById(R.id.fragmentContainer);

        /**
         * in case if it is already existing, and wants to reload the
         * fragments again, specially for singleTask Activities
         */
        if (fragment != null) {
            if (mReloadFrag) {
                FragmentTransaction ft = fManager.beginTransaction();
                ft.remove(fragment);
                ft.commit();
                fragment = null;
            }
        }
        mReloadFrag = false;

        if (fragment == null) {
            fManager.beginTransaction().add(R.id.fragmentContainer, createFragment()).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (findViewById(R.id.fragmentDetailContainer) != null) {
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.fragmentDetailContainer);
            if (fragment != null) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
               // ft.commit();
                ft.commitAllowingStateLoss();
            }
        }
     //   super.onSaveInstanceState(outState);
    }

    public int getContentViewId() {
        return R.layout.activity_fragment;
    }

    public Fragment createFragment() {
        Intent intent = getIntent();
        if (intent != null) {
            Class fragmentClass = (Class) intent.getSerializableExtra(EXTRA_FRAGMENT_CLASS);
            Bundle fragmentArgs = intent.getBundleExtra(EXTRA_FRAGMENT_ARGS);
            mError = intent.getStringExtra(EXTRA_ERROR);

            if (fragmentClass != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                try {
                    Fragment fragment = (Fragment) fragmentClass.newInstance();
                    if (fragmentArgs != null) fragment.setArguments(fragmentArgs);
                    return fragment;
                } catch (InstantiationException | IllegalAccessException e) {
                    Log.e(TAG, "ERROR:" + e.getMessage());
                }
            } else {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setIcon(R.drawable.ic_action);
                if (TextUtils.isEmpty(new Prefs.AppData(this).getOAuthToken())) {
                    return new FragLogin();
                } else {
                    return new FragHome();
                }
            }
        }
        return new FragLogin();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*public static class AuthActivity extends SingleFragmentActivity {

        public static Intent getIntent(Context context) {
            Intent intent = new Intent(context, AuthActivity.class);
            intent.putExtra(EXTRA_FRAGMENT_CLASS, FragLogin.class);
            return intent;
        }

        @Override
        protected void onCreate(Bundle arg0) {
            super.onCreate(arg0);
            if (!TextUtils.isEmpty(new Prefs.AppData(this).getOAuthToken())) {
                startActivity(HomeActivity.getIntent(this));
                finish();
            } else {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setIcon(R.mipmap.ic_launcher);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }*/

    public static class HomeActivity extends SingleFragmentActivity {

        public static Intent getIntent(Context context) {
            Intent intent = new Intent(context, HomeActivity.class);
            intent.putExtra(EXTRA_FRAGMENT_CLASS, FragHome.class);
            return intent;
        }

        @Override
        protected void onCreate(Bundle arg0) {
            super.onCreate(arg0);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_action);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    public static class BuyActivity extends SingleFragmentActivity {

        public static Intent getIntent(Context context) {
            Intent intent = new Intent(context, BuyActivity.class);
            intent.putExtra(EXTRA_RELOAD_FRAG, true);
            intent.putExtra(EXTRA_FRAGMENT_CLASS, FragProgramList.class);
            return intent;
        }

        @Override
        public void onBackPressed() {
            startActivity(HomeActivity.getIntent(this));
        }
    }

    public static class MyProfileActivity extends SingleFragmentActivity {

        public static Intent getIntent(Context context) {
            Intent intent = new Intent(context, MyProfileActivity.class);
            intent.putExtra(EXTRA_RELOAD_FRAG, true);
            return intent;
        }

        @Override
        public void onBackPressed() {
            startActivity(MyProfileActivity.getIntent(this));
        }
    }
}
