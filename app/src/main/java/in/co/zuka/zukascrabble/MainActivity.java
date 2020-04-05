package in.co.zuka.zukascrabble;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent quickGameIntent = new Intent(this, BoardActivity.class);
        startActivity(quickGameIntent);
    }


}
