package hma.path.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import hma.path.mapcontrol.Location;
import hma.path.R;

public class HomePage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        final boolean demo = true;
        final EditText editText = (EditText)findViewById(R.id.home_search);

        Button next = (Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                if (demo) {
                    final Location location = new  Location("2242 Yorktown Circle", "Mississauga", "ON", "L5M5X9");
                    intent.putExtra("base_loc", location);
                }
                else {
                    String startInuput = editText.getText().toString();
                    intent.putExtra("base_loc", startInuput);
                }
                startActivity(intent);
            }
        });
        Button aboutButton = (Button)findViewById(R.id.helpButton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Help.class);
                startActivity(intent);
            }
        });

    }
}
