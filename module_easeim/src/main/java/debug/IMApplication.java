package debug;
import com.supersit.common.base.BaseApplication;
import com.supersit.easeim.IMHelper;

/**
 * <p>类说明</p>
 *
 * @author 田皓午 2018/3/27 20:09
 * @version V1.2.0
 * @name IMApplication
 */
public class IMApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
//        FlowManager.init(this);// 初始化
        IMHelper.getInstance().init(this);
    }

}
