package com.tennisdc.tennisleaguenetwork.modules.buy;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.tennisdc.tennisleaguenetwork.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.common.Prefs;
import com.tennisdc.tennisleaguenetwork.model.Program;
import com.tennisdc.tennisleaguenetwork.network.VolleyHelper;
import com.tennisdc.tennisleaguenetwork.network.WSHandle;
import com.tennisdc.tennisleaguenetwork.ui.CustomSpinnerWithErrorAdapter;
import com.tennisdc.tennisleaguenetwork.ui.RecyclerAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/**
 * Created  on 2015-04-16.
 */
public class FragProgramList extends Fragment {

    /* Views */
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.viewSummary)
    View mSummaryView;

    @BindView(R.id.txtFinalCost)
    TextView mFinalCostTextView;

    @BindView(R.id.btnCheckout)
    Button mCheckoutButton;

    /* Data */
    private List<Program> mProgramList;
    private RecyclerAdapter.RecyclerSelectionHelper<Program> mRecyclerSelectionHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_programs, container, false);

        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));

        mCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray selectedPositions = mRecyclerSelectionHelper.getSelectedPositions();

                if (selectedPositions.size() > 0) {
                    List<Program> selectedPrograms = new ArrayList<>();
                    for (int i = 0; i < selectedPositions.size(); i++)
                        selectedPrograms.add(mProgramList.get(selectedPositions.keyAt(i)));

                    //TODO send selected programs to checkout page
                    startActivity(SingleFragmentActivity.getIntent(getActivity(), FragCheckout.class, FragCheckout.buildArgs(selectedPrograms)));
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        App.sCouponDetails = null;

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "fetching programs...", true, false);
        WSHandle.Buy.getProgramList(new VolleyHelper.IRequestListener<List<Program>, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(List<Program> response) {
                mProgramList = response;

                mRecyclerSelectionHelper = new RecyclerAdapter.RecyclerSelectionHelper<>(mRecyclerView);
                mRecyclerSelectionHelper.setSelectionMode(RecyclerAdapter.RecyclerSelectionHelper.SELECTION_MODE_MULTI);

                mRecyclerView.setAdapter(new RecyclerAdapter<Program, ProgramViewHolder>(mProgramList) {

                    @Override
                    public ProgramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_program_item, null);
                        return new ProgramViewHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(ProgramViewHolder holder, int position) {
                        holder.bindItem(getItem(position));
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }
                });

                mSummaryView.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle("Join Today");
    }

    void updateFinalCost() {

        double finalCost = 0;

        SparseBooleanArray selectedPositions = mRecyclerSelectionHelper.getSelectedPositions();
        if (selectedPositions.size() > 0) {
            mCheckoutButton.setEnabled(true);
            for (int i = 0; i < selectedPositions.size(); i++) {
                Program program = mProgramList.get(selectedPositions.keyAt(i));
                if (program.programPrices.get(program.selectedPriceIndex).priceAfterDiscount == null
                        && program != null && program.programPrices != null
                        && !program.programPrices.isEmpty()) {
                    finalCost += program.programPrices.get(program.selectedPriceIndex).price;
                } else {
                    finalCost += program.programPrices.get(program.selectedPriceIndex).priceAfterDiscount;
                }
            }
        } else {
            mCheckoutButton.setEnabled(false);
        }

        mFinalCostTextView.setText(String.format("$%.2f", finalCost));
    }

    public class ProgramViewHolder extends RecyclerView.ViewHolder implements RecyclerAdapter.RecyclerSelectionHelper.SelectionHelperCallbacks, CompoundButton.OnCheckedChangeListener, View.OnClickListener {

        /* views */
        @BindView(R.id.chkBxProgramName)
        CheckBox mProgramNameCheckBox;

        @BindView(R.id.txtLeagueType)
        TextView mLeagueTypeTextView;

        @BindView(R.id.txtDates)
        TextView mDatesTextView;

        @BindView(R.id.vwPrice)
        View mPriceView;

        @BindView(R.id.spnrPrice)
        Spinner mPriceSpinner;

        @BindView(R.id.txtPrice)
        TextView mPriceTextView;

        @BindView(R.id.txtAlreadyEnrolled)
        TextView mAlreadyEnrolledTextView;

        @BindView(R.id.discount_message)
        TextView mDiscountMessage;

        @BindView(R.id.btnFreeMonth)
        Button mFreeMonthButton;

        /* data */
        Program mProgram;
        private CustomSpinnerWithErrorAdapter mPriceSpinnerAdapter;

        public ProgramViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            mProgramNameCheckBox.setOnCheckedChangeListener(this);
        }

        public void bindItem(Program item) {
            mProgram = item;

            mProgramNameCheckBox.setText(item.name);
            mLeagueTypeTextView.setText(item.type);
            mDatesTextView.setText(item.dates);

            if (mProgram.freeMonth) {
                mPriceView.setVisibility(View.GONE);
                mFreeMonthButton.setVisibility(View.VISIBLE);

                mProgramNameCheckBox.setEnabled(false);

                mFreeMonthButton.setOnClickListener(this);
            } else {
                mPriceView.setVisibility(View.VISIBLE);
                mFreeMonthButton.setVisibility(View.GONE);

                mProgramNameCheckBox.setEnabled(true);
                mProgramNameCheckBox.setChecked(mRecyclerSelectionHelper.isItemChecked(getLayoutPosition()));

                if(mProgram.programPrices.size() > 1) {
                    /**/
                    mPriceSpinnerAdapter = new CustomSpinnerWithErrorAdapter<>(getActivity(), mProgram.programPrices);
                    mPriceSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    mPriceTextView.setVisibility(View.GONE);
                    mPriceSpinner.setVisibility(View.VISIBLE);

                    mPriceSpinner.setAdapter(mPriceSpinnerAdapter);
                    mPriceSpinner.setSelection(mProgram.selectedPriceIndex);
                    //mPriceSpinner.setEnabled(mProgram.programPrices.size() > 1);

                    mPriceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            mProgram.selectedPriceIndex = position;
                            updateFinalCost();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}

                    });
                } else if(mProgram.programPrices.size() == 1) {
                    mPriceTextView.setVisibility(View.VISIBLE);
                    mPriceSpinner.setVisibility(View.GONE);
                    mPriceSpinner.setAdapter(null);

                    mPriceTextView.setText(mProgram.programPrices.get(0).description);
                }else if(mProgram.programPrices != null && mProgram.programPrices.isEmpty()){
                    mPriceTextView.setVisibility(View.GONE);
                    mPriceSpinner.setVisibility(View.GONE);
                    mPriceSpinner.setAdapter(null);
                }


                /**/
                mAlreadyEnrolledTextView.setVisibility(mProgram.alreadyJoined ? View.VISIBLE : View.GONE);
                if (item.programPrices.get(0).discountMessage != null) {
                    mDiscountMessage.setText(item.programPrices.get(0).discountMessage);
                    mDiscountMessage.setVisibility(View.VISIBLE);
                } else {
                    mDiscountMessage.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void setActivated(boolean active) {
            mProgramNameCheckBox.setChecked(active);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mRecyclerSelectionHelper.setItemChecked(getLayoutPosition(), isChecked, false);
            updateFinalCost();
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btnFreeMonth)
            {
                final BaseDialog.SimpleDialog buyDialog = BaseDialog.SimpleDialog.getDialogInstance("Apply for Free Month", 0,"Do you know by grabbing the free month of this program you are stating you are prepared to reach out to the players in program and meet up on the courts. Only say YES if you are prepared to do that.");
                buyDialog.setPositiveButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enrollForFreeMonth();
                        buyDialog.dismiss();
                    }
                }).setNegativeButton("No", null);
                buyDialog.show(getChildFragmentManager(), "buy-dlg");
            }
        }

        private void enrollForFreeMonth(){
            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "applying for free month...");
            WSHandle.Buy.getFreeMonth(mProgram.id, mProgram.type, new VolleyHelper.IRequestListener<String, String>() {
                @Override
                public void onFailureResponse(String response) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccessResponse(String response) {
                    progressDialog.dismiss();
                    new Prefs.AppData(getActivity()).setUpdatePlayer(true);
                    new BaseDialog.SimpleDialog().setMessage(response).setPositiveButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
                        }
                    }).setNegativeButton("No", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
                        }
                    }).show(getChildFragmentManager(), "confirm-dlg");
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
