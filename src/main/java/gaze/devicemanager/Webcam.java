package gaze.devicemanager;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class Webcam {

    private VideoCapture webcamCapture = new VideoCapture();
    private static int webcamId = 0;
    private Mat frameCapture;

    private boolean webcamActive = false;

    CascadeClassifier cascadeFaceClassifier;
    CascadeClassifier cascadeEyesClassifier;

    MatOfRect face = new MatOfRect();
    MatOfRect eyes = new MatOfRect();

    JFrame frameDetection;
    JLabel frameLabel;
    ImageIcon imgIcon;

    Runnable frameRunnable;
    Thread frameThread;

    public Webcam(){
        super();
    }

    public void WebcamManager(){
        if (this.webcamActive) {
            this.closeWebcam();
        }
        else {
            this.setupDetection();
            this.createThread();
            this.startWebcam();
        }
    }

    public void setupDetection(){
        this.cascadeFaceClassifier = new CascadeClassifier("haarcascade_frontalface_default.xml");
        this.cascadeEyesClassifier = new CascadeClassifier("haarcascade_eye.xml");
    }

    public void createThread(){
        this.frameRunnable = new Runnable() {
            @Override
            public void run() {
                while(webcamActive){
                    frameCapture = new Mat();
                    webcamCapture.read(frameCapture);

                    faceDetection();
                    eyesDetection();

                    pushImage(ConvertMat2Image(frameCapture));
                }
            }
        };
        this.frameThread = new Thread(this.frameRunnable);
    }

    public void startWebcam(){
        this.webcamActive = true;
        this.webcamCapture.open(webcamId);
        this.frameThread.start();
    }

    public void faceDetection(){
        this.cascadeFaceClassifier.detectMultiScale(this.frameCapture, this.face);
        for (Rect rect : face.toArray()) {
            Imgproc.putText(this.frameCapture, "Face", new Point(rect.x, rect.y - 5), 1, 2, new Scalar(0,0,255));
            Imgproc.rectangle(this.frameCapture, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 100, 0), 3);
        }
    }

    public void eyesDetection(){
        this.cascadeEyesClassifier.detectMultiScale(this.frameCapture, this.eyes);
        for (Rect rect : eyes.toArray()) {
            Imgproc.putText(this.frameCapture, "Eye", new Point(rect.x, rect.y - 5), 1, 2, new Scalar(0,0,255));
            Imgproc.rectangle(this.frameCapture, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(200, 200, 100), 2);
        }
    }

    public BufferedImage ConvertMat2Image(Mat frame){
        MatOfByte byteMat = new MatOfByte();
        Imgcodecs.imencode(".jpg", frame, byteMat);
        byte[] byteArray = byteMat.toArray();
        BufferedImage imgBuff = null;
        try{
            InputStream in = new ByteArrayInputStream(byteArray);
            imgBuff = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return imgBuff;
    }

    public void createFrame(){
        this.frameDetection = new JFrame();
        this.frameDetection.setLayout(new FlowLayout());
        this.frameDetection.setSize(700, 600);
        this.frameDetection.setVisible(true);
        this.frameDetection.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void pushImage(Image img){
        if (this.frameDetection == null){
            this.createFrame();
        }
        if (this.frameLabel != null){
            this.frameDetection.remove(this.frameLabel);
        }
        this.imgIcon = new ImageIcon(img);
        this.frameLabel = new JLabel();
        this.frameLabel.setIcon(this.imgIcon);
        this.frameDetection.add(frameLabel);
        this.frameDetection.revalidate();
    }

    public void closeWebcam(){
        this.frameThread.stop();
        this.webcamCapture.release();
        this.webcamActive = false;
    }
}
