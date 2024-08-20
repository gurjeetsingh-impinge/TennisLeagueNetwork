package com.tennisdc.tennisleaguenetwork.modules.courts;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.tennisdc.tennisleaguenetwork.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.model.Court;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;
import com.tennisdc.tennisleaguenetwork.ui.DividerItemDecoration;
import com.tennisdc.tennisleaguenetwork.ui.RecyclerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created  on 2015-03-12.
 */
public class FragCourts extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    private static final int VIEW_NO_DATA = 0;
    private static final int VIEW_LIST = 1;
    private static final int VIEW_MAP = 2;
    /* Views */
    @BindView(R.id.edtZipCode)
    EditText mZipCodeEditText;
    @BindView(R.id.imgBtnSearchByZipCode)
    ImageButton mSearchByZipCodeImageButton;
    @BindView(R.id.btnSearchByLocation)
    Button mSearchByLocationButton;
    @BindView(R.id.recyclerView)
    RecyclerView mCourtsRecyclerView;
    @BindView(R.id.btnShowMap)
    Button mShowMapButton;
    @BindView(R.id.btnShowList)
    Button mShowListButton;
    @BindView(R.id.viewFlipper)
    ViewFlipper mFlipper;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    /* Data */
    private GoogleMap mGoogleMap;
    private List<Court> mCourts;

    private int mCurrentView = VIEW_NO_DATA;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_courts_landing, container, false);

        ButterKnife.bind(this, view);

        buildGoogleApiClient();

        SupportMapFragment mSupportMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapFragmentContainer, mSupportMapFragment);
        fragmentTransaction.commit();

        mSupportMapFragment.getMapAsync(this);

        mCourtsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mCourtsRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));

        mShowListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentView(VIEW_LIST);
            }
        });

        mShowMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentView(VIEW_MAP);
            }
        });

        mZipCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mSearchByZipCodeImageButton.performClick();
                    return true;
                }
                return false;
            }
        });

        mSearchByLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.isNetworkOnline(getActivity())) {
                    if (mGoogleApiClient.isConnected()) {
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        if (mLastLocation == null) {
                            Toast.makeText(getActivity(), R.string.alert_msg_current_loc_not_found, Toast.LENGTH_LONG).show();
                        } else {
                            search(mLastLocation);
                        }
                    } else {
                        if (!mGoogleApiClient.isConnecting()) {
                            mGoogleApiClient.connect();
                        }
                        Toast.makeText(getActivity(), R.string.alert_msg_current_loc_not_found, Toast.LENGTH_LONG).show();
                    }
                } else {
                    BaseDialog.MissingInternetErrorDialog.getDialogInstance().show(getChildFragmentManager(), "dlg-alert");
                }
            }
        });

        mSearchByZipCodeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.isNetworkOnline(getActivity())) {
                    String strZipCode = mZipCodeEditText.getText().toString().trim();

                    if (TextUtils.isEmpty(strZipCode)) {
                        mZipCodeEditText.setError("Enter ZipCode");
                    } else if (strZipCode.length() < 5 || !TextUtils.isDigitsOnly(strZipCode)) {
                        mZipCodeEditText.setError("Invalid ZipCode");
                    } else {
                        search(strZipCode);
                    }
                } else {
                    BaseDialog.MissingInternetErrorDialog.getDialogInstance().show(getChildFragmentManager(), "dlg-alert");
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Courts");
        setCurrentView(mCurrentView);
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.reset(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.opt_court, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_court:
                startActivity(SingleFragmentActivity.getIntent(getActivity(), FragCourtAdd.class, null));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCurrentView(int view) {
        mCurrentView = view;
        mFlipper.setDisplayedChild(mCurrentView);
    }

    private void search(String zipCode) {
        App.hideSoftKeyboard(getActivity());
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Fetching courts", getString(R.string.alert_msg_please_wait));
        WSHandle.Courts.searchCourts(zipCode, new VolleyHelper.IRequestListener<List<Court>, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(List<Court> response) {
                progressDialog.dismiss();
                setCourts(response);
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        setCurrentView(mCurrentView);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mCourts == null) {
            if (App.isNetworkOnline(getActivity())) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation == null) {
                    Toast.makeText(getActivity(), R.string.alert_msg_current_loc_not_found, Toast.LENGTH_LONG).show();
                } else {
                    search(mLastLocation);
                }
            } else {
                BaseDialog.MissingInternetErrorDialog.getDialogInstance().show(getChildFragmentManager(), "dlg-alert");
            }
        }
    }

    private void search(Location lastLocation) {
        App.hideSoftKeyboard(getActivity());
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Fetching nearby courts", getString(R.string.alert_msg_please_wait));
        WSHandle.Courts.searchCourts(lastLocation.getLatitude(), lastLocation.getLongitude(), new VolleyHelper.IRequestListener<List<Court>, String>() {
            @Override
            public void onFailureResponse(String response) {
                try {
                    progressDialog.dismiss();
                } catch (Exception ignored) {}
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(List<Court> response) {
                try {
                    progressDialog.dismiss();
                } catch (Exception ignored) {}
                setCourts(response);
            }

            @Override
            public void onError(VolleyError error) {
                try {
                    progressDialog.dismiss();
                } catch (Exception ignored) {}
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setCourts(List<Court> courts) {
        mCourts = courts;
        if (mCourts == null || mCourts.size() == 0) {
            setCurrentView(VIEW_NO_DATA);
        } else {
            refreshList();
            refreshMap();
            if (mCurrentView == VIEW_NO_DATA) setCurrentView(VIEW_MAP);
        }
    }

    private void refreshList() {
        if (mCourts != null) {
            mCourtsRecyclerView.setAdapter(new RecyclerAdapter<Court, CourtsViewHolder>(mCourts) {

                @Override
                public CourtsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_court_list_item, null);
                    return new CourtsViewHolder(view);
                }

                @Override
                public void onBindViewHolder(CourtsViewHolder holder, int position) {
                    holder.bindItem(getItem(position));
                }

            });
        }
    }

    public void refreshMap() {
        if (mGoogleMap != null && isAdded() && mCourts != null) {
            mGoogleMap.clear();

            final LatLngBounds.Builder b = new LatLngBounds.Builder();

            final HashMap<Marker, Long> markerHashMap = new HashMap<>();
            int index = 0;
            for (Court court : mCourts) {
                LatLng latLng = new LatLng(court.getLatitude(), court.getLongitude());
                if (index < 10) b.include(latLng);
                Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker)).title(court.getName()));
                markerHashMap.put(marker, court.getId());
                index++;
            }

            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if (markerHashMap.containsKey(marker)) {
                        Long courtId = markerHashMap.get(marker);
                        LatLng position = marker.getPosition();
                        startActivity(SingleFragmentActivity.getIntent(getActivity(), FragCourtDetails.class, FragCourtDetails.buildArguments(courtId, position.latitude, position.longitude)));
                    }
                }
            });

            mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition arg0) {
                    // Move camera.
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(b.build(), 100));
                    // Remove listener to prevent position reset on camera move.
                    mGoogleMap.setOnCameraChangeListener(null);
                }
            });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                onDialogDismissed();
            }
        };
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getChildFragmentManager(), "errordialog");
    }

    // The rest of this code is all about building the error dialog

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    private double computeMileage(LatLng latLng) {
        if (mLastLocation != null && latLng != null) {
            List<LatLng> latLngs = new ArrayList<>();
            latLngs.add(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            latLngs.add(latLng);
            return SphericalUtil.computeLength(latLngs);
        }
        return -1;
    }

    public class CourtsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /* Views */
        @BindView(R.id.txtCourtName)
        TextView mCourtNameTextView;
        @BindView(R.id.txtMileage)
        TextView mMileageTextView;
        @BindView(R.id.txtCourtAddress)
        TextView mCourtAddressTextView;
        private Court mCourt;

        public CourtsViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void bindItem(Court court) {
            mCourt = court;
            mCourtNameTextView.setText(mCourt.getName());
            mCourtAddressTextView.setText(mCourt.getAddress());
            double mileage = computeMileage(new LatLng(mCourt.getLatitude(), mCourt.getLongitude()));
            mMileageTextView.setText(mileage == -1 ? "" : String.format("%.1f mi", App.convertMeterTo(mileage, App.DistanceUnit.mi)));
        }

        @Override
        public void onClick(View v) {
            startActivity(SingleFragmentActivity.getIntent(getActivity(), FragCourtDetails.class, FragCourtDetails.buildArguments(mCourt.getId(), mCourt.getLatitude(), mCourt.getLongitude())));
        }
    }

    /* A fragment to display an error dialog */
    public static abstract class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode, this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public abstract void onDismiss(DialogInterface dialog);
    }
}
