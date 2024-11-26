package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText emailEditText;
    private Button loginButton;
    private LinearLayout selectionLayout;
    private Button wardenButton, advisorButton;
    private String selectedEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore and UI components
        db = FirebaseFirestore.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        loginButton = findViewById(R.id.loginButton);
        selectionLayout = findViewById(R.id.selectionLayout);
        wardenButton = findViewById(R.id.wardenButton);
        advisorButton = findViewById(R.id.advisorButton);

        // Set the login button click listener
        loginButton.setOnClickListener(v -> handleLogin());

        // Set up selection button click listeners
        wardenButton.setOnClickListener(v -> navigateToWardenActivity(selectedEmail)); // Pass the selected email
        advisorButton.setOnClickListener(v -> navigateToAdvisorInfo(selectedEmail));
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            showToast("Please enter your email");
            return;
        }

        if (email.endsWith("@kongu.edu")) {
            checkWardenOrAdvisorEmail(email);
        } else {
            showToast("Invalid email domain. Please use @kongu.edu");
        }
    }

    private void checkWardenOrAdvisorEmail(String email) {
        db.collection("warden")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isWarden = !task.getResult().isEmpty();
                        checkAdvisorEmail(email, isWarden);
                    } else {
                        showToast("Error checking warden email: " + task.getException().getMessage());
                    }
                });
    }

    private void checkAdvisorEmail(String email, boolean isWarden) {
        db.collection("advisors")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isAdvisor = !task.getResult().isEmpty();
                        if (isWarden && isAdvisor) {
                            showSelectionLayout(email);
                        } else if (isWarden) {
                            navigateToWardenActivity(email); // Pass email directly here
                        } else if (isAdvisor) {
                            navigateToAdvisorInfo(email);
                        } else {
                            checkStudentEmail(email);
                        }
                    } else {
                        showToast("Error checking advisor email: " + task.getException().getMessage());
                    }
                });
    }

    private void showSelectionLayout(String email) {
        selectedEmail = email;
        selectionLayout.setVisibility(View.VISIBLE);
    }

    private void checkStudentEmail(String email) {
        db.collection("users")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        navigateToDisplayUserInfo(email);
                    } else {
                        navigateToUserinfo(email);
                    }
                })
                .addOnFailureListener(e -> {
                    showToast("Error checking student email: " + e.getMessage());
                });
    }

    private void navigateToUserinfo(String email) {
        Intent intent = new Intent(MainActivity.this, Userinfo.class);
        intent.putExtra("EMAIL", email);
        startActivity(intent);
    }

    private void navigateToDisplayUserInfo(String email) {
        Intent intent = new Intent(MainActivity.this, DisplayUserInfoActivity.class);
        intent.putExtra("EMAIL", email);
        startActivity(intent);
    }

    private void navigateToWardenActivity(String email) {
        Intent intent = new Intent(MainActivity.this, HostelInfoActivity.class);
        intent.putExtra("WARDEN_EMAIL", email); // Pass the warden's email
        startActivity(intent);
    }

    private void navigateToAdvisorInfo(String email) {
        Intent intent = new Intent(MainActivity.this, AdvisorInfoActivity.class);
        intent.putExtra("EMAIL", email);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
