package com.example.reportgeneration;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TextView dataTextView;
    private Handler mainHandler;
    private Button generateReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainHandler = new Handler(Looper.getMainLooper());

        // Initialize the Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("your_database_path");

        // Initialize the TextView from your XML layout
        dataTextView = findViewById(R.id.dataTextView);

        generateReportButton = findViewById(R.id.dataView);
        generateReportButton.setOnClickListener(view -> {
            // Call a method to retrieve data when the button is clicked
            retrieveDataFromFirebase();
        });
    }

    private void retrieveDataFromFirebase() {
        // Add a ValueEventListener to retrieve data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called when data is retrieved successfully
                final StringBuilder displayText = new StringBuilder();
                HashMap<String, Integer> studentMarksMap = new HashMap<>();

                if (dataSnapshot.exists()) {
                    // Data in the database exists
                    for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                        String studentName = studentSnapshot.getKey();
                        int totalMarks = 0;

                        for (DataSnapshot courseSnapshot : studentSnapshot.getChildren()) {
                            // Access data using snapshot
                            String courseName = courseSnapshot.child("courseName").getValue(String.class);
                            int marks = courseSnapshot.child("marks").getValue(Integer.class);

                            // Append the retrieved data to the displayText
                            displayText.append("Student: ").append(studentName).append(", ");
                            displayText.append("Course Name: ").append(courseName).append(", Marks: ").append(marks).append("\n");

                            // Calculate total marks for the student
                            totalMarks += marks;
                        }

                        // Calculate mean marks for the student
                        int numberOfCourses = (int) studentSnapshot.getChildrenCount();
                        double meanMarks = (double) totalMarks / numberOfCourses;

                        // Display mean marks and total marks for the student
                        displayText.append("Mean Marks: ").append(meanMarks).append(", Total Marks: ").append(totalMarks).append("\n");

                        // Evaluate performance and provide a recommendation
                        String recommendation = evaluatePerformance(totalMarks);
                        displayText.append("Recommendation: ").append(recommendation).append("\n\n");

                        // Store mean marks and total marks in the map
                        studentMarksMap.put(studentName, totalMarks);
                    }
                } else {
                    // Data in the database is empty
                    displayText.append("Database is empty.");
                }

                // Update the TextView with the retrieved data on the main thread
                mainHandler.post(() -> {
                    dataTextView.setText(displayText.toString());
                    // You can use studentMarksMap for any further processing or display
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // This method is called if there is an error
                Log.e("Firebase", "Data retrieval failed: " + databaseError.getMessage());
            }
        });
    }

    private String evaluatePerformance(int totalMarks) {
        // Add logic to evaluate performance and provide a recommendation
        if (totalMarks >= 400) {
            return "A (Excellent)";
        } else if (totalMarks >= 300) {
            return "B";
        } else if (totalMarks >= 200) {
            return "C";
        } else if (totalMarks >= 100) {
            return "D";
        } else {
            return "Not Classified";
        }
    }
}
