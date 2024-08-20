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

public class BusinessCardActivity extends AppCompatActivity {

    TextView mTxtTitleToolbar;
    TextView mEdtCityBusinessCard;
    TextView mEdtStreetAddressBusinessCard;
    ImageView mImgBack;
    Button btnSubmitBusinessCard;
    Spinner mSpnBusinessCard;
    List<List<String>> mBusinessCardList = new ArrayList<>();
    Prefs.AppData prefs = null;
    PlayerInformationModel.UserDataBean mUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_card);
        App.LogFacebookEvent(this,this.getClass().getName());
        prefs = new Prefs.AppData(this);
        Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();

        mUserData = gson.fromJson(prefs.getUserData(), new TypeToken<PlayerInformationModel.UserDataBean>() {
        }.getType());
        mTxtTitleToolbar = (TextView) findViewById(R.id.mTxtTitleToolbar);
        mEdtStreetAddressBusinessCard = (TextView) findViewById(R.id.mEdtStreetAddressBusinessCard);
        mEdtCityBusinessCard = (TextView) findViewById(R.id.mEdtCityBusinessCard);
        mImgBack = (ImageView) findViewById(R.id.mImgBack);
        btnSubmitBusinessCard = (Button) findViewById(R.id.btnSubmitBusinessCard);
        mTxtTitleToolbar.setText(R.string.business_card);
        mEdtStreetAddressBusinessCard.setText(mUserData.getUser_street_address());
        mEdtCityBusinessCard.setText(mUserData.getUser_city()+", "+mUserData.getUser_state());
        mSpnBusinessCard = (Spinner) findViewById(R.id.mSpnBusinessCard);
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSubmitBusinessCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null,
                        0, "Please confirm you want us to mail you 10 businesscards at "+
                        mUserData.getUser_street_address()+", "+mUserData.getUser_city()+", "+
                        mUserData.getUser_state());
                dialog.setPositiveButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        submitBusinessCard();

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


        mBusinessCardList = gson.fromJson(getIntent().getStringExtra("list"),
                new TypeToken<List<List<String>>>() {
                }.getType());
        mSpnBusinessCard.setAdapter(new ListItemAdapter(mBusinessCardList));
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
                listItem = LayoutInflater.from(BusinessCardActivity.this).inflate(R.layout.view_spinner_item, parent, false);
            }
            TextView mTxtData = (TextView) listItem.findViewById(R.id.mTxt);
            mTxtData.setText(mList.get(position).get(0));
            return listItem;
        }
    }


    void submitBusinessCard() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Please wait...");
        WSHandle.Profile.updateBusinessCard(((List<String>) mSpnBusinessCard.getSelectedItem()).get(1),
                mEdtStreetAddressBusinessCard.getText().toString() + ", " +
                        mEdtCityBusinessCard.getText().toString(), new VolleyHelper.IRequestListener<String, String>() {
                    @Override
                    public void onFailureResponse(String response) {
                        progressDialog.dismiss();
                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                        dialog.show(getSupportFragmentManager(), "dlg-frag");
                    }

                    @Override
                    public void onSuccessResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(BusinessCardActivity.this, response, Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(BusinessCardActivity.this, "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
