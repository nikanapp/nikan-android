package com.bloomlife.videoapp.activity.fragment;

import static com.bloomlife.videoapp.common.CacheKeyConstants.LOCATION_LAST_VISIABL_AREA;
import static com.bloomlife.videoapp.model.MapControllOption.Default_Max_level;
import static com.bloomlife.videoapp.model.MapControllOption.Default_min_level;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bloomlife.android.bean.CacheBean;
import com.bloomlife.android.common.util.UiUtils;
import com.bloomlife.android.framework.MyInjectActivity;
import com.bloomlife.android.log.Logger;
import com.bloomlife.android.log.LoggerFactory;
import com.bloomlife.videoapp.R;
import com.bloomlife.videoapp.app.AppContext;
import com.bloomlife.videoapp.common.CacheKeyConstants;
import com.bloomlife.videoapp.common.DefaultAnimatorListener;
import com.bloomlife.videoapp.common.util.ImageLoaderUtils;
import com.bloomlife.videoapp.manager.MapBoxVideoLoadController;
import com.bloomlife.videoapp.manager.RandomShower;
import com.bloomlife.videoapp.model.CacheLatLng;
import com.bloomlife.videoapp.model.MyLatLng;
import com.bloomlife.videoapp.model.SysCode;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.result.GetVideoListResult;
import com.bloomlife.videoapp.model.result.MoreVideoResult;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.android.gestures.StandardScaleGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraBoundsOptions;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.ScreenCoordinate;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.gestures.OnScaleListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import net.tsz.afinal.annotation.view.ViewInject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Created by zxt lan4627@Gmail.com on 2015/8/3.
 */
public class MapBoxFragment extends BaseMapFragment {

    private static final String TAG = MapBoxFragment.class.getSimpleName();

    public static final float HOT_ANCHOR_Y = 0.79f;
    public static final float HOT_ANCHOR_X = 0.50f;

    public static final float MORE_ANCHOR_Y = 0.89f;
    public static final float MORE_ANCHOR_X = 0.50f;

    public static final int FLY_DURATION = 2000;
    public static final int MAX_ZOOM_LEVEL = 22;

    @ViewInject(id = R.id.fragment_mapbox_map)
    private MapView mMapView;

    @ViewInject(id = R.id.main_layout)
    private ViewGroup mMainLayout;

    @ViewInject(id = R.id.fragment_mapbox_touch_layer)
    private View mTouchLayer;

    @ViewInject(id = R.id.fragment_mapbox_main_location_animview)
    private View mLocationAnimView;

    private MapboxMap mMap;

    private MapBoxVideoLoadController mVideoLoadController;

    private CacheLatLng cacheLatLng;

    private DisplayImageOptions options;

    private String mSelectTopic;

    private double mZoom;

    private boolean mOnMapScroll;
    private boolean mOnMapZoom;
    private boolean mClickLocalBtn;

    private int mHotMarkerHeight;

    private GestureDetector mGDetector;

    private Logger mLog = LoggerFactory.getLogger(TAG);

    private ImageView mClickHotView1;
    private ImageView mClickHotView2;
    private ObjectAnimator mClickHotAnim1;
    private ObjectAnimator mClickHotAnim2;

    private View.OnTouchListener mLayoutTouchListener;

    private Map<String, Video> mSymbolVideoMap = new HashMap<>();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mLayoutTouchListener = (View.OnTouchListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_mapbox, container, false);
        MyInjectActivity.initInjectedView(this, layout);
        super.onCreateView(inflater, container, savedInstanceState);
        initMapLayout(layout);
        mTouchLayer.setOnTouchListener(mLayoutTouchListener);
        mLog.d("onCreate");
        return layout;
    }

    private void initMapLayout(View layout){
        mVideoLoadController = new MapBoxVideoLoadController(this, getActivity());
        cacheLatLng = CacheBean.getInstance().getObject(getActivity().getApplicationContext(), LOCATION_LAST_VISIABL_AREA, CacheLatLng.class);

        initMap();
        initUi(layout);

        initHotMarkerHeight();

        // 如果有保存用户的地理位置，那么直接采用上一次的地理位置。
        if(cacheLatLng != null){
            if(cacheLatLng.hasLoadVideoCoord()){
                hasLoadVideo = true;
                mVideoLoadController.setCacheLatLng(cacheLatLng);
                mVideoLoadController.loadVideo(cacheLatLng.getBottomLeft(), cacheLatLng.getTopRight(), null);
            }
        } else {
            cacheLatLng = new CacheLatLng();
            mVideoLoadController.setCacheLatLng(cacheLatLng);
        }
        options = ImageLoaderUtils.getDescPreviewImageOption(getActivity());

        mGDetector = new GestureDetector(getActivity(), mGestureListener);
        mGDetector.setOnDoubleTapListener(mMapDoubleTapListener);
        initHotClickAnim();
    }

    private void initHotClickAnim(){
        mClickHotView1 = new ImageView(getActivity());
        mClickHotView2 = new ImageView(getActivity());
        mClickHotView1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mClickHotView2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mClickHotView1.setImageResource(R.drawable.display_dot_back1);
        mClickHotView2.setImageResource(R.drawable.display_dot_back2);
        mClickHotView1.setVisibility(View.INVISIBLE);
        mClickHotView2.setVisibility(View.INVISIBLE);

        mClickHotAnim1 = ObjectAnimator.ofPropertyValuesHolder(
                mClickHotView1,
                PropertyValuesHolder.ofFloat("scaleX", 0.8f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 0.8f, 1f));
        mClickHotAnim2 = ObjectAnimator.ofPropertyValuesHolder(
                mClickHotView2,
                PropertyValuesHolder.ofFloat("scaleX", 0.7f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 0.7f, 1f));
        mClickHotAnim2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mClickHotView1.setVisibility(View.INVISIBLE);
                mClickHotView2.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mMainLayout.addView(mClickHotView1);
        mMainLayout.addView(mClickHotView2);
    }

    /**
     * 初始化用于显示热点掉落动画的view
     */
    private void initHotMarkerHeight(){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.play_dot_female, options);

        int width = options.outWidth;
        int height = options.outHeight;

        mHotMarkerHeight = height;

        bringUiToFront();
    }

    public static final String ICON_DOT_FEMALE = "dotFemale";
    public static final String ICON_DOT_MALE = "dotMale";
    public static final String ICON_DOT_FEMALE_VD = "dotFemaleVd";
    public static final String ICON_DOT_MALE_VD = "dotMaleVd";
    public static final String ICON_DOT_INVERSION = "dotInversion";
    public static final String ICON_FEMALE_LOCATION = "femaleLocation";
    public static final String ICON_MALE_LOCATION = "maleLocation";

    private PointAnnotationManager pointAnnotationManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initMap() {
        mLog.d("initMap");
        mMap = mMapView.getMapboxMap();
        mMap.loadStyleUri("mapbox://styles/nikanapp/clo7oy6tb008f01rce5kf4i9g", new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NotNull Style style) {
                style.addImage(ICON_DOT_FEMALE, BitmapFactory.decodeResource(getResources(), R.drawable.play_dot_female));
                style.addImage(ICON_DOT_MALE, BitmapFactory.decodeResource(getResources(), R.drawable.play_dot_male));
                style.addImage(ICON_DOT_FEMALE_VD, BitmapFactory.decodeResource(getResources(), R.drawable.play_dot_female_vd));
                style.addImage(ICON_DOT_MALE_VD, BitmapFactory.decodeResource(getResources(), R.drawable.play_dot_male_vd));
                style.addImage(ICON_DOT_INVERSION, BitmapFactory.decodeResource(getResources(), R.drawable.tiny_dot_inversion));
                style.addImage(ICON_MALE_LOCATION, BitmapFactory.decodeResource(getResources(), R.drawable.male_location));
                style.addImage(ICON_FEMALE_LOCATION, BitmapFactory.decodeResource(getResources(), R.drawable.female_location));

                AnnotationPlugin annotationApi = AnnotationPluginImplKt.getAnnotations(mMapView);
                pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationApi, new AnnotationConfig());
                pointAnnotationManager.addClickListener(mMarkerClickListener);

                CameraOptions build = new CameraOptions.Builder()
                        .center(getUserLatLng())
                        .build();
                mMap.setCamera(build);
            }
        });
        GesturesPlugin plugin = mMapView.getPlugin(Plugin.MAPBOX_GESTURES_PLUGIN_ID);
        plugin.addOnScaleListener(mMapOnScaleListener);

        LocationComponentPlugin locationComponentPlugin = mMapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
        locationComponentPlugin.updateSettings(new Function1<LocationComponentSettings, Unit>() {
            @Override
            public Unit invoke(LocationComponentSettings locationComponentSettings) {
                locationComponentSettings.setEnabled(false);
                locationComponentSettings.setPulsingEnabled(false);
                return null;
            }
        });

        SysCode sysCode = AppContext.getSysCode();
        mVideoLoadController.setCurrentZoomLevel(option.getZoomLevel(getActivity()));
        option.maxZoomLevel = sysCode.getMaxlevel(Default_Max_level);
        option.minZoomLevel = sysCode.getMinlevel(Default_min_level);
        mMap.setBounds(new CameraBoundsOptions.Builder()
                .maxZoom((double)option.maxZoomLevel).minZoom((double)option.minZoomLevel).build());
        mLocationAnimView.setVisibility(View.INVISIBLE);
        mLocationAnimView.bringToFront();
    }

    private Point getUserLatLng(){
        String latStr = CacheBean.getInstance().getString(getActivity(), CacheKeyConstants.LOCATION_LAT);
        String lonStr = CacheBean.getInstance().getString(getActivity(), CacheKeyConstants.LOCATION_LON);
        double userLat = Double.parseDouble(TextUtils.isEmpty(latStr) ? "0" : latStr);
        double userLon = Double.parseDouble(TextUtils.isEmpty(lonStr) ? "0" : lonStr);
        return Point.fromLngLat(
                userLon,userLat
        );
    }

    private boolean stopDisplayWindow(){
        if (randomShow.isShow()){
            randomShow.stopShow();
            return true;
        }
        return false;
    }

    private final OnScaleListener mMapOnScaleListener = new OnScaleListener() {

        @Override
        public void onScaleEnd(@NonNull StandardScaleGestureDetector standardScaleGestureDetector) {
            mOnMapZoom = true;
            double lastZoom = mZoom;
            mZoom = mMap.getCameraState().getZoom();
            mVideoLoadController.setCurrentZoomLevel(mZoom);
            stopDisplayWindow();
            mLog.d("onScaleEnd " + lastZoom + " zoom " + mZoom);
        }

        @Override
        public void onScaleBegin(@NonNull StandardScaleGestureDetector standardScaleGestureDetector) {

        }

        @Override
        public void onScale(@NonNull StandardScaleGestureDetector standardScaleGestureDetector) {

        }
    };

    private void startHotAnim(float x, float y){
        int padding = UiUtils.dip2px(getActivity(), 8);
        mClickHotView1.setX(x - mClickHotView1.getWidth() / 2f);
        mClickHotView1.setY(y - mClickHotView1.getHeight() + padding);
        mClickHotView2.setX(x - mClickHotView1.getWidth() / 2f);
        mClickHotView2.setY(y - mClickHotView2.getHeight() + padding);
        mClickHotView1.setVisibility(View.VISIBLE);
        mClickHotView2.setVisibility(View.VISIBLE);
        mClickHotAnim1.start();
        mClickHotAnim2.start();
    }

    private Video mTapMoreVideo;
    private PointAnnotation mTapMoreMarker;

    private OnPointAnnotationClickListener mMarkerClickListener = new OnPointAnnotationClickListener() {

        @Override
        public boolean onAnnotationClick(@NotNull final PointAnnotation annotation) {
            // 大点被点击
            if (mHotMarkers.containsKey(annotation)){
                Video v = mHotMarkers.get(annotation);
                ScreenCoordinate coordinate = getScreenPoint(Double.parseDouble(v.getLat()), Double.parseDouble(v.getLon()));
                startHotAnim((float) coordinate.getX(), (float) coordinate.getY());
                jumpToPlayVideo(v);
                stopDisplayWindow();
                return true;
            }
            // 小点被点击
            if (mMoreMarkers.containsKey(annotation)){
                MoreVideoResult.MoreVideoVo mv = mMoreMarkers.get(annotation);
                mTapMoreVideo = Video.makeByMore(mv);
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        pointAnnotationManager.delete(annotation);
                        if (mTapMoreMarker != null)
                            pointAnnotationManager.delete(mTapMoreMarker);
                        HotMarkerOptions options = drawVideoPoint(mTapMoreVideo, true);
                        if (options != null) {
                            mTapMoreMarker = pointAnnotationManager.create(options.options);
                            if (options.video.isSendVideo())  {
                                mSendMarker = mTapMoreMarker;
                            }
                        }
                    }
                }, 500);
                MyLatLng center = new MyLatLng(Double.parseDouble(mv.getLat()), Double.parseDouble(mv.getLon()));
                changeMap(center, mMap.getCameraState().getZoom() + 2);
                stopDisplayWindow();
                return true;
            }
            return false;
        }
    };

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.OnGestureListener() {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            // TODO Auto-generated method stub
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                float distanceY) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // TODO Auto-generated method stub
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }
    };

    private GestureDetector.OnDoubleTapListener mMapDoubleTapListener = new GestureDetector.OnDoubleTapListener() {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return stopDisplayWindow();
        }
    };

    private Runnable mMapChangeFinishLoad = new Runnable() {

        @Override
        public void run() {
            mOnMapZoom = false;
            mOnMapScroll = false;
            onLoadVideo();
            if (mClickLocalBtn){
                mClickLocalBtn = false;
                locationAnimator();
            }
        }
    };

    private void onLoadVideo(){
        mZoom = mMap.getCameraState().getZoom();
        mVideoLoadController.setMapViewSize(mMapView);
        //还是要地图加载完成才能获取服务器视频列表，不然的话，你不知道地图显示的区域啊
        Point center = mMap.getCameraState().getCenter();
        cacheLatLng.saveCenterPoint(new MyLatLng(center.latitude(), center.longitude()));
        if (mVideoLoadController.isLoadVideo(mMapView.getMapboxMap(), center, mZoom)){
            loadVideoData(null);
        } else {
            // 视频点不用加载的话就直接显示弹窗。
            if (!randomShow.isShow()) randomShow.setDisplayViewToShow(mVideoList, mGetScreenHotPoint);
            showRandomVideoWindow();
        }
        mapStatusChangeFinish();
    }

    private RandomShower.GetScreenPoint mGetScreenHotPoint = new RandomShower.GetScreenPoint(){

        @Override
        public android.graphics.Point toPoint(
                MyLatLng l) {
            ScreenCoordinate coordinate = mMapView.getMapboxMap().pixelForCoordinate(Point.fromLngLat(l.getLon(), l.getLat()));
            return new android.graphics.Point((int)coordinate.getX(), (int)(coordinate.getY() + mHotMarkerHeight * (1 - HOT_ANCHOR_Y)));
        }

    };

    @Override
    protected boolean changeMap(MyLatLng myLatLng) {
        return changeMap(myLatLng, -1, null);
    }

    @Override
    protected boolean changeMap(MyLatLng myLatLng, double zoom, Animator.AnimatorListener animatorListener) {
        Point center = Point.fromLngLat(myLatLng.getLon(), myLatLng.getLat());
        CameraOptions.Builder cameraOptions = new CameraOptions
                .Builder()
                .center(center);
        if (zoom > 0) {
            cameraOptions.zoom(zoom);
        }
        MapAnimationOptions.Builder animationOptions = new MapAnimationOptions.Builder().duration(FLY_DURATION);
        if (animatorListener != null) {
            animationOptions.animatorListener(animatorListener);
        }
        CameraAnimationsUtils.flyTo(mMap, cameraOptions.build(), animationOptions.build());
        return true;
    }

    @Override
    protected boolean changeMap(MyLatLng myLatLng, double zoom) {
        return changeMap(myLatLng, zoom, null);
    }

    @Override
    protected void loadVideoData(String topic) {
        mSelectTopic = topic;
        mVideoLoadController.loadVideo(mMapView.getMapboxMap(), topic);
    }

    @Override
    protected void showUserSendVideo() {
        if (userVideo != null){
            if(mSendMarker != null){
                pointAnnotationManager.delete(mSendMarker);
            }
            isSendAnimationFinish = false;
            Point centerLng = myLatLngToBoxLatLng(cacheLatLng.getCenterPoint());
            Point user = Point.fromLngLat(userLatLng.getLon(), userLatLng.getLat());
            double latDeviation = Math.abs(centerLng.latitude()-user.latitude());
            double lonDeviation = Math.abs(centerLng.longitude()-user.longitude());

            final Animator.AnimatorListener animatorListener = new DefaultAnimatorListener() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    Log.d(TAG, " 动画结束，绘制发送点point");
                    addHotMarker(userVideo);
                    addHotMarkerToMap(mHotMarkerOptions);
                }
            };

            if(latDeviation<Deviation && lonDeviation<Deviation){
                Log.d(TAG, "  地图中心点为用户当前位置，不需要进行地图平移");
                playSendDotAnimator(getVideoToScreenPoint(userVideo), animatorListener);
            }else {
                changeMap(userLatLng, option.maxZoomLevel);
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // 获取这个视频应该显示在屏幕上的位置
                        playSendDotAnimator(getVideoToScreenPoint(userVideo), animatorListener);
                    }
                }, 600); // 为了能让用户看到移动效果。
            }
        }
    }

    @Override
    protected boolean isOnScreenRange(MyLatLng latLng) {
        Point bottomLeft = mMap.coordinateForPixel(new ScreenCoordinate(0, mMapView.getHeight()));
        Point topRight = mMap.coordinateForPixel(new ScreenCoordinate(mMapView.getWidth(), 0));
        return latLng.getLat() >= bottomLeft.latitude() && latLng.getLon() >= bottomLeft.longitude()
                && topRight.latitude() >= latLng.getLat() && topRight.longitude() >= latLng.getLon();
    }

    @Override
    public void onResume() {
        super.onResume();
        mLocationAnimView.setBackgroundResource(
                AppContext.getSysCode().getSex() == Video.MALE ? R.drawable.circle_male_location : R.drawable.circle_female_location
        );
        randomShow.setDisplayViewToShow(mVideoList, mGetScreenHotPoint);
        mVideoLoadController.setCacheLatLng(cacheLatLng);
        loadVideoData(mSelectTopic);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            drawUserLocation();
        }
    }

    private int zoomLevel = 0;

    private Runnable mRun = new Runnable() {

        @Override
        public void run() {
            zoomLevel += 1;
            CameraAnimationsUtils.easeTo(mMap,
                    new CameraOptions.Builder().center(Point.fromLngLat(0, 0)).zoom((double) zoomLevel).build(),
                    new MapAnimationOptions.Builder().duration(FLY_DURATION).build());
            if (zoomLevel != MAX_ZOOM_LEVEL)
                mHandler.postDelayed(mRun, 10000);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        randomShow.stopShow();
    }

    private void startMapChangeFinishLoad() {
        mOnMapScroll = true;
        mHandler.removeCallbacks(mMapChangeFinishLoad);
        stopDisplayWindow();
        if (mLocationAnimView.getVisibility() == View.VISIBLE){
            mLocationAnimView.setVisibility(View.INVISIBLE);
        }
        mHandler.postDelayed(mMapChangeFinishLoad, 50);
        Logger.d("onScroll", "action onMoveBegin");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.local:
                if(userLatLng != null) {
                    if (changeMap(userLatLng, option.maxZoomLevel, new DefaultAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            startMapChangeFinishLoad();
                        }
                    }))
                    mClickLocalBtn = true;
                } else {
                    Log.e(TAG, " 无法获得用户位置，使用屏幕中间点放大");
                    changeMap(cacheLatLng.getCenterPoint(), option.maxZoomLevel, new DefaultAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            startMapChangeFinishLoad();
                        }
                    });
                    mClickLocalBtn = true;
                }
                break;

            case R.id.btn_load_status:
                loadVideoData(null);
                v.setVisibility(View.INVISIBLE);
                break;

            default:
                break;
        }
    }

    private void locationAnimator(){
        AnimatorSet inAnim = new AnimatorSet();
        inAnim.playTogether(
                ObjectAnimator.ofFloat(mLocationAnimView, "scaleX", 0, 1.4f, 1),
                ObjectAnimator.ofFloat(mLocationAnimView, "scaleY", 0, 1.4f, 1)
        );
        inAnim.setDuration(800);
//		inAnim.setStartDelay(300);
        inAnim.setInterpolator(new DecelerateInterpolator());
        inAnim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mLocationAnimView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                AnimatorSet outAnim = new AnimatorSet();
                outAnim.playTogether(
                        ObjectAnimator.ofFloat(mLocationAnimView, "scaleX", 1, 1.15f, 0),
                        ObjectAnimator.ofFloat(mLocationAnimView, "scaleY", 1, 1.15f, 0)
                );
                outAnim.setInterpolator(new AccelerateInterpolator());
                outAnim.setDuration(600);
                outAnim.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });
        inAnim.start();
    }

    private List<HotMarkerOptions> mHotMarkerOptions = new ArrayList<>();
    private List<MoreMarkerOptions> mMoreMarkerOptions = new ArrayList<>();

    private Map<PointAnnotation, Video> mHotMarkers = new HashMap<>();
    private Map<PointAnnotation, MoreVideoResult.MoreVideoVo> mMoreMarkers = new HashMap<>();

    private PointAnnotation mUserMarker;
    private PointAnnotation mSendMarker;

    /** 大点是否加载完 **/
    private boolean mHotDotLoad;
    /** 小点是否加载完 **/
    private boolean mMoreDotLoad;

    @Override
    public void showProgress(boolean show) {
        super.showProgress(show);
        if (show){
            mHotDotLoad = false;
            mMoreDotLoad = false;
        }
    }

    @Override
    public void doLoadVideoSuccess(GetVideoListResult result) {
        super.doLoadVideoSuccess(result);
        /**  大点掉落的相关动画  */
        stopDisplayWindow();
        mVideoList.clear();
        // mController.clearMarkerFocus();
        Log.v(TAG, "isHotAnimationFinish = false");
        if (!mHotMarkers.isEmpty()){
            pointAnnotationManager.delete(new ArrayList<>(mHotMarkers.keySet()));
            mHotMarkers.clear();
        }
        boolean isContain = false;
        for (Video v:result.getVideos()){
            if (mRandomVideo != null && mRandomVideo.getVideoid().equals(v.getVideoid()))
                isContain = true;
            mVideoList.add(v);
            addHotMarker(v);
        }
        // 如果有随机视频，且随机视频没在返回的视频列表中时需要把随机视频添加到视频列表和地图
        if (mRandomVideo != null && !isContain){
            mVideoList.add(mRandomVideo);
            addHotMarker(mRandomVideo);
        }
        addUserVideoToHotList(mVideoList);

        Log.v(TAG, "addHotMarkerToMap start");
        addHotMarkerToMap(mHotMarkerOptions);
        randomShow.setDisplayViewToShow(mVideoList, mGetScreenHotPoint);

        if (mTapMoreVideo != null){
            videoCacheMap.put(mTapMoreVideo.getVideoid(), mTapMoreVideo);
            mVideoList.add(mTapMoreVideo);
        }
        // 原来的小点有可能会和新返回的大点叠加，所以要检查一遍，把会叠加的去掉。
        for (Map.Entry<PointAnnotation, MoreVideoResult.MoreVideoVo> entry: mMoreMarkers.entrySet()) {
            if (videoCacheMap.containsKey(entry.getValue().getVideoid()) || isOnHotdot(entry.getValue()))
                pointAnnotationManager.delete(entry.getKey());
        }
        showRandomVideoWindow();
    }

    private void addHotMarker(Video v){
        HotMarkerOptions options = drawVideoPoint(v, false);
        if (options != null)
            mHotMarkerOptions.add(options);
    }

    private void addHotMarkerToMap(List<HotMarkerOptions> optionsList){
        for (HotMarkerOptions options:optionsList) {
            PointAnnotation marker = pointAnnotationManager.create(options.options);
            mHotMarkers.put(marker, options.video);
            if (options.video.isSendVideo())  {
                mSendMarker = marker;
            }
        }
        optionsList.clear();
        // 用户位置的点要放在最后面绘制，防止绘制后挡在视频点前面
        drawUserLocation();
        mHotDotLoad = true;
        // 如果这个时候小点已经加载完成了，把小点添加到地图
        if (mMoreDotLoad){
            // 添加小点
            addMoreMarkerToMap(mMoreMarkerOptions);
        }
    }

    private android.graphics.Point getVideoToScreenPoint(Video video){
        ScreenCoordinate coordinate = mMapView.getMapboxMap().pixelForCoordinate(makeLatLng(video));
        return new android.graphics.Point((int)coordinate.getX(), (int) (coordinate.getY() + mHotMarkerHeight * (1 - HOT_ANCHOR_Y)));
    }

    private void addUserVideoToHotList(List<Video> videos){
        if(userVideo != null && isOnScreenRange(userVideo.obtainLatLng())){
            if(!videos.contains(userVideo))
                videos.add(userVideo);
        }
    }

    private HotMarkerOptions drawVideoPoint(Video video, boolean drawSendMarker){
        ScreenCoordinate point = mMap.pixelForCoordinate(Point.fromLngLat(Double.parseDouble(video.getLon()), Double.parseDouble(video.getLat())));
        if(point.getX()<=0||point.getY()<=0){
            Log.v(TAG, " x "+point.getX()+" y "+point.getY());
            Log.v(TAG, " 点并不在屏幕显示区域中，不进行绘制 ");
            return null;
        }
        return makeHotMarker(video, drawSendMarker);
    }

    static class HotMarkerOptions {
        private PointAnnotationOptions options;
        private Video video;

        public HotMarkerOptions(PointAnnotationOptions options, Video video) {
            this.options = options;
            this.video = video;
        }

        public PointAnnotationOptions getOptions() {
            return options;
        }

        public Video getVideo() {
            return video;
        }
    }

    static class MoreMarkerOptions {
        private PointAnnotationOptions options;
        private MoreVideoResult.MoreVideoVo video;

        public MoreMarkerOptions(PointAnnotationOptions options, MoreVideoResult.MoreVideoVo video) {
            this.options = options;
            this.video = video;
        }

        public PointAnnotationOptions getOptions() {
            return options;
        }

        public MoreVideoResult.MoreVideoVo getVideo() {
            return video;
        }
    }

    private HotMarkerOptions makeHotMarker(Video video, boolean drawSendMarker){
        PointAnnotationOptions annotationOptions = new PointAnnotationOptions();
        // annotationOptions.textAllowOverlap(true);
        // annotationOptions.iconAllowOverlap(true);
        annotationOptions.withPoint(getDrawLatLng(Double.parseDouble(video.getLat()), Double.parseDouble(video.getLon())));
        if(video.isSendVideo())  {
            if(!drawSendMarker&&!isSendAnimationFinish) return null;
            if(mSendMarker != null) pointAnnotationManager.delete(mSendMarker);
            annotationOptions.withIconImage(isUserFemale() ? ICON_DOT_FEMALE : ICON_DOT_MALE);
        } else if(video.getSex() == Video.FEMALE) {
            annotationOptions.withIconImage(video.isLook() ? ICON_DOT_FEMALE_VD : ICON_DOT_FEMALE);
        } else if(video.getSex() == Video.MALE) {
            annotationOptions.withIconImage(video.isLook() ? ICON_DOT_MALE_VD : ICON_DOT_MALE);
        }
        annotationOptions.withIconAnchor(IconAnchor.BOTTOM);
        return new HotMarkerOptions(annotationOptions, video);
    }

    private MoreMarkerOptions drawMoreVideoDot(MoreVideoResult.MoreVideoVo mv){
        PointAnnotationOptions annotationOptions = new PointAnnotationOptions();
        annotationOptions.withPoint(getDrawLatLng(Double.parseDouble(mv.getLat()), Double.parseDouble(mv.getLon())));
        annotationOptions.withIconImage(ICON_DOT_INVERSION);
        annotationOptions.withIconAnchor(IconAnchor.BOTTOM);
        return new MoreMarkerOptions(annotationOptions, mv);
    }

    private boolean isUserFemale(){
        return AppContext.getSysCode().getSex() == Video.FEMALE;
    }

    private Point getDrawLatLng(double lat, double lon){
        return Point.fromLngLat(lon, lat);
    }

    private Point makeLatLng(Video video){
        return Point.fromLngLat(Double.parseDouble(video.getLon()), Double.parseDouble(video.getLat()));
    }

    @Override
    public void doLoadMoreVideoSuccess(MoreVideoResult result) {
        super.doLoadMoreVideoSuccess(result);
        if (!mMoreMarkers.isEmpty()){
            pointAnnotationManager.delete(new ArrayList<>(mMoreMarkers.keySet()));
            mMoreMarkers.clear();
        }
        mMoreMarkerOptions.clear();
        for (MoreVideoResult.MoreVideoVo v:mMoreList){
            mMoreMarkerOptions.add(drawMoreVideoDot(v));
        }
        mMoreDotLoad = true;
        // 当大点加载完成后才能添加小点 ，避免小点显示在大点上面
        if (mHotDotLoad)
            addMoreMarkerToMap(mMoreMarkerOptions);
    }

    private Point myLatLngToBoxLatLng(MyLatLng mll){
        return Point.fromLngLat(mll.getLon(), mll.getLat());
    }

    private void addMoreMarkerToMap(List<MoreMarkerOptions> moreMarkerOptions){
        for (MoreMarkerOptions options:moreMarkerOptions) {
            PointAnnotation marker = pointAnnotationManager.create(options.options);
            mMoreMarkers.put(marker, options.video);
        }
        moreMarkerOptions.clear();
    }

    /**
     * 绘制用户所在的点
     */
    private void drawUserLocation(){
        if (pointAnnotationManager != null) {
            if (mUserMarker != null) {
                pointAnnotationManager.delete(mUserMarker);
            }
            PointAnnotationOptions annotationOptions = new PointAnnotationOptions();
            annotationOptions.withPoint(getUserLatLng());
            annotationOptions.withIconImage(AppContext.getSysCode().getSex() == Video.MALE ? ICON_MALE_LOCATION : ICON_FEMALE_LOCATION);
            annotationOptions.withIconAnchor(IconAnchor.CENTER);
            mUserMarker = pointAnnotationManager.create(annotationOptions);
        }
    }

    @Override
    protected ScreenCoordinate getScreenPoint(double lat, double lon) {
        return mMap.pixelForCoordinate(Point.fromLngLat(lon, lat));
    }

    @Override
    protected void addHotMarkerToMap(Video v) {
        HotMarkerOptions options = makeHotMarker(v, false);
        if (options != null){
            mVideoList.add(v);
            mHotMarkers.put(pointAnnotationManager.create(options.options), options.video);
        }
    }

    @Override
    protected RandomShower.GetScreenPoint getScreenPointUtil() {
        return mGetScreenHotPoint;
    }
}
