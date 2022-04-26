package app.i.cdms.ui.libraries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import app.i.cdms.ui.theme.AppTheme
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutLibrariesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme(context) {
                    LibrariesContainer(
                        Modifier.fillMaxSize(),
                        showLicenseBadges = false,
                        showAuthor = false,
                        showVersion = false
                    )
                }
            }
        }
    }
}