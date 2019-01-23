package com.hostelbasera.seller;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.Listener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.apis.PostWithRequestParam;
import com.hostelbasera.main.CategoryListActivity;
import com.hostelbasera.model.AddImageAttachmentModel;
import com.hostelbasera.model.AddRoomModel;
import com.hostelbasera.model.CheckSellerPaymentDataModel;
import com.hostelbasera.model.FileUploadModel;
import com.hostelbasera.model.GetPropertyDetModel;
import com.hostelbasera.model.SellerDropdownModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;

import static com.hostelbasera.utility.Globals.checkFileSize;

public class AddHostelPGActivity extends BaseActivity implements PermissionListener, GoogleApiClient.OnConnectionFailedListener, Listener {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img_share)
    ImageView imgShare;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sp_property)
    Spinner spProperty;
    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.sp_category)
    Spinner spCategory;
    @BindView(R.id.sp_type_of_property)
    Spinner spTypeOfProperty;
    @BindView(R.id.sp_size_of_property)
    Spinner spSizeOfProperty;
    @BindView(R.id.edt_email)
    EditText edtEmail;
    @BindView(R.id.tv_address)
    TextView edtAddress;
    @BindView(R.id.rv_contact)
    RecyclerView rvContact;
    @BindView(R.id.edt_description)
    EditText edtDescription;
    @BindView(R.id.sp_state)
    Spinner spState;
    @BindView(R.id.sp_city)
    Spinner spCity;
    @BindView(R.id.btn_facility)
    Button btnFacility;
    @BindView(R.id.edt_water_timings)
    EditText edtWaterTimings;
    @BindView(R.id.edt_open_hours)
    EditText edtOpenHours;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.edt_price)
    EditText edtPrice;
    @BindView(R.id.rv_rooms)
    RecyclerView rvRooms;
    @BindView(R.id.btn_add_hostel_pg)
    Button btnAddHostelPg;

    @BindView(R.id.ll_city)
    LinearLayout llCity;
    @BindView(R.id.tv_add_contact)
    AppCompatTextView tvAddContact;

    @BindView(R.id.ll_type_of_property)
    LinearLayout llTypeOfProperty;
    @BindView(R.id.ll_size_of_property)
    LinearLayout llSizeOfProperty;

    @BindView(R.id.ll_water_timings)
    LinearLayout llWaterTimings;
    @BindView(R.id.ll_laundry_fees)
    LinearLayout llLaundryFees;
    @BindView(R.id.edt_laundry_fees)
    EditText edtLaundryFees;

    @BindView(R.id.tv_add_menu)
    TextView tvAddMenu;
    @BindView(R.id.rv_menu)
    RecyclerView rvMenu;
    @BindView(R.id.cv_cooking_menu)
    CardView cvCookingMenu;
    EasyWayLocation easyWayLocation;

    Globals globals;
    SellerDropdownModel.SellerDropdownDetail sellerDropdownDetail;
    ArrayList<SellerDropdownModel.TypesList> arrProperty;
    ArrayList<SellerDropdownModel.PropertycategoriesList> arrCategories;
    ArrayList<SellerDropdownModel.PropertytypesList> arrTypeOfProperty;
    ArrayList<SellerDropdownModel.Propertysizes> arrPropertySizes;
    ArrayList<SellerDropdownModel.StateList> arrStateList;
    ArrayList<SellerDropdownModel.CityList> arrCityList;
    ArrayList<SellerDropdownModel.FacilityList> arrFacilityList;

    ArrayAdapter<SellerDropdownModel.TypesList> adapterProperty;
    ArrayAdapter<SellerDropdownModel.PropertycategoriesList> adapterCategories;
    ArrayAdapter<SellerDropdownModel.PropertytypesList> adapterTypeOfProperty;
    ArrayAdapter<SellerDropdownModel.Propertysizes> adapterPropertySizes;
    ArrayAdapter<SellerDropdownModel.StateList> adapterStateList;
    ArrayAdapter<SellerDropdownModel.CityList> adapterCityList;
    ArrayAdapter<SellerDropdownModel.FacilityList> adapterFacilityList;

    int type_id = 0, property_category_id = 0, property_type_id = 0, property_size_id = 0, state_id = 0, city_id = 0, facility_id = 0;
    AdapterContact adapterContact;
    AdapterRoom adapterRoom;
    ArrayList<String> arrFile;
    AdapterAddAttachment adapterAddAttachment;
    ArrayList<AddImageAttachmentModel> arrAddImageAttachment;
    CharSequence[] arrFacility;

    AdapterMenuAttachment adapterMenuAttachment;
    ArrayList<AddImageAttachmentModel> arrAddMenuAttachment;
    int property_id;
    double longitude, latitude;
    ArrayList<String> arrContact;
    GetPropertyDetModel.PropertyDetails propertyDetails;

    public int PAYMENT_CODE = 123;
    String paymentId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hostel_pg);
        ButterKnife.bind(this);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        checkPermission();
//        init();
    }


    public void checkPermission() {
        new TedPermission(this)
                .setPermissionListener(this)
                .setRationaleMessage(R.string.rationale_message)
                .setDeniedMessage(R.string.denied_message)
                .setGotoSettingButtonText(R.string.ok)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    }

    double lng = 0;
    double lat = 0;

    @Override
    public void onPermissionGranted() {
        init();
        easyWayLocation = new EasyWayLocation(this);
        easyWayLocation.setListener(this);
    }

    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//        init();
        finish();
    }

    public void init() {
        imgBack.setVisibility(View.VISIBLE);
        globals = ((Globals) getApplicationContext());

        arrProperty = new ArrayList<>();
        arrCategories = new ArrayList<>();
        arrTypeOfProperty = new ArrayList<>();
        arrPropertySizes = new ArrayList<>();
        arrStateList = new ArrayList<>();
        arrCityList = new ArrayList<>();
        arrFacilityList = new ArrayList<>();
        arrAddImageAttachment = new ArrayList<>();
        arrAddMenuAttachment = new ArrayList<>();
        arrContact = new ArrayList<>();

        property_id = getIntent().getIntExtra(Constant.Property_id, 0);

        if (property_id > 0) {
            toolbarTitle.setText(getString(R.string.update_hostel_pg));
            btnAddHostelPg.setText(getString(R.string.update_hostel_pg));
        } else {
            toolbarTitle.setText(getString(R.string.add_hostel_pg));
            btnAddHostelPg.setText(getString(R.string.add_hostel_pg));
        }

        if (Globals.isNetworkAvailable(this)) {
            getSellerDropdownData();
        } else {
            Toaster.shortToast(R.string.no_internet_msg);
            finish();
        }
    }


    public void getSellerDropdownData() {
        JSONObject postData = HttpRequestHandler.getInstance().getSellerDropdownParam();

        if (postData != null) {
            new PostRequest(this, getString(R.string.getSellerDropdown), postData, true,
                    new PostRequest.OnPostServiceCallListener() {
                        @Override
                        public void onSucceedToPostCall(JSONObject response) {
                            SellerDropdownModel sellerDropdownModel = new Gson().fromJson(response.toString(), SellerDropdownModel.class);
                            if (sellerDropdownModel.status == 0) {
                                sellerDropdownDetail = sellerDropdownModel.sellerDropdownDetail;
                                setData();

                            } else
                                Toaster.shortToast(sellerDropdownModel.message);
                        }

                        @Override
                        public void onFailedToPostCall(int statusCode, String msg) {
                            Toaster.shortToast(msg);
                        }
                    }).execute();
        }
        Globals.hideKeyboard(this);
    }

    public void getPropertyDetails() {
        JSONObject postData = HttpRequestHandler.getInstance().getPropertyDetParam(property_id);

        if (postData != null) {
            new PostRequest(this, getString(R.string.getPropertyDet), postData, true,
                    new PostRequest.OnPostServiceCallListener() {
                        @Override
                        public void onSucceedToPostCall(JSONObject response) {
                            GetPropertyDetModel getPropertyDetModel = new Gson().fromJson(response.toString(), GetPropertyDetModel.class);
                            if (getPropertyDetModel.status == 0) {
                                propertyDetails = getPropertyDetModel.propertyDetails;
                                setPropertyDetails();
                            } else
                                Toaster.shortToast(getPropertyDetModel.message);
                        }

                        @Override
                        public void onFailedToPostCall(int statusCode, String msg) {
                            Toaster.shortToast(msg);
                        }
                    }).execute();
        }
        Globals.hideKeyboard(this);
    }

    public void setData() {
        setSpProperty();
        setSpCategory();
        setSpTypeOfProperty();
        setSpSizeOfProperty();
        setSpState();
        arrFacilityList.addAll(sellerDropdownDetail.facilityList);

        setContactAdapter(null);
        setRoomsAdapter();

        if (property_id > 0) {
            getPropertyDetails();
        }
    }

    public void setPropertyDetails() {

        if (propertyDetails != null) {
            edtName.setText(propertyDetails.property_name);

            for (int i = 0; i < arrProperty.size(); i++) {
                if (arrProperty.get(i).type_id == propertyDetails.type_id) {
                    spProperty.setSelection(i);
                    type_id = propertyDetails.type_id;
                    if (type_id == 2) {
                        llTypeOfProperty.setVisibility(View.VISIBLE);
                        llSizeOfProperty.setVisibility(View.VISIBLE);
//                        setSpTypeOfProperty();
                        for (int j = 0; j < arrTypeOfProperty.size(); j++) {
                            if (arrTypeOfProperty.get(j).property_type_id == propertyDetails.property_type_id) {
                                spTypeOfProperty.setSelection(j);
                                property_type_id = propertyDetails.property_type_id;
                            }
                        }

//                        setSpSizeOfProperty();
                        for (int k = 0; k < arrPropertySizes.size(); k++) {
                            if (arrPropertySizes.get(k).property_size_id == propertyDetails.property_size_id) {
                                spSizeOfProperty.setSelection(k);
                                property_size_id = propertyDetails.property_size_id;
                            }
                        }
                    } else {
                        property_type_id = 0;
                        property_size_id = 0;
                        llTypeOfProperty.setVisibility(View.GONE);
                        llSizeOfProperty.setVisibility(View.GONE);
                    }
                }
            }

            for (int i = 0; i < arrCategories.size(); i++) {
                if (arrCategories.get(i).property_category_id == propertyDetails.property_category_id) {
                    spCategory.setSelection(i);
                    property_category_id = propertyDetails.property_category_id;
                    break;
                }
            }

            edtEmail.setText(propertyDetails.email);
            edtAddress.setText(propertyDetails.address);

            setContactAdapter(propertyDetails.cont_no);
            edtDescription.setText(propertyDetails.description);

            for (int i = 0; i < arrStateList.size(); i++) {
                if (arrStateList.get(i).state_id == propertyDetails.state_id) {
                    spState.setSelection(i);
                    state_id = propertyDetails.state_id;
                    city_id = 0;
                    if (arrStateList.get(i).cityList != null && !arrStateList.get(i).cityList.isEmpty()) {
                        llCity.setVisibility(View.VISIBLE);
                        setSpCity(arrStateList.get(i).cityList);
                        for (int j = 0; j < arrCityList.size(); j++) {
                            if (arrCityList.get(j).city_id == propertyDetails.city_id) {
                                city_id = propertyDetails.city_id;
                                spCity.setSelection(j);
                            }
                        }
                    } else {
                        llCity.setVisibility(View.GONE);
                    }
                }
            }

            for (int i = 0; i < arrFacilityList.size(); i++) {
                for (int j = 0; j < propertyDetails.propertyFacility.size(); j++) {
                    if (arrFacilityList.get(i).facility_id == propertyDetails.propertyFacility.get(j)) {
                        arrFacilityList.get(i).isSelected = true;
                        onChangeSelectedFacility();
                    }
                }
            }

            edtOpenHours.setText(propertyDetails.timing);

            for (int i = 0; i < propertyDetails.productImages.size(); i++) {
                AddImageAttachmentModel addImageAttachmentModel = new AddImageAttachmentModel();
                addImageAttachmentModel.FileName = propertyDetails.productImages.get(i);
                addImageAttachmentModel.FilePath = getString(R.string.image_url) + propertyDetails.productImages.get(i);
                arrAddImageAttachment.add(addImageAttachmentModel);
            }

            setAttachment();
            edtPrice.setText(propertyDetails.price);

            ArrayList<AddRoomModel> mValues = new ArrayList<>();
            AddRoomModel addRoomModel;
            if (propertyDetails.propertyrooms != null && !propertyDetails.propertyrooms.isEmpty())

                for (int i = 0; i < propertyDetails.propertyrooms.size(); i++) {
                    addRoomModel = new AddRoomModel();
                    addRoomModel.name = propertyDetails.propertyrooms.get(i).roomname;
                    addRoomModel.price = propertyDetails.propertyrooms.get(i).roomprice;
                    mValues.add(addRoomModel);
                }

            if (adapterRoom == null) {
                adapterRoom = new AdapterRoom(this);
            }
            adapterRoom.doRefresh(mValues);

            if (rvRooms.getAdapter() == null) {
                rvRooms.setLayoutManager(new LinearLayoutManager(this));
                rvRooms.setItemAnimator(new DefaultItemAnimator());
                rvRooms.setAdapter(adapterRoom);
            }
            latitude = propertyDetails.latitude != null && !propertyDetails.latitude.isEmpty() ? Double.parseDouble(propertyDetails.latitude) : 0;
            longitude = propertyDetails.longitude != null && !propertyDetails.longitude.isEmpty() ? Double.parseDouble(propertyDetails.longitude) : 0;
        }
    }

    @OnClick(R.id.tv_add_contact)
    public void doAddContact() {
        if (adapterContact != null) {
            adapterContact.doAdd();
        } else {
            setContactAdapter(null);
        }
    }

    @OnClick(R.id.tv_add_room)
    public void doAddRoom() {
        if (adapterRoom != null) {
            adapterRoom.doAdd();
        } else {
            setRoomsAdapter();
        }
    }

    public void setContactAdapter(ArrayList<String> arrCont) {
        if (adapterContact == null) {
            adapterContact = new AdapterContact(this);
        }
        if (arrCont != null)
            adapterContact.doRefresh(arrCont);

        if (rvContact.getAdapter() == null) {
            rvContact.setLayoutManager(new LinearLayoutManager(this));
            rvContact.setItemAnimator(new DefaultItemAnimator());
            rvContact.setAdapter(adapterContact);
        }
    }

    public void setRoomsAdapter() {
        if (adapterRoom == null) {
            adapterRoom = new AdapterRoom(this);
        }
//        adapterContact.doAdd();

        if (rvRooms.getAdapter() == null) {
            rvRooms.setLayoutManager(new LinearLayoutManager(this));
            rvRooms.setItemAnimator(new DefaultItemAnimator());
            rvRooms.setAdapter(adapterRoom);
        }
    }

    boolean isImage;

    @OnClick(R.id.tv_add_image)
    public void doAddImage() {
        isImage = true;
        FilePickerBuilder.getInstance()
                .setMaxCount(10)
//                    .setSelectedFiles(photoPaths)
                .setActivityTheme(R.style.LibAppTheme)
                .enableVideoPicker(false)
                .enableCameraSupport(true)
                .showGifs(false)
                .showFolderView(true)
                .enableImagePicker(true)
                .enableDocSupport(true)
                .withOrientation(Orientation.UNSPECIFIED)
                .pickPhoto(this);

    }

    @OnClick(R.id.tv_add_menu)
    public void doAddMenuImage() {
        isImage = false;
        FilePickerBuilder.getInstance()
                .setMaxCount(10)
//                    .setSelectedFiles(photoPaths)
                .setActivityTheme(R.style.LibAppTheme)
                .enableVideoPicker(false)
                .enableCameraSupport(true)
                .showGifs(false)
                .showFolderView(true)
                .enableImagePicker(true)
                .enableDocSupport(true)
                .withOrientation(Orientation.UNSPECIFIED)
                .pickPhoto(this);

    }

    private GoogleApiClient mGoogleApiClient;
    int PLACE_PICKER_REQUEST = 153;

    @OnClick(R.id.tv_address)
    public void doAddAddress() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {

            builder.setLatLngBounds(new LatLngBounds(new LatLng(lat, lng), new LatLng(lat, lng)));
            startActivityForResult(builder.build(AddHostelPGActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                /*
                StringBuilder stBuilder = new StringBuilder();
                String placename = String.format("%s", place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());
                stBuilder.append("Name: ");
                stBuilder.append(placename);
                stBuilder.append("\n");
                stBuilder.append("Latitude: ");
                stBuilder.append(latitude);
                stBuilder.append("\n");
                stBuilder.append("Logitude: ");
                stBuilder.append(longitude);
                stBuilder.append("\n");
                stBuilder.append("Address: ");
                stBuilder.append(address);*/
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                edtAddress.setText(String.format("%s", place.getAddress()));
            }
        }

        if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                arrFile = new ArrayList<>();
                arrFile = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                doAttachment();
            }
        } else if (requestCode == PAYMENT_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                if (data.getStringExtra(Constant.RESULT).equals("Success")) {
                    paymentId = data.getStringExtra(Constant.Payment_id);

                    if (arrAddImageAttachment.size() > 0) {
                        setProgressDialog(arrAddImageAttachment.size());
                        for (int i = 0; i < arrAddImageAttachment.size(); i++) {
                            doUploadFile(new File(arrAddImageAttachment.get(i).FilePath), i);
                        }
                    } else if (arrAddMenuAttachment.size() > 0) {
                        setProgressDialog(arrAddMenuAttachment.size());
                        for (int i = 0; i < arrAddMenuAttachment.size(); i++) {
                            doUploadMenuFile(new File(arrAddMenuAttachment.get(i).FilePath), i);
                        }
                    } else {
                        doAddHostelPG();
                    }
                }

            }
        }
    }

    public void doAttachment() {
        for (int i = 0; i < arrFile.size(); i++) {
            if (checkFileSize(arrFile.get(i))) {
                Toaster.shortToast("Max 5mb file allowed.");
                return;
            }
        }
        for (int i = 0; i < arrFile.size(); i++) {
            AddImageAttachmentModel addImageAttachmentModel = new AddImageAttachmentModel();
            addImageAttachmentModel.FilePath = arrFile.get(i);
            if (isImage)
                arrAddImageAttachment.add(addImageAttachmentModel);
            else
                arrAddMenuAttachment.add(addImageAttachmentModel);
        }

        if (isImage)
            setAttachment();
        else
            setMenuAttachment();
    }

    public void setAttachment() {
        if (adapterAddAttachment == null) {
            adapterAddAttachment = new AdapterAddAttachment(this);
        }
        adapterAddAttachment.doRefresh(arrAddImageAttachment);

        if (rvImages.getAdapter() == null) {
            rvImages.setHasFixedSize(true);
            rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvImages.setItemAnimator(new DefaultItemAnimator());
            rvImages.setAdapter(adapterAddAttachment);
        }
    }

    public void setMenuAttachment() {
        if (adapterMenuAttachment == null) {
            adapterMenuAttachment = new AdapterMenuAttachment(this);
        }
        adapterMenuAttachment.doRefresh(arrAddMenuAttachment);

        if (rvMenu.getAdapter() == null) {
            rvMenu.setHasFixedSize(true);
            rvMenu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvMenu.setItemAnimator(new DefaultItemAnimator());
            rvMenu.setAdapter(adapterMenuAttachment);
        }
    }

    public void setSpProperty() {
        SellerDropdownModel.TypesList typesList = new SellerDropdownModel.TypesList();
        typesList.type_id = 0;
        typesList.type_name = "Select Property";

        arrProperty.add(typesList);
        arrProperty.addAll(sellerDropdownDetail.typesList);

        adapterProperty = new ArrayAdapter<SellerDropdownModel.TypesList>(getApplicationContext(), R.layout.spinner_item, arrProperty) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            // Change color item
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (position == 0) {
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }
                return mView;
            }
        };

        spProperty.setAdapter(adapterProperty);

        spProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type_id = arrProperty.get(position).type_id;
                if (type_id == 2) {
                    llTypeOfProperty.setVisibility(View.VISIBLE);
                    llSizeOfProperty.setVisibility(View.VISIBLE);
//                    spTypeOfProperty.setSelection(0);
//                    spSizeOfProperty.setSelection(0);
//                    setSpTypeOfProperty();
//                    setSpSizeOfProperty();
                } else {
                    property_type_id = 0;
                    property_size_id = 0;
                    llTypeOfProperty.setVisibility(View.GONE);
                    llSizeOfProperty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setSpCategory() {
        SellerDropdownModel.PropertycategoriesList propertycategoriesList = new SellerDropdownModel.PropertycategoriesList();
        propertycategoriesList.property_category_id = 0;
        propertycategoriesList.property_category_name = "Select Category";

        arrCategories.add(propertycategoriesList);
        arrCategories.addAll(sellerDropdownDetail.propertycategoriesList);

        adapterCategories = new ArrayAdapter<SellerDropdownModel.PropertycategoriesList>(getApplicationContext(), R.layout.spinner_item, arrCategories) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            // Change color item
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (position == 0) {
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }
                return mView;
            }
        };

        spCategory.setAdapter(adapterCategories);

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                property_category_id = arrCategories.get(position).property_category_id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setSpTypeOfProperty() {
        property_type_id = 0;
        arrTypeOfProperty = new ArrayList<>();
        SellerDropdownModel.PropertytypesList propertytypesList = new SellerDropdownModel.PropertytypesList();
        propertytypesList.property_type_id = 0;
        propertytypesList.property_type = "Select Type of Property";

        arrTypeOfProperty.add(propertytypesList);
        arrTypeOfProperty.addAll(sellerDropdownDetail.propertytypesList);

        adapterTypeOfProperty = new ArrayAdapter<SellerDropdownModel.PropertytypesList>(getApplicationContext(), R.layout.spinner_item, arrTypeOfProperty) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            // Change color item
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (position == 0) {
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }
                return mView;
            }
        };

        spTypeOfProperty.setAdapter(adapterTypeOfProperty);

        spTypeOfProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                property_type_id = arrTypeOfProperty.get(position).property_type_id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setSpSizeOfProperty() {
        property_size_id = 0;
        arrPropertySizes = new ArrayList<>();
        SellerDropdownModel.Propertysizes propertysizes = new SellerDropdownModel.Propertysizes();
        propertysizes.property_size_id = 0;
        propertysizes.property_size = "Select Size of Property";

        arrPropertySizes.add(propertysizes);
        arrPropertySizes.addAll(sellerDropdownDetail.propertysizes);

        adapterPropertySizes = new ArrayAdapter<SellerDropdownModel.Propertysizes>(getApplicationContext(), R.layout.spinner_item, arrPropertySizes) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            // Change color item
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (position == 0) {
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }
                return mView;
            }
        };

        spSizeOfProperty.setAdapter(adapterPropertySizes);

        spSizeOfProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                property_size_id = arrPropertySizes.get(position).property_size_id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setSpState() {
        SellerDropdownModel.StateList stateList = new SellerDropdownModel.StateList();
        stateList.state_id = 0;
        stateList.state_name = "Select State";

        arrStateList.add(stateList);
        arrStateList.addAll(sellerDropdownDetail.stateList);

        adapterStateList = new ArrayAdapter<SellerDropdownModel.StateList>(getApplicationContext(), R.layout.spinner_item, arrStateList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            // Change color item
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (position == 0) {
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }
                return mView;
            }
        };

        spState.setAdapter(adapterStateList);

        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state_id = arrStateList.get(position).state_id;
                city_id = 0;
                if (arrStateList.get(position).cityList != null && !arrStateList.get(position).cityList.isEmpty()) {
                    llCity.setVisibility(View.VISIBLE);
                    setSpCity(arrStateList.get(position).cityList);
                } else {
                    llCity.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setSpCity(ArrayList<SellerDropdownModel.CityList> cityList) {
        arrCityList = new ArrayList<>();

        SellerDropdownModel.CityList cityListModel = new SellerDropdownModel.CityList();
        cityListModel.city_id = 0;
        cityListModel.city_name = "Select City";

        arrCityList.add(cityListModel);
        arrCityList.addAll(cityList);

        adapterCityList = new ArrayAdapter<SellerDropdownModel.CityList>(getApplicationContext(), R.layout.spinner_item, arrCityList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            // Change color item
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (position == 0) {
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }
                return mView;
            }
        };

        spCity.setAdapter(adapterCityList);

        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city_id = arrCityList.get(position).city_id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.btn_facility)
    protected void showSelectFacilityDialog() {
        boolean[] checkedFacility = new boolean[arrFacilityList.size()];
        arrFacility = new CharSequence[arrFacilityList.size()];

        for (int i = 0; i < arrFacilityList.size(); i++) {
            checkedFacility[i] = arrFacilityList.get(i).isSelected;
            arrFacility[i] = arrFacilityList.get(i).facility_name;
        }

        DialogInterface.OnMultiChoiceClickListener coloursDialogListener = new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                arrFacilityList.get(which).isSelected = isChecked;
                onChangeSelectedFacility();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Facility");
        builder.setMultiChoiceItems(arrFacility, checkedFacility, coloursDialogListener);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    protected void onChangeSelectedFacility() {
        StringBuilder strAssignee = new StringBuilder();
        for (int i = 0; i < arrFacilityList.size(); i++) {
            if (arrFacilityList.get(i).isSelected) {
                strAssignee.append(arrFacilityList.get(i).facility_name).append(", ");
                 /*
TODO :
    Facility Id = 15 Then laundry_fees textbox need to show / if not available then null
    Facility Id = 18 Then water_timing textbox need to hide / if not available then null
    Facility Id = 16 Then cooking_menu image need to upload / if not available then null
     */
                if (arrFacilityList.get(i).facility_id == 15) {
                    llLaundryFees.setVisibility(View.VISIBLE);
                    if (propertyDetails != null)
                        edtLaundryFees.setText(propertyDetails.laundry_fees);
                }
                if (arrFacilityList.get(i).facility_id == 16) {
                    cvCookingMenu.setVisibility(View.VISIBLE);

//                    if (propertyDetails != null)

                }
                if (arrFacilityList.get(i).facility_id == 18) {
                    edtWaterTimings.setText("");
                    llWaterTimings.setVisibility(View.GONE);
                }
            } else {
                if (arrFacilityList.get(i).facility_id == 15) {
                    edtLaundryFees.setText("");
                    llLaundryFees.setVisibility(View.GONE);
                }
                if (arrFacilityList.get(i).facility_id == 16) {
                    arrAddMenuAttachment = new ArrayList<>();
                    setMenuAttachment();
                    cvCookingMenu.setVisibility(View.GONE);
                }
                if (arrFacilityList.get(i).facility_id == 18) {
                    llWaterTimings.setVisibility(View.VISIBLE);
                    if (propertyDetails != null)
                        edtWaterTimings.setText(propertyDetails.water_timing);
                }
            }
        }
        if (!strAssignee.toString().isEmpty()) {
            strAssignee.deleteCharAt(strAssignee.length() - 2);
            btnFacility.setText(strAssignee.toString());
        } else
            btnFacility.setText(getString(R.string._none_selected_));
    }

    @OnClick(R.id.img_back)
    public void onImgBackClicked() {
        onBackPressed();
    }

    ArrayList<String> arrFileName;
    ArrayList<Integer> arrFileIndex;

    @OnClick(R.id.btn_add_hostel_pg)
    public void onBtnAddHostelPgClicked() {

        arrRoomDetails = new ArrayList<>();
        arrContact = new ArrayList<>();
        if (adapterRoom != null) {
            arrRoomDetails = adapterRoom.mValues;
        }

        if (adapterContact != null) {
            arrContact = adapterContact.mValues;
        }

        if (!doValidate())
            return;

        if (Globals.isNetworkAvailable(this)) {
            doCheckSellerPaymentData();
        } else {
            Toaster.shortToast(R.string.no_internet_msg);
            return;
        }
    }

    String price_plan = "";

    public void doCheckSellerPaymentData() {
        price_plan = "";
        JSONObject postData = HttpRequestHandler.getInstance().getCheckSellerPaymentDataParam();

        if (postData != null) {
            new PostRequest(this, getString(R.string.checkSellerPaymentData), postData, true,
                    new PostRequest.OnPostServiceCallListener() {
                        @Override
                        public void onSucceedToPostCall(JSONObject response) {
                            CheckSellerPaymentDataModel checkSellerPaymentDataModel = new Gson().fromJson(response.toString(), CheckSellerPaymentDataModel.class);
                            if (checkSellerPaymentDataModel.status == 0) {
                                price_plan = checkSellerPaymentDataModel.price_plan;
                                if (checkSellerPaymentDataModel.payment_required == 1) {
                                    if (checkSellerPaymentDataModel.priceBlockDetails.size() > 0) {
                                        priceBlockDialog(checkSellerPaymentDataModel.priceBlockDetails);
                                    } else {
                                        startActivityForResult(new Intent(AddHostelPGActivity.this, PayMentGateWay.class)
                                                .putExtra(Constant.Full_name, edtName.getText().toString())
                                                .putExtra(Constant.Phone_number, arrContact.get(0))
                                                .putExtra(Constant.Email, edtEmail.getText().toString())
                                                .putExtra(Constant.Price, "" + checkSellerPaymentDataModel.payment_value), PAYMENT_CODE);
                                    }
                                } else {
                                    if (arrAddImageAttachment.size() > 0) {
                                        setProgressDialog(arrAddImageAttachment.size());
                                        for (int i = 0; i < arrAddImageAttachment.size(); i++) {
                                            doUploadFile(new File(arrAddImageAttachment.get(i).FilePath), i);
                                        }
                                    } else if (arrAddMenuAttachment.size() > 0) {
                                        setProgressDialog(arrAddMenuAttachment.size());
                                        for (int i = 0; i < arrAddMenuAttachment.size(); i++) {
                                            doUploadMenuFile(new File(arrAddMenuAttachment.get(i).FilePath), i);
                                        }
                                    } else {
                                        doAddHostelPG();
                                    }
                                }
                            } else
                                Toaster.shortToast(checkSellerPaymentDataModel.message);
                        }

                        @Override
                        public void onFailedToPostCall(int statusCode, String msg) {
                            Toaster.shortToast(msg);
                        }
                    }).execute();
        }
        Globals.hideKeyboard(this);
    }


    public void priceBlockDialog(ArrayList<CheckSellerPaymentDataModel.PriceBlockDetails> arrPriceBlockDetails) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.MyEnquiryAlertDialogStyle);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pricing_block_dialog, null);
        dialogBuilder.setView(dialogView);

        TextView tvSelectHostelPgPlans = dialogView.findViewById(R.id.tv_select_hostel_pg_plans);
        RecyclerView rvPricing = dialogView.findViewById(R.id.rv_pricing);
        tvSelectHostelPgPlans.setTypeface(tvSelectHostelPgPlans.getTypeface(), Typeface.BOLD);

        AlertDialog alertDialog = dialogBuilder.create();
        AdapterPricing adapterPricing = null;
        if (adapterPricing == null) {
            adapterPricing = new AdapterPricing(this);
        }
        adapterPricing.doRefresh(arrPriceBlockDetails);

        if (rvPricing.getAdapter() == null) {
            rvPricing.setHasFixedSize(false);
            rvPricing.setLayoutManager(new GridLayoutManager(this, Constant.GRID_SPAN));
            rvPricing.setItemAnimator(new DefaultItemAnimator());
            rvPricing.setAdapter(adapterPricing);
        }
        adapterPricing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivityForResult(new Intent(AddHostelPGActivity.this, PayMentGateWay.class)
                        .putExtra(Constant.Full_name, edtName.getText().toString())
                        .putExtra(Constant.Phone_number, arrContact.get(0))
                        .putExtra(Constant.Email, edtEmail.getText().toString())
                        .putExtra(Constant.Price, "" + arrPriceBlockDetails.get(position).price), PAYMENT_CODE);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void setProgressDialog(int size) {
        FileCount = 0;
        arrFileName = new ArrayList<>();
        arrFileIndex = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(size);
        progressDialog.setTitle("File Uploading");
        progressDialog.show();
    }

    ProgressDialog progressDialog;
    int FileCount = 0;

    private void doUploadFile(File file, int index) {
        RequestParams requestParams = new RequestParams();
        try {
            requestParams.put(Constant.Userfile, file);
            requestParams.put(Constant.File_id, index);
            requestParams.put(Constant.Type, "property");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        new PostWithRequestParam(this, getString(R.string.uploadPropertyimages), requestParams,
                false, new PostWithRequestParam.OnPostWithReqParamServiceCallListener() {
            @Override
            public void onSucceedToPostCall(JSONObject response) {
                progressDialog.setProgress(FileCount);
                FileCount++;
                FileUploadModel fileUploadModel = new Gson().fromJson(response.toString(), FileUploadModel.class);
                if (fileUploadModel.status == 0) {
                    arrFileName.add(fileUploadModel.uploadPropertyImagesDetail.file_name);
                    arrFileIndex.add(fileUploadModel.uploadPropertyImagesDetail.file_id);

                    if (arrFileName.size() == arrAddImageAttachment.size()) {
                        int count = 0;
                        for (int j = 0; j < arrAddImageAttachment.size(); j++) {
                            for (int k = 0; k < arrFileIndex.size(); k++) {
                                if (arrFileIndex.get(k) == count) {
                                    arrAddImageAttachment.get(j).FileName = arrFileName.get(k);
                                    break;
                                }
                            }
                            count++;
                        }
//                        if (dialog != null && dialog.isShowing())
//                            dialog.dismiss();
                        progressDialog.dismiss();
                        if (arrAddMenuAttachment.size() > 0) {
                            setProgressDialog(arrAddMenuAttachment.size());
                            for (int i = 0; i < arrAddMenuAttachment.size(); i++) {
                                doUploadMenuFile(new File(arrAddMenuAttachment.get(i).FilePath), i);
                            }
                        } else {
                            doAddHostelPG();
                        }
                    }
                } else {
//                    isFailToUpload = true;
                    progressDialog.dismiss();
                    Toaster.shortToast(fileUploadModel.message);
                }
            }

            @Override
            public void onFailedToPostCall(int statusCode, String msg) {
//                isFailToUpload = true;
//                if (dialog != null && dialog.isShowing())
//                    dialog.dismiss();
                progressDialog.dismiss();
                Toaster.shortToast(msg);
            }

            @Override
            public void onProgressCall(int progress) {
                progressDialog.setProgress(progress);
            }
        }).execute();
    }

    private void doUploadMenuFile(File file, int index) {
        RequestParams requestParams = new RequestParams();
        try {
            requestParams.put(Constant.Userfile, file);
            requestParams.put(Constant.File_id, index);
            requestParams.put(Constant.Type, "coocking_menu");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        new PostWithRequestParam(this, getString(R.string.uploadPropertyimages), requestParams,
                false, new PostWithRequestParam.OnPostWithReqParamServiceCallListener() {
            @Override
            public void onSucceedToPostCall(JSONObject response) {
                progressDialog.setProgress(FileCount);
                FileCount++;
                FileUploadModel fileUploadModel = new Gson().fromJson(response.toString(), FileUploadModel.class);
                if (fileUploadModel.status == 0) {
                    arrFileName.add(fileUploadModel.uploadPropertyImagesDetail.file_name);
                    arrFileIndex.add(fileUploadModel.uploadPropertyImagesDetail.file_id);

                    if (arrFileName.size() == arrAddMenuAttachment.size()) {
                        int count = 0;
                        for (int j = 0; j < arrAddMenuAttachment.size(); j++) {
                            for (int k = 0; k < arrFileIndex.size(); k++) {
                                if (arrFileIndex.get(k) == count) {
                                    arrAddMenuAttachment.get(j).FileName = arrFileName.get(k);
                                    break;
                                }
                            }
                            count++;
                        }
//                        if (dialog != null && dialog.isShowing())
//                            dialog.dismiss();
                        progressDialog.dismiss();
                        doAddHostelPG();
                    }
                } else {
//                    isFailToUpload = true;
                    progressDialog.dismiss();
                    Toaster.shortToast(fileUploadModel.message);
                }
            }

            @Override
            public void onFailedToPostCall(int statusCode, String msg) {
//                isFailToUpload = true;
//                if (dialog != null && dialog.isShowing())
//                    dialog.dismiss();
                progressDialog.dismiss();
                Toaster.shortToast(msg);
            }

            @Override
            public void onProgressCall(int progress) {
                progressDialog.setProgress(progress);
            }
        }).execute();
    }

    public ArrayList<AddRoomModel> arrRoomDetails;


    public void doAddHostelPG() {
        StringBuilder contact_no = new StringBuilder();
        for (int i = 0; i < arrContact.size(); i++) {
            contact_no.append(arrContact.get(i)).append(", ");
        }
// TODO : Do Check Proper Data in Update Hostel / PG
        JSONObject postData = HttpRequestHandler.getInstance().getAddPropertyParam(property_id, type_id, edtName.getText().toString(),
                property_category_id, property_size_id, edtEmail.getText().toString(), edtAddress.getText().toString(), longitude, latitude,
                contact_no.deleteCharAt(contact_no.length() - 2).toString(), edtDescription.getText().toString(), state_id, city_id, edtOpenHours.getText().toString(),
                edtWaterTimings.getText().toString(), edtLaundryFees.getText().toString(), arrAddMenuAttachment, edtPrice.getText().toString(), arrFacilityList,
                arrAddImageAttachment, arrRoomDetails, paymentId, price_plan);


        if (postData != null) {
            new PostRequest(this, getString(R.string.addProperty), postData, true,
                    new PostRequest.OnPostServiceCallListener() {
                        @Override
                        public void onSucceedToPostCall(JSONObject response) {
                            SellerDropdownModel sellerDropdownModel = new Gson().fromJson(response.toString(), SellerDropdownModel.class);
                            if (sellerDropdownModel.status == 0) {
                                onBackPressed();
                            }
                            Toaster.shortToast(sellerDropdownModel.message);
                        }

                        @Override
                        public void onFailedToPostCall(int statusCode, String msg) {
                            Toaster.shortToast(msg);
                        }
                    }).execute();
        }
        Globals.hideKeyboard(this);
    }

    public boolean doValidate() {
        if (edtName.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter name");
            return false;
        }
        if (type_id == 0) {
            Toaster.shortToast("Please select property");
            return false;
        }
        if (property_category_id == 0) {
            Toaster.shortToast("Please select category");
            return false;
        }
        if (edtAddress.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter address");
            return false;
        }

//        getLocationFromAddress(edtAddress.getText().toString().trim());

        /* if (latitude == 0 || longitude == 0) {
            Toaster.shortToast("Enter valid address.");
            return false;
        }*/

        if (arrContact.isEmpty()) {
            Toaster.shortToast("Please enter contact");
            return false;
        }
        if (edtDescription.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter description");
            return false;
        }
        if (state_id == 0) {
            Toaster.shortToast("Please select state");
            return false;
        }
        if (city_id == 0) {
            Toaster.shortToast("Please select city");
            return false;
        }
        if (!doSelectedFacility()) {
            Toaster.shortToast("Please select facility");
            return false;
        }
        if (edtPrice.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter price");
            return false;
        }
        if (arrRoomDetails.isEmpty()) {
            Toaster.shortToast("Please enter room details");
            return false;
        }
        return true;
    }


    public boolean doSelectedFacility() {
        for (int i = 0; i < arrFacilityList.size(); i++) {
            if (arrFacilityList.get(i).isSelected) {
                return true;
            }
        }
        return false;
    }


    public void getLocationFromAddress(String strAddress) {
        latitude = 0;
        longitude = 0;
        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.isEmpty()) {
                return;
            }

            Address location = address.get(0);

            latitude = location.getLatitude();
            longitude = location.getLongitude();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK, new Intent(this, SellerDashboardActivity.class));
        finish();
    }

    @OnClick(R.id.ll_main)
    public void onClickMain() {
        Globals.hideKeyboard(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(edtAddress, connectionResult.getErrorMessage() + "", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (easyWayLocation != null)
            easyWayLocation.beginUpdates();
    }


    @Override
    public void locationOn() {
        easyWayLocation.beginUpdates();
        lat = easyWayLocation.getLatitude();
        lng = easyWayLocation.getLongitude();
    }

    @Override
    public void onPositionChanged() {
        lat = easyWayLocation.getLatitude();
        lng = easyWayLocation.getLongitude();
    }

    @Override
    public void locationCancelled() {
        easyWayLocation.showAlertDialog("Cancelled", "Cancelled Location", null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (easyWayLocation != null)
            easyWayLocation.endUpdates();
    }
}

