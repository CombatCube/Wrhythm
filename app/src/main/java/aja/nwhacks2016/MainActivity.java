package aja.nwhacks2016;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends ActionBarActivity {
    private Player player = Player.getPlayer();
    private boolean isTieOn = false;
    private MainActivity self = this;
    int beatIndex = 0;
    View beatViewArray[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAllKeyboardListeners();
        initPlayer();
        beatViewArray = new View[] {
                findViewById(R.id.beat1Button),
                findViewById(R.id.beat2Button),
                findViewById(R.id.beat3Button),
                findViewById(R.id.beat4Button)};
        for (int i = 0; i < 4; ++i) {
            final int index = i;
            beatViewArray[i].setBackground(getResources().getDrawable(R.drawable.notes_0001).mutate());
            player.addBeat(0, 4);
            beatViewArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    beatIndex = index;
                    // darken self
                    v.getBackground().setColorFilter(new PorterDuffColorFilter(0xFFD3D3D3, PorterDuff.Mode.DARKEN));
                    for (int j = 0; j < 4; ++j) {
                        // lighten all other button views
                        if (j != index) {
                            beatViewArray[j].getBackground()
                                    .setColorFilter(null);
                        }
                    }
                }
            });
        }

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

    private void initPlayer() {
        File csdFile = copyFile("playmidi.csd", true);
        File soundfontFile = copyFile("soundfont.sf2", true);
        player.initSounder(csdFile, getBaseContext().getApplicationInfo().nativeLibraryDir);
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

        // the keyboard
        int[] subdiv_4_buttonids = {R.id.button_0001,R.id.button_0011,R.id.button_0101,
                R.id.button_0111,R.id.button_1001,R.id.button_1011,
                R.id.button_1101,R.id.button_1111};
        int[] subdiv_3_buttonids = {R.id.button_011,R.id.button_101,R.id.button_111};

        for (int i = 0; i < 8; ++i) {
            setKeyboardButtonListener(subdiv_4_buttonids[i], 4, i*2 + 1);
        }
        for (int i = 0; i < 3; ++i) {
            setKeyboardButtonListener(subdiv_3_buttonids[i], 3, i*2 + 3);
        }


        // toggle
        ImageButton toggleButton = (ImageButton)findViewById(R.id.button_t);
        toggleButton.setBackground(toggleButton.getBackground().mutate());
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

    private void setKeyboardButtonListener(int buttonId, final int subdivision, final int beatpattern){
        View button = findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newbeatpattern = (self.isTieOn ? beatpattern-1 : beatpattern);
                player.addBeat(beatIndex, newbeatpattern, subdivision);
                Drawable backgroundClone = getMutableDrawByBeatAndSub(subdivision, newbeatpattern);
                backgroundClone.setColorFilter(new PorterDuffColorFilter(0xFFD3D3D3, PorterDuff.Mode.DARKEN));
                beatViewArray[beatIndex].setBackground(backgroundClone);
            }
        });
    }


    protected File copyFile(String filename, boolean overwrite) {
        File file = null;
        file = new File(this.getCacheDir(), filename);
        if (!file.exists() || overwrite) {
            try {
                copyInputStreamToFile(getAssets().open(filename), file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private void copyInputStreamToFile(InputStream in, File file) throws Exception {
        OutputStream out = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int len;
        while((len=in.read(buf))>0){
            out.write(buf,0,len);
        }
        out.close();
        in.close();
    }

    private Drawable getMutableDrawByBeatAndSub(int subdiv, int beat) {
        if (subdiv == 4) {
            switch (beat) {
                case 0: return getResources().getDrawable(R.drawable.notes_0000).mutate();
                case 1: return getResources().getDrawable(R.drawable.notes_0001).mutate();
                case 2: return getResources().getDrawable(R.drawable.notes_0010).mutate();
                case 3: return getResources().getDrawable(R.drawable.notes_0011).mutate();
                case 4: return getResources().getDrawable(R.drawable.notes_0100).mutate();
                case 5: return getResources().getDrawable(R.drawable.notes_0101).mutate();
                case 6: return getResources().getDrawable(R.drawable.notes_0110).mutate();
                case 7: return getResources().getDrawable(R.drawable.notes_0111).mutate();
                case 8: return getResources().getDrawable(R.drawable.notes_1000).mutate();
                case 9: return getResources().getDrawable(R.drawable.notes_1001).mutate();
                case 10: return getResources().getDrawable(R.drawable.notes_1010).mutate();
                case 11: return getResources().getDrawable(R.drawable.notes_1011).mutate();
                case 12: return getResources().getDrawable(R.drawable.notes_1100).mutate();
                case 13: return getResources().getDrawable(R.drawable.notes_1101).mutate();
                case 14: return getResources().getDrawable(R.drawable.notes_1110).mutate();
                case 15: return getResources().getDrawable(R.drawable.notes_1111).mutate();
                default: return null;
            }
        } else {
            // subdiv == 3
            switch (beat) {
                case 2: return getResources().getDrawable(R.drawable.notes_010).mutate();
                case 3: return getResources().getDrawable(R.drawable.notes_011).mutate();
                case 4: return getResources().getDrawable(R.drawable.notes_100).mutate();
                case 5: return getResources().getDrawable(R.drawable.notes_101).mutate();
                case 6: return getResources().getDrawable(R.drawable.notes_110).mutate();
                case 7: return getResources().getDrawable(R.drawable.notes_111).mutate();
                default: return null;
            }
        }
    }
}
