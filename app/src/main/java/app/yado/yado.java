package app.yado;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class yado extends Application {
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
