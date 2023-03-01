package com.authentication.mvvm.feature.login

import android.content.Intent
import android.content.IntentSender
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.authentication.mvvm.R
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.authentication.mvvm.base.BaseActivity
import com.authentication.mvvm.databinding.ActivityLoginBinding
import com.authentication.mvvm.feature.MainActivity
import com.authentication.mvvm.model.exception.ApiException
import com.authentication.mvvm.utils.extension.view.clicks
import com.authentication.mvvm.utils.extension.view.gone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>(LoginViewModel::class) {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    companion object {
        fun newInstance() = LoginActivity()
    }

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityLoginBinding =
        ActivityLoginBinding.inflate(inflater)

    override fun initialize() {
        setupGoogle()
        setupFacebook()
        with(viewBinding) {
            cvLogin.clicks {
                gLogin.gone(isGone = viewModel.isShowLogin)
                viewModel.isShowLogin = !viewModel.isShowLogin
            }
            cvSignUp.clicks {
                gSignup.gone(isGone = viewModel.isShowSignup)
                viewModel.isShowSignup = !viewModel.isShowSignup
            }
            // Get email and password to register with firebase
            btnSignup.clicks {
                signUpWithAccount(
                    email = edtEmailSingup.text.toString(),
                    password = edtPasswordSignup.text.toString()
                )
            }
            // Get email and password to login with firebase
            btnLogin.clicks {
                loginWithAccount(
                    email = edtEmailLogin.text.toString(),
                    password = edtPasswordLogin.text.toString()
                )
            }
        }
    }

    // Update ui when login success
    private fun updateUI() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    private fun signUpWithAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Toast.makeText(this, "Signup Success", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                }
            }
    }

    private fun loginWithAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI()
                } else {
                    // If sign in fails, display a message to the user.
                }
            }
    }

    private fun setupGoogle() {
        // Open one tap login Google
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    //web_client_id get from google console
                    .setServerClientId(getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
        auth = Firebase.auth
        viewBinding.cvGoogle.clicks {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        getResult.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                        // Error - Bypass
                    }
                }
                .addOnFailureListener(this) {
                    // Error - Bypass
                }
        }
    }

    private fun setupFacebook() {
        callbackManager = CallbackManager.Factory.create()
        viewBinding.cvFacebook.clicks {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("public_profile", "email"))
        }
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            if (task.isSuccessful) {
                                handleFacebookAccessToken(result.accessToken)
                            } else {
                                // If sign in fails, display a message to the user.
                            }
                        }
                }

                override fun onCancel() {
                    // Cancel
                }

                override fun onError(error: FacebookException) {
                    // Error
                }
            })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI()
                } else {
                    // If sign in fails, display a message to the user.
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(it.data)
                val idToken = credential.googleIdToken
                when {
                    idToken != null -> {
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    val user = auth.currentUser
                                    updateUI()
                                } else {
                                    // Token null
                                    // Error - Bypass
                                }
                            }
                            .addOnFailureListener(this) {
                                // Error - Bypass
                            }
                    }
                    else -> {
                        // Error - Bypass
                    }
                }
            } catch (e: ApiException) {
                // Error - Bypass
            }
        }
}