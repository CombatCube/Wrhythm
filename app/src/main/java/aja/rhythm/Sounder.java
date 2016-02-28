package aja.rhythm;

import com.csounds.CsoundObj;

import java.io.File;

import csnd6.controlChannelType;

/**
 * Created by Andrew on 2016-02-27.
 */
public class Sounder {

    static String OPCODE6DIR;
    protected CsoundObj csoundObj;
    protected static final String iStatement = "i %d %f %f %d %d\n";

    public Sounder(String nativeLibraryDir) {
        OPCODE6DIR = nativeLibraryDir;
        initCsoundObj();
        csoundObj.setMessageLoggingEnabled(true);
        csoundObj.getCsound().SetOption("-odac");
        csoundObj.getCsound().SetOption("-b256");
        csoundObj.getCsound().SetOption("-B2048");
        csoundObj.getCsound().SetOption("-+rtmidi=null");
        csoundObj.getCsound().SetOption("-+rtaudio=alsa");
    }

    private void initCsoundObj() {
        File opcodeDir = new File(OPCODE6DIR);
        File[] files = opcodeDir.listFiles();
        for (File file : files) {
            String pluginPath = file.getAbsoluteFile().toString();
            try {
                System.load(pluginPath);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        // This must be set before the Csound object is created.
        csnd6.csndJNI.csoundSetGlobalEnv("OPCODE6DIR", OPCODE6DIR);
        csoundObj = new CsoundObj(false,true);
    }

    public void playNote(int inst, double duration, int pitch, int velocity) {
        csoundObj.inputMessage(String.format(iStatement, inst, 0.0f, duration, pitch, velocity));
    }

    public void start() {
        csoundObj.play();
    }
}
