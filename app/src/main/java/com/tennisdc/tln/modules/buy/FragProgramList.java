package com.tennisdc.tln.modules.buy;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.SingleFragmentActivity;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.DialogsUtil;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.interfaces.OnDialogButtonNuteralClickListener;
import com.tennisdc.tln.model.Program;
import com.tennisdc.tln.model.Swag_items;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.CustomSpinnerWithErrorAdapter;
import com.tennisdc.tln.ui.RecyclerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created  on 2015-04-16.
 */
public class FragProgramList extends Fragment {

    /* Views */
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    /* Views */
    @BindView(R.id.recyclerViewSwagItem)
    RecyclerView mRVSwagItem;

    @BindView(R.id.viewSummary)
    View mSummaryView;

    @BindView(R.id.txtFinalCost)
    TextView mFinalCostTextView;

    @BindView(R.id.btnCheckout)
    Button mCheckoutButton;

    DialogsUtil mDialogUtils;
    /* Data */
    private List<Program> mProgramList;
    private List<Swag_items> mSwagItemList;
   private List<String> addDesc;

    private RecyclerAdapter.RecyclerSelectionHelper<Program> mRecyclerSelectionHelper;
    private RecyclerAdapter.RecyclerSelectionHelper<Swag_items> mRecyclerItemSelectionHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_programs, container, false);
        App.LogFacebookEvent(getActivity(),this.getClass().getName());

        ButterKnife.bind(this, view);

        App.LogFacebookEvent(getActivity(),this.getClass().getName());

        mDialogUtils = new DialogsUtil();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRVSwagItem.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));

        mCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onClick(View v) {
                boolean mFlagProceed = false;
                List<Program> selectedPrograms = new ArrayList<>();
                List<Swag_items> selectedItems = new ArrayList<>();
                SparseBooleanArray selectedPositionsItems = mRecyclerItemSelectionHelper.getSelectedPositions();

                if (selectedPositionsItems.size() > 0) {
                    mFlagProceed = true;

                    for (int i = 0; i < selectedPositionsItems.size(); i++)

                        selectedItems.add(mSwagItemList.get(selectedPositionsItems.keyAt(i)));
                }


                for (int i = 0; i < selectedPositionsItems.size(); i++) {

                    // Get the name of the selected program and swag item and concatenate them into a single string
                    String name = mProgramList.get(selectedPositionsItems.keyAt(i)).name;
                    Log.e("Check mProgramList ",name);
                    // Add the concatenated string to the addDesc list
                    addDesc.add(name);
                }

                for (int i = 0; i < mSwagItemList.size(); i++) {

                    // Get the name of the selected program and swag item and concatenate them into a single string
                    String name = mSwagItemList.get(selectedPositionsItems.keyAt(i)).title;
                    Log.e("Check mSwagItemList ",name);

                    // Add the concatenated string to the addDesc list
                    addDesc.add(name);
                }

                SparseBooleanArray selectedPositions = mRecyclerSelectionHelper.getSelectedPositions();

                if (selectedPositions.size() > 0) {
                    mFlagProceed = true;
                    for (int i = 0; i < selectedPositions.size(); i++) {
                        Program mProgramData = mProgramList.get(selectedPositions.keyAt(i));
//                        for (int j = 0; j < mProgramData.programPrices.size(); j++) {
//                            if (mProgramData.programPrices.get(j).discountMessage == null) {
//                                mProgramData.programPrices.get(j).discountMessage = "";
//                            }
//                            if (mProgramData.programPrices.get(j).priceAfterDiscount == null) {
//                                mProgramData.programPrices.get(j).priceAfterDiscount = 0.0;
//                            }
//                        }
                        selectedPrograms.add(mProgramList.get(selectedPositions.keyAt(i)));
                    }
                }
                if(mFlagProceed)

                startActivity(SingleFragmentActivity.getIntent(getActivity(), FragCheckout.class, FragCheckout.buildArgs(selectedPrograms,selectedItems, TextUtils.join(",", addDesc) + "-" + new com.tennisdc.tennisleaguenetwork.common.Prefs.AppData(getActivity()).getDomainName())));
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        App.sCouponDetails = null;
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "fetching programs...", true, false);
        WSHandle.Buy.getProgramList(new VolleyHelper.IRequestListener<JSONObject, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccessResponse(JSONObject response) {
                try {
                    Type listType = new TypeToken<List<Program>>() {
                    }.getType();
                    mProgramList = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("programs_details"), listType);

                    Type ItemListType = new TypeToken<List<Swag_items>>() {
                    }.getType();
                    mSwagItemList = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("swag_items"), ItemListType);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mRecyclerItemSelectionHelper = new RecyclerAdapter.RecyclerSelectionHelper<>(mRVSwagItem);
                mRecyclerItemSelectionHelper.setSelectionMode(RecyclerAdapter.RecyclerSelectionHelper.SELECTION_MODE_MULTI);
                mRVSwagItem.setAdapter(new RecyclerAdapter<Swag_items, SwagItemViewHolder>(mSwagItemList) {

                    @Override
                    public SwagItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_swag_item, null);
                        return new SwagItemViewHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(SwagItemViewHolder holder, int position) {
                        holder.bindItem(getItem(position));
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }
                });

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
                Toast.makeText(getActivity(), getActivity().getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle("Join Today");
    }

    void updateFinalCost(Boolean mFlagSwagItem) {

        double finalCost = 0;
//        if (mFlagSwagItem) {
			SparseBooleanArray selectedPositionsItems = mRecyclerItemSelectionHelper.getSelectedPositions();
			if (selectedPositionsItems.size() > 0) {
				mCheckoutButton.setEnabled(true);
				for (int i = 0; i < selectedPositionsItems.size(); i++) {
					Swag_items swagItems = mSwagItemList.get(selectedPositionsItems.keyAt(i));
						finalCost +=  (Double.valueOf(swagItems.cost)) + (Double.valueOf(swagItems.shipping_cost));
				}
			} /*else {
				mCheckoutButton.setEnabled(false);
			}*/
//        } else {
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
//                    program.programPrices.set(program.selectedPriceIndex).priceAfterDiscount;

                    }
//                }
            }/* else {
                mCheckoutButton.setEnabled(false);
            }*/
        }
        if(finalCost == 0.00){
			mCheckoutButton.setEnabled(false);
		}else{
			mCheckoutButton.setEnabled(true);
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

//				if (mProgram.type.equalsIgnoreCase("Non League Program")||mProgram.type.equalsIgnoreCase("Tennis Ladder")) {
//					mFreeMonthButton.setText(R.string.free_month_text);
//				} else {
//					mFreeMonthButton.setText(R.string.free_season_text);
//				}
                mFreeMonthButton.setText(mProgram.free_program_text);
            } else {
                mPriceView.setVisibility(View.VISIBLE);
                mFreeMonthButton.setVisibility(View.GONE);
                mProgramNameCheckBox.setEnabled(true);
                mProgramNameCheckBox.setChecked(mRecyclerSelectionHelper.isItemChecked(getLayoutPosition()));

                if (mProgram.programPrices.size() > 1) {
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
                            updateFinalCost(false);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }

                    });
                } else if (mProgram.programPrices.size() == 1) {
                    mPriceTextView.setVisibility(View.VISIBLE);
                    mPriceSpinner.setVisibility(View.GONE);
                    mPriceSpinner.setAdapter(null);

                    mPriceTextView.setText(mProgram.programPrices.get(0).description);
                } else if (mProgram.programPrices != null && mProgram.programPrices.isEmpty()) {
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
            updateFinalCost(false);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnFreeMonth) {
                if (mProgram.free_program_text.toLowerCase().contains("month")) {
                    final BaseDialog.SimpleDialog buyDialog = BaseDialog.SimpleDialog.getDialogInstance("Apply for Free Month", 0, "Do you know by grabbing the free month of this program you are stating you are prepared to reach out to the players in program and meet up on the courts. Only say YES if you are prepared to do that.");
                    buyDialog.setPositiveButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enrollForFreeMonth();
                            buyDialog.dismiss();
                        }
                    }).setNegativeButton("No", null);
                    buyDialog.setCancelable(false);
                    buyDialog.show(getChildFragmentManager(), "buy-dlg");
                } else {
//					Toast.makeText(getActivity(), "call API", Toast.LENGTH_SHORT).show();
                    BaseDialog.SimpleDialog simpleDialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, "Here is the response");
                    simpleDialog.setCancelable(false);
                    simpleDialog.setPositiveButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
                        }
                    });
                    simpleDialog.setNegativeButton("No", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
                        }
                    });
                    simpleDialog.show(getChildFragmentManager(), "confirm-dlg");
//                    enrollForFreeMonth();
                }
            }
        }

        private void enrollForFreeMonth() {
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
                    BaseDialog.SimpleDialog simpleDialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                    simpleDialog.setCancelable(false);
                    simpleDialog.setPositiveButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
                        }
                    });
                    simpleDialog.setNegativeButton("No", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
                        }
                    });
                    simpleDialog.show(getChildFragmentManager(), "confirm-dlg");

//					new BaseDialog.SimpleDialog().setMessage(response).setPositiveButton("Yes", new View.OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
//						}
//					}).setNegativeButton("No", new View.OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
//						}
//					}).show(getChildFragmentManager(), "confirm-dlg");
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), getActivity().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public class SwagItemViewHolder extends RecyclerView.ViewHolder implements RecyclerAdapter.RecyclerSelectionHelper.SelectionHelperCallbacks, CompoundButton.OnCheckedChangeListener, View.OnClickListener {

        /* views */
        @BindView(R.id.chkBxSwagItem)
        CheckBox mSwagItemCheckBox;

        @BindView(R.id.mImgSwagItem)
        AppCompatImageView mImgSwagItem;

        @BindView(R.id.mTxtNameSwagItem)
        TextView mTxtNameSwagItem;

        @BindView(R.id.mTxtPriceSwagItem)
        TextView mTxtPriceSwagItem;

        @BindView(R.id.mTxtShippingSwagItem)
        TextView mTxtShippingSwagItem;

        @BindView(R.id.mTxtSizeSwagItem)
        TextView mTxtSizeSwagItem;

        @BindView(R.id.mImgInfoSwagItem)
        AppCompatImageView mImgInfoSwagItem;

        /* data */
        Swag_items mSwagItem;
        private CustomSpinnerWithErrorAdapter mPriceSpinnerAdapter;

        public SwagItemViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            mSwagItemCheckBox.setOnCheckedChangeListener(this);
        }

        public void bindItem(Swag_items item) {
            mSwagItem = item;

            Glide.with(getActivity()).load(mSwagItem.image)
                    .placeholder(R.drawable.ic_photos).into(mImgSwagItem);
            mTxtNameSwagItem.setText(item.title);
            mTxtPriceSwagItem.setText(getActivity().getString(R.string.currency_symbol) + item.cost);
            mTxtShippingSwagItem.setText("+" + getActivity().getString(R.string.currency_symbol) + item.shipping_cost + " " + getString(R.string.shipping_cost));
//			mTxtSizeSwagItem.setText(String.valueOf(item.sizes.size()));
            mTxtSizeSwagItem.setOnClickListener(this);
            mImgInfoSwagItem.setOnClickListener(this);
            if (item.sizes.size() <= 0) {
                mTxtSizeSwagItem.setVisibility(View.GONE);
            } else {
                mTxtSizeSwagItem.setVisibility(View.VISIBLE);
                mTxtSizeSwagItem.setText(item.sizes.get(0));
            }
        }

        @Override
        public void setActivated(boolean active) {
            mSwagItemCheckBox.setChecked(active);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            if(mTxtSizeSwagItem.getText().equals(getActivity().getString(R.string.select_size))){
//                Toast.makeText(getActivity(), R.string.please_select_size_first, Toast.LENGTH_LONG).show();
//            }else {
                mRecyclerItemSelectionHelper.setItemChecked(getLayoutPosition(), isChecked, false);
                updateFinalCost(true);
//            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.mTxtSizeSwagItem) {
                PopupMenu mSizePopup = new PopupMenu(getActivity(), mTxtSizeSwagItem);
                for (int i = 0; i < mSwagItem.sizes.size(); i++) {
                    mSizePopup.getMenu().add(mSwagItem.sizes.get(i));
                }
                //registering popup with OnMenuItemClickListener
                mSizePopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        mTxtSizeSwagItem.setText(item.getTitle());
                        return true;
                    }
                });
                mSizePopup.show();
            }
            if (v.getId() == R.id.mImgInfoSwagItem) {
                mDialogUtils.openAlertDialogWithCustomTitle(getActivity(), mSwagItem.title, mSwagItem.description, getActivity().getString(R.string.done), "", "",
                        new OnDialogButtonNuteralClickListener() {
                            @Override
                            public void onPositiveButtonClicked() {

                            }

                            @Override
                            public void onNegativeButtonClicked() {

                            }

                            @Override
                            public void onNuteralButtonClicked() {

                            }
                        });
//				final BaseDialog.SimpleDialog mDescriptionDialog = BaseDialog.SimpleDialog.getDialogInstance(mSwagItem.title, 0, mSwagItem.description);
//				mDescriptionDialog.setPositiveButton(getActivity().getString(R.string.done), new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						mDescriptionDialog.dismiss();
//					}
//				});
//				mDescriptionDialog.setCancelable(false);
//				mDescriptionDialog.show(getChildFragmentManager(), "buy-dlg");
            }
            if (v.getId() == R.id.btnFreeMonth) {
				/*if(mProgram.free_program_text.toLowerCase().contains("month")){
					final BaseDialog.SimpleDialog buyDialog = BaseDialog.SimpleDialog.getDialogInstance("Apply for Free Month", 0, "Do you know by grabbing the free month of this program you are stating you are prepared to reach out to the players in program and meet up on the courts. Only say YES if you are prepared to do that.");
					buyDialog.setPositiveButton("Yes", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							enrollForFreeMonth();
							buyDialog.dismiss();
						}
					}).setNegativeButton("No", null);
					buyDialog.setCancelable(false);
					buyDialog.show(getChildFragmentManager(), "buy-dlg");
				}else{
//					Toast.makeText(getActivity(), "call API", Toast.LENGTH_SHORT).show();
					enrollForFreeMonth();
				}*/
            }
        }

		/*private void enrollForFreeMonth() {
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
					BaseDialog.SimpleDialog simpleDialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
					simpleDialog.setCancelable(false);
					simpleDialog.setPositiveButton("Yes", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
						}
					});
					simpleDialog.setNegativeButton("No", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
						}
					});
					simpleDialog.show(getChildFragmentManager(), "confirm-dlg");

//					new BaseDialog.SimpleDialog().setMessage(response).setPositiveButton("Yes", new View.OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							startActivity(SingleFragmentActivity.BuyActivity.getIntent(getActivity()));
//						}
//					}).setNegativeButton("No", new View.OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							startActivity(SingleFragmentActivity.HomeActivity.getIntent(getActivity()));
//						}
//					}).show(getChildFragmentManager(), "confirm-dlg");
				}

				@Override
				public void onError(VolleyError error) {
					progressDialog.dismiss();
					Toast.makeText(getActivity(), getActivity().getString(R.string.network_error), Toast.LENGTH_LONG).show();
				}
			});
		}*/
    }
}
