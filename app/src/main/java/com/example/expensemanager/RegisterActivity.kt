package com.example.expensemanager

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanager.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityRegisterBinding.inflate(layoutInflater)
        supportActionBar?.title = "Register"
        setContentView(binding.root)
//       val  signInRequest = BeginSignInRequest.builder()
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    // Your server's client ID, not your Android client ID.
//                    .setServerClientId(getString(R.string.client_id))
//                    // Only show accounts previously used to sign in.
//                    .setFilterByAuthorizedAccounts(true)
//                    .build())
//            .build()
val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestIdToken(getString(R.string.client_id))
    .requestEmail().build()

        val googleSignInClient = GoogleSignIn.getClient(this,gso)


        binding.loginTV.setOnClickListener{
           startActivity(Intent(this,LoginActivity::class.java));
       }
        binding.createAccountBtn.setOnClickListener{
            val email = binding.emailRegister.text.toString()
            val password = binding.passwordRegister.text.toString()
            if(email.isNotEmpty()&&password.isNotEmpty()){
                MainActivity.auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(this,"User Registered Successfully",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,LoginActivity::class.java))
//                        MainActivity.auth.signInWithCredential()
                        finish()
                    }
                }
                    .addOnFailureListener {
                        Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()

                    }

            }
        }
        binding.googleBtn.setOnClickListener{
googleSignInClient.signOut()
            startActivityForResult(googleSignInClient.signInIntent,13);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 13 && resultCode == RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)!!
          firebaseAuthWithGoogle(account.idToken!!)
        }
    }

    private fun firebaseAuthWithGoogle(idToken:String){
        val credential = GoogleAuthProvider.getCredential(idToken,null);
        MainActivity.auth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
        }
            .addOnFailureListener {
Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()
            }

    }
}