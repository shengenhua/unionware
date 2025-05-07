package unionware.base.network.cookie

import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * Author: sheng
 * Date:2025/4/2
 */
class UnionwareCookieJar {
    companion object {
        private var instance: UnionwareCookieJar? = null

        @JvmStatic
        fun getInstance(): UnionwareCookieJar {
            if (instance == null) {
                instance = UnionwareCookieJar()
            }
            return instance!!
        }
    }

    private var unionwareCookieJar: PersistentCookieJar? = null

    fun create(@ApplicationContext context: Context): PersistentCookieJar {
        if (unionwareCookieJar == null) {
            unionwareCookieJar =
                PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
        }
        return unionwareCookieJar!!
    }

    fun getCookieJar(): PersistentCookieJar? {
        return unionwareCookieJar
    }

    fun clear() {
        unionwareCookieJar?.clear()
    }
}