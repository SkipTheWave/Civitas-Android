package pt.unl.fct.civitas.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import pt.unl.fct.civitas.databinding.FragmentProfileBinding;
import pt.unl.fct.civitas.ui.login.LoginActivity;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private HomeViewModel viewModel;
    private Gson gson = new Gson();

    private TextView usernameTextView;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText telephoneEditText;
    private EditText mobilePhoneEditText;
    private EditText nifEditText;
    private Spinner profileOption;
    private Button submitButton;
    // TODO final ProgressBar loadingProgressBar = binding.loading;

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
        profileOption = binding.profileProfileDropdown;
        submitButton = binding.profileSubmitButton;

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        setUsernameDisplay(
                gson.fromJson( TokenStore.getToken(getActivity()), LoggedInUser.class).getUsername());

        binding.profileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.action_ProfileFragment_to_FirstFragment);
            }
        });

        viewModel.getProfileResult().observe(getViewLifecycleOwner(), new Observer<ProfileResult>() {
            @Override
            public void onChanged(@Nullable ProfileResult profileResult) {
                if( profileResult.getError() != null ) {
                    showProfileFailure();
                } else if( profileResult.getSuccess() != null ) {
                    // TODO show profile data
                    setNameEditText(profileResult.getSuccess().name);
                    setEmailEditText(profileResult.getSuccess().email);
                    setTelephoneEditText(profileResult.getSuccess().telephone);
                    setMobilePhoneEditText(profileResult.getSuccess().mobilePhone);
                    setNifEditText(profileResult.getSuccess().nif);
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

//    public void setProfileOption(String option) {
//        ArrayAdapter adapter = profileOption.getAdapter();
//    } TODO fix this

    public void showProfileFailure() {
        Toast.makeText(getActivity(), R.string.error_show_profile, Toast.LENGTH_LONG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}