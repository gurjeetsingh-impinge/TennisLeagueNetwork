package com.tennisdc.tln.modules.courts;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tennisleaguenetwork.model.Court;
import com.tennisdc.tln.model.CourtMainModel;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.DividerItemDecoration;
import com.tennisdc.tln.ui.RecyclerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.Unbinder;

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
    //    String[] arr = { "Paries,France", "PA,United States","Parana,Brazil",
//            "Padua,Italy", "Pasadena,CA,United States"};
    /* Views */
//    @BindView(R.id.mSearchAutoComplete)
//    AppCompatAutoCompleteTextView mSearchAutoComplete;
    @BindView(R.id.recyclerViewCourtName)
    RecyclerView mRVCourtName;
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
    /*@BindView(R.id.mapCourt)
    MapView mMapView;*/
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    /* Data */
    private GoogleMap mGoogleMap;
    private List<Court> mCourts;
    private ArrayList<HashMap<String, String>> mCourtNameList = new ArrayList<>();
    SearchCourtNameAdapter mCourtNameAdapter = null;

    private int mCurrentView = VIEW_NO_DATA;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    Prefs.AppData prefs;
    Unbinder unbinder;
//    private List<Court> mCourtsRealm;
//    private Realm mRealm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_courts_landing, container, false);
        App.LogFacebookEvent(getActivity(), this.getClass().getName());

        unbinder = ButterKnife.bind(this, view);

        buildGoogleApiClient();

        prefs = new Prefs.AppData(getActivity());
        HashMap<String, String> mHashMap = new HashMap<>();
        mHashMap.put("title", prefs.getHomeCourt());
        mHashMap.put("type", "api");
        mCourtNameList.add(mHashMap);
        try {
                /*mMapView.onCreate(savedInstanceState);
                mMapView.onResume();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
//        mMapView.getMapAsync(this);
        initializeMap();
        mRVCourtName.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRVCourtName.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST,
                R.drawable.divider_white));

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
        mZipCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    mRVCourtName.setVisibility(View.GONE);
                } else {
                    mRVCourtName.setVisibility(View.VISIBLE);
                }
                if (mCourtNameAdapter != null) {
                    ArrayList<HashMap<String, String>> mCourtNameListTemp = new ArrayList<>();

                    if (mCourtNameList.size() > 0) {
                        mCourtNameListTemp.add(mCourtNameList.get(0));
                    }
                    for (int i = 1; i < mCourtNameList.size(); i++) {
                        if (mCourtNameList.get(i).get("title").toLowerCase().contains(s.toString().toLowerCase()))
                            mCourtNameListTemp.add(mCourtNameList.get(i));
                    }
                    mCourtNameAdapter.updateList(mCourtNameListTemp);
                } else {
                    mCourtNameAdapter = new SearchCourtNameAdapter(mCourtNameList);
                    mRVCourtName.setAdapter(mCourtNameAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                    } else /*if (strZipCode.length() < 5 || !TextUtils.isDigitsOnly(strZipCode)) {
                        mZipCodeEditText.setError("Invalid ZipCode");
                    } else*/ {
                        search(strZipCode);
                    }
                } else {
                    BaseDialog.MissingInternetErrorDialog.getDialogInstance().show(getChildFragmentManager(), "dlg-alert");
                }
            }
        });
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>
//                (getActivity(),android.R.layout.select_dialog_item, arr);
//        mSearchAutoComplete.setThreshold(2);
//        mSearchAutoComplete.setAdapter(adapter);
        getCourtNames();
        return view;
    }

    private void initializeMap() {
        // Get a handle to the fragment and register the callback.
        FragmentActivity activity = getActivity();
        if (activity != null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null)
                mapFragment.getMapAsync(this);
        }
    }

    //fgfd
    void getCourtNames() {

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Fetching courts", getString(R.string.alert_msg_please_wait));
        WSHandle.Courts.getCourtNames(new VolleyHelper.IRequestListener<ArrayList<String>, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(ArrayList<String> response) {
                progressDialog.dismiss();
                for (int i = 0; i < response.size(); i++) {
                    HashMap<String, String> mHashMap = new HashMap<>();
                    mHashMap.put("title", response.get(i));
                    mHashMap.put("type", "api");
//                    if (!mCourtNameList.contains(mHashMap)) {
                    mHashMap.put("type", "realm");
                    mCourtNameList.add(mHashMap);
//                    }
                }
//                setCourts(response);
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }


    class SearchCourtNameAdapter extends RecyclerView.Adapter<SearchCourtNameAdapter.MyViewHolder> {

        ArrayList<HashMap<String, String>> mList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView mTxtData;
            public TextView mTxtHeader;
            RelativeLayout mLayoutMain;

            public MyViewHolder(View view) {
                super(view);
                mTxtHeader = (TextView) view.findViewById(R.id.mTxtMyStats);
                mTxtData = (TextView) view.findViewById(R.id.mTxtMyStatsValue);
                mLayoutMain = (RelativeLayout) view.findViewById(R.id.mRlMyStats);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_my_referrals_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            String mCourtName = mList.get(position).get("title");
            holder.mTxtData.setText(mCourtName);
            if (position > 0) {
                if (mList.get(position - 1).get("type").equalsIgnoreCase(mList.get(position).get("type"))) {
                    holder.mTxtHeader.setVisibility(View.GONE);
                } else {
                    holder.mTxtHeader.setVisibility(View.VISIBLE);
                    holder.mTxtHeader.setText("Suggestion by TennisLeague");
                }
            } else {
                holder.mTxtHeader.setVisibility(View.VISIBLE);
                holder.mTxtHeader.setText("Home Court");
            }
            holder.mTxtData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    search(mCourtName);
                }
            });
        }

        public SearchCourtNameAdapter(ArrayList<HashMap<String, String>> mList) {
            this.mList = mList;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }


        public void updateList(ArrayList<HashMap<String, String>> mListTemp) {
            mList = mListTemp;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Courts");
        setCurrentView(mCurrentView);
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
//        if(mMapView != null){
//            mMapView.onResume();
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        if(mMapView != null){
//            mMapView.onStart();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
//        if(mMapView != null){
//            mMapView.onPause();
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if(mMapView != null){
//            mMapView.onStop();
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //   ButterKnife.reset(this);
        unbinder.unbind();
//        if(mMapView != null){
//            mMapView.onDestroy();
//        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
//        if(mMapView != null){
//            mMapView.onLowMemory();
//        }
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
        WSHandle.Courts.searchCourts(zipCode, new VolleyHelper.IRequestListener<CourtMainModel, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(CourtMainModel response) {
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
        if (checkLocationPermission()) {
            mGoogleMap.setMyLocationEnabled(true);
        } /*else {

        }*/
        setCurrentView(VIEW_MAP);
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
                } catch (Exception ignored) {
                }
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(List<Court> response) {
                try {
                    progressDialog.dismiss();
                } catch (Exception ignored) {
                }
                setCourts(response);
            }

            @Override
            public void onError(VolleyError error) {
                try {
                    progressDialog.dismiss();
                } catch (Exception ignored) {
                }
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setCourts(CourtMainModel courtsResponse) {
        mRVCourtName.setVisibility(View.GONE);
        mCourts = courtsResponse.getCourts_list();
        if (mCourts == null || mCourts.size() == 0) {
            setCurrentView(VIEW_NO_DATA);
        } else {
            refreshList();
            refreshMap(courtsResponse.getParent_lat(), courtsResponse.getParent_long());
//            refreshMap(38.897095, -77.006332);
            if (mCurrentView == VIEW_NO_DATA) setCurrentView(VIEW_MAP);
        }
    }

    private void setCourts(List<Court> courtsResponse) {
        mCourts = courtsResponse;
        if (mCourts == null || mCourts.size() == 0) {
            setCurrentView(VIEW_NO_DATA);
        } else {
            refreshMap(0.0, 0.0);
            refreshList();
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

    public void refreshMap(double lat, double lng) {
        if (mGoogleMap != null && isAdded() && mCourts != null) {
            mGoogleMap.clear();

            final LatLngBounds.Builder b = new LatLngBounds.Builder();

            final HashMap<Marker, Long> markerHashMap = new HashMap<>();
            int index = 0;
            for (Court court : mCourts) {
                LatLng latLng = new LatLng(court.getLatitude(), court.getLongitude());
                if (index < 10) b.include(latLng);
                Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
                        .title(court.getName())/*.snippet(court.getAddress())*/);
                marker.showInfoWindow();
//                Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).
//                        icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker)).title(court.getName())
//                .snippet(court.getAddress()));
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
            LatLng latLng;
            if (lat != 0.0 && lng != 0.0) {
                latLng = new LatLng(lat, lng);
            } else {
                Court court = mCourts.get(mCourts.size() / 2);
                latLng = new LatLng(court.getLatitude(), court.getLongitude());
            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

    /*void showAllMarkers(){
        for (court in mCourtNameList)
        LatLng latLng = new LatLng(court.getLatitude(), court.getLongitude());
        if (index < 10) b.include(latLng);
        Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).
                icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
                .title(court.getName())*//*.snippet(court.getAddress())*//*);
        marker.showInfoWindow();
//                Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).
//                        icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker)).title(court.getName())
//                .snippet(court.getAddress()));
        markerHashMap.put(marker, court.getId());

    }*/

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
            mMileageTextView.setVisibility(View.GONE);
//            double mileage = computeMileage(new LatLng(mCourt.getLatitude(), mCourt.getLongitude()));
//            mMileageTextView.setText(mileage == -1 ? "" : String.format("%.1f mi", App.convertMeterTo(mileage, App.DistanceUnit.mi)));
        }

        @Override
        public void onClick(View v) {
            startActivity(SingleFragmentActivity.getIntent(getActivity(), FragCourtDetails.class, FragCourtDetails.buildArguments(mCourt.getId(), mCourt.getLatitude(), mCourt.getLongitude())));
        }
    }

    /* A fragment to display an error dialog */
    public static abstract class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode, this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public abstract void onDismiss(DialogInterface dialog);
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                        //Request location updates:
//                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
}
