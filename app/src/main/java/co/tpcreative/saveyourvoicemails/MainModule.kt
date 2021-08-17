package co.tpcreative.saveyourvoicemails

import co.tpcreative.presentation.ui.home.HomeAct
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module()
abstract class MainModule {

    //@PerActivity
    @ContributesAndroidInjector
    abstract fun get(): HomeAct
}