package aja.nwhacks2016;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import aja.rhythm.Player;

public class MainActivity extends ActionBarActivity {
    private Player player = Player.getPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAllKeyboardListeners();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAllKeyboardListeners(){
        int[] buttonids = {R.id.imageButton,R.id.imageButton2,R.id.imageButton3,
            R.id.imageButton4,R.id.imageButton5,R.id.imageButton6,
            R.id.imageButton7,R.id.imageButton8,R.id.imageButton9,
            R.id.imageButton10,R.id.imageButton11,R.id.imageButton12};

        for (int row=0;row<3;row++){
            for (int col=0;col<4;col++){
                int subdivision;
                if (row<2){
                    subdivision = 4;
                    setKeyboardButtonListener(buttonids[row*4+col],subdivision,row*4+col);
                }
                else if (col!=0){
                    subdivision = 3;
                    setKeyboardButtonListener(buttonids[row*4+col],subdivision,col-1);
                }
                else{
                    final ImageButton toggleButton = (ImageButton)findViewById(buttonids[row*4+col]);
                    toggleButton.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            //System.out.println("Toggle Tie");
                            if (player.toggleTie()){
                                toggleButton.setAlpha(0.5f);
                            }
                            else{
                                toggleButton.setAlpha(1f);
                            }

                        }
                    });
                }
            }
        }
    }

    private void setKeyboardButtonListener(int buttonId,final int subdivision,final int beatpattern){
        ImageButton button = (ImageButton)findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println(beatpattern);
                player.addBeat(beatpattern, subdivision);
            }
        });
    }
}
