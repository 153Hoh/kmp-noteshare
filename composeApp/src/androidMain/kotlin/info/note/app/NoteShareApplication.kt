package info.note.app

import android.app.Application
import info.note.app.di.coreModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NoteShareApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NoteShareApplication)
            modules(coreModule)
        }
    }
}