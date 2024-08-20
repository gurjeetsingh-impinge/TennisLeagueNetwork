package com.tennisdc.tln.modules.courts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.model.Court;
import com.tennisdc.tennisleaguenetwork.model.CourtRegion;
import com.tennisdc.tennisleaguenetwork.model.Location;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.BackHandledFragment;
import com.tennisdc.tln.ui.CustomSpinnerWithErrorAdapter;
import com.tennisdc.tln.ui.NumberPickerHorizontal;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created  on 2015-03-14.
 */
public class FragCourtAdd extends BackHandledFragment {

    private static final int STEP_ONE = 1;
    private static final int STEP_TWO = 2;
    private static final int STEP_THREE = 3;

    private int mCurrentStep = STEP_ONE;

    @BindView(R.id.viewFlipper)
    ViewFlipper mViewFlipper;

    @BindView(R.id.edtCourtName)
    EditText mNameEditText;

    @BindView(R.id.spnrLocations)
    Spinner mLocationSpinner;

    @BindView(R.id.spnrCourtRegion)
    Spinner mCourtRegionSpinner;

    @BindViews({R.id.pickerTotalCourts, R.id.pickerClayCourts, R.id.pickerIndoorCourts, R.id.pickerLightedCourts, R.id.pickerCourtFee})
    List<NumberPickerHorizontal> mCourtsNumberPickerHorizontalList;

    @BindViews({R.id.chkBxIsClub, R.id.chkBxIsTennisStore, R.id.chkBxIsRacquetStringer, R.id.chkBxHasHittingWalls, R.id.chkBxHasBathroom, R.id.chkBxHasWater, R.id.chkBxHasFee, R.id.chkBxRestricted})
    List<CheckBox> mCheckBoxes;

    @BindView(R.id.edtPhone)
    EditText mPhoneEditText;

    @BindView(R.id.edtAddress)
    EditText mAddressEditText;

    @BindView(R.id.edtZipCode)
    EditText mZipCodeEditText;

    @BindView(R.id.edtCity)
    EditText mCityEditText;

    @BindView(R.id.edtState)
    EditText mStateEditText;

    @BindView(R.id.edtNotes)
    EditText mNotesEditText;

    @BindView(R.id.edtWebsite)
    EditText mWebsiteEditText;

    private Court mCourt = new Court();
    private List<Location> mLocations;
    private Realm mRealm;
    private Unbinder unbinder;
    @Override
    public String getTagText() {
        return FragCourtAdd.class.getSimpleName();
    }

    @Override
    public boolean onBackPressed() {
        return moveToPrevious();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_court_add, container, false);
        App.LogFacebookEvent(getActivity(),this.getClass().getName());

     //   ButterKnife.bind(this, view);
        unbinder = ButterKnife.bind(this, view);
        mLocations = mRealm.allObjects(Location.class);

        mLocationSpinner.setAdapter(new CustomSpinnerWithErrorAdapter.LocationsAdapter(getActivity(), mLocations));

        mLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RealmList<CourtRegion> courtRegions = mLocations.get(position).getCourtRegions();
                if (courtRegions == null) {
                    mCourtRegionSpinner.setAdapter(null);
                    mCourtRegionSpinner.setEnabled(false);
                } else {
                    mCourtRegionSpinner.setEnabled(true);
                    mCourtRegionSpinner.setAdapter(new CustomSpinnerWithErrorAdapter.CourtRegionsAdapter(getActivity(), courtRegions));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mLocationSpinner.setSelection(0);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.opt_new_item, menu);

        if (mCurrentStep != STEP_THREE) {
            menu.findItem(R.id.action_done).setVisible(false);
        } else {
            menu.findItem(R.id.action_next).setVisible(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("New Court");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mRealm = Realm.getInstance(activity);
    }

    @Override
    public void onDetach() {
        mRealm.close();
        mRealm = null;
        super.onDetach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
            case R.id.action_next:
                moveToNextStep();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private boolean moveToPrevious() {
        if (mCurrentStep == STEP_ONE) return false;
        mCurrentStep--;
        changeView(mCurrentStep);
        return true;
    }

    private void submit() {
        if(App.isNetworkOnline(getActivity())) {
            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
            WSHandle.Courts.createCourts(mCourt, new VolleyHelper.IRequestListener<Court, String>() {
                @Override
                public void onFailureResponse(String response) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccessResponse(Court response) {
                    progressDialog.dismiss();
                    if (response != null) {
                        BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, "Court \"" + response.getName() + "\" has been added successfully");
                        dialog.setPositiveButton("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().finish();
                            }
                        });
                        dialog.show(getChildFragmentManager(), "dlg-alert");
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            BaseDialog.MissingInternetErrorDialog
                    .getDialogInstance()
                    .show(getChildFragmentManager(), "dlg-alert");
        }
    }

    private void changeView(int step) {
        mViewFlipper.setDisplayedChild(step - 1);
        getActivity().supportInvalidateOptionsMenu();
        App.hideSoftKeyboard(getActivity());
    }

    private void moveToNextStep() {
        switch (mCurrentStep) {
            case STEP_ONE:
                if (!validateStepOne()) return;
                break;

            case STEP_TWO:
                if (!validateStepTwo()) return;
                break;

            case STEP_THREE:
                if (!validateStepThree()) return;
                break;
        }

        if ((mCurrentStep + 1) <= STEP_THREE) {
            mCurrentStep++;
            changeView(mCurrentStep);
        } else {
            //TODO submit
            submit();
        }
    }

    private boolean validateStepOne() {
        boolean isSuccess = true;

        String strName = mNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(strName)) {
            mNameEditText.setError("Please provide mCourt name");
            mNameEditText.requestFocus();
            isSuccess = false;
        }

        if (isSuccess) {
            mCourt.setName(strName);
            mCourt.setLocationId(((Location) mLocationSpinner.getSelectedItem()).getId());
            if (mCourtRegionSpinner.isEnabled())
                mCourt.setRegionId(((CourtRegion) mCourtRegionSpinner.getSelectedItem()).getId());
            else mCourt.setRegionId(0);
        }

        return isSuccess;
    }

    private boolean validateStepTwo() {
        boolean isSuccess = true;

        if (isSuccess) {
            for (NumberPickerHorizontal picker : mCourtsNumberPickerHorizontalList) {
                switch (picker.getId()) {
                    case R.id.pickerTotalCourts:
                        mCourt.setTotalCourts(picker.getValue());
                        break;
                    case R.id.pickerClayCourts:
                        mCourt.setClayCourts(picker.getValue());
                        break;
                    case R.id.pickerIndoorCourts:
                        mCourt.setIndoorCourts(picker.getValue());
                        break;
                    case R.id.pickerLightedCourts:
                        mCourt.setLightedCourts(picker.getValue());
                        break;
                    case R.id.pickerCourtFee:
                        mCourt.setFee(picker.getValue());
                        break;
                }
            }

            for (CheckBox checkBox : mCheckBoxes) {
                switch (checkBox.getId()) {
                    case R.id.chkBxRestricted:
                        mCourt.setRestricted(checkBox.isChecked() ? 1 : 0);
                        break;
                    case R.id.chkBxIsClub:
                        mCourt.setIsClub(checkBox.isChecked() ? 1 : 0);
                        break;
                    case R.id.chkBxIsTennisStore:
                        mCourt.setHasStore(checkBox.isChecked() ? 1 : 0);
                        break;
                    case R.id.chkBxIsRacquetStringer:
                        mCourt.setHasStringer(checkBox.isChecked() ? 1 : 0);
                        break;
                    case R.id.chkBxHasHittingWalls:
                        mCourt.setHasHittingWall(checkBox.isChecked() ? 1 : 0);
                        break;
                    case R.id.chkBxHasWater:
                        mCourt.setHasWater(checkBox.isChecked() ? 1 : 0);
                        break;
                    case R.id.chkBxHasFee:
                        mCourt.setHasFee(checkBox.isChecked() ? 1 : 0);
                        break;
                    case R.id.chkBxHasBathroom:
                        mCourt.setHasBathroom(checkBox.isChecked() ? 1 : 0);
                        break;
                }
            }
        }

        return isSuccess;
    }

    private boolean validateStepThree() {
        boolean isSuccess = true;
        View viewForFocus = null;

        String strAddress = mAddressEditText.getText().toString().trim();
        String strCity = mCityEditText.getText().toString().trim();
        String strState = mStateEditText.getText().toString().trim();
        String strZipCode = mZipCodeEditText.getText().toString().trim();

        if (TextUtils.isEmpty(strAddress)) {
            mAddressEditText.setError("Please provide mCourt address");
            viewForFocus = mAddressEditText;
            isSuccess = false;
        }

        if (TextUtils.isEmpty(strState)) {
            mStateEditText.setError("Please provide state");
            if (viewForFocus == null) viewForFocus = mStateEditText;
            isSuccess = false;
        }

        if (TextUtils.isEmpty(strCity)) {
            mCityEditText.setError("Please provide city");
            if (viewForFocus == null) viewForFocus = mCityEditText;
            isSuccess = false;
        }

        if (TextUtils.isEmpty(strZipCode)) {
            mZipCodeEditText.setError("Please provide zip code");
            if (viewForFocus == null) viewForFocus = mZipCodeEditText;
            isSuccess = false;
        }

        if (viewForFocus != null) viewForFocus.requestFocus();

        if (isSuccess) {
            mCourt.setPhone(mPhoneEditText.getText().toString().trim());
            mCourt.setAddress(strAddress);
            mCourt.setCity(strCity);
            mCourt.setState(strState);
            mCourt.setZipCode(strZipCode);
            mCourt.setWebsite(mWebsiteEditText.getText().toString().trim());
            mCourt.setNotes(mNotesEditText.getText().toString().trim());
        }

        return isSuccess;
    }

}
