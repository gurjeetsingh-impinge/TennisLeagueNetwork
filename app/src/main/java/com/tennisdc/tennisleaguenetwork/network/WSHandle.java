package com.tennisdc.tennisleaguenetwork.network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.common.App;
import com.tennisdc.tennisleaguenetwork.common.GsonRealmExclusionStrategy;
import com.tennisdc.tennisleaguenetwork.model.ChallengeIncoming;
import com.tennisdc.tennisleaguenetwork.model.ChallengeOutgoing;
import com.tennisdc.tennisleaguenetwork.model.Competition;
import com.tennisdc.tennisleaguenetwork.model.CompetitionType;
import com.tennisdc.tennisleaguenetwork.model.CouponDetails;
import com.tennisdc.tennisleaguenetwork.model.Court;
import com.tennisdc.tennisleaguenetwork.model.Location;
import com.tennisdc.tennisleaguenetwork.model.MatchStats;
import com.tennisdc.tennisleaguenetwork.model.NameIdPair;
import com.tennisdc.tennisleaguenetwork.model.PlayerDetail;
import com.tennisdc.tennisleaguenetwork.model.Program;
import com.tennisdc.tennisleaguenetwork.model.SkillLevel;
import com.tennisdc.tennisleaguenetwork.model.TournamentRound;
import com.tennisdc.tennisleaguenetwork.model.TournamentRoundDetails;
import com.tennisdc.tennisleaguenetwork.model.UserDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WSHandle {

    /* Post Services */
    private final static String WS_CREATE_ACCOUNT = "create_account.json";
    private final static String WS_LOGIN = "login.json";
    private final static String WS_LOGOUT = "logout.json";
    private final static String WS_UPDATE = "get_player_update.json";
    private final static String WS_FORGOT_PASS = "forget_password.json";
    private final static String WS_PROFILE_DETAILS = "profile_details";
    private final static String WS_CREATE_REFERRALS = "create_referral";

    /* Get Services */
    private final static String WS_PREFERRED_LEVEL_DATA = "preferred_level_data.json";
    private final static String WS_PARTNERS_PREFERRED_LEVEL_DATA = "preferred_level_doubles.json";
    private final static String WS_USTA_RANK_DATA = "usta_rank_data.json";

    public static class Login {

        public static void createAccount(String fName, String lName, String email, String pass, String zip, String mobile, String skillId, String ustaRank,
                                         String dateString,String doublesInterest,String partnerSkillId,String partnersFName,String partnerLName, String partnersEmail, String referralMethod ,final VolleyHelper.IRequestListener<JSONObject, String> requestListener) {
            //WSCore wsCore = new WSCore();

            JSONObject params = new JSONObject();
            try {
                JSONObject data = new JSONObject();
                data.put("first_name", fName);
                data.put("last_name", lName);
                data.put("email_address", email);
                data.put("password", pass);
                data.put("password_confirmation", pass);
                data.put("zipcode", zip);

                data.put("primary_telephone_number", mobile);
                data.put("preferred_skill_id", skillId);
//                data.put("usta_rank", ustaRank);
                data.put("privacy_policy", true);
                data.put("terms", "1");

                data.put("dob", dateString);

//                data.put("doubles_interest", doublesInterest);
                data.put("partner_first_name", partnersFName);
                data.put("partner_last_name", partnerLName);
                data.put("partner_email", partnersEmail);
                data.put("doubles_skill_id", partnerSkillId);
                data.put("referral_method",referralMethod);

                params.put("player", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + WS_CREATE_ACCOUNT, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("responseCode").equals("200")) {
                            requestListener.onSuccessResponse(response);
                        } else {
                            requestListener.onFailureResponse(response.getString("responseMessage"));
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);


            /*String response = wsCore.Post(WS_CREATE_ACCOUNT, jsonObject, null);

            try {
                return new JSONObject(response);
            } catch (JSONException|NullPointerException e) {
                e.printStackTrace();
            }

            return null;*/
        }

        public static void login(String email, String pass, final VolleyHelper.IRequestListener<JSONObject, String> requestListener) {
            //WSCore wsCore = new WSCore();

            JSONObject params = new JSONObject();
            try {
                params.put("email_address", email);
                params.put("password", pass);
                params.put("allow_banned_user",true);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "login.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("responseCode").equals("200")) {
                            requestListener.onSuccessResponse(response);
                        } else {
                            requestListener.onFailureResponse(response.getString("responseMessage"));
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);

            /*String response = wsCore.Post(WS_LOGIN, data, null);

            try {
                return new JSONObject(response);
            } catch (JSONException|NullPointerException e) {
                e.printStackTrace();
            }*/

            //return null;
        }



        public static void updatePlayer(String oAuthToken, final VolleyHelper.IRequestListener<JSONObject, String> requestListener) {

            //WSCore wsCore = new WSCore();

            JSONObject data = new JSONObject();
            try {
                data.put("oauth_token", oAuthToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + WS_UPDATE, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("responseCode").equals("200")) {
                            requestListener.onSuccessResponse(response);
                        } else {
                            requestListener.onFailureResponse(response.getString("responseMessage"));
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);
        }

        public static void logout(String oAuthToken, final VolleyHelper.IRequestListener<String, String> requestListener) {

            //WSCore wsCore = new WSCore();

            JSONObject data = new JSONObject();
            try {
                data.put("oauth_token", oAuthToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + WS_LOGOUT, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("responseCode").equals("200")) {
                            requestListener.onSuccessResponse("ok");
                        } else {
                            requestListener.onFailureResponse(response.getString("responseMessage"));
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);

            //String response = wsCore.Post(WS_LOGOUT, data, null);

            /*try {
                return new JSONObject(response);
            } catch (JSONException|NullPointerException e) {
                e.printStackTrace();
            }

            return null;*/
        }

        public static void forgotPass(String email, final VolleyHelper.IRequestListener<String, String> requestListener) {
            //WSCore wsCore = new WSCore();

            JSONObject params = new JSONObject();
            try {
                params.put("email_address", email);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + WS_FORGOT_PASS, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("responseCode").equals("200")) {
                            requestListener.onSuccessResponse("ok");
                        } else {
                            requestListener.onFailureResponse(response.getString("responseMessage"));
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);

            /*String response = wsCore.Post(WS_FORGOT_PASS, params, null);

            try {
                return new JSONObject(response);
            } catch (JSONException|NullPointerException e) {
                e.printStackTrace();
            }

            return null;*/
        }

        public static void getSkillLevels(final VolleyHelper.IRequestListener<List<SkillLevel>, String> requestListener) {
            //WSCore wsCore = new WSCore();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + WS_PREFERRED_LEVEL_DATA, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.has("preferred_level_data") ){ //(response.getString("responseCode").equals("200")) {
                            Type listType = new TypeToken<List<SkillLevel>>() {}.getType();
                            List<SkillLevel> posts = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("preferred_level_data"), listType);

                            requestListener.onSuccessResponse(posts);
                        } else {
                            requestListener.onFailureResponse(response.getString("responseMessage"));
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);

            /*String response = wsCore.Get(WS_PREFERRED_LEVEL_DATA, null);

            try {
                return new JSONObject(response);
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }*/

            //return null;
        }

        public static void getPartnersSkillLevels(final VolleyHelper.IRequestListener<List<SkillLevel>, String> requestListener) {

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + WS_PARTNERS_PREFERRED_LEVEL_DATA, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    try {
                        if (response.has("preferred_level_doubles") ){ //(response.getString("responseCode").equals("200")) {
                            Type listType = new TypeToken<List<SkillLevel>>() {}.getType();
                            List<SkillLevel> posts = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("preferred_level_doubles"), listType);

                            requestListener.onSuccessResponse(posts);
                        } else {
                            requestListener.onFailureResponse(response.getString("responseMessage"));
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);
        }


        /*public static JSONObject GetUstaRankData() {
            WSCore wsCore = new WSCore();

            String response = wsCore.Get(WS_USTA_RANK_DATA, null);

            try {
                return new JSONObject(response);
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

            return null;
        }*/

        public static void getUserInfo(final VolleyHelper.IRequestListener<PlayerDetail, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "score_form.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("responseCode").equals("200")) {
                            PlayerDetail playerDetail = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("player"), new TypeToken<PlayerDetail>() {}.getType());
                            requestListener.onSuccessResponse(playerDetail);
                        } else {
                            requestListener.onFailureResponse(response.getString("responseMessage"));
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);
        }
    }

    public static class SubmitScore {
        public static Request getCompetitionListRequest(String oAuthToken, final VolleyHelper.IRequestListener<List<NameIdPair>, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", oAuthToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "competition_list.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<List<NameIdPair>>() {}.getType();
                                List<NameIdPair> competitions = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("competitions"), listType);
                                requestListener.onSuccessResponse(competitions);
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };
        }

        public static Request getCompetitorsRequest(String oAuthToken, long competitionId, final VolleyHelper.IRequestListener<JSONObject, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", oAuthToken);
                params.put("id", competitionId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "score_form.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        Log.d("response", response.toString());
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response);
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };
        }

        public static Request getSubmitScoreRequest(String oAuthToken, MatchStats matchStats, final VolleyHelper.IRequestListener<String, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                JSONObject score = new JSONObject();

                score.put("competition_id", matchStats.Competition.Id);
                score.put("court_id", matchStats.Court.getId());

                score.put("match_type", matchStats.Competition.Name);
                score.put("match_date", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date(matchStats.Date)));// "2007-04-18 23:00:00");
                score.put("match_hour", matchStats.Hours);
                score.put("match_minute", matchStats.Minutes);

                score.put("scoring_type", matchStats.getScoreFormat().getInternalName());

                score.put("winner_id", matchStats.Winner.Player.Id);
                score.put("winner_set1_games", matchStats.Winner.getSet1());
                score.put("winner_set2_games", matchStats.Winner.getSet2());
                score.put("winner_set3_games", matchStats.Winner.getSet3());
                score.put("winner_set3_tie_games", matchStats.Winner.getSet3Tie());

                score.put("loser_id", matchStats.Looser.Player.Id);
                score.put("loser_set1_games", matchStats.Looser.getSet1());
                score.put("loser_set2_games", matchStats.Looser.getSet2());
                score.put("loser_set3_games", matchStats.Looser.getSet3());
                score.put("loser_set3_tie_games", matchStats.Looser.getSet3Tie());

                score.put("tie", matchStats.IsTie ? "1" : "0");
                score.put("retired", matchStats.IsRetired ? "1" : "0");
                score.put("late_cancel", matchStats.IsLateCancel ? "1" : "0");
                score.put("no_show", matchStats.IsNoShow ? "1" : "0");
                score.put("set_tie", matchStats.IsHitAround ? "1" : "0");

                score.put("comment", matchStats.Comment);

                params.put("oauth_token", oAuthToken);
                params.put("score", score);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "save_score.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };
        }

    }

    public static class Leagues {

        public static Request getCompetitionListRequest(long playerId, final VolleyHelper.IRequestListener<List<Competition>, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("player_id", playerId);
                params.put("competition_type", CompetitionType.league);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "player_competitions_list.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<List<Competition>>() {}.getType();
                                List<Competition> competitions = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("competitions"), listType);

                                for (Competition competition : competitions)
                                    competition.Type = CompetitionType.league;

                                requestListener.onSuccessResponse(competitions);
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };
        }

        public static Request getDetailsRequest(String oAuthToken, long divisionId, final VolleyHelper.IRequestListener<JSONObject, String> requestListener) {
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", oAuthToken);
                params.put("division_id", divisionId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "division_report.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response);
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

        }

        public static void getLeagueDivisions(long competitionId, final VolleyHelper.IRequestListener<List<NameIdPair>, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("competition_id", competitionId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "league_divisions.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<List<NameIdPair>>() {}.getType();
                                List<NameIdPair> locations = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("divisionList"), listType);

                                requestListener.onSuccessResponse(locations);
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                            requestListener.onFailureResponse(e.getMessage());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }
    }

    public static class Alerts {

        public static Request getDetailsRequest(final String oAuth, final VolleyHelper.IRequestListener<JSONObject, String> iRequestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", oAuth);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return  new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "upcoming_programs.json", params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("LOG", "+++++++++ success");
                            if (response != null) {
                                iRequestListener.onSuccessResponse(response);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("LOG", "--------- error");
                            iRequestListener.onError(error);
                        }
                    });
        }

        public static Request updateToNotShow(final String oAuth, boolean mobilePopup, final VolleyHelper.IRequestListener<JSONObject, String> iRequestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", oAuth);
                params.put("mobile_slider_popup_setting", mobilePopup);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return  new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "upcoming_programs_popup_setting.json", params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("LOG", "+++++++++ success");
                            if (response != null) {
                                iRequestListener.onSuccessResponse(response);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("LOG", "--------- error");
                            iRequestListener.onError(error);
                        }
                    });
        }
    }

    public static class PartnerProgram {

        public static Request getDetailsRequest(final String oAuth, long playerId, final VolleyHelper.IRequestListener<JSONObject, String> iRequestListener) {

            final long playerId1 = playerId;
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", oAuth);
                params.put("player_id", playerId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "partners_program_standing.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200") || response.getString("responseCode").equals("404")) {
                                iRequestListener.onSuccessResponse(response);
                            } else {
                                iRequestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    iRequestListener.onError(error);
                    Log.e("ERROR", "Error occurred ", error);
                }
            }){
                @Override
                public String getBodyContentType()
                {
                    return "application/json";
                }
            };

//            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
//                    WSCore._URL + "/" + "partners_program_standing.json", params,
//                    new PotyResponse.Listener<JSONObject>() {
//
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            Log.d("printed value response", response.toString());
//                            if (response != null) {
//                                try {
//                                    if (response.getString("responseCode").equals("200")) {
//                                        iRequestListener.onSuccessResponse(response);
//                                    } else {
//                                        iRequestListener.onFailureResponse(response.getString("responseMessage"));
//                                    }
//                                } catch (JSONException | NullPointerException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }, new PotyResponse.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    VolleyLog.d("", "Error: " + error.getMessage());
//                }
//            }) {
//
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("oauth_token", oAuth);
//                    params.put("player_id",String.valueOf(playerId1));
//
//                    return params;
//                }
//
//            };
//            return jsonObjReq;
        }
    }

    public static class SendMail {

        public static Request sendMailRequest(String oAuth, String type, String matchId, long[] recipientsIds,
                                              String programName, String[] recipientsEmails, String playType, String courtId,
                                              String[] daysSelected, String[] timeSelected,String notes,
                                              final VolleyHelper.IRequestListener<JSONObject, String> iRequestListener) {
            JSONObject params = new JSONObject();

            try {
                params.put("oauth_token", oAuth);
                params.put("type", type);
                params.put("id", matchId);
                params.put("recipient_ids", new JSONArray(Arrays.asList(recipientsIds)));
                params.put("prog_name", programName);
                params.put("recipient_emails", new JSONArray(Arrays.asList(recipientsEmails)));
                params.put("play", playType);
                params.put("court", courtId);
                params.put("days", new JSONArray(Arrays.asList(daysSelected)));
                params.put("time", new JSONArray(Arrays.asList(timeSelected)));
                params.put("notes", notes);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("response", params.toString());

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "send_email.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                iRequestListener.onSuccessResponse(response);
                            } else {
                                iRequestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    iRequestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

        }
    }

    public static class Ladder {

        public static Request getDetailsRequest(String oAuth, final VolleyHelper.IRequestListener<JSONObject, String> iRequestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", oAuth);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "mobile_tennis_ladder.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            String responseCode = response.getString("responseCode");
                            if (responseCode.equals("200") || responseCode.equals("404")) {
                                iRequestListener.onSuccessResponse(response);
                            } else {
                                iRequestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    iRequestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

        }
    }

    public static class LadderChallenge {

        public static Request getCreateChallengeRequest(String oAuth, long opponentId, final VolleyHelper.IRequestListener<String, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", oAuth);
                params.put("opponent_id", opponentId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "mob_create_challenge.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

        }

        public static Request getCancelRequest(String oAuth, ChallengeOutgoing challengeOutgoing, final VolleyHelper.IRequestListener<String, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", oAuth);
                params.put("opponent_id", challengeOutgoing.OpponentId);
                params.put("status", challengeOutgoing.Status);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "mob_cancel_current_challenge.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

        }

        public static Request getAcceptRequest(String oAuth, ChallengeIncoming challengeIncoming, final VolleyHelper.IRequestListener<String, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", oAuth);
                params.put("player_id", challengeIncoming.FromPlayerId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "mob_accept_challenge.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

        }

        public static Request getDeclineRequest(String oAuth, ChallengeIncoming challengeIncoming, final VolleyHelper.IRequestListener<String, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", oAuth);
                params.put("player_id", challengeIncoming.FromPlayerId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "mob_decline_challenge.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

        }

    }

    public static class Courts {

        public static void getLocationAndRegions(final VolleyHelper.IRequestListener<List<Location>, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "locations_with_court_regions.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<List<Location>>() {}.getType();
                                List<Location> locations = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("locationList"), listType);

                                requestListener.onSuccessResponse(locations);
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                            requestListener.onFailureResponse(e.getMessage());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void getCourts(final VolleyHelper.IRequestListener<List<Court>, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "courts_list.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<List<Court>>() {}.getType();
                                List<Court> courts = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("courts"), listType);

                                requestListener.onSuccessResponse(courts);
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                            requestListener.onFailureResponse(e.getMessage());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);
        }

        public static void getCourtDetails(long courtId, final VolleyHelper.IRequestListener<Court, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("court_id", courtId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "mob_court_display.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<Court>() {}.getType();
                                Court courts = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("court_details"), listType);

                                requestListener.onSuccessResponse(courts);
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                            requestListener.onFailureResponse(e.getMessage());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);
        }

        public static void searchCourts(String zipCode, final VolleyHelper.IRequestListener<List<Court>, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("zipcode", zipCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "mob_courts_listing.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<List<Court>>() {}.getType();
                                List<Court> courts = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("courts_list"), listType);

                                requestListener.onSuccessResponse(courts);
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void searchCourts(double latitude, double longitude, final VolleyHelper.IRequestListener<List<Court>, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("latitude", latitude);
                params.put("longitude", longitude);
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "mob_courts_listing.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<List<Court>>() {}.getType();
                                List<Court> courts = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("courts_list"), listType);

                                requestListener.onSuccessResponse(courts);
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void createCourts(Court court, final VolleyHelper.IRequestListener<Court, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                JSONObject courtJsonObject = new JSONObject();
                courtJsonObject.put("name", court.getName());
                courtJsonObject.put("location_id", court.getLocationId());
                courtJsonObject.put("court_region_id", court.getRegionId());
                courtJsonObject.put("is_club", court.getIsClub());
                courtJsonObject.put("has_store", court.getHasStore());
                courtJsonObject.put("has_stringer", court.getHasStringer());
                courtJsonObject.put("total_courts", court.getTotalCourts());
                courtJsonObject.put("indoor_courts", court.getIndoorCourts());
                courtJsonObject.put("clay_courts", court.getClayCourts());
                courtJsonObject.put("lighted_courts", court.getLightedCourts());
                courtJsonObject.put("has_hitting_wall", court.getHasHittingWall());
                courtJsonObject.put("restricted", court.getRestricted());
                courtJsonObject.put("bathroom_available", court.getHasBathroom());
                courtJsonObject.put("water_access", court.getHasWater());
                courtJsonObject.put("has_fee", court.getHasFee());
                courtJsonObject.put("fee", court.getFee());
                courtJsonObject.put("phone_number", court.getPhone());
                courtJsonObject.put("address", court.getAddress());
                courtJsonObject.put("city", court.getCity());
                courtJsonObject.put("state", court.getState());
                courtJsonObject.put("zip_code", court.getZipCode());
                courtJsonObject.put("website", court.getWebsite());
                courtJsonObject.put("notes", court.getNotes());

                params.put("oauth_token", App.sOAuth);
                params.put("court", courtJsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "mob_create_court.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<Court>() {}.getType();
                                Court court = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("court"), listType);

                                requestListener.onSuccessResponse(court);
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

    }

    public static class ErrorHandler {

        public static void mailError(JSONArray uncaughtExceptions, final VolleyHelper.IRequestListener<String, String> requestListener) {
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("error_arr", uncaughtExceptions);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "error_report_mailer1.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                            requestListener.onFailureResponse(e.getMessage());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
return;
//            JSONObject params = new JSONObject();
//            try {
//                params.put("oauth_token", App.sOAuth);
//                params.put("error_arr", uncaughtExceptions);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "error_report_mailer.json", params, new PotyResponse.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    if (response != null) {
//                        try {
//                            if (response.getString("responseCode").equals("200")) {
//                                requestListener.onSuccessResponse(response.getString("responseMessage"));
//                            } else {
//                                requestListener.onFailureResponse(response.getString("responseMessage"));
//                            }
//                        } catch (JSONException | NullPointerException e) {
//                            e.printStackTrace();
//                            requestListener.onFailureResponse(e.getMessage());
//                        }
//                    }
//                }
//            }, new PotyResponse.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    requestListener.onError(error);
//                }
//            });
//
//            VolleyHelper.getInstance().addToRequestQueue(request);
        }

    }

    public static class Settings {

        public static void communicationSettings(boolean canCall, boolean textCall, final VolleyHelper.IRequestListener<String, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("call", String.valueOf(canCall));
                params.put("text", String.valueOf(textCall));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "mobile_settings.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                            requestListener.onFailureResponse(e.getMessage());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

    }

    public static class Tournament {

        public static void getCompetitionList(long playerId, final VolleyHelper.IRequestListener<List<Competition>, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("player_id", playerId);
                params.put("competition_type", CompetitionType.tournament);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "player_competitions_list.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<List<Competition>>() {}.getType();
                                List<Competition> competitions = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("competitions"), listType);

                                for (Competition competition : competitions)
                                    competition.Type = CompetitionType.tournament;

                                requestListener.onSuccessResponse(competitions);
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void getTournamentReport(long tournamentId, final VolleyHelper.IRequestListener<JSONObject, String> iRequestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("tournament_id", tournamentId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "tournament_report.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                iRequestListener.onSuccessResponse(response);
                            } else {
                                iRequestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    iRequestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void getTournamentDetailsList(long tournamentId, final VolleyHelper.IRequestListener<List<TournamentRound>, String> iRequestListener) {
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("tournament_id", tournamentId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        //    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "tournament_report_bracket.json", params, new PotyResponse.Listener<JSONObject>() {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "tournament_report_bracket2.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                   Log.e("PotyResponse",response.toString());
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<List<TournamentRoundDetails>>() {
                                }.getType();
                              /*  List<TournamentRound> tournamentsList = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("tournaments"), listType);
                                iRequestListener.onSuccessResponse(tournamentsList);
*/
                                // ----- Temp ----

                               ArrayList<TournamentRound> tournamentsList = new ArrayList<TournamentRound>();
                                if(response.opt("tournaments")!=null) {
                                    JSONArray tarray = response.getJSONArray("tournaments");
                                    for(int i=0;i<tarray.length();i++) {
                                        JSONObject tobj = (JSONObject)tarray.get(i);
                                        TournamentRound round = new TournamentRound();
                                        round.setTournamentTitle(tobj.getString("title"));
                                        round.setTournamentDate(tobj.getString("date"));
                                        round.setOpenDefault(tobj.getBoolean("open_default"));
                                        List<TournamentRoundDetails> matchList = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(tobj.getString("matches"), listType);
                                        if((matchList!=null) && (matchList.size() > 0)) {
                                            round.setMatchesList(matchList);
                                        }
                                        tournamentsList.add(round);
                                    }
                                }
                                iRequestListener.onSuccessResponse(tournamentsList);
                            }
                            else {
                                iRequestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    iRequestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

    }

    public static class Buy {

        public static void getProgramList(final VolleyHelper.IRequestListener<List<Program>, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "programs_list.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<List<Program>>() {}.getType();
                                List<Program> programs = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("programs_details"), listType);

                                requestListener.onSuccessResponse(programs);
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void getPromoCodeDetails(final String couponCode, final VolleyHelper.IRequestListener<CouponDetails, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("coupon_code", couponCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "promo_code.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<CouponDetails>() {}.getType();
                                CouponDetails couponDetails = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("discount_details"), listType);

                                couponDetails.promoCode = couponCode;

                                requestListener.onSuccessResponse(couponDetails);
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void getFreeMonth(long id, String type, final VolleyHelper.IRequestListener<String, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("id", id);
                params.put("type", type);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "submit_free_month.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse("Error occurred");
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void submitPaymentDetails(JSONObject paymentDetails, final VolleyHelper.IRequestListener<String, String> requestListener) {

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "submit_order.json", paymentDetails, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse("Error occurred");
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }
    }

    public static class Profile {

        public static void getUserDetails(final VolleyHelper.IRequestListener<UserDetails, String> requestListener) {
            JSONObject data = new JSONObject();
            try {
                data.put("oauth_token", App.sOAuth);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + WS_PROFILE_DETAILS, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("player") != null) {// (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<UserDetails>() {
                                }.getType();
                                UserDetails details = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("player"), listType);
                                requestListener.onSuccessResponse(details);
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("app_type","Android");
                return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void updateProfileName(String fname, String lname, final VolleyHelper.IRequestListener<String, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("first_name", fname);
                params.put("last_name", lname);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "change_name.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                            requestListener.onFailureResponse("Error occurred");
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void updateProfileEmail(String oldEmail, String newEmail, String confirmEmail, final VolleyHelper.IRequestListener<String, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("old_email", oldEmail);
                params.put("new_email", newEmail);
                params.put("confirm_email", confirmEmail);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "change_email.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                            requestListener.onFailureResponse("Error occurred");
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void updateProfilePassword(String currPasswd, String newPasswd, String confirmPasswd, final VolleyHelper.IRequestListener<String, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("password", newPasswd);
                params.put("password_confirmation", confirmPasswd);
                params.put("current_password", currPasswd);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "change_password.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                            requestListener.onFailureResponse("Error occurred");
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void updateProfileDetails(JSONObject params, final VolleyHelper.IRequestListener<String, String> requestListener) {

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "profile_update.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                            requestListener.onFailureResponse("Error occurred");
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }
    }

    public static class poty {

        public static Request getPotyRequest(final String oAuth, int year, final VolleyHelper.IRequestListener<JSONObject, String> iRequestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", oAuth);
                params.put("year", year);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "player_of_the_year.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        /*try {
                            if (response.getString("responseCode").equals("200") || response.getString("responseCode").equals("404")) {
                                iRequestListener.onSuccessResponse(response);
                            } else {
                                iRequestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }*/
                        iRequestListener.onSuccessResponse(response);
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    iRequestListener.onError(error);
                    Log.e("ERROR", "Error occurred ", error);
                }
            }){
                @Override
                public String getBodyContentType()
                {
                    return "application/json";
                }
            };

        }
    }

    public static class Referral {

        public static void createReferrals(JSONObject params, final VolleyHelper.IRequestListener<String, String> requestListener) {

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + WS_CREATE_REFERRALS, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                            requestListener.onFailureResponse("Error occurred");
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type","Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }
    }
}