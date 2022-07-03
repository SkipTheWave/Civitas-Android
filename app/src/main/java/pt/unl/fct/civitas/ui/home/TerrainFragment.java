package pt.unl.fct.civitas.ui.home;

import static com.google.maps.android.SphericalUtil.computeArea;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.data.model.TerrainData;
import pt.unl.fct.civitas.data.model.TerrainInfo;
import pt.unl.fct.civitas.data.model.VertexData;
import pt.unl.fct.civitas.databinding.FragmentTerrainBinding;

public class TerrainFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    private CameraPosition cameraPosition;
    private FusedLocationProviderClient fusedLocationProviderClient;

    // for storing activity state
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    public static final String TERRAIN_SAVED_APPROVAL = "saved";
    public static final String TERRAIN_APPROVED_APPROVAL = "approved";
    public static final String TERRAIN_WAITING_APPROVAL = "waiting";
    public static final String TERRAIN_REJECTED_APPROVAL = "rejected";

    // terrain colors
    private static final int OWN_SAVED_OUTLINE_COLOR = 0xffee1199;
    private static final int OWN_SAVED_FILL_COLOR = 0x44ee1199;
    private static final int OWN_APPROVED_OUTLINE_COLOR = 0xff33dd22;
    private static final int OWN_APPROVED_FILL_COLOR = 0x4433dd22;
    private static final int OWN_WAITING_OUTLINE_COLOR = 0xffff8800;
    private static final int OWN_WAITING_FILL_COLOR = 0x44ff8800;
    private static final int OWN_REJECTED_OUTLINE_COLOR = 0xffee2200;
    private static final int OWN_REJECTED_FILL_COLOR = 0x44ee2200;
    private static final int ALL_FILL_COLOR = 0x88444444;

    private final LatLng DEFAULT_LOCATION = new LatLng(39.5554, -7.9960);
    private static final int DEFAULT_ZOOM = 14;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private MapView mapView;
    private boolean locationPermissionGranted;
    private boolean requestingLocationUpdates;
    private Button buttonAddTerrain;
    private Button buttonEditTerrain;
    private Button buttonCancel;
    private Button buttonFinish;
    private ProgressBar loading;
    private HomeViewModel viewModel;
    private Location lastKnownLocation;
    private LatLng lastCoords;
    static boolean addTerrainMode;

    // TODO REMOVE
    private TerrainData debugTerrainData = new TerrainData("owner",
            0.0,
            "parish",
            "section",
            "article",
            "name",
            "description",
            "coverage",
            "current",
            "last",
            "owners",
            "saved");

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
        mMap = googleMap;
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(mLocationRequest);
        lastCoords = DEFAULT_LOCATION;

        getLocationPermission();
        getDeviceLocation();
        createLocationRequest();
        startLocationUpdates();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));

        viewModel.getShowTerrainResult().observe(getViewLifecycleOwner(), new Observer<ShowTerrainResult>() {
            @Override
            public void onChanged(@Nullable ShowTerrainResult terrainResult) {
                loading.setVisibility(View.GONE);
                List<TerrainData> terrains = showTerrainsAux(terrainResult, false);
                mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                    @Override
                    public void onPolygonClick(@NonNull Polygon polygon) {
                        // TODO redirect to terrain info page, or something
                        if(polygon.getTag() != null)
                            Toast.makeText(getActivity(), "Voila! This is " +
                                    ((TerrainData) polygon.getTag()).terrainId, Toast.LENGTH_SHORT).show();
                    }
                });
                    // moves camera to last terrain's last vertex (or default location if no terrains are found)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(lastCoords));

                    if(!addTerrainMode)
                        Toast.makeText(getActivity(), terrains.size() + " terrains found", Toast.LENGTH_SHORT).show();
//                       if( terrains.isEmpty() )
//                           Toast.makeText(getActivity(), R.string.zero_terrains, Toast.LENGTH_LONG).show();
            }
        });
        viewModel.getShowAllTerrainResult().observe(getViewLifecycleOwner(), new Observer<ShowTerrainResult>() {
            @Override
            public void onChanged(@Nullable ShowTerrainResult terrainResult) {
                loading.setVisibility(View.GONE);

                // if the search succeeds but returns no terrains
                Toast.makeText(getActivity(), R.string.help_add_terrain, Toast.LENGTH_LONG).show();
//                        if( terrains.isEmpty() )
//                            Toast.makeText(getActivity(), R.string.zero_terrains, Toast.LENGTH_LONG).show();
            }
        });
        viewModel.showTerrains();
        updateLocationUI();
        if(addTerrainMode) {
            addTerrain(viewModel.getCurrentTerrainData().getValue());
        }

//            viewModel.getCurrentTerrainData().observe(getActivity(), new Observer<TerrainData>() {
//                @Override
//                public void onChanged(TerrainData terrainData) {
//                    debugTerrainData = viewModel.getCurrentTerrainData().getValue();
//                }
//            });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_terrain, container, false);

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                }
            }
        };

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        buttonAddTerrain = (Button) view.findViewById(R.id.button_add_terrain);
        buttonEditTerrain = (Button) view.findViewById(R.id.button_edit_terrain);
        buttonCancel = (Button) view.findViewById(R.id.button_cancel);
        buttonFinish = (Button) view.findViewById(R.id.button_finish);
        loading = (ProgressBar) view.findViewById(R.id.terrain_progress);

        loading.setVisibility(View.VISIBLE);

       buttonAddTerrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTerrainMode = true;
                NavHostFragment.findNavController(TerrainFragment.this)
                        .navigate(R.id.action_TerrainFragment_to_terrainInfoFragment);
            }
        });
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
        buttonEditTerrain.setVisibility(View.GONE);
        buttonAddTerrain.setVisibility(View.GONE);
        buttonCancel.setVisibility(View.VISIBLE);
    }

    private void cancelTerrainOp() {
        buttonEditTerrain.setVisibility(View.GONE);
        buttonAddTerrain.setVisibility(View.VISIBLE);
        buttonCancel.setVisibility(View.GONE);
        buttonFinish.setVisibility(View.GONE);
        addTerrainMode = false;

        mMap.setOnMapClickListener(null);
    }

    private void addTerrain(TerrainData terrainData) {
        startTerrainOp();
        List<LatLng> points = new LinkedList<>();
        List<Marker> markers = new LinkedList<>();
        List<VertexData> vertices = new LinkedList<>();

        Polyline line = mMap.addPolyline(new PolylineOptions()
                .color(OWN_SAVED_OUTLINE_COLOR));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                points.add(latLng);
                vertices.add( new VertexData("?", String.valueOf(vertices.size()),
                                String.valueOf(latLng.latitude), String.valueOf(latLng.longitude)) );
                markers.add( mMap.addMarker(new MarkerOptions()
                        .position(latLng)) );
                line.setPoints(points);
                if(points.size() == 3)
                    buttonFinish.setVisibility(View.VISIBLE);
            }
        });

        buttonFinish.setOnClickListener(viewFinish -> {
            Polygon polygon = mMap.addPolygon(new PolygonOptions()
                    .addAll(points)
                    .strokeColor(OWN_SAVED_OUTLINE_COLOR)
                    .fillColor(OWN_SAVED_FILL_COLOR)
                    .clickable(true));
            for(Marker m : markers)
                m.remove();
            terrainData.area = computeArea(points) / 10000;
            viewModel.registerTerrain(terrainData, vertices);
            cancelTerrainOp();
        });

        buttonCancel.setOnClickListener(view -> {
                cancelTerrainOp();
            for(Marker m : markers)
                m.remove();
            line.remove();
        });
    }

    private List<TerrainData> showTerrainsAux(ShowTerrainResult terrainResult, boolean all) {
        int fillColor = OWN_SAVED_FILL_COLOR;
        int strokeColor = OWN_SAVED_OUTLINE_COLOR;
        String username = viewModel.getUsername();
        if(all) {
            fillColor = ALL_FILL_COLOR;
            strokeColor = ALL_FILL_COLOR;
        }
        if (terrainResult.getError() != null) {
            showTerrainFailure(terrainResult);
        } else if (terrainResult.getSuccess() != null) {
            List<TerrainData> terrains = terrainResult.getSuccess();
            for (TerrainData terrain : terrains) {
                //determine user's terrain fill color
                switch (terrain.approved) {
                    case TERRAIN_APPROVED_APPROVAL:
                        fillColor = OWN_APPROVED_FILL_COLOR;
                        strokeColor = OWN_APPROVED_OUTLINE_COLOR;
                        break;
                    case TERRAIN_WAITING_APPROVAL:
                        fillColor = OWN_WAITING_FILL_COLOR;
                        strokeColor = OWN_WAITING_OUTLINE_COLOR;
                        break;
                    case TERRAIN_REJECTED_APPROVAL:
                        fillColor = OWN_REJECTED_FILL_COLOR;
                        strokeColor = OWN_REJECTED_OUTLINE_COLOR;
                }

                List<LatLng> points = new LinkedList<>();
                Collections.sort(terrain.vertices);
                for (VertexData vertex : terrain.vertices) {
                    lastCoords = new LatLng(Double.parseDouble(vertex.latitude), Double.parseDouble(vertex.longitude));
                    points.add(lastCoords);
                }
                if (!points.isEmpty()) {
                    if (!all || !terrain.owner.equals(username)) {
                        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                                .addAll(points)
                                .strokeColor(strokeColor)
                                .fillColor(fillColor)
                                .clickable(!all));
                        polygon.setTag(terrain);
                    }
                }
            }
            return terrains;
        }
        return new ArrayList<>();
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                mMap.setMyLocationEnabled(true);
                locationResult.addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // set map's camera to the current location of the device
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            LatLng knownPos = new LatLng(lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude());
                        }
                    } else {
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void startLocationUpdates() {
        if (locationPermissionGranted) {
            try {
                fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                        locationCallback,
                        Looper.getMainLooper());
            } catch (SecurityException e) {
                Log.e("Exception: %s", e.getMessage());
            }
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            //if request is cancelled, the result arrays are empty
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);

            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(), "Moving to your location...", Toast.LENGTH_SHORT);
        // the return is so we don't consume the event, the default behavior still occurs
        // (in this case, the camera moving towards device location)
        return false;
    }



    @Override
    public void onResume() {
        mapView.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("requestingLocationUpdates",
                requestingLocationUpdates);
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }
}