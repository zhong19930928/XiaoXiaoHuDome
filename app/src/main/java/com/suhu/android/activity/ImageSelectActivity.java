package com.suhu.android.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.suhu.android.R;
import com.suhu.android.adapter.GridImageAdapter;
import com.suhu.android.base.activity.BaseSlidingActivity;
import com.suhu.android.manager.FullyGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * @author suhu
 * @data 2017/9/8.
 * @description https://github.com/suhuMM/PictureSelector
 */

public class ImageSelectActivity extends BaseSlidingActivity {
    private static final int MAX_SELECT_NUM = 9;
    private static final int IMAGE_SPAN_COUNT= 3;


    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    private GridImageAdapter adapter;


    private List<LocalMedia> selectList = new ArrayList<>();

    @Override
    public int showContView() {
        return R.layout.activity_image_select;
    }

    @Override
    public void setActionBar() {
        title.setText("图片选择");
    }

    @Override
    public void setCreateView(Bundle savedInstanceState) {
        addData();
    }

    private void addData() {

        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, IMAGE_SPAN_COUNT, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(MAX_SELECT_NUM);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            PictureSelector.create(ImageSelectActivity.this).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(ImageSelectActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(ImageSelectActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    selectList = PictureSelector.obtainMultipleResult(data);
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }

    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            PictureSelector.create(ImageSelectActivity.this)
                    .openGallery(PictureMimeType.ofAll())
                    .maxSelectNum(MAX_SELECT_NUM)
                    .minSelectNum(1)
                    .imageSpanCount(IMAGE_SPAN_COUNT)
                    .selectionMode(PictureConfig.MULTIPLE)
                    .previewImage(true)
                    .compressGrade(Luban.THIRD_GEAR)
                    .isCamera(true)
                    .isZoomAnim(true)
                    .sizeMultiplier(0.5f)
                    .setOutputCameraPath("/CustomPath")
                    .enableCrop(false)
                    .compress(true)
                    .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)
                    .withAspectRatio(1, 1)
                    .isGif(true)
                    .freeStyleCropEnabled(true)
                    .selectionMedia(selectList)
                    .previewEggs(true)
                    .cropCompressQuality(90)
                    .compressMaxKB(Luban.CUSTOM_GEAR)
                    .compressWH(1, 1)
                    .videoQuality(1)
                    .videoSecond(10)
                    .recordVideoSecond(10)
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        }

    };



    @OnClick(R.id.submit)
    public void onViewClicked(View view) {
        super.onViewClicked(view);
        clear();
    }


    private void clear(){
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(ImageSelectActivity.this);
                    Toast.makeText(ImageSelectActivity.this, "清理成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ImageSelectActivity.this,
                            getString(R.string.picture_jurisdiction), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }
}
