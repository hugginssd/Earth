package com.sriram;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author sriram
 */
public class EarthAndSun extends Application {

    private final double EARTH_RADIUS = 10.0;
    private final double MOON_RADIUS = 2.73; // Approximately 3.66 times lesser
    private final double SUN_RADIUS = 30.0;
    private final double EARTH_MOON_DISTANCE = 25.0;

    private final int EARTH_ROTATION_DURATION = 5;

    private final String EARTH_IMAGE = "earth_flat.jpg";
    private final String MOON_IMAGE = "moon_flat.jpg";
    private final String SUN_IMAGE = "sun_flat.jpg";

    private final DoubleProperty moonRevolutionAngle = new SimpleDoubleProperty(0.0);

    private Camera camera;
    private Slider rotateXSlider;
    private Slider rotateYSlider;

    @Override
    public void start(Stage stage) {
        camera = makeCamera();
        final Sphere earth = makeEarth();
        final Sphere moon = makeMoon();
        final Sphere sun = makeSun();
        final PointLight light = makePointLight(-10, 0);
        Group root = new Group(earth, moon, sun, light, camera);
        final SubScene threeDScene = make3DScene(root, camera);
        stage.setScene(makeScene(threeDScene));
        stage.setMaximized(true);
        stage.setTitle("Earth & Sun");

        startMoonAnimation(moon);
        startSunAnimation(sun);
        startEarthAnimation(earth);
        initCameraBindings();
        stage.show();
    }

    private Scene makeScene(SubScene threeDScene) {
        StackPane root = new StackPane();
        rotateXSlider = getRotateXSlider();
        StackPane.setAlignment(rotateXSlider, Pos.CENTER_RIGHT);
        rotateYSlider = getRotateYSlider();
        StackPane.setAlignment(rotateYSlider, Pos.BOTTOM_CENTER);
        root.getChildren().addAll(threeDScene, rotateXSlider, rotateYSlider);

        Scene scene = new Scene(root);
        scene.setFill(Color.rgb(5, 5, 5));
        threeDScene.widthProperty().bind(root.widthProperty());
        threeDScene.heightProperty().bind(root.heightProperty());
        return scene;
    }

    private SubScene make3DScene(Group root, Camera camera) {
        SubScene scene = new SubScene(root, 800, 700, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.rgb(5, 5, 5));
        scene.setCamera(camera);
        return scene;
    }

    private Slider getRotateXSlider() {
        Slider slider = new Slider(0.0, 180.0, 0.0);
        slider.setPadding(new Insets(20, 0, 20, 0));
        slider.setBlockIncrement(5.0);
        slider.setShowTickMarks(true);
        slider.setOrientation(Orientation.VERTICAL);
        return slider;
    }

    private Slider getRotateYSlider() {
        Slider slider = new Slider(0.0, 180.0, 0.0);
        slider.setPadding(new Insets(0, 20, 0, 20));
        slider.setBlockIncrement(5.0);
        slider.setShowTickMarks(true);
        return slider;
    }

    private Camera makeCamera() {
        Camera cam = new PerspectiveCamera(true);
        cam.setTranslateZ(-100);
        cam.setFarClip(200.0);
        return cam;
    }

    private Sphere makeEarth() {
        Sphere earth = new Sphere(EARTH_RADIUS);
        earth.setMaterial(getTexture(EARTH_IMAGE));
        earth.setDrawMode(DrawMode.FILL);
        earth.setCullFace(CullFace.BACK);
        earth.setTranslateX(EARTH_MOON_DISTANCE);
        return earth;
    }

    private Sphere makeMoon() {
        Sphere moon = new Sphere(MOON_RADIUS);
        moon.setMaterial(getTexture(MOON_IMAGE));
        moon.setDrawMode(DrawMode.FILL);
        moon.setCullFace(CullFace.BACK);
        return moon;
    }

    private Sphere makeSun() {
        Sphere sun = new Sphere(SUN_RADIUS);
        sun.setMaterial(getTexture(SUN_IMAGE));
        sun.setDrawMode(DrawMode.FILL);
        sun.setCullFace(CullFace.BACK);
        sun.setTranslateX(-60);
        return sun;
    }

    private Material getTexture(String name) {
        PhongMaterial material = new PhongMaterial();
        Image textureImage = new Image(getClass().getResourceAsStream(name));
        material.setDiffuseMap(textureImage);
        material.setDiffuseColor(Color.WHITE);
        return material;
    }

    private PointLight makePointLight(double x, double y) {
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(x);
        light.setTranslateY(y);
        return light;
    }

    private void initCameraBindings() {
        // Simple rotation around X and Y axis. We do not want to do
        // rotation based on any axis in 3d space.

        rotateXSlider.valueProperty().addListener((Observable o) -> {
            camera.setRotationAxis(new Point3D(1.0, 0, 0));
            camera.setRotate(-1 * rotateXSlider.getValue());

            camera.setTranslateX(0.0);
            camera.setTranslateY(-100 * Math.sin(Math.toRadians(rotateXSlider.getValue())));
            camera.setTranslateZ(-100 * Math.cos(Math.toRadians(rotateXSlider.getValue())));
        });

        rotateYSlider.valueProperty().addListener((Observable o) -> {
            camera.setRotationAxis(Rotate.Y_AXIS);
            camera.setRotate(-1 * rotateYSlider.getValue());

            camera.setTranslateX(100 * Math.sin(Math.toRadians(rotateYSlider.getValue())));
            camera.setTranslateY(0.0);
            camera.setTranslateZ(-100 * Math.cos(Math.toRadians(rotateYSlider.getValue())));
        });
    }

    private void startMoonAnimation(Sphere moon) {
        // Initiate bindings
        // For circle C(x, y) where x and y are center of circle
        // Pathx = r * cos(theta) + x
        // Pathy = r * sin(theta)
        moon.translateXProperty().bind(Bindings.createDoubleBinding(() -> {
            return (EARTH_MOON_DISTANCE * Math.cos(Math.toRadians(moonRevolutionAngle.get()))) + EARTH_MOON_DISTANCE;
        }, moonRevolutionAngle));

        moon.translateZProperty().bind(Bindings.createDoubleBinding(() -> {
            return (EARTH_MOON_DISTANCE * Math.sin(Math.toRadians(moonRevolutionAngle.get())));
        }, moonRevolutionAngle));

        moon.setRotationAxis(Rotate.Y_AXIS);
        moon.rotateProperty().bind(Bindings.multiply(-1.0, moonRevolutionAngle));
        Timeline moonRevolution = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(moonRevolutionAngle, 0.0)),
                new KeyFrame(Duration.seconds(EARTH_ROTATION_DURATION * 15), new KeyValue(moonRevolutionAngle, 360.0))
        );
        moonRevolution.setCycleCount(Timeline.INDEFINITE);
        moonRevolution.setAutoReverse(false);
        moonRevolution.play();
    }

    private void startSunAnimation(Sphere sun) {
        sun.setRotationAxis(Rotate.Y_AXIS);
        Timeline sunRotation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(sun.rotateProperty(), 0.0)),
                new KeyFrame(Duration.seconds(3), new KeyValue(sun.rotateProperty(), 360.0))
        );
        sunRotation.setCycleCount(Timeline.INDEFINITE);
        sunRotation.setAutoReverse(false);
        sunRotation.play();
    }

    private void startEarthAnimation(Sphere earth) {
        earth.setRotationAxis(Rotate.Y_AXIS);
        Timeline earthRotation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(earth.rotateProperty(), 360.0)),
                new KeyFrame(Duration.seconds(EARTH_ROTATION_DURATION), new KeyValue(earth.rotateProperty(), 0.0))
        );
        earthRotation.setCycleCount(Timeline.INDEFINITE);
        earthRotation.setAutoReverse(false);
        earthRotation.play();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
