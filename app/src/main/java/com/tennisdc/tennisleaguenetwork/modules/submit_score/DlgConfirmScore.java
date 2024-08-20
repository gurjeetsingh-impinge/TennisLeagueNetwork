package com.tennisdc.tennisleaguenetwork.modules.submit_score;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tennisdc.tennisleaguenetwork.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tennisleaguenetwork.common.ScoreFormat;
import com.tennisdc.tennisleaguenetwork.model.Competition;
import com.tennisdc.tennisleaguenetwork.model.NameIdPair;
import com.tennisdc.tennisleaguenetwork.model.PlayerDetail;
import com.tennisdc.tennisleaguenetwork.model.PlayerScore;

import org.parceler.Parcels;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created  on 2015-02-12.
 */
public class DlgConfirmScore extends BaseDialog {

    private static final String EXTRA_SCORE_FORMAT = "score_format";
    private static final String EXTRA_WINNER = "winner_score";
    private static final String EXTRA_OPPONENT = "opponent_score";

    @BindView(R.id.txtWinner)
    TextView WinnerTextView;

    @BindView(R.id.txtWinnerScore)
    TextView WinnerScoreTextView;

    @BindView(R.id.txtOpponent)
    TextView OpponentTextView;

    @BindView(R.id.txtOpponentScore)
    TextView OpponentScoreTextView;

    private PlayerScore mWinnerScore;
    private PlayerScore mOpponentScore;
    private ScoreFormat mScoreFormat;

    public static DlgConfirmScore getDialogInstance(String scoreFormatName, PlayerScore winner, PlayerScore opponent) {
        DlgConfirmScore dialog = new DlgConfirmScore();

        Bundle args = new Bundle();
        args.putString(EXTRA_SCORE_FORMAT, scoreFormatName);
        args.putParcelable(EXTRA_WINNER, Parcels.wrap(winner));
        args.putParcelable(EXTRA_OPPONENT, Parcels.wrap(opponent));

        dialog.setArguments(args);

        return dialog;
    }

    @Override
    protected int getCustomContentLayout() {
        return R.layout.dlg_confirm_score;
    }

    @Override
    protected boolean isForceWrap() {
        return false;
    }

    @Override
    protected void preCreateViewSetup() {
        super.preCreateViewSetup();

        Bundle args = getArguments();
        if (args == null) throw new RuntimeException("Missing Arguments");

        mScoreFormat = ScoreFormat.valueOf(args.getString(EXTRA_SCORE_FORMAT));
        mWinnerScore = Parcels.unwrap(args.getParcelable(EXTRA_WINNER));
        mOpponentScore = Parcels.unwrap(args.getParcelable(EXTRA_OPPONENT));

        showTitle(false);

        setNegativeButton("Cancel", null);
    }

    @Override
    protected void postCreateViewSetup(View v) {
        super.postCreateViewSetup(v);

        ButterKnife.bind(this, v);

        WinnerTextView.setText(mWinnerScore.Player.Name);
        WinnerScoreTextView.setText(mWinnerScore.getScoreString(mScoreFormat));

        OpponentTextView.setText(mOpponentScore.Player.Name);
        OpponentScoreTextView.setText(mOpponentScore.getScoreString(mScoreFormat));
    }
}
