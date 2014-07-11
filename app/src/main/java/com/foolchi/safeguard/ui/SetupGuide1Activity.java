package com.foolchi.safeguard.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.foolchi.safeguard.R;
/**
 * Created by foolchi on 6/21/14.
 */
public class SetupGuide1Activity extends Activity implements View.OnClickListener {

    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_guide1);

        next = (Button)findViewById(R.id.bt_guide_next);
        next.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_guide_next:
                Intent intent = new Intent(this, SetupGuide2Activity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                break;

            default:
                break;
        }

    }
}
