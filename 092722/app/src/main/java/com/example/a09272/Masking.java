package com.example.a09272;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;


public class Masking extends Service {
//앱이 종료된 후에도 항상 마스킹 기능은 켜져 있어야 해서 sevice를 상속받았다.
//마스킹디자인 정해야 함
    //이거 더 나은 코드 있는지 확인하기
    //다른 그림 위에 그리기 맨 위에 그리기
    String rect;
    private View onTopView;//항상 보이게 할 뷰
    private WindowManager manager;//스크린에 직접 뷰를 띄우기 위해 Windoe Manager사용

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /*  Service 객체와 (화면단 Activity 사이에서)통신(데이터를 주고받을) 때 사용하는 메서드 우선, 데이터를 전달 받을 필요가 없다고 써 놓았다. return null;*/
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String strId = getString(R.string.noti_channel_id);
            final String strTitle = getString(R.string.app_name);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = notificationManager.getNotificationChannel(strId);
            if (channel == null) {
                channel = new NotificationChannel(strId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            Notification notification = new NotificationCompat.Builder(this, strId)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("제목")
                    .setContentText("내용")
                    .build();
            startForeground(1, notification);
        }else{
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "10001";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon)) //BitMap 이미지 요구
                    .setContentTitle("상태바 드래그시 보이는 타이틀")
                    .setContentText("상태바 드래그시 보이는 서브타이틀")
                    // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                    //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)// 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                    .setAutoCancel(true);

      builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

            assert notificationManager != null;
            notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        onTopView = inflater.inflate(R.layout.masking, null);


        //윈도우 레이아웃 파라미터 생성 및 설정 넣기 위한 설정
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                500,
                500,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                // Android O 이상인 경우 TYPE_APPLICATION_OVERLAY 로 설정
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|//FLAG_NOT_TOUCH_MODAL 값을 설정한다. 해당 플래그는 윈도우 영역 밖의 다른 윈도우도 이벤트를 받을 수 있도록 허용하는 플래그
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH|//앱에 외부의 모든 터치를 감시하도록 지시합니다.
                        WindowManager.LayoutParams.FLAG_SECURE,//캡처방지코드
                PixelFormat.TRANSLUCENT);//투명배경

        params.gravity = Gravity.LEFT | Gravity.BOTTOM;//창의 위치 왼쪽 하단으로

        manager = (WindowManager) getSystemService(WINDOW_SERVICE);//윈도우 매니저
        manager.addView(onTopView, params);//윈도우에 뷰를 추가 하였다.

        startForeground(1, new Notification());

    }

    //서비스 종료시 뷰 제거
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (onTopView != null) {
            manager.removeView(onTopView);
            onTopView = null;
        }
    }
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // 여기서 처리 한다. 시스템 특성상 아래 특징 확인
//        if(intent == null){
//            return Service.START_STICKY;
//            // 끈적끈적하게 계쏙 서비스가 종료되더라도 자동으로 실행되도록한다.
//        }else{          // Null이 아닐경우
//            processCommand(intent); // 메소드를 분리하는게 보기가 좋다. 여기서 처리한다.
//        }
//
//        return super.onStartCommand(intent, flags, startId);
//
//    }
//    private void processCommand(Intent intent){
//        // 전달 받은 데이터 찍기 위함.
//        rect = intent.getStringExtra("rect"); // command 는 구분 하기 위한것
//
//    }

}