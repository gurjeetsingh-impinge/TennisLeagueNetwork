package com.tennisdc.tln.common;

/**
 * Image capturing related utility methods
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tennisdc.tln.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

//import okhttp3.MediaType;
//import okhttp3.RequestBody;

public class ImageUtility {
    private Context context;
    Uri outputFileUri;
    public String currentPhotoPath;

    public ImageUtility(Context context) {
        this.context = context;
    }



    /**
     * Display a dialog to select between Camera and Gallery to pick your image
     *
     * @param cameraRequestCode  request code for camera use
     * @param galleryRequestCode request code gallery use
     */
    public Uri CameraGalleryIntent(final Activity activity, final int cameraRequestCode, final int galleryRequestCode) {
        /*final File root = getFile(activity);
        Log.e("TAG", "CameraGalleryIntent: dir created "+ root.mkdirs());
        String filename = getUniqueImageFilename();
        File sdImageMainDirectory = new File(root, filename);
//        final Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);

        if (Build.VERSION.SDK_INT >= 24)
            outputFileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", sdImageMainDirectory);
        else
            outputFileUri = Uri.fromFile(sdImageMainDirectory);*/

        File storageDir = getFile(activity);
        String filename = getUniqueImageFilename();

        try {
            File image = File.createTempFile(filename,"",storageDir);

            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = image.getAbsolutePath();

            outputFileUri = FileProvider.getUriForFile(context,"com.tennisdc.tln.fileprovider",image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        CharSequence items[] = new CharSequence[]{context.getResources().getString(R.string.camera),
                context.getResources().getString(R.string.gallery)};
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int n) {
                switch (n) {
                    case 0:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                        activity.startActivityForResult(intent, cameraRequestCode);
                        break;
                    case 1:
                        Intent pickIntent = new Intent(Intent.ACTION_PICK);
                        pickIntent.setType("image/*");
                        activity.startActivityForResult(pickIntent, galleryRequestCode);
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.setTitle(context.getResources().getString(R.string.selection));
        if (outputFileUri != null) {
            dialog.show();
        }
        return outputFileUri;
    }

    public Uri getUri(Context context) {
        File storageDir = getFile(context);
        String filename = getUniqueImageFilename();
        try {
            File image = File.createTempFile(filename, "", storageDir);
            return Uri.fromFile(image);
        }catch (Exception e){
            e.printStackTrace();
            final File root = getFile(context);
            root.mkdirs();
            String filename1 = getUniqueImageFilename();
            File sdImageMainDirectory = new File(root, filename1);

            return Uri.fromFile(sdImageMainDirectory);
        }
    }

    public void VideoGalleryIntent(Activity activity, final int galleryRequestCode) {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("video/*");
        activity.startActivityForResult(pickIntent, galleryRequestCode);

    }

    /**
     * Compresses the image
     *
     * @param filePath : Location of image file
     * @return compressed image file path
     */
    @SuppressWarnings("deprecation")
    public String compressImage(String filePath, Context context) {

        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bmp = null;
        options.inJustDecodeBounds = true;

        bmp = BitmapFactory.decodeFile(filePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        options.inSampleSize = ImageUtility.calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(
                Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.e("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.e("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.e("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.e("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(),
                    matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filename = getFilename(context);
        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (bmp != null) {
                bmp.recycle();
                bmp = null;
            }
            if (scaledBitmap != null) {
                scaledBitmap.recycle();
            }
        }
        return filename;

    }

    private String getRealPathFromURI1(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    //get file name
    public String getFilename(Context context) {

        final File root = getFile(context);
        root.mkdirs();
        final String filename = getUniqueImageFilename();
        File file = new File(root, filename);
        return file.getAbsolutePath();
    }

    private File getFile(Context context) {
       /* return new File(Environment.getExternalStorageDirectory() + File.separator + "LaundryXchange"
                + File.separator);*/
//        return context.getExternalFilesDir("LaundryXchange");
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    //return a unique file name
    public static String getUniqueImageFilename() {
        return "img_" + System.currentTimeMillis() + ".png";
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //get file path from its URI
    public String getRealPathFromURI(Activity context, Uri contentUri) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentUri, proj, // Which
                // columns
                // to
                // return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // load image into image view using Picasso
    public void LoadImage(String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    /**
     * Compress image and convert to multipart
     *
     * @param filePath path of the file to be converted
     * @return multipart image for the path supplied
     */
    /*public Observable<RequestBody> getCompressedFile(final String filePath, final ImageUtility imageUtility) {
        return Observable.create(new Observable.OnSubscribe<RequestBody>() {
            @Override
            public void call(Subscriber<? super RequestBody> subscriber) {
                try {
                    String newFilePath = getCompressedImage(filePath);
                    subscriber.onNext(imageUtility.getRequestBodyImage(new File(newFilePath)));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }*/

    /**
     * get request body image
     */
//    public RequestBody getRequestBodyImage(File file) {
//        return RequestBody.create(MediaType.parse("image/jpg"), file);
//    }

    /**
     * convert image to base 64 string
     *
     * @param filePath path of image
     * @return base 64 string
     */
    public String getBase64Image(String filePath) {
        filePath = getCompressedImage(filePath);
        Bitmap bm = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**
     * get compressed image
     */
    private String getCompressedImage(String filePath) {
        String newFilePath;
        int file_size = Integer.parseInt(String.valueOf(new File(filePath).length() >> 10));
        if (file_size >= 120) {
//            newFilePath = compressImage(filePath);
        } else {
            newFilePath = filePath;
        }
        return filePath;
    }

    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    public boolean checksize(Uri uri, Context context) {
        File file = new File(uri.getPath());
        long size = 0;

        try {
        /*// Get the number of bytes in the file
        long sizeInBytes = file.length();*/
            Cursor cursor = context.getContentResolver().query(uri,
                    null, null, null, null);
            cursor.moveToFirst();
            size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //transform in MB
        long sizeInMb = size / (1024 * 1024);
        return sizeInMb < 12;
    }

    private String getCharAtZero(String str) {
        String first;
        if (str.equalsIgnoreCase(""))
            first = "";
        else
            first = String.valueOf(str.charAt(0));
        return first;
    }
}