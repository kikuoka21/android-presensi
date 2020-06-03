package tech.opsign.kkp.absensi;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;

public class test_layout extends AppCompatActivity {
    private test_layout activity;

    private PieChart chart;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coba_piechart);

        this.activity = this;

    }
}
