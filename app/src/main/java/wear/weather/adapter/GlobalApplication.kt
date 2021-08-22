package wear.weather.adapter

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.kakao.sdk.common.KakaoSdk
/**
 * 이미지를 캐시를 앱 수준에서 관리하기 위한 애플리케이션 객체이다.
 * 로그인 기반 샘플앱에서 사용한다.
 *
 * @author MJ
 */
class GlobalApplication : Application() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, getString(wear.weather.R.string.kakao_app_key))

    }


}