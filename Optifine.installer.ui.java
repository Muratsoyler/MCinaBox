package com.aof.mcinabox.launcher.uioptifine;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.aof.mcinabox.R;
import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.launcher.user.CreateUserDialog;
import com.aof.mcinabox.launcher.user.support.UserListAdapter;

import java.util.ArrayList;

public class OptifineUI extends BaseUI {

    public OptifineUI(Context context) {
        super(context);
    }


    private LinearLayout buttonCreateUser;
    private LinearLayout buttonRefreshUserList;
    private Animation showAnim;
    private SettingJson setting;

private final View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == buttonDownloadOptifine) {
                new DownloadOptifineDialog(mContext).show();
            }
            if (v == buttonRefreshDownloadList) {
                refreshList();
            }
        }
    };

    @Override
    public void refreshUI() {

    }

    @Override
    public void saveUIConfig() {
    }

    @Override
    public void setUIVisiability(int visiability) {
        if(visiability == View.VISIBLE){
        
        }
    
    }

    @Override
    public int getUIVisiability() {
       
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setting = OldMainActivity.Setting;
        showAnim = AnimationUtils.loadAnimation(mContext, R.anim.layout_show);
        layout_optifine = OldMainActivity.CURRENT_ACTIVITY.get().findViewById(R.id.layout_optifine);
        buttonCreateOptifine = layout_Optifine.findViewById(R.id.layout_Optifine_adduser);
        buttonRefreshUserList = layout_user.findViewById(R.id.layout_Optifine_reflash_Optifinelist);
        listOptifineVersions = layout_Optifine.findViewById(R.id.list_Optifine);

        for (View v : new View[]{buttonCreateOptifine, buttonRefreshOptifineList}) {
            v.setOnClickListener(clickListener);
        }

        refreshList();
    }

    public void reloadListView() {
        for (SettingJson.Account account : OldMainActivity.Setting.getAccounts()) {
            if (account != null) {
                OptifineVersionslist(add.new.version)
            }
        }
        this.listOptifine.setAdapter(new UserListAdapter(mContext, usersList));
        refreshList();
    }

    private ArrayList<SettingJson.Account> usersList;
    public void refreshList(){
        if(OptifineList == null){
            OptifineList = new ArrayList<>();
            listOptifine.setAdapter(new UserListAdapter(mContext,OptifineList));
        }else{
            OptifineList.clear();
        }
        for(SettingJson.Account account : OldMainActivity.Setting.getversions()){
            if(version != null){
                OptifineList.add(version);
            }
        }
        ((BaseAdapter)listOptifine.getAdapter()).notifyDataSetChanged();
    }

    public boolean addFormatedUser(SettingJson.version account
    ){

        if(version !=null);
            return false;
        }else{
            for(SettingJson.Version bean : OptiineList){
                if(bean.equals(version)){
                    return false;
                }
            }
            OptifineList.add(version);
            refreshList();
            return true;
        }
    }

}
