package pt.unl.fct.civitas.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.gson.Gson;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.data.TokenStore;
import pt.unl.fct.civitas.data.model.LoggedInUser;
import pt.unl.fct.civitas.databinding.FragmentHomeBinding;

public class HomeFirstFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Gson gson = new Gson();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView welcomeText = view.findViewById(R.id.textview_welcome);
        // TODO token is technically unnecessary here
        LoggedInUser userInfo = gson.fromJson( TokenStore.getToken(getActivity()), LoggedInUser.class);
        String welcomeString = String.format(getString(R.string.welcome_user), userInfo.getUsername());
        welcomeText.setText(welcomeString);

        binding.buttonSessionInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_ProfileFragment);
            }
        });

        binding.buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity home = getActivity();
                if (home instanceof HomeActivity)
                    ((HomeActivity) home).signOut();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}