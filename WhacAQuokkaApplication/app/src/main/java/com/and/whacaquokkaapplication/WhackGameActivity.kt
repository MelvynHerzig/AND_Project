package com.and.whacaquokkaapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class WhackGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whack_game)
    }

    /*final ImageView image = (ImageView) view.findViewById(R.id.fav);

    image.setOnClickListener(new View.OnClickListener(){

        @Override
        public void onClick(View v){
            image.setBackgroundResource(R.drawable.ic_fav);

            final Button btn = (Button) v.findViewById(R.id.fav);
            btn.setPressed(true);
        }
    });*/
}