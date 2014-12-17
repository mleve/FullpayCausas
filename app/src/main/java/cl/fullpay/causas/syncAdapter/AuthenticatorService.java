package cl.fullpay.causas.syncAdapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by mario on 16-12-14.
 */
public class AuthenticatorService extends Service {

    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
