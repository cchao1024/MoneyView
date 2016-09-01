package com.github.moneyview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.cchao.MoneyView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cchao on 2016/8/31.
 * E-mail:   cchao1024@163.com
 * Description:
 */
public class SampleActivity extends AppCompatActivity {
        MoneyView mMoneyView3;

        @Override
        protected void onCreate ( @Nullable Bundle savedInstanceState ) {
                super.onCreate ( savedInstanceState );
                setContentView ( R.layout.activity_sample );
                mMoneyView3 = ( MoneyView ) findViewById ( R.id.money_view_3 );
                random ( );
        }

        private void random ( ) {
                Timer timer = new Timer ( );
                TimerTask timerTask = new TimerTask ( ) {
                        @Override
                        public void run ( ) {
                                runOnUiThread ( new Runnable ( ) {
                                        @Override
                                        public void run ( ) {
                                                mMoneyView3.setMoneyText ( new Random ( ).nextFloat ( ) * 10000 + "" );
                                        }
                                } );
                        }
                };
                timer.schedule ( timerTask, 100, 2000 );
        }
}
