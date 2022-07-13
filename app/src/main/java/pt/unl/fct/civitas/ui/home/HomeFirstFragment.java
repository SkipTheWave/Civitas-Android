package pt.unl.fct.civitas.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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
        // token is technically unnecessary here
        if(getActivity() != null) {
            LoggedInUser userInfo = gson.fromJson(TokenStore.getToken(getActivity()), LoggedInUser.class);
            String welcomeString = String.format(getString(R.string.welcome_user), userInfo.getUsername());
            welcomeText.setText(welcomeString);
        }

        binding.buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity home = getActivity();
                if (home instanceof HomeActivity)
                    ((HomeActivity) home).signOut();
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
                    .into((ImageView) view.findViewById(R.id.home_avatar));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}