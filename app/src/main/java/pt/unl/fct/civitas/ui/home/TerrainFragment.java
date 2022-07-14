package pt.unl.fct.civitas.ui.home;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;
import static com.google.maps.android.SphericalUtil.computeArea;
import static pt.unl.fct.civitas.util.FragmentUtils.refreshFragment;
import static pt.unl.fct.civitas.util.GeometryHelper.checkIntersections;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Priority;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import pt.unl.fct.civitas.R;
import pt.unl.fct.civitas.data.model.TerrainData;
import pt.unl.fct.civitas.data.model.VertexData;

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
    public static final String TERRAIN_APPROVED_APPROVAL = "accepted";
    public static final String TERRAIN_WAITING_APPROVAL = "waiting";
    public static final String TERRAIN_REJECTED_APPROVAL = "rejected";

    // terrain colors
    private static final int OWN_SAVED_OUTLINE_COLOR = 0xddcc2299;
    private static final int OWN_SAVED_FILL_COLOR = 0x44cc2299;
    private static final int OWN_APPROVED_OUTLINE_COLOR = 0xdd33dd22;
    private static final int OWN_APPROVED_FILL_COLOR = 0x4433dd22;
    private static final int OWN_WAITING_OUTLINE_COLOR = 0xddff8800;
    private static final int OWN_WAITING_FILL_COLOR = 0x44ff8800;
    private static final int OWN_REJECTED_OUTLINE_COLOR = 0xddee2200;
    private static final int OWN_REJECTED_FILL_COLOR = 0x44ee2200;
    private static final int SHARED_OUTLINE_COLOR = 0xdd2233dd;
    private static final int SHARED_FILL_COLOR = 0x442233dd;
    private static final int ALL_FILL_COLOR = 0x7733aa44;
    private static final int ERROR_FILL_COLOR = 0xeeeeeeee;

    private final LatLng DEFAULT_LOCATION = new LatLng(39.5554, -7.9960);
    private static final int DEFAULT_ZOOM = 14;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private MapView mapView;
    private boolean locationPermissionGranted;
    private boolean requestingLocationUpdates;
    private boolean addingTerrain;
    private Button buttonAddTerrain;
    private Button buttonAddVertexLoc;
    private Button buttonEditTerrain;
    private Button buttonCancel;
    private Button buttonFinish;
    private Spinner spinnerTerrain;
    private ProgressBar loading;
    private HomeViewModel viewModel;
    private Location lastKnownLocation;
    private LatLng lastCoords;
    private List<TerrainData> userTerrains = new ArrayList<>();
    private List<List<LatLng>> shownTerrains = new ArrayList<>();
    private List<Polygon> othersTerrains = new ArrayList<>();

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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(mLocationRequest);
        lastCoords = DEFAULT_LOCATION;

        getLocationPermission();
        getDeviceLocation();
        //createLocationRequest();
        //startLocationUpdates();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));

        //selectTerrainListener = new AdapterView.OnItemSelectedListener()

        viewModel.getShowTerrainResult().observe(getViewLifecycleOwner(), new Observer<ShowTerrainResult>() {
            @Override
            public void onChanged(@Nullable ShowTerrainResult terrainResult) {
                loading.setVisibility(View.GONE);
                List<TerrainData> terrains = showTerrainsAux(terrainResult, false);

                // creating the terrain list
                String[] terrainIds = new String[terrains.size()];
                for (int i = 0; i < terrains.size(); i++) {
                    terrainIds[i] = terrains.get(i).name;
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                        requireContext(), R.layout.spinner_item_dropdown_map, terrainIds);
                spinnerTerrain.setAdapter(spinnerAdapter);

                // makes camera to move selected terrain on the list
                spinnerTerrain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        for (TerrainData terrain : userTerrains) {
                            String terrainName = spinnerTerrain.getSelectedItem().toString();
                            if(terrain.name.equals(terrainName)) {
                                LatLng pos = new LatLng( Double.parseDouble( terrain.vertices.get(0).latitude),
                                        Double.parseDouble( terrain.vertices.get(0).longitude));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                                return;
                            }

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });

                mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                    @Override
                    public void onPolygonClick(@NonNull Polygon polygon) {
                        viewModel.setSelectedTerrain((TerrainData) polygon.getTag());
                        NavHostFragment.findNavController(TerrainFragment.this)
                                .navigate(R.id.action_TerrainFragment_to_selectedTerrainFragment);
                    }
                });
                    // moves camera to last terrain's last vertex (or default location if no terrains are found)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(lastCoords));

//                    if(!addTerrainMode)
//                        Toast.makeText(getActivity(), terrains.size() + " terrains found", Toast.LENGTH_SHORT).show();
//                       if( terrains.isEmpty() )
//                           Toast.makeText(getActivity(), R.string.zero_terrains, Toast.LENGTH_LONG).show();
            }
        });
        viewModel.getShowAllTerrainResult().observe(getViewLifecycleOwner(), new Observer<ShowTerrainResult>() {
            @Override
            public void onChanged(@Nullable ShowTerrainResult terrainResult) {
                if(terrainResult == null || getActivity() == null || !HomeViewModel.addTerrainMode)
                    return;

                loading.setVisibility(View.GONE);

                Toast.makeText(getActivity(), R.string.help_add_terrain, Toast.LENGTH_LONG).show();
                showTerrainsAux(terrainResult, true);
            }
        });
        viewModel.getRegisterTerrainEndResult().observe(getViewLifecycleOwner(), new Observer<RegisterTerrainResult>() {
            @Override
            public void onChanged(@Nullable RegisterTerrainResult result) {
                loading.setVisibility(View.GONE);
                if(result == null || getActivity() == null || !addingTerrain)
                    return;
                addingTerrain = false;
                HomeViewModel.addTerrainMode = false;
                    if (result.getError() != null) {
                        if( viewModel.isTokenExpired( result.getError()) )
                            ((HomeActivity)getActivity()).signOut();
                        else {
                            Toast.makeText(getActivity(), R.string.error_add_terrain, Toast.LENGTH_LONG).show();
                            refreshFragment(TerrainFragment.this);
                        }
                }
                if (result.getSuccess() != null) {
                    Toast.makeText(getActivity(), R.string.success_add_terrain, Toast.LENGTH_SHORT).show();
                }
            }
        });
        updateLocationUI();
        viewModel.showTerrains();
        if(HomeViewModel.addTerrainMode) {
            viewModel.showAllTerrains();
            loading.setVisibility(View.VISIBLE);
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
        buttonAddTerrain = view.findViewById(R.id.button_add_terrain);
        buttonAddVertexLoc = view.findViewById(R.id.button_add_location_vertex);
        buttonEditTerrain = view.findViewById(R.id.button_edit_terrain);
        buttonCancel = view.findViewById(R.id.button_cancel);
        buttonFinish = view.findViewById(R.id.button_finish);
        spinnerTerrain = view.findViewById(R.id.spinner_terrain);
        loading = view.findViewById(R.id.terrain_progress);

        loading.setVisibility(View.VISIBLE);

        buttonAddTerrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void showTerrainFailure(ShowTerrainResult result) {
        if(result.getError() != null)
            if( viewModel.isTokenExpired( result.getError()) )
                ((HomeActivity)getActivity()).signOut();
            else
                Toast.makeText(getActivity(), result.getError(), Toast.LENGTH_LONG).show();
    }

    private void startTerrainOp() {
        buttonEditTerrain.setVisibility(View.GONE);
        buttonAddTerrain.setVisibility(View.GONE);
        buttonAddVertexLoc.setVisibility(View.VISIBLE);
        buttonCancel.setVisibility(View.VISIBLE);
    }

    private void cancelTerrainOp() {
        buttonEditTerrain.setVisibility(View.GONE);
        buttonAddTerrain.setVisibility(View.VISIBLE);
        buttonCancel.setVisibility(View.GONE);
        buttonFinish.setVisibility(View.GONE);
        buttonAddVertexLoc.setVisibility(View.GONE);
        HomeViewModel.addTerrainMode = false;
        for (Polygon terrain : othersTerrains) {
            terrain.remove();
        }
        othersTerrains.clear();
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
                if( checkIntersections(true, points, shownTerrains) ) {
                    points.remove(latLng);
                    Toast.makeText(requireActivity(), R.string.error_terrain_intersection, Toast.LENGTH_LONG).show();
                    return;
                }
                vertices.add( new VertexData("?", String.valueOf(vertices.size()),
                                String.valueOf(latLng.latitude), String.valueOf(latLng.longitude)) );
                markers.add( mMap.addMarker(new MarkerOptions()
                        .position(latLng)) );
                line.setPoints(points);
                if(points.size() == 3)
                    buttonFinish.setVisibility(View.VISIBLE);
            }
        });

        buttonAddVertexLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View view) {
                //getDeviceLocation();
                LatLng currentPos = new LatLng(
                        lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude() );
                points.add(currentPos);
                if( checkIntersections(true, points, shownTerrains) ) {
                    points.remove(currentPos);
                    Toast.makeText(requireActivity(), R.string.error_terrain_intersection, Toast.LENGTH_LONG).show();
                    return;
                }
                vertices.add( new VertexData("?", String.valueOf(vertices.size()),
                        String.valueOf(currentPos.latitude), String.valueOf(currentPos.longitude)) );
                markers.add( mMap.addMarker(new MarkerOptions()
                        .position(currentPos)) );
                line.setPoints(points);
                if(points.size() == 3)
                    buttonFinish.setVisibility(View.VISIBLE);
            }
        });

        buttonFinish.setOnClickListener(viewFinish -> {
            if( checkIntersections(false, points, shownTerrains) ) {
                Toast.makeText(requireActivity(), R.string.error_terrain_intersection, Toast.LENGTH_LONG).show();
                line.remove();
                points.clear();
                for(Marker m : markers)
                    m.remove();
                buttonFinish.setVisibility(View.GONE);
                addTerrain(terrainData);
                return;
            }
            Polygon polygon = mMap.addPolygon(new PolygonOptions()
                    .addAll(points)
                    .strokeColor(OWN_SAVED_OUTLINE_COLOR)
                    .fillColor(OWN_SAVED_FILL_COLOR)
                    .clickable(true));
            for(Marker m : markers)
                m.remove();
            //shownTerrains.add(polygon.getPoints());
            userTerrains.add(terrainData);
            terrainData.area = computeArea(points) / 10000;
            viewModel.registerTerrain(terrainData, vertices);
            addingTerrain = true;
            terrainData.owners = terrainData.owner;
            //terrainData.terrainId = "calculating...";
            terrainData.vertices = vertices;
            polygon.setTag(terrainData);
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
        //mMap.clear();
        String username = viewModel.getUsername();
        if(all) {
            fillColor = ALL_FILL_COLOR;
            strokeColor = ALL_FILL_COLOR;
        }
        if (terrainResult.getError() != null) {
            if( viewModel.isTokenExpired( terrainResult.getError()) )
                ((HomeActivity)getActivity()).signOut();
            else
                showTerrainFailure(terrainResult);
        } else if (terrainResult.getSuccess() != null) {
            List<TerrainData> terrains = terrainResult.getSuccess();
            for (TerrainData terrain : terrains) {
                //determine user's terrain fill color
                if(!all) {
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
                            break;
                        case TERRAIN_SAVED_APPROVAL:
                            fillColor = OWN_SAVED_FILL_COLOR;
                            strokeColor = OWN_SAVED_OUTLINE_COLOR;
                            break;
                        default:
                            fillColor = ERROR_FILL_COLOR;
                            strokeColor = ERROR_FILL_COLOR;
                            break;
                    }
                }
                if(terrain.shared) {
                    fillColor = SHARED_FILL_COLOR;
                    strokeColor = SHARED_OUTLINE_COLOR;
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
                        //shownTerrains.add(polygon.getPoints());
                        if(all) {
                            othersTerrains.add(polygon);
                            shownTerrains.add(polygon.getPoints());
                        }
                        else
                            userTerrains.add(terrain);
                    }
                }
            }
            return terrains;
        }
        return new ArrayList<>();
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(4000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getCurrentLocation(
                        PRIORITY_HIGH_ACCURACY, null);
                mMap.setMyLocationEnabled(true);
                locationResult.addOnCompleteListener(requireActivity(), task -> {
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
        }
    }

    private void startLocationUpdates() {
        if (locationPermissionGranted) {
            try {
                fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                        locationCallback,
                        Looper.getMainLooper());
            } catch (SecurityException e) {
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
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(), "Moving to your location...", Toast.LENGTH_SHORT).show();
        // the return is so we don't consume the event, the default behavior still occurs
        // (in this case, the camera moving towards device location)
        return false;
    }


    @Override
    public void onResume() {
        if(mapView != null)
            mapView.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mapView != null)
            mapView.onStart();
    }

    @Override
    public void onPause() {
        refreshFragment(TerrainFragment.this);
        if(mapView != null)
            mapView.onPause();
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onStop() {
        if(mapView != null)
            mapView.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if(mapView != null)
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