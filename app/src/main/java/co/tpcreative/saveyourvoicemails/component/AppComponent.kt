package co.tpcreative.saveyourvoicemails.component

import co.tpcreative.saveyourvoicemails.MyApplication
import co.tpcreative.saveyourvoicemails.modules.home.HomeModule
import co.tpcreative.saveyourvoicemails.AppModule
import co.tpcreative.saveyourvoicemails.ViewModelModule
import co.tpcreative.saveyourvoicemails.modules.settings.SettingsModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ViewModelModule::class,
    AppModule::class,
    HomeModule::class,
    SettingsModule::class
])
interface AppComponent : AndroidInjector<MyApplication> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<MyApplication>()
}