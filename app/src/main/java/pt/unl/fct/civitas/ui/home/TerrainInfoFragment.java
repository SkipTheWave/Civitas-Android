package pt.unl.fct.civitas.ui.home;

import static pt.unl.fct.civitas.ui.home.TerrainFragment.TERRAIN_SAVED_APPROVAL;
import static pt.unl.fct.civitas.ui.register.RegisterViewModel.checkUndefined;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTerrainInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        nameEditText = view.findViewById(R.id.terrain_name_field);
        articleEditText = binding.terrainArticleField;
        sectionEditText = binding.terrainSectionField;
        descriptionEditText = binding.terrainDescriptionField;
        terrainCoverageEditText = binding.terrainTypeField;
        currentUsageEditText = binding.terrainCurrentUsageField;
        previousUsageEditText = binding.terrainPreviousUsageField;
        ownersEditText = binding.terrainOwnersField;
        parishDropdown = binding.parishDropdown;
        submitButton = (Button) view.findViewById(R.id.terrain_submit_button);

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
                homeViewModel.terrainDataChanged(articleEditText.getText().toString(), sectionEditText.getText().toString());
            }
        };
        articleEditText.addTextChangedListener(afterTextChangedListener);
        sectionEditText.addTextChangedListener(afterTextChangedListener);

        homeViewModel.getTerrainInfoFormState().observe(getActivity(), new Observer<TerrainInfoFormState>() {
            @Override
            public void onChanged(@Nullable TerrainInfoFormState terrainInfoFormState) {
                if (terrainInfoFormState == null) {
                    return;
                }
                submitButton.setEnabled(terrainInfoFormState.isDataValid());
                if (terrainInfoFormState.getArticleError() != null) {
                    articleEditText.setError(getString(terrainInfoFormState.getArticleError()));
                }
                if (terrainInfoFormState.getSectionError() != null) {
                    sectionEditText.setError(getString(terrainInfoFormState.getSectionError()));
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TerrainData data = new TerrainData(homeViewModel.getUsername(), 0.0,
                        parishDropdown.getSelectedItem().toString(),
                        checkUndefined( sectionEditText.getText().toString() ),
                        checkUndefined( articleEditText.getText().toString() ),
                        checkUndefined( nameEditText.getText().toString() ),
                        checkUndefined( descriptionEditText.getText().toString() ),
                        checkUndefined( terrainCoverageEditText.getText().toString() ),
                        checkUndefined( currentUsageEditText.getText().toString() ),
                        checkUndefined( previousUsageEditText.getText().toString() ),
                        checkUndefined( ownersEditText.getText().toString() ),
                        TERRAIN_SAVED_APPROVAL);
                homeViewModel.addTerrainAux(data);

               NavHostFragment.findNavController(TerrainInfoFragment.this)
                       .navigate(R.id.action_terrainInfoFragment_to_TerrainFragment);
            }
        });
    }

}