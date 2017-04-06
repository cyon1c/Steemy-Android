package io.steemapp.steemy.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.squareup.otto.Subscribe;

import io.steemapp.steemy.R;
import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.events.AccountsEvent;
import io.steemapp.steemy.events.NetworkErrorEvent;
import io.steemapp.steemy.events.NoNetworkEvent;
import io.steemapp.steemy.events.NotLoggedInEvent;
import io.steemapp.steemy.fragments.TransferFragment;
import io.steemapp.steemy.transactions.TransactionManager;

public class TransferActivity extends AbstractActivity implements TransferFragment.TransferFragmentListener{

    private TransferFragment mFragment;

    @Override
    public void onSubmitClicked(String accountName) {
        mService.getAccount(accountName);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_transfer;
    }

    @Override
    public void setActivity() {
        activity = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus.register(this);
        mFragment = TransferFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.transfer_container, mFragment).commit();
    }

    public static Intent intent(Activity activity){
        return new Intent(activity, TransferActivity.class);
    }

    @Subscribe
    public void onEvent(AccountsEvent event){
        if(event.account.getAccounts().size() == 0){
            mFragment.incorrectAccountName();
        }else{
            mTransactionManager.newTx()
                    .addTransferOp(mAccountManager.getAccountName(),
                            event.account.getAccounts().get(0).getName(),
                            mFragment.getTransferAmount(),
                            mFragment.getTransferCurrencyType(),
                            "")
                    .prepareTx()
                    .broadcastTx();
        }
    }

    //ERROR HANDLING HERE
    @Subscribe
    public void onEvent(NetworkErrorEvent event){
        showErrorDialog("Network Error", event.error);
    }

    @Subscribe
    public void onEvent(NotLoggedInEvent event){
        showErrorDialog("Error", "You must be logged in to do that.");
    }

    @Subscribe
    public void onEvent(NoNetworkEvent event){
        showErrorDialog("No Network", null);
    }

    private boolean mIsDialogShowing = false;
    private void showErrorDialog(String title, String message){
        if(!mIsDialogShowing) {
            AlertDialog dialog = SteemyGlobals.buildSteemyErrorDialog(this, title, message);

            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay >", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mIsDialogShowing = false;
                    dialogInterface.dismiss();
                }
            });
            dialog.show();
        }
    }
}
