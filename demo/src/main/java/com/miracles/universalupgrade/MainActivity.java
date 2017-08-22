package com.miracles.universalupgrade;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.miracles.annotations.InsertUpgradeCode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @InsertUpgradeCode(id = "db", fromVersion = 1, toVersion = 2)
    public void dbUpgrade1() {

    }

    @InsertUpgradeCode(id = "db", fromVersion = 2, toVersion = 3)
    public void dbUpgrade2() {

    }

    @InsertUpgradeCode(id = "app")
    public void appUpgrade() {

    }

    private void m1(View view) {

    }

}
