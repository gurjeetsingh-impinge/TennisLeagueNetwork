package com.tennisdc.tln.modules.courts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.model.CoomentsBean;
import com.tennisdc.tln.model.CourtDetailModel;
import com.tennisdc.tennisleaguenetwork.model.CourtRegion;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.DividerItemDecoration;
import com.tennisdc.tln.ui.RecyclerAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.Unbinder;
import io.realm.Realm;

/**
 * Created  on 2015-03-14.
 */
public class FragCourtDetails extends Fragment implements OnMapReadyCallback {

    private static final String EXTRA_COURT_ID = "court_id";
    private static final String EXTRA_COURT_LAT = "latitude";
    private static final String EXTRA_COURT_LON = "longitude";

    private Realm mRealm;
    private GoogleMap mGoogleMap;
    private LatLng mCourtLocation;
    private SupportMapFragment mSupportMapFragment;

    public static Bundle buildArguments(long courtId, double lat, double lon) {
        Bundle args = new Bundle();
        args.putLong(EXTRA_COURT_ID, courtId);
        args.putDouble(EXTRA_COURT_LAT, lat);
        args.putDouble(EXTRA_COURT_LON, lon);

        return args;
    }

    /* View */
    @BindViews({R.id.txtRestricted, R.id.txtHasBathroon, R.id.txtHasHittingWalls, R.id.txtHasWaterAccess, R.id.txtRegion, R.id.txtGetDirections, R.id.txtTotalCourts, R.id.txtLightedCourts, R.id.txtIndoorCourts, R.id.txtClayCourts, R.id.txtFee, R.id.txtCourtAddress, R.id.txtCityStateZip, R.id.txtPhone, R.id.txtNotes , R.id.txtMatchesPlayed})
    List<TextView> mTextViews;

    @BindViews({R.id.ratingOverall, R.id.ratingSurface, R.id.ratingLights, R.id.ratingCrowds, R.id.ratingManagement })
    List<RatingBar> mRatingBars;

    @BindView(R.id.recyclerViewComments)
    RecyclerView mCommentRecyclerView;

    /*@BindViews({R.id.chkBxHasBathroom, R.id.chkBxRestricted, R.id.chkBxHasWater, R.id.chkBxHasHittingWalls})
    List<CheckBox> mCheckBoxes;*/

    /* Data */
    private CourtDetailModel mCourt;
    private Unbinder unbinder;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_court_details, container, false);
        App.LogFacebookEvent(getActivity(),this.getClass().getName());


        unbinder= ButterKnife.bind(this, view);
        mSupportMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapFragmentContainer, mSupportMapFragment);
        fragmentTransaction.commit();
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mCommentRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments == null) throw new RuntimeException("Missing Court Id");

        long courtId = arguments.getLong(EXTRA_COURT_ID, 0);
        if (courtId == 0) throw new RuntimeException("Missing Court Id");

        double lat = arguments.getDouble(EXTRA_COURT_LAT);
        double lon = arguments.getDouble(EXTRA_COURT_LON);

        mCourtLocation = new LatLng(lat, lon);

        /*Court court = mRealm.where(Court.class).equalTo("id", courtId).findFirst();

        if (court != null && court.getState() != null) {
            setCourt(court);
        } else*/ if (App.isNetworkOnline(getActivity())) {
            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...");
            WSHandle.Courts.getCourtDetails(courtId, new VolleyHelper.IRequestListener<CourtDetailModel, String>() {

                @Override
                public void onFailureResponse(String response) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccessResponse(CourtDetailModel response) {
                    progressDialog.dismiss();
                    CourtDetailModel court = mRealm.where(CourtDetailModel.class).equalTo("id", response.getId()).findFirst();

                    if (court != null) {
                        mRealm.beginTransaction();
                        mRealm.copyToRealmOrUpdate(response);
                        mRealm.commitTransaction();
                    }

                    setCourt(response);
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            BaseDialog.MissingInternetErrorDialog.getDialogInstance().show(getChildFragmentManager(), "dlg-error");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCourt != null) getActivity().setTitle(mCourt.getName());
    }

    public void refreshMap() {
        if (mGoogleMap != null && isAdded() && mCourtLocation != null && mCourt != null) {
            mGoogleMap.clear();

            final LatLngBounds.Builder b = new LatLngBounds.Builder();

            b.include(mCourtLocation);
            mGoogleMap.addMarker(new MarkerOptions().position(mCourtLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker)).title(mCourt.getName()))
                    .showInfoWindow();
//                    .setTitle(mCourt.getName());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCourtLocation, 11.0f));
            /*mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition arg0) {
                    // Move camera.
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCourtLocation, 11.0f));

                    // Remove listener to prevent position reset on camera move.
                    mGoogleMap.setOnCameraChangeListener(null);
                }
            });*/
        }
    }

    private void setCourt(CourtDetailModel court) {
        mCourt = court;
        View view = getView();
        if (mCourt != null && view != null) {
            getActivity().setTitle(mCourt.getName());

            /* Text Views */
            for (TextView textView : mTextViews) {
                switch (textView.getId()) {
                    case R.id.txtGetDirections:
                        if (mCourtLocation != null) {
                            //refreshMap();
                            view.findViewById(R.id.mapFragmentContainer).setVisibility(View.VISIBLE);
                            mSupportMapFragment.getMapAsync(this);
                            textView.setVisibility(View.VISIBLE);
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("geo:0,0?q=" + mCourtLocation.latitude + "," + mCourtLocation.longitude + "(" + mCourt.getName() + ")"));
                                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                        break;

                    case R.id.txtRegion:
                        CourtRegion courtRegion = mRealm.where(CourtRegion.class).equalTo("id", mCourt.getCourt_region_id()).findFirst();
                        if (courtRegion != null) {
                            textView.setText(courtRegion.getName());
                        }
                        break;

                    /* Courts */
                    case R.id.txtTotalCourts:
                        if (mCourt.getHas_store() == 0) {
                            view.findViewById(R.id.vwTotalCourts).setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(mCourt.getTotal_courts()));
                        }
                        break;

                    case R.id.txtIndoorCourts:
                        if (mCourt.getHas_store() == 0 && mCourt.getIndoor_courts() > 0) {
                            view.findViewById(R.id.vwIndoorCourts).setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(mCourt.getIndoor_courts() ));
                        }
                        break;

                    case R.id.txtClayCourts:
                        if (mCourt.getHas_store() == 0 && mCourt.getClay_courts() > 0) {
                            view.findViewById(R.id.vwClayCourts).setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(mCourt.getClay_courts()));
                        }
                        break;

                    case R.id.txtLightedCourts:
                        if (mCourt.getHas_store() == 0) {
                            view.findViewById(R.id.vwLightedCourts).setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(mCourt.getLighted_courts()));
                        }
                        break;

                    case R.id.txtFee:
                        textView.setText(mCourt.getHas_store() == 0 ? "No" : ("Yes, " + (mCourt.getFee() > 0 ? String.valueOf(mCourt.getFee()) : "Not Sure")));
                        break;

                    case R.id.txtMatchesPlayed:
                        textView.setText(String.valueOf(mCourt.getMatches_played_here()));
                        break;

                    /* Contact details */
                    case R.id.txtCourtAddress:
                        textView.setText(mCourt.getAddress());
                        break;

                    case R.id.txtCityStateZip:
                        textView.setText(mCourt.getCity() + ", " + mCourt.getState() + ", " + mCourt.getZip_code());
                        break;

                    case R.id.txtPhone:
                        if (!TextUtils.isEmpty(mCourt.getPhone_number())) {
                            view.findViewById(R.id.vwPhone).setVisibility(View.VISIBLE);
                            textView.setText(mCourt.getPhone_number());
                        }
                        break;

                    case R.id.txtNotes:
                        if (!TextUtils.isEmpty(mCourt.getNotes())) {
                            view.findViewById(R.id.vwNotes).setVisibility(View.VISIBLE);
                            textView.setText(mCourt.getNotes());
                        }
                        break;

                    case R.id.txtRestricted:
                        if (mCourt.getRestricted() == 1) {
                            view.findViewById(R.id.vwRestricted).setVisibility(View.VISIBLE);
                        }
                        break;

                    case R.id.txtHasWaterAccess:
                        textView.setText(mCourt.getWater_access() == 1 ? "Yes" : "No");
                        break;

                    case R.id.txtHasHittingWalls:
                        textView.setText(mCourt.getHas_hitting_wall() == 1 ? "Yes" : "No");
                        break;

                    case R.id.txtHasBathroon:
                        textView.setText(mCourt.getBathroom_available() == 1 ? "Yes" : "No");
                        break;
                }
            }

            /* RatingBars */
            if (mCourt.getOverall_rating() > 0) {
                view.findViewById(R.id.vwRatings).setVisibility(View.VISIBLE);

                if (!TextUtils.isEmpty(mCourt.getNotes())) {
                    TextView ratingNotesView = (TextView) view.findViewById(R.id.rating_notes);
                    ratingNotesView.setVisibility(View.VISIBLE);
                    ratingNotesView.setText(mCourt.getNotes());
                }

                for (RatingBar ratingBar : mRatingBars) {
                    switch (ratingBar.getId()) {
                        case R.id.ratingOverall:
                            ratingBar.setRating((float) mCourt.getOverall_rating());
                            break;

                        case R.id.ratingCrowds:
                            ratingBar.setRating((float) mCourt.getRating_crowds());
                            break;

                        case R.id.ratingSurface:
                            ratingBar.setRating((float) mCourt.getSurface_rating());
                            break;

                        case R.id.ratingLights:
                            if ((float)mCourt.getRating_lights() > 0) {
                                view.findViewById(R.id.vwRatingLights).setVisibility(View.VISIBLE);
                                ratingBar.setRating((float)mCourt.getRating_lights());
                            }
                            break;

                        case R.id.vwRatingManagement:
                            if (mCourt.getIs_club() == 1) {
                                view.findViewById(R.id.vwRatingManagement).setVisibility(View.VISIBLE);
                                ratingBar.setRating((float)mCourt.getRating_management());
                            }
                            break;
                    }
                }
            }

            if(mCourt.getComments().size() > 0){
                view.findViewById(R.id.vwComments).setVisibility(View.VISIBLE);
                mCommentRecyclerView.setAdapter(new RecyclerAdapter<CoomentsBean, CommentViewHolder>(mCourt.getComments()) {

                    @Override
                    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_court_comment_list_item, null);
                        return new CommentViewHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(CommentViewHolder holder, int position) {
                        holder.bindItem(getItem(position));
                    }

                });
            }
        } else {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
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
    public void onDestroyView() {
        super.onDestroyView();
      //  ButterKnife.bind(this);
        unbinder.unbind();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        refreshMap();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /* Views */
        @BindView(R.id.txtCommentName)
        TextView mTxtCommentName;
        @BindView(R.id.txtCommentDate)
        TextView mTxtCommentDate;
        @BindView(R.id.txtCommentDescription)
        TextView mTxtCommentDescription;
        private CoomentsBean mComment;

        public CommentViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void bindItem(CoomentsBean mComment) {
            this.mComment = mComment;
            mTxtCommentName.setText(this.mComment.getPlayer_name());
            mTxtCommentDate.setText(this.mComment.getComment_date());
            if(!mComment.getContent().trim().isEmpty()){
                mTxtCommentDescription.setText(this.mComment.getContent());
            }
        }

        @Override
        public void onClick(View v) {
        }
    }
}
