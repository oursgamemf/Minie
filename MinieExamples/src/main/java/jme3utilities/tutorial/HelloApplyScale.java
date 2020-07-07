/*
 Copyright (c) 2020, Stephen Gold
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 * Neither the name of the copyright holder nor the names of its contributors
 may be used to endorse or promote products derived from this software without
 specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jme3utilities.tutorial;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.Materials;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Box;

/**
 * A simple example of a kinematic RigidBodyControl with setApplyScale(true).
 *
 * @author Stephen Gold sgold@sonic.net
 */
public class HelloApplyScale extends SimpleApplication {
    // *************************************************************************
    // fields

    /**
     * simulation time (in seconds, &ge;0)
     */
    private float elapsedTime = 0f;
    /**
     * cube geometry, varying in size
     */
    private Geometry cubeGeometry;
    // *************************************************************************
    // new methods exposed

    /**
     * Main entry point for the HelloApplyScale application.
     *
     * @param ignored array of command-line arguments (not null)
     */
    public static void main(String[] ignored) {
        HelloApplyScale application = new HelloApplyScale();
        application.start();
    }
    // *************************************************************************
    // SimpleApplication methods

    /**
     * Initialize this application.
     */
    @Override
    public void simpleInitApp() {
        addLighting();
        configureCamera();
        PhysicsSpace physicsSpace = configurePhysics();

        // Create the cube Geometry and add it to the scene graph.
        Material cubeMaterial = new Material(assetManager, Materials.LIGHTING);
        Mesh cubeMesh = new Box(1f, 1f, 1f);
        cubeGeometry = new Geometry("kine", cubeMesh);
        cubeGeometry.setMaterial(cubeMaterial);
        rootNode.attachChild(cubeGeometry);

        // Create a RigidBodyControl and add it to the Geometry.
        float mass = 2f;
        RigidBodyControl kineRbc = new RigidBodyControl(mass);
        cubeGeometry.addControl(kineRbc);

        // Add the PhysicsControl to the PhysicsSpace.
        physicsSpace.add(kineRbc);

        // Set the kinematic and "apply scale" flags on the PhysicsControl.
        kineRbc.setKinematic(true);
        kineRbc.setApplyScale(true);
    }

    /**
     * Callback invoked once per frame.
     *
     * @param tpf the time interval between frames (in seconds, &ge;0)
     */
    @Override
    public void simpleUpdate(float tpf) {
        /*
         * Vary the scale of the Geometry with time.
         */
        float cycleTime = 3f; // seconds
        float phaseAngle = elapsedTime * FastMath.TWO_PI / cycleTime;

        float scaleFactor = 1f + 0.5f * FastMath.sin(phaseAngle);
        Vector3f scale = Vector3f.UNIT_XYZ.mult(scaleFactor);
        cubeGeometry.setLocalScale(scale);

        elapsedTime += tpf;
    }
    // *************************************************************************
    // private methods

    /**
     * Add lighting to the scene.
     */
    private void addLighting() {
        AmbientLight ambient = new AmbientLight(ColorRGBA.White.mult(0.2f));
        rootNode.addLight(ambient);

        Vector3f direction = new Vector3f(-0.7f, -0.3f, -0.5f).normalizeLocal();
        DirectionalLight sun = new DirectionalLight(direction, ColorRGBA.White);
        rootNode.addLight(sun);
    }

    /**
     * Configure the camera during startup (for a better view).
     */
    private void configureCamera() {
        cam.setLocation(new Vector3f(1f, 1.452773f, 10.1f));
        cam.setRotation(new Quaternion(0f, 0.99891f, -0.043f, -0.017f));
    }

    /**
     * Configure physics during startup.
     */
    private PhysicsSpace configurePhysics() {
        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // Enable debug visualization to reveal what occurs in physics space.
        bulletAppState.setDebugEnabled(true);

        // Direct debug visuals to a post ViewPort that clears the depth buffer.
        // This prevents z-fighting between the box and its debug visuals.
        ViewPort overlay = renderManager.createPostView("Overlay", cam);
        overlay.setClearFlags(false, true, false);
        bulletAppState.setDebugViewPorts(overlay);

        PhysicsSpace physicsSpace = bulletAppState.getPhysicsSpace();

        return physicsSpace;
    }
}