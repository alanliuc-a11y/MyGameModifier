package com.my.modifier;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.my.modifier.engine.MemoryEngine;
import com.my.modifier.service.FloatingService;

public class GameModifierActivity extends Activity {

    private EditText etCurrentValue;
    private EditText etNewValue;
    private Button btnSearch;
    private Button btnModify;
    private Button btnFloat;
    private TextView tvResults;

    private MemoryEngine engine;
    private Handler handler;
    private String gamePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier);

        gamePath = getIntent().getStringExtra("game_path");
        handler = new Handler(Looper.getMainLooper());
        engine = new MemoryEngine();

        initViews();
        checkOverlayPermission();
    }

    private void initViews() {
        etCurrentValue = findViewById(R.id.etCurrentValue);
        etNewValue = findViewById(R.id.etNewValue);
        btnSearch = findViewById(R.id.btnSearch);
        btnModify = findViewById(R.id.btnModify);
        btnFloat = findViewById(R.id.btnFloat);
        tvResults = findViewById(R.id.tvResults);

        btnSearch.setOnClickListener(v -> performSearch());
        btnModify.setOnClickListener(v -> performModify());
        btnFloat.setOnClickListener(v -> startFloatingService());
    }

    private void performSearch() {
        String valueStr = etCurrentValue.getText().toString();
        if (valueStr.isEmpty()) {
            Toast.makeText(this, "请输入当前数值", Toast.LENGTH_SHORT).show();
            return;
        }

        long targetValue = Long.parseLong(valueStr);
        tvResults.setText("搜索中...");
        
        // 模拟搜索（实际需要在虚拟空间或Root环境）
        handler.postDelayed(() -> {
            String result = engine.searchMemory(targetValue);
            tvResults.setText(result);
        }, 1000);
    }

    private void performModify() {
        String newValueStr = etNewValue.getText().toString();
        if (newValueStr.isEmpty()) {
            Toast.makeText(this, "请输入新数值", Toast.LENGTH_SHORT).show();
            return;
        }

        long newValue = Long.parseLong(newValueStr);
        
        // 模拟修改
        boolean success = engine.modifyValue(newValue);
        if (success) {
            Toast.makeText(this, "修改成功！", Toast.LENGTH_SHORT).show();
        } else {
            showNoRootDialog();
        }
    }

    private void startFloatingService() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "需要悬浮窗权限", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, FloatingService.class);
        startService(intent);
        Toast.makeText(this, "悬浮窗已启动", Toast.LENGTH_SHORT).show();
    }

    private void checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this)
                .setTitle("需要悬浮窗权限")
                .setMessage("修改器需要悬浮窗权限才能显示在游戏上方")
                .setPositiveButton("去授权", (d, w) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                })
                .show();
        }
    }

    private void showNoRootDialog() {
        new AlertDialog.Builder(this)
            .setTitle("需要特殊环境")
            .setMessage("本修改器需要以下环境之一：\n\n" +
                "1. Root权限设备\n" +
                "2. 虚拟机/双开环境\n" +
                "3. Xposed框架\n\n" +
                "请使用VMOS Pro + 本修改器实现免Root")
            .setPositiveButton("了解", null)
            .show();
    }
}
