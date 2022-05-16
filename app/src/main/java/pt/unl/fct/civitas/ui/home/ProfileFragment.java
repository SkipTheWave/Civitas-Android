package pt.unl.fct.civitas.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.databinding.FragmentHomeBinding;
import pt.unl.fct.civitas.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private HomeViewModel viewModel;

    final TextView usernameTextView = binding.profileUsername;
    final EditText nameEditText = binding.profileNameField;
    //final Button loginButton = binding.login;
    //final Button registerButton = binding.createAccount;
    // TODO final ProgressBar loadingProgressBar = binding.loading;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);


        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileFragment.this)
                        .navigate(R.id.action_ProfileFragment_to_FirstFragment);
            }
        });
    }

    public void setUsernameDisplay(String username) {
        usernameTextView.setText(username);
    }

    public void setNameEditText(String name) {
        nameEditText.setText(name);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}