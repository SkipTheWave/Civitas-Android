package pt.unl.fct.civitas.ui.home;

import static pt.unl.fct.civitas.ui.register.RegisterViewModel.checkUndefined;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.Objects;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.data.TokenStore;
import pt.unl.fct.civitas.data.model.LoggedInUser;
import pt.unl.fct.civitas.data.model.ProfileData;
import pt.unl.fct.civitas.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private HomeViewModel viewModel;
    private final Gson gson = new Gson();

    private TextView usernameTextView;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText telephoneEditText;
    private EditText mobilePhoneEditText;
    private EditText nifEditText;
    private Button submitButton;
    private ProgressBar loadingProgressBar;
    private ProfileData profileData;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

            usernameTextView = binding.profileUsername;
            nameEditText = binding.profileNameField;
            emailEditText = binding.profileEmailField;
            telephoneEditText = binding.profileTelephoneField;
            mobilePhoneEditText = binding.profileMobilePhoneField;
            nifEditText = binding.profileNifField;
            submitButton = binding.profileSubmitButton;
            loadingProgressBar = binding.profileLoading;

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        setUsernameDisplay(
                gson.fromJson( TokenStore.getToken(getActivity()), LoggedInUser.class).getUsername());

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.profileDataChanged(emailEditText.getText().toString(), nameEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        nameEditText.addTextChangedListener(afterTextChangedListener);

        binding.profileSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // might be important to make the observer work
                Toast.makeText(getActivity(), R.string.changes_success, Toast.LENGTH_LONG);
                //loadingProgressBar.setVisibility(View.VISIBLE);
                viewModel.editProfile(new ProfileData(profileData.username,
                        profileData.username, emailEditText.getText().toString(),
                        nameEditText.getText().toString(),
                        checkUndefined( telephoneEditText.getText().toString() ),
                        checkUndefined( mobilePhoneEditText.getText().toString() ),
                        profileData.nif, profileData.role, profileData.state, profileData.profilePic));
                refreshFragment(ProfileFragment.this);
            }
        });
        loadingProgressBar.setVisibility(View.VISIBLE);

        viewModel.getProfileResult().observe(getViewLifecycleOwner(), new Observer<ProfileResult>() {
            @Override
            public void onChanged(@Nullable ProfileResult profileResult) {
                if( profileResult.getError() != null && getActivity() != null ) {
                    if( viewModel.isTokenExpired( profileResult.getError().toString()) )
                        ((HomeActivity)getActivity()).signOut();
                    else
                        showProfileFailure();
                } else if( profileResult.getSuccess() != null ) {
                    loadingProgressBar.setVisibility(View.GONE);
                    profileData = profileResult.getSuccess();
                    setNameEditText(profileData.name);
                    setEmailEditText(profileData.email);
                    setTelephoneEditText(profileData.telephone);
                    setMobilePhoneEditText(profileData.mobilePhone);
                    setNifEditText(profileData.nif);
                }
            }
        });

        Context c = getActivity().getApplicationContext();
        SharedPreferences prefs = c.getSharedPreferences(c.getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
        String pfpUrl = prefs.getString(c.getString(R.string.shared_preferences_pfp), "");

        //if(!pfpUrl.isEmpty())
            Glide.with(getActivity())
                    .load(pfpUrl)
                    .placeholder(R.drawable.ic_person)
                    .circleCrop()
                    .into((ImageView) view.findViewById(R.id.profile_avatar));

        viewModel.getProfile();
    }

    public void setUsernameDisplay(String username) {
        usernameTextView.setText(username);
    }

    public void setNameEditText(String text) {
        nameEditText.setText(text);
    }

    public void setEmailEditText(String text) { emailEditText.setText(text); }

    public void setTelephoneEditText(String text) { telephoneEditText.setText(text); }

    public void setMobilePhoneEditText(String text) { mobilePhoneEditText.setText(text); }

    public void setNifEditText(String text) { nifEditText.setText(text); }

    public void showProfileFailure() {
        Toast.makeText(getActivity(), R.string.error_show_profile, Toast.LENGTH_LONG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static void refreshFragment(Fragment frag) {
        NavController navController = NavHostFragment.findNavController(frag);
        int id = Objects.requireNonNull(navController.getCurrentDestination()).getId();
        navController.popBackStack(id, true);
        navController.navigate(id);
    }

}