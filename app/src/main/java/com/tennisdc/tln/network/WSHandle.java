package com.tennisdc.tln.network;

import static com.tennisdc.tln.network.WSCore.paymentUrl;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

//import com.android.volley.AuthFailureError;
import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
//import com.android.volley.Volley;
import com.android.volley.VolleyError;
//import com.android.volley.error.AuthFailureError;
//import com.android.volley.request.JsonObjectRequest;
//import com.android.volley.request.SimpleMultiPartRequest;
//import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.interfaces.VolleyMultipartRequest;
import com.tennisdc.tln.model.ChallengeIncoming;
import com.tennisdc.tln.model.ChallengeOutgoing;
import com.tennisdc.tln.model.Competition;
import com.tennisdc.tln.model.CompetitionType;
import com.tennisdc.tln.model.CouponDetails;
import com.tennisdc.tennisleaguenetwork.model.Court;
import com.tennisdc.tln.model.CourtDetailModel;
import com.tennisdc.tln.model.CourtMainModel;
import com.tennisdc.tennisleaguenetwork.model.Location;
import com.tennisdc.tln.model.HeadToHeadData;
import com.tennisdc.tln.model.LeagueSwagItems;
import com.tennisdc.tln.model.MatchStats;
import com.tennisdc.tln.model.MatchesPlayedModel;
import com.tennisdc.tln.model.NameIdPair;
import com.tennisdc.tln.model.PlayerDetail;
import com.tennisdc.tln.model.PreOrderResponse;
import com.tennisdc.tln.model.Program;
import com.tennisdc.tln.model.SkillLevel;
import com.tennisdc.tln.model.Swag_items;
import com.tennisdc.tln.model.TournamentRound;
import com.tennisdc.tln.model.TournamentRoundDetails;
import com.tennisdc.tln.model.UTRConfigurationModel;
import com.tennisdc.tln.model.UpcomingTournamentModel;
import com.tennisdc.tln.model.UserDetails;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final static String WS_GET_STATE_CODES = "getStateCodes";
    private final static String WS_CHECK_FORCE_UPDATE = "check_force_update";
    private final static String WS_LATEST_SCORE = "latest_scores.json";
    private final static String WS_DELETE_DATA = "players/delete_my_data";
    private final static String WS_SWAG_PROGRAM_LIST = "swag_programs_list";

    /* Get Services */
    private final static String WS_PREFERRED_LEVEL_DATA = "preferred_level_data.json";
    private final static String WS_PARTNERS_PREFERRED_LEVEL_DATA = "preferred_level_doubles.json";
    private final static String WS_USTA_RANK_DATA = "usta_rank_data.json";
    private final static String WS_HEAD_TO_HEAD_DATA = "head_to_head";

    private final static String WS_SWAG_ITEMS = "swag_items";




    public static class Login {

        public static void createAccount(String fName, String lName, String email, String pass, String zip, String domainId, String mobile, String skillId, String ustaRank,
                                         String dateString, String doublesInterest, String partnerSkillId, String partnersFName,
                                         String partnerLName, String partnersEmail, String referralMethod, String state, String city,
                                         final VolleyHelper.IRequestListener<JSONObject, String> requestListener) {
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
                data.put("location_id", domainId);

                data.put("primary_telephone_number", mobile);
                data.put("preferred_skill_id", skillId);
                data.put("usta_rank", ustaRank);
                data.put("privacy_policy", true);
                data.put("terms", "1");

                data.put("dob", dateString);

                data.put("doubles_interest", doublesInterest);
                data.put("partner_first_name", partnersFName);
                data.put("partner_last_name", partnerLName);
                data.put("partner_email", partnersEmail);
                data.put("doubles_skill_id", partnerSkillId);
                data.put("referral_method", referralMethod);

                data.put("state", state);
                data.put("city", city);

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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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

        public static void getStateCodes(final VolleyHelper.IRequestListener<JSONObject, String> requestListener) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, WSCore._URL + "/" + WS_GET_STATE_CODES, null, new Response.Listener<JSONObject>() {
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);

        }

        public static void login(String email, String pass, String token, String device_type, final VolleyHelper.IRequestListener<JSONObject, String> requestListener) {
            //WSCore wsCore = new WSCore();

            JSONObject params = new JSONObject();
            try {
                params.put("email_address", email);
                params.put("password", pass);
                params.put("allow_banned_user", true);
                params.put("token", token);
                params.put("device_type", device_type);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);
        }

        public static void checkForceUpdate(String oAuthToken, final VolleyHelper.IRequestListener<JSONObject, String> requestListener) {

            //WSCore wsCore = new WSCore();

            JSONObject data = new JSONObject();
            try {
                data.put("oauth_token", oAuthToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + WS_CHECK_FORCE_UPDATE, data, new Response.Listener<JSONObject>() {
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, WSCore._URL + "/" + WS_PREFERRED_LEVEL_DATA, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.has("preferred_level_data")) { //(response.getString("responseCode").equals("200")) {
                            Type listType = new TypeToken<List<SkillLevel>>() {
                            }.getType();
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
                        if (response.has("preferred_level_doubles")) { //(response.getString("responseCode").equals("200")) {
                            Type listType = new TypeToken<List<SkillLevel>>() {
                            }.getType();
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + WS_UPDATE, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("responseCode").equals("200")) {
                            PlayerDetail playerDetail = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("player"), new TypeToken<PlayerDetail>() {
                            }.getType());
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
                                Type listType = new TypeToken<List<NameIdPair>>() {
                                }.getType();
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
        }

        public static Request getSubmitScoreRequest(String oAuthToken, MatchStats matchStats, boolean allow_same_score, final VolleyHelper.IRequestListener<JSONObject, JSONObject> requestListener) {

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

                score.put("Comment", matchStats.Comment);

                params.put("oauth_token", oAuthToken);
                params.put("score", score);
                if (allow_same_score)
                    params.put("allow_same_score", allow_same_score);
//                params.put("test", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "save_score.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response);
                            } else {
                                requestListener.onFailureResponse(response/*.getString("responseMessage")*/);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };

//            return null;
        }
    }

    public static class Leagues {

        public static Request getMatchesPlayedRequest(int mDate, int mYear,
                                                      final VolleyHelper.IRequestListener<JSONObject, String> requestListener) {
//            HttpsTrustManager.allowAllSSL();
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String mUrl = WSCore._URL + "/" + "MatchesPlayedContest/" + mDate + "-" + mYear;
            return new JsonObjectRequest(Request.Method.POST, mUrl, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                //     Type listType = new TypeToken<List<MatchesPlayedModel>>() {
                                //     }.getType();
                                //     MatchesPlayedModel competitions = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.toString(), listType);

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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
        }


        public static Request getCompetitionListRequest(long playerId, final VolleyHelper.IRequestListener<List<Competition>, String> requestListener) {
//            HttpsTrustManager.allowAllSSL();
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
                                Type listType = new TypeToken<List<Competition>>() {
                                }.getType();
                                List<Competition> competitions = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("competitions"), listType);
//								List<Competition> competitions = new Gson().fromJson(response.getString("competitions"),listType);
                             /*   List<Competition> competitions = new ArrayList<>();
                                JSONArray mCompetitionsJsonArray = new JSONArray(response.getString("competitions"));
                                for (int i = 0; i < mCompetitionsJsonArray.length(); i++) {
                                    JSONObject mCompetitionsObject = mCompetitionsJsonArray.getJSONObject(i);
                                    Competition mCompetition = new Competition();
                                    mCompetition.setCompetitionName(mCompetitionsObject.getString("competition_name"));
                                    if (mCompetitionsObject.get("competition_id") instanceof Long)
                                        mCompetition.setCompetitionId(mCompetitionsObject.getLong("competition_id"));
                                    if (mCompetitionsObject.get("division_id") instanceof Long)
                                        mCompetition.setDivisionId(mCompetitionsObject.getLong("division_id"));
                                    mCompetition.setDivisionName(mCompetitionsObject.getString("division_name"));
                                    mCompetition.setIs_doubles(mCompetitionsObject.getBoolean("is_doubles"));
                                    mCompetition.setStart_date(mCompetitionsObject.getString("start_date"));
                                    mCompetition.setEnrolled(mCompetitionsObject.getBoolean("enrolled"));
                                    if(mCompetitionsObject.has("closed")) {
                                        mCompetition.setClosed(mCompetitionsObject.getBoolean("closed"));
                                    }else{
                                        mCompetition.setClosed(false);
                                    }
                                    if(mCompetitionsObject.has("status")) {
                                        mCompetition.setStatus(mCompetitionsObject.getInt("status"));
                                    }else{
                                        mCompetition.setStatus(0);
                                    }
                                    competitions.add(mCompetition);
                                }*/
                                for (Competition competition : competitions)
                                    competition.CompitionType = CompetitionType.league;

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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
        }

        public static Request getDetailsRequest(String oAuthToken, long divisionId, final VolleyHelper.IRequestListener<JSONObject, String> requestListener) {
//            HttpsTrustManager.allowAllSSL();
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
                            if (response.has("responseCode") && response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response);
                            } else if (response.has("responseMessage")) {
                                requestListener.onFailureResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse("");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };

        }

        public static void getSwagItems(String token, final VolleyHelper.IRequestListener<LeagueSwagItems, String> requestListener) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, WSCore._URL + "/" + WS_SWAG_ITEMS + "?oauth_token=" + token, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("responseCode").equals("200")) {
                            Gson gson = new Gson();
                            LeagueSwagItems data = gson.fromJson(response.toString(), LeagueSwagItems.class);
                            requestListener.onSuccessResponse(data);
                        } else {
                            requestListener.onFailureResponse(response.getString("responseMessage"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);
        }

        public static void callPreorderApi(String name, String swagItemId, String size, String cost, String shippingAddress1, String shippingAddress2, final VolleyHelper.IRequestListener<PreOrderResponse, String> requestListener) {
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("name", name);
                params.put("swag_item_id", swagItemId);
                params.put("size", size);
                params.put("cost", cost);
                params.put("shipping_address1", shippingAddress1);
                params.put("shipping_address2", shippingAddress2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + WS_SWAG_ITEMS, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("responseCode").equals("200")) {
                            Gson gson = new Gson();
                            PreOrderResponse data = gson.fromJson(response.toString(), PreOrderResponse.class);
                            requestListener.onSuccessResponse(data);
                        }else{
                            requestListener.onFailureResponse(response.getString("responseMessage"));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);
        }

        public static void getHeadToHeadData(String userId, String opponentId, final VolleyHelper.IRequestListener<HeadToHeadData, String> requestListener) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, WSCore._URL + "/" + WS_HEAD_TO_HEAD_DATA + "?opponent_id=" + opponentId + "&player_id=" + userId, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("responseCode").equals("200")) {//(response.getString("responseCode").equals("200")) {
                            Gson gson = new Gson();
                            HeadToHeadData data = gson.fromJson(response.toString(), HeadToHeadData.class);
                            requestListener.onSuccessResponse(data);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);

        }

        public static void getLeagueDivisions(long competitionId, final VolleyHelper.IRequestListener<List<NameIdPair>, String> requestListener) {
//            HttpsTrustManager.allowAllSSL();
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
                                Type listType = new TypeToken<List<NameIdPair>>() {
                                }.getType();
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "upcoming_programs.json", params,
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
                params.put("mobile_slider_popup", mobilePopup);
//                if(mobilePopup) {
//                    params.put("mobile_slider_popup_setting", "true");
//                }else{
//                    params.put("mobile_slider_popup_setting", "false");
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "upcoming_programs_popup_setting.json", params,
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
//								response.put("program_started",true);
//								response.put("responseMessage","<ul>\n" +
//										"  <li>Lorem ipsum dolor sit amet, consectetuer adipiscing \n" +
//										"  elit. Aenean commodo ligula eget dolor. Aenean \n" +
//										"  massa.</li>\n" +
//										"  <li>Cum sociis natoque penatibus et magnis dis \n" +
//										"  parturient montes, nascetur ridiculus mus. Donec quam \n" +
//										"  felis, ultricies nec, pellentesque eu, pretium quis, \n" +
//										"  sem.</li>\n" +
//										"  <li>Nulla consequat massa quis enim. Donec pede justo, \n" +
//										"  fringilla vel, aliquet nec, vulputate eget, arcu.</li>\n" +
//										"  <li>In enim justo, rhoncus ut, imperdiet a, venenatis \n" +
//										"  vitae, justo. Nullam dictum felis eu pede mollis \n" +
//										"  pretium. Integer tincidunt.</li>\n" +
//										"</ul>");
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
            }) {
                @Override
                public String getBodyContentType() {
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
//
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
//            };
//            return jsonObjReq;
        }
    }

    public static class SendMail {

        public static Request sendMailRequest(String oAuth, String type, String matchId, long[] recipientsIds,
                                              String programName, String[] recipientsEmails, String playType, String courtId,
                                              String[] daysSelected, String[] timeSelected, String notes,
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
//                params.put("test", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("response", params.toString());
//            return null;
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
                                Type listType = new TypeToken<List<Location>>() {
                                }.getType();
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
                                Type listType = new TypeToken<List<Court>>() {
                                }.getType();
                                // List<Court> courts = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("courts"), listType);
                                List<Court> mCourtList = new ArrayList<>();
                                JSONArray mCompetitionsJsonArray = new JSONArray(response.getString("courts"));
                                for (int i = 0; i < mCompetitionsJsonArray.length(); i++) {
                                    JSONObject mCourtObject = mCompetitionsJsonArray.getJSONObject(i);
                                    Court mCompetition = new Court();

                                    if (!mCourtObject.isNull("latitude")) {
                                        if (mCourtObject.get("latitude") instanceof String) {
                                            mCompetition.setLatitude(0.0);
                                        } else {
                                            mCompetition.setLatitude(Double.parseDouble(mCourtObject.getString("latitude")));

                                        }
                                    } else {
                                        mCompetition.setLatitude(0.0);
                                    }

                                    if (!mCourtObject.isNull("longitude")) {
                                        if (mCourtObject.get("longitude") instanceof String) {
                                            mCompetition.setLongitude(0.0);
                                        } else {
                                            mCompetition.setLongitude(Double.parseDouble(mCourtObject.getString("longitude")));

                                        }
                                    } else {
                                        mCompetition.setLongitude(0.0);
                                        //mCompetition.setLongitude(Double.parseDouble(mCompetitionsObject.getString("longitude")));
                                    }

                                    mCourtList.add(mCompetition);
                                }
//                                for (Court competition : competitions)
//                                    competition.CompitionType = CompetitionType.league;

                                requestListener.onSuccessResponse(mCourtList);
                                // }
                                // requestListener.onSuccessResponse(courts);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);
        }

        public static void getCourtsHome(final VolleyHelper.IRequestListener<List<Court>, String> requestListener) {

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
                                Type listType = new TypeToken<List<Court>>() {
                                }.getType();
                                List<Court> courts = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("courts"), listType);
//                                List<Court> mCourtList = new ArrayList<>();
//                                JSONArray mCompetitionsJsonArray = new JSONArray(response.getString("courts"));
//                                for (int i = 0; i < mCompetitionsJsonArray.length(); i++) {
//                                    JSONObject mCourtObject = mCompetitionsJsonArray.getJSONObject(i);
//                                    Court mCompetition = new Court();
//
//                                    if(!mCourtObject.isNull("latitude"))
//                                    {
//                                        if(mCourtObject.get("latitude") instanceof String){
//                                            mCompetition.setLatitude(0.0);
//                                        } else{
//                                            mCompetition.setLatitude(Double.parseDouble(mCourtObject.getString("latitude")));
//
//                                        }}else
//                                    {
//                                        mCompetition.setLatitude(0.0);
//                                    }
//
//                                    if(!mCourtObject.isNull("longitude"))
//                                    {
//                                        if(mCourtObject.get("longitude") instanceof String){
//                                            mCompetition.setLongitude(0.0);
//                                        } else{
//                                            mCompetition.setLongitude(Double.parseDouble(mCourtObject.getString("longitude")));
//
//                                        }
//                                    }else
//                                    {
//                                        mCompetition.setLongitude(0.0);
//                                        //mCompetition.setLongitude(Double.parseDouble(mCompetitionsObject.getString("longitude")));
//                                    }
//
//                                    mCourtList.add(mCompetition);
//                                }
//                                for (Court competition : competitions)
//                                    competition.CompitionType = CompetitionType.league;

//                                requestListener.onSuccessResponse(mCourtList);
                                // }
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);
        }

        public static void getCourtDetails(long courtId, final VolleyHelper.IRequestListener<CourtDetailModel, String> requestListener) {

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
                                Type listType = new TypeToken<CourtDetailModel>() {
                                }.getType();
                                CourtDetailModel courts = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("court_details"), listType);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(jsonObjectRequest);
        }

        public static void searchCourts(String zipCode, final VolleyHelper.IRequestListener<CourtMainModel, String> requestListener) {

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
                                Type listType = new TypeToken<CourtMainModel>() {
                                }.getType();

                                CourtMainModel courts = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.toString(), listType);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }


        public static void getCourtNames(final VolleyHelper.IRequestListener<ArrayList<String>, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "court_names.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<ArrayList<String>>() {
                                }.getType();

                                ArrayList<String> mCourtNameList = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("court_names"), listType);
                                requestListener.onSuccessResponse(mCourtNameList);
                            } else {
                                requestListener.onFailureResponse("Something went wrong!!!");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
                                Type listType = new TypeToken<List<Court>>() {
                                }.getType();
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
                                Type listType = new TypeToken<Court>() {
                                }.getType();
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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

        public static void communicationSettings(boolean canCall, boolean textCall, boolean pushnotfication, final VolleyHelper.IRequestListener<String, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("call", String.valueOf(canCall));
                params.put("text", String.valueOf(textCall));
                params.put("pushnotfication", String.valueOf(pushnotfication));
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void getChampionShip(final VolleyHelper.IRequestListener<JSONObject, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "national_championships.json", params, new Response.Listener<JSONObject>() {
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
                            requestListener.onFailureResponse(e.getMessage());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestListener.onError(error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
                                Type listType = new TypeToken<List<Competition>>() {
                                }.getType();
                                List<Competition> competitions = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("competitions"), listType);

                                for (Competition competition : competitions)
                                    competition.CompitionType = CompetitionType.tournament;

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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void getTournamentDetailsListJson(long tournamentId, final VolleyHelper.IRequestListener<JSONObject, String> iRequestListener) {
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
                    Log.e("PotyResponse", response.toString());
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<List<TournamentRoundDetails>>() {
                                }.getType();
                              /*  List<TournamentRound> tournamentsList = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("tournaments"), listType);
                                iRequestListener.onSuccessResponse(tournamentsList);
*/
                                // ----- Temp ----

//                                ArrayList<TournamentRound> tournamentsList = new ArrayList<TournamentRound>();
//                                if (response.opt("tournaments") != null) {
//                                    JSONArray tarray = response.getJSONArray("tournaments");
//                                    for (int i = 0; i < tarray.length(); i++) {
//                                        JSONObject tobj = (JSONObject) tarray.get(i);
//                                        TournamentRound round = new TournamentRound();
//                                        round.setTournamentTitle(tobj.getString("title"));
//                                        round.setTournamentDate(tobj.getString("date"));
//                                        round.setOpenDefault(tobj.getBoolean("open_default"));
//                                        List<TournamentRoundDetails> matchList = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(tobj.getString("matches"), listType);
//                                        if ((matchList != null) && (matchList.size() > 0)) {
//                                            round.setMatchesList(matchList);
//                                        }
//                                        tournamentsList.add(round);
//                                    }
//                                }
                                iRequestListener.onSuccessResponse(response);
                            } else {
                                iRequestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    iRequestListener.onError(error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
                    Log.e("PotyResponse", response.toString());
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
                                if (response.opt("tournaments") != null) {
                                    JSONArray tarray = response.getJSONArray("tournaments");
                                    for (int i = 0; i < tarray.length(); i++) {
                                        JSONObject tobj = (JSONObject) tarray.get(i);
                                        TournamentRound round = new TournamentRound();
                                        round.setTournamentTitle(tobj.getString("title"));
                                        round.setTournamentDate(tobj.getString("date"));
                                        round.setOpenDefault(tobj.getBoolean("open_default"));
                                        List<TournamentRoundDetails> matchList = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(tobj.getString("matches"), listType);
                                        if ((matchList != null) && (matchList.size() > 0)) {
                                            round.setMatchesList(matchList);
                                        }
                                        tournamentsList.add(round);
                                    }
                                }
                                iRequestListener.onSuccessResponse(tournamentsList);
                            } else {
                                iRequestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    iRequestListener.onError(error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void getTournamentPlayersForUpcoming(long tournamentId, final VolleyHelper.IRequestListener<UpcomingTournamentModel, String> iRequestListener) {
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("id", tournamentId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "tournament_report_bracket.json", params, new PotyResponse.Listener<JSONObject>() {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "tournament.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("PotyResponse", response.toString());
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<UpcomingTournamentModel>() {
                                }.getType();
                                UpcomingTournamentModel tournamentsList = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.toString(), listType);
                                iRequestListener.onSuccessResponse(tournamentsList);

                                // ----- Temp ----

                          /*      ArrayList<TournamentRound> tournamentsList = new ArrayList<TournamentRound>();
                                if (response.opt("tournaments") != null) {
                                    JSONArray tarray = response.getJSONArray("tournaments");
                                    for (int i = 0; i < tarray.length(); i++) {
                                        JSONObject tobj = (JSONObject) tarray.get(i);
                                        TournamentRound round = new TournamentRound();
                                        round.setTournamentTitle(tobj.getString("title"));
                                        round.setTournamentDate(tobj.getString("date"));
                                        round.setOpenDefault(tobj.getBoolean("open_default"));
                                        List<TournamentRoundDetails> matchList = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(tobj.getString("matches"), listType);
                                        if ((matchList != null) && (matchList.size() > 0)) {
                                            round.setMatchesList(matchList);
                                        }
                                        tournamentsList.add(round);
                                    }
                                }*/
                                iRequestListener.onSuccessResponse(tournamentsList);
                            } else {
                                iRequestListener.onFailureResponse(response.getString("responseMessage"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    iRequestListener.onError(error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }
    }

    public static class Buy {

        public static void getProgramList(final VolleyHelper.IRequestListener<JSONObject, String> requestListener) {

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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
                                Type listType = new TypeToken<CouponDetails>() {
                                }.getType();
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void submitPaymentDetails(JSONObject paymentDetails, final VolleyHelper.IRequestListener<String, String> requestListener) {
//            try {
//                paymentDetails.put("test",1);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void submitPaymentDetailsBrainTree(JSONObject paymentDetails, final VolleyHelper.IRequestListener<String, String> requestListener) {
//            try {
//                paymentDetails.put("test",1);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "submit_order2.json", paymentDetails, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            Log.e(" payment", response.toString());
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public String getBodyContentType() {
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }
    }


    public static class Scores {

        public static void getLatestScores(final VolleyHelper.IRequestListener<JSONObject, String> requestListener) {

            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "latest_scores.json", params, new Response.Listener<JSONObject>() {
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
                    Log.e("error", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void getPlayerInformation(String user_id, String page, final VolleyHelper.IRequestListener<String, String> requestListener) {
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("user_id", user_id);
                params.put("page", page);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "get_player_information.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("user_data"));
                            } else {
                                requestListener.onFailureResponse("Something went wrong"/*response.getString("responseMessage")*/);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void getPlayerList(String user_id, String page, final VolleyHelper.IRequestListener<String, String> requestListener) {
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("user_id", user_id);
                params.put("page", page);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "our_players.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("players"));
                            } else {
                                requestListener.onFailureResponse("Something went wrong"/*response.getString("responseMessage")*/);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }


        public static void getPlayerPictured(String user_id, final VolleyHelper.IRequestListener<String, String> requestListener) {
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("user_id", user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "get_player_pictures.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getJSONArray("photo_urls").toString());
                            } else {
                                requestListener.onFailureResponse("Something went wrong"/*response.getString("responseMessage")*/);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void getPlayerStats(String user_id, final VolleyHelper.IRequestListener<String, String> requestListener) {
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("user_id", user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "get_player_stats.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("player_stats"));
                            } else {
                                requestListener.onFailureResponse("Something went wrong"/*response.getString("responseMessage")*/);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void getPlayerScores(String user_id, int mPageCount, final VolleyHelper.IRequestListener<String, String> requestListener) {
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("user_id", user_id);
                params.put("page", mPageCount);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "get_player_scores.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("player_scores"));
                            } else {
                                requestListener.onFailureResponse("Something went wrong"/*response.getString("responseMessage")*/);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void getPlayerRefferal(String user_id, final VolleyHelper.IRequestListener<String, String> requestListener) {
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
                params.put("user_id", user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "get_player_referrals.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.toString());
                            } else {
                                requestListener.onFailureResponse("Something went wrong"/*response.getString("responseMessage")*/);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void deleteMyData(final VolleyHelper.IRequestListener<String,String> requestListener){
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
            } catch (Exception e) {
                e.printStackTrace();
            }
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + WS_DELETE_DATA, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try{
                                    if (response!=null) {
                                        if (response.getString("responseCode").equals("200")) {
                                            requestListener.onSuccessResponse(response.getJSONObject("response").getString("message"));
                                        }else {
                                            requestListener.onFailureResponse(response.getString("responseMessage"));
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                    requestListener.onFailureResponse(e.getMessage());
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
                        headers.put("app_type", "Android");
                        return headers;
                    }
                };
            VolleyHelper.getInstance().addToRequestQueue(request);


        }

        public static void updatePlayerProfile(String id, String mailing_address_1, String city, String state, String primary_telephone_number
                , String location_region_id, String court_id, String fourty_plus_league_option, String loc_preference, String external_link,
                                               String favorite_shot, String game_description,
                                               String dob, String favorite_player,
                                               final VolleyHelper.IRequestListener<String, String> requestListener) {
            JSONObject params = new JSONObject();
            try {

                params.put("oauth_token", App.sOAuth);
                JSONObject mPlayerJson = new JSONObject();
                mPlayerJson.put("id", id);
                mPlayerJson.put("mailing_address_1", mailing_address_1);
                mPlayerJson.put("city", city);
                mPlayerJson.put("state", state);
                mPlayerJson.put("primary_telephone_number", primary_telephone_number);
                mPlayerJson.put("location_region_id", location_region_id);
                mPlayerJson.put("court_id", court_id);
                mPlayerJson.put("fourty_plus_league_option", fourty_plus_league_option);
                mPlayerJson.put("loc_preference", loc_preference);
                mPlayerJson.put("external_link", external_link);
                mPlayerJson.put("favorite_shot", favorite_shot);
                mPlayerJson.put("game_description", game_description);
                mPlayerJson.put("dob", dob);
                mPlayerJson.put("favorite_player", favorite_player);
                params.put("player", mPlayerJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("params ->", params.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "update_player_info.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse("");
                            } else {
                                requestListener.onFailureResponse("Something went wrong"/*response.getString("responseMessage")*/);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void updateBusinessCard(String number_of_cards, String address,
                                              final VolleyHelper.IRequestListener<String, String> requestListener) {
            JSONObject params = new JSONObject();
            try {

                params.put("oauth_token", App.sOAuth);
                params.put("number_of_cards", number_of_cards);
                params.put("address", address);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("params ->", params.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "submit_business_cards.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse("Something went wrong"/*response.getString("responseMessage")*/);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void updateFlerCard(String number_of_flyers, String address,
                                          final VolleyHelper.IRequestListener<String, String> requestListener) {
            JSONObject params = new JSONObject();
            try {

                params.put("oauth_token", App.sOAuth);
                params.put("number_of_flyers", number_of_flyers);
                params.put("address", address);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("params ->", params.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "submit_business_cards.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse("Something went wrong"/*response.getString("responseMessage")*/);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }


        public static void updatePlayerStatus(String status,
                                              final VolleyHelper.IRequestListener<String, String> requestListener) {
            JSONObject params = new JSONObject();
            try {

                params.put("oauth_token", App.sOAuth);
                params.put("status", status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("params ->", params.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "update_player_status.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse("");
                            } else {
                                requestListener.onFailureResponse("Something went wrong"/*response.getString("responseMessage")*/);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }


        public static void removeProfilePicture(String id,
                                                final VolleyHelper.IRequestListener<String, String> requestListener) {
            JSONObject params = new JSONObject();
            try {

                params.put("oauth_token", App.sOAuth);
                params.put("id", id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("params ->", params.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    WSCore._URL + "/" + "remove_profile_image.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse("Something went wrong"/*response.getString("responseMessage")*/);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static void resetBadgeCount(final VolleyHelper.IRequestListener<String, String> requestListener) {
            JSONObject params = new JSONObject();
            try {

                params.put("oauth_token", App.sOAuth);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("params ->", params.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    WSCore._URL + "/" + "reset_badge_count.json", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse("Something went wrong"/*response.getString("responseMessage")*/);
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        public static byte[] getFileDataFromDrawable(Bitmap bitmap) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }

        public static void updateProfileImage(Context mContext, final Bitmap fileName,
                                              final VolleyHelper.IRequestListener<String, String> requestListener) {
          /*  SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, WSCore._URL + "/" + "update_profile_image.json",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response", response);
                            if (response != null) {
                                try {
                                    JSONObject mJsonResponse = new JSONObject(response);
                                    if (mJsonResponse.getString("responseCode").equals("200")) {
                                        requestListener.onSuccessResponse("");
                                    } else {
                                        requestListener.onFailureResponse("Something went wrong"*//*response.getString("responseMessage")*//*);
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
            });

            smr.addStringParam("oauth_token", App.sOAuth);
            smr.addMultipartParam("picture", "image/*", fileName);*/


            //our custom volley request
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                    WSCore._URL + "/" + "update_profile_image.json",
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
//                           try {
//                               JSONObject obj = new JSONObject(new String(response.data));
//                               Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
//                           } catch (JSONException e) {
//                               e.printStackTrace();
//                           }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                           Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("tags", "tags");
                    return params;
                }

                /*
                 * Here we are passing image by renaming it with a unique name
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    long imagename = System.currentTimeMillis();
                    params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(fileName)));
                    return params;
                }
            };
//           Volley.newRequestQueue(mContext).add(volleyMultipartRequest);
            VolleyHelper.getInstance().addToRequestQueue(volleyMultipartRequest);
        }

        //update Notifier Configuration
        public static void updateNotifierStatus(String apiName, final VolleyHelper.IRequestListener<String, String> requestListener) {
            JSONObject data = new JSONObject();
            try {
                data.put("oauth_token", App.sOAuth);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + apiName + ".json", data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getInt("responseCode") == 200) {// (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse("Something went wrong!!!");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }
    } // Profile Class

    public static class Swag{

        public static void getSeparateSwagItems(final VolleyHelper.IRequestListener<List<Swag_items>, String> requestListener){
            JSONObject params = new JSONObject();
            try {
                params.put("oauth_token", App.sOAuth);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + WS_SWAG_PROGRAM_LIST, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("responseCode").equals("200")) {
                                    Type ItemListType = new TypeToken<List<Swag_items>>() {
                                    }.getType();
                                    List<Swag_items> mSwagItemList = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.getString("swag_items"), ItemListType);

                                    /*Gson gson = new Gson();
                                    List<Swag_items> swagItems = gson.fromJson(response.getJSONArray("swag_items").toString(), List.class);*/
                                    requestListener.onSuccessResponse(mSwagItemList);
                                }else{
                                    requestListener.onFailureResponse("Error");
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                requestListener.onFailureResponse(e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("swagItem", "onErrorResponse: ");
                    requestListener.onError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };

            VolleyHelper.getInstance().addToRequestQueue(request);
        }
    }

    public static class UTR {

        //get UTR Configuration
        public static void getUTRConfiguration(final VolleyHelper.IRequestListener<UTRConfigurationModel, String> requestListener) {
            JSONObject data = new JSONObject();
            try {
                data.put("oauth_token", App.sOAuth);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "UTRConfigurationWizard.json", data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getInt("responseCode") == 200) {// (response.getString("responseCode").equals("200")) {
                                Type listType = new TypeToken<UTRConfigurationModel>() {
                                }.getType();
                                UTRConfigurationModel mURTDetails = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create().fromJson(response.toString(), listType);
                                requestListener.onSuccessResponse(mURTDetails);
                            } else {
                                requestListener.onFailureResponse("Something went wrong!!!");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }

        //update UTR Configuration
        public static void updateUTRConfiguration(String question, boolean answer, final VolleyHelper.IRequestListener<String, String> requestListener) {
            JSONObject data = new JSONObject();
            try {
                data.put("oauth_token", App.sOAuth);
                data.put("question", question);
                data.put("answer", answer);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, WSCore._URL + "/" + "update_utr_status.json", data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getInt("responseCode") == 200) {// (response.getString("responseCode").equals("200")) {
                                requestListener.onSuccessResponse(response.getString("responseMessage"));
                            } else {
                                requestListener.onFailureResponse("Something went wrong!!!");
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
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("app_type", "Android");
                    return headers;
                }
            };
            VolleyHelper.getInstance().addToRequestQueue(request);
        }
    }


    public static void UpdatePaymentCheckout(@NonNull String account_number,@NonNull String expidate
            ,@NonNull String accountHolderName, @NonNull String transaction_amount
            ,@NonNull String notification_email_address, String description,
                final VolleyHelper.IRequestListener<String, String>
               requestListener) {
        JSONObject params = new JSONObject();
        try {
            if (notification_email_address.isEmpty()) {
                params.put("transaction_amount", transaction_amount);
                params.put("account_number", account_number);
                params.put("exp_date", expidate);
                params.put("account_holder_name", accountHolderName);
            }
            else {
                params.put("transaction_amount", transaction_amount);
                params.put("account_number", account_number);
                params.put("exp_date", expidate);
                params.put("account_holder_name", accountHolderName);
                params.put("notification_email_address", notification_email_address);
                params.put("description", description);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("params ->", params.toString());
        Log.e("params ->", paymentUrl.toString());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,WSCore.paymentUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if (response.getString("type").equals("Transaction")) {
                            requestListener.onSuccessResponse(response.getString("data"));
                        } else {
                            requestListener.onFailureResponse("Something went wrong"/*response.getString("responseMessage")*/);
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                        requestListener.onFailureResponse("Error occurred");
                    }
                }
                Log.e("responseAvd ->", response.toString());
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                requestListener.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                ///Development
                HashMap<String, String> headers = new HashMap<>();

                headers.put("user-id", "11ecc57da139742abefe49cb");
                headers.put("user-api-key", "11ecc5f1415a394ea7abd11f");
                headers.put("developer-id", "wIncRORH");

                Log.e("headers ->", headers.toString());

                //Production
/*
                headers.put("user-id", "11ed69a88515b9fa98f3bd8d");
                headers.put("user-api-key", "11ed69a897479fdaab34109a");
                headers.put("developer-id", "TLN20938");
*/

                return headers;
            }
        };
        VolleyHelper.getInstance().addToRequestQueue(request);
    }
}