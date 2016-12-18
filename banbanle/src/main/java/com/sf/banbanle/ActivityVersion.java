package com.sf.banbanle;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.basesmartframe.baseui.BaseActivity;
import com.basesmartframe.dialoglib.DialogFactory;
import com.basesmartframe.request.SFHttpFileHandler;
import com.maxleap.GetCallback;
import com.maxleap.MLDataManager;
import com.maxleap.MLObject;
import com.maxleap.MLQuery;
import com.maxleap.MLQueryManager;
import com.maxleap.exception.MLException;
import com.sf.httpclient.newcore.MethodType;
import com.sf.httpclient.newcore.SFHttpFileCallback;
import com.sf.httpclient.newcore.SFRequest;
import com.sf.loglib.*;
import com.sf.utils.baseutil.NetWorkManagerUtil;
import com.sf.utils.baseutil.SFFileCreationUtil;
import com.sf.utils.baseutil.SFFileHelp;
import com.sf.utils.baseutil.SFManifestUtil;
import com.sf.utils.baseutil.SFToast;

import java.io.File;

/**
 * Created by mac on 16/12/18.
 */

public class ActivityVersion extends BaseActivity {
    private View mVersionView;
    private Dialog mUpgradeDialog;
    private Dialog mDownloadDialog;
    private ProgressBar mDownloadPb;
    private TextView mDownloadTv, mDownloadRetry, mContentTv;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        mVersionView = findViewById(R.id.check_version_ll);

        mVersionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVersion();
            }
        });
    }

    private void checkVersion() {
        MLQuery mlQuery = new MLQuery("Version");
        MLQueryManager.getFirstInBackground(mlQuery, new GetCallback() {
            @Override
            public void done(MLObject mlObject, MLException e) {
                if (e == null) {
                    int versionCode = mlObject.getInt("versionCode");
                    mUrl = mlObject.getString("url");
                    String description = mlObject.getString("description");
                    int curVersionCode = BuildConfig.VERSION_CODE;
                    if (curVersionCode < versionCode) {
                        showUpgradeDialog(mUrl, description);
                    }
                } else {
                    L.error(TAG, "checkVersion exception: " + e);
                }
            }
        });
    }

    private void showUpgradeDialog(final String url, String description) {
        if (mUpgradeDialog == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View rootView = layoutInflater.inflate(R.layout.dialog_upgrade, null);
            mContentTv = (TextView) rootView.findViewById(R.id.upgrade_content_tv);
            Button cancelBt = (Button) rootView.findViewById(R.id.cancel_bt);
            Button upgradeBt = (Button) rootView.findViewById(R.id.upgrade_bt);
            cancelBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUpgradeDialog.dismiss();
                }
            });
            upgradeBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUpgradeDialog.dismiss();
                    if (NetWorkManagerUtil.isNetworkAvailable()) {
                        doDownLoad(url);
                        showLoadingDialog();
                    } else {
                        SFToast.showToast(R.string.net_unavailable);
                    }
                }
            });
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUpgradeDialog.dismiss();
                }
            });
            mUpgradeDialog = new Dialog(this, R.style.base_dialog);
            mUpgradeDialog.setContentView(rootView);
        }
        mContentTv.setText(description);
        mUpgradeDialog.show();

    }

    private void doDownLoad(String url) {
        SFRequest request = new SFRequest(url, MethodType.GET) {
            @Override
            public Class getClassType() {
                return File.class;
            }
        };
        File file = SFFileHelp.createFileOnSD("wakeUp", "wakeup.apk");
        SFHttpFileHandler fileHandler = new SFHttpFileHandler(request, file.getAbsolutePath(), new SFHttpFileCallback() {
            @Override
            public void onSuccess(SFRequest sfRequest, Object o) {
                updateProgress(100);
                mDownloadDialog.dismiss();
            }

            @Override
            public void onFailed(SFRequest sfRequest, Exception e) {
                mDownloadTv.setText("下载失败!");
                mDownloadRetry.setVisibility(View.VISIBLE);
            }

            @Override
            public void callBack(SFRequest request, long count, long current) {
                super.callBack(request, count, current);
                int progress = (int) ((current * 1.0 / count) * 100);
                updateProgress(progress);
            }
        });
        fileHandler.start();
    }

    private void showLoadingDialog() {
        if (mDownloadDialog == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View rootView = layoutInflater.inflate(R.layout.dialog_downloading, null);
            mDownloadPb = (ProgressBar) rootView.findViewById(R.id.upgrade_pb);
            mDownloadTv = (TextView) rootView.findViewById(R.id.upgrade_tv);
            mDownloadRetry = (TextView) rootView.findViewById(R.id.retry_tv);
            mDownloadDialog = new Dialog(this, R.style.base_dialog);
            mDownloadRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doDownLoad(mUrl);
                    showLoadingDialog();
                }
            });
            mDownloadDialog.setContentView(rootView);
            mDownloadDialog.setCanceledOnTouchOutside(false);
            mDownloadDialog.setCancelable(false);
        }
        mDownloadPb.setMax(100);
        mDownloadPb.setProgress(0);
        mDownloadTv.setText("0%");
        mDownloadRetry.setVisibility(View.GONE);
        mDownloadDialog.show();
    }

    private void updateProgress(int progress) {
        mDownloadPb.setProgress(progress);
        mDownloadTv.setText(progress + "%");
    }
}
