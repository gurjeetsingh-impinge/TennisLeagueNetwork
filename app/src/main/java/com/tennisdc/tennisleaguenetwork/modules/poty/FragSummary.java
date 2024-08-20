package com.tennisdc.tennisleaguenetwork.modules.poty;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tennisdc.tln.R;
import androidx.appcompat.app.AppCompatActivity;
import com.tennisdc.tennisleaguenetwork.model.potyresponse.SUMMARYItem;

import org.parceler.Parcels;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import androidx.core.content.ContextCompat;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragSummary.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragSummary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragSummary extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXTRA_LIST = "object_list";

    private List<SUMMARYItem> summaryItemList;

    private OnFragmentInteractionListener mListener;

    /* Views */
    @BindView(R.id.prize_pool_layout)
    LinearLayout prizePoolLayout;

    @BindView(R.id.prize_pool_title)
    TextView prizePoolTitle;

    @BindView(R.id.level_pool)
    TextView levelPool;

    @BindView(R.id.total_points)
    TextView totalPointsPool;

    @BindView(R.id.league_title_layout)
    LinearLayout leagueTitleLayout;

    @BindView(R.id.league_arrow)
    ImageView leagueArrow;

    @BindView(R.id.league_title)
    TextView leagueTitle;

    @BindView(R.id.league_sub_title)
    TextView leagueSubTitle;

    @BindView(R.id.league_desc_layout)
    LinearLayout leagueDescLayout;

    @BindView(R.id.tourney_desc)
    TextView tourneyDesc;

    @BindView(R.id.league_desc)
    TextView leagueDesc;

    @BindView(R.id.ladder_desc)
    TextView ladderDesc;

    @BindView(R.id.partner_desc)
    TextView partnerDesc;

    @BindView(R.id.league_growth_title_layout)
    LinearLayout leagueGrowthTitleLayout;

    @BindView(R.id.league_growth_arrow)
    ImageView leagueGrowthArrow;

    @BindView(R.id.league_growth_title)
    TextView leagueGrowthTitle;

    @BindView(R.id.league_growth_sub_title)
    TextView leagueGrowthSubTitle;

    @BindView(R.id.league_growth_desc_layout)
    LinearLayout leagueGrowthDescLayout;

    @BindView(R.id.leagues_growth_desc)
    TextView leagueGrowthDesc;

    @BindView(R.id.tourneys_growth_desc)
    TextView tourneysGrowthDesc;

    @BindView(R.id.partner_growth_desc)
    TextView partnerGrowthDesc;

    @BindView(R.id.ideas_growth_desc)
    TextView ideasGrowthDesc;

    @BindView(R.id.blog_growth_desc)
    TextView blogGrowthDesc;

    @BindView(R.id.reviews_growth_desc)
    TextView reviewsGrowthDesc;

    private View view;

    public FragSummary() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param list Parameter 1.
     * @return A new instance of fragment FragSummary.
     */
    // TODO: Rename and change types and number of parameters
    public static FragSummary newInstance(List<SUMMARYItem> list) {
        FragSummary fragment = new FragSummary();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_LIST, Parcels.wrap(list));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            summaryItemList = Parcels.unwrap(getArguments().getParcelable(EXTRA_LIST));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.frag_summary, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prizePoolTitle.setText(summaryItemList.get(0).getTitle());
        StringBuilder builder = new StringBuilder();

        for (int i=0; i<summaryItemList.get(0).getFadedDesc().size(); i++) {
            builder.append(summaryItemList.get(0).getFadedDesc().get(i));
            builder.append("\n");
        }
        levelPool.setText(builder.toString());
        totalPointsPool.setText(summaryItemList.get(0).getDesc().get(0));
        prizePoolTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prizePoolLayout.getVisibility() == View.VISIBLE) {
                    prizePoolLayout.setVisibility(View.GONE);
                    prizePoolTitle.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.arrow_down,0);
                } else {
                    prizePoolLayout.setVisibility(View.VISIBLE);
                    prizePoolTitle.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.arrow_up,0);
                }
            }
        });

        //League desc
        leagueTitle.setText(summaryItemList.get(1).getTitle());
        leagueSubTitle.setText(summaryItemList.get(1).getSubTitle());
        leagueTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (leagueDescLayout.getVisibility() == View.VISIBLE) {
                    leagueDescLayout.setVisibility(View.GONE);
                    leagueArrow.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.arrow_down));
                } else {
                    leagueDescLayout.setVisibility(View.VISIBLE);
                    leagueArrow.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.arrow_up));
                }
            }
        });

        /*StringBuilder leagueDescBuilder = new StringBuilder();
        for (int i=0; i<summaryItemList.get(1).getDesc().size(); i++) {
            leagueDescBuilder.append(summaryItemList.get(1).getDesc().get(i));
            leagueDescBuilder.append("\n");
        }*/

        tourneyDesc.setText(summaryItemList.get(1).getDesc().get(0));
        leagueDesc.setText(summaryItemList.get(1).getDesc().get(1));
        ladderDesc.setText(summaryItemList.get(1).getDesc().get(2));
        partnerDesc.setText(summaryItemList.get(1).getDesc().get(3));

        //League Growth Desc
        leagueGrowthTitle.setText(summaryItemList.get(2).getTitle());
        leagueGrowthSubTitle.setText(summaryItemList.get(2).getSubTitle());
        leagueGrowthTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (leagueGrowthDescLayout.getVisibility() == View.VISIBLE) {
                    leagueGrowthDescLayout.setVisibility(View.GONE);
                    leagueGrowthArrow.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.arrow_down));
                } else {
                    leagueGrowthDescLayout.setVisibility(View.VISIBLE);
                    leagueGrowthArrow.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.arrow_up));
                }
            }
        });

        /*StringBuilder leagueGrowthDescBuilder = new StringBuilder();
        for (int i=0; i<summaryItemList.get(2).getDesc().size(); i++) {
            leagueGrowthDescBuilder.append(summaryItemList.get(2).getDesc().get(i));
            leagueGrowthDescBuilder.append("\n");
        }*/
        leagueGrowthDesc.setText(summaryItemList.get(2).getDesc().get(0));
        tourneysGrowthDesc.setText(summaryItemList.get(2).getDesc().get(1));
        partnerGrowthDesc.setText(summaryItemList.get(2).getDesc().get(2));
        ideasGrowthDesc.setText(summaryItemList.get(2).getDesc().get(3));
        blogGrowthDesc.setText(summaryItemList.get(2).getDesc().get(4));
        reviewsGrowthDesc.setText(summaryItemList.get(2).getDesc().get(5));

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
