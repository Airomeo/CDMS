package app.i.cdms.data.source.remote.main

import app.i.cdms.api.ApiService
import javax.inject.Inject

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class MainDataSource @Inject constructor(private val service: ApiService)