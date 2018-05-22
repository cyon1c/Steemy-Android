package io.steemapp.steemy.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ViewFlipper;

import io.steemapp.steemy.R;
import io.steemapp.steemy.views.SteemyButton;

public class TermsAndPrivacyActivity extends AppCompatActivity {

    private final static String FIRST_RUN_KEY = "firstrun";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_privacy);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        if(prefs.contains(FIRST_RUN_KEY)) {
        if(true){
            startActivity(HomeListActivity.intent(this));
            finish();
        }

        final ViewFlipper flipper = (ViewFlipper)findViewById(R.id.tos_flipper);
        WebView tos = (WebView)findViewById(R.id.tos);
        WebView pp = (WebView)findViewById(R.id.pp);
        SteemyButton tosButton = (SteemyButton)findViewById(R.id.tos_button);
        SteemyButton ppButton = (SteemyButton)findViewById(R.id.pp_button);
        SteemyButton agreeButton = (SteemyButton)findViewById(R.id.agree_button);

        tos.loadUrl(getString(R.string.tos_link));
        pp.loadUrl(getString(R.string.privacy_link));
        tosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipper.setDisplayedChild(0);
            }
        });
        ppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipper.setDisplayedChild(1);
            }
        });

        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(TermsAndPrivacyActivity.this)
                        .setTitle("Terms of Service & Privacy Policy")
                        .setPositiveButton("I agree", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(HomeListActivity.intent(TermsAndPrivacyActivity.this));
                                finish();
                            }
                        })
                        .setMessage("By tapping \"I Agree\", you confirm that you have read the Terms of Service, that you understand them fully, and that you agree to be bound by them.")
                        .create().show();
            }
        });
    }
}
