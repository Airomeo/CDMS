package app.i.cdms.ui.orderlist

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import app.i.cdms.R
import app.i.cdms.data.model.OrderItem
import app.i.cdms.data.model.OrderState
import app.i.cdms.data.model.RawOrder
import app.i.cdms.data.model.RawOrderJsonAdapter
import app.i.cdms.ui.theme.AppTheme
import com.squareup.moshi.Moshi
import dagger.hilt.android.AndroidEntryPoint

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class OrderFragment : Fragment() {

    private val viewModel: OrderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme(context = context) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "OrderListScreen") {
                        composable("OrderListScreen") {
                            OrderListScreen(
                                viewModel = viewModel,
                                navController = navController,
                                onBackPressed = { findNavController().popBackStack() }
                            )
                        }
                        composable("OrderDetailScreen/{data}") {
                            val data = it.arguments?.getString("data")
                            val json = Base64.decode(data, Base64.URL_SAFE).toString(Charsets.UTF_8)
                            val rawOrder =
                                RawOrderJsonAdapter(Moshi.Builder().build()).fromJson(json)
                            OrderDetailScreen(rawOrder = rawOrder!!, navController = navController)
                        }
                        /*...*/
                    }
                }
            }
        }
    }
}

@Composable
fun TopAppBar(
    topAppBarState: OrderViewModel.TopAppBarState,
    keywords: String,
    onValueChange: (String) -> Unit,
    onClosePressed: () -> Unit,
    onBackPressed: () -> Unit,
    onSearchTriggered: () -> Unit,
    onTipsTriggered: () -> Unit,
) {
    SmallTopAppBar(
        title = {
            when (topAppBarState) {
                OrderViewModel.TopAppBarState.DEFAULT -> {
                    Text(text = stringResource(id = R.string.title_order_list))
                }
                OrderViewModel.TopAppBarState.SEARCH -> {
                    TextField(
                        value = keywords,
                        onValueChange = { onValueChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                modifier = Modifier.alpha(0.5F),
                                text = stringResource(id = R.string.action_search),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )
                        },
                        textStyle = TextStyle(fontSize = MaterialTheme.typography.titleMedium.fontSize),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                onValueChange(keywords)
                            }
                        ),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            disabledTextColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                if (keywords.isNotEmpty()) {
                    onValueChange("")
                    onClosePressed()
                } else {
                    onBackPressed()
                }
            }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            // RowScope here, so these icons will be placed horizontally
            when (topAppBarState) {
                OrderViewModel.TopAppBarState.DEFAULT -> {
                    IconButton(onClick = onSearchTriggered) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onTipsTriggered) {
                        Icon(Icons.Default.Info, contentDescription = "Tips")
                    }
                }
                OrderViewModel.TopAppBarState.SEARCH -> {
                    IconButton(onClick = {
                        if (keywords.isNotEmpty()) {
                            onValueChange("")
                        } else {
                            onClosePressed()
                        }
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear and close")
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    viewModel: OrderViewModel,
    navController: NavController,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    viewModel.uiState.messages.firstOrNull()?.let { message ->
//        LaunchedEffect(message) {
        val text = when (message) {
            is Int -> {
                // Resources.NotFoundException – Throws NotFoundException if the given ID does not exist.
                stringResource(id = message)
            }
            is String -> {
                message
            }
            else -> {
                "Unsupported message type"
            }
        }
        if (text.length > 30) {
            MessageDialog(
                msg = text,
                onDismiss = {
                    viewModel.dialogMessageShown(message)
                },
                onPositive = {
                    // copy
                    val clipboard =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText("simple text", "asd")
                    // Set the clipboard's primary clip.
                    clipboard.setPrimaryClip(clip)
                    viewModel.dialogMessageShown(message)
                })
        } else {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            viewModel.dialogMessageShown(message)
        }
//        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                topAppBarState = viewModel.uiState.topAppBarState,
                keywords = viewModel.uiState.keywords,
                onValueChange = { viewModel.search(it) },
                onClosePressed = { viewModel.updateTopAppBarState(OrderViewModel.TopAppBarState.DEFAULT) },
                onBackPressed = onBackPressed,
                onSearchTriggered = { viewModel.updateTopAppBarState(OrderViewModel.TopAppBarState.SEARCH) },
                onTipsTriggered = { viewModel.showMessage(R.string.order_tips) }
            )
        }
    ) {
        Column(
            Modifier.padding(it)
        ) {
            val tabs = listOf(
                stringResource(id = R.string.sto) to "STO-INT",
                stringResource(id = R.string.yto) to "YTO",
                stringResource(id = R.string.jt) to "JT",
                stringResource(id = R.string.dpk) to "DOP",
                stringResource(id = R.string.jd) to "JD",
                stringResource(id = R.string.zto) to "ZTO",
                stringResource(id = R.string.sf) to "SF",
                stringResource(id = R.string.yd) to "YUND",
            )
            ScrollableTabRow(selectedTabIndex = viewModel.uiState.tabIndex) {
                tabs.forEachIndexed { index, pair ->
                    Tab(
                        text = { Text(pair.first) },
                        selected = viewModel.uiState.tabIndex == index,
                        onClick = {
                            viewModel.updateTabIndex(index, pair.second)
                        }
                    )
                }
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            ) {
                items(viewModel.uiState.filteredRawOrderList) { rawOrder ->
                    Box() {
                        var expanded by remember { mutableStateOf(false) }
                        var dpOffset by remember { mutableStateOf(DpOffset(0.dp, 0.dp)) }
                        OrderListItem(
                            order = rawOrder.orderItem,
                            onNavigate = {
                                val json =
                                    RawOrderJsonAdapter(Moshi.Builder().build()).toJson(
                                        rawOrder
                                    )
                                val data =
                                    Base64.encodeToString(
                                        json.toByteArray(),
                                        Base64.URL_SAFE
                                    )
                                navController.navigate("OrderDetailScreen/$data")
                            },
                            onLongPress = { offset ->
                                dpOffset = DpOffset(offset.x, 0.dp)
                                expanded = true
                            })
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            offset = dpOffset
                        ) {
                            if (rawOrder.orderStatus == "1") {
                                // 待取件，显示取消订单
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(id = R.string.order_cancel)) },
                                    onClick = {
                                        viewModel.batchCancelOrder(
                                            deliveryType = rawOrder.deliveryType!!,
                                            deliveryIds = listOf(rawOrder.deliveryId)
                                        )
                                        expanded = false
                                    })
                            } else if (rawOrder.weightState == "2") {
                                // 超重，显示处理订单
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(id = R.string.order_overweight_do)) },
                                    onClick = {
                                        viewModel.updateOrderWeightState(
                                            deliveryType = rawOrder.deliveryType!!,
                                            orderIds = listOf(rawOrder.id)
                                        )
                                        expanded = false
                                    })
                            } else {
                                expanded = false
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun OrderListItem(
    order: OrderItem,
    onNavigate: () -> Unit,
    onLongPress: (DpOffset) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
//            .border(1.dp, Color.Red, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(if (order.weightState == "2") MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant)
            .pointerInput(order) {
                detectTapGestures(
                    onPress = { /* Called when the gesture starts */ },
                    onDoubleTap = { /* Called on Double Tap */ },
                    onLongPress = { onLongPress(DpOffset(it.x.toDp(), it.y.toDp())) },
                    onTap = { onNavigate() }
                )
            }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = order.deliveryId,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp, 20.dp),
                onClick = {
                    // copy
                    val clipboard =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData =
                        ClipData.newPlainText("simple text", order.deliveryId)
                    // Set the clipboard's primary clip.
                    clipboard.setPrimaryClip(clip)
                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_content_copy_24),
                    contentDescription = "Localized description",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.weight(1F))
            Text(
                text = order.userName,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = order.senderName,
                modifier = Modifier
                    .weight(1F)
                    .padding(start = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = when (order.orderState) {
                    OrderState.WAITING -> stringResource(R.string.state_waiting)
                    OrderState.DELIVERING -> stringResource(R.string.state_delivering)
                    OrderState.RECEIVED -> stringResource(R.string.state_received)
                    OrderState.ERROR -> stringResource(R.string.state_error)
                    OrderState.CANCELED -> stringResource(R.string.state_canceled)
                },
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = order.receiverName,
                modifier = Modifier
                    .weight(1F)
                    .padding(end = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = order.senderAddress,
                modifier = Modifier
                    .weight(1F)
                    .padding(start = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = ">>>>>>>",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = order.receiverAddress,
                modifier = Modifier
                    .weight(1F)
                    .padding(end = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text =
            if (order.weightState == "2") {
                stringResource(id = R.string.order_overweight_todo)
            } else {
                order.orderStateDetails
                    ?: stringResource(id = R.string.state_waiting_assign)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(rawOrder: RawOrder, navController: NavController) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(id = R.string.title_order_details)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) {
        SelectionContainer() {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(it)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.order_info),
                    modifier = Modifier.padding(start = 0.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(id = R.string.order_courier, rawOrder.deliveryId),
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(id = R.string.order_system, rawOrder.orderNo),
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(id = R.string.order_username, rawOrder.userName),
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(id = R.string.order_time, rawOrder.createTime),
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(id = R.string.order_customer, rawOrder.channelName),
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(id = R.string.order_info_from),
                    modifier = Modifier.padding(start = 0.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = rawOrder.senderName + (rawOrder.senderMobile
                        ?: rawOrder.senderTel) + "\n" + rawOrder.senderAddress,
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(id = R.string.order_info_to),
                    modifier = Modifier.padding(start = 0.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = rawOrder.receiveName + (rawOrder.receiveMobile
                        ?: rawOrder.receiveTel) + "\n" + rawOrder.receiveAddress,
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(id = R.string.order_info_goods),
                    modifier = Modifier.padding(start = 0.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(id = R.string.order_goods, rawOrder.goods),
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(
                        id = R.string.order_package_count,
                        rawOrder.packageCount
                    ),
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(id = R.string.order_weight, rawOrder.weight),
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(
                        id = R.string.order_weight_actual,
                        rawOrder.realWeight ?: ""
                    ),
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(id = R.string.order_volume, rawOrder.volume),
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(id = R.string.order_volume_actual, rawOrder.volume),
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(id = R.string.order_info_logistics),
                    modifier = Modifier.padding(start = 0.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = when (rawOrder.orderStatus) {
                        "1" -> stringResource(R.string.state_waiting)
                        "2" -> stringResource(R.string.state_delivering)
                        "3" -> stringResource(R.string.state_received)
                        "6" -> stringResource(R.string.state_error)
                        "10" -> stringResource(R.string.state_canceled)
                        else -> ""
                    } + "\n" + (rawOrder.realOrderState
                        ?: stringResource(id = R.string.state_waiting_assign)),
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun MessageDialog(
    msg: String,
    onDismiss: () -> Unit,
    onPositive: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onPositive) {
                Text(text = stringResource(id = R.string.common_copy))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.dialog_negative_text))
            }
        },
        text = { Text(text = msg) })
}