package com.tennisdc.tennisleaguenetwork.modules.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tennisleaguenetwork.ui.DividerItemDecoration;
import com.tennisdc.tennisleaguenetwork.ui.RecyclerAdapter;

import org.parceler.Parcels;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created  on 02-02-2015.
 */
public class FragListSelection extends Fragment {

    private static final String EXTRA_LIST = "object_list";
    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_SUB_TITLE = "sub_title";

    public static final String RESULT_SELECTION = "selection_object";

    public static Intent getInstance(Context context, Parcelable list, String title, String subTitle) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_LIST, list);
        args.putString(EXTRA_TITLE, title);
        args.putString(EXTRA_SUB_TITLE, subTitle);

        return SingleFragmentActivity.getIntent(context, FragListSelection.class, args);
    }

    /* Views*/
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.txtSubTitle)
    TextView mSubTitleTextView;

    /* Data */
    private List<Object> mDataList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_select_from_list, container, false);

        ButterKnife.bind(this, rootView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));

        rootView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                    getActivity().setResult(Activity.RESULT_CANCELED);
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args == null) throw new RuntimeException("Missing Arguments");

        mDataList = Parcels.unwrap(args.getParcelable(EXTRA_LIST));
        String strTitle = args.getString(EXTRA_TITLE);
        String strSubTitle = args.getString(EXTRA_SUB_TITLE);

        if (!TextUtils.isEmpty(strSubTitle)) mSubTitleTextView.setText(strSubTitle);

        if (!TextUtils.isEmpty(strTitle)) getActivity().setTitle(strTitle);

        mRecyclerView.setAdapter(new RecyclerAdapter<Object, ObjectViewHolder>(mDataList) {

            @Override
            public ObjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_selection_list_item, parent, false);
                return new ObjectViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ObjectViewHolder holder, int position) {
                holder.bindData(getItem(position));
            }

        });
    }

    class ObjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(android.R.id.text1)
        TextView text1;

        public ObjectViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void bindData(Object item) {
            text1.setText(item.toString());
        }

        @Override
        public void onClick(View v) {
            Intent data = new Intent();

            data.putExtra(RESULT_SELECTION, Parcels.wrap(mDataList.get(getPosition())));

            getActivity().setResult(Activity.RESULT_OK, data);

            getActivity().finish();
        }
    }
}
