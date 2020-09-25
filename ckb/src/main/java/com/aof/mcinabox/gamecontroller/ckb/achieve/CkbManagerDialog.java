package com.aof.mcinabox.gamecontroller.ckb.achieve;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.FileObserver;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.aof.mcinabox.definitions.manifest.AppManifest;
import com.aof.mcinabox.gamecontroller.ckb.R;
import com.aof.mcinabox.gamecontroller.ckb.button.GameButton;
import com.aof.utils.dialog.support.DialogSupports;
import com.aof.utils.dialog.DialogUtils;
import com.aof.utils.FileTool;
import com.aof.utils.PromptUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CkbManagerDialog extends Dialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, Dialog.OnCancelListener {

    private Context mContext;
    private CkbManager mManager;
    private RadioButton radioEditable;
    private RadioButton radioGame;
    private TextView textButtonSum;
    private Button buttonAdd;
    private Spinner spinnerSelected;
    private EditText editFileName;
    private Button buttonLoad;
    private Button buttonExport;
    private Button buttonOK;
    private Button buttonDel;
    private Button buttonClear;
    private KeyboardFileListener fileListener;



    private final static String TAG = "CkbConfigDialog";

    public CkbManagerDialog(@NonNull Context context, CkbManager manager) {
        super(context);
        this.setContentView(R.layout.dialog_customize_keyboard_config);
        this.mContext = context;
        this.mManager = manager;
        initUI();
    }

    private void initUI() {

        radioEditable = findViewById(R.id.input_customize_keyboard_dialog_radio_editable);
        radioGame = findViewById(R.id.input_customize_keyboard_dialog_radio_ingame);
        textButtonSum = findViewById(R.id.input_customize_keyboard_dialog_text_button_sum);
        buttonAdd = findViewById(R.id.input_customize_keyboard_dialog_button_add);
        spinnerSelected = findViewById(R.id.input_customize_keyboard_dialog_spinner_select);
        editFileName = findViewById(R.id.input_customize_keyboard_dialog_edit_filename);
        buttonLoad = findViewById(R.id.input_customize_keyboard_dialog_button_load);
        buttonExport = findViewById(R.id.input_customize_keyboard_dialog_button_export);
        buttonOK = findViewById(R.id.input_customize_keyboard_dialog_button_ok);
        buttonDel = findViewById(R.id.input_customize_keyboard_dialog_button_delete);
        buttonClear = findViewById(R.id.input_customize_keyboard_dialog_button_clear);

        //设定监听
        for (View v : new View[]{buttonAdd, buttonLoad, buttonExport, buttonOK, buttonDel, buttonClear}) {
            v.setOnClickListener(this);
        }
        for (RadioButton r : new RadioButton[]{radioGame, radioEditable}) {
            r.setOnCheckedChangeListener(this);
        }
        this.setOnCancelListener(this);

        //是否显示模式选项
        if(mManager.getController() == null){
            ((LinearLayout)findViewById(R.id.input_customize_keyboard_dialog_layout_mode)).setVisibility(View.GONE);
        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        //关闭目录监听
        fileListener.stopWatching();
        //关闭按键数量刷新
        setCountsRefresh(false);
    }

    @Override
    public void show() {
        super.show();
        //启用目录变化监听
        fileListener = new KeyboardFileListener(this);
        fileListener.startWatching();
        updataUI();
        //启用按键数量刷新
        setCountsRefresh(true);
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    private void removeSelectedFile(){
        String filePath = AppManifest.MCINABOX_KEYBOARD + "/" + spinnerSelected.getSelectedItem().toString();
        FileTool.deleteFile(new File(filePath));
    }

    private void loadSelectedFile(){
        String fileName = spinnerSelected.getSelectedItem().toString();
        if(! mManager.loadKeyboard(fileName)){
            DialogUtils.createSingleChoiceDialog(mContext, "错误", "载入键盘文件失败，文件已损坏！", "确定", null);
        }else{
            PromptUtils.createPrompt(mContext,"键盘文件载入成功!");
        }
    }

    @Override
    public void onClick(View v) {
        if (v == buttonOK) {
            this.dismiss();
        }
        if (v == buttonAdd) {
            mManager.addGameButton(null);
        }
        if (v == buttonExport) {
            if (editFileName.getText() == null) {
                PromptUtils.createPrompt(mContext, "文件名不能为空!");
                return;
            }
            final String fn = editFileName.getText().toString();
            if (fn.equals("")) {
                PromptUtils.createPrompt(mContext, "文件名不能为空!");
                return;
            }

            //检查文件是否存在重复，如果重复，提示是否覆盖。
            boolean run = true;
            for (String str : FileTool.listChildFilesFromTargetDir(AppManifest.MCINABOX_KEYBOARD)) {
                if (str.equals(fn + ".json")) {
                    run = false;
                    DialogUtils.createBothChoicesDialog(mContext, "是否覆盖", "存在同名文件，是否覆盖现有文件？覆盖操作不可恢复", "覆盖", "取消", new DialogSupports() {
                        @Override
                        public void runWhenPositive() {
                            mManager.exportKeyboard(fn);
                        }
                    });
                }
            }
            if (run) {
                mManager.exportKeyboard(fn);
            }
        }

        if (v == buttonDel) {
            if (spinnerSelected.getSelectedItem() != null) {
                String str = spinnerSelected.getSelectedItem().toString();
                if (!str.equals("")) {
                    DialogUtils.createBothChoicesDialog(mContext, "即将删除文件","您将要删除文件 " + str + " ,删除操作不可恢复。", "删除", "取消", new DialogSupports(){
                        @Override
                        public void runWhenPositive(){
                            removeSelectedFile();
                        }
                    });
                }
            }
        }

        if(v == buttonLoad){
            if(spinnerSelected.getSelectedItem() != null){
                String str = spinnerSelected.getSelectedItem().toString();
                if(!str.equals("")){
                    DialogUtils.createBothChoicesDialog(mContext, "即将载入文件","您将要载入文件 " + str + " ,载入操作将会清除您当前的配置，请确认您已经保存了当前的模板。", "载入", "取消", new DialogSupports(){
                        @Override
                        public void runWhenPositive(){
                            loadSelectedFile();
                        }
                    });
                }
            }
        }

        if(v == buttonClear){
            DialogUtils.createBothChoicesDialog(mContext,"即将清空模板", "您将要清除当前已添加的所有自定义按键，此操作不会影响已保存的模板文件，清除操作不可恢复。","清空","取消",new DialogSupports(){
                @Override
                public void runWhenPositive(){
                    mManager.clearKeyboard();
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView == radioEditable) {
            if (isChecked) {
                mManager.setButtonsMode(GameButton.MODE_MOVEABLE_EDITABLE);
                buttonAdd.setVisibility(View.VISIBLE);
            }
        }

        if (buttonView == radioGame) {
            if (isChecked) {
                mManager.setButtonsMode(GameButton.MODE_GAME);
                buttonAdd.setVisibility(View.GONE);
            }
        }
    }

    private ArrayList<String> data;
    public void updataUI() {
        if(data == null){
            data = new ArrayList<>();
            data.addAll(FileTool.listChildFilesFromTargetDir(AppManifest.MCINABOX_KEYBOARD));
            spinnerSelected.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, data));
        }else{
            data.clear();
            data.addAll(FileTool.listChildFilesFromTargetDir(AppManifest.MCINABOX_KEYBOARD));
            ( (BaseAdapter) spinnerSelected.getAdapter()).notifyDataSetChanged();
        }
    }

    public void setButtonCounts(final int counts){
        this.textButtonSum.post(new Runnable() {
            @Override
            public void run() {
                textButtonSum.setText(String.valueOf(counts));
            }
        });
    }

    private Timer mTimer;
    public void setCountsRefresh(boolean able){
        if(able){
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setButtonCounts(mManager.getButtonCounts());
                }
            },500,500);
        }else{
            if(mTimer != null){
                mTimer.cancel();
            }
        }
    }
}

class KeyboardFileListener extends FileObserver {

    private CkbManagerDialog mDialog;

    public KeyboardFileListener(CkbManagerDialog dialog) {
        super(AppManifest.MCINABOX_KEYBOARD);
        this.mDialog = dialog;
    }

    @Override
    public void onEvent(int event, @Nullable String path) {
        switch (event) {
            case FileObserver.CREATE:
            case FileObserver.DELETE:
                mDialog.updataUI();
                break;
            default:
                break;
        }
    }
}