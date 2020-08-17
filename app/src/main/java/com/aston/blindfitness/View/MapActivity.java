package com.aston.blindfitness.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aston.blindfitness.Model.IndividualLocation;
import com.aston.blindfitness.R;
import com.aston.blindfitness.SharedPrefs;
import com.aston.blindfitness.Utils.LinearLayoutManagerWithSmoothScroller;
import com.google.gson.JsonObject;
import com.mapbox.android.core.location.LocationEngine;


import com.aston.blindfitness.Adapter.LocationRecyclerViewAdapter;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfConversion;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

/**
 * MapActivity will display map to the user with the help of MapBox
 * Will show users options of nearby support groups / route navigation options / ability to search a place
 */
public class MapActivity extends AppCompatActivity implements LocationRecyclerViewAdapter.ClickListener, OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {

    SharedPrefs userPreferences;
    private MapView mapView;
    private MapboxMap map;
    private Button startButton;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private Point originPosition;
    private Point destinationPosition;
    private Marker destinationMarker;
    private NavigationMapRoute navigationMapRoute;
    private static final String TAG = "MapActivity";
    private DirectionsRoute currentRoute;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private CarmenFeature home;
    private CarmenFeature work;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    private LocationComponent locationComponent;

    private RecyclerView locationsRecyclerView;
    private LocationRecyclerViewAdapter styleRvAdapter;
    private ArrayList<IndividualLocation> listOfIndividualLocations;
    private int chosenTheme;
    private FeatureCollection featureCollection;
    private static final String PROPERTY_SELECTED = "selected";
    private static final LatLng MOCK_DEVICE_LOCATION_LAT_LNG = new LatLng(40.713469, -74.006735);
    private static final int CAMERA_MOVEMENT_SPEED_IN_MILSECS = 1200;
    private CustomThemeManager customThemeManager;
    private static final float NAVIGATION_LINE_WIDTH = 9;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        userPreferences = new SharedPrefs(MapActivity.this);
        if (userPreferences.loadDarkTheme()) {
            userPreferences.setLightTheme(false);
            userPreferences.setYellowTheme(false);
            setTheme(R.style.DarkTheme);
        } else if (userPreferences.loadYellowTheme()) {
            userPreferences.setLightTheme(false);
            userPreferences.setDarkTheme(false);
            setTheme(R.style.YellowTheme);
        } else if (userPreferences.loadLightTheme()) {
            userPreferences.setDarkTheme(false);
            userPreferences.setYellowTheme(false);
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_map);

        listOfIndividualLocations = new ArrayList<>();


        findViewById(R.id.fab22).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationComponent.setLocationComponentEnabled(true);
                // Set the component's camera mode
                locationComponent.setCameraMode(CameraMode.TRACKING);
            }
        });

        // Create a GeoJSON feature collection from the GeoJSON file in the assets folder.
        try {
            getFeatureCollectionFromJson();
        } catch (Exception exception) {
            Log.e("MapActivity", "onCreate: " + exception);
            Toast.makeText(this, R.string.failure_to_load_file, Toast.LENGTH_LONG).show();
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        if (userPreferences.loadDarkTheme()) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'> Map</font>"));
        } else {
            getSupportActionBar().setTitle(Html.fromHtml("Map"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setDisplayUseLogoEnabled(true);

    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        chosenTheme = R.style.AppTheme;
        map = mapboxMap;

        // Initialize the custom class that handles marker icon creation and map styling based on the selected theme
        customThemeManager = new CustomThemeManager(chosenTheme, MapActivity.this);

        map.setStyle(new Style.Builder().fromUrl(customThemeManager.getMapStyle()), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                addDestinationIconSymbolLayer(style);
                initSearchFab();
                addUserLocations();

                // Add the symbol layer icon to map for future use
                style.addImage(symbolIconId, BitmapFactory.decodeResource(MapActivity.this.getResources(), R.drawable.custom_marker));

                // Create an empty GeoJSON source using the empty feature collection
                setUpSource(style);

                // Set up a new symbol layer for displaying the searched location's feature coordinates
                setupLayer(style);

                // Set up the SymbolLayer which will show the icons for each store location
                initStoreLocationIconSymbolLayer();

                // Set up the SymbolLayer which will show the selected store icon
                initSelectedStoreSymbolLayer();

                // Set up the LineLayer which will show the navigation route line to a particular store location
                initNavigationPolylineLineLayer();

                // Add the fake device location marker to the map. In a real use case scenario,
                // the Maps SDK's LocationComponent can be used to easily display and customize
                // the device location's puck
                addMockDeviceLocationMarkerToMap();

                // Create a list of features from the feature collection
                List<Feature> featureList = featureCollection.features();

                // Retrieve and update the source designated for showing the store location icons
                GeoJsonSource source = mapboxMap.getStyle().getSourceAs("store-location-source-id");
                if (source != null) {
                    source.setGeoJson(FeatureCollection.fromFeatures(featureList));
                }

                for (int x = 0; x < featureList.size(); x++) {

                    Feature singleLocation = featureList.get(x);

                    // Get the single location's String properties to place in its map marker
                    String singleLocationName = singleLocation.getStringProperty("name");
                    String singleLocationHours = singleLocation.getStringProperty("hours");
                    String singleLocationDescription = singleLocation.getStringProperty("description");
                    String singleLocationPhoneNum = singleLocation.getStringProperty("phone");

                    // Add a boolean property to use for adjusting the icon of the selected store location
                    singleLocation.addBooleanProperty(PROPERTY_SELECTED, false);

                    // Get the single location's LatLng coordinates
                    Point singleLocationPosition = (Point) singleLocation.geometry();

                    // Create a new LatLng object with the Position object created above
                    LatLng singleLocationLatLng = new LatLng(singleLocationPosition.latitude(),
                            singleLocationPosition.longitude());

                    // Add the location to the Arraylist of locations for later use in the recyclerview
                    listOfIndividualLocations.add(new IndividualLocation(
                            singleLocationName,
                            singleLocationDescription,
                            singleLocationHours,
                            singleLocationPhoneNum,
                            singleLocationLatLng
                    ));

                    // Call getInformationFromDirectionsApi() to eventually display the location's
                    // distance from mocked device location
                    getInformationFromDirectionsApi(singleLocationPosition, false, x);
                }

                setUpRecyclerViewOfLocationCards(chosenTheme);

                map.addOnMapClickListener(MapActivity.this);
                startButton = findViewById(R.id.startButton);

                startButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Launch navigation UI
                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(currentRoute)
                                .shouldSimulateRoute(true)
                                .build();

                        NavigationLauncher.startNavigation(MapActivity.this, options);
                    }
                });
            }
        });


    }

    /**
     * Adds a SymbolLayer which will show all of the location's icons
     */
    private void initStoreLocationIconSymbolLayer() {
        Style style = map.getStyle();
        if (style != null) {
            // Add the icon image to the map
            style.addImage("store-location-icon-id", customThemeManager.getUnselectedMarkerIcon());

            // Create and add the GeoJsonSource to the map
            GeoJsonSource storeLocationGeoJsonSource = new GeoJsonSource("store-location-source-id");
            style.addSource(storeLocationGeoJsonSource);

            // Create and add the store location icon SymbolLayer to the map
            SymbolLayer storeLocationSymbolLayer = new SymbolLayer("store-location-layer-id",
                    "store-location-source-id");
            storeLocationSymbolLayer.withProperties(
                    iconImage("store-location-icon-id"),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true)
            );
            style.addLayer(storeLocationSymbolLayer);

        } else {
            Log.d("StoreFinderActivity", "initStoreLocationIconSymbolLayer: Style isn't ready yet.");

            throw new IllegalStateException("Style isn't ready yet.");
        }
    }

    /**
     * Adds a SymbolLayer which will show the selected location's icon
     */
    private void initSelectedStoreSymbolLayer() {
        Style style = map.getStyle();
        if (style != null) {

            // Add the icon image to the map
            style.addImage("selected-store-location-icon-id", customThemeManager.getSelectedMarkerIcon());

            // Create and add the store location icon SymbolLayer to the map
            SymbolLayer selectedStoreLocationSymbolLayer = new SymbolLayer("selected-store-location-layer-id",
                    "store-location-source-id");
            selectedStoreLocationSymbolLayer.withProperties(
                    iconImage("selected-store-location-icon-id"),
                    iconAllowOverlap(true)
            );
            selectedStoreLocationSymbolLayer.withFilter(eq((get(PROPERTY_SELECTED)), literal(true)));
            style.addLayer(selectedStoreLocationSymbolLayer);
        } else {
            Log.d("StoreFinderActivity", "initSelectedStoreSymbolLayer: Style isn't ready yet.");
            throw new IllegalStateException("Style isn't ready yet.");
        }
    }

    private void initNavigationPolylineLineLayer() {
        // Create and add the GeoJsonSource to the map
        GeoJsonSource navigationLineLayerGeoJsonSource = new GeoJsonSource("navigation-route-source-id");
        map.getStyle().addSource(navigationLineLayerGeoJsonSource);

        // Create and add the LineLayer to the map to show the navigation route line
        LineLayer navigationRouteLineLayer = new LineLayer("navigation-route-layer-id",
                navigationLineLayerGeoJsonSource.getId());
        navigationRouteLineLayer.withProperties(
                lineColor(customThemeManager.getNavigationLineColor()),
                lineWidth(NAVIGATION_LINE_WIDTH)
        );
        map.getStyle().addLayerBelow(navigationRouteLineLayer, "store-location-layer-id");
    }

    private void addMockDeviceLocationMarkerToMap() {
        // Add the fake user location marker to the map
        Style style = map.getStyle();
        if (style != null) {
            // Add the icon image to the map
            style.addImage("mock-device-location-icon-id", customThemeManager.getMockLocationIcon());

            style.addSource(new GeoJsonSource("mock-device-location-source-id", Feature.fromGeometry(
                    Point.fromLngLat(MOCK_DEVICE_LOCATION_LAT_LNG.getLongitude(), MOCK_DEVICE_LOCATION_LAT_LNG.getLatitude()))));

            style.addLayer(new SymbolLayer("mock-device-location-layer-id",
                    "mock-device-location-source-id").withProperties(
                    iconImage("mock-device-location-icon-id"),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true)
            ));
        } else {
            throw new IllegalStateException("Style isn't ready yet.");
        }
    }

    private void setUpRecyclerViewOfLocationCards(int chosenTheme) {
        // Initialize the recyclerview of location cards and a custom class for automatic card scrolling
        locationsRecyclerView = findViewById(R.id.map_layout_rv);
        locationsRecyclerView.setHasFixedSize(true);
        locationsRecyclerView.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(this));
        styleRvAdapter = new LocationRecyclerViewAdapter(listOfIndividualLocations, getApplicationContext(), this, chosenTheme);
        locationsRecyclerView.setAdapter(styleRvAdapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(locationsRecyclerView);
    }

    /**
     * Initalise search FAB
     */
    private void initSearchFab() {
        findViewById(R.id.fab_location_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10)
                                .addInjectedFeature(home)
                                .addInjectedFeature(work)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(MapActivity.this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });
    }

    /**
     * Add default locations for user to search for
     */
    private void addUserLocations() {
        home = CarmenFeature.builder().text("Mapbox SF Office")
                .geometry(Point.fromLngLat(-122.3964485, 37.7912561))
                .placeName("50 Beale St, San Francisco, CA")
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();

        work = CarmenFeature.builder().text("Mapbox DC Office")
                .placeName("740 15th Street NW, Washington DC")
                .geometry(Point.fromLngLat(-77.0338348, 38.899750))
                .id("mapbox-dc")
                .properties(new JsonObject())
                .build();
    }

    /**
     * Get information for user's nav route
     * @param destinationPoint
     * @param fromMarkerClick
     * @param listIndex
     */
    private void getInformationFromDirectionsApi(Point destinationPoint,
                                                 final boolean fromMarkerClick, @Nullable final Integer listIndex) {
        // Set up origin and destination coordinates for the call to the Mapbox Directions API
        Point mockCurrentLocation = Point.fromLngLat(MOCK_DEVICE_LOCATION_LAT_LNG.getLongitude(),
                MOCK_DEVICE_LOCATION_LAT_LNG.getLatitude());

        Point destinationMarker = Point.fromLngLat(destinationPoint.longitude(), destinationPoint.latitude());

        // Initialize the directionsApiClient object for eventually drawing a navigation route on the map
        MapboxDirections directionsApiClient = MapboxDirections.builder()
                .origin(mockCurrentLocation)
                .destination(destinationMarker)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();

        directionsApiClient.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // Check that the response isn't null and that the response has a route
                if (response.body() == null) {
                    Log.e("MapActivity", "No routes found, make sure you set the right user and access token.");
                } else if (response.body().routes().size() < 1) {
                    Log.e("MapActivity", "No routes found");
                } else {
                    if (fromMarkerClick) {
                        // Retrieve and draw the navigation route on the map
                        currentRoute = response.body().routes().get(0);
                        drawNavigationPolylineRoute(currentRoute);
                    } else {
                        // Use Mapbox Turf helper method to convert meters to miles and then format the mileage number
                        DecimalFormat df = new DecimalFormat("#.#");
                        String finalConvertedFormattedDistance = String.valueOf(df.format(TurfConversion.convertLength(
                                response.body().routes().get(0).distance(), TurfConstants.UNIT_METERS,
                                TurfConstants.UNIT_MILES)));

                        // Set the distance for each location object in the list of locations
                        if (listIndex != null) {
                            listOfIndividualLocations.get(listIndex).setDistance(finalConvertedFormattedDistance);
                            // Refresh the displayed recyclerview when the location's distance is set
                            styleRvAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Toast.makeText(MapActivity.this, R.string.failure_to_retrieve, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Get our feature collections from our json list of locations
     * @throws IOException
     */
    private void getFeatureCollectionFromJson() throws IOException {
        try {
            // Use fromJson() method to convert the GeoJSON file into a usable FeatureCollection object
            featureCollection = FeatureCollection.fromJson(loadGeoJsonFromAsset("list_of_locations.geojson"));

        } catch (Exception exception) {
            Log.e("MapActivity", "getFeatureCollectionFromJson: " + exception);
        }
    }

    /**
     * Load our Json file from assets
     * @param filename
     * @return
     */
    private String loadGeoJsonFromAsset(String filename) {
        try {
            // Load the GeoJSON file from the local asset folder
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (Exception exception) {
            Log.e("MapActivity", "Exception Loading GeoJSON: " + exception.toString());
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Draw Navigation lines to display route to user
     * @param route
     */
    private void drawNavigationPolylineRoute(DirectionsRoute route) {
        // Retrieve and update the source designated for showing the store location icons
        GeoJsonSource source = map.getStyle().getSourceAs("navigation-route-source-id");
        if (source != null) {
            source.setGeoJson(FeatureCollection.fromFeature(Feature.fromGeometry(
                    LineString.fromPolyline(route.geometry(), PRECISION_6))));
        }
    }


    @SuppressWarnings({"MissingPermission"})
    /**
     * Enable Mapbox location component to show users' location
     */
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = map.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /**
     * Add a destionation icon
     * @param loadedMapStyle
     */
    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    /**
     * Set up our map style
     * @param loadedMapStyle
     */
    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    /**
     * Set up our map layer
     * @param loadedMapStyle
     */
    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[]{0f, -8f})
        ));
    }

    /**
     * Set up default map zoom
     * @param location
     */
    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13.0f)); // Zoom to new location
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        // Present toast or dialog if the deny permissions
        Toast.makeText(this, "Permission is required to view location", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            //enableLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Necessary activity lifestyles for our map & handle any memory leaks

    @Override
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            // locationEngine.requestLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null) {
            // locationEngine.removeLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            //   locationEngine.deactivate();
        }
        mapView.onDestroy();
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());

        GeoJsonSource source = map.getStyle().getSourceAs("destination-source-id");
        if (source != null) {
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }

        getRoute(originPoint, destinationPoint);
        startButton.setEnabled(true);
        startButton.setVisibility(View.VISIBLE);
        startButton.setBackgroundResource(R.color.mapboxBlue);
        return true;
    }

    /**
     * Get route between users' location and chosen destination
     * @param origin
     * @param destination
     */
    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(MapActivity.this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, check right user and access token");
                            return;
                        } else if (response.body().routes().size() == 0) {
                            Log.e(TAG, "No routes found");
                            return;
                        }
                        // Assuming we have at least one route from here on.
                        currentRoute = response.body().routes().get(0); // highest recommended
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "Error: " + t.getMessage());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon

            if (map != null) {
                Style style = map.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

                    // Move map camera to the selected location
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);
                    Point rePoint = Point.fromLngLat(((Point) selectedCarmenFeature.geometry()).latitude(), ((Point) selectedCarmenFeature.geometry()).longitude());
                    Point orPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                            locationComponent.getLastKnownLocation().getLatitude());
                    getRoute(orPoint, rePoint);

                }
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        // Get the selected individual location via its card's position in the recyclerview of cards
        IndividualLocation selectedLocation = listOfIndividualLocations.get(position);

        // Evaluate each Feature's "select state" to appropriately style the location's icon
        List<Feature> featureList = featureCollection.features();
        Point selectedLocationPoint = (Point) featureCollection.features().get(position).geometry();
        for (int i = 0; i < featureList.size(); i++) {
            if (featureList.get(i).getStringProperty("name").equals(selectedLocation.getName())) {
                if (featureSelectStatus(i)) {
                    setFeatureSelectState(featureList.get(i), false);
                } else {
                    setSelected(i);
                }
            } else {
                setFeatureSelectState(featureList.get(i), false);
            }
        }

        // Reposition the map camera target to the selected marker
        if (selectedLocation != null) {
            repositionMapCamera(selectedLocationPoint);
        }

        // Check for an internet connection before making the call to Mapbox Directions API
        if (deviceHasInternetConnection()) {
            // Start call to the Mapbox Directions API
            if (selectedLocation != null) {
                getInformationFromDirectionsApi(selectedLocationPoint, true, null);
            }
        } else {
            Toast.makeText(this, R.string.no_internet_message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Check if users' device has internet connection
     * @return users' network connection
     */
    private boolean deviceHasInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    /**
     * Reposition map location to target location
     * @param newTarget
     */
    private void repositionMapCamera(Point newTarget) {
        CameraPosition newCameraPosition = new CameraPosition.Builder()
                .target(new LatLng(newTarget.latitude(), newTarget.longitude()))
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition), CAMERA_MOVEMENT_SPEED_IN_MILSECS);
    }

    /**
     * Set a feature selected state.
     * @param index the index of selected feature
     */
    private void setSelected(int index) {
        Feature feature = featureCollection.features().get(index);
        setFeatureSelectState(feature, true);
        refreshSource();
    }

    /**
     * Checks whether a Feature's boolean "selected" property is true or false
     * @param index the specific Feature's index position in the FeatureCollection's list of Features.
     * @return true if "selected" is true. False if the boolean property is false.
     */
    private boolean featureSelectStatus(int index) {
        if (featureCollection == null) {
            return false;
        }
        return featureCollection.features().get(index).getBooleanProperty(PROPERTY_SELECTED);
    }

    /**
     * Selects the state of a feature
     * @param feature the feature to be selected.
     */
    private void setFeatureSelectState(Feature feature, boolean selectedState) {
        feature.properties().addProperty(PROPERTY_SELECTED, selectedState);
        refreshSource();
    }

    /**
     * Updates the display of data on the map after the FeatureCollection has been modified
     */
    private void refreshSource() {
        GeoJsonSource source = map.getStyle().getSourceAs("store-location-source-id");
        if (source != null && featureCollection != null) {
            source.setGeoJson(featureCollection);
        }
    }

    /**
     * Custom class which creates marker icons and colors based on the selected theme
     */
    class CustomThemeManager {
        private int selectedTheme;
        private Context context;
        private Bitmap unselectedMarkerIcon;
        private Bitmap selectedMarkerIcon;
        private Bitmap mockLocationIcon;
        private int navigationLineColor;
        private String mapStyle;

        CustomThemeManager(int selectedTheme, Context context) {
            this.selectedTheme = selectedTheme;
            this.context = context;
            initializeTheme();
        }

        private void initializeTheme() {
            switch (selectedTheme) {
                case R.style.AppTheme:
                    mapStyle = getString(R.string.blue_map_style);
                    navigationLineColor = getResources().getColor(R.color.navigationRouteLine_blue);
                    unselectedMarkerIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.mapbox_marker_icon_default);
                    selectedMarkerIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.mapbox_marker_icon_default);
                    mockLocationIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.mapbox_marker_icon_default);
                    break;
            }
        }

        public Bitmap getUnselectedMarkerIcon() {
            return unselectedMarkerIcon;
        }

        public Bitmap getMockLocationIcon() {
            return mockLocationIcon;
        }

        public Bitmap getSelectedMarkerIcon() {
            return selectedMarkerIcon;
        }

        int getNavigationLineColor() {
            return navigationLineColor;
        }

        public String getMapStyle() {
            return mapStyle;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
