package rx.downloadlibrary

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*

internal class CustomCompositeDisposable {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val disposableMap: HashMap<Any, Disposable> = HashMap()

    fun add(key: Any, value: Disposable) {
        disposableMap[key] = value
        compositeDisposable.add(value)
    }

    fun isDisposed(key: String): Boolean {
        return !disposableMap.containsKey(key) || disposableMap[key]!!.isDisposed
    }

    fun dispose(url: String) {
        val disposable = disposableMap[url]
        if (disposable != null) {
            disposableMap.remove(url)
            compositeDisposable.remove(disposable)
        }
    }

    fun dispose() {
        compositeDisposable.dispose()
        compositeDisposable.clear()
        disposableMap.clear()
    }
}
