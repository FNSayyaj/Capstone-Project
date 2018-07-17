package com.aboelfer.knightrider.radicalnews.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.aboelfer.knightrider.radicalnews.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.licence_btn)
    Button licenceBTN;
    @BindView(R.id.usePolicy_btn)
    Button usePolicyBTN;
    @BindView(R.id.appVersion_btn)
    Button appVersionBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setTitle("About");
        ButterKnife.bind(this);

    }

    public void Licence(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getResources().getString(R.string.licence_url)));
        startActivity(intent);
    }

    public void Version(View view) {
        Toast.makeText(this, getResources().getString(R.string.app_version), Toast.LENGTH_LONG).show();
    }

    public void Policy(View view) {
        Toast.makeText(this, getResources().getString(R.string.use_policy), Toast.LENGTH_LONG).show();
    }
}
