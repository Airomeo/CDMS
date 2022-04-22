package app.i.cdms.ui.team

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
import app.i.cdms.data.model.AgentLevel
import app.i.cdms.databinding.FragmentTeamBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class TeamFragment : Fragment(R.layout.fragment_team) {

    private val viewModel: TeamViewModel by hiltNavGraphViewModels(R.id.navigation_team)
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
                launch {
                    viewModel.uiState.collectLatest {
                        mAdapter.submitList(it)
                    }
                }
                launch {
                    viewModel.myTeam.collectLatest {
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
                launch {
                    viewModel.agentLevelList.collectLatest {
                        if (it.isEmpty()) {
                            Toast.makeText(requireContext(), "您当前的代理等级没有邀请权限", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            showInviteDialog(it)
                        }
                    }
                }
                launch {
                    viewModel.inviteCode.collectLatest {
                        it ?: return@collectLatest
                        showInviteCodeDialog(it)
                    }
                }
            }
        }
    }

    /**
     * 显示获取邀请码窗口
     *
     * @param list: List<AgentLevel>
     * @return
     */
    private fun showInviteDialog(list: List<AgentLevel>) {
        val array = list.map { it.desc }.toTypedArray()
        var selectedIndex = 0
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.invite_choice_dialog_title)
            .setPositiveButton(R.string.dialog_positive_text) { dialog, which ->
                viewModel.fetchInviteCode(list[selectedIndex].type)
            }
            .setNegativeButton(R.string.dialog_negative_text, null)
            .setSingleChoiceItems(
                array, selectedIndex
            ) { dialog, which ->
//                ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                selectedIndex = which
            }
            .show()
    }

    /**
     * 显示生成的邀请码文案
     *
     * @param code: 邀请码
     * @return
     */
    private fun showInviteCodeDialog(code: String) {
        val msg =
            "欢迎加入易达，邀请码${code}，有效期24小时，打开链接注册账户。：https://www.yida178.cn/register?code=${code}"
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.common_result)
            .setMessage(msg)
            .setPositiveButton(R.string.common_copy) { dialog, which ->
                // copy
                val clipboard =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("simple text", msg)
                // Set the clipboard's primary clip.
                clipboard.setPrimaryClip(clip)
            }
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_invite -> {
                viewModel.fetchAgentLevel()
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
        val pendingQuery = viewModel.filter.value.keyName
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
                viewModel.search(AgentFilter(keyName = newText.orEmpty()))
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}