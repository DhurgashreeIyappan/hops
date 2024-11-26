package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class NewOutpassActivity extends AppCompatActivity {

    private TextView nameTextView, rollNumberTextView;
    private EditText reasonEditText, dateFromEditText, dateToEditText, outTimeEditText, inTimeEditText;
    private Button submitButton;
    private FirebaseFirestore db;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_outpass);

        // Initialize Firestore and views
        db = FirebaseFirestore.getInstance();
        nameTextView = findViewById(R.id.nameTextView);
        rollNumberTextView = findViewById(R.id.rollNumberTextView);
        reasonEditText = findViewById(R.id.reasonEditText);
        dateFromEditText = findViewById(R.id.dateFromEditText);
        dateToEditText = findViewById(R.id.dateToEditText);
        outTimeEditText = findViewById(R.id.outTimeEditText);
        inTimeEditText = findViewById(R.id.inTimeEditText);
        submitButton = findViewById(R.id.submitButton);

        // Retrieve the user email
        userEmail = getIntent().getStringExtra("EMAIL");
        if (userEmail == null) {
            Toast.makeText(this, "User email is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch user information
        fetchUserInfo(userEmail);
    }

    private void fetchUserInfo(String email) {
        db.collection("users")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String rollNumber = documentSnapshot.getString("rollNumber");
                        String advisorEmail = documentSnapshot.getString("advisorEmail");
                        String wardenEmail = documentSnapshot.getString("wardenEmail"); // Updated field name

                        nameTextView.setText("Name: " + name);
                        rollNumberTextView.setText("Roll Number: " + rollNumber);

                        // Set up submit button listener
                        submitButton.setOnClickListener(v -> {
                            Log.d("ButtonClick", "Submit button clicked");
                            submitOutpassRequest(name, rollNumber, advisorEmail, wardenEmail); // Pass wardenEmail
                        });
                    } else {
                        Toast.makeText(NewOutpassActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching user info: " + e.getMessage());
                    Toast.makeText(NewOutpassActivity.this, "Error fetching user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void submitOutpassRequest(String name, String rollNumber, String advisorEmail, String wardenEmail) {
        String reason = reasonEditText.getText().toString().trim();
        String dateFrom = dateFromEditText.getText().toString().trim();
        String dateTo = dateToEditText.getText().toString().trim();
        String outTime = outTimeEditText.getText().toString().trim();
        String inTime = inTimeEditText.getText().toString().trim();

        // Validate inputs
        if (reason.isEmpty() || dateFrom.isEmpty() || dateTo.isEmpty() || outTime.isEmpty() || inTime.isEmpty()) {
            Toast.makeText(NewOutpassActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log input values
        Log.d("OutpassRequest", "Submitting request with: " +
                "User Email: " + userEmail +
                ", Reason: " + reason +
                ", Date From: " + dateFrom +
                ", Date To: " + dateTo +
                ", Out Time: " + outTime +
                ", In Time: " + inTime);

        // Create a unique request ID
        String requestId = userEmail + "_" + System.currentTimeMillis();

        // Create OutpassRequest object
        OutpassRequest outpassRequest = new OutpassRequest();
        outpassRequest.setRequestId(requestId);
        outpassRequest.setUserEmail(userEmail);
        outpassRequest.setReason(reason);
        outpassRequest.setDateFrom(dateFrom);
        outpassRequest.setDateTo(dateTo);
        outpassRequest.setOutTime(outTime);
        outpassRequest.setInTime(inTime);
        outpassRequest.setAdvisorEmail(advisorEmail);
        outpassRequest.setWardenEmail(wardenEmail); // Set the selected warden's email
        outpassRequest.setStatus("pending");
        outpassRequest.setName(name);
        outpassRequest.setRollNumber(rollNumber);
        outpassRequest.setTimestamp(System.currentTimeMillis());
        outpassRequest.setWstatus("pending");

        // Save the request to Firestore
        db.collection("outpassRequests").document(requestId).set(outpassRequest)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(NewOutpassActivity.this, "Outpass request submitted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error submitting request: " + e.getMessage());
                    Toast.makeText(NewOutpassActivity.this, "Error submitting request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
