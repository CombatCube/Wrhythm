<CsoundSynthesizer>
<CsInstruments>
;Example by Iain McCurdy

sr = 48000
ksmps = 32
nchnls = 2
0dbfs = 1

giEngine1     fluidEngine                                            ; start fluidsynth engine\
iSfNum1      fluidLoad          "soundfont.sf2", giEngine1, 1         ; load a soundfont
             fluidProgramSelect giEngine1, 1, iSfNum1, 0, 116         ; direct each midi channel to a particular soundfont
  massign 0,0
  massign 1,11

  instr 11                                                           ;fluid synths for midi channels 1
    ;mididefault   60, p3 ; Default duration of 60 -- overridden by score.
    midinoteonkey p4, p5 ; Channels MIDI input to pfields.
    iKey    =    p4                                           ; read in midi note number
    iVel    =    p5                                            ; read in key velocity
    fluidNote    giEngine1, 1, iKey, iVel                            ; apply note to relevant soundfont
  endin

  instr 99; gathering of fluidsynth audio and audio output
    kamplitude1 = 1
    aSigL1,aSigR1      fluidOut          giEngine1; read all audio from the given soundfont
    outs               (aSigL1 * kamplitude1), \
                       (aSigR1 * kamplitude1)
  endin

</CsInstruments>

<CsScore>
i 99 0 360; audio output instrument also keeps performance going
e
</CsScore>

</CsoundSynthesizer>