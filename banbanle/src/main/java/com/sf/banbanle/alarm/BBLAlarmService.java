package com.sf.banbanle.alarm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sfchat.media.MediaPlayManager;
import com.maxleap.MLDataManager;
import com.maxleap.MLObject;
import com.maxleap.SaveCallback;
import com.maxleap.exception.MLException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sf.banbanle.FragmentHome;
import com.sf.banbanle.R;
import com.sf.banbanle.bean.TaskBean;
import com.sf.banbanle.bean.UserInfoBean;
import com.sf.banbanle.config.BBLConstant;
import com.sf.banbanle.http.BDPushUtil;
import com.sf.loglib.L;
import com.sf.utils.baseutil.SFToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 16/12/10.
 */

public class BBLAlarmService extends Service {
    public static final String ACTION_SERVICE = "com.sf.banbanle.alarm.floating";

    private final String TAG = getClass().getName();

    private Vibrator mVibrator;

    //定义浮动窗口布局
    private View mRootView;
    private WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;

    private Button mDenyBt, mFinishBt;
    private TextView mTitleTv, mContentTv;
    private ImageView mPhotoIv;

    private String mAlarmId = "";
    private TaskBean mTaskBean;
    private UserInfoBean mUserInfoBean;
    private boolean mIsAdd = false;

    @Override
    public void onCreate() {
        super.onCreate();
        initWindowManager();
        initMotion();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAlarmId = intent.getStringExtra(BBLAlarmManager.ALARM_ID);
        mTaskBean = (TaskBean) intent.getSerializableExtra(BBLConstant.TASK_BEAN);
        mUserInfoBean = (UserInfoBean) intent.getSerializableExtra(BBLConstant.USER_INFO_BEAN);
        createFloatView();
        doMotion();
        playSound();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMotion() {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void initWindowManager() {
        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mRootView = inflater.inflate(R.layout.alarm_float_layout, null);
        //浮动窗口按钮
        mFinishBt = (Button) mRootView.findViewById(R.id.finish_bt);
        mDenyBt = (Button) mRootView.findViewById(R.id.deny_bt);
        mTitleTv = (TextView) mRootView.findViewById(R.id.title_tv);
        mContentTv = (TextView) mRootView.findViewById(R.id.content_tv);
        mPhotoIv = (ImageView) mRootView.findViewById(R.id.photo_iv);

    }

    private void playSound() {
        MediaPlayManager.getInstance().createMediaPlay();
        MediaPlayManager.getInstance().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                MediaPlayManager.getInstance().getPlayer().setLooping(true);
            }
        });
        if (mTaskBean == null || TextUtils.isEmpty(mTaskBean.getVideoPath())) {
            AssetFileDescriptor assetFile = getResources().openRawResourceFd(R.raw.video1);
//            String path="http://hao.1015600.com/upload/ring/000/975/28dab8e81862c9c36111a9657b9391c7.mp3";
            MediaPlayManager.getInstance().startPlay(assetFile.getFileDescriptor(), assetFile.getStartOffset(), assetFile.getLength());
        } else {
            MediaPlayManager.getInstance().startPlay(mTaskBean.getVideoPath());
        }

    }

    private void doMotion() {
        long[] pattern = {200, 400, 200, 400, 200, 400, 200, 400, 200, 400};   // 停止 开启 停止 开启
        mVibrator.vibrate(pattern, -1);
    }

    private void updateDetailView() {
        if (mTaskBean == null) {
            return;
        }
        ImageLoader.getInstance().displayImage("http://" + mTaskBean.getUrl(), mPhotoIv);
        mTitleTv.setText(mTaskBean.getTitle());
        mContentTv.setText(mTaskBean.getContent());
    }

    private void notifyMsgOwner(String state, String taskTitle) {
        JSONObject notification = new JSONObject();
        try {
            notification.put("title", "帮帮乐助手");
            StringBuilder description = new StringBuilder();
            if (BBLConstant.ACCEPT.equals(state)) {
                description.append(mUserInfoBean.getUserName()).append("拒绝了任务: ").append(taskTitle);
            } else if (BBLConstant.DENY.equals(state)) {
                description.append(mUserInfoBean.getUserName()).append("接受了任务: ").append(taskTitle);
            }
            notification.put("description", description);
            notification.put("notification_builder_id", 0);
            notification.put("notification_basic_style", 7);
            notification.put("open_type", 2);
            Intent intent = new Intent(BBLAlarmService.this, FragmentHome.class);
            intent.putExtra(FragmentHome.PAGE_INDEX, 0);
            String url = intent.toURI();
            notification.put("pkg_content", url);
        } catch (Exception e) {
            L.error(TAG, "pushToSingleDevice exception: " + e);
        }
        List<String> channelIds = new ArrayList<>();
        channelIds.add(mUserInfoBean.getChannelId());
        BDPushUtil.pushToBatchDevice(channelIds, notification);
    }


    private void updateTaskState(final String state) {
        MLObject mlObject = new MLObject("Task");
        mlObject.put("state", state);
        mlObject.setObjectId(mTaskBean.getId());
        MLDataManager.saveInBackground(mlObject, new SaveCallback() {
            @Override
            public void done(MLException e) {
                if (e == null) {
                    mVibrator.cancel();
                    MediaPlayManager.getInstance().stopPlay();
                    MediaPlayManager.getInstance().destroyPlayer();
                    if (BBLConstant.DENY.equals(state)) {
                        SFToast.showToast(R.string.deny_task_success);
                        notifyMsgOwner(BBLConstant.DENY, mTaskBean.getTitle());
                    } else {
                        SFToast.showToast(R.string.finish_task_success);
                        notifyMsgOwner(BBLConstant.FINISH, mTaskBean.getTitle());
                    }
                } else {
                    if (BBLConstant.DENY.equals(state)) {
                        SFToast.showToast(R.string.deny_task_fail);
                    } else {
                        SFToast.showToast(R.string.accept_task_fail);
                    }
                    L.error(TAG, "updateTaskState exception: " + e);
                }
                mIsAdd = false;
                mWindowManager.removeViewImmediate(mRootView);
            }
        });
    }

    private void createFloatView() {
        updateDetailView();
        if (!mIsAdd) {
            //添加mFloatLayout
            mWindowManager.addView(mRootView, wmParams);
            mIsAdd = true;
        }
        mFinishBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BBLAlarmManager.getManager().destroyAlarm(BBLAlarmService.this, mAlarmId);
                updateTaskState(BBLConstant.FINISH);
            }
        });

        mDenyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BBLAlarmManager.getManager().destroyAlarm(BBLAlarmService.this, mAlarmId);
                updateTaskState(BBLConstant.DENY);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWindowManager.removeViewImmediate(mRootView);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
