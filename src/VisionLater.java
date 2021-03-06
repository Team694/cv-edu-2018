import java.util.ArrayList;

import stuyvision.VisionModule;
import stuyvision.gui.VisionGui;
import stuyvision.gui.IntegerSliderVariable;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class VisionLater extends VisionModule {
    public IntegerSliderVariable minHue = new IntegerSliderVariable("Min Hue", 86,  0, 255);
    public IntegerSliderVariable maxHue = new IntegerSliderVariable("Max Hue", 96, 0, 255);

    public IntegerSliderVariable minSaturation = new IntegerSliderVariable("Min Saturation", 0, 0, 255);
    public IntegerSliderVariable maxSaturation = new IntegerSliderVariable("Max Saturation", 255, 0, 255);

    public IntegerSliderVariable minValue = new IntegerSliderVariable("Min Value", 83, 0, 255);
    public IntegerSliderVariable maxValue = new IntegerSliderVariable("Max Value", 255, 0, 255);

    public void run(Mat frame) {
        postImage(frame, "Camera Feed");

        //Imgproc.medianBlur(frame, frame, 5);
        //postImage(frame, "Blurred");
        Mat mat = new Mat();
        Imgproc.bilateralFilter(frame, mat, 7, 5, 5);
        frame.release();
        frame = mat;

        postImage(frame, "Bilateral filtered");

        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);

        ArrayList<Mat> channels = new ArrayList<Mat>();
        Core.split(frame, channels);

        // channels.get(0) is the hue channel
        // channels.get(1) is the saturation channel
        // channels.get(2) is the value channel

        postImage(channels.get(0), "Hue frame");
        Imgproc.medianBlur(channels.get(0), channels.get(0), 5);
        postImage(channels.get(0), "Blurred hue frame");

        Core.inRange(channels.get(0), new Scalar(minHue.value()), new Scalar(maxHue.value()), channels.get(0));
        postImage(channels.get(0), "Hue-Filtered Frame");

        // Erode and dilate hue:
        Mat dilateKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Imgproc.dilate(channels.get(0), channels.get(0), dilateKernel);
        postImage(channels.get(0), "Hue after dilate");

        Mat erodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.erode(channels.get(0), channels.get(0), erodeKernel);
        postImage(channels.get(0), "Hue after erode");

        Core.inRange(channels.get(1), new Scalar(minSaturation.value()), new Scalar(maxSaturation.value()), channels.get(1));
        postImage(channels.get(1), "Saturation-Filtered Frame");

        Core.inRange(channels.get(2), new Scalar(minValue.value()), new Scalar(maxValue.value()), channels.get(2));
        postImage(channels.get(2), "Value-Filtered Frame");

        // AND the three channels into channels.get(0)
        Core.bitwise_and(channels.get(0), channels.get(1), channels.get(0));
        Core.bitwise_and(channels.get(0), channels.get(2), channels.get(0));

        postImage(channels.get(0), "Final filtering");

        // Release unused Mats
        channels.get(1).release();
        channels.get(2).release();

        // Just a name
        Mat filtered = channels.get(0);
    }
}
