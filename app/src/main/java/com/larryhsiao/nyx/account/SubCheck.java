package com.larryhsiao.nyx.account;

import android.app.AlertDialog;
import android.content.Context;
import androidx.fragment.app.FragmentManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.account.api.NyxApi;
import com.larryhsiao.nyx.account.api.SubReq;
import com.larryhsiao.nyx.sync.SyncService;
import com.silverhetch.aura.view.alert.Alert;
import com.silverhetch.clotho.Action;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Action to do premium check.
 */
public class SubCheck implements Action {
    private final Context context;
    private final FragmentManager fragMgr;
    private final String purchaseToken;
    private final Runnable updateView;

    public SubCheck(
        Context context,
        FragmentManager fragMgr,
        String purchaseToken,
        Runnable updateView
    ) {
        this.context = context;
        this.fragMgr = fragMgr;
        this.purchaseToken = purchaseToken;
        this.updateView = updateView;
    }

    @Override
    public void fire() {
        requestSub(false);
    }

    private void requestSub(boolean changeUser) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getIdToken(true).addOnSuccessListener(getTokenResult -> {
                requestSub(changeUser, getTokenResult.getToken());
            });
        }
    }

    private void requestSub(boolean changeUser, String userTokenId) {
        SubReq req = new SubReq();
        req.sku_id = "premium";
        req.purchase_token = purchaseToken;
        req.changeUser = changeUser;
        NyxApi.client().subscription(
            "Bearer " + userTokenId,
            req
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    updateView.run();
                    SyncService.enqueue(context);
                } else if (response.code() == 409) {
                    new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setTitle(R.string.Change_account)
                        .setMessage(
                            R.string.User_not_as_same_as_previous_logged_in__change_to_this_account_)
                        .setPositiveButton(R.string.Yes, (dialog, which) -> requestSub(true))
                        .setNegativeButton(R.string.cancel, (dialog, which) -> logout())
                        .show();
                } else {
                    logout();
                    Alert.Companion.newInstance(
                        100,
                        context.getString(R.string.Invalid_purchase_state)
                    ).show(fragMgr, null);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                logout();
            }
        });
    }

    private void logout() {
        new LogoutAction(context).fire();
        updateView.run();
    }
}
