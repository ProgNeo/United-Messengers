package com.progcorp.unitedmessengers

import android.app.Application
import android.os.Build
import com.progcorp.unitedmessengers.data.clients.TelegramClient
import com.progcorp.unitedmessengers.data.clients.VKClient
import com.progcorp.unitedmessengers.data.db.TelegramDataSource
import com.progcorp.unitedmessengers.data.db.TelegramRepository
import com.progcorp.unitedmessengers.data.db.VKDataSource
import com.progcorp.unitedmessengers.data.db.VKRepository
import com.progcorp.unitedmessengers.interfaces.IAccountService
import org.drinkless.td.libcore.telegram.TdApi
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*

class App : Application() {
    companion object {
        lateinit var application: App
    }

    private lateinit var vkAccountService: IAccountService
    private lateinit var vkRetrofit: Retrofit

    lateinit var vkRepository: VKRepository
    lateinit var tgClient: TelegramClient
    lateinit var tgRepository: TelegramRepository

    override fun onCreate() {
        super.onCreate()
        setLocale()

        tgClient = TelegramClient(TdApi.TdlibParameters().apply {
            apiId = applicationContext.resources.getInteger(R.integer.telegram_api_id)
            apiHash = applicationContext.getString(R.string.telegram_api_hash)
            useMessageDatabase = true
            useSecretChats = true
            systemLanguageCode = Locale.getDefault().language
            databaseDirectory = applicationContext.filesDir.absolutePath
            deviceModel = Build.MODEL
            systemVersion = Build.VERSION.RELEASE
            applicationVersion = "1.0.0"
            enableStorageOptimizer = true
        })
        tgRepository = TelegramRepository(TelegramDataSource(tgClient))

        vkAccountService = VKClient(getSharedPreferences("vk_account", MODE_PRIVATE))
        vkRetrofit = Retrofit.Builder()
            .baseUrl("https://api.vk.com/method/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        vkRepository = VKRepository(VKDataSource(vkRetrofit, vkAccountService as VKClient))
    }

    private fun setLocale() {
        val config = resources.configuration
        if (Locale.getDefault().displayLanguage == "Russia") {
            val locale = Locale("ru")
            Locale.setDefault(locale)
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
}