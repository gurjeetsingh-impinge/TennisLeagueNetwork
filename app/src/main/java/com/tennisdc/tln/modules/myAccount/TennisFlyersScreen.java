package com.tennisdc.tln.modules.myAccount;

import android.app.ProgressDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.PlayerInformationModel;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;

import java.util.ArrayList;
import java.util.List;

public class TennisFlyersScreen extends AppCompatActivity {

    TextView mTxtTitleToolbar;
    TextView mEdtCityFlyerCard;
    TextView mEdtStreetAddressFlyerCard;
    ImageView mImgBack;
    Button btnSubmitFlyerCard;
    Spinner mSpnFlyerCard;
    List<List<String>> mFlyerCardList = new ArrayList<>();
    Prefs.AppData prefs = null;
    PlayerInformationModel.UserDataBean mUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tennis_flyers_screen);
        App.LogFacebookEvent(this,this.getClass().getName());
        prefs = new Prefs.AppData(this);
        Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();

        mUserData = gson.fromJson(prefs.getUserData(), new TypeToken<PlayerInformationModel.UserDataBean>() {
        }.getType());
        mTxtTitleToolbar = (TextView) findViewById(R.id.mTxtTitleToolbar);
        mEdtStreetAddressFlyerCard = (TextView) findViewById(R.id.mEdtStreetAddressFlyerCard);
        mEdtCityFlyerCard = (TextView) findViewById(R.id.mEdtCityFlyerCard);
        mImgBack = (ImageView) findViewById(R.id.mImgBack);
        mSpnFlyerCard = (Spinner) findViewById(R.id.mSpnFlyerCard);
        btnSubmitFlyerCard = (Button) findViewById(R.id.btnSubmitFlyerCard);
        mTxtTitleToolbar.setText(R.string.tennis_flyers);
        mEdtStreetAddressFlyerCard.setText(mUserData.getUser_street_address());
        mEdtCityFlyerCard.setText(mUserData.getUser_city()+", "+mUserData.getUser_state());
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSubmitFlyerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null,
                        0, "Please confirm you want us to mail you 4 flyers at "+
                                mUserData.getUser_street_address()+", "+mUserData.getUser_city()+", "+
                                mUserData.getUser_state());
                dialog.setPositiveButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        submitFlyerCard();

                    }
                });
                dialog.setNegativeButton("No", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
                dialog.show(getSupportFragmentManager(), "dlg-frag");
            }
        });
        mFlyerCardList = gson.fromJson(getIntent().getStringExtra("list"),
                new TypeToken<List<List<String>>>() {
                }.getType());
        mSpnFlyerCard.setAdapter(new ListItemAdapter(mFlyerCardList));
    }

    class ListItemAdapter extends BaseAdapter {

        List<List<String>> mList;

        public ListItemAdapter(List<List<String>> mList) {
            this.mList = mList;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null) {
                listItem = LayoutInflater.from(TennisFlyersScreen.this).inflate(R.layout.view_spinner_item, parent, false);
            }
            TextView mTxtData = (TextView) listItem.findViewById(android.R.id.text1);
            mTxtData.setText(mList.get(position).get(0));
            return listItem;
        }
    }
    void submitFlyerCard() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Please wait...");
        WSHandle.Profile.updateFlerCard(((List<String>) mSpnFlyerCard.getSelectedItem()).get(1),
                mEdtStreetAddressFlyerCard.getText().toString() + ", " +
                        mEdtCityFlyerCard.getText().toString(), new VolleyHelper.IRequestListener<String, String>() {
                    @Override
                    public void onFailureResponse(String response) {
                        progressDialog.dismiss();
                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                        dialog.show(getSupportFragmentManager(), "dlg-frag");
                    }

                    @Override
                    public void onSuccessResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(TennisFlyersScreen.this, response, Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(TennisFlyersScreen.this, "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
