//package com.tennisdc.tln.modules.tournament;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Parcelable;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.AppCompatActivity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.android.volley.VolleyError;
//import com.tennisdc.tln.R;
//import com.tennisdc.tln.model.UpcomingTournamentModel;
//import com.tennisdc.tln.network.VolleyHelper;
//import com.tennisdc.tln.network.WSHandle;
//
//public class UpcomingTournament extends AppCompatActivity {
//
//
//    public static UpcomingTournament getInstance(Context context, long Id)  {
//        UpcomingTournament fragment = new UpcomingTournament();
//        Bundle args = new Bundle();
//        args.putLong("id", Id);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
////            mParam1 = getArguments().getString(ARG_PARAM1);
////            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//       View mView = inflater.inflate(R.layout.fragment_upcoming_tournament, container, false);
//        fetchTournamentUpcomingList();
//        return mView;
//    }
//
//    private void fetchTournamentUpcomingList() {
//
////        getActivity().setTitle(mCurCompetition.CompetitionName);
//        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);
//
//        WSHandle.Tournament.getTournamentPlayersForUpcoming(7312, new VolleyHelper.IRequestListener<UpcomingTournamentModel, String>() {
//            @Override
//            public void onFailureResponse(String response) {
//                progressDialog.dismiss();
//                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onSuccessResponse(UpcomingTournamentModel response) {
//                getActivity().setTitle(response.getCompetition_name()+": "+
//                        response.getDivision_name()+": "+response.getSkill_name());
//
//                if (response != null && !response.isEnrolled()) {
//
//                } else {
//
//                }
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                progressDialog.dismiss();
//                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//}
