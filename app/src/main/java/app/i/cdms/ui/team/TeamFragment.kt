package app.i.cdms.ui.team

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import app.i.cdms.R
import app.i.cdms.databinding.FragmentTeamBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class TeamFragment : Fragment(R.layout.fragment_team) {

    private val teamViewModel: TeamViewModel by hiltNavGraphViewModels(R.id.navigation_team)
    private var _binding: FragmentTeamBinding? = null
    private val binding get() = _binding!!
    private var columnCount = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTeamBinding.bind(view)

        val mAdapter = TeamRecyclerViewAdapter()
        // Set the adapter
        with(binding.list) {
            adapter = mAdapter
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                teamViewModel.uiState.collectLatest {
                    mAdapter.submitList(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                teamViewModel.myTeam.collectLatest {
                    it ?: return@collectLatest
                    binding.tvAllUsers.text =
                        getString(R.string.my_team_all_users, it.total.toString())
                    it.rows?.let { list ->
                        val activeUsers = list.filter { agent ->
                            agent.accountBalance != "0.0"
                        }.size
                        binding.tvPayingUsers.text =
                            getString(R.string.my_team_paying_users, activeUsers.toString())
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_register -> {
                findNavController().navigate(R.id.action_teamFragment_to_registerDialogFragment)
                true
            }
            else -> item.onNavDestinationSelected(findNavController()) || super.onOptionsItemSelected(
                item
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.team, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        val pendingQuery = teamViewModel.filter.value.keyName
        if (!pendingQuery.isNullOrEmpty()) {
            searchItem.expandActionView()
            searchView.onActionViewExpanded() // Expand the SearchView
            searchView.setQuery(pendingQuery, false)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                teamViewModel.search(AgentFilter(keyName = newText.orEmpty()))
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}