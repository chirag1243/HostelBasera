package com.hostelbasera.seller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.SellerDropdownModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;

import static com.hostelbasera.utility.Globals.checkFileSize;

public class AddHostelPGActivity extends BaseActivity {

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
    @BindView(R.id.edt_add_comment)
    EditText edtAddComment;
    @BindView(R.id.sp_category)
    Spinner spCategory;
    @BindView(R.id.sp_type_of_property)
    Spinner spTypeOfProperty;
    @BindView(R.id.sp_size_of_property)
    Spinner spSizeOfProperty;
    @BindView(R.id.edt_email)
    EditText edtEmail;
    @BindView(R.id.edt_address)
    EditText edtAddress;
    @BindView(R.id.rv_contact)
    RecyclerView rvContact;
    @BindView(R.id.edt_description)
    EditText edtDescription;
    @BindView(R.id.sp_state)
    Spinner spState;
    @BindView(R.id.sp_city)
    Spinner spCity;
    @BindView(R.id.sp_facility)
    Spinner spFacility;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hostel_pg);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        imgBack.setVisibility(View.VISIBLE);
        globals = ((Globals) getApplicationContext());
        toolbarTitle.setText(getString(R.string.add_hostel_pg));
        arrProperty = new ArrayList<>();
        arrCategories = new ArrayList<>();
        arrTypeOfProperty = new ArrayList<>();
        arrPropertySizes = new ArrayList<>();
        arrStateList = new ArrayList<>();
        arrCityList = new ArrayList<>();
        arrFacilityList = new ArrayList<>();

        if (Globals.isNetworkAvailable(this)) {
            getSellerDropdownData();
        } else {
            Toaster.shortToast(R.string.no_internet_msg);
            finish();
        }
    }
    /*
TODO :
    Paying Guest Id = 2 Then show property size 1,2,3 BHK
    Facility Id = 15 Then laundry_fees textbox need to show / if not available then null
    Facility Id = 18 Then water_timing textbox need to hide / if not available then null
    Facility Id = 16 Then cooking_menu image need to upload / if not available then null

     */

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

    public void setData() {
        setSpProperty();
        setSpCategory();
        setSpTypeOfProperty();
        setSpSizeOfProperty();
        setSpState();
        setSpFacility();

        setContactAdapter();
        setRoomsAdapter();
    }

    @OnClick(R.id.tv_add_contact)
    public void doAddContact() {
        if (adapterContact != null) {
            adapterContact.doAdd();
        } else {
            setContactAdapter();
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

    public void setContactAdapter() {
        if (adapterContact == null) {
            adapterContact = new AdapterContact(this);
        }
//        adapterContact.doAdd();

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


    @OnClick(R.id.tv_add_image)
    public void doAddImage() {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                arrFile = new ArrayList<>();
                arrFile = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                doAttachment();
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
        //TODO : Add Attachment Changes
//        setAttachment();
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

    public void setSpFacility() {
        SellerDropdownModel.FacilityList facilityList = new SellerDropdownModel.FacilityList();
        facilityList.facility_id = 0;
        facilityList.facility_name = "Select Facility";

        arrFacilityList.add(facilityList);
        arrFacilityList.addAll(sellerDropdownDetail.facilityList);

        adapterFacilityList = new ArrayAdapter<SellerDropdownModel.FacilityList>(getApplicationContext(), R.layout.spinner_item, arrFacilityList) {
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

        spFacility.setAdapter(adapterFacilityList);

        spFacility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                facility_id = arrFacilityList.get(position).facility_id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.img_back)
    public void onImgBackClicked() {
        onBackPressed();
    }

    @OnClick(R.id.btn_add_hostel_pg)
    public void onBtnAddHostelPgClicked() {
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
