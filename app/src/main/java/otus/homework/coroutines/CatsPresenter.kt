package otus.homework.coroutines

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import kotlin.coroutines.CoroutineContext

class CatsPresenter(
    private val catsService: CatsService
) {

    private var _catsView: ICatsView? = null

    private val presenterScope = PresenterScope()

    fun onInitComplete() {

        presenterScope.launch {
            try {
                val fact = catsService.getCatFact()
                _catsView?.populate(fact)
            } catch (_: SocketTimeoutException) {
                _catsView?.showToast("Не удалось получить ответ от сервера")
            } catch (e: Exception) {
                CrashMonitor.trackWarning(e)
                _catsView?.showToast(e.message ?: "")
            }
        }
    }

    fun attachView(catsView: ICatsView) {
        _catsView = catsView
    }

    fun detachView() {
        _catsView = null
    }

    fun cancelJob() {
        presenterScope.cancel()
    }
}

class PresenterScope() : CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext =
        Dispatchers.Main + job + CoroutineName("CatsCoroutine")

    fun cancel() {
        job.cancel()
    }
}