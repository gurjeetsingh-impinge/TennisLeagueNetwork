package com.tennisdc.tln.modules.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.model.Competition;
import com.tennisdc.tln.model.CompetitionType;
import com.tennisdc.tennisleaguenetwork.model.Court;
import com.tennisdc.tln.model.DivisionBean;
import com.tennisdc.tln.model.PlayerDetail;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.CustomSpinnerWithErrorAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.BindViews;
import io.realm.Realm;

/**
 * Created  on 05-02-2015.
 */


public class DlgSendMail extends BaseDialog {

    //private static final String EXTRA_COURTS = "courts";
    private static final String EXTRA_COMPETITION = "competition";
    private static final String EXTRA_COMPETITION_ID = "competition_id";
    private static final String EXTRA_FROM = "from_player";
    private static final String EXTRA_TO = "to_player";
    private static final String EXTRA_COMPETITION_TYPE = "competition_type";
    private static final String EXTRA_SEND_SMS = "send_sms";
    private static final String EXTRA_SUPPORT_MAIL = "customer_support_email";

    @BindView(R.id.spnrCourts)
    Spinner CourtsSpinner;

    @BindView(R.id.spnrPreferredAction)
    Spinner ActionSpinner;

    @BindViews({R.id.chkBxMon, R.id.chkBxTue, R.id.chkBxWed, R.id.chkBxThu, R.id.chkBxFri, R.id.chkBxSat, R.id.chkBxSun})
    List<CheckBox> DaysCheckBoxes;

    @BindViews({R.id.chkBxMornings, R.id.chkBxAfternoons, R.id.chkBxEvenings})
    List<CheckBox> TimesCheckBoxes;

    @BindView(R.id.edtAdditionalNotes)
    EditText AdditionalNotesEditText;

    PlayerDetail mToPlayer, mFromPlayer;

    List<PlayerDetail> playerDetails;

    Competition mCompetition;
    DivisionBean mDivision;
    private List<Court> mCourts;
    private CompetitionType mCompetitionType;
    private Realm mRealm;
    private String mSupportMail = "";
    private long mCompetitionID = 0;

    public static DlgSendMail getDialogInstanceLeagues(Competition competition, PlayerDetail fromPlayer, PlayerDetail toPlayer) {
        DlgSendMail dialog = new DlgSendMail();

        Bundle args = new Bundle();
        //args.putParcelable(EXTRA_COURTS, Parcels.wrap(courts));
        args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(competition));
        args.putString(EXTRA_COMPETITION_TYPE, CompetitionType.league.name());
        args.putParcelable(EXTRA_FROM, Parcels.wrap(fromPlayer));
        args.putParcelable(EXTRA_TO, Parcels.wrap(toPlayer));

        dialog.setArguments(args);

        return dialog;
    }


    public static DlgSendMail getDialogInstanceLeagues(Competition competition, PlayerDetail fromPlayer, List<PlayerDetail> toPlayer, boolean isSMS) {
        DlgSendMail dialog = new DlgSendMail();
        Bundle args = new Bundle();
        //args.putParcelable(EXTRA_COURTS, Parcels.wrap(courts));
        args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(competition));
        args.putString(EXTRA_COMPETITION_TYPE, CompetitionType.league.name());
        args.putParcelable(EXTRA_FROM, Parcels.wrap(fromPlayer));
        args.putParcelable(EXTRA_TO, Parcels.wrap(toPlayer));
        args.putParcelable(EXTRA_SEND_SMS, Parcels.wrap(isSMS));

        dialog.setArguments(args);

        return dialog;
    }

    public static DlgSendMail getDialogInstanceLeaguesDivision(long mCompetitionID,Competition competition,
                                                               PlayerDetail fromPlayer, List<PlayerDetail> toPlayer,
                                                               boolean isSMS, String mSupportMial) {

        DlgSendMail dialog = new DlgSendMail();

        Bundle args = new Bundle();
        //args.putParcelable(EXTRA_COURTS, Parcels.wrap(courts));
        args.putLong(EXTRA_COMPETITION_ID, mCompetitionID);
        args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(competition));
        args.putString(EXTRA_COMPETITION_TYPE, CompetitionType.league.name());
        args.putParcelable(EXTRA_FROM, Parcels.wrap(fromPlayer));
        args.putParcelable(EXTRA_TO, Parcels.wrap(toPlayer));
        args.putParcelable(EXTRA_SEND_SMS, Parcels.wrap(isSMS));
        args.putParcelable(EXTRA_SUPPORT_MAIL, Parcels.wrap(mSupportMial));
        args.putParcelable(EXTRA_SUPPORT_MAIL, Parcels.wrap(mSupportMial));

        dialog.setArguments(args);

        return dialog;
    }

    public static DlgSendMail getDialogInstanceTournament(Competition competition, PlayerDetail fromPlayer, PlayerDetail toPlayer) {
        DlgSendMail dialog = new DlgSendMail();

        Bundle args = new Bundle();
        //args.putParcelable(EXTRA_COURTS, Parcels.wrap(courts));
        args.putParcelable(EXTRA_COMPETITION, Parcels.wrap(competition));
        args.putString(EXTRA_COMPETITION_TYPE, CompetitionType.tournament.name());
        args.putParcelable(EXTRA_FROM, Parcels.wrap(fromPlayer));
        args.putParcelable(EXTRA_TO, Parcels.wrap(toPlayer));

        dialog.setArguments(args);

        return dialog;
    }

    public static DlgSendMail getDialogInstancePartnerProgram(PlayerDetail fromPlayer, List<PlayerDetail> toPlayer, boolean isSMS) {
        DlgSendMail dialog = new DlgSendMail();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_FROM, Parcels.wrap(fromPlayer));
        args.putString(EXTRA_COMPETITION_TYPE, CompetitionType.partner.name());
        args.putParcelable(EXTRA_TO, Parcels.wrap(toPlayer));
        args.putParcelable(EXTRA_SEND_SMS, Parcels.wrap(isSMS));

        dialog.setArguments(args);

        return dialog;
    }

    public static DlgSendMail getDialogInstancePartnerProgram(PlayerDetail fromPlayer, PlayerDetail toPlayer) {
        DlgSendMail dialog = new DlgSendMail();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_FROM, Parcels.wrap(fromPlayer));
        args.putString(EXTRA_COMPETITION_TYPE, CompetitionType.partner.name());
        args.putParcelable(EXTRA_TO, Parcels.wrap(toPlayer));

        dialog.setArguments(args);

        return dialog;
    }

    public static DlgSendMail getDialogInstanceTennisLadder(PlayerDetail fromPlayer, List<PlayerDetail> toPlayer, boolean isSMS) {
        DlgSendMail dialog = new DlgSendMail();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_FROM, Parcels.wrap(fromPlayer));
        args.putString(EXTRA_COMPETITION_TYPE, CompetitionType.ladder.name());
        args.putParcelable(EXTRA_TO, Parcels.wrap(toPlayer));
        args.putParcelable(EXTRA_SEND_SMS, Parcels.wrap(isSMS));

        dialog.setArguments(args);

        return dialog;
    }

    public static DlgSendMail getDialogInstanceTennisLadder(PlayerDetail fromPlayer, PlayerDetail toPlayer) {
        DlgSendMail dialog = new DlgSendMail();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_FROM, Parcels.wrap(fromPlayer));
        args.putString(EXTRA_COMPETITION_TYPE, CompetitionType.ladder.name());
        args.putParcelable(EXTRA_TO, Parcels.wrap(toPlayer));
        //args.putParcelable(EXTRA_COURTS, Parcels.wrap(courts));

        dialog.setArguments(args);

        return dialog;
    }

    @Override
    protected void preCreateViewSetup() {
        super.preCreateViewSetup();

        Bundle args = getArguments();
        if (args == null) throw new RuntimeException("Missing Arguments");

        mCourts = new ArrayList<>();
        Court court = new Court();
        court.setId(0);
        court.setName("Select Match Location");
        mCourts.add(court);

        mCourts.addAll(mRealm.allObjects(Court.class));
        if(args.containsKey(EXTRA_SUPPORT_MAIL)){
            mSupportMail = args.getString(EXTRA_SUPPORT_MAIL);
        }
        if(args.containsKey(EXTRA_COMPETITION_ID)){
            mCompetitionID = args.getLong(EXTRA_COMPETITION_ID);
        }
        if (Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION)) instanceof Competition)
            mCompetition = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));
        else
            mDivision = Parcels.unwrap(args.getParcelable(EXTRA_COMPETITION));
        mFromPlayer = Parcels.unwrap(args.getParcelable(EXTRA_FROM));

        if (Parcels.unwrap(args.getParcelable(EXTRA_TO)) instanceof List) {
            playerDetails = Parcels.unwrap(args.getParcelable(EXTRA_TO));
        } else {
            mToPlayer = Parcels.unwrap(args.getParcelable(EXTRA_TO));
        }

        mCompetitionType = Enum.valueOf(CompetitionType.class, args.getString(EXTRA_COMPETITION_TYPE));

        if (args.getParcelable(EXTRA_SEND_SMS) != null) {
            if (Parcels.unwrap(args.getParcelable(EXTRA_SEND_SMS))) {
                sendSMSButtonSetup();
            } else {
                sendMailButtonSetup();
            }
        } else {
            sendMailButtonSetup();
        }

        setNegativeButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onNoClick();
                }
                dismiss();
            }
        });
    }

    private void sendSMSButtonSetup() {
        String nameString = "";
        final String[] recipient_name = new String[((playerDetails != null) ? playerDetails.size() : 0)];

        if (playerDetails != null) {
            int count = 0;
            for (PlayerDetail player : playerDetails) {
                nameString = nameString + player.Phone + ",";
                recipient_name[count] = player.Name;
                count++;
            }
        } else {
            setTitle("mail to : " + mToPlayer.Name);
            nameString = mToPlayer.Name;
        }
        final String[] toNumbers = nameString.split(",");

        setPositiveButton("Send SMS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSuccess = true;

                String matchId = "";

                if (mCompetitionType == CompetitionType.league) {
                    Court court = (Court) CourtsSpinner.getSelectedItem();
                    if (court.getId() <= 0) {
                        isSuccess = false;
                        ((CustomSpinnerWithErrorAdapter) CourtsSpinner.getAdapter()).setError(CourtsSpinner.getSelectedView(), "Please select a Location");
                    } else {
                        matchId = String.valueOf(court.getId());
                    }
                }

                StringBuilder days = new StringBuilder();
                for (CheckBox checkBox : DaysCheckBoxes) {
                    if (checkBox.isChecked()) {
                        if (days.length() > 0) days.append(", ");
                        days.append(checkBox.getTag());
                    }
                }

                if (days.length() == 0) {
                    Toast.makeText(getActivity(), "Please select preferred days of the week", Toast.LENGTH_LONG).show();
                    return;
                }

                StringBuilder times = new StringBuilder();
                for (CheckBox checkBox : TimesCheckBoxes) {
                    if (checkBox.isChecked()) {
                        if (times.length() > 0) times.append(", ");
                        times.append(checkBox.getTag());
                    }
                }

                if (times.length() == 0) {
                    Toast.makeText(getActivity(), "Please select preferred time of the day", Toast.LENGTH_LONG).show();
                    return;
                }

                if (isSuccess) {

                    Court preferredCourt = (mCompetitionType == CompetitionType.partner) ? mFromPlayer.HomeCourt : (Court) CourtsSpinner.getSelectedItem();

                    if (mCompetitionType == CompetitionType.league || mCompetitionType == CompetitionType.tournament) {
                        String mDomainName = new Prefs.AppData(getActivity()).getDomainName().replace("www.","").replace(".com","");
                        String strSMSBody = "Hello " +TextUtils.join(",", recipient_name) +", my name " + mFromPlayer.Name + " from "
                                + mDomainName + ".";
//                        String recipient_name_temp = TextUtils.join(",", recipient_name) +", my name " + mFromPlayer.Name + " from "
//                                + new Prefs.AppData(getActivity()).getDomainName();
                        try {
                            strSMSBody = "Hello " + recipient_name[0].split(",")[1].trim() +", my name " + mFromPlayer.Name + " from "
                                    + mDomainName + ".";
                        }catch (Exception e){
                            e.printStackTrace();
                        }
//                        if (mCompetition != null) {
//                            strSMSBody = getString(R.string.sms_league_invitation_body,recipient_name_temp, mCompetition.CompetitionName, preferredCourt.getName(),
//                                    times.toString() + " on " + days.toString());
//                        } else {
//                            strSMSBody = getString(R.string.sms_league_invitation_body,recipient_name_temp, mDivision.getName(), preferredCourt.getName(),
//                                    times.toString() + " on " + days.toString());
//                        }

                        String strHtmlMessage = "";
                        String messageStr = AdditionalNotesEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(messageStr))
                            strHtmlMessage = getString(R.string.sms_league_invitation_player_message, messageStr);

                        String strHtmlPlayerInfo = getString(R.string.sms_league_invitation_player_info, mFromPlayer.Name, mFromPlayer.Phone, mFromPlayer.Email);

                        String smsBodySpanned = strSMSBody; // + strHtmlMessage + strHtmlPlayerInfo;

                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + TextUtils.join(";", toNumbers)));
                        smsIntent.putExtra("sms_body", smsBodySpanned);
                        startActivity(Intent.createChooser(smsIntent, "Send Message"));
                        if (callback != null) {
                            callback.onSendClick();
                        }
                        dismiss();

                    } else if (mCompetitionType == CompetitionType.partner || mCompetitionType == CompetitionType.ladder) {

                        String actionStr = ActionSpinner.getSelectedItem().toString();
                        if (ActionSpinner.getSelectedItemPosition() == 2)
                            actionStr = "PLAY A MATCH or HIT AROUND";

                        String mDomainName = new Prefs.AppData(getActivity()).getDomainName().replace("www.","").replace(".com","");
                        String strSMSBody = "Hello " +TextUtils.join(",", recipient_name) +", my name " + mFromPlayer.Name + " from "
                                + mDomainName + " Community";
                        try {
                            strSMSBody = "Hello " + recipient_name[0].split(",")[1].trim() +", my name " + mFromPlayer.Name + " from "
                                    + mDomainName + " Community";
                        }catch (Exception e){
                            e.printStackTrace();
                        }
//                        String strSMSBody = getString(R.string.sms_partner_invitation_body,
//                                TextUtils.join(",", recipient_name), actionStr, preferredCourt.getName(),
//                                times.toString() + " on " + days.toString());

                        String strHtmlMessage = "";
                        String messageStr = AdditionalNotesEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(messageStr))
                            strHtmlMessage = getString(R.string.sms_partner_invitation_player_message, messageStr);

                        String strHtmlPlayerInfo = getString(R.string.sms_partner_invitation_player_info, mFromPlayer.Name, mFromPlayer.Phone, mFromPlayer.Email);

                        String smsBodySpanned = strSMSBody; //+ strHtmlMessage + strHtmlPlayerInfo;

                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + TextUtils.join(";", toNumbers)));
                        smsIntent.putExtra("sms_body", smsBodySpanned);
                        startActivity(Intent.createChooser(smsIntent, "Send Message"));
                        if (callback != null) {
                            callback.onSendClick();
                        }
                        dismiss();
                    }
                }
            }
        });
    }

    private void sendMailButtonSetup() {
        String nameString = "";
        final long[] recipient_ids = new long[((playerDetails != null) ? playerDetails.size() : 0)];

        if (playerDetails != null) {
            int count = 0;
            for (PlayerDetail player : playerDetails) {
                nameString = nameString + player.Email + ",";
                recipient_ids[count] = player.id;
                count++;
            }
        } else {
            setTitle("mail to : " + mToPlayer.Name);
            nameString = mToPlayer.Name;
        }
        final String nameStringFinal = nameString;
        final String[] recipentsEmails = nameString.split(",");

        setPositiveButton("Send Mail", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSuccess = true;

                String matchId = "";
                String competitionName = "";

                if (mCompetitionType == CompetitionType.league) {
                    Court court = (Court) CourtsSpinner.getSelectedItem();
                    if (court.getId() <= 0) {
                        isSuccess = false;
                        ((CustomSpinnerWithErrorAdapter) CourtsSpinner.getAdapter()).setError(CourtsSpinner.getSelectedView(), "Please select a Location");
                    } else {
                        matchId = String.valueOf(court.getId());
                        competitionName = court.getName();
                    }
                }

                StringBuilder days = new StringBuilder();
                for (CheckBox checkBox : DaysCheckBoxes) {
                    if (checkBox.isChecked()) {
                        if (days.length() > 0) days.append(", ");
                        days.append(checkBox.getTag());
                    }
                }

                if (days.length() == 0) {
                    Toast.makeText(getActivity(), "Please select preferred days of the week", Toast.LENGTH_LONG).show();
                    return;
                }

                StringBuilder times = new StringBuilder();
                for (CheckBox checkBox : TimesCheckBoxes) {
                    if (checkBox.isChecked()) {
                        if (times.length() > 0) times.append(", ");
                        times.append(checkBox.getTag());
                    }
                }

                if (times.length() == 0) {
                    Toast.makeText(getActivity(), "Please select preferred time of the day", Toast.LENGTH_LONG).show();
                    return;
                }

                if (isSuccess) {

                    String subjectStr = "";
                    Spanned emailBodySpanned = null;

                    Court preferredCourt = (mCompetitionType == CompetitionType.partner) ? mFromPlayer.HomeCourt : (Court) CourtsSpinner.getSelectedItem();
                    if (mToPlayer == null) {
                        if (mCompetitionType == CompetitionType.league || mCompetitionType == CompetitionType.tournament) {

                            if (playerDetails != null) {

                                String competitionType;

                                if (mCompetitionType == CompetitionType.league) {
                                    competitionType = "League";
                                } else {
                                    competitionType = "Tournament";
                                }

                                String[] daySelc = days.toString().split(",");
                                String[] timeSelc = times.toString().split(",");

                                String messageStr = AdditionalNotesEditText.getText().toString().trim();
                                if (mCompetition != null) {
                                    sendMail(competitionType, String.valueOf(mCompetition.CompetitionId), recipient_ids, competitionType, recipentsEmails, competitionName,
                                            matchId, daySelc, timeSelc, messageStr);
                                } else {
                                    sendMail(competitionType, String.valueOf(mCompetitionID), recipient_ids, competitionType, recipentsEmails, competitionName,
                                            matchId, daySelc, timeSelc, messageStr);
                                }
                                return;
                            }

                            subjectStr = getString(R.string.email_league_invitation_subject, new Prefs.AppData(getActivity()).getDomainName(), mFromPlayer.CityName, mFromPlayer.Name);

                            String strHtmlBody = getString(R.string.email_league_invitation_body, mToPlayer.Name,
                                    mCompetition.CompetitionName, preferredCourt.getName(),
                                    times.toString() + " on " + days.toString());

                            String strHtmlMessage = "";
                            String messageStr = AdditionalNotesEditText.getText().toString().trim();
                            if (!TextUtils.isEmpty(messageStr))
                                strHtmlMessage = getString(R.string.email_league_invitation_player_message, messageStr);

                            String strHtmlPlayerInfo = getString(R.string.email_league_invitation_player_info, mFromPlayer.Name, mFromPlayer.Phone, mFromPlayer.Email);

                            emailBodySpanned = Html.fromHtml(strHtmlBody + strHtmlMessage + strHtmlPlayerInfo);

                        } else if (mCompetitionType == CompetitionType.partner || mCompetitionType == CompetitionType.ladder) {

                            if (playerDetails != null) {

                                String competitionType;

                                if (mCompetitionType == CompetitionType.ladder) {
                                    competitionType = "Ladder";
                                } else {
                                    competitionType = "Partner";
                                }

                                String actionStr = ActionSpinner.getSelectedItem().toString();
                                if (ActionSpinner.getSelectedItemPosition() == 2)
                                    actionStr = "PLAY A MATCH or HIT AROUND";

                                String[] daySelc = days.toString().split(",");
                                String[] timeSelc = times.toString().split(",");

                                String messageStr = AdditionalNotesEditText.getText().toString().trim();

                                sendMail(competitionType, matchId, recipient_ids, competitionType, recipentsEmails, actionStr,
                                        String.valueOf(preferredCourt.getId()), daySelc, timeSelc, messageStr);

                                return;
                            }

                            if (mCompetitionType == CompetitionType.ladder) {
                                subjectStr = getString(R.string.email_ladder_invitation_subject, new Prefs.AppData(getActivity()).getDomainName(), mFromPlayer.Name);
                            } else {
                                subjectStr = getString(R.string.email_partner_invitation_subject, mFromPlayer.CityName, mFromPlayer.Name);
                            }

                            String actionStr = ActionSpinner.getSelectedItem().toString();
                            if (ActionSpinner.getSelectedItemPosition() == 2)
                                actionStr = "PLAY A MATCH or HIT AROUND";

                            String strHtmlBody = getString(R.string.email_partner_invitation_body,
                                    mToPlayer.Name, actionStr, preferredCourt.getName(),
                                    times.toString() + " on " + days.toString());

                            String strHtmlMessage = "";
                            String messageStr = AdditionalNotesEditText.getText().toString().trim();
                            if (!TextUtils.isEmpty(messageStr))
                                strHtmlMessage = getString(R.string.email_partner_invitation_player_message, messageStr);

                            String strHtmlPlayerInfo = getString(R.string.email_partner_invitation_player_info, mFromPlayer.Name, mFromPlayer.Phone, mFromPlayer.Email);

                            emailBodySpanned = Html.fromHtml(strHtmlBody + strHtmlMessage + strHtmlPlayerInfo);
                            dismiss();
                        }
                    } else {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mToPlayer.Email, null));
//                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", nameStringFinal, null));
                        intent.putExtra(Intent.EXTRA_CC, new String[] { mFromPlayer.Email,mSupportMail });
                        intent.putExtra(Intent.EXTRA_SUBJECT, subjectStr);
                        intent.putExtra(Intent.EXTRA_TEXT, emailBodySpanned);

                        startActivity(Intent.createChooser(intent, "Send Email"));
                    }
                }

            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
//        hideSoftKeyboard();
    }

    @Override
    protected void postCreateViewSetup(View v) {
        super.postCreateViewSetup(v);
        ButterKnife.bind(this, v);

        if (mCompetitionType == CompetitionType.league || mCompetitionType == CompetitionType.tournament) {

            v.findViewById(R.id.vwAction).setVisibility(View.GONE);
            v.findViewById(R.id.vwLocation).setVisibility(View.VISIBLE);

            if (mCourts == null) throw new RuntimeException("Missing 'Courts'");

            CourtsSpinner.setAdapter(new CustomSpinnerWithErrorAdapter.CourtsAdapter(getActivity(), mCourts));

            for (int i = 0; i < mCourts.size(); i++)
                if (mFromPlayer.HomeCourt.getId() == mCourts.get(i).getId()) {
                    CourtsSpinner.setSelection(i);
                    break;
                }

        } else if (mCompetitionType == CompetitionType.ladder) {

            v.findViewById(R.id.vwAction).setVisibility(View.VISIBLE);
            v.findViewById(R.id.vwLocation).setVisibility(View.VISIBLE);

            if (mCourts == null) throw new RuntimeException("Missing 'Courts'");

            CourtsSpinner.setAdapter(new CustomSpinnerWithErrorAdapter.CourtsAdapter(getActivity(), mCourts));

            for (int i = 0; i < mCourts.size(); i++)
                if (mFromPlayer.HomeCourt.getId() == mCourts.get(i).getId()) {
                    CourtsSpinner.setSelection(i);
                    break;
                }

            String[] actions = getResources().getStringArray(mCompetitionType == CompetitionType.ladder ? R.array.arr_actions_ladder_match : R.array.arr_actions);
            ActionSpinner.setAdapter(new CustomSpinnerWithErrorAdapter<>(getActivity(), actions));

        } else if (mCompetitionType == CompetitionType.partner) {

            v.findViewById(R.id.vwAction).setVisibility(View.VISIBLE);
            v.findViewById(R.id.vwLocation).setVisibility(View.GONE);

            String[] actions = getResources().getStringArray(mCompetitionType == CompetitionType.ladder ? R.array.arr_actions_ladder_match : R.array.arr_actions);
            ActionSpinner.setAdapter(new CustomSpinnerWithErrorAdapter<>(getActivity(), actions));

        }

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    protected int getCustomContentLayout() {
        return R.layout.dlg_send_mail;
    }

    @Override
    protected boolean isForceWrap() {
        return false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mRealm = Realm.getInstance(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mRealm.close();
        mRealm = null;
    }

    public void sendMail(String type, String matchId, long[] recipentsIds,
                         String programName, String[] recipientsEmails, String playType, String courtId,
                         String[] daysSelected, String[] timeSelected, String notes) {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), null, "Please wait...", true, false);

        Request divisionReportRequest = WSHandle.SendMail.sendMailRequest(App.sOAuth, type, matchId, recipentsIds, programName,
                recipientsEmails, playType, courtId, daysSelected, timeSelected, notes,
                new VolleyHelper.IRequestListener<JSONObject, String>() {
                    @Override
                    public void onFailureResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccessResponse(JSONObject response) {
                        progressDialog.dismiss();
                        if (callback != null) {
                            callback.onSendClick();
                        }
                        try {
                            String message = response.getString("responseMessage");
                            if (message != null) {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }

                        dismiss();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    }
                });
        if (divisionReportRequest != null)
            VolleyHelper.getInstance(getActivity()).addToRequestQueue(divisionReportRequest);
        else
            progressDialog.dismiss();
    }
}
