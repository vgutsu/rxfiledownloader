package rx.downloadlibrary

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*

class CustomCompositeDisposable {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val disposableMap: HashMap<Any, Disposable> = HashMap()

    fun add(tag: Any, value: Disposable) {
        disposableMap[tag] = value
        compositeDisposable.add(value)
    }

    fun isDisposed(tag: Any): Boolean {
        return !disposableMap.containsKey(tag) || disposableMap[tag]!!.isDisposed
    }

    fun dispose(tag: Any) {
        val disposable = disposableMap[tag]
        if (disposable != null) {
            disposableMap.remove(tag)
            compositeDisposable.remove(disposable)
        }
    }

    fun dispose() {
        compositeDisposable.dispose()
        compositeDisposable.clear()
        disposableMap.clear()
    }
}
