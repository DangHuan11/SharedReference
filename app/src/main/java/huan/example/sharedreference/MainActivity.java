package huan.example.sharedreference;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    RadioGroup rgSize;
    RadioButton rbNho, rbVua, rbTo;
    EditText etTong, etTenTep, etDong1, etDong2, etDong3;
    Button btnDoc, btnLuu;
    SharedPreferences sharedPreferences;
    public static final String PREF_NAME = "RefApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ view
        rgSize = findViewById(R.id.rgSize);
        rbNho = findViewById(R.id.rbNho);
        rbVua = findViewById(R.id.rbVua);
        rbTo = findViewById(R.id.rbTo);
        etTong = findViewById(R.id.etTong);
        etTenTep = findViewById(R.id.etTenTep);
        etDong1 = findViewById(R.id.etDong1);
        etDong2 = findViewById(R.id.etDong2);
        etDong3 = findViewById(R.id.etDong3);
        btnDoc = findViewById(R.id.btnDoc);
        btnLuu = findViewById(R.id.btnLuu);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        btnLuu.setOnClickListener(v -> luuDuLieu());
        btnDoc.setOnClickListener(v -> docDuLieu());
    }

    private void luuDuLieu() {
        // Lưu SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int selectedId = rgSize.getCheckedRadioButtonId();
        String kichThuoc = "vua"; // mặc định
        if (selectedId == R.id.rbNho) kichThuoc = "nho";
        else if (selectedId == R.id.rbVua) kichThuoc = "vua";
        else if (selectedId == R.id.rbTo) kichThuoc = "to";

        String tong = etTong.getText().toString().trim();
        String tenTep = etTenTep.getText().toString().trim();

        if (tenTep.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên tệp!", Toast.LENGTH_SHORT).show();
            return;
        }

        editor.putString("kichthuoc", kichThuoc);
        editor.putString("tongso", tong);
        editor.putString("tentep", tenTep);
        editor.apply();

        // Lưu 3 dòng nội dung vào file .txt trong folder "tentep" Documents
        String dong1 = etDong1.getText().toString();
        String dong2 = etDong2.getText().toString();
        String dong3 = etDong3.getText().toString();
        String noiDung = dong1 + "\n" + dong2 + "\n" + dong3;

        try {
            // Thư mục Documents
            File docsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            // Folder tentep (tạo 1 lần nếu chưa có)
            File folder = new File(docsFolder, "tentep");
            if (!folder.exists()) folder.mkdirs();  // Tạo folder tentep nếu chưa tồn tại

            // File lưu trong folder tentep
            File file = new File(folder, tenTep + ".txt");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(noiDung.getBytes());
            fos.close();

            Toast.makeText(this, "Đã lưu file thành công!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Lỗi khi lưu file!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void docDuLieu() {
        // Đọc SharedPreferences để lấy kích thước và tổng
        String kichThuoc = sharedPreferences.getString("kichthuoc", "vua");
        String tongso = sharedPreferences.getString("tongso", "");

        if (kichThuoc.equals("nho")) rbNho.setChecked(true);
        else if (kichThuoc.equals("vua")) rbVua.setChecked(true);
        else rbTo.setChecked(true);

        etTong.setText(tongso);

        // Lấy tên file từ EditText nhập vào
        String tenTep = etTenTep.getText().toString().trim();
        if (tenTep.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên file cần đọc!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Folder tentep trong Documents
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "tentep");

            if (!folder.exists()) {
                Toast.makeText(this, "Folder tentep không tồn tại!", Toast.LENGTH_SHORT).show();
                return;
            }

            // File cần đọc dựa theo tên người dùng nhập
            File file = new File(folder, tenTep + ".txt");
            if (!file.exists()) {
                Toast.makeText(this, "Không tìm thấy file " + tenTep + ".txt!", Toast.LENGTH_SHORT).show();
                return;
            }

            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            String data = new String(buffer);
            String[] lines = data.split("\n");

            if (lines.length > 0) etDong1.setText(lines[0]);
            if (lines.length > 1) etDong2.setText(lines[1]);
            if (lines.length > 2) etDong3.setText(lines[2]);

            Toast.makeText(this, "Đã đọc file " + tenTep + ".txt thành công", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Lỗi khi đọc file!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
