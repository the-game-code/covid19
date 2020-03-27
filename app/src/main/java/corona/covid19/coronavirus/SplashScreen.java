package corona.covid19.coronavirus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends Activity {

    private final static int FADE_DURATION = 1000;
    TextView splash_text, center_text;

    ImageView app_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        app_img = (ImageView) findViewById(R.id.app_img);
        center_text = (TextView) findViewById(R.id.center_text);
        splash_text = (TextView) findViewById(R.id.splash_text);

        setFadeAnimation(app_img);
        setFadeAnimation(center_text);

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void setFadeAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

}