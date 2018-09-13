package abak.tr.com.boxedverticalseekbar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import abak.tr.com.boxedverticalseekbar.weight.Weight;

public class MainActivity extends AppCompatActivity {
    private DoseLevel bv;
    private Circle capsule;
    private Height height;
    private Weight weight;

    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final TextView valueTextView = (TextView) findViewById(R.id.valueTextView);
        final Button semi = (Button) findViewById(R.id.semi);
        semi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,HalfCircleActivtiy.class));
            }
        });

        bv = (DoseLevel) findViewById(R.id.boxed_vertical);
        height = (Height) findViewById(R.id.height);
        capsule = findViewById(R.id.circleCapsule);
        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        height.setValue(10);
        bv.setOnBoxedPointsChangeListener(new DoseLevel.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(DoseLevel boxedPoints, final int points) {
                valueTextView.setText("Current Value is " + String.valueOf(points));
            }

            @Override
            public void onStartTrackingTouch(DoseLevel boxedPoints) {
            }

            @Override
            public void onStopTrackingTouch(DoseLevel boxedPoints) {
            }
        });
        /*height.setOnBoxedPointsChangeListener(new Height.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(Height boxedPoints, final int points) {
                valueTextView.setText("Current Value is " + String.valueOf(points));
            }

            @Override
            public void onStartTrackingTouch(Height boxedPoints) {
            }

            @Override
            public void onStopTrackingTouch(Height boxedPoints) {
            }
        });*/

        bv.setValue(2);
        capsule.setProgressStep(2);

        weight = (Weight) findViewById(R.id.croller);
//        croller.setIndicatorWidth(10);
//        croller.setBackCircleColor(Color.parseColor("#EDEDED"));
//        croller.setMainCircleColor(Color.WHITE);
//        croller.setMax(50);
//        croller.setStartOffset(45);
//        croller.setIsContinuous(false);
//        croller.setLabelColor(Color.BLACK);
//        croller.setProgressPrimaryColor(Color.parseColor("#0B3C49"));
//        croller.setIndicatorColor(Color.parseColor("#0B3C49"));
//        croller.setProgressSecondaryColor(Color.parseColor("#EEEEEE"));
//        croller.setProgressRadius(380);
//        croller.setBackCircleRadius(300);
//        weight.setProgress(90);
        i = 0;
        final Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                try {
                    synchronized (this) {
                        wait(5);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                weight.setProgress(i);
                                if (i >= 90) {
                                    myTimer.cancel();
                                    return;
                                }
                                i++;
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }, 0, 10);
        weight.setOnWeightChangeListener(new Weight.onProgressChangedListener() {
            @Override
            public void onProgressChanged(Weight weight, int progress) {

                Log.e("Proces ", "--> " + progress);
            }

            @Override
            public void onStartTrackingTouch(Weight weight) {
//                Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(Weight weight) {
//                Toast.makeText(MainActivity.this, "Stop", Toast.LENGTH_SHORT).show();
            }
        });

    }

   /* public void setBorder(View v) {
        int val = Integer.valueOf(v.getTag().toString());
        bv.setCornerRadius(val);
        Toast.makeText(MainActivity.this, "New corner radius is " + String.valueOf(val), Toast.LENGTH_SHORT).show();
    }*/

    public void setMax(View v) {
        int val = Integer.valueOf(v.getTag().toString());
        bv.setMax(val);
        Toast.makeText(MainActivity.this, "New max value is " + String.valueOf(val), Toast.LENGTH_SHORT).show();
    }

}
