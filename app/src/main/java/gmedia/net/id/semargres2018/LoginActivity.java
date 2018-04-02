package gmedia.net.id.semargres2018;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.RuntimePermissionsActivity;
import com.maulana.custommodul.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import gmedia.net.id.semargres2018.NotificationUtils.InitFirebaseSetting;
import gmedia.net.id.semargres2018.Utils.ServerURL;

public class LoginActivity extends RuntimePermissionsActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final int RC_SIGN_IN_GOOGLE = 9001;
    private FirebaseAuth auth;
    private CallbackManager mCallbackManager;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final String TAG = "Login";
    public static GoogleApiClient googleApiClient;
    private FirebaseUser user;
    private String loginMethod;
    private ProgressDialog progressDialog;
    private ItemValidation iv = new ItemValidation();
    private boolean signUpFlag = false;
    private SessionManager session;
    private LoginButton btnFacebookOri;
    private Button btnLoginFacebook;
    private Button btnLoginGoogle;
    private String jenisLogin = "FACEBOOK";

    private static boolean doubleBackToExitPressedOnce;
    private boolean exitState = false;
    private int timerClose = 2000;

    private static final int REQUEST_PERMISSIONS = 20;

    private String refreshToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        AppEventsLogger.activateApp(this);

        //Check close statement
        doubleBackToExitPressedOnce = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.getBoolean("exit", false)) {
                exitState = true;
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }

        // for android > M
        if (ContextCompat.checkSelfPermission(
                LoginActivity.this, android.Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            LoginActivity.super.requestAppPermissions(new
                            String[]{android.Manifest.permission.WRITE_SETTINGS,
                            android.Manifest.permission.WAKE_LOCK,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, R.string
                            .runtime_permissions_txt
                    , REQUEST_PERMISSIONS);
        }

        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "gmedia.net.id.semargres2018",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/

        session = new SessionManager(LoginActivity.this);

        InitFirebaseSetting.getFirebaseSetting(LoginActivity.this);
        refreshToken = FirebaseInstanceId.getInstance().getToken();

        initGoogleAuth();
        initFirebaseAuth();
        initUI();
        initFacebookAuth();
        //checkLogin();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    //region Google Login
    private void initGoogleAuth() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this,this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleApiClient.connect();
    }

    private void initFirebaseAuth() {

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

        // logout
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            if(bundle.getBoolean("logout",false)){
                try{
                    auth.signOut();
                    LoginManager.getInstance().logOut();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid() + " " + user.getEmail() + " " + user.getDisplayName());
                    if(session.isSaved()){
                        jenisLogin =  session.getUserInfo(SessionManager.TAG_JENIS);
                    }

                    postSuccessLogin();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }
    private void initUI() {

        btnFacebookOri = (LoginButton) findViewById(R.id.btn_fb_ori);
        btnLoginFacebook = (Button) findViewById(R.id.btn_facebook);
        btnLoginGoogle = (Button) findViewById(R.id.btn_google);

        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                LoginManager.getInstance().logOut();
                btnFacebookOri.performClick();
                signUpFlag = true;
            }
        });

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                signIn();
                signUpFlag = true;
            }
        });
    }

    private void signIn() {
        googleApiClient.clearDefaultAccountAndReconnect();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    //region Facebook Authorization
    private void initFacebookAuth() {

        mCallbackManager = CallbackManager.Factory.create();
        btnFacebookOri.setReadPermissions("email", "public_profile");
        btnFacebookOri.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                jenisLogin = "FACEBOOK";
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: "+error.toString());
            }
        });

    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            dismissProgressDialog();
                            Toast.makeText(LoginActivity.this, "Authentication failed. Your account already linked by Google login",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
    //endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Google login
        if(requestCode == RC_SIGN_IN_GOOGLE){

            jenisLogin = "GOOGLE";
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){

                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                showProgressDialog();
            }else{

                Log.d(TAG, "onAuthStateChanged:signed_out");
                // User not Authenticated
            }
        }else{

            // FB Login
            if(resultCode == -1){

                jenisLogin = "FACEBOOK";
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
                showProgressDialog();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            dismissProgressDialog();
                            Toast.makeText(LoginActivity.this, "Authentication failed. Your account already linked by facebook login",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkLogin(){

        user = auth.getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid() + " " + user.getEmail() + " " + user.getDisplayName());

            if(session.isSaved()){
                jenisLogin =  session.getUserInfo(SessionManager.TAG_JENIS);
            }

            postSuccessLogin();
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
            /*try {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                Log.d(TAG, "Sign out from google");
                            }
                        });
            }catch (Exception e){

                e.printStackTrace();
            }*/
        }
    }

    private void postSuccessLogin(){

        if(user != null){

            String uid = "";
            try {
                uid = user.getUid();

            }catch (Exception e){
                e.printStackTrace();
                uid = "";
            }

            if(!uid.equals("")){

                JSONObject jBody = new JSONObject();

                try {

                    jBody.put("uid",user.getUid());
                    jBody.put("nama",user.getDisplayName());
                    jBody.put("email",user.getEmail());
                    jBody.put("foto",String.valueOf(user.getPhotoUrl()));
                    jBody.put("jenis_login",jenisLogin);
                    jBody.put("fcm_id", refreshToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApiVolley request = new ApiVolley(LoginActivity.this, jBody, "POST", ServerURL.login, "", "", 0, new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        dismissProgressDialog();

                        try {
                            JSONObject responseAPI = new JSONObject(result);

                            String status = responseAPI.getJSONObject("metadata").getString("status");

                            if(iv.parseNullDouble(status) == 200){

                                String statusResponse = responseAPI.getJSONObject("response").getString("status");
                                String message = responseAPI.getJSONObject("response").getString("message");
                                String token = "";

                                try {
                                    token = responseAPI.getJSONObject("response").getString("token");
                                }catch (JSONException e){
                                    e.printStackTrace();
                                    token = "";
                                }

                                if(statusResponse.equals("1")){ // terdaftar

                                    if(user != null){

                                        if(session.getKTP().equals("")){ // sudah terdaftar, belum registrasi

                                            session.createLoginSession(user.getUid(),
                                                    (user.getEmail() != null && user.getEmail().length() > 0) ? user.getEmail() : session.getEmail(),
                                                    session.getNama().length() > 0 ? session.getNama() : user.getDisplayName(),
                                                    String.valueOf(user.getPhotoUrl()),
                                                    jenisLogin,
                                                    session.getKTP(),
                                                    "1");
                                            session.updateToken(token);
                                            Toast.makeText(LoginActivity.this, message,Toast.LENGTH_LONG).show();

                                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                            finish();
                                            startActivity(intent);
                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        }else{

                                            session.createLoginSession(user.getUid(),
                                                    (user.getEmail() != null && user.getEmail().length() > 0) ? user.getEmail() : session.getEmail(),
                                                    session.getNama().length() > 0 ? session.getNama() : user.getDisplayName(),
                                                    String.valueOf(user.getPhotoUrl()),
                                                    jenisLogin, session.getKTP(),
                                                    "1");
                                            session.updateToken(token);
                                            Toast.makeText(LoginActivity.this, message,Toast.LENGTH_LONG).show();

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            finish();
                                            startActivity(intent);
                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        }
                                    }else{
                                        Toast.makeText(LoginActivity.this, "Harap login kembali", Toast.LENGTH_LONG).show();
                                        dismissProgressDialog();
                                    }
                                }else if(statusResponse.equals("0")){ // daftarkan dahulu

                                    if(user != null){

                                        registerUser();

                                    }else{
                                        Toast.makeText(LoginActivity.this, "Harap login kembali", Toast.LENGTH_LONG).show();
                                        dismissProgressDialog();
                                    }
                                }else{
                                    Toast.makeText(LoginActivity.this, "Harap login kembali", Toast.LENGTH_LONG).show();
                                    dismissProgressDialog();
                                }
                            }

                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dismissProgressDialog();
                        Toast.makeText(LoginActivity.this, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                Toast.makeText(LoginActivity.this, "Harap login kembali", Toast.LENGTH_LONG).show();
                dismissProgressDialog();
            }
        }else{
            Toast.makeText(LoginActivity.this, "Harap login kembali", Toast.LENGTH_LONG).show();
            dismissProgressDialog();
        }
    }

    private void registerUser(){

        JSONObject jBody = new JSONObject();

        try {

            jBody.put("uid",user.getUid());
            jBody.put("profile_name",user.getDisplayName());
            jBody.put("email",user.getEmail());
            jBody.put("foto",String.valueOf(user.getPhotoUrl()));
            jBody.put("type",jenisLogin);
            jBody.put("fcm_id", refreshToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley request = new ApiVolley(LoginActivity.this, jBody, "POST", ServerURL.register, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();

                try {
                    JSONObject responseAPI = new JSONObject(result);

                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullDouble(status) == 200){

                        String statusResponse = responseAPI.getJSONObject("response").getString("status");
                        String message = responseAPI.getJSONObject("response").getString("message");
                        String token = "";

                        try {
                            token = responseAPI.getJSONObject("response").getString("token");
                        }catch (JSONException e){
                            e.printStackTrace();
                            token = "";
                        }

                        if(statusResponse.equals("1")){ // Register

                            if(user != null){

                                session.createLoginSession(user.getUid(),
                                        (user.getEmail() != null && user.getEmail().length() > 0) ? user.getEmail() : session.getEmail(),
                                        session.getNama().length() > 0 ? session.getNama() : user.getDisplayName(),
                                        String.valueOf(user.getPhotoUrl()),
                                        jenisLogin,
                                        session.getKTP(),
                                        "1");
                                session.updateToken(token);
                                Toast.makeText(LoginActivity.this, message,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                finish();
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                            }else{
                                Toast.makeText(LoginActivity.this, "Harap login kembali", Toast.LENGTH_LONG).show();
                                dismissProgressDialog();
                            }
                        }else if(statusResponse.equals("0")){

                            Toast.makeText(LoginActivity.this, "Harap login kembali", Toast.LENGTH_LONG).show();
                            dismissProgressDialog();
                        }
                    }

                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                dismissProgressDialog();
                Toast.makeText(LoginActivity.this, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setMessage("Memuat...");
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        // Origin backstage
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
            intent.putExtra("exit", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //System.exit(0);
        }

        if(!exitState && !doubleBackToExitPressedOnce){
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.app_exit), Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, timerClose);
    }
}
