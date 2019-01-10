package edu.wpi.grip.core.operations.composite;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import edu.wpi.grip.core.Description;
import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHints;
import org.bytedeco.javacpp.opencv_core;

import java.util.List;

/**
 * Crop an image to an exact x range and y range. Cropping
 * images can be a useful optimization, because sometimes
 * only part of the camera's view is important.
 */
@Description(name = "Crop Image",
        summary = "Crop an image to an exact size",
        category = OperationDescription.Category.IMAGE_PROCESSING,
        iconName = "crop")
public class CropOperation implements Operation {

    private final InputSocket<opencv_core.Mat> inputSocket;
    private final InputSocket<List<Number>> xSocket;
    private final InputSocket<List<Number>> ySocket;

    private final OutputSocket<opencv_core.Mat> outputSocket;

    @Inject
    @SuppressWarnings("JavadocMethod")
    public CropOperation(InputSocket.Factory inputSocketFactory, OutputSocket.Factory
            outputSocketFactory) {
        this.inputSocket = inputSocketFactory.create(SocketHints.Inputs
                .createMatSocketHint("Input", false));
        this.xSocket = inputSocketFactory.create(SocketHints.Inputs
                .createNumberListRangeSocketHint("x range", 0, 1));
        this.ySocket = inputSocketFactory.create(SocketHints.Inputs
                .createNumberListRangeSocketHint("y range", 0, 1));

        this.outputSocket = outputSocketFactory.create(SocketHints.Outputs
                .createMatSocketHint("Output"));
    }

    @Override
    public List<InputSocket> getInputSockets() {
        return ImmutableList.of(
                inputSocket,
                xSocket,
                ySocket
        );
    }

    @Override
    public List<OutputSocket> getOutputSockets() {
        return ImmutableList.of(
                outputSocket
        );
    }

    @Override
    public void perform() {
        final opencv_core.Mat input = inputSocket.getValue().get();
        final List<Number> x = xSocket.getValue().get();
        final List<Number> y = ySocket.getValue().get();

        opencv_core.Range xRange = new opencv_core.Range(
                (int) (input.cols() * x.get(0).floatValue()),
                (int) (input.cols() * x.get(1).floatValue())
        );

        opencv_core.Range yRange = new opencv_core.Range(
                (int) (input.rows() * y.get(0).floatValue()),
                (int) (input.rows() * y.get(1).floatValue())
        );

        outputSocket.setValue(input.apply(yRange, xRange));
    }
}