package com.example.fingerprint;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {

    // Declare a string variable for the key we’re going to use in our fingerprint authentication
     static final String KEY_NAME = "yourKey";
    static  Cipher cipher;
    static  Cipher cipher2;
    static  Cipher cipher3;
    public static KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private TextView textView;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    static SharedPreferences utilsNote;
    static SharedPreferences utilsIv;
    static byte[] iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utilsIv = getSharedPreferences("IV", MODE_PRIVATE);
        utilsNote = getSharedPreferences("Note", MODE_PRIVATE);

        // If you’ve set your app’s minSdkVersion to anything lower than 23, then you’ll need to verify that the device is running Marshmallow
        // or higher before executing any fingerprint-related code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Get an instance of KeyguardManager and FingerprintManager//
            keyguardManager =
                    (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager =
                    (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            textView = (TextView) findViewById(R.id.textview);

            //Check whether the device has a fingerprint sensor//
            if (!fingerprintManager.isHardwareDetected()) {
                // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
                textView.setText("Your device doesn't support fingerprint authentication");
            }
            //Check whether the user has granted your app the USE_FINGERPRINT permission//
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // If your app doesn't have this permission, then display the following text//
                textView.setText("Please enable the fingerprint permission");
            }

            //Check that the user has registered at least one fingerprint//
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                // If the user hasn’t configured any fingerprints, then display the following message//
                textView.setText("No fingerprint configured. Please register at least one fingerprint in your device's Settings");
            }

            //Check that the lockscreen is secured//
            if (!keyguardManager.isKeyguardSecure()) {
                // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
                textView.setText("Please enable lockscreen security in your device's Settings");
            } else {
                try {
                    keyStore = KeyStore.getInstance("AndroidKeyStore");
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                }
                try {
                    keyStore.load(null);
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                try {

                    if (!keyStore.containsAlias(KEY_NAME)) {
                        keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                        keyGenerator.init(new
                                KeyGenParameterSpec.Builder(KEY_NAME,
                                KeyProperties.PURPOSE_ENCRYPT |
                                        KeyProperties.PURPOSE_DECRYPT)
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                .setUserAuthenticationRequired(false)
                                .setEncryptionPaddings(
                                        KeyProperties.ENCRYPTION_PADDING_NONE)
                                .build());
                        keyGenerator.generateKey();
                        cipher = Cipher.getInstance(
                                KeyProperties.KEY_ALGORITHM_AES + "/"
                                        + KeyProperties.BLOCK_MODE_GCM + "/"
                                        + KeyProperties.ENCRYPTION_PADDING_NONE);
                        SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                                null);
                        cipher.init(Cipher.ENCRYPT_MODE, key);

                    }
                    else {
                        cipher = Cipher.getInstance(
                                KeyProperties.KEY_ALGORITHM_AES + "/"
                                        + KeyProperties.BLOCK_MODE_GCM + "/"
                                        + KeyProperties.ENCRYPTION_PADDING_NONE);;
                        SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                                null);
                        cipher.init(Cipher.ENCRYPT_MODE, key);
                    }

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (UnrecoverableKeyException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }


                cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler helper = new FingerprintHandler(this);
                    helper.startAuth(fingerprintManager, cryptoObject);


            }
        }
    }

//Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//


    /*@RequiresApi(api = Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            //Generate the key//
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            //Initialize an empty KeyStore//
            keyStore.load(null);

            //Initialize the KeyGenerator//
            keyGenerator.init(new

                    //Specify the operation(s) this key can be used for//
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)

                    //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                    .setUserAuthenticationRequired(false)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build());

            //Generate the key//GCM
            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    //Create a new method that we’ll use to initialize our cipher//
    public boolean initCipher() {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_GCM + "/"
                            + KeyProperties.ENCRYPTION_PADDING_NONE);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            iv = cipher.getIV ();
            //Return true if the cipher has been initialized successfully//
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {

            //Return false if cipher initialization failed//
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }*/




/*    @RequiresApi(api = Build.VERSION_CODES.O)
    static void Decrypt() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, InvalidKeyException, UnrecoverableKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {


        keyStore.load(null);
        SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                null);
        String loadFromPref = utilsIv.getString("IV","");
        iv = cipher.doFinal(Base64.getDecoder().decode(loadFromPref));
        final GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

    }*/

/*
    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }
*/


}
