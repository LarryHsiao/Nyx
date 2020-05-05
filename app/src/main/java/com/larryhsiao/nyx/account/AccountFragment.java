package com.larryhsiao.nyx.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.base.JotFragment;
import com.larryhsiao.nyx.core.jots.AllJots;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import com.larryhsiao.nyx.core.tags.AllTags;
import com.larryhsiao.nyx.core.tags.QueriedTags;
import com.larryhsiao.nyx.sync.SyncService;
import com.silverhetch.aura.view.bitmap.CircledDrawable;
import com.silverhetch.clotho.file.FileSize;
import com.silverhetch.clotho.file.SizeText;
import com.silverhetch.clotho.source.ConstSource;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.graphics.Bitmap.Config.ARGB_8888;
import static androidx.swiperefreshlayout.widget.CircularProgressDrawable.LARGE;

/**
 * Account page
 */
public class AccountFragment extends JotFragment implements PurchasesUpdatedListener {
    private static final int REQUEST_CODE_LOG_IN = 1000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateView(view);
    }

    private void updateView(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean isLoggedIn = user != null;
        if (isLoggedIn) {
            updateViewLoggedIn(view, user);
        } else {
            updateViewLoggedOut(view);
        }
        TextView staticsText = view.findViewById(R.id.account_jot_statics);
        staticsText.setText("");
        staticsText.append(
            getString(R.string.jots_title,
                "" + new QueriedJots(new AllJots(db)).value().size()));
        staticsText.append("\n");
        staticsText.append(
            getString(R.string.tags_title,
                "" + new QueriedTags(new AllTags(db)).value().size()));
        staticsText.append("\n");
        staticsText.append(getString(
            R.string.Storage_usage_,
            new SizeText(
                new FileSize(
                    new File(
                        getContext().getFilesDir(),
                        "attachments"
                    ).toPath()
                )
            ).value()
            )
        );
        staticsText.append("\n");
    }

    private void loadUserIcon(View view, FirebaseUser user) {
        final CircularProgressDrawable placeholder
            = new CircularProgressDrawable(view.getContext());
        placeholder.setStyle(LARGE);
        final ImageView icon = view.findViewById(R.id.account_icon);
        Glide.with(this).load(user.getPhotoUrl())
            .error(getResources().getDrawable(R.drawable.ic_user, null))
            .placeholder(placeholder)
            .apply(RequestOptions.circleCropTransform())
            .into(icon);
    }

    private void updateViewLoggedOut(View view) {
        Bitmap bitmap = Bitmap.createBitmap(1, 1, ARGB_8888);
        bitmap.eraseColor(Color.GRAY);
        ImageView icon = view.findViewById(R.id.account_icon);
        icon.setImageDrawable(new CircledDrawable(
            getResources(),
            new ConstSource<>(bitmap)
        ).value());
        TextView info = view.findViewById(R.id.account_info);
        info.setText("");
        Button loginLogoutBtn = view.findViewById(R.id.account_login_logout);
        loginLogoutBtn.setText(R.string.login);
        loginLogoutBtn.setOnClickListener(v -> {
            startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build()
                )).build(), REQUEST_CODE_LOG_IN
            );
        });
    }

    private void updateViewLoggedIn(View view, FirebaseUser user) {
        TextView info = view.findViewById(R.id.account_info);
        info.append(getString(R.string.name_title, user.getDisplayName()));
        info.append("\n");
        info.append(getString(R.string.email_title, user.getEmail()));
        Button loginLogoutBtn = view.findViewById(R.id.account_login_logout);
        loginLogoutBtn.setText(R.string.logout);
        loginLogoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            updateView(view);
        });
        loadUserIcon(view, user);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOG_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                updateView(getView());
                SyncService.enqueue(getContext());
            } else {
                Toast.makeText(getContext(),
                    "error " + response,
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {

    }
}
