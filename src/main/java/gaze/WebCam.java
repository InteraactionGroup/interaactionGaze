package gaze;

import java.io.IOException;

public class WebCam {

    Process[] listProcess = new Process[1];
    ProcessBuilder pythonWebcam = new ProcessBuilder("python3", "gazeWebcam.py", "--config", "configs/mpiigaze_resnet.yaml");;

    public WebCam(){
        super();
    }

    public void startWebcam() throws IOException {
        this.listProcess[0] = this.pythonWebcam.start();
    }

    public void stopWebcam(){
        this.listProcess[0].destroy();
    }
}
