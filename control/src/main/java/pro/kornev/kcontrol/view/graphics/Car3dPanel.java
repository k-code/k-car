package pro.kornev.kcontrol.view.graphics;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import pro.kornev.kcontrol.view.GBLHelper;
import pro.kornev.kcontrol.view.panels.GraphicPanel;

import com.jogamp.opengl.util.FPSAnimator;

public class Car3dPanel extends GraphicPanel {
    private static final long serialVersionUID = 2219431191224761363L;
    private static final int FPS = 80;

    private Car3dView car3dView;
    
    public Car3dPanel() {
        super();
        name = "3D simulator";
        setLayout(new GridBagLayout());

        GLCanvas canvas = new GLCanvas(createGLCapabilities());
        add(canvas, GBLHelper.create().fillB());

        FPSAnimator animator = new FPSAnimator(FPS);
        car3dView = new Car3dView();

        canvas.addGLEventListener(car3dView);
        
        animator.add(canvas);

        canvas.setMinimumSize(new Dimension());  
        animator.start();
    }
    
    private GLCapabilities createGLCapabilities() {
        GLCapabilities capabilities = new GLCapabilities(GLProfile.getGL2GL3());
        capabilities.setHardwareAccelerated(true);

        capabilities.setNumSamples(2);
        capabilities.setSampleBuffers(true);
        
        return capabilities;
    }
    
    public Car3dView getCar3dView() {
        return car3dView;
    }
}
