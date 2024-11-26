package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewStatusActivity extends AppCompatActivity {

    private static final String TAG = "ViewStatusActivity";
    private TextView statusTextView;
    private TextView nameTextView;
    private TextView rollNumberTextView;

    private FirebaseFirestore db;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        statusTextView = findViewById(R.id.statusTextView);
        nameTextView = findViewById(R.id.nameTextView);
        rollNumberTextView = findViewById(R.id.rollNumberTextView);

        // Get the email passed from the previous activity
        userEmail = getIntent().getStringExtra("EMAIL");

        Log.d(TAG, "User email received: " + userEmail);

        // Fetch student details based on email
        if (userEmail != null) {
            fetchStudentDetails(userEmail);
        } else {
            Toast.makeText(this, "No email provided", Toast.LENGTH_SHORT).show();
        }
    }

    // Fetch the student's details (name and roll number) based on the email
    private void fetchStudentDetails(String email) {
        Log.d(TAG, "Fetching student details for email: " + email);
        db.collection("users").document(email)
                .get()
                .addOnCompleteListener(studentTask -> {
                    if (studentTask.isSuccessful() && studentTask.getResult() != null) {
                        DocumentSnapshot studentDoc = studentTask.getResult();
                        if (studentDoc.exists()) {
                            String studentName = studentDoc.getString("name");
                            String rollNumber = studentDoc.getString("rollNumber");

                            nameTextView.setText("Name: " + studentName);
                            rollNumberTextView.setText("Roll Number: " + rollNumber);

                            // Fetch the outpass requests after fetching the student's details
                            fetchOutpassRequests(email);
                        } else {
                            Log.e(TAG, "Student document does not exist");
                            Toast.makeText(ViewStatusActivity.this, "Student not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Error fetching student details: ", studentTask.getException());
                        Toast.makeText(ViewStatusActivity.this, "Error fetching student details: " +
                                (studentTask.getException() != null ? studentTask.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Fetch all outpass requests based on the student's email
    private void fetchOutpassRequests(String email) {
        Log.d(TAG, "Fetching all outpass requests for email: " + email);

        db.collection("outpassRequests")
                .whereEqualTo("userEmail", email) // Use 'userEmail' to match the field in Firestore
                .get() // Fetch all documents related to this email
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Outpass requests fetch successful");

                        if (!task.getResult().isEmpty()) {
                            Log.d(TAG, "Documents found: " + task.getResult().getDocuments().size());

                            statusTextView.setText(""); // Clear previous text

                            for (DocumentSnapshot requestDoc : task.getResult().getDocuments()) {
                                Log.d(TAG, "Fetched Document: " + requestDoc.getData()); // Log entire document

                                // Extract fields
                                 // Advisor status
                                String reason = requestDoc.getString("reason");
                                String dateFrom = requestDoc.getString("dateFrom"); // Change as per actual field name
                                String dateTo = requestDoc.getString("dateTo"); // Change as per actual field name
                                String outTime = requestDoc.getString("outTime"); // Change as per actual field name
                                String inTime = requestDoc.getString("inTime"); // Change as per actual field name
                                String advisorStatus = requestDoc.getString("status"); // This is advisor status
                                String wardenStatus = requestDoc.getString("wstatus"); // This is warden status

                                // Append details to the text view
                                statusTextView.append("Reason: " + reason + "\n");
                                statusTextView.append("Date From: " + dateFrom + "\n");
                                statusTextView.append("Date To: " + dateTo + "\n");
                                statusTextView.append("Out Time: " + outTime + "\n");
                                statusTextView.append("In Time: " + inTime + "\n");
                                statusTextView.append("Advisor Status: " + advisorStatus + "\n");
                                statusTextView.append("Warden Status: " + wardenStatus + "\n");
                                statusTextView.append("----------------------\n");
                            }
                        } else {
                            Log.d(TAG, "No documents found for the user.");
                            statusTextView.setText("No updates yet.");
                        }
                    } else {
                        Log.e(TAG, "Error fetching status: ", task.getException());
                        Toast.makeText(ViewStatusActivity.this, "Error fetching status: " +
                                (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
