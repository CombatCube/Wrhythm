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
            beatViewArray[i].setBackground(findViewById(R.id.imageButton0).getBackground().getConstantState().newDrawable().mutate());
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
                int newbeatpattern = (self.isTieOn ? beatpattern+8 : beatpattern);
                player.addBeat(beatIndex, newbeatpattern, subdivision);
                Drawable backgroundClone = getResources().getDrawable(R.drawable.keyboard_01t);
                if (self.isTieOn) {
                    if (backgroundClone == null) {
                        backgroundClone = view.getBackground().getConstantState().newDrawable().mutate();

                    } else {
                        backgroundClone = backgroundClone.mutate();
                    }
                } else {
                    backgroundClone = view.getBackground().getConstantState().newDrawable().mutate();
                }
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
}
