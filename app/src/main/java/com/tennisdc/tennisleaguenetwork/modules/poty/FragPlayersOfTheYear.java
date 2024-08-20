package com.tennisdc.tennisleaguenetwork.modules.poty;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.tennisdc.tln.R;
import androidx.appcompat.app.AppCompatActivity;
import com.tennisdc.tennisleaguenetwork.model.potyresponse.PLAYERSOFTHEYEARItem;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.ui.RecyclerAdapter;

import org.parceler.Parcels;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import androidx.core.content.ContextCompat;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragPlayersOfTheYear.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragPlayersOfTheYear#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragPlayersOfTheYear extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXTRA_LIST = "object_list";

    private List<PLAYERSOFTHEYEARItem> mPlayersoftheyearItemList;

    /* Views */
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private OnFragmentInteractionListener mListener;
    private View view;
    private RecyclerAdapter adapter;

    private Drawable[] potyImages = new Drawable[20];

    public FragPlayersOfTheYear() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param list Parameter 1.
     * @return A new instance of fragment FragPlayersOfTheYear.
     */
    public static FragPlayersOfTheYear newInstance(List<PLAYERSOFTHEYEARItem> list) {
        FragPlayersOfTheYear fragment = new FragPlayersOfTheYear();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_LIST, Parcels.wrap(list));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlayersoftheyearItemList = Parcels.unwrap(getArguments().getParcelable(EXTRA_LIST));
        }

        potyImages[0] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_1);
        potyImages[1] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_2);
        potyImages[2] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_3);
        potyImages[3] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_4);
        potyImages[4] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_5);
        potyImages[5] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_6);
        potyImages[6] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_7);
        potyImages[7] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_8);
        potyImages[8] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_9);
        potyImages[9] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_10);
        potyImages[10] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_11);
        potyImages[11] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_12);
        potyImages[12] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_13);
        potyImages[13] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_14);
        potyImages[14] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_15);
        potyImages[15] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_16);
        potyImages[16] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_17);
        potyImages[17] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_18);
        potyImages[18] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_19);
        potyImages[19] = ContextCompat.getDrawable(getActivity(), R.drawable.poty_20);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.frag_players_of_the_year, container, false);
        ButterKnife.bind(this, view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new RecyclerAdapter<PLAYERSOFTHEYEARItem,PLAYERSOFTHEYEARViewHolder>(mPlayersoftheyearItemList) {
            @Override
            public PLAYERSOFTHEYEARViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.player_of_the_year_details_list_item, null);
                return new PLAYERSOFTHEYEARViewHolder(view);
            }

            @Override
            public void onBindViewHolder(PLAYERSOFTHEYEARViewHolder holder, int position) {
                if(position % 2==0)
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.gray_button_light));
                else
                    holder.itemView.setBackgroundColor(Color.WHITE);
                holder.bindItem(getItem(position), mPlayersoftheyearItemList.size(),position);
            }
        };

        if (adapter != null) {
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            adapter.notifyDataSetChanged();
        }
    }

    class PLAYERSOFTHEYEARViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.poty_image)
        ImageView potyImage;

        @BindView(R.id.profile_image)
        NetworkImageView profileImage;

        @BindView(R.id.player_name)
        TextView playerName;

        @BindView(R.id.champion_cup)
        NetworkImageView championCup;

        @BindView(R.id.player_points)
        TextView playerPoints;

        @BindView(R.id.player_referrals)
        TextView playerReferrals;

        public PLAYERSOFTHEYEARViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {

        }

        public void bindItem(PLAYERSOFTHEYEARItem item, int size, int position) {
            playerName.setText(item.getPlayerName());
            playerPoints.setText(item.getPoints());
            playerReferrals.setText(item.getReferrals());
            potyImage.setImageDrawable(potyImages[position]);

            // Setting profile image of individual person
            profileImage.setDefaultImageResId(android.R.drawable.ic_menu_gallery);
            if (!TextUtils.isEmpty(item.getProfileImage())) {
                Glide.with(getActivity()).load(item.getProfileImage()).placeholder(android.R.drawable.ic_menu_gallery).into(profileImage);
//                profileImage.setImageUrl(item.getProfileImage(), VolleyHelper.getInstance(getActivity()).getImageLoader());
                profileImage.setVisibility(View.VISIBLE);
            }

            //Setting champion cup of individual person earned
            championCup.setDefaultImageResId(android.R.drawable.ic_menu_gallery);
            if (item.getChampionCupImage() != null) {
                Glide.with(getActivity()).load(item.getChampionCupImage()).placeholder(android.R.drawable.ic_menu_gallery).into(championCup);
//                championCup.setImageUrl(item.getChampionCupImage(), VolleyHelper.getInstance(getActivity()).getImageLoader());
                championCup.setVisibility(View.VISIBLE);
            } else {
                championCup.setVisibility(View.GONE);
            }
        }
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
