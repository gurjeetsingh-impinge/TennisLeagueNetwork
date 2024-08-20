package com.tennisdc.tln.modules.myAccount;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kcode.bottomlib.BottomDialog;
import com.soundcloud.android.crop.Crop;
import com.tennisdc.tln.BaseDialog;
import com.tennisdc.tln.Constants;
import com.tennisdc.tln.R;
import com.tennisdc.tln.common.App;
import com.tennisdc.tln.common.DialogsUtil;
import com.tennisdc.tln.common.GsonRealmExclusionStrategy;
import com.tennisdc.tln.common.ImageUtility;
import com.tennisdc.tln.common.Prefs;
import com.tennisdc.tln.interfaces.OnDialogButtonClickListener;
import com.tennisdc.tln.interfaces.ScalingUtilities;
import com.tennisdc.tln.interfaces.UserPicture;
import com.tennisdc.tln.interfaces.VolleyMultipartRequest;
import com.tennisdc.tln.model.PlayerPhotoModel;
import com.tennisdc.tln.network.VolleyHelper;
import com.tennisdc.tln.network.WSCore;
import com.tennisdc.tln.network.WSHandle;
import com.tennisdc.tln.ui.RecyclerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.BindView;

public class PhotosScreen extends AppCompatActivity implements OnDialogButtonClickListener  {

    RecyclerView mRVPhotos;
    TextView mTxtTitleToolbar;
    ImageView mImgBack;
    Button mBtnAddPhotos;
    TextView mTxtAddPhotos;
    ArrayList<PlayerPhotoModel> mPlayerPictureList = new ArrayList<>();
    Prefs.AppData prefs = null;
    ImageUtility mImgUtils;
    DialogsUtil mDialogUtils;
    int mDialogType;
    private Uri cameraUri;
    private Uri targetUri;
    String mRequestCameraPermissions;
    String mRequestSettings;
    String mGrantPermissions;
    String mCancel;
    String mGoToSettings;
    String mFileNew;
    File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_screen);
        App.LogFacebookEvent(this,this.getClass().getName());
        prefs = new Prefs.AppData(this);
        mDialogUtils = new DialogsUtil();
        mImgUtils = new ImageUtility(this);
        mRequestCameraPermissions = getString(R.string.explanation_multiple_request);
        mRequestSettings = getString(R.string.explanation_multiple_settings);
        mGrantPermissions = getString(R.string.action_grant_permission);
        mCancel = getString(R.string.action_cancel);
        mGoToSettings = getString(R.string.action_goto_settings);
        mTxtTitleToolbar = (TextView)findViewById(R.id.mTxtTitleToolbar);
        mTxtTitleToolbar.setText(R.string.photos);
        mImgBack = (ImageView) findViewById(R.id.mImgBack);
        mBtnAddPhotos = (Button) findViewById(R.id.mBtnAddPhotos);
        mTxtAddPhotos = (TextView) findViewById(R.id.mTxtAddPhotos);
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRVPhotos = (RecyclerView)findViewById(R.id.mRVPhotos);
//        mRVPhotos.setLayoutManager(new GridLayoutManager(this,2));
        mRVPhotos.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        DividerItemDecoration verticalDecoration = new DividerItemDecoration(mRVPhotos.getContext(),
                DividerItemDecoration.HORIZONTAL);
        Drawable verticalDivider = ContextCompat.getDrawable(this, R.drawable.vertical_divider);
        verticalDecoration.setDrawable(verticalDivider);
        mRVPhotos.addItemDecoration(verticalDecoration);

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(mRVPhotos.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(this, R.drawable.horizontal_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        mRVPhotos.addItemDecoration(horizontalDecoration);

        if(getIntent().getStringExtra("mPlayerId").trim().isEmpty()){
            getPlayerPictures(prefs.getUserID());
            mTxtAddPhotos.setVisibility(View.VISIBLE);
            mBtnAddPhotos.setVisibility(View.VISIBLE);
        }else {
            getPlayerPictures(getIntent().getStringExtra("mPlayerId"));
            mTxtAddPhotos.setVisibility(View.GONE);
            mBtnAddPhotos.setVisibility(View.GONE);
        }


        mBtnAddPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayerPictureList.size() < 3)
                checkCameraPermissions();
                else
                    Toast.makeText(PhotosScreen.this,"You can upload maximum 3 pictures", Toast.LENGTH_LONG).show();
            }
        });
    }

    class PhotosHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.mImgPhotoView)
        ImageView mImgPhootView;
        @BindView(R.id.mImgDeletePhotoView)
        ImageView mImgDeletePhotoView;

        public PhotosHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(final PlayerPhotoModel mPhotoData) {
            Glide.with(PhotosScreen.this).load(mPhotoData.getUrl())
                    .placeholder(R.drawable.ic_photos).into(mImgPhootView);

            if(getIntent().getStringExtra("mPlayerId").trim().isEmpty()){
                mImgDeletePhotoView.setVisibility(View.VISIBLE);
            }else{
                mImgDeletePhotoView.setVisibility(View.GONE);
            }
            mImgDeletePhotoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomDialog dialog = BottomDialog.newInstance("",getString(R.string.btn_cancel),new String[]{
                            "Remove Picture"});
/**
 *
 * BottomDialog dialog = BottomDialog.newInstance("titleText","cancelText",new String[]{"item1","item2"});
 *
 * use public static BottomDialog newInstance(String titleText,String cancelText, String[] items)
 * set cancel text
 */
                    dialog.show(getSupportFragmentManager(),"dialog");
                    //add item click listener
                    dialog.setListener(new BottomDialog.OnClickListener() {
                        @Override
                        public void click(int position) {
                            removeProfilePicture(String.valueOf(mPhotoData.getId()));
                        }
                    });
                }
            });

        }
    }


    void getPlayerPictures(String mPlayerList) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Please wait...");
        WSHandle.Profile.getPlayerPictured(mPlayerList, new VolleyHelper.IRequestListener<String, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                dialog.show(getSupportFragmentManager(), "dlg-frag");
            }

            @Override
            public void onSuccessResponse(String response) {
                progressDialog.dismiss();
                Gson gson = new GsonBuilder().setExclusionStrategies(new GsonRealmExclusionStrategy()).create();

                mPlayerPictureList = gson.fromJson(response,
                        new TypeToken<ArrayList<PlayerPhotoModel>>() {
                }.getType());

                mRVPhotos.setAdapter(new RecyclerAdapter<PlayerPhotoModel, PhotosHolder>(mPlayerPictureList) {

                    @Override
                    public PhotosHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(PhotosScreen.this).inflate(R.layout.view_photo_item, null);
                        return new PhotosHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(PhotosHolder holder, int position) {
                        holder.bindItem(mPlayerPictureList.get(position));
                    }

                });
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(PhotosScreen.this, "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    void removeProfilePicture(String mID) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Please wait...");
        WSHandle.Profile.removeProfilePicture(mID, new VolleyHelper.IRequestListener<String, String>() {
            @Override
            public void onFailureResponse(String response) {
                progressDialog.dismiss();
                BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response);
                dialog.show(getSupportFragmentManager(), "dlg-frag");
            }

            @Override
            public void onSuccessResponse(String response) {
                progressDialog.dismiss();
                if(getIntent().getStringExtra("mPlayerId").trim().isEmpty()){
                    getPlayerPictures(prefs.getUserID());
                }else {
                    getPlayerPictures(getIntent().getStringExtra("mPlayerId"));
                }
            }

            @Override
            public void onError(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(PhotosScreen.this, "Network Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    /*******************************/
    void cameraPermissionsGranted() {
        cameraUri = mImgUtils.CameraGalleryIntent(this, Constants.REQUEST_CAMERA, Constants.REQUEST_GALLERY);
        targetUri = mImgUtils.getUri(this);
    }

    void getDialogType(int dialogType) {
        mDialogType = dialogType;
    }

    /**
     * Check whether user has give camera and storage permissions
     */
    void checkCameraPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkMultiplePermissions(Constants.REQUEST_CODE_ASK_CAMERA_MULTIPLE_PERMISSIONS);
        } else {
            cameraPermissionsGranted();
        }
    }

    /**
     * Check if user has allowed application to use Location Permissions else ask for permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    void checkMultiplePermissions(int permissionCode) {
        String[] PERMISSIONS = {Constants.CAMERA_PERMISSION, Constants.READ_EXTERNAL_STORAGE_PERMISSION,
                Constants.WRITE_EXTERNAL_STORAGE_PERMISSION};
        if (!hasPermissions(PhotosScreen.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(PhotosScreen.this, PERMISSIONS, permissionCode);
        } else {
            cameraPermissionsGranted();
        }
    }

    /**
     * Handle result for permission grant or denial
     */

    @TargetApi(Build.VERSION_CODES.M)
    void takeActionOnPermissionChanges(int[] grantResults, OnDialogButtonClickListener onDialogButtonClickListener
            , String mRequestPermissions, String mRequsetSettings, String mGrantPermissions, String mCancel, String mGoToSettings) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            cameraPermissionsGranted();
        } else {
            boolean showRationale1 = shouldShowRequestPermissionRationale(Constants.CAMERA_PERMISSION);
            boolean showRationale2 = shouldShowRequestPermissionRationale(Constants.READ_EXTERNAL_STORAGE_PERMISSION);
            boolean showRationale3 = shouldShowRequestPermissionRationale(Constants.WRITE_EXTERNAL_STORAGE_PERMISSION);

            if (showRationale1 && showRationale2 && showRationale3) {
                //explain to user why we need the permissions
                getDialogType(Constants.DIALOG_DENY);
                mDialogUtils.openAlertDialog(PhotosScreen.this, mRequestPermissions, mGrantPermissions, mCancel, onDialogButtonClickListener);
            } else {
                //explain to user why we need the permissions and ask him to go to settings to enable it
                getDialogType(Constants.DIALOG_NEVER_ASK);
                mDialogUtils.openAlertDialog(PhotosScreen.this, mRequsetSettings, mGoToSettings, mCancel, onDialogButtonClickListener);
            }
        }
    }

    /**
     * check if user has permissions for the asked permissions
     */
    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * redirect user to your application settings in device
     */
    public void redirectToAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        takeActionOnPermissionChanges(grantResults, this,
                mRequestCameraPermissions, mRequestSettings, mGrantPermissions, mCancel, mGoToSettings);
    }

    /**
     * User clicked positive button on Alert Dialog
     */
    @Override
    public void onPositiveButtonClicked() {
        switch (mDialogType) {
            case Constants.DIALOG_DENY:
                checkMultiplePermissions(Constants.REQUEST_CODE_ASK_CAMERA_MULTIPLE_PERMISSIONS);
                break;
            case Constants.DIALOG_NEVER_ASK:
                redirectToAppSettings();
                break;
        }
    }

    /**
     * User clicked positive button on Alert Dialog
     * so close the activity and prevent user from opening camera
     */
    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CAMERA) {
            try {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                Drawable color = new ColorDrawable(getResources().getColor(android.R.color.transparent));
                BitmapDrawable imageProfileBitmap = new BitmapDrawable(getResources(), imageBitmap);
                if (imageProfileBitmap != null) {
                    uploadprofilePicture(imageProfileBitmap);
                }
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), targetUri);
//                new RotateImage().execute(cameraUri);
            } catch (Exception e) {
                e.printStackTrace();
            }


//            Crop.of(cameraUri, targetUri).withAspect(80, 80).start(PhotosScreen.this);
        } else if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_GALLERY) {
            if (data != null) {
                try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), targetUri);
                    new RotateImage().execute(data.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                Crop.of(data.getData(), targetUri).withAspect(80, 80).start(PhotosScreen.this);
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {

            String image = mImgUtils.compressImage(targetUri.getPath(), this);


            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), targetUri);
                new PhotosScreen.RotateImage().execute(targetUri);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            Glide.with(this)
//                    .load("file://" + targetUri.getPath())
//                    .into(mImgUserEditProfile);

//            uploading image to server

        }

    }

    void uploadprofilePicture(final BitmapDrawable imageProfileBitmap) {
        try {
            final ProgressDialog progressDialog = ProgressDialog.show(this, null, "Please wait...");
            VolleyMultipartRequest mMultiPart = new VolleyMultipartRequest(Request.Method.POST, WSCore._URL + "/" + "update_profile_image.json", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse networkResponse) {
                    Log.e("result", new String(networkResponse.data));
                    String message = "";

                    try {
                        JSONObject jsonObject = new JSONObject(new String(networkResponse.data));
                        if (jsonObject.getString("responseCode").equals("200")) {
                            if (getIntent().getStringExtra("mPlayerId").trim().isEmpty()) {
                                getPlayerPictures(prefs.getUserID());
                            } else {
                                getPlayerPictures(getIntent().getStringExtra("mPlayerId"));
                            }
                        } else {
                            BaseDialog.SimpleDialog dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, "Something went wrong");
                            dialog.show(getSupportFragmentManager(), "dlg-frag");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    String message = null;
                    if (volleyError instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (volleyError instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (volleyError instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }
                    progressDialog.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    super.getParams();
                    Map<String, String> params = new HashMap<>();
                    params.put("oauth_token", App.sOAuth);
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() throws AuthFailureError {
                    Map<String, DataPart> params = new HashMap<>();
                    if (imageProfileBitmap != null) {
                        params.put("picture", new DataPart("file_image1" + Calendar.getInstance().getTimeInMillis() + ".png", getFileDataFromDrawable(PhotosScreen.this,
                                imageProfileBitmap), "image/png"));
                    }
                    return params;

                }
            };
            mMultiPart.setRetryPolicy(new DefaultRetryPolicy(120000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleyHelper.getInstance(this).addToRequestQueue(mMultiPart);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    class RotateImage extends AsyncTask<Uri, String, String> {
        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Uri... params) {
//            mFileNew = savebitmap(params[0], "profile");
            mFileNew = new ImageUtility(PhotosScreen.this).getRealPathFromURI(PhotosScreen.this,params[0]);
            return mFileNew;
//            return mImgUtils.currentPhotoPath;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        protected void onPostExecute(String file) {
            super.onPostExecute(file);
            if (file != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
                Drawable color = new ColorDrawable(getResources().getColor(android.R.color.transparent));
                BitmapDrawable imageProfileBitmap = new BitmapDrawable(getResources(), bitmap);
                LayerDrawable ldrawable = new LayerDrawable(new Drawable[]{color, imageProfileBitmap});
                if (imageProfileBitmap != null) {
                    uploadprofilePicture(imageProfileBitmap);
                }
               /* img_profilepic.setImageDrawable(ldrawable);
                Log.e("responceDrawablePic", "" + roundedImage);*/

            }
        }
    }

    private String savebitmap(Uri path, String filename) {
        String dir = Environment.getExternalStorageDirectory() + File.separator + "PillerBurken";
        File file_dir = new File(dir);
        file_dir.mkdir();
        FileOutputStream outStream = null;
        mFile = new File(dir, filename + ".jpg");
        if (mFile.exists()) {
            mFile.delete();
            mFile = new File(dir, filename + ".jpg");
            Log.e("file exist", "" + mFile + ",Bitmap= " + filename);
        }
        try {
            Bitmap p_image = ScalingUtilities.createScaledBitmap(new UserPicture(path, getContentResolver()).getBitmap(), 500, 500, ScalingUtilities.ScalingLogic.CROP);
            outStream = new FileOutputStream(mFile);
            p_image.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("path", "" + path);
        Log.e("file", "" + mFile);
        return mFile.toString();
    }
}
