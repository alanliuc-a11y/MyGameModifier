package com.my.modifier;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_INSTALL_PERMISSION = 101;

    private Button btnSelectGame;
    private Button btnStartModifier;
    private TextView tvStatus;

    private String selectedGamePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        checkPermissions();
    }

    private void initViews() {
        btnSelectGame = findViewById(R.id.btnSelectGame);
        btnStartModifier = findViewById(R.id.btnStartModifier);
        tvStatus = findViewById(R.id.tvStatus);

        btnSelectGame.setOnClickListener(v -> showGameSelectionDialog());
        btnStartModifier.setOnClickListener(v -> startModifier());

        btnStartModifier.setEnabled(false);
    }

    private void showGameSelectionDialog() {
        // 简化为直接使用本地游戏APK
        new AlertDialog.Builder(this)
            .setTitle("选择游戏")
            .setMessage("请将游戏APK放在 /Download/GameModifier/ 目录下")
            .setPositiveButton("知道了", (dialog, which) -> {
                // 打开文件选择器
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/vnd.android.package-archive");
                startActivityForResult(intent, REQUEST_INSTALL_PERMISSION);
            })
            .show();
    }

    private void startModifier() {
        if (selectedGamePath == null) {
            Toast.makeText(this, "请先选择游戏", Toast.LENGTH_SHORT).show();
            return;
        }

        // 启动修改器界面
        Intent intent = new Intent(this, GameModifierActivity.class);
        intent.putExtra("game_path", selectedGamePath);
        startActivity(intent);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ 需要特殊权限
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        } else {
            // Android 10 及以下
            String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) 
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
                }
            }
        }

        // 检查悬浮窗权限
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_INSTALL_PERMISSION && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    selectedGamePath = uri.toString();
                    tvStatus.setText("已选择: " + uri.getLastPathSegment());
                    btnStartModifier.setEnabled(true);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "需要存储权限", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
