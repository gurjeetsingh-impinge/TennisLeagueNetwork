package com.tennisdc.tennisleaguenetwork.modules.poty;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.common.GsonRealmExclusionStrategy;
import com.tennisdc.tennisleaguenetwork.model.potyresponse.PLAYERSOFTHEYEARItem;
import com.tennisdc.tennisleaguenetwork.model.potyresponse.SUMMARYItem;
import com.tennisdc.tennisleaguenetwork.modules.DateYearPicker.YearPickerDialog;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link FragPlayersOfTheYearHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragPlayersOfTheYearHome extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    int mYear;

    @BindView(R.id.pager)
    ViewPager mPager;

    @BindView(R.id.tabs)
    PagerSlidingTabStrip mPagerSlidingTabStrip;

    public FragPlayersOfTheYearHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragPlayersOfTheYearHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragPlayersOfTheYearHome newInstance(String param1, String param2) {
        FragPlayersOfTheYearHome fragment = new FragPlayersOfTheYearHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mYear = Calendar.getInstance().get(Calendar.YEAR);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.frag_players_of_the_year_details, container, false);
        ButterKnife.bind(this, view);
        addListnerToSlider();
        return view;
    }

    private void addListnerToSlider() {
        mPagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getPotyApiCall();

    }

    private void getPotyApiCall() {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null,
                "Please wait...", true, false);

        Request potyRequest = WSHandle.poty.getPotyRequest(App.sOAuth, mYear,
                new VolleyHelper.IRequestListener<JSONObject, String>() {
                    @Override
                    public void onFailureResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccessResponse(JSONObject response) {
                        try {

                            /*if(response.getString("responseCode").equals("404"))
                                progressDialog.dismiss();*/
                            progressDialog.dismiss();

                            Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();
                            final List<PLAYERSOFTHEYEARItem> playersoftheyearItemsList = gson.fromJson(response.getString("PLAYERS OF THE YEAR"), new TypeToken<List<PLAYERSOFTHEYEARItem>>() {}.getType());
                            final List<SUMMARYItem> summaryItemsList = gson.fromJson(response.getString("SUMMARY"), new TypeToken<List<SUMMARYItem>>() {}.getType());
                            final String tab1 = response.getString("TAB1");
                            final String tab2 = response.getString("TAB2");

                            mPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
                                @Override
                                public Fragment getItem(int position) {
                                    switch (position) {
                                        case 0:
                                            return FragPlayersOfTheYear.newInstance(playersoftheyearItemsList);
                                        case 1:
                                            return FragSummary.newInstance(summaryItemsList);
                                    }
                                    return null;
                                }

                                @Override
                                public int getCount() {
                                    return 2;
                                }

                                @Override
                                public CharSequence getPageTitle(int position) {
                                    switch (position) {
                                        default:
                                        case 0:
                                            return tab1;

                                        case 1:
                                            return tab2;
                                    }
                                }

                            });

                            mPagerSlidingTabStrip.setViewPager(mPager);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    }
                });
        VolleyHelper.getInstance(getActivity()).addToRequestQueue(potyRequest);
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(getString(R.string.poty_title) + " " + mYear);
        (((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.poty_calendar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_poty_calendar:
                new YearPickerDialog(FragPlayersOfTheYearHome.this.getContext(), new YearPickerDialog.OnDateSetListener() {
                    @Override
                    public void onYearSet(int year) {
                        mYear = year;
                        getActivity().setTitle(getString(R.string.poty_title) + " " + mYear);
                        getPotyApiCall();
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
