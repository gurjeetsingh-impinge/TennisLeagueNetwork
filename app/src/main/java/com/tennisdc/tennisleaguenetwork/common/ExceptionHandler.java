package com.tennisdc.tennisleaguenetwork.common;

import android.app.Activity;
import android.os.Build;

import com.tennisdc.tennisleaguenetwork.SingleFragmentActivity;
import com.tennisdc.tennisleaguenetwork.model.UncaughtException;
import com.tennisdc.tennisleaguenetwork.modules.FragError;

import java.io.PrintWriter;
import java.io.StringWriter;

import io.realm.Realm;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created  on 17-03-2015.
 */
public class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {

    private final Activity myContext;

    public ExceptionHandler(Activity context) {
        myContext = context;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        if (exception.getCause() instanceof RealmMigrationNeededException) {
            Realm.deleteRealmFile(myContext);
        }

        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("\n************ CAUSE OF ERROR ************\n\n");
        errorReport.append(stackTrace.toString());

        errorReport.append("\n************ DEVICE INFORMATION ***********\n");
        errorReport.append("Brand: ").append(Build.BRAND);
        String LINE_SEPARATOR = "\n";
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: ").append(Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ").append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Id: ").append(Build.ID);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: ").append(Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n************ FIRMWARE ************\n");
        errorReport.append("SDK: ").append(Build.VERSION.SDK_INT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: ").append(Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Incremental: ").append(Build.VERSION.INCREMENTAL);
        errorReport.append(LINE_SEPARATOR);

        /* create realm object */
        UncaughtException uncaughtException = new UncaughtException();
        uncaughtException.setDetails(errorReport.toString());

        /* save exception in realm */
        Realm realm = Realm.getInstance(myContext);
        realm.beginTransaction();
        realm.copyToRealm(uncaughtException);
        realm.commitTransaction();
        realm.close();

        myContext.startActivity(SingleFragmentActivity.getIntent(myContext, FragError.class, null));

        myContext.finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

}