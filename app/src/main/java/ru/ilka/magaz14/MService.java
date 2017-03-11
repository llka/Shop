package ru.ilka.magaz14;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MService extends Service {

    ExecutorService es;
    int dol_rub = 20000, euro_rub = 27000;
    boolean f;

    public void onCreate() {
        super.onCreate();
        es = Executors.newFixedThreadPool(1);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        f = intent.getBooleanExtra(MainActivity.PARAM_FLAG, true);
        PendingIntent pi = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);

        MyRun mr = new MyRun(startId, pi);
        es.execute(mr);

        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    class MyRun implements Runnable {
        int startId;
        PendingIntent pi;

        public MyRun(int startId, PendingIntent pi) {
            this.startId = startId;
            this.pi = pi;
        }

        public void run() {
            try {

                Intent intent = new Intent().putExtra(MainActivity.PARAM_RESULT, dol_rub);
                intent.putExtra(MainActivity.PARAM_EURO,euro_rub);

                pi.send(MService.this, MainActivity.STATUS_FINISH, intent);
                if(f == true)
                    someTask();

            }
            catch (CanceledException e) {
                e.printStackTrace();
            }
        }

        void someTask() {
            new Thread(new Runnable() {
                public void run() {
                    for (int i = 1; i<=100; i++) {
                        dol_rub++;
                        euro_rub+=2;
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    stopSelf();
                }
            }).start();
        }

    }
}
