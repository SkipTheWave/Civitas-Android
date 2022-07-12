package pt.unl.fct.civitas.ui.home;

import static pt.unl.fct.civitas.ui.home.TerrainFragment.TERRAIN_SAVED_APPROVAL;
import static pt.unl.fct.civitas.ui.register.RegisterViewModel.checkUndefined;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.gson.Gson;

import org.locationtech.jts.triangulate.quadedge.Vertex;

import java.text.DecimalFormat;
import java.util.List;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.data.TokenStore;
import pt.unl.fct.civitas.data.model.TerrainData;
import pt.unl.fct.civitas.data.model.VertexData;
import pt.unl.fct.civitas.data.model.shareTerrainInfo;
import pt.unl.fct.civitas.databinding.FragmentTerrainInfoBinding;
import pt.unl.fct.civitas.databinding.FragmentTerrainSelectedBinding;

public class SelectedTerrainFragment extends Fragment {

    private HomeViewModel viewModel;
    private FragmentTerrainSelectedBinding binding;
    private final Gson gson = new Gson();

    private EditText nameEditText;
    private EditText articleEditText;
    private EditText sectionEditText;
    private EditText descriptionEditText;
    private EditText terrainCoverageEditText;
    private EditText currentUsageEditText;
    private EditText previousUsageEditText;
    private TextView ownersText;
    private EditText newOwnersEditText;
    private TextView parishText;
    private TextView areaText;
    private Button uploadButton;
    private Button submitButton;
    private Button shareButton;
    private Button directionsButton;
    private ProgressBar loading;

    private TerrainData terrain;
    private DecimalFormat dformat;
    private boolean waitingForUpdate, waitingForShare;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTerrainSelectedBinding.inflate(inflater, container, false);
        dformat = new DecimalFormat("#.####");
        waitingForShare = false;
        waitingForUpdate = false;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        nameEditText = view.findViewById(R.id.terrain_name_field);
        articleEditText = binding.terrainArticleField;
        sectionEditText = binding.terrainSectionField;
        descriptionEditText = binding.terrainDescriptionField;
        terrainCoverageEditText = binding.terrainTypeField;
        currentUsageEditText = binding.terrainCurrentUsageField;
        previousUsageEditText = binding.terrainPreviousUsageField;
        newOwnersEditText = binding.terrainOwnersField;
        ownersText = binding.terrainOtherOwnersText;
        parishText = binding.editTerrainParish;
        areaText = binding.editTerrainArea;
        submitButton = view.findViewById(R.id.edit_terrain_submit_button);
        shareButton = view.findViewById(R.id.edit_terrain_owners_button);
        directionsButton = view.findViewById(R.id.edit_terrain_directions_button);
        loading = binding.editTerrainLoading;

        terrain = viewModel.getSelectedTerrain();

        nameEditText.setText(terrain.name);
        articleEditText.setText(terrain.article);
        sectionEditText.setText(terrain.section);
        descriptionEditText.setText(terrain.description);
        terrainCoverageEditText.setText(terrain.coverage);
        currentUsageEditText.setText(terrain.current);
        previousUsageEditText.setText(terrain.last);
        ownersText.setText(terrain.owners.replaceAll(",", ", "));
        parishText.setText(terrain.county);
        areaText.setText(dformat.format(terrain.area));

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TerrainData data = new TerrainData(viewModel.getUsername(),
                        terrain.terrainId, terrain.owner,
                        checkUndefined( nameEditText.getText().toString() ),
                        checkUndefined( descriptionEditText.getText().toString() ),
                        checkUndefined( terrainCoverageEditText.getText().toString() ),
                        checkUndefined( currentUsageEditText.getText().toString() ),
                        checkUndefined( previousUsageEditText.getText().toString() ),
                        terrain.owners,
                        terrain.approved);

                viewModel.updateTerrain(data);
                waitingForUpdate = true;
                loading.setVisibility(View.VISIBLE);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newShares = newOwnersEditText.getText().toString();
                if(!newShares.equals("")) {
                    String[] newSharesAux = viewModel.checkShares(newShares, terrain.owner);
                    if(newSharesAux == null)
                        Toast.makeText(requireActivity(), R.string.terrain_share_self, Toast.LENGTH_SHORT).show();

                    else {
                        loading.setVisibility(View.VISIBLE);
                        viewModel.shareTerrain(new shareTerrainInfo(terrain.owner, newSharesAux, terrain.terrainId));
                        waitingForShare = true;
                    }
                }

            }
        });

        viewModel.getUpdateTerrainResult().observe(getViewLifecycleOwner(), new Observer<RegisterTerrainResult>() {
            @Override
            public void onChanged(@Nullable RegisterTerrainResult result) {
                loading.setVisibility(View.GONE);
                if(result == null || getActivity() == null || !waitingForUpdate)
                    return;
                waitingForUpdate = false;
                if (result.getError() != null) {
                    //Toast.makeText(getActivity(), R.string.error_terrain_update, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), result.getError(), Toast.LENGTH_SHORT).show();
                }
                if (result.getSuccess() != null) {
                    Toast.makeText(getActivity(), R.string.changes_success, Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getShareTerrainResult().observe(getViewLifecycleOwner(), new Observer<RegisterTerrainResult>() {
            @Override
            public void onChanged(@Nullable RegisterTerrainResult result) {
                loading.setVisibility(View.GONE);
                if(result == null || getActivity() == null || !waitingForShare)
                    return;
                waitingForShare = false;
                if (result.getError() != null) {
                    Toast.makeText(getActivity(), result.getError(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getActivity(), R.string.error_terrain_share, Toast.LENGTH_LONG).show();
                }
                if (result.getSuccess() != null) {
                    Toast.makeText(getActivity(), R.string.success_terrain_share, Toast.LENGTH_SHORT).show();
                }
            }
        });

        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<VertexData> vertices = terrain.vertices;
                String uri = "https://www.google.com/maps/dir/?api=1&destination=" +
                        vertices.get(0).latitude + "%2C" + vertices.get(0).longitude;

                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse(uri);

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
            }
        });
    }

}