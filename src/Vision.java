import java.util.ArrayList;

import stuyvision.VisionModule;
import stuyvision.gui.VisionGui;
import stuyvision.gui.IntegerSliderVariable;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Vision extends VisionModule {
    public IntegerSliderVariable minHue = new IntegerSliderVariable("Min Hue", 64,  0, 255);
    public IntegerSliderVariable maxHue = new IntegerSliderVariable("Max Hue", 92, 0, 255);

    public IntegerSliderVariable minSaturation = new IntegerSliderVariable("Min Saturation", 0, 0, 255);
    public IntegerSliderVariable maxSaturation = new IntegerSliderVariable("Max Saturation", 255, 0, 255);

    public IntegerSliderVariable minValue = new IntegerSliderVariable("Min Value", 0, 0, 255);
    public IntegerSliderVariable maxValue = new IntegerSliderVariable("Max Value", 255, 0, 255);

    public void run(Mat frame) {
        postImage(frame, "Camera Feed");

        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);

        ArrayList<Mat> channels = new ArrayList<Mat>();
        Core.split(frame, channels);

        // channels.get(0) is the hue channel
        // channels.get(1) is the saturation channel
        // channels.get(2) is the value channel

        Core.inRange(channels.get(0), new Scalar(minHue.value()), new Scalar(maxHue.value()), channels.get(0));
        postImage(channels.get(0), "Hue-Filtered Frame");

        Core.inRange(channels.get(1), new Scalar(minSaturation.value()), new Scalar(maxSaturation.value()), channels.get(1));
        postImage(channels.get(1), "Saturation-Filtered Frame");

        Core.inRange(channels.get(2), new Scalar(minValue.value()), new Scalar(maxValue.value()), channels.get(2));
        postImage(channels.get(2), "Value-Filtered Frame");

        // AND the three channels into channels.get(0)
        Core.bitwise_and(channels.get(0), channels.get(1), channels.get(0));
        Core.bitwise_and(channels.get(0), channels.get(2), channels.get(0));

        postImage(channels.get(0), "Final filtering");
    }
}
