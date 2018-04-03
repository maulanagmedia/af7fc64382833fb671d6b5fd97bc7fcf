package gmedia.net.id.semargres2018;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;
import com.maulana.custommodul.OptionItem;
import com.maulana.custommodul.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gmedia.net.id.semargres2018.Utils.ServerURL;

public class ProfileActivity extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private ImageView ivNoKTP, ivNama, ivTempatLahir, ivTanggalLahir, ivAlamat, ivEmail, ivTelepon;
    private EditText edtNoKTP, edtNama, edtTempatLahir, edtTanggalLahir, edtAlamat, edtEmail, edtTelepon;
    private Spinner spJenisKelamin, spAgama, spStatusPernikahan, spPekerjaan;
    private Button btnCancel, btnSkip, btnSimpan;
    private String dateString = "";
    private List<OptionItem> listJenisKelamin, listAgama, listStatusNikah, listPekerjaan;
    private ProgressBar pbProcess;
    private String selectedJenisKelamin = "", selectedAgama = "", selectedStatusNikah = "", selectedPekerjaan = "";
    private ProgressDialog progressDialog;
    private final String TAG = "PROFILE";
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        setTitle("Profile Pengguna");

        context = this;
        session = new SessionManager(this);
        initUI();
    }

    private void initUI() {

        ivNoKTP = (ImageView) findViewById(R.id.iv_no_ktp);
        ivNama = (ImageView) findViewById(R.id.iv_nama);
        ivTempatLahir = (ImageView) findViewById(R.id.iv_tempat_lahir);
        ivTanggalLahir = (ImageView) findViewById(R.id.iv_tanggal_lahir);
        ivAlamat = (ImageView) findViewById(R.id.iv_alamat);
        ivEmail = (ImageView) findViewById(R.id.iv_email);
        ivTelepon = (ImageView) findViewById(R.id.iv_no_telp);

        edtNoKTP = (EditText) findViewById(R.id.edt_no_ktp);
        edtNama = (EditText) findViewById(R.id.edt_nama);
        edtTempatLahir = (EditText) findViewById(R.id.edt_tempat_lahir);
        edtTanggalLahir = (EditText) findViewById(R.id.edt_tanggal_lahir);
        edtAlamat = (EditText) findViewById(R.id.edt_alamat);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtTelepon = (EditText) findViewById(R.id.edt_no_telp);

        spJenisKelamin = (Spinner) findViewById(R.id.sp_jenis_kelamin);
        spAgama = (Spinner) findViewById(R.id.sp_agama);
        spStatusPernikahan = (Spinner) findViewById(R.id.sp_status_nikah);
        spPekerjaan = (Spinner) findViewById(R.id.sp_pekerjaan);

        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnSimpan = (Button) findViewById(R.id.btn_simpan);

        pbProcess = (ProgressBar) findViewById(R.id.pb_process);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            isEdit = bundle.getBoolean("is_edit", false);
        }

        initEvent();

        initData();
    }

    private void initEvent() {

        edtNoKTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() == 0){
                    ivNoKTP.setImageResource(R.mipmap.ic_cb_uncheck);
                }else{
                    ivNoKTP.setImageResource(R.mipmap.ic_cb_active);
                }
            }
        });

        edtNama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() == 0){
                    ivNama.setImageResource(R.mipmap.ic_cb_uncheck);
                }else{
                    ivNama.setImageResource(R.mipmap.ic_cb_active);
                }
            }
        });

        edtTanggalLahir.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() == 0){
                    ivTanggalLahir.setImageResource(R.mipmap.ic_cb_uncheck);
                }else{
                    ivTanggalLahir.setImageResource(R.mipmap.ic_cb_active);
                }
            }
        });

        edtTempatLahir.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() == 0){
                    ivTempatLahir.setImageResource(R.mipmap.ic_cb_uncheck);
                }else{
                    ivTempatLahir.setImageResource(R.mipmap.ic_cb_active);
                }
            }
        });

        edtAlamat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() == 0){
                    ivAlamat.setImageResource(R.mipmap.ic_cb_uncheck);
                }else{
                    ivAlamat.setImageResource(R.mipmap.ic_cb_active);
                }
            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() == 0){
                    ivEmail.setImageResource(R.mipmap.ic_cb_uncheck);
                }else{
                    ivEmail.setImageResource(R.mipmap.ic_cb_active);
                }
            }
        });

        edtTelepon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() == 0){
                    ivTelepon.setImageResource(R.mipmap.ic_cb_uncheck);
                }else{
                    ivTelepon.setImageResource(R.mipmap.ic_cb_active);
                }
            }
        });

        // Edit Tanggal

        dateString = iv.getCurrentDate(FormatItem.formatDateDisplay) ;
        //edtTanggalLahir.setText();

        edtTanggalLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar customDate;
                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);

                Date dateValue = null;

                try {
                    dateValue = sdf.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                customDate = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        customDate.set(Calendar.YEAR,year);
                        customDate.set(Calendar.MONTH,month);
                        customDate.set(Calendar.DATE,date);

                        SimpleDateFormat sdFormat = new SimpleDateFormat(FormatItem.formatDateDisplay, Locale.US);
                        dateString = sdFormat.format(customDate.getTime());
                        edtTanggalLahir.setText(dateString);

                        if(edtTanggalLahir.getText().toString().length() == 0){
                            ivTanggalLahir.setImageResource(R.mipmap.ic_cb_uncheck);
                        }else{
                            ivTanggalLahir.setImageResource(R.mipmap.ic_cb_active);
                        }

                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(ProfileActivity.this ,date , iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateBerforeSaving();
            }
        });
    }

    private void validateBerforeSaving() {

        if(edtNoKTP.getText().toString().length() == 0){

            edtNoKTP.setError("Nomor KTP harap diisi");
            edtNoKTP.requestFocus();
            return;
        }else{
            edtNoKTP.setError(null);
        }

        if(edtNama.getText().toString().length() == 0){

            edtNama.setError("Nama harap diisi");
            edtNama.requestFocus();
            return;
        }else{
            edtNama.setError(null);
        }

        if(edtTempatLahir.getText().toString().length() == 0){

            edtTempatLahir.setError("Tempat lahir harap diisi");
            edtTempatLahir.requestFocus();
            return;
        }else{
            edtTempatLahir.setError(null);
        }

        if(edtTanggalLahir.getText().toString().length() == 0){

            edtTanggalLahir.setError("Tanggal lahir harap diisi");
            edtTanggalLahir.requestFocus();
            return;
        }else{
            edtTanggalLahir.setError(null);
        }

        if(edtAlamat.getText().toString().length() == 0){

            edtAlamat.setError("ALamat harap diisi");
            edtAlamat.requestFocus();
            return;
        }else{
            edtAlamat.setError(null);
        }

        if(edtEmail.getText().toString().length() == 0){

            edtEmail.setError("Email harap diisi");
            edtEmail.requestFocus();
            return;
        }else{
            edtEmail.setError(null);
        }

        if(edtTelepon.getText().toString().length() == 0){

            edtTelepon.setError("Nomor telepon harap diisi");
            edtTelepon.requestFocus();
            return;
        }else{
            edtTelepon.setError(null);
        }

        if(selectedJenisKelamin.length() == 0){

            Toast.makeText(context, "Harap pilih jenis kelamin terlebih dahulu", Toast.LENGTH_LONG).show();
            return;
        }

        if(selectedAgama.length() == 0){

            Toast.makeText(context, "Harap pilih agama terlebih dahulu", Toast.LENGTH_LONG).show();
            return;
        }

        if(selectedStatusNikah.length() == 0){

            Toast.makeText(context, "Harap pilih status pernikahan terlebih dahulu", Toast.LENGTH_LONG).show();
            return;
        }

        if(selectedPekerjaan.length() == 0){

            Toast.makeText(context, "Harap pilih pekerjaan terlebih dahulu", Toast.LENGTH_LONG).show();
            return;
        }

        if(pbProcess.getVisibility() == View.VISIBLE){

            Toast.makeText(context, "Harap tunggu hingga proses pengambilan data selesai", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Konfirmasi")
                .setMessage("Anda yakin ingin menyimpan data?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveData();

                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void saveData() {

        JSONObject jBody = new JSONObject();
        showProgressDialog();

        try {

            jBody.put("email", edtEmail.getText().toString());
            jBody.put("profile_name", edtNama.getText().toString());
            jBody.put("tgl_lahir", iv.ChangeFormatDateString(edtTanggalLahir.getText().toString(), FormatItem.formatDateDisplay, FormatItem.formatDate));
            jBody.put("no_telp", edtTelepon.getText().toString());
            jBody.put("no_ktp", edtNoKTP.getText().toString());
            jBody.put("alamat", edtAlamat.getText().toString());
            jBody.put("jenis_kelamin", selectedJenisKelamin);
            //jBody.put("agama", selectedAgama);
            jBody.put("tempat_lahir", edtTempatLahir.getText().toString());
            //jBody.put("status_nikah", selectedStatusNikah);
            //jBody.put("pekerjaan", selectedPekerjaan);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.saveProfile, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();

                try {
                    JSONObject responseAPI = new JSONObject(result);

                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullDouble(status) == 200){

                        String statusResponse = responseAPI.getJSONObject("response").getString("status");
                        String message = responseAPI.getJSONObject("response").getString("message");

                        if(statusResponse.equals("1")){

                            session.createLoginSession(session.getUid(),edtEmail.getText().toString(),edtNama.getText().toString(), session.getUserInfo(SessionManager.TAG_PICTURE), session.getUserInfo(SessionManager.TAG_JENIS), edtNoKTP.getText().toString(), "1");
                            Toast.makeText(context, message,Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(context, MainActivity.class);
                            finish();
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    }

                } catch (JSONException e) {
                    Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                dismissProgressDialog();
                Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initData() {

        // Get Data Jenis Kelamin
        pbProcess.setVisibility(View.VISIBLE);
        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getGender, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbProcess.setVisibility(View.GONE);

                try {

                    JSONObject responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    listJenisKelamin = new ArrayList<>();

                    if(iv.parseNullDouble(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listJenisKelamin.add(new OptionItem(jo.getString("id"), jo.getString("jenis_kelamin")));
                        }

                        spJenisKelamin.setAdapter(null);

                        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.layout_simple_list, listJenisKelamin);
                        spJenisKelamin.setAdapter(adapter);

                        spJenisKelamin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                if(spJenisKelamin.getAdapter() != null){

                                    //((TextView) spJenisKelamin.getSelectedView()).setTextColor(getResources().getColor(R.color.color_white));
                                    selectedJenisKelamin = (listJenisKelamin.get(position)).getValue();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        spJenisKelamin.setSelection(0, true);

                        getAgama();

                    }else{

                        showErrorDialog();
                    }

                } catch (JSONException e) {

                    showErrorDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                pbProcess.setVisibility(View.GONE);
                showErrorDialog();
            }
        });

        if(!isEdit){
            btnSkip.setVisibility(View.VISIBLE);

            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(context, "Silahkan lengkapi data profile untuk mendapatkan e-kupon Semargres", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, MainActivity.class);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        }
    }

    private void getAgama() {

        // Get Data Jenis Kelamin
        pbProcess.setVisibility(View.VISIBLE);
        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getAgama, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbProcess.setVisibility(View.GONE);

                try {

                    JSONObject responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    listAgama = new ArrayList<>();

                    if(iv.parseNullDouble(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listAgama.add(new OptionItem(jo.getString("id"), jo.getString("agama")));
                        }

                        spAgama.setAdapter(null);

                        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.layout_simple_list, listAgama);
                        spAgama.setAdapter(adapter);

                        spAgama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                if(spAgama.getAdapter() != null){

                                    //((TextView) spJenisKelamin.getSelectedView()).setTextColor(getResources().getColor(R.color.color_white));
                                    selectedAgama = (listAgama.get(position)).getValue();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        spAgama.setSelection(0, true);
                        getStatusNikah();

                    }else{

                        showErrorDialog();
                    }

                } catch (JSONException e) {

                    showErrorDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                pbProcess.setVisibility(View.GONE);
                showErrorDialog();
            }
        });
    }

    private void getStatusNikah() {

        // Get Data Status Pernikahan
        pbProcess.setVisibility(View.VISIBLE);
        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getMarriage, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbProcess.setVisibility(View.GONE);

                try {

                    JSONObject responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    listStatusNikah = new ArrayList<>();

                    if(iv.parseNullDouble(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listStatusNikah.add(new OptionItem(jo.getString("id"), jo.getString("marriage")));
                        }

                        spStatusPernikahan.setAdapter(null);

                        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.layout_simple_list, listStatusNikah);
                        spStatusPernikahan.setAdapter(adapter);

                        spStatusPernikahan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                if(spStatusPernikahan.getAdapter() != null){

                                    //((TextView) spJenisKelamin.getSelectedView()).setTextColor(getResources().getColor(R.color.color_white));
                                    selectedStatusNikah = (listStatusNikah.get(position)).getValue();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        spStatusPernikahan.setSelection(0, true);
                        getPekerjaan();

                    }else{

                        showErrorDialog();
                    }

                } catch (JSONException e) {

                    showErrorDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                pbProcess.setVisibility(View.GONE);
                showErrorDialog();
            }
        });
    }

    private void getPekerjaan() {

        // Get Data Pekerjaan
        pbProcess.setVisibility(View.VISIBLE);
        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getPekerjaan, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbProcess.setVisibility(View.GONE);

                try {

                    JSONObject responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");
                    listPekerjaan = new ArrayList<>();

                    if(iv.parseNullDouble(status) == 200){

                        JSONArray jsonArray = responseAPI.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length();i++){

                            JSONObject jo = jsonArray.getJSONObject(i);
                            listPekerjaan.add(new OptionItem(jo.getString("id"), jo.getString("pekerjaan")));
                        }

                        spPekerjaan.setAdapter(null);

                        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.layout_simple_list, listPekerjaan);
                        spPekerjaan.setAdapter(adapter);

                        spPekerjaan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                if(spPekerjaan.getAdapter() != null){

                                    //((TextView) spJenisKelamin.getSelectedView()).setTextColor(getResources().getColor(R.color.color_white));
                                    selectedPekerjaan = (listPekerjaan.get(position)).getValue();

                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        spPekerjaan.setSelection(0, true);

                        getProfile();

                    }else{

                        showErrorDialog();
                    }

                } catch (JSONException e) {

                    showErrorDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                pbProcess.setVisibility(View.GONE);
                showErrorDialog();
            }
        });
    }

    private void getProfile() {

        // Get Profile
        pbProcess.setVisibility(View.VISIBLE);
        ApiVolley request = new ApiVolley(context, new JSONObject(), "GET", ServerURL.getProfile, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                pbProcess.setVisibility(View.GONE);

                try {

                    JSONObject responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if(iv.parseNullDouble(status) == 200){

                        JSONObject jo = responseAPI.getJSONObject("response");

                        edtNoKTP.setText(jo.getString("no_ktp"));
                        if(jo.getString("profile_name").length() == 0){
                           edtNama.setText(session.getNama());
                        }else{
                            edtNama.setText(jo.getString("profile_name"));
                        }

                        edtEmail.setText(session.getEmail().length() > 0 ? session.getEmail() : jo.getString("email"));
                        edtTempatLahir.setText(jo.getString("tempat_lahir"));
                        edtTanggalLahir.setText(iv.ChangeFormatDateString(jo.getString("tgl_lahir"), FormatItem.formatDate, FormatItem.formatDateDisplay));
                        edtAlamat.setText(jo.getString("alamat"));
                        edtTelepon.setText(jo.getString("no_telp"));

                        String selectedJenisKelaminJ = jo.getString("id_gender");
                        String selectedAgamaJ = jo.getString("id_agama");
                        String selectedStatusNikahJ = jo.getString("id_marriage");
                        String selectedPekerjaanJ = jo.getString("id_pekerjaan");

                        // Jenis Kelamin
                        int position = 0;
                        int x = 0;
                        for(OptionItem item: listJenisKelamin){
                            if(item.getValue().equals(selectedJenisKelaminJ)) {
                                position = x;
                                break;
                            }
                            x++;
                        }
                        spJenisKelamin.setSelection(position, true);

                        // Agama
                        position = 0;
                        x = 0;
                        for(OptionItem item: listAgama){
                            if(item.getValue().equals(selectedAgamaJ)) {
                                position = x;
                                break;
                            }
                            x++;
                        }
                        spAgama.setSelection(position, true);

                        // status marital
                        position = 0;
                        x = 0;
                        for(OptionItem item: listStatusNikah){
                            if(item.getValue().equals(selectedStatusNikahJ)) {
                                position = x;
                                break;
                            }
                            x++;
                        }
                        spStatusPernikahan.setSelection(position, true);

                        // Pekerjaan
                        position = 0;
                        x = 0;
                        for(OptionItem item: listPekerjaan){
                            if(item.getValue().equals(selectedPekerjaanJ)) {
                                position = x;
                                break;
                            }
                            x++;
                        }
                        spPekerjaan.setSelection(position, true);

                    }else{
                        //showErrorDialog();
                    }

                } catch (JSONException e) {

                    showErrorDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {

                pbProcess.setVisibility(View.GONE);
                showErrorDialog();
            }
        });
    }

    private void showLogoutDialog() {

        AlertDialog builder = new AlertDialog.Builder(ProfileActivity.this)
                .setIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                .setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin logout?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        logOut();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                showLogoutDialog();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(isEdit){

            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        }else{

            logOut();
        }

        /*super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);*/
    }

    private void logOut(){

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.putExtra("logout", true);
        session.logoutUser(intent);
    }

    private void showErrorDialog(){
        Toast.makeText(context, "Terjadi kesalahan saat mengakses data, harap ulangi kembali", Toast.LENGTH_LONG).show();
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyimpan...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
