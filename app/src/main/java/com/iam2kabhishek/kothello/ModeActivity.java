package com.iam2kabhishek.kothello;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.iam2kabhishek.kothello.R;

public class ModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_mode_layout);
        setTitle(getString(R.string.app_name));
        initializeModeButtons();
    }


    private void initializeModeButtons() {
        findViewById(R.id.two_usersBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), BoardActivity.class);
                startIntent.putExtra(BoardActivity.KEY_MODE, BoardActivity.MODE_TWO_USERS);
                startActivity(startIntent);
            }
        });
    }
}
