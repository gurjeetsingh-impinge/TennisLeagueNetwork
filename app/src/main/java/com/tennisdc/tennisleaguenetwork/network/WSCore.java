package com.tennisdc.tennisleaguenetwork.network;

import com.tennisdc.tln.Constants;

public class WSCore {

    public final static String _URL = Constants.BASE_URL + "/api";
 //   public final static String _URL = "http://192.168.0.102:3000/api/";

    /*private DefaultHttpClient mHttpClient;

    public WSCore() {
        HttpParams params = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(params, 10000);
        HttpConnectionParams.setSoTimeout(params, 10000);

        mHttpClient = new DefaultHttpClient(params);
    }

	*//*public String webInvoke(String methodName, JSONObject jsonObj) {
        JSONObject data = new JSONObject();
		try {
			data.put("TheData", jsonObj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return webInvoke(methodName, jsonObj.toString(), null);
	}*//*

    public String Post(String methodName, JSONObject data, String contentType) {
        mHttpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);

        HttpPost httpPost = new HttpPost(_URL + "/" + methodName);
        httpPost.setHeader("Accept", "application/json");

        if (contentType != null) {
            httpPost.setHeader("Content-type", contentType);
        } else {
            httpPost.setHeader("Content-type", "application/json");
        }

        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(data.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpPost.setEntity(stringEntity);

        HttpResponse response = null;
        try {
            response = mHttpClient.execute(httpPost, new BasicHttpContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            try {
                return EntityUtils.toString(response.getEntity());
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public String Get(String methodName, LinkedHashMap<String, String> params) {
        mHttpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);

        Uri.Builder b = Uri.parse(_URL + "/" + methodName).buildUpon();
        if (params != null) {
            Iterator<?> it = params.entrySet().iterator();
            while (it.hasNext()) {
                @SuppressWarnings("rawtypes") Map.Entry pairs = (Map.Entry) it.next();
                b.appendQueryParameter((String) pairs.getKey(), (String) pairs.getValue());
            }
        }

        HttpGet httpGet = new HttpGet(b.build().toString());
        httpGet.setHeader("Accept", "application/json");

        //Modify request to accept GZip
        //AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpGet);

        HttpResponse response = null;
        try {
            response = mHttpClient.execute(httpGet, new BasicHttpContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            try {
                //Decompress GZiped response
				*//*InputStream is = AndroidHttpClient.getUngzippedContent(response.getEntity());
                BufferedReader br = null;
				StringBuilder sb = new StringBuilder();
		 
				String line;
				try {
					br = new BufferedReader(new InputStreamReader(is));
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return sb.toString();*//*

                return EntityUtils.toString(response.getEntity());
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }*/
}
