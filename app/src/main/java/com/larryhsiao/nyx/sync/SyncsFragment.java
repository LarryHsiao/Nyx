package com.larryhsiao.nyx.sync;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.larryhsiao.nyx.R;
import com.larryhsiao.nyx.account.api.NyxApi;
import com.larryhsiao.nyx.account.api.SubReq;
import com.larryhsiao.nyx.base.JotFragment;
import com.larryhsiao.nyx.core.jots.AllJots;
import com.larryhsiao.nyx.core.jots.QueriedJots;
import com.larryhsiao.nyx.core.tags.AllTags;
import com.larryhsiao.nyx.core.tags.QueriedTags;
import com.silverhetch.aura.view.alert.Alert;
import com.silverhetch.aura.view.bitmap.CircledDrawable;
import com.silverhetch.aura.view.span.ClickableStr;
import com.silverhetch.aura.view.span.ColoredStr;
import com.silverhetch.clotho.file.FileSize;
import com.silverhetch.clotho.file.SizeText;
import com.silverhetch.clotho.source.ConstSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;
import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.graphics.Color.BLUE;
import static android.view.View.GONE;
import static android.widget.Toast.LENGTH_SHORT;
import static androidx.swiperefreshlayout.widget.CircularProgressDrawable.LARGE;

/**
 * Fragment for login Firebase.
 */
public class SyncsFragment extends JotFragment {
    private static final int REQUEST_CODE_LOG_IN = 1000;
    private static final String ARG_PURCHASE_TOKEN = "ARG_PURCHASE_TOKEN";

    public static Fragment newInstance(String token) {
        Fragment frag = new SyncsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PURCHASE_TOKEN, token);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true).addOnSuccessListener(
            new OnSuccessListener<GetTokenResult>() {
                @Override
                public void onSuccess(GetTokenResult getTokenResult) {
                    System.out.println(getTokenResult.getToken()+"  AAAA++++");
                }
            });
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
            R.layout.page_backup,
            container,
            false
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView title = view.findViewById(R.id.backup_title);
        title.setText(getString(R.string.Syncs));
        view.findViewById(R.id.backup_backupBtn).setVisibility(GONE);
        view.findViewById(R.id.backup_restoreBtn).setVisibility(GONE);
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
        TextView staticsText = view.findViewById(R.id.backup_accountInfo);
        staticsText.append(
            getString(R.string.jots_title,
                "" + new QueriedJots(new AllJots(db)).value().size()));
        staticsText.append("\n");
        staticsText.append(
            getString(R.string.tags_title,
                "" + new QueriedTags(new AllTags(db)).value().size())
        );
        staticsText.append("\n");
        staticsText.append(
            getString(R.string.Storage_usage_,
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

    private void updateViewLoggedOut(View view) {
        Bitmap bitmap = Bitmap.createBitmap(1, 1, ARGB_8888);
        bitmap.eraseColor(Color.GRAY);
        ImageView icon = view.findViewById(R.id.backup_userIcon);
        icon.setImageDrawable(new CircledDrawable(
            getResources(),
            new ConstSource<>(bitmap)
        ).value());
        TextView info = view.findViewById(R.id.backup_accountInfo);
        info.setText(new ClickableStr(
            new ColoredStr(
                new ConstSource<>(getString(R.string.login)),
                BLUE
            ),
            () -> startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(Collections.singletonList(
                    new AuthUI.IdpConfig.GoogleBuilder().build()
                )).build(), REQUEST_CODE_LOG_IN
            )
        ).value());
        info.append("\n");
        info.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void updateViewLoggedIn(View view, FirebaseUser user) {
        TextView info = view.findViewById(R.id.backup_accountInfo);
        info.setText(new ClickableStr(
            new ColoredStr(
                new ConstSource<>(getString(R.string.logout)),
                BLUE
            ),
            this::logout
        ).value());
        info.append("\n");
        info.append(getString(R.string.name_title, user.getDisplayName()));
        info.append("\n");
        info.append(getString(R.string.email_title, user.getEmail()));
        info.append("\n");
        info.setMovementMethod(LinkMovementMethod.getInstance());
        loadUserIcon(view, user);
    }

    private void logout() {
        View view = requireView();
        view.getContext().stopService(new Intent(view.getContext(), SyncService.class));
        AuthUI.getInstance().signOut(view.getContext());
        FirebaseAuth.getInstance().signOut();
        updateView(view);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOG_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                requestSub(false);
            } else {
                Toast.makeText(getContext(), "error " + response, LENGTH_SHORT).show();
            }
        }
    }

    private void requestSub(boolean changeUser){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            user.getIdToken(true).addOnSuccessListener(getTokenResult -> {
                requestSub(changeUser, getTokenResult.getToken());
            });
        }
    }

    private void requestSub(boolean changeUser, String token) {
        SubReq req = new SubReq();
        req.sku_id = "premium";
        req.purchase_token = getArguments().getString(ARG_PURCHASE_TOKEN, "");
        req.changeUser = changeUser;
        NyxApi.client().subscription(
            "Bearer " +token,
            req
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    updateView(getView());
                    SyncService.enqueue(getContext());
                } else if (response.code() == 409) {
                    new AlertDialog.Builder(getContext())
                        .setCancelable(false)
                        .setTitle(R.string.Change_account)
                        .setMessage(R.string.User_not_as_same_as_previous_logged_in__change_to_this_account_)
                        .setPositiveButton(R.string.Yes, (dialog, which) -> requestSub(true))
                        .setNegativeButton(R.string.cancel, (dialog, which) -> logout())
                        .show();
                } else {
                    logout();
                    Alert.Companion.newInstance(
                        100,
                        getString(R.string.Invalid_purchase_state)
                    ).show(getChildFragmentManager(), null);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                logout();
            }
        });
    }

    private void loadUserIcon(View view, FirebaseUser user) {
        final CircularProgressDrawable placeholder = new CircularProgressDrawable(view.getContext());
        placeholder.setStyle(LARGE);
        placeholder.start();
        final ImageView icon = view.findViewById(R.id.backup_userIcon);
        Glide.with(this).load(user.getPhotoUrl())
            .error(getResources().getDrawable(R.drawable.ic_user, null))
            .placeholder(placeholder)
            .apply(RequestOptions.circleCropTransform())
            .into(icon);
    }
}
