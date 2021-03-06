package com.example.sistemashacienda;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

public class Webview extends AppCompatActivity {

    private static final String URL="http://www.sistemas-hacienda.sanluis.gov.ar/nuevositio/sistemas/";

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    public String mToken;
    public String correo_gmail;
    boolean control=true;
    private static final int RC_SIGN_IN = 777 ;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //inicio configuracion log google
        //GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          //      .requestEmail()
            //    .build();

       // mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //OAuthProvider.Builder provider = OAuthProvider.newBuilder("yahoo.com");
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");


        //fin  configuracion log google


        super.onCreate(savedInstanceState);
        //Obtiene el token de firebse y lo pega en el clipboad
        obtenerToken();

        setContentView(R.layout.activity_webview);
        webView = (WebView) findViewById(R.id.idWebView);
        swipeRefreshLayout=findViewById(R.id.idSwipeRefreshLayout);

        webView.loadUrl(URL);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });

        webView.setWebViewClient( new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                swipeRefreshLayout.setRefreshing(true);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(control) {
                    webView.setVisibility(View.VISIBLE);
                    setTheme(R.style.AppTheme);
                    control=false;
                }
                swipeRefreshLayout.setRefreshing(false);
                super.onPageFinished(view, url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.addJavascriptInterface(new WebViewJSInterface(this), "Android");

        webView.setDownloadListener(new DownloadListener()
        {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {

                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));

                request.setMimeType(mimeType);

                String cookies = CookieManager.getInstance().getCookie(url);

                request.addRequestHeader("cookie", cookies);

                request.addRequestHeader("User-Agent", userAgent);

                request.setDescription("Descargando...");

                request.setTitle(URLUtil.guessFileName(url, contentDisposition,
                        mimeType));

                request.allowScanningByMediaScanner();

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                                url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Descargando...",
                        Toast.LENGTH_LONG).show();
            }});



        //signIn();//llama al metodo para loguear con google

        //inicio logueo con yahoo
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth
                .startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                // User is signed in.
                                // IdP data available in
                                Toast.makeText(Webview.this,  authResult.getUser().getEmail(), Toast.LENGTH_SHORT).show();
                               // authResult.getAdditionalUserInfo().getUsername();
                                // The OAuth access token can be retrieved:
                                //authResult.getCredential().getAccessToken().
                                // Yahoo OAuth ID token can also be retrieved:
                                // authResult.getCredential().getIdToken().

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Webview.this,e.toString(), Toast.LENGTH_SHORT).show();
                                // Handle failure.

                            }
                        });

        // fin logeuo yahoo




    }

    @Override
    public void onBackPressed() {
        if (webView.isFocused() && webView.canGoBack()) {

            webView.goBack(); //si puede volver atras llama a goback();

        } else {
            cerrarAplicacion();
            //super.onBackPressed();
        }
    }

    private void cerrarAplicacion() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.notificacion)
                .setTitle("¿Realmente desea cerrar la aplicación?")
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {// un listener que al pulsar, cierre la aplicacion
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid()); //Su funcion es algo similar a lo que se llama cuando se presiona el botón "Forzar Detención" o "Administrar aplicaciones", lo cuál mata la aplicación
                        //finish(); Si solo quiere mandar la aplicación a segundo plano
                    }
                }).show();
    }


    ///Firebase
    private void obtenerToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Webview.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                mToken = instanceIdResult.getToken();
                setClipboard(Webview.this,mToken);

            }
        });
    }
    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
        //Toast.makeText(Webview.this, "token copiado", Toast.LENGTH_SHORT).show();
    }

    ///fin firebase

    private void signIn() {  //llamar al metodo para loguearse

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        if( GoogleSignIn.getLastSignedInAccount(this)!= null)
        {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            correo_gmail =account.getEmail().toString();
            Toast.makeText(this,correo_gmail, Toast.LENGTH_SHORT).show();

        }


    }



}
