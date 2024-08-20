package com.tennisdc.tln;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.modules.login.FragLogin;

/**
 * Created  on 08-09-2015.
 */
public class AuthActivity extends SingleFragmentActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, AuthActivity.class);
        intent.putExtra(SingleFragmentActivity.EXTRA_FRAGMENT_CLASS, FragLogin.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
//        Fresco.initialize(this);
        if (!TextUtils.isEmpty(new Prefs.AppData(this).getOAuthToken())) {
            startActivity(HomeActivity.getIntent(this));
            finish();
        } else {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_action);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }
}
