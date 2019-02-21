package com.cesoft.cesdoom.input;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.cesoft.cesdoom.util.Log;

import de.golfgl.gdx.controllers.mapping.ConfiguredInput;
import de.golfgl.gdx.controllers.mapping.ControllerMappings;


////////////////////////////////////////////////////////////////////////////////////////////////////
//
//TODO: Configuracion por usuario:
//https://github.com/MrStahlfelge/gdx-controllerutils/wiki/Configurable-Game-Controller-Mappings
public class CtrMappings extends ControllerMappings {

    public static final int AXIS_HORIZONTAL_DIRECTION = 0;//TODO: Pasar variables a PlayerInput?
    public static final int AXIS_VERTICAL_DIRECTION = 1;
    public static final int AXIS_HORIZONTAL_ORIENTATION = 2;
    public static final int AXIS_VERTICAL_ORIENTATION = 3;
    public static final int BTN_JUMP = 4;
    public static final int BTN_START = 5;
    public static final int BTN_MENU = 6;
    public static final int BTN_BACK = 7;
    public static final int BTN_FIRE1 = 8;
    public static final int BTN_FIRE2 = 9;
    public static final int BTN_FIRE3 = 10;

    /// WINDOWS
    private static final int WIN_BTN_A = 0;
    private static final int WIN_BTN_B = 1;
    private static final int WIN_BTN_X = 2;
    private static final int WIN_BTN_Y = 3;
    private static final int WIN_BTN_FIRE1 = 4;
    private static final int WIN_BTN_FIRE2 = 5;
    private static final int WIN_BTN_SELECT = 6;
    private static final int WIN_BTN_START = 7;

    /// CHINA CES
	private static final int CES_START = 108;
	private static final int CES_SELECT = 4;
	private static final int CES_R_STICK = 107;
	private static final int CES_L_STICK = 106;
	private static final int CES_R_FIRE = 103;
	private static final int CES_L_FIRE = 102;
	private static final int CES_A = 96;
	private static final int CES_B = 97;
	private static final int CES_X = 99;
	private static final int CES_Y = 100;
	private static final int CES_AXIS_R_H = 4;
	private static final int CES_AXIS_R_V = 5;
	private static final int CES_AXIS_L_H = 0;
	private static final int CES_AXIS_L_V = 1;

    /// ANDROID
    //Android.KeyEvent
    private static final int KEYCODE_BUTTON_A      =96;
    private static final int KEYCODE_BUTTON_B      =97;
    private static final int KEYCODE_BUTTON_X      =99;
    private static final int KEYCODE_BUTTON_Y      =100;
    private static final int KEYCODE_BUTTON_R1     =103;
    private static final int KEYCODE_BUTTON_R2     =105;
    private static final int KEYCODE_BUTTON_SELECT =109;
    private static final int KEYCODE_BUTTON_START  =108;
    //Android.MotionEvent
    private static final int AXIS_X =0;     // -1.0 (left) to 1.0 (right)
    private static final int AXIS_Y =1;     // -1.0 (up) to 1.0 (down).
    private static final int KEYCODE_BUTTON_THUMBL=106;
    private static final int AXIS_RZ=14;    // -1.0 (counter-clockwise) to 1.0 (clockwise)
    private static final int AXIS_Z =11;    // -1.0 (high) to 1.0 (low)
    private static final int KEYCODE_BUTTON_THUMBR=107;

    CtrMappings(InputProcessor inputProcessor) {
        super();
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BTN_JUMP));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BTN_FIRE1));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BTN_FIRE2));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BTN_START));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BTN_MENU));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BTN_BACK));
        //TODO: axisAnalog??? https://github.com/MrStahlfelge/gdx-controllerutils/blob/master/core-mapping/src/de/golfgl/gdx/controllers/mapping/ConfiguredInput.java
        //https://github.com/MrStahlfelge/gdx-controllerutils/wiki/Configurable-Game-Controller-Mappings
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axis, AXIS_HORIZONTAL_DIRECTION));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axis, AXIS_VERTICAL_DIRECTION));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axis, AXIS_HORIZONTAL_ORIENTATION));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axis, AXIS_VERTICAL_ORIENTATION));
        commitConfig();

        // Para teclado
        /*ControllerToInputAdapter ctrToInputAdapter = new ControllerToInputAdapter(this);
        ctrToInputAdapter.addButtonMapping(BTN_JUMP, Input.Keys.SPACE);
        ctrToInputAdapter.addButtonMapping(BTN_FIRE1, Input.Keys.ALT_LEFT);
        ctrToInputAdapter.addButtonMapping(BTN_FIRE2, Input.Keys.CONTROL_LEFT);
        ctrToInputAdapter.addButtonMapping(BTN_FIRE3, Input.Keys.CONTROL_RIGHT);
        ctrToInputAdapter.addButtonMapping(BTN_START, Input.Keys.ENTER);
        ctrToInputAdapter.addButtonMapping(BTN_MENU, Input.Keys.ESCAPE);
        ctrToInputAdapter.addButtonMapping(BTN_BACK, Input.Keys.BACK);
        ctrToInputAdapter.addAxisMapping(AXIS_HORIZONTAL_DIRECTION, Input.Keys.LEFT, Input.Keys.RIGHT);
        ctrToInputAdapter.addAxisMapping(AXIS_VERTICAL_DIRECTION, Input.Keys.UP, Input.Keys.DOWN);
        ctrToInputAdapter.addAxisMapping(AXIS_HORIZONTAL_ORIENTATION, Input.Keys.A, Input.Keys.D);
        ctrToInputAdapter.addAxisMapping(AXIS_VERTICAL_ORIENTATION, Input.Keys.W, Input.Keys.S);
        ctrToInputAdapter.setInputProcessor(inputProcessor);*/
    }

    @Override
    public boolean getDefaultMapping(MappedInputs defaultMapping) {
        // see https://developer.android.com/reference/android/view/KeyEvent.html#KEYCODE_BUTTON_A
        boolean onAndroid = Gdx.app.getType() == Application.ApplicationType.Android;
        Log.INSTANCE.e("CtrMappings", "getDefaultMapping----------------"+onAndroid);


        // Buttons
        defaultMapping.putMapping(new MappedInput(BTN_JUMP, new ControllerAxis(CES_A)));
        defaultMapping.putMapping(new MappedInput(BTN_FIRE1, new ControllerAxis(CES_L_FIRE)));
        defaultMapping.putMapping(new MappedInput(BTN_FIRE2, new ControllerAxis(CES_R_FIRE)));
        defaultMapping.putMapping(new MappedInput(BTN_FIRE3, new ControllerAxis(999)));
        defaultMapping.putMapping(new MappedInput(BTN_START, new ControllerAxis(CES_START)));
        defaultMapping.putMapping(new MappedInput(BTN_MENU, new ControllerAxis(CES_SELECT)));
        defaultMapping.putMapping(new MappedInput(BTN_BACK, new ControllerAxis(CES_B)));

        // Axis
        defaultMapping.putMapping(new MappedInput(AXIS_HORIZONTAL_DIRECTION, new ControllerAxis(CES_AXIS_L_H)));
        defaultMapping.putMapping(new MappedInput(AXIS_VERTICAL_DIRECTION, new ControllerAxis(CES_AXIS_L_V)));
        defaultMapping.putMapping(new MappedInput(AXIS_HORIZONTAL_ORIENTATION, new ControllerAxis(CES_AXIS_R_H)));
        defaultMapping.putMapping(new MappedInput(AXIS_VERTICAL_ORIENTATION, new ControllerAxis(CES_AXIS_R_V)));

        /*
        // Buttons
        defaultMapping.putMapping(new MappedInput(BTN_JUMP, new ControllerAxis(onAndroid ? KEYCODE_BUTTON_A : WIN_BTN_A)));
        defaultMapping.putMapping(new MappedInput(BTN_FIRE1, new ControllerAxis(onAndroid ? KEYCODE_BUTTON_R1 : WIN_BTN_FIRE1)));
        defaultMapping.putMapping(new MappedInput(BTN_FIRE2, new ControllerAxis(onAndroid ? KEYCODE_BUTTON_R2 : WIN_BTN_FIRE2)));
        defaultMapping.putMapping(new MappedInput(BTN_FIRE3, new ControllerAxis(onAndroid ? KEYCODE_BUTTON_THUMBR : 999)));
        defaultMapping.putMapping(new MappedInput(BTN_START, new ControllerAxis(onAndroid ? KEYCODE_BUTTON_START : WIN_BTN_START)));
        defaultMapping.putMapping(new MappedInput(BTN_MENU, new ControllerAxis(onAndroid ? KEYCODE_BUTTON_SELECT : WIN_BTN_SELECT)));
        defaultMapping.putMapping(new MappedInput(BTN_BACK, new ControllerAxis(onAndroid ? KEYCODE_BUTTON_B : WIN_BTN_B)));

        // Axis
        defaultMapping.putMapping(new MappedInput(AXIS_HORIZONTAL_DIRECTION, new ControllerAxis(onAndroid ? AXIS_X : 1)));
        defaultMapping.putMapping(new MappedInput(AXIS_VERTICAL_DIRECTION, new ControllerAxis(onAndroid ? AXIS_Y : 0)));
        defaultMapping.putMapping(new MappedInput(AXIS_HORIZONTAL_ORIENTATION, new ControllerAxis(onAndroid? AXIS_RZ : 3)));
        defaultMapping.putMapping(new MappedInput(AXIS_VERTICAL_ORIENTATION, new ControllerAxis(onAndroid ? AXIS_Z : 2)));
*/
        return true;
    }
}
