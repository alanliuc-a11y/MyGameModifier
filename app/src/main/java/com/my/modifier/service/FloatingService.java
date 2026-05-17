package com.my.modifier.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.my.modifier.R;
import com.my.modifier.engine.MemoryEngine;

public class FloatingService extends Service {
    
    private WindowManager windowManager;
    private View floatView;
    private View panelView;
    
    private boolean isPanelOpen = false;
    private MemoryEngine engine;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        engine = new MemoryEngine();
        
        createNotification();
        initFloatingWindow();
    }

    private void createNotification() {
        NotificationChannel channel = new NotificationChannel(
            "modifier", "修改器", NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        
        Notification notification = new NotificationCompat.Builder(this, "modifier")
            .setContentTitle("游戏修改器运行中")
            .setContentText("点击悬浮球使用")
            .setSmallIcon(android.R.drawable.ic_menu_edit)
            .build();
        
        startForeground(1, notification);
    }

    private void initFloatingWindow() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        // 悬浮球
        floatView = LayoutInflater.from(this).inflate(R.layout.floating_ball, null);
        
        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O 
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY 
            : WindowManager.LayoutParams.TYPE_PHONE;
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            100, 100, type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 100;
        params.y = 200;
        
        // 点击展开面板
        floatView.findViewById(R.id.ball).setOnClickListener(v -> togglePanel());
        
        // 拖拽
        floatView.setOnTouchListener(new View.OnTouchListener() {
            private int x, y;
            private float touchX, touchY;
            
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = params.x;
                        y = params.y;
                        touchX = e.getRawX();
                        touchY = e.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = x + (int)(e.getRawX() - touchX);
                        params.y = y + (int)(e.getRawY() - touchY);
                        windowManager.updateViewLayout(floatView, params);
                        return true;
                }
                return false;
            }
        });
        
        windowManager.addView(floatView, params);
    }

    private void togglePanel() {
        if (isPanelOpen) {
            if (panelView != null) {
                windowManager.removeView(panelView);
                panelView = null;
            }
        } else {
            showPanel();
        }
        isPanelOpen = !isPanelOpen;
    }

    private void showPanel() {
        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O 
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY 
            : WindowManager.LayoutParams.TYPE_PHONE;
        
        panelView = LayoutInflater.from(this).inflate(R.layout.floating_panel, null);
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            400, 600, type,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER;
        
        setupPanelViews(panelView);
        windowManager.addView(panelView, params);
    }

    private void setupPanelViews(View view) {
        EditText etSearch = view.findViewById(R.id.etSearch);
        EditText etModify = view.findViewById(R.id.etModify);
        TextView tvResult = view.findViewById(R.id.tvResult);
        Button btnSearch = view.findViewById(R.id.btnSearch);
        Button btnModify = view.findViewById(R.id.btnModify);
        Button btnClose = view.findViewById(R.id.btnClose);
        
        btnSearch.setOnClickListener(v -> {
            String val = etSearch.getText().toString();
            if (!val.isEmpty()) {
                tvResult.setText("搜索中...");
                handler.postDelayed(() -> {
                    String result = engine.searchMemory(Long.parseLong(val));
                    tvResult.setText(result);
                }, 500);
            }
        });
        
        btnModify.setOnClickListener(v -> {
            String val = etModify.getText().toString();
            if (!val.isEmpty()) {
                boolean success = engine.modifyValue(Long.parseLong(val));
                Toast.makeText(this, success ? "修改成功" : "修改失败", 
                    Toast.LENGTH_SHORT).show();
            }
        });
        
        btnClose.setOnClickListener(v -> togglePanel());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatView != null) windowManager.removeView(floatView);
        if (panelView != null) windowManager.removeView(panelView);
    }
}
