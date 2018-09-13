package abak.tr.com.boxedverticalseekbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.reverseOrder;

public class HalfCircleActivtiy extends AppCompatActivity {

    private SemiCircle semiCircle;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_half_circle);
        semiCircle = (SemiCircle) findViewById(R.id.semi_circle);
//        semiCircle.setProgress(150, entriesSortedByValues(map_1));
        i = 0;
       /* final Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                try {
                    synchronized (this) {
                        wait(5);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                semiCircle.setProgress(i, Color.parseColor("#66cc33"));
//                                semiCircle.setProgress(90, Color.parseColor("#66cc33"));
//                                semiCircle.setProgress(45, Color.parseColor("#ff0000"));
                                if (i >= 100) {
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

        }, 0, 10);*/

        HashMap<String, Integer> map_1 = new HashMap<>();

        map_1.put("#ff0000", 180);
        map_1.put("#66cc33", 133 + 80);
//        map_1.put("#000000", 133 + 80 + 9);


        List<Map.Entry<String, Integer>> entries = entriesDescByValues(map_1);
        Log.e("MAP", "" + entries);
        semiCircle.setProgress(entries);

    }

    static <K, V extends Comparable<? super V>> List<Map.Entry<String, Integer>> entriesDescByValues(Map<String, Integer> map) {

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());

        Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                return e2.getValue().compareTo(e1.getValue());
            }
        });

        return sortedEntries;
    }
}
