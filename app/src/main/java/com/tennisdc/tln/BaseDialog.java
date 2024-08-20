package com.tennisdc.tln;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tennisdc.tln.model.Competition;
import com.tennisdc.tln.model.DivisionBean;
import com.tennisdc.tln.modules.common.DialogClickListener;
import com.tennisdc.tln.modules.league.FragLeagueDetails;
import com.tennisdc.tln.ui.DividerItemDecoration;
import com.tennisdc.tln.ui.RecyclerAdapter;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams;
import static com.tennisdc.tln.modules.common.FragListSelection.*;


public abstract class BaseDialog extends DialogFragment {

    private final OnClickListener onNullClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };
    public DialogClickListener callback;

    Button btnPositive, btnNegative, btnNeutral;
    int mLeftIconResId;
    View dlgTitle;
    TextView txtDlgTitle;
    ImageView imgDlgTitleLeftIcon;
    ImageView imgDlgTitleRightIcon;
    private boolean mShowTitle = true;
    private CharSequence mTitle, mPositiveText, mNegativeText, mNeutralText;
    private OnClickListener onPositiveClickListener, onNegativeClickListener, onNeutralClickListener;
    private int mRightIconResId;
    private ProgressBar mProgressBar;

    protected BaseDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            callback = (DialogClickListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
        }

        setStyle(STYLE_NO_TITLE, R.style.AppTheme_Dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isForceWrap()) forceWrapContent(getView());
    }

    protected boolean isForceWrap() {
        return true;
    }

    private void forceWrapContent(View v) {
        View current = v;
        do {
            ViewParent parent = current.getParent();
            if (parent != null) {
                try {
                    current = (View) parent;
                } catch (ClassCastException e) {
                    break;
                }
                current.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
            }
        } while (current.getParent() != null);
        current.requestLayout();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        preCreateViewSetup();

        View v = inflater.inflate(R.layout.dlg_generic, container);

        RelativeLayout dlgContent = (RelativeLayout) v.findViewById(R.id.dlgContent);
        dlgContent.addView(inflater.inflate(getCustomContentLayout(), null));

        postCreateViewSetup(v);
        return v;
    }

    protected void preCreateViewSetup() {
    }

    protected void postCreateViewSetup(View v) {
        dlgTitle = v.findViewById(R.id.dlgTitle);
        txtDlgTitle = (TextView) v.findViewById(R.id.txtDlgTitle);
        imgDlgTitleLeftIcon = (ImageView) v.findViewById(R.id.imgDlgTitleLeftIcon);
        imgDlgTitleRightIcon = (ImageView) v.findViewById(R.id.imgDlgRightIcon);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBarDlgTitle);

        btnPositive = (Button) v.findViewById(R.id.btnPositive);
        btnNegative = (Button) v.findViewById(R.id.btnNegative);
        btnNeutral = (Button) v.findViewById(R.id.btnNeutral);

        if (mShowTitle) {
            if (!TextUtils.isEmpty(mTitle)) txtDlgTitle.setText(mTitle);
            if (mLeftIconResId > 0) imgDlgTitleLeftIcon.setImageResource(mLeftIconResId);
            if (mLeftIconResId > 0) imgDlgTitleRightIcon.setImageResource(mRightIconResId);
        } else {
            dlgTitle.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(mPositiveText) && TextUtils.isEmpty(mNegativeText) && TextUtils.isEmpty(mNeutralText)) {
            //check if no button set
            btnPositive.setVisibility(View.VISIBLE);
            btnPositive.setText(android.R.string.ok);
            btnPositive.setOnClickListener(onNullClickListener);
        } else {
            if (!TextUtils.isEmpty(mPositiveText)) {
                btnPositive.setVisibility(View.VISIBLE);
                btnPositive.setText(mPositiveText);
                if (onPositiveClickListener == null)
                    btnPositive.setOnClickListener(onNullClickListener);
                else btnPositive.setOnClickListener(onPositiveClickListener);
            }

            if (!TextUtils.isEmpty(mNegativeText)) {
                btnNegative.setVisibility(View.VISIBLE);
                btnNegative.setText(mNegativeText);
                if (onNegativeClickListener == null)
                    btnNegative.setOnClickListener(onNullClickListener);
                else btnNegative.setOnClickListener(onNegativeClickListener);
            }

            if (!TextUtils.isEmpty(mNeutralText)) {
                btnNeutral.setVisibility(View.VISIBLE);
                btnNeutral.setText(mNeutralText);
                if (onNeutralClickListener == null)
                    btnNeutral.setOnClickListener(onNullClickListener);
                else btnNeutral.setOnClickListener(onNeutralClickListener);
            }
        }
    }

    protected abstract int getCustomContentLayout();

    public void setProgressBarVisibility(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public BaseDialog setTitle(CharSequence title) {
        this.mTitle = title;
        if (txtDlgTitle != null) txtDlgTitle.setText(this.mTitle);
        return this;
    }

    public BaseDialog setLeftIcon(int iconResId) {
        this.mLeftIconResId = iconResId;
        if (imgDlgTitleLeftIcon != null) {
            imgDlgTitleLeftIcon.setVisibility((iconResId == 0) ? View.GONE : View.VISIBLE);
            imgDlgTitleLeftIcon.setImageResource(this.mLeftIconResId);
        }
        return this;
    }

    public BaseDialog setRightIcon(int iconResId) {
        this.mRightIconResId = iconResId;
        if (imgDlgTitleRightIcon != null) {
            imgDlgTitleRightIcon.setVisibility((iconResId == 0) ? View.GONE : View.VISIBLE);
            imgDlgTitleRightIcon.setImageResource(this.mRightIconResId);
        }
        return this;
    }

    public void showTitle(boolean showTitle) {
        this.mShowTitle = showTitle;
    }

    public BaseDialog setPositiveButton(CharSequence buttonText, OnClickListener onClickListener) {
        this.mPositiveText = buttonText;
        onPositiveClickListener = onClickListener;
        if (btnPositive != null) {
            btnPositive.setText(this.mPositiveText);
            btnPositive.setOnClickListener(onClickListener);
        }
        return this;
    }

    public BaseDialog setNegativeButton(CharSequence buttonText, OnClickListener onClickListener) {
        this.mNegativeText = buttonText;
        onNegativeClickListener = onClickListener;
        if (btnNegative != null) {
            btnNegative.setText(this.mNegativeText);
            btnNegative.setOnClickListener(onClickListener);
        }
        return this;
    }

    protected void hideSoftKeyboard() {
        View view = getActivity().getWindow().getDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm.isAcceptingText()) {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public BaseDialog setNeutralButton(CharSequence buttonText, OnClickListener onClickListener) {
        this.mNeutralText = buttonText;
        onNeutralClickListener = onClickListener;
        if (btnNeutral != null) {
            btnNeutral.setText(this.mNeutralText);
            btnNeutral.setOnClickListener(onClickListener);
        }
        return this;
    }

    public static class MissingInternetErrorDialog {

        public static SimpleDialog getDialogInstance() {
            SimpleDialog dialog = new SimpleDialog();
            dialog.setTitle("Internet?");
            dialog.setLeftIcon(0);
            dialog.setCancelable(false);
            dialog.setMessage("Please check the internet and try again later.");
            return dialog;
        }

    }

    public static class SimpleDialog extends BaseDialog {

        private CharSequence mMessage;

        public static SimpleDialog getDialogInstance(CharSequence title, int resId, String message) {
            SimpleDialog dialog = new SimpleDialog();
            dialog.setTitle(title);
            dialog.setLeftIcon(resId);
            dialog.setMessage(message);
            dialog.setCancelable(false);

            return dialog;
        }

        public SimpleDialog setMessage(CharSequence message) {
            this.mMessage = message;
            return this;
        }

        @Override
        protected void postCreateViewSetup(View v) {
            super.postCreateViewSetup(v);
            TextView txtDlgMessage = (TextView) v.findViewById(R.id.txtDlgMessage);
            txtDlgMessage.setText(mMessage);
        }

        @Override
        public int getCustomContentLayout() {
            return R.layout.dlg_simple_text_content;
        }

    }

    public static class DivisionDialog extends BaseDialog {

        List<DivisionBean> mDataList;
        Competition mCompetitionData;
        int mStatus = 0;
        long mCompetitionID = 0;

        public static DivisionDialog getDialogInstance(CharSequence title, int resId,
                                                       List<DivisionBean> mDataListTemp, int status) {
//			mDataList = mDataListTemp;
            DivisionDialog dialog = new DivisionDialog();
            dialog.setTitle(title);
            dialog.setLeftIcon(resId);
            dialog.setCancelable(false);

            return dialog;
        }

        public DivisionDialog setRecyclerView(Competition mCompetitionData) {
            this.mDataList = mCompetitionData.getDivisions();
            this.mCompetitionData = mCompetitionData;
            this.mStatus = mCompetitionData.status;
            this.mCompetitionID = mCompetitionData.getCompetitionId();
            return this;
        }

        @Override
        protected void postCreateViewSetup(View v) {
            super.postCreateViewSetup(v);
            RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, R.drawable.divider_competition));

            v.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                        getActivity().setResult(Activity.RESULT_CANCELED);
                    return false;
                }
            });
            mRecyclerView.setAdapter(new RecyclerAdapter<DivisionBean, ObjectViewHolder>(mDataList) {

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

        @Override
        public int getCustomContentLayout() {
            return R.layout.dlg_division;
        }

        class ObjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @BindView(R.id.text1bind)
            TextView text1;
            @BindView(R.id.text2bind)
            TextView text2;


            public ObjectViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(this);
            }

            public void bindData(Object item) {
                String mValue = ((DivisionBean) item).getName();
                if(!((DivisionBean) item).getDivision_text().trim().isEmpty())
                    mValue = mValue +"->"+((DivisionBean) item).getDivision_text();
                text1.setText(mValue);
                text2.setVisibility(View.GONE);

            }

            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putParcelable(RESULT_SELECTION, Parcels.wrap(mCompetitionData));
                data.putParcelable(RESULT_SELECTION_DIVISION,
                        Parcels.wrap((DivisionBean) (mDataList.get(getAdapterPosition()))));
                data.putInt(RESULT_STATUS, mStatus);
                data.putString(RESULT_START_DATE,  mCompetitionData.getStart_date());
                data.putLong(RESULT_COMPETITION_ID,mCompetitionID);
                Class fragClass = FragLeagueDetails.class;
                startActivity(SingleFragmentActivity.getIntent(getActivity(), fragClass, data));
//
//                Intent data = new Intent();
////                data.putExtra(RESULT_SELECTION, Parcels.wrap(mDataList.get(getAdapterPosition())));
//                data.putExtra(RESULT_SELECTION, Parcels.wrap(mCompetitionData));
//                data.putExtra(RESULT_SELECTION_DIVISION, Parcels.wrap(mDataList.get(getAdapterPosition())));
//                data.putExtra(RESULT_STATUS, mStatus);
//                data.putExtra(RESULT_START_DATE, mCompetitionData.getStart_date());
//                data.putExtra(RESULT_COMPETITION_ID, mCompetitionID);
//                getActivity().setResult(Activity.RESULT_OK, data);
//                getActivity().finish();

            }
        }
    }

}
