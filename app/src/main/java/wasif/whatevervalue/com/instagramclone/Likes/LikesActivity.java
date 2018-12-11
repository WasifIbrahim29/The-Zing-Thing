package wasif.whatevervalue.com.instagramclone.Likes;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Date;

import wasif.whatevervalue.com.instagramclone.Models.User;
import wasif.whatevervalue.com.instagramclone.R;
import wasif.whatevervalue.com.instagramclone.Utils.BottomNavigationViewHelper;
import wasif.whatevervalue.com.instagramclone.services.LocationService;

import static wasif.whatevervalue.com.instagramclone.Utils.Constants.ERROR_DIALOG_REQUEST;
import static wasif.whatevervalue.com.instagramclone.Utils.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static wasif.whatevervalue.com.instagramclone.Utils.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class LikesActivity extends AppCompatActivity {
    private static final String TAG = "LikesActivity";
    private static final int ACTIVITY_NUM=3;
    private Context mContext=LikesActivity.this;

    //vars
    //private ArrayList<Chatroom> mChatrooms = new ArrayList<>();
    //private Set<String> mChatroomIds = new HashSet<>();
    //private ChatroomRecyclerAdapter mChatroomRecyclerAdapter;
    private RecyclerView mChatroomRecyclerView;
    private ListenerRegistration mChatroomEventListener;
    private FirebaseFirestore mDb;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    //private UserLocation mUserLocation;
    private ArrayList<User> mUserList = new ArrayList<>();
    //private ArrayList<users> mUserNewList = new ArrayList<>();
    private ListenerRegistration mChatMessageEventListener, mUserListEventListener;
    private UserLocation mUserLocation;
    private DatabaseReference mRootRef;
    private DatabaseReference mUserLocationRef;
    private String mCurrentUserId;
    private FirebaseAuth mAuth;
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        Log.d(TAG, "onCreate: started");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        //mUserList=null;

        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();


        getChatroomUsers(new FirebaseCallBack() {
            @Override
            public void onCallback(ArrayList<User> mUserList) {

                for(User user: mUserList){

                    getChatroomLocations(new FirebaseCallBack1() {
                        @Override
                        public void onCallback(ArrayList<UserLocation> mUserLocations) {


                            inflateUserListFragment();

                        }
                    },user);

                }
            }
        });

        Log.d(TAG, "onCreate: "+ mUserList);
        //Log.d(TAG, "onCreate: "+ mUserList.get(0));
        //inflateUserListFragment();



        //setupBottomNavigationView();
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, LocationService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

                LikesActivity.this.startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("wasif.whatevervalue.com.instagramclone.services.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }




    private void getUserDetails(){
        if(mUserLocation == null){
            mUserLocation = new UserLocation();

            DatabaseReference locationRef=mRootRef.child("users").child(FirebaseAuth.getInstance().getUid());

            locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user=dataSnapshot.getValue(User.class);
                    mUserLocation.setUser(user);
                    ((UserClient)(getApplicationContext())).setUser(user);
                    getLastKnownLocation();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            getLastKnownLocation();
        }
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull Task<android.location.Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint=new GeoPoint(location.getLatitude(),location.getLongitude());
                    mUserLocation.setGeo_point(geoPoint);
                    Date obj= new Date(System.currentTimeMillis()*1000);
                    mUserLocation.setTimestamp(obj);
                    saveUserLocation();
                    startLocationService();
                }
            }
        });

    }

    private void saveUserLocation(){

        if(mUserLocation != null){

            DatabaseReference locationRef=mRootRef.child("User Locations").child(FirebaseAuth.getInstance().getUid());

            locationRef.setValue(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "saveUserLocation: \ninserted user location into database." +
                                "\n latitude: " + mUserLocation.getGeo_point().getLatitude() +
                                "\n longitude: " + mUserLocation.getGeo_point().getLongitude());

                    }

                }
            });
        }
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getUserDetails();
            //getLastKnownLocation();
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LikesActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LikesActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    getUserDetails();

                }
                else{
                    getLocationPermission();
                }
            }
        }

    }

    private void getChatroomUsers(final FirebaseCallBack firebaseCallBack ){

        DatabaseReference usersRef=mRootRef.child("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){

                    //mUserLocations= new ArrayList<>();
                    for (DataSnapshot doc : dataSnapshot.getChildren()) {
                        User user = doc.getValue(User.class);
                        mUserList.add(user);

                    }

                    firebaseCallBack.onCallback(mUserList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getChatroomLocations(final FirebaseCallBack1 firebaseCallBack, final User user ){

        DatabaseReference usersRef=mRootRef.child("User Locations");

        usersRef.child(user.getUser_id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){

                    //mUserLocations= new ArrayList<>();
                    UserLocation userlocation=dataSnapshot.getValue(UserLocation.class);
                    mUserLocations.add(userlocation);
                    firebaseCallBack.onCallback(mUserLocations);

                    //firebaseCallBack.onCallback(userlocation);

                    //inflateUserListFragment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public interface FirebaseCallBack {
        void onCallback(ArrayList<User> mUserList);
    }

    public interface FirebaseCallBack1 {
        void onCallback(ArrayList<UserLocation> mUserLocations);
    }

    private void inflateUserListFragment(){
        hideSoftKeyboard();
        

        if(mUserList!=null){
            Log.d(TAG, "inflateUserListFragment: inside shit");
            //Log.d(TAG, "inflateUserListFragment: "+ mUserList.get(0));
            Log.d(TAG, "inflateUserListFragment: "+ mUserList.get(0).getUsername());
            //Log.d(TAG, "inflateUserListFragment: "+ userLocation.get(0).getGeo_point());
            //Toast.makeText(mContext,mUserList.get(0).getUsername(),Toast.LENGTH_SHORT).show();

            UserListFragment fragment = UserListFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(getString(R.string.intent_user_list), mUserList);
            bundle.putParcelableArrayList(getString(R.string.intent_user_locations),mUserLocations);
            fragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
            transaction.replace(R.id.user_list_container, fragment, getString(R.string.fragment_user_list));
            transaction.addToBackStack(getString(R.string.fragment_user_list));
            transaction.commit();

        }
        else{
            Log.d(TAG, "inflateUserListFragment: Its not working");
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkMapServices()){
            if(mLocationPermissionGranted){
                getUserDetails();
                //UserListFragment fragment = new UserListFragment();
                //FragmentTransaction transaction = LikesActivity.this.getSupportFragmentManager().beginTransaction();
                //transaction.replace(R.id.user_list_container, fragment);
                //transaction.commit();
            }
            else{
                getLocationPermission();
            }
        }
    }







    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up bottamNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
