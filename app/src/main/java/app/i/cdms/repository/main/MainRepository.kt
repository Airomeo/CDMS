package app.i.cdms.repository.main

import app.i.cdms.data.model.Token
import app.i.cdms.data.source.local.UserPrefDataSource
import app.i.cdms.data.source.remote.main.MainDataSource

/**
 * @author ZZY
 * 2021/10/22.
 */
class MainRepository(
    private val dataSource: MainDataSource, private val userPrefDataSource: UserPrefDataSource
) {
    val token = userPrefDataSource.tokenFlow

    suspend fun updateToken(token: Token) {
        userPrefDataSource.updateToken(token)
    }
}