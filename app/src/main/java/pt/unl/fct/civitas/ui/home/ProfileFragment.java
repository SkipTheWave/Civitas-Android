package pt.unl.fct.civitas.ui.home;

import static pt.unl.fct.civitas.ui.register.RegisterViewModel.checkUndefined;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.gson.Gson;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.data.TokenStore;
import pt.unl.fct.civitas.data.model.LoggedInUser;
import pt.unl.fct.civitas.data.model.ProfileData;
import pt.unl.fct.civitas.databinding.FragmentProfileBinding;
import pt.unl.fct.civitas.ui.register.RegisterViewModel;

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

        binding.profileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.action_ProfileFragment_to_FirstFragment);
            }
        });

        binding.profileSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO fix the Result Observer bug instead of using this workaround
                Toast.makeText(getActivity(), "Changes applied successfully(?)", Toast.LENGTH_LONG);
                //loadingProgressBar.setVisibility(View.VISIBLE);
                viewModel.editProfile(new ProfileData(profileData.username,
                        profileData.username, emailEditText.getText().toString(),
                        nameEditText.getText().toString(),
                        checkUndefined( telephoneEditText.getText().toString() ),
                        checkUndefined( mobilePhoneEditText.getText().toString() ),
                        profileData.nif, profileData.role, profileData.state, profileData.profilePic));
            }
        });
        loadingProgressBar.setVisibility(View.VISIBLE);

        viewModel.getProfileResult().observe(getViewLifecycleOwner(), new Observer<ProfileResult>() {
            @Override
            public void onChanged(@Nullable ProfileResult profileResult) {
                if( profileResult.getError() != null ) {
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

}