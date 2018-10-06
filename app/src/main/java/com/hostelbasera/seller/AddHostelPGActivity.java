package com.hostelbasera.seller;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.apis.PostWithRequestParam;
import com.hostelbasera.model.AddImageAttachmentModel;
import com.hostelbasera.model.AddRoomModel;
import com.hostelbasera.model.FileUploadModel;
import com.hostelbasera.model.SellerDropdownModel;
import com.hostelbasera.model.UserDetailModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;

import static com.hostelbasera.utility.Globals.checkFileSize;

public class AddHostelPGActivity extends BaseActivity implements PermissionListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hostel_pg);
        ButterKnife.bind(this);
        checkPermission();
//        init();
    }

    public void checkPermission() {
        new TedPermission(this)
                .setPermissionListener(this)
                .setRationaleMessage(R.string.rationale_message)
                .setDeniedMessage(R.string.denied_message)
                .setGotoSettingButtonText(R.string.ok)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    @Override
    public void onPermissionGranted() {
        init();
    }

    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//        init();
        finish();
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
        arrAddImageAttachment = new ArrayList<>();
        arrAddMenuAttachment = new ArrayList<>();
        arrContact = new ArrayList<>();

        property_id = getIntent().getIntExtra(Constant.Property_id, 0);

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

    public void setData() {
        setSpProperty();
        setSpCategory();
        setSpTypeOfProperty();
        setSpSizeOfProperty();
        setSpState();
        arrFacilityList.addAll(sellerDropdownDetail.facilityList);

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
                    setSpTypeOfProperty();
                    setSpSizeOfProperty();
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
                }
                if (arrFacilityList.get(i).facility_id == 16) {
                    cvCookingMenu.setVisibility(View.VISIBLE);
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

    // TODO : Set longitude & latitude

    public void doAddHostelPG() {
        arrRoomDetails = new ArrayList<>();
        if (adapterRoom != null) {
            arrRoomDetails = adapterRoom.mValues;
        }

        if (adapterContact != null) {
            arrContact = adapterContact.mValues;
        }

        StringBuilder contact_no = new StringBuilder();
        for (int i = 0; i < arrContact.size(); i++) {
            contact_no.append(arrContact.get(i)).append(", ");
        }
/*
getAddPropertyParam(int property_id, int type_id, String property_name, int property_category_id,
                                          int property_size_id, String email, String address, double longitude,
                                          double latitude, String cont_no, String description, int state_id, int city_id,
                                          String timing, String water_timing, String laundry_fees, ArrayList<AddImageAttachmentModel> arrAddMenuAttachment,
                                          String price, ArrayList<SellerDropdownModel.FacilityList> arrFacilityList,
                                          ArrayList<AddImageAttachmentModel> arrAddImageAttachment, ArrayList<AddRoomModel> arrRoomDetails)
 */
        JSONObject postData = HttpRequestHandler.getInstance().getAddPropertyParam(property_id, type_id, edtName.getText().toString(),
                property_category_id,property_size_id,edtEmail.getText().toString(), edtAddress.getText().toString(), longitude, latitude,
                contact_no.deleteCharAt(contact_no.length() - 2).toString(), edtDescription.getText().toString(), state_id ,city_id,edtOpenHours.getText().toString(),
                edtWaterTimings.getText().toString(),edtLaundryFees.getText().toString(),arrAddMenuAttachment,edtPrice.getText().toString(),arrFacilityList,arrAddImageAttachment,arrRoomDetails);

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

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
