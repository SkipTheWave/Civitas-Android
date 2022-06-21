package pt.unl.fct.civitas.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.LinkedList;
import java.util.List;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.data.model.TerrainData;
import pt.unl.fct.civitas.data.model.VertexData;
import pt.unl.fct.civitas.databinding.FragmentTerrainBinding;

public class TerrainFragment extends Fragment {

    private GoogleMap mMap;
    //private ActivityMapsBinding binding;
    private CameraPosition cameraPosition;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final int OUTLINE_COLOR = 0xffff7700;
    private static final int FILL_COLOR = 0x44ff7700;

    private final LatLng DEFAULT_LOCATION = new LatLng(39.5554, -7.9960);
    private static final int DEFAULT_ZOOM = 12;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private boolean locationPermissionGranted;
    private FragmentTerrainBinding binding;
    private Button buttonAddTerrain;
    private HomeViewModel viewModel;
    private Location lastKnownLocation;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
//            LatLng sydney = new LatLng(-34, 151);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap = googleMap;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));

            buttonAddTerrain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startTerrainOp();
                    List<LatLng> points = new LinkedList<>();

                    binding.buttonFinish.setOnClickListener(viewFinish -> {
                        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                                .addAll(points)
                                .strokeColor(OUTLINE_COLOR)
                                .fillColor(FILL_COLOR)
                                .clickable(true));
                        // TODO make addTerrain rest call, then add listener to polygon like the others
                        cancelTerrainOp();
                    });

                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng latLng) {
                            points.add(latLng);
                            if(points.size() == 3)
                                binding.buttonFinish.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });

            viewModel.getShowTerrainResult().observe(getViewLifecycleOwner(), new Observer<ShowTerrainResult>() {
                @Override
                public void onChanged(@Nullable ShowTerrainResult terrainResult) {
                    if( terrainResult.getError() != null ) {
                        showTerrainFailure(terrainResult);
                    } else if( terrainResult.getSuccess() != null ) {
                        LatLng coords = DEFAULT_LOCATION;
                        List<TerrainData> terrains = terrainResult.getSuccess();

                        for(TerrainData terrain : terrains) {
                            List<LatLng> points = new LinkedList<>();
                            for(VertexData vertex : terrain.vertexList) {
                                coords = new LatLng(Double.parseDouble(vertex.latitude), Double.parseDouble(vertex.longitude));
                                points.add(coords);
                            }
                            Polygon polygon = mMap.addPolygon(new PolygonOptions()
                                    .addAll(points)
                                    .strokeColor(OUTLINE_COLOR)
                                    .fillColor(FILL_COLOR)
                                    .clickable(true));
                            polygon.setTag(terrain);
                            mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                                @Override
                                public void onPolygonClick(@NonNull Polygon polygon) {
                                    // TODO redirect to terrain info page, or something
                                    if(polygon.getTag() != null)
                                        Toast.makeText(getActivity(), "Voila! " +
                                                ((TerrainData) polygon.getTag()).terrainId, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        // moves camera to last terrain's last vertex (or default location if no terrains are found)
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(coords));

                        // if the search succeeds but returns no terrains
                        Toast.makeText(getActivity(), terrains.size() + " terrains found", Toast.LENGTH_LONG).show();
                        if( terrains.isEmpty() )
                            Toast.makeText(getActivity(), R.string.zero_terrains, Toast.LENGTH_LONG).show();
                    }
                }
            });

            viewModel.showTerrains();

            binding.buttonCancel.setOnClickListener(view -> cancelTerrainOp());
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTerrainBinding.inflate(inflater, container, false);
        View v = inflater.inflate(R.layout.fragment_terrain, container, false);
        buttonAddTerrain = binding.buttonAddTerrain;

        MapView mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        if (mapView != null) {
            mapView.getMapAsync(callback);
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

    }

    /**
     * Request location permission. The result of the permission request is handled
     * by a callback, onRequestPermissionsResult
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void showTerrainFailure(ShowTerrainResult result) {
        if(result.getError() != null)
            Toast.makeText(getActivity(), result.getError(), Toast.LENGTH_LONG).show();
    }

    private void startTerrainOp() {
        binding.buttonEditTerrain.setVisibility(View.GONE);
        buttonAddTerrain.setVisibility(View.GONE);
        binding.buttonCancel.setVisibility(View.VISIBLE);
    }

    private void cancelTerrainOp() {
        binding.buttonEditTerrain.setVisibility(View.VISIBLE);
        buttonAddTerrain.setVisibility(View.VISIBLE);
        binding.buttonCancel.setVisibility(View.GONE);
        binding.buttonFinish.setVisibility(View.GONE);

        mMap.setOnMapClickListener(null);
    }
}