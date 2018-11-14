package com.contri;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Constraints;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.contri.Contri.mCredential;
import static com.contri.Contri.mUser;

public class VerifyPhone extends AppCompatActivity {

    ImageView profile_image;
    TextView profile_name;
    FrameLayout frameLayout;
    ProgressBar progressBar;
    String verificationID,phoneNumber;
    FragmentManager fragmentManager;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_verify_phone);
        profile_image=findViewById(R.id.verify_photo_im);
        profile_name=findViewById(R.id.verify_name_tv);
        Picasso.get().load(mUser.getPhotoUrl()).into(profile_image);
        Log.i("Profile Photo",mUser.getPhotoUrl().toString());
        profile_name.setText(mUser.getDisplayName());
        progressBar=findViewById(R.id.progress_circular);
        frameLayout=findViewById(R.id.verify_container_fl);
        fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.verify_container_fl,new EnterNumber()).commit();

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(Constraints.TAG, "onVerificationCompleted:$credential");
                setProgress(false);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                setProgress(false);
                Log.w(Constraints.TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException)
                Toast.makeText(getBaseContext(),"Invalid Request: $e", Toast.LENGTH_LONG).show();
                else if (e instanceof FirebaseTooManyRequestsException)
                Toast.makeText(getBaseContext(),"The SMS quota for the project has been exceeded: $e", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                verificationID=verificationId;
                setProgress(false);
                fragmentManager.beginTransaction().replace(R.id.verify_container_fl,new EnterOtp()).commit();
            }
        };
    }


    public static class EnterNumber extends Fragment{
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.f_enter_number,container,false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
        {
            super.onViewCreated(view, savedInstanceState);
            final EditText text=view.findViewById(R.id.editNumber);
            view.findViewById(R.id.cardLogIn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s=text.getText().toString();
                    Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();
                    if(!TextUtils.isEmpty(s) && s.length()==10)
                    ((VerifyPhone)Objects.requireNonNull(getContext())).verifyPhone(s);
                }
            });
        }
    }

    private void verifyPhone(final String phone) {
        setProgress(true);
        phoneNumber="+91"+phone;
        DatabaseReference refer= FirebaseDatabase.getInstance().getReference();
        refer.child("users").orderByChild("phn").equalTo(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null) {
                            setProgress(false);
                            Toast.makeText(getBaseContext(), "Number Already Registered!", Toast.LENGTH_LONG).show();

                        }
                        else{


                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    phoneNumber,        // Phone number to verify
                                    60,                 // Timeout duration
                                    TimeUnit.SECONDS,   // Unit of timeout
                                    VerifyPhone.this,               // Activity (for callback binding)
                                    mCallbacks);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        // OnVerificationStateChangedCallbacks
    }

    public static class EnterOtp extends Fragment{

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.f_enetr_otp,container,false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            final EditText edit_otp=view.findViewById(R.id.otp_et);
            CardView btn_submit=view.findViewById(R.id.btn_sub);
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s=(edit_otp.getText()!=null)?edit_otp.getText().toString():null;

                    ((VerifyPhone)Objects.requireNonNull(getContext())).verifyOtpandSignIn(s);
                }
            });
            view.findViewById(R.id.btn_sub2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String s=(edit_otp.getText()!=null)?edit_otp.getText().toString():null;
                    Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();
                    ((VerifyPhone)Objects.requireNonNull(getContext())).verifyOtpandSignIn(s);
                }
            });
        }
    }

    void verifyOtpandSignIn(String code){
        setProgress(true);
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationID,code);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setProgress(false);
                if (task.isSuccessful()){
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(Constraints.TAG, "signInWithCredential:success");
                    final FirebaseUser user = task.getResult().getUser();
                    DatabaseReference refer=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                    refer.child("phn").setValue(phoneNumber);
                    refer.child("name").setValue(mUser.getDisplayName());
                    Toast.makeText(getBaseContext(),"Number Verified", Toast.LENGTH_LONG).show();


                }
                else{
                    // Sign in failed, display a message and update the UI
                    Log.w(Constraints.TAG, "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(getBaseContext(),"Code doesn't match", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    void setProgress(Boolean s){
        if(s) {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }else {
            //To get user interaction back you just need to add the following code
            progressBar.setVisibility(View.INVISIBLE) ;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
