package com.example.android.googlechallenge;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.googlechallenge.Models.Thought;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditorActivity extends AppCompatActivity {
    private TextInputEditText mThoughtTitleInput;
    private TextInputEditText mThoughtTitleContentInput;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    // is the editor in update or add mode
    private boolean isEdit;
    private Thought selectedThought;

    private static final String EDIT_INTENT_KEY = "EDIT_THOUGHT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mThoughtTitleInput = findViewById(R.id.thought_title);
        mThoughtTitleContentInput = findViewById(R.id.thought_content);
        Button saveBtn = findViewById(R.id.btn_save);
        Button deleteBtn = findViewById(R.id.btn_delete_thought);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteThought();
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        // Edit or Add ?
        if (intent.hasExtra(EDIT_INTENT_KEY)) {
            // edit
            isEdit = true;
            deleteBtn.setVisibility(View.VISIBLE);
            selectedThought = intent.getParcelableExtra(EDIT_INTENT_KEY);
            mThoughtTitleInput.setText(selectedThought.getTitle());
            mThoughtTitleContentInput.setText(selectedThought.getContent());
        }
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isThoughtInValid()) {
                    Toast.makeText(EditorActivity.this, getString(R.string.invalid_entries), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isEdit)
                    updateThought();
                else
                    addThought();
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLogin();
    }

    private void checkLogin() {
        if (mAuth.getCurrentUser() != null)
            mDatabase = FirebaseDatabase.getInstance().getReference("thoughts").child(mAuth.getCurrentUser().getUid());
        else {
            Intent loginIntent = new Intent(this, MainActivity.class);
            startActivity(loginIntent);
        }
    }

    private void addThought() {
        String title = mThoughtTitleInput.getText().toString();
        String content = mThoughtTitleContentInput.getText().toString();
        String id = mDatabase.push().getKey();
        Thought thought = new Thought(id, title, content);
        mDatabase.child(id).setValue(thought);
        Toast.makeText(EditorActivity.this, getString(R.string.thought_added_success), Toast.LENGTH_SHORT).show();
    }

    private void updateThought() {
        selectedThought.setTitle(mThoughtTitleInput.getText().toString());
        selectedThought.setContent(mThoughtTitleContentInput.getText().toString());
        mDatabase.child(selectedThought.getId()).setValue(selectedThought);
        Toast.makeText(EditorActivity.this, getString(R.string.thought_updated_success), Toast.LENGTH_SHORT).show();
    }

    /**
     * Delete existing thought
     */
    private void deleteThought() {
        if (selectedThought != null) {
            mDatabase.child(selectedThought.getId()).setValue(null);
        }
    }

    private boolean isThoughtInValid() {
        return TextUtils.isEmpty(mThoughtTitleInput.getText().toString()) ||
                TextUtils.isEmpty(mThoughtTitleContentInput.getText().toString());
    }
}
