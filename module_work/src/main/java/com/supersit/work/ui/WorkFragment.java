package com.supersit.work.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.BaseFragment;
import com.supersit.common.entity.EventEntity;
import com.supersit.common.provider.IIMService;
import com.supersit.common.ui.WebViewActivity;
import com.supersit.common.widget.recyclerview.GridSpacingItemDecoration;
import com.supersit.common.widget.recyclerview.SpaceItemDecoration;
import com.supersit.mvp.presenter.RequirePresenter;
import com.supersit.work.R;
import com.supersit.work.R2;
import com.supersit.work.adapter.AppsAdapter;
import com.supersit.work.adapter.PatrolDynamicAdapter;
import com.supersit.work.entity.ApplicationEntity;
import com.supersit.work.entity.WeaterIcon;
import com.supersit.work.entity.WeatherDayEntity;
import com.supersit.work.entity.WeatherEntity;
import com.supersit.work.model.Urls;
import com.supersit.work.presenter.WorkPresenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.lzy.okgo.utils.HttpUtils.runOnUiThread;


@Route(path = ARouterConstant.WorkFragment)
@RequirePresenter(WorkPresenter.class)
public class WorkFragment extends BaseFragment<WorkPresenter> {


    @BindView(R2.id.tv_wait_accept_unread_count)
    TextView tvWaitAcceptUnreadCount;
    @BindView(R2.id.tv_processing_unread_count)
    TextView tvProcessingUnreadCount;
    @BindView(R2.id.rv_applications)
    RecyclerView rvApplications;
    @BindView(R2.id.rv_patrol_dynamic)
    RecyclerView rvPatrolDynamic;

    @BindView(R2.id.tv_today_weater)
    TextView tvTodayWeater;
    @BindView(R2.id.tv_area)
    TextView tvArea;
    @BindView(R2.id.iv_today_weater_icon)
    ImageView ivTodayWeaterIcon;
    @BindView(R2.id.tv_temperature)
    TextView tvTemperature;
    @BindView(R2.id.tv_weather)
    TextView tvWeather;
    @BindView(R2.id.iv_secondday_weater_icon)
    ImageView ivSeconddayWeaterIcon;
    @BindView(R2.id.tv_secondday_weater_temperature)
    TextView tvSeconddayWeaterTemperature;
    @BindView(R2.id.tv_secondday_weater_text)
    TextView tvSeconddayWeaterText;
    @BindView(R2.id.tv_thirdday)
    TextView tvThirdday;
    @BindView(R2.id.iv_thirdday_weater_icon)
    ImageView ivThirddayWeaterIcon;
    @BindView(R2.id.tv_thirdday_weater_temperature)
    TextView tvThirddayWeaterTemperature;
    @BindView(R2.id.tv_thirdday_weater_text)
    TextView tvThirddayWeaterText;
    @BindView(R2.id.tv_fourthday)
    TextView tvFourthday;
    @BindView(R2.id.iv_fourthday_weater_icon)
    ImageView ivFourthdayWeaterIcon;
    @BindView(R2.id.tv_fourthday_weater_temperature)
    TextView tvFourthdayWeaterTemperature;
    @BindView(R2.id.tv_fourthday_weater_text)
    TextView tvFourthdayWeaterText;
    @BindView(R2.id.tv_fifthday)
    TextView tvFifthday;
    @BindView(R2.id.iv_fifthday_weater_icon)
    ImageView ivFifthdayWeaterIcon;
    @BindView(R2.id.tv_fifthday_weater_temperature)
    TextView tvFifthdayWeaterTemperature;
    @BindView(R2.id.tv_fifthday_weater_text)
    TextView tvFifthdayWeaterText;
    @BindView(R2.id.tv_weather_error)
    TextView tvWeatherError;

    private int appsSpanCount = 5;

    private AppsAdapter mAppsAdapter;
    private PatrolDynamicAdapter mPatrolDynamicAdapter;

    @Override
    public void initData(Bundle bundle) {
        ARouter.getInstance().inject(this);
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_work;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {

        rvApplications.setLayoutManager(new GridLayoutManager(mContext, appsSpanCount));
        int spacingInPixels = mContext.getResources().getDimensionPixelSize(R.dimen.spacing_l);
        rvApplications.addItemDecoration(new GridSpacingItemDecoration(appsSpanCount, spacingInPixels, true));


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvPatrolDynamic.setLayoutManager(linearLayoutManager);
        rvPatrolDynamic.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {
        if(isVisible)
            getPresenter().initWorkEventUnReadCount();
    }

    @Override
    public void onFragmentFirstVisible() {
    }


    @OnClick({R2.id.tv_weather_error,R2.id.btn_wait_accept, R2.id.btn_processing, R2.id.btn_processed, R2.id.btn_submited})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_weather_error) {
            tvWeatherError.setVisibility(View.INVISIBLE);
            getPresenter().initWeather();
        } else if (i == R.id.btn_wait_accept) {
            WorkEventListActivity.start(mContext, 1);
        } else if (i == R.id.btn_processing) {
            WorkEventListActivity.start(mContext, 2);
        } else if (i == R.id.btn_processed) {
            WorkEventListActivity.start(mContext, 3);
        } else if (i == R.id.btn_submited) {
            WorkEventListActivity.start(mContext, 4);
        }
    }

    @SuppressLint("StringFormatInvalid")
    public void refreshWeather(WeatherEntity weatherEntity) {
        if(null != weatherEntity && null != weatherEntity.getForecast() && weatherEntity.getForecast().size()>4){
            if(View.INVISIBLE != tvWeatherError.getVisibility())
                tvWeatherError.setVisibility(View.INVISIBLE);
            runOnUiThread(() -> {
                tvTodayWeater.setText(getString(R.string.weather_up_time,
                        TimeUtils.getNowString(new SimpleDateFormat("MM-dd HH:mm:ss")),
                        weatherEntity.getTemperature()));
                List<WeatherDayEntity> weatherDayEntities = weatherEntity.getForecast();
                if(null == weatherDayEntities || weatherDayEntities.isEmpty()){
                    if(View.VISIBLE != tvWeatherError.getVisibility())
                        tvWeatherError.setVisibility(View.VISIBLE);
                    return;
                }
                WeatherDayEntity todayWeahter = weatherDayEntities.get(0);
                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                ivTodayWeaterIcon.setImageResource(WeaterIcon.getWeaterIcon(todayWeahter.getType(), hour));
                tvTemperature.setText(String.format(getResources().getString(R.string.temperature), (todayWeahter.getLow().split(" "))[1], (todayWeahter.getHigh().split(" "))[1]));
                tvWeather.setText(todayWeahter.getType());

                WeatherDayEntity seconddayWeather = weatherDayEntities.get(1);
                ivSeconddayWeaterIcon.setImageResource(WeaterIcon.getWeaterIcon(seconddayWeather.getType(), hour));
                tvSeconddayWeaterText.setText(seconddayWeather.getType());
                tvSeconddayWeaterTemperature.setText(String.format(getResources().getString(R.string.temperature),
                        (seconddayWeather.getLow().split(" "))[1], (seconddayWeather.getHigh().split(" "))[1]));

                WeatherDayEntity thirddayWeather = weatherDayEntities.get(2);
                tvThirdday.setText(thirddayWeather.getDate());
                ivThirddayWeaterIcon.setImageResource(WeaterIcon.getWeaterIcon(thirddayWeather.getType(), hour));
                tvThirddayWeaterText.setText(thirddayWeather.getType());
                tvThirddayWeaterTemperature.setText(String.format(getResources().getString(R.string.temperature),
                        (thirddayWeather.getLow().split(" "))[1], (thirddayWeather.getHigh().split(" "))[1]));

                WeatherDayEntity fourthdayWeather = weatherDayEntities.get(3);
                tvFourthday.setText(fourthdayWeather.getDate());
                ivFourthdayWeaterIcon.setImageResource(WeaterIcon.getWeaterIcon(fourthdayWeather.getType(), hour));
                tvFourthdayWeaterText.setText(fourthdayWeather.getType());
                tvFourthdayWeaterTemperature.setText(String.format(getResources().getString(R.string.temperature),
                        (fourthdayWeather.getLow().split(" "))[1], (fourthdayWeather.getHigh().split(" "))[1]));

                WeatherDayEntity fifthdayWeather = weatherDayEntities.get(4);
                tvFifthday.setText(fifthdayWeather.getDate());
                ivFifthdayWeaterIcon.setImageResource(WeaterIcon.getWeaterIcon(fifthdayWeather.getType(), hour));
                tvFifthdayWeaterText.setText(fifthdayWeather.getType());
                tvFifthdayWeaterTemperature.setText(String.format(getResources().getString(R.string.temperature),
                        (fifthdayWeather.getLow().split(" "))[1], (fifthdayWeather.getHigh().split(" "))[1]));
            });
        }else{
            if(View.VISIBLE != tvWeatherError.getVisibility())
                tvWeatherError.setVisibility(View.VISIBLE);
        }

    }

    public void refreshAppList(List<ApplicationEntity> applicationEntityList) {
        runOnUiThread(() -> {
            if (null == mAppsAdapter) {
                mAppsAdapter = new AppsAdapter(applicationEntityList);
                mAppsAdapter.setOnItemClickListener((adapter, view, position) -> {
                    ApplicationEntity applicationEntity = mAppsAdapter.getItem(position);
                    switch (applicationEntity.getTag()) {
                        case WorkConstant.APP_NOTICE:
                            NotificationListActivity.start(mContext);
                            break;
                        case WorkConstant.APP_WEATHER:
                            WebViewActivity.start(mContext, applicationEntity.getName(), Urls.URL_WEATHER_FORECAST);
                            break;

                        case WorkConstant.APP_PROJECT:
                            ARouter.getInstance().build(ARouterConstant.ProjectListActivity).navigation();
                            break;

                        default:
                            WebViewActivity.start(mContext, applicationEntity.getName(), Urls.URL_WEATHER_FORECAST);
                            break;
                    }
                });
                mAppsAdapter.bindToRecyclerView(rvApplications);
            } else
                mAppsAdapter.setNewData(applicationEntityList);
        });
    }

    public void refreshPatrolDynamicList(List<EventEntity> workEventEntities) {
        if (null == mPatrolDynamicAdapter) {
            mPatrolDynamicAdapter = new PatrolDynamicAdapter(workEventEntities);
            mPatrolDynamicAdapter.setOnItemClickListener((adapter, view, position) -> {
                Bundle bundle = new Bundle();
                EventEntity eventEntity = (EventEntity) adapter.getItem(position);
                bundle.putParcelable("eventEntity",eventEntity);
                ARouter.getInstance().build(ARouterConstant.PatrolLocationActivity).with(bundle).navigation();
            });
            mPatrolDynamicAdapter.bindToRecyclerView(rvPatrolDynamic);
        } else{
            mPatrolDynamicAdapter.setNewData(workEventEntities);
            if(mPatrolDynamicAdapter.getData()!=null && !mPatrolDynamicAdapter.getData().isEmpty())
                rvPatrolDynamic.scrollToPosition(0);
        }

        mPatrolDynamicAdapter.setEmptyView(R.layout.base_empty_page);
    }

    public void addPatrolDynamicList(EventEntity workEventEntitie) {
        if (null == mPatrolDynamicAdapter) {
            List<EventEntity> eventEntities = new ArrayList<>();
            eventEntities.add(workEventEntitie);
            mPatrolDynamicAdapter = new PatrolDynamicAdapter(eventEntities);
            mPatrolDynamicAdapter.setOnItemClickListener((adapter, view, position) -> {

            });
            mPatrolDynamicAdapter.bindToRecyclerView(rvPatrolDynamic);
        } else{
            EventEntity eventEntitie = mPatrolDynamicAdapter.getData().get(0);
            if(eventEntitie.getTime()<workEventEntitie.getTime()){
                mPatrolDynamicAdapter.addData(0,workEventEntitie);
                rvPatrolDynamic.scrollToPosition(0);
            }
        }

    }

    public void refreshWaitAcceptUnreadCount(int unReadCount) {
        runOnUiThread(() -> {
            tvWaitAcceptUnreadCount.setVisibility(unReadCount > 0 ? View.VISIBLE : View.INVISIBLE);
            tvWaitAcceptUnreadCount.setText(String.valueOf(unReadCount));
        });

    }


    public void refreshProcessingUnreadCount(int unReadCount) {
        runOnUiThread(() -> {
            tvProcessingUnreadCount.setVisibility(unReadCount > 0 ? View.VISIBLE : View.INVISIBLE);
            tvProcessingUnreadCount.setText(String.valueOf(unReadCount));
        });
    }

    BaseQuickAdapter.OnItemClickListener onItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        }
    };

}
