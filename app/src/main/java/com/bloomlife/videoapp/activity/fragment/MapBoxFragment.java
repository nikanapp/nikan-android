package com.bloomlife.videoapp.activity.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.bloomlife.videoapp.manager.MessageManager;
import com.bloomlife.videoapp.manager.RandomShower;
import com.bloomlife.videoapp.model.CacheLatLng;
import com.bloomlife.videoapp.model.MyLatLng;
import com.bloomlife.videoapp.model.SysCode;
import com.bloomlife.videoapp.model.Video;
import com.bloomlife.videoapp.model.result.GetVideoListResult;
import com.bloomlife.videoapp.model.result.MoreVideoResult;
import com.easemob.util.LatLng;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.android.gestures.StandardScaleGestureDetector;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.observable.eventdata.CameraChangedEventData;
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer;
import com.mapbox.maps.plugin.MapCameraPlugin;
import com.mapbox.maps.plugin.MapPlugin;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.delegates.listeners.OnCameraChangeListener;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.gestures.OnScaleListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.bloomlife.videoapp.common.CacheKeyConstants.LOCATION_LAST_VISIABL_AREA;
import static com.bloomlife.videoapp.model.MapControllOption.Default_Max_level;
import static com.bloomlife.videoapp.model.MapControllOption.Default_min_level;

/**
 * Created by zhengxingtian lan4627@Gmail.com on 2015/8/3.
 */
public class MapBoxFragment extends BaseMapFragment {

    private static final String TAG = MapBoxFragment.class.getSimpleName();

    public static final float HOT_ANCHOR_Y = 0.79f;
    public static final float HOT_ANCHOR_X = 0.50f;

    public static final float MORE_ANCHOR_Y = 0.89f;
    public static final float MORE_ANCHOR_X = 0.50f;

    @ViewInject(id = R.id.fragment_mapbox_map)
    private MapView mMapView;

    @ViewInject(id = R.id.main_layout)
    private ViewGroup mMainLayout;

    @ViewInject(id = R.id.fragment_mapbox_touch_layer)
    private View mTouchLayer;

    @ViewInject(id = R.id.fragment_mapbox_main_location_animview)
    private View mLocationAnimView;

    private MapboxMap mController;

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initMap() {
        mLog.d("initMap");
        mController = mMapView.getMapboxMap();
        CameraOptions build = new CameraOptions.Builder()
                .center(getUserLatLng())
                .bearing(-17.6)
                .pitch(60.0)
                .build();
        mController.setCamera(build);
        mController.loadStyleUri(Style.MAPBOX_STREETS);
        GesturesPlugin plugin = mMapView.getPlugin(Plugin.MAPBOX_GESTURES_PLUGIN_ID);
        plugin.addOnMoveListener(mMapOnMoveListener);
        plugin.addOnScaleListener(mMapOnScaleListener);

        LocationComponentPlugin locationComponentPlugin = mMapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
        locationComponentPlugin.updateSettings(new Function1<LocationComponentSettings, Unit>() {
            @Override
            public Unit invoke(LocationComponentSettings locationComponentSettings) {
                locationComponentSettings.setEnabled(true);
                locationComponentSettings.setPulsingEnabled(true);
                return null;
            }
        });
        mMapView.addListener(mMapListener);
        mMapView.setMapViewListener(mMarkerClickListener);
        mMapView.getMapOverlay().setLoadingBackgroundColor(getResources().getColor(R.color.fragment_mapbox_background));
        mMapView.getMapOverlay().setLoadingLineColor(getResources().getColor(R.color.fragment_mapbox_line_color));

        SysCode sysCode = AppContext.getSysCode();
        mVideoLoadController.setCurrentZoomLevel(option.getZoomLevel(getActivity()));
        option.maxZoomLevel = sysCode.getMaxlevel(Default_Max_level);
        option.minZoomLevel = sysCode.getMinlevel(Default_min_level);
        mMapView.setMaxZoomLevel(option.maxZoomLevel);
        mMapView.setMinZoomLevel(option.minZoomLevel);

        mLocationAnimView.setVisibility(View.INVISIBLE);
        mLocationAnimView.bringToFront();
    }

    private com.mapbox.geojson.Point getUserLatLng(){
        String latStr = CacheBean.getInstance().getString(getActivity(), CacheKeyConstants.LOCATION_LAT);
        String lonStr = CacheBean.getInstance().getString(getActivity(), CacheKeyConstants.LOCATION_LON);
        double userLat = Double.parseDouble(TextUtils.isEmpty(latStr) ? "0" : latStr);
        double userLon = Double.parseDouble(TextUtils.isEmpty(lonStr) ? "0" : lonStr);
        return com.mapbox.geojson.Point.fromLngLat(
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
            mZoom = mController.getCameraState().getZoom();
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

    private final OnMoveListener mMapOnMoveListener = new OnMoveListener() {

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }

        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
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
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {

            return false;
        }
    };

    private void startHotAnim(float x, float y){
        int padding = UiUtils.dip2px(getActivity(), 8);
        mClickHotView1.setX(x - mClickHotView1.getWidth() / 2);
        mClickHotView1.setY(y - mClickHotView1.getHeight() + padding);
        mClickHotView2.setX(x - mClickHotView1.getWidth() / 2);
        mClickHotView2.setY(y - mClickHotView2.getHeight() + padding);
        mClickHotView1.setVisibility(View.VISIBLE);
        mClickHotView2.setVisibility(View.VISIBLE);
        mClickHotAnim1.start();
        mClickHotAnim2.start();
    }

    private Video mTapMoreVideo;
    private Marker mTapMoreMarker;

    private MapViewListener mMarkerClickListener = new MapViewListener() {

        @Override
        public void onTapMarker(final MapView pMapView, final Marker pMarker) {
            // 大点被点击
            if (pMarker instanceof HotVideoMarker){
                Video v = ((HotVideoMarker)pMarker).getVideo();
                PointF point = getScreenPoint(Double.parseDouble(v.getLat()), Double.parseDouble(v.getLon()));
                startHotAnim(point.x, point.y);
                jumpToPlayVideo(v);
                stopDisplayWindow();
                return;
            }
            // 小点被点击
            if (pMarker instanceof MoreVideoMarker){
                MoreVideoResult.MoreVideoVo mv = ((MoreVideoMarker)pMarker).getMoreVideo();
                mTapMoreVideo = Video.makeByMore(mv);
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        pMapView.clearMarkerFocus();
                        pMapView.removeMarker(pMarker);

                        if (mTapMoreMarker != null)
                            pMapView.removeMarker(mTapMoreMarker);
                        mTapMoreMarker = drawVideoPoint(mTapMoreVideo, true);
                        if (mTapMoreMarker != null)
                            pMapView.addMarker(mTapMoreMarker);
                    }
                }, 500);

                MyLatLng center = new MyLatLng(Double.parseDouble(mv.getLat()), Double.parseDouble(mv.getLon()));
                changeMap(center, pMapView.getZoomLevel()+2);
                stopDisplayWindow();
                return;
            }

        }

        @Override
        public void onTapMap(MapView pMapView, ILatLng pPosition) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onShowMarker(MapView pMapView, Marker pMarker) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLongPressMarker(MapView pMapView, Marker pMarker) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLongPressMap(MapView pMapView, ILatLng pPosition) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onHideMarker(MapView pMapView, Marker pMarker) {
            // TODO Auto-generated method stub

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
        mZoom = mMapView.getZoomLevel();
        mVideoLoadController.setMapViewSize(mMapView);
        //还是要地图加载完成才能获取服务器视频列表，不然的话，你不知道地图显示的区域啊
        LatLng center = mMapView.getCenter();
        cacheLatLng.saveCenterPoint(new MyLatLng(center.getLatitude(), center.getLongitude()));
        if (mVideoLoadController.isLoadVideo(mMapView.getProjection(), center, mZoom)){
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
        public Point toPoint(
                MyLatLng l) {
            PointF point = mMapView.getProjection().toPixels(new LatLng(l.getLat(), l.getLon()), null);
            return new Point((int)point.x, (int)(point.y + mHotMarkerHeight * (1 - HOT_ANCHOR_Y)));
        }

    };

    @Override
    protected boolean changeMap(MyLatLng myLatLng) {
        LatLng center = new LatLng(myLatLng.getLat(), myLatLng.getLon());
        mController.setCurrentlyInUserAction(false);
        return mController.animateTo(center);
    }

    @Override
    protected boolean changeMap(MyLatLng myLatLng, float zoom) {
        LatLng latlng = new LatLng(myLatLng.getLat(), myLatLng.getLon());
        return mController.setZoomAnimated(zoom, latlng, true, false);
    }

    @Override
    protected void loadVideoData(String topic) {
        mSelectTopic = topic;
        mVideoLoadController.loadVideo(mMapView.getProjection(), topic);
    }

    @Override
    protected void showUserSendVideo() {
        if (userVideo != null){
            if(mSendMarker != null){
                mMapView.removeMarker(mSendMarker);
            }
            isSendAnimationFinish = false;
            LatLng centerLng = myLatLngToBoxLatLng(cacheLatLng.getCenterPoint());
            LatLng user = new LatLng(userLatLng.getLat(), userLatLng.getLon());
            double latDeviation = Math.abs(centerLng.getLatitude()-user.getLatitude());
            double lonDeviation = Math.abs(centerLng.getLongitude()-user.getLongitude());

            final Animator.AnimatorListener animatorListener = new DefaultAnimatorListener() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    Log.d(TAG, " 动画结束，绘制发送点point");
                    addHotMarker(userVideo);
                    addHotMarkerToMap(mHotMarkers);
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
                        Point point = getVideoToScreenPoint(userVideo);
                        playSendDotAnimator(point, animatorListener);
                    }
                }, 600); // 为了能让用户看到移动效果。
            }
        }
    }

    @Override
    protected boolean isOnScreenRange(MyLatLng latLng) {
        Projection pj = mMapView.getProjection();
        ILatLng bottomLeft = pj.fromPixels(0, mMapView.getHeight());
        ILatLng topRight = pj.fromPixels(mMapView.getWidth(), 0);
        return latLng.getLat() >= bottomLeft.getLatitude() && latLng.getLon() >= bottomLeft.getLongitude()
                && topRight.getLatitude() >= latLng.getLat() && topRight.getLongitude() >= latLng.getLon();
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

    private int zoomlevel = 0;

    private Runnable mRun = new Runnable() {

        @Override
        public void run() {
            mMapView.getController().setZoom(++zoomlevel, new LatLng(0, 0), false);
            if (zoomlevel != 22)
                mHandler.postDelayed(mRun, 10000);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        randomShow.stopShow();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.local:
                if(userLatLng != null) {
                    if (changeMap(userLatLng, option.maxZoomLevel))
                        mClickLocalBtn = true;
                } else {
                    Log.e(TAG, " 无法获得用户位置，使用屏幕中间点放大");
                    changeMap(cacheLatLng.getCenterPoint(), option.maxZoomLevel);
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

    private List<Marker> mHotMarkers = new ArrayList<>();
    private List<Marker> mMoreMarkers = new ArrayList<>();

    private Marker mUserMarker;
    private Marker mSendMarker;

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
        mMapView.clearMarkerFocus();
        Log.v(TAG, "isHotAnimationFinish = false");
        if (!mHotMarkers.isEmpty()){
            mMapView.removeMarkers(mHotMarkers);
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
        addHotMarkerToMap(mHotMarkers);
        randomShow.setDisplayViewToShow(mVideoList, mGetScreenHotPoint);

        if (mTapMoreVideo != null){
            videoCacheMap.put(mTapMoreVideo.getVideoid(), mTapMoreVideo);
            mVideoList.add(mTapMoreVideo);
        }
        // 原来的小点有可能会和新返回的大点叠加，所以要检查一遍，把会叠加的去掉。
        for (Marker marker:mMoreMarkers) {
            MoreVideoResult.MoreVideoVo video = ((MoreVideoMarker)marker).getMoreVideo();
            if (videoCacheMap.containsKey(video.getVideoid()) || isOnHotdot(video))
                mMapView.removeMarker(marker);
        }
        showRandomVideoWindow();
    }

    private void addHotMarker(Video v){
        Marker marker = drawVideoPoint(v, false);
        if (marker != null)
            mHotMarkers.add(marker);
    }

    private void addHotMarkerToMap(List<Marker> markerList){
        mMapView.addMarkers(markerList);
        // 用户位置的点要放在最后面绘制，防止绘制后挡在视频点前面
        drawUserLocation();
        mHotDotLoad = true;
        // 如果这个时候小点已经加载完成了，把小点添加到地图
        if (mMoreDotLoad){
            // 添加小点
            addMoreMarkerToMap();
        }
    }

    private Point getVideoToScreenPoint(Video video){
        PointF pointF = mMapView.getProjection().toPixels(makeLatLng(video), null);
        return new Point((int)pointF.x, (int) (pointF.y + mHotMarkerHeight * (1 - HOT_ANCHOR_Y)));
    }

    private void addUserVideoToHotList(List<Video> videos){
        if(userVideo != null && isOnScreenRange(userVideo.obtainLatLng())){
            if(!videos.contains(userVideo))
                videos.add(userVideo);
        }
    }

    private Marker drawVideoPoint(Video video, boolean drawSendMarker){
        LatLng latLng = new LatLng(Double.parseDouble(video.getLat()), Double.parseDouble(video.getLon()));
        PointF point = mMapView.getProjection().toPixels(latLng, null);
        if(point.x<=0||point.y<=0){
            Log.v(TAG, " x "+point.x+" y "+point.y);
            Log.v(TAG, " 点并不在屏幕显示区域中，不进行绘制 ");
            return null;
        }
        Marker marker = makeHotMarker(video, drawSendMarker);
        if(video.isSendVideo())  {
            mSendMarker = marker;
        }
        return marker;
    }

    private SymbolLayer makeHotMarker(Video video, boolean drawSendMarker){
        SymbolLayer stretchLayer = new SymbolLayer("1", "2");
        stretchLayer.textField();
        stretchLayer.iconImage();
        stretchLayer.textAllowOverlap(true);
        stretchLayer.iconAllowOverlap(true);
        stretchLayer.iconTextFit();

        HotVideoMarker marker = new HotVideoMarker(mMapView, null, null, getDrawLatLng(Double.parseDouble(video.getLat()), Double.parseDouble(video.getLon())));
        if(video.isSendVideo())  {
            if(!drawSendMarker&&!isSendAnimationFinish) return null;
            if(mSendMarker != null) mMapView.removeMarker(mSendMarker);
            marker.setMarker(getResources().getDrawable(isUserFemale() ? R.drawable.play_dot_female : R.drawable.play_dot_male), false);
        } else if(video.getSex() == Video.FEMALE){
            marker.setMarker(getResources().getDrawable(video.isLook() ? R.drawable.play_dot_female_vd : R.drawable.play_dot_female), false);
        } else if(video.getSex() == Video.MALE){
            marker.setMarker(getResources().getDrawable(video.isLook() ? R.drawable.play_dot_male_vd : R.drawable.play_dot_male), false);
        }
        marker.setVideo(video);
        marker.setAnchor(new PointF(HOT_ANCHOR_X, HOT_ANCHOR_Y));
        return marker;
    }

    private boolean isUserFemale(){
        return AppContext.getSysCode().getSex() == Video.FEMALE;
    }

    private LatLng getDrawLatLng(double lat, double lon){
        return new LatLng(lat, lon);
    }

    private LatLng makeLatLng(Video video){
        return new LatLng(Double.parseDouble(video.getLat()), Double.parseDouble(video.getLon()));
    }

    @Override
    public void doLoadMoreVideoSuccess(MoreVideoResult result) {
        super.doLoadMoreVideoSuccess(result);
        if (!mMoreMarkers.isEmpty()){
            mMapView.removeMarkers(mMoreMarkers);
            mMoreMarkers.clear();
        }
        for (MoreVideoResult.MoreVideoVo v:mMoreList){
            mMoreMarkers.add(drawMoreVideoDot(v));
        }
        mMoreDotLoad = true;
        // 当大点加载完成后才能添加小点 ，避免小点显示在大点上面
        if (mHotDotLoad)
            addMoreMarkerToMap();
    }

    private Marker drawMoreVideoDot(MoreVideoResult.MoreVideoVo mv){
        MoreVideoMarker marker = new MoreVideoMarker(mMapView, null, null, getDrawLatLng(Double.parseDouble(mv.getLat()), Double.parseDouble(mv.getLon())));
        marker.setMarker(getResources().getDrawable(R.drawable.tiny_dot_inversion), false);
        marker.setMoreVideo(mv);
        marker.setAnchor(new PointF(MORE_ANCHOR_X, MORE_ANCHOR_Y));
        return marker;
    }

    private LatLng myLatLngToBoxLatLng(MyLatLng mll){
        return new LatLng(mll.getLat(), mll.getLon());
    }

    private void addMoreMarkerToMap(){
        mMapView.addMarkers(mMoreMarkers);
        // 用户位置的点要放在最后面绘制，防止绘制后挡在视频点前面
//		drawUserLocation();
    }

    /**
     * 绘制用户所在的点
     */
    private void drawUserLocation(){
        if (mUserMarker != null)
            mMapView.removeMarker(mUserMarker);
        mUserMarker = new Marker(mMapView, null, null, getUserLatLng());
        mUserMarker.setMarker(getResources().getDrawable(AppContext.getSysCode().getSex()==Video.MALE ? R.drawable.male_location : R.drawable.female_location));
        mUserMarker.setAnchor(new PointF(0.5f, 0.5f));
        mMapView.addMarker(mUserMarker);
    }


    static class HotVideoMarker extends SymbolLayer {

        private Video mVideo;

        public HotVideoMarker(MapView mv, String aTitle, String aDescription,
                              LatLng aLatLng) {
            super(mv, aTitle, aDescription, aLatLng);
            // TODO Auto-generated constructor stub
        }

        public void setVideo(Video video){
            mVideo = video;
        }

        public Video getVideo(){
            return this.mVideo;
        }

    }

    static class MoreVideoMarker extends Marker{

        private MoreVideoResult.MoreVideoVo mMoreVideo;

        public MoreVideoMarker(MapView mv, String aTitle, String aDescription,
                               LatLng aLatLng) {
            super(mv, aTitle, aDescription, aLatLng);
            // TODO Auto-generated constructor stub
        }

        public void setMoreVideo(MoreVideoResult.MoreVideoVo v){
            this.mMoreVideo = v;
        }

        public MoreVideoResult.MoreVideoVo getMoreVideo(){
            return this.mMoreVideo;
        }

    }

    @Override
    protected PointF getScreenPoint(double lat, double lon) {
        return mMapView.getProjection().toPixels(new LatLng(lat, lon), null);
    }

    @Override
    protected void addHotMarkerToMap(Video v) {
        Marker marker = makeHotMarker(v, false);
        if (marker != null){
            mVideoList.add(v);
            mHotMarkers.add(marker);
            mMapView.addMarker(marker);
        }
    }

    @Override
    protected RandomShower.GetScreenPoint getScreenPointUtil() {
        return mGetScreenHotPoint;
    }
}
