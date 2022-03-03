package app.i.cdms.ui.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.R
import app.i.cdms.data.model.Agent
import app.i.cdms.data.model.ChannelDetail
import app.i.cdms.data.model.CustomerChannel
import app.i.cdms.data.model.MyTeam
import app.i.cdms.repository.agent.AgentRepository
import app.i.cdms.repository.main.MainRepository
import app.i.cdms.repository.team.TeamRepository
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val agentRepository: AgentRepository,
    private val teamRepository: TeamRepository
) : ViewModel() {

    private val channelDetailList = mutableListOf<ChannelDetail>()
    private val _channelDetailListFlow = MutableStateFlow<List<ChannelDetail>>(listOf())
    val channelDetailListFlow = _channelDetailListFlow.asStateFlow()

    private val _myTeam = MutableStateFlow<MyTeam?>(null)
    val myTeam = _myTeam.asStateFlow()

    // 获取所有渠道详细信息
    fun getAllChannelDetail() {
        viewModelScope.launch {
            val customerType = listOf("personal", "business", "poizon")
            for (customer in customerType) {
                val result = mainRepository.getCustomerChannel(customer)
                result?.data?.sto?.forEach {
                    getChannelDetail(it)
                }
                result?.data?.yto?.forEach {
                    getChannelDetail(it)
                }
                result?.data?.jd?.forEach {
                    getChannelDetail(it)
                }
                result?.data?.dop?.forEach {
                    getChannelDetail(it)
                }
                result?.data?.jt?.forEach {
                    getChannelDetail(it)
                }
            }
            _channelDetailListFlow.value = channelDetailList
        }
    }

    // 获取渠道详细信息
    private suspend fun getChannelDetail(customerChannel: CustomerChannel) {
        val result = mainRepository.getCustomerChannelDetail(customerChannel.id)
        result?.data?.let {
            for (item in it) {
                val copy = item.copy(customerChannel = customerChannel)
                channelDetailList.add(copy)
            }
        }
    }

    // 获取我的所有下级
    fun getMyTeam(
        pageNum: Int, pageSize: Int, userName: String?,
        parentUserId: Int?,
        than: String?,
        balance: String?,
    ) {
        viewModelScope.launch {
            val result =
                teamRepository.getMyTeam(pageNum, pageSize, userName, parentUserId, than, balance)
            _myTeam.value = result
        }
    }

    // 更新/绑定渠道至用户
    fun updateChannel(
        firstCommission: Float,
        addCommission: Float,
        discountCommission: Float,
        perAddCommission: Float,
        agents: List<Agent>
    ) {
        EventBus.produceEvent(BaseEvent.Toast(R.string.channel_update_config_waiting))
        viewModelScope.launch {
            for (item in channelDetailList) {
                val result = agentRepository.getChannelConfigForAllUsers(item.channelId)
                result?.data ?: return@launch
                val channelConfigList = result.data

                val updateList = mutableListOf<Int>() // 原先存在，需要更新配置的用户
                val bindList = mutableListOf<Int>() // 原先没有，需要绑定配置的用户
                for (agent in agents) {
                    val channelConfig = channelConfigList.find {
                        it.userId == agent.userId && it.calcFeeType != null
                    }
                    if (channelConfig == null) {
                        bindList.add(agent.userId)
                    } else {
                        updateList.add(agent.userId)
                    }
                }

                val discount = item.discountPercent?.toFloat()?.plus(discountCommission)
                val perAdd = item.perAdd?.toFloat()?.plus(perAddCommission)
                if (updateList.size > 0) { // 更新配置
                    agentRepository.updateChildPrice(
                        firstCommission,
                        addCommission,
                        item.channelId,
                        item.customerChannel!!.id,
                        discount,
                        perAdd,
                        updateList
                    )
                }
                if (bindList.size > 0) { // 绑定配置
                    agentRepository.bindChannelToUser(
                        firstCommission,
                        addCommission,
                        item.channelId,
                        item.customerChannel!!.id,
                        discount,
                        perAdd,
                        bindList
                    )
                }
            }
            EventBus.produceEvent(BaseEvent.Toast(R.string.channel_update_config_finished))
        }
    }
}