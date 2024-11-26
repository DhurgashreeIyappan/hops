package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HostelInfoActivity extends AppCompatActivity implements AcceptedRequestAdapter.RequestActionCallback {

    private ListView acceptedRequestListView;
    private FirebaseFirestore db;
    private List<OutpassRequest> acceptedRequestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel_info);

        acceptedRequestListView = findViewById(R.id.acceptedRequestListView);
        db = FirebaseFirestore.getInstance();
        acceptedRequestList = new ArrayList<>();

        fetchAcceptedRequests(); // Fetch accepted requests
    }

    private void fetchAcceptedRequests() {
        String wardenEmail = getIntent().getStringExtra("WARDEN_EMAIL");

        // Fetch only requests that are still pending
        db.collection("outpassRequests")
                .whereEqualTo("wardenEmail", wardenEmail)
                .whereEqualTo("wstatus", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        acceptedRequestList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            OutpassRequest request = document.toObject(OutpassRequest.class);
                            fetchUserRoomNumber(request);
                        }
                    } else {
                        Toast.makeText(HostelInfoActivity.this, "No pending requests for this warden", Toast.LENGTH_SHORT).show();
                        updateAcceptedRequestList();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HostelInfoActivity.this, "Error fetching requests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchUserRoomNumber(OutpassRequest request) {
        String userEmail = request.getUserEmail(); // Ensure this is the correct identifier
        Log.d("HostelInfoActivity", "Fetching room number for user: " + userEmail);

        db.collection("users").whereEqualTo("email", userEmail).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot userDocument = queryDocumentSnapshots.getDocuments().get(0);
                        String roomNumber = userDocument.getString("roomNumber");
                        request.setRoomNumber(roomNumber); // Set the room number in the request
                        acceptedRequestList.add(request); // Add the request with room number
                    } else {
                        Toast.makeText(HostelInfoActivity.this, "User not found for email: " + userEmail, Toast.LENGTH_SHORT).show();
                    }
                    updateAcceptedRequestList(); // Update the ListView after fetching all room numbers
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HostelInfoActivity.this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateAcceptedRequestList() {
        AcceptedRequestAdapter adapter = new AcceptedRequestAdapter(this, acceptedRequestList, this);
        acceptedRequestListView.setAdapter(adapter); // Set the adapter to the ListView
    }

    @Override
    public void onRequestAction(String requestId, String wstatus) {
        Log.d("HostelInfoActivity", "onRequestAction called with requestId: " + requestId + ", wstatus: " + wstatus);

        // Ensure requestId is not null or empty
        if (requestId == null || requestId.isEmpty()) {
            Log.e("HostelInfoActivity", "Invalid requestId");
            return;
        }

        db.collection("outpassRequests").document(requestId)
                .update("wstatus", wstatus)
                .addOnSuccessListener(aVoid -> {
                    Log.d("HostelInfoActivity", "Successfully updated wstatus to " + wstatus);
                    Toast.makeText(HostelInfoActivity.this, "Warden status updated to " + wstatus, Toast.LENGTH_SHORT).show();

                    // Remove the updated request from the list
                    acceptedRequestList.removeIf(request -> request.getRequestId().equals(requestId));

                    // Update the ListView
                    updateAcceptedRequestList();
                })
                .addOnFailureListener(e -> {
                    Log.e("HostelInfoActivity", "Error updating request: " + e.getMessage());
                    Toast.makeText(HostelInfoActivity.this, "Error updating request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void updateRequestStatus(String requestId, String newStatus) {
        // Implement if needed, but not required in this context
    }
}
