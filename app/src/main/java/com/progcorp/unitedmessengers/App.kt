package com.progcorp.unitedmessengers

import android.app.Application
import com.progcorp.unitedmessengers.data.TelegramClient
import com.progcorp.unitedmessengers.di.AppModule
import com.progcorp.unitedmessengers.util.VKAccountService
import com.progcorp.unitedmessengers.interfaces.IAccountService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

@ExperimentalCoroutinesApi
class App : Application() {
    lateinit var vkAccountService: IAccountService
    lateinit var vkRetrofit: Retrofit
    lateinit var tgClient: TelegramClient

    companion object {
        lateinit var application: App
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        tgClient = TelegramClient(AppModule.provideTdlibParameters(applicationContext))
        tgClient.startAuthentication()
        vkAccountService = VKAccountService(getSharedPreferences("vk_account", MODE_PRIVATE))
        vkRetrofit = Retrofit.Builder()
            .baseUrl("https://api.vk.com/method/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }
}