package pt.unl.fct.civitas.ui.home;

import static pt.unl.fct.civitas.ui.register.RegisterViewModel.checkUndefined;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.data.model.ProfileData;
import pt.unl.fct.civitas.data.model.TerrainData;
import pt.unl.fct.civitas.databinding.FragmentProfileBinding;
import pt.unl.fct.civitas.databinding.FragmentTerrainInfoBinding;

public class TerrainInfoFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentTerrainInfoBinding binding;
    private final Gson gson = new Gson();

    private EditText nameEditText;
    private EditText articleEditText;
    private EditText sectionEditText;
    private EditText descriptionEditText;
    private EditText terrainCoverageEditText;
    private EditText currentUsageEditText;
    private EditText previousUsageEditText;
    private EditText ownersEditText;
    private Spinner parishDropdown;
    private Button uploadButton;
    private Button submitButton;
    private TerrainData terrainData;

    public static TerrainInfoFragment newInstance() {
        return new TerrainInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTerrainInfoBinding.inflate(inflater, container, false);
        return inflater.inflate(R.layout.fragment_terrain_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        nameEditText = binding.terrainNameField;
        articleEditText = binding.terrainArticleField;
        sectionEditText = binding.terrainSectionField;
        descriptionEditText = binding.terrainDescriptionField;
        terrainCoverageEditText = binding.terrainTypeField;
        currentUsageEditText = binding.terrainCurrentUsageField;
        previousUsageEditText = binding.terrainPreviousUsageField;
        ownersEditText = binding.terrainOwnersField;
        parishDropdown = binding.parishDropdown;
        submitButton = (Button) view.findViewById(R.id.terrain_submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TerrainData data = new TerrainData(homeViewModel.getUsername(), 0.0,
                        parishDropdown.getSelectedItem().toString(),
                        sectionEditText.getText().toString(),
                        articleEditText.getText().toString(),
                        nameEditText.getText().toString(),
                        descriptionEditText.getText().toString(),
                        terrainCoverageEditText.getText().toString(),
                        currentUsageEditText.getText().toString(),
                        previousUsageEditText.getText().toString(),
                        ownersEditText.getText().toString());
                homeViewModel.setCurrentTerrainData(data);

                NavHostFragment.findNavController(TerrainInfoFragment.this)
                        .navigate(R.id.action_terrainInfoFragment_pop);
            }
        });
    }

}