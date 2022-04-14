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
import app.i.cdms.R
import app.i.cdms.databinding.ActivityMainBinding
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Material You DynamicColors
        DynamicColors.applyIfAvailable(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        val hideActionBarDestinations = setOf(
            R.id.notificationsFragment
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in hideActionBarDestinations) {
                supportActionBar?.hide()
                // Set color of system statusBar same as ActionBar
                window.statusBarColor = SurfaceColors.SURFACE_0.getColor(this)
            } else {
                supportActionBar?.show()
                window.statusBarColor = SurfaceColors.SURFACE_2.getColor(this)
            }
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(hideActionBarDestinations)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // 这里call mainViewModel 执行init方法
        mainViewModel

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                EventBus.events.collectLatest {
                    Log.d("TAG", "EventBus.event: $it")
                    binding.loading.visibility = View.INVISIBLE
                    when (it) {
                        is BaseEvent.Error -> {
                            showMessage(it.exception.message.toString())
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
                            showMessage(it.data.toString())
                        }
                        is BaseEvent.Toast -> {
                            showMessage(getString(it.resId))
                        }
                    }
                }
            }
        }
    }

    private fun showMessage(text: String) {
        if (text.length > 30) {
            Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG)
                .setAction(R.string.message_detail) { showMessageDialog(text) }
                .show()
        } else {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showMessageDialog(text: String) {
        MaterialAlertDialogBuilder(this)
            .setMessage(text)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}