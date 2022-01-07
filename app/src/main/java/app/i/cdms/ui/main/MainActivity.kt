package app.i.cdms.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import app.i.cdms.R
import app.i.cdms.databinding.ActivityMainBinding
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        val topDestinations = setOf(
            R.id.homeFragment, R.id.dashboardFragment, R.id.notificationsFragment
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in topDestinations) {
                binding.navView.visibility = View.VISIBLE
            } else {
                binding.navView.visibility = View.GONE
            }
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(topDestinations)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // 这里call mainViewModel 执行init方法
        mainViewModel

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                EventBus.events.collectLatest {
                    Log.d("TAG", "EventBus.event: $it")
                    binding.loading.visibility = View.GONE
                    when (it) {
                        is BaseEvent.Error -> {
                            Toast.makeText(
                                this@MainActivity,
                                it.exception.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is BaseEvent.Loading -> {
                            binding.loading.visibility = View.VISIBLE
                        }
                        is BaseEvent.Nothing -> {}
                        is BaseEvent.NeedLogin -> {
                            navController.navigate(R.id.loginFragment)
                        }
                        is BaseEvent.Refresh -> {
//                            Re-enter current fragment
                            val id = navController.currentDestination?.id
                            navController.popBackStack(id!!, true)
                            navController.navigate(id)
                        }
                        is BaseEvent.Failed -> {
                            Toast.makeText(
                                this@MainActivity,
                                it.data.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is BaseEvent.Toast -> {
                            Toast.makeText(
                                this@MainActivity,
                                it.msg,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}