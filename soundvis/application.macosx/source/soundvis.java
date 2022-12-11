import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.sound.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class soundvis extends PApplet {

/**
 * This sketch shows how to use the Amplitude class to analyze the changing
 * "loudness" of a stream of sound. In this case an audio sample is analyzed.
 */



// Declare the processing sound variables 
FFT fft;
AudioIn in; 
Amplitude rms;

// Declare a smooth factor to smooth out sudden changes in amplitude.
// With a smooth factor of 1, only the last measured amplitude is used for the
// visualisation, which can lead to very abrupt changes. As you decrease the
// smooth factor towards 0, the measured amplitudes are averaged across frames,
// leading to more pleasant gradual changes
float smoothingFactor = 0.25f;

// Used for storing the smoothed amplitude value
float sum;
int numSquares = 5; // press left and right mouse button to add & remove squares
float radius; // move the mouse over the X axis to increase & decrease the radius

int sensitivity=2;
int darkness=35;
float voice; 
PVector prevPoint;
int bands=512;
float [] spectrum= new float[bands];
float [] av_spec= new float [bands];
float maxspec;
int freq;

public void setup() {
  
  colorMode(HSB, 360, 100, 100);
  //Load and play a soundfile and loop it

  rms = new Amplitude(this);
     //Sound s = new Sound(this);
   //s.inputDevice(1);
  in=new AudioIn(this, 0); 
  

  // Create an Input stream which is routed into the Amplitude analyzer
  fft = new FFT(this, bands);

  in.start(); 
 //amp = new Amplitude(this);
 rms.input(in); 

  // patch the AudioIn
  fft.input(in);
background(180, 180, 180);
}      

public void draw() {
  // Set background color, noStroke and fill color
  
  

  // smooth the rms data by smoothing factor
  sum += (rms.analyze() - sum) * smoothingFactor;
   voice=rms.analyze()*500; 

  fft.analyze(spectrum);

  // rms.analyze() return a value between 0 and 1. It's
  // scaled to height/2 and then multiplied by a fixed scale factor
  float rms_scaled = sum * (height/2) * 5;
  
  
  maxspec=0;
  for (int i=00; i < bands; i++) {
    spectrum[i]*=sqrt(i);
    av_spec[i]=0.95f*av_spec[i]+0.05f*spectrum[i];
    if (av_spec[i]>maxspec) {
      maxspec=av_spec[i];
      freq=i;
    }
  }
  println(rms_scaled);
 
  numSquares= PApplet.parseInt(abs(rms_scaled-(400))/20);
   println(numSquares);
   println("frq:"+freq);
    translate(width/2,height/2);
radius = width/8;
  for (int i=0; i<numSquares; i++) {
    pushMatrix();
    translate(sin(radians(i*360/numSquares))*radius,cos(radians(i*360/numSquares))*radius);
    noStroke();
   fill(rms_scaled, rms_scaled-50, freq*4);
     ellipse(0,0, freq*2, freq*2);
  fill(freq, 100, 100,30);
      ellipse(0,0, rms_scaled, rms_scaled);
     
    popMatrix();
  }

}
  public void settings() {  size(1000, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "soundvis" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
