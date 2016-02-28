package aja.nwhacks2016;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import aja.rhythm.Player;

public class MainActivity extends ActionBarActivity {
    private Player player = Player.getPlayer();
    private boolean isTieOn = false;
    private MainActivity self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAllKeyboardListeners();

        ToggleButton playbutton = (ToggleButton) findViewById(R.id.playToggleButton);
        playbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean istoggled) {
                if (istoggled) {
                    //System.out.println("start");
                    player.start();
                } else {
                    //System.out.println("stop");
                    player.stop();
                }
            }
        });

        ToggleButton repeatbutton = (ToggleButton) findViewById(R.id.repeatToggleButton);
        repeatbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean istoggled){
                //System.out.println("repeatToggled");
                player.toggleRepeat();
            }
        });

        final EditText tempoField = (EditText) findViewById(R.id.tempoEditorBox);
        tempoField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                System.out.println(tempoField.getText());
                if (!(tempoField.getText().toString().matches(""))) {
                    player.setTempo(Integer.parseInt(tempoField.getText().toString()));
                }
            }
        });

        ImageButton randomButton = (ImageButton) findViewById(R.id.randomButton);
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("Random Clicked");
                player.random();
            }
        });
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
        int[] buttonids = {R.id.imageButton0,R.id.imageButton1,R.id.imageButton2,
            R.id.imageButton3,R.id.imageButton4,R.id.imageButton5,
            R.id.imageButton6,R.id.imageButton7,R.id.imageButton8,
            R.id.imageButton9,R.id.imageButton10,R.id.imageButton11};

        for (int row=0;row<3;row++){
            for (int col=0;col<4;col++){
                int subdivision;
                if (row<2){
                    subdivision = 4;
                    setKeyboardButtonListener(buttonids[row*4+col],subdivision,row*4+col);
                }
                else if (col!=0){
                    subdivision = 3;
                    setKeyboardButtonListener(buttonids[row*4+col],subdivision,col);
                }
                else{
                    ImageButton toggleButton = (ImageButton)findViewById(buttonids[row*4+col]);
                    toggleButton.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            self.isTieOn = !self.isTieOn;
                            if (self.isTieOn){
                                view.getBackground().setColorFilter(new PorterDuffColorFilter(0xFFD3D3D3, PorterDuff.Mode.DARKEN));
                            }
                            else{
                                view.getBackground().setColorFilter(null);
                            }
                            //System.out.println(self.isTieOn);
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
                player.addBeat((self.isTieOn : beatpattern+8 ? beatpattern), subdivision);
                //System.out.println(newbeatpattern);
            }
        });
    }
}
