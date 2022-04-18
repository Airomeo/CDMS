package app.i.cdms.ui.book

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import app.i.cdms.BuildConfig
import app.i.cdms.R
import app.i.cdms.data.model.BookBody
import app.i.cdms.data.model.BookResult
import app.i.cdms.data.model.Channel
import app.i.cdms.databinding.CatBottomsheetScrollableContentBinding
import app.i.cdms.databinding.DialogFillAddressBinding
import app.i.cdms.databinding.FragmentBookBinding
import app.i.cdms.ui.channel.ChannelRecyclerViewAdapter
import app.i.cdms.utils.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.elevation.SurfaceColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class BookFragment : Fragment(R.layout.fragment_book) {

    private val viewModel: BookViewModel by viewModels()
    private var _binding: FragmentBookBinding? = null
    private val binding get() = _binding!!
    private val listener = View.OnClickListener {
        with(binding) {
            when (it) {
                iv, tvName, tvAddress -> {
                    showFillAddressDialog(true)
                }
                iv2, tvName2, tvAddress2 -> {
                    showFillAddressDialog(false)
                }
                switcher -> {
                    it.animate().rotation(it.rotation + 180F)
                    viewModel.bookBodyChanged {
                        it.copy(
                            senderName = it.receiveName,
                            senderTel = it.receiveTel,
                            senderMobile = it.receiveMobile,
                            senderProvince = it.receiveProvince,
                            senderProvinceCode = it.receiveProvinceCode,
                            senderCity = it.receiveCity,
                            senderDistrict = it.receiveDistrict,
                            senderAddress = it.receiveAddress,
                            senderValues = it.receiveValues,
                            receiveName = it.senderName,
                            receiveTel = it.senderTel,
                            receiveMobile = it.senderMobile,
                            receiveProvince = it.senderProvince,
                            receiveProvinceCode = it.senderProvinceCode,
                            receiveCity = it.senderCity,
                            receiveDistrict = it.senderDistrict,
                            receiveAddress = it.senderAddress,
                            receiveValues = it.senderValues
                        )
                    }
                }
                clearAll -> {
                    initData()
                }
                dropdown -> {
                    it.animate().rotation(it.rotation + 180F)
                    val visibility = if (it.rotation % 360F > 0) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                    goodsName.visibility = visibility
                    goodsLength.visibility = visibility
                    goodsWidth.visibility = visibility
                    goodsHeight.visibility = visibility
                    guaranteeValueAmount.visibility = visibility
                    packageCount.visibility = visibility
                    goodsPrice.visibility = visibility
                }
                weightMinus -> {
                    var weight = tvGoodsWeight.text.toString().toIntOrNull() ?: 1
                    weight -= 1
                    if (weight < 1) {
                        weight = 1
                    }
                    tvGoodsWeight.setText(weight.toString())
                }
                weightPlus -> {
                    var weight = tvGoodsWeight.text.toString().toIntOrNull() ?: 1
                    weight += 1
                    tvGoodsWeight.setText(weight.toString())
                }
                clearTime -> {
                    it.animate().rotation(it.rotation + 180F)
                    tvPickUpTime.text = null
                }
                price, channelName, channelTips -> {
                    val list = viewModel.smartPreOrderChannels.map { it.uiChannel }
                    showBookChannelListDialogs(list)
                }
                book -> {
                    viewModel.book()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentBookBinding.bind(view)
        with(binding) {
            iv.setOnClickListener(listener)
            tvName.setOnClickListener(listener)
            tvAddress.setOnClickListener(listener)
            iv2.setOnClickListener(listener)
            tvName2.setOnClickListener(listener)
            tvAddress2.setOnClickListener(listener)
            switcher.setOnClickListener(listener)
            clearAll.setOnClickListener(listener)
            if (BuildConfig.DEBUG) {
                clearAll.setOnLongClickListener {
                    populateData()
                    true
                }
            }
            dropdown.setOnClickListener(listener)
            weightMinus.setOnClickListener(listener)
            weightPlus.setOnClickListener(listener)
            clearTime.setOnClickListener(listener)
            price.setOnClickListener(listener)
            channelName.setOnClickListener(listener)
            channelTips.setOnClickListener(listener)
            book.setOnClickListener(listener)
            nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY - oldScrollY > 0) {
                    if (!bottomAppBar.isScrolledDown) {
                        requireActivity().window.navigationBarColor =
                            SurfaceColors.SURFACE_0.getColor(requireContext())
                    }
                } else {
                    if (!bottomAppBar.isScrolledUp) {
                        requireActivity().window.navigationBarColor =
                            SurfaceColors.SURFACE_2.getColor(requireContext())
                    }
                }
            }
            tvGoodsWeight.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.cat_exposed_dropdown_popup_item,
                    resources.getIntArray(R.array.cat_goods_weight).toTypedArray()
                )
            )
            tvGoodsName.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.cat_exposed_dropdown_popup_item,
                    resources.getStringArray(R.array.cat_goods_type)
                )
            )
            tvPackageCount.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.cat_exposed_dropdown_popup_item,
                    resources.getIntArray(R.array.cat_package_quantity).toTypedArray()
                )
            )
            tvGuaranteeValueAmount.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.cat_exposed_dropdown_popup_item,
                    resources.getIntArray(R.array.cat_goods_price).toTypedArray()
                )
            )
            tvGoodsPrice.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.cat_exposed_dropdown_popup_item,
                    resources.getIntArray(R.array.cat_goods_price).toTypedArray()
                )
            )
            tvPickUpTime.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.cat_exposed_dropdown_popup_item,
                    getTimeArray()
                )
            )
            tvNote.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.cat_exposed_dropdown_popup_item,
                    resources.getStringArray(R.array.cat_book_note)
                )
            )

            tvGoodsWeight.doOnTextChanged { text, start, before, count ->
                viewModel.bookBodyChanged {
                    it.copy(weight = text.toString().toIntOrNull())
                }
            }
            tvGoodsName.doOnTextChanged { text, start, before, count ->
                viewModel.bookBodyChanged {
                    it.copy(goods = if (text.isNullOrBlank()) null else text.toString())
                }
            }
            tvGoodsLength.doOnTextChanged { text, start, before, count ->
                viewModel.bookBodyChanged {
                    it.copy(vloumLong = if (text.isNullOrBlank()) null else text.toString())
                }
            }
            tvGoodsWidth.doOnTextChanged { text, start, before, count ->
                viewModel.bookBodyChanged {
                    it.copy(vloumWidth = if (text.isNullOrBlank()) null else text.toString())
                }
            }
            tvGoodsHeight.doOnTextChanged { text, start, before, count ->
                viewModel.bookBodyChanged {
                    it.copy(vloumHeight = if (text.isNullOrBlank()) null else text.toString())
                }
            }
            tvGuaranteeValueAmount.doOnTextChanged { text, start, before, count ->
                viewModel.bookBodyChanged {
                    it.copy(guaranteeValueAmount = if (text.isNullOrBlank()) null else text.toString())
                }
            }
            tvGoodsPrice.doOnTextChanged { text, start, before, count ->
                viewModel.bookBodyChanged {
                    it.copy(unitPrice = text.toString().toIntOrNull())
                }
            }
            tvPackageCount.doOnTextChanged { text, start, before, count ->
                viewModel.bookBodyChanged {
                    // qty暂时用packageCount这个替代
                    it.copy(
                        packageCount = text.toString().toIntOrNull(),
                        qty = text.toString().toIntOrNull()
                    )
                }
            }
            tvPickUpTime.doOnTextChanged { text, start, before, count ->
                if (text.isNullOrBlank()) {
                    clearTime.visibility = View.INVISIBLE
                    return@doOnTextChanged
                }
                clearTime.visibility = View.VISIBLE
                val dateStr = text.substring(0, 11)
                val startTimeStr = text.substring(11, 16) + ":00"
                val endTimeStr = text.substring(17, 22) + ":00"
                viewModel.bookBodyChanged {
                    it.copy(
                        dateTime = text.toString(),
                        pickUpStartTime = dateStr + startTimeStr,
                        pickUpEndTime = dateStr + endTimeStr
                    )
                }
            }

            tvNote.doOnTextChanged { text, start, before, count ->
                viewModel.bookBodyChanged {
                    it.copy(remark = if (text.isNullOrBlank()) null else text.toString())
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiEvent.collectLatest { event ->
                        when (event) {
                            is BookViewModel.Event.ShowBookResult -> {
                                // 显示下单结果
                                event.result ?: return@collectLatest
                                showBookResultDialog(event.result)
                            }
                            is BookViewModel.Event.BookInfoIncomplete -> {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.book_info_incomplete,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                launch {
                    viewModel.selectedPreOrderChannel.collectLatest { preOrderChannel ->
                        if (preOrderChannel == null) {
                            // 没有可用渠道 或 信息不全没选择渠道。提示文字
                            binding.price.text = getString(R.string.book_price, "-")
                            binding.channelName.visibility = View.GONE
                            binding.channelName.text = null
                            binding.channelTips.text =
                                if (viewModel.bookBody.isReadyForPreOrder) {
                                    getString(R.string.book_tips_not_found)
                                } else {
                                    getString(R.string.book_price_tips_not_filled)
                                }
                        } else {
                            binding.price.text =
                                getString(R.string.book_price, preOrderChannel.preOrderFee)
                            binding.channelName.visibility = View.VISIBLE
                            binding.channelName.text = preOrderChannel.channelName
                            binding.channelTips.text = getString(R.string.book_tips_selectable)
                        }
                    }
                }
            }
        }
        initData()
    }

    // 初始化数据
    private fun initData() {
        with(binding) {
            viewModel.bookBodyChanged { BookBody() }

            tvName.setText(R.string.book_info_from)
            tvAddress.setText(R.string.book_address_tips)
            tvName2.setText(R.string.book_info_to)
            tvAddress2.setText(R.string.book_address_tips)
            tvGoodsWeight.setText("1")
            tvGoodsName.setText(resources.getStringArray(R.array.cat_goods_type).first())
            tvPackageCount.setText("1")
            tvGoodsLength.text = null
            tvGoodsWidth.text = null
            tvGoodsHeight.text = null
            tvGuaranteeValueAmount.text = null
            tvGoodsPrice.setText(resources.getIntArray(R.array.cat_goods_price).last().toString())
            tvPickUpTime.text = null
            tvNote.text = null
        }
    }

    // 填充数据，测试用
    private fun populateData() {
        with(binding) {
            viewModel.bookBodyChanged {
                BookBody(
                    receiveAddress = "乐成米兰城3楼南大街372(到付拒收)",
                    receiveCity = "石家庄市",
                    receiveDistrict = "长安区",
                    receiveMobile = "13333333333",
                    receiveName = "小白",
                    receiveProvince = "河北省",
                    receiveProvinceCode = "130000",
                    receiveTel = null,
                    receiveValues = listOf("130000", "130100", "130102"),
                    senderAddress = "翠福园40号楼二单元802室",
                    senderCity = "北京市",
                    senderDistrict = "通州区",
                    senderMobile = "17777777777",
                    senderName = "小黄",
                    senderProvince = "北京市",
                    senderProvinceCode = "110000",
                    senderTel = null,
                    senderValues = listOf("110000", "110100", "110112"),
                )
            }
            tvName.text = "小黄,17777777777"
            tvAddress.text = "北京市北京市通州区翠福园40号楼二单元802室"
            tvName2.text = "小白,13333333333"
            tvAddress2.text = "河北省石家庄市长安区乐成米兰城3楼南大街372(到付拒收)"
            tvGoodsWeight.setText("1")
            tvGoodsName.setText(resources.getStringArray(R.array.cat_goods_type).first())
            tvPackageCount.setText("1")
            tvGoodsLength.text = null
            tvGoodsWidth.text = null
            tvGoodsHeight.text = null
            tvGuaranteeValueAmount.text = null
            tvGoodsPrice.setText(resources.getIntArray(R.array.cat_goods_price).last().toString())
            tvPickUpTime.text = null
            tvNote.text = null
        }
    }

    /**
     * 选择省市区窗口
     *
     * @param view: The view which popup attach to
     * @return
     */
    private fun showMenu(view: View) {
        viewModel.areaList?.let {
            val popup = PopupMenu(requireContext(), view)
            for (a in it) {
                if (a.children == null) {
                    popup.menu.add(Menu.NONE, a.value.toInt(), Menu.NONE, a.label)
                } else {
                    val aMenu =
                        popup.menu.addSubMenu(Menu.NONE, a.value.toInt(), Menu.NONE, a.label)
                    for (b in a.children) {
                        if (b.children == null) {
                            aMenu.add(Menu.NONE, b.value.toInt(), Menu.NONE, b.label)
                        } else {
                            val bMenu =
                                aMenu.addSubMenu(Menu.NONE, b.value.toInt(), Menu.NONE, b.label)
                            for (c in b.children) {
                                if (c.children == null) {
                                    bMenu.add(Menu.NONE, c.value.toInt(), Menu.NONE, c.label)
                                } else {
                                    val cMenu =
                                        bMenu.addSubMenu(
                                            Menu.NONE,
                                            c.value.toInt(),
                                            Menu.NONE,
                                            c.label
                                        )
                                    for (d in c.children) {
                                        cMenu.add(
                                            Menu.NONE,
                                            d.value.toInt(),
                                            Menu.NONE,
                                            d.label
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            val edit = view as EditText
            var str = ""
            val list = mutableListOf<Pair<String, String>>()
            popup.setOnMenuItemClickListener { item ->
                str += item.title.toString()
                list.add(Pair(item.title.toString(), item.itemId.toString()))
                if (!item.hasSubMenu()) {
                    edit.setText(str)
                    edit.tag = list
                }
                true
            }
            popup.show()
        }
    }

    /**
     * 显示填写地址窗口
     *
     * @param isFrom: 寄件人true，收件人false
     * @return
     */
    private fun showFillAddressDialog(isFrom: Boolean) {
        val b = DialogFillAddressBinding.inflate(layoutInflater, null, false)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(b.root)
        dialog.setOnDismissListener {
            viewModel.clearParsedAddressBySf()
        }
        dialog.dismissWithAnimation = true

        // 如果是之前填好了信息，重新点进去的，就从viewModel读取并填充数据
        if (isFrom) {
            b.title.setText(R.string.book_info_from)
            viewModel.bookBody.senderName?.let {
                b.name.editText?.setText(it)
            }
            viewModel.bookBody.senderMobile?.let {
                b.phone.editText?.setText(it)
            }
            viewModel.bookBody.senderTel?.let {
                b.phone.editText?.setText(it)
            }
            val provinceName = viewModel.bookBody.senderProvince
            val cityName = viewModel.bookBody.senderCity
            val countyName = viewModel.bookBody.senderDistrict
            val values = viewModel.bookBody.senderValues
            if (provinceName != null && cityName != null && countyName != null && values != null) {
                b.provinceMenu.setText(provinceName + cityName + countyName)
                b.provinceMenu.tag = listOf(
                    Pair(provinceName, values[0]),
                    Pair(cityName, values[1]),
                    Pair(countyName, values[2])
                )
            }
            viewModel.bookBody.senderAddress?.let {
                b.address.editText?.setText(it)
            }
        } else {
            b.title.setText(R.string.book_info_to)
            viewModel.bookBody.receiveName?.let {
                b.name.editText?.setText(it)
            }
            viewModel.bookBody.receiveMobile?.let {
                b.phone.editText?.setText(it)
            }
            viewModel.bookBody.receiveTel?.let {
                b.phone.editText?.setText(it)
            }
            val provinceName = viewModel.bookBody.receiveProvince
            val cityName = viewModel.bookBody.receiveCity
            val countyName = viewModel.bookBody.receiveDistrict
            val values = viewModel.bookBody.receiveValues
            if (provinceName != null && cityName != null && countyName != null && values != null) {
                b.provinceMenu.setText(provinceName + cityName + countyName)
                b.provinceMenu.tag = listOf(
                    Pair(provinceName, values[0]),
                    Pair(cityName, values[1]),
                    Pair(countyName, values[2])
                )
            }
            viewModel.bookBody.receiveAddress?.let {
                b.address.editText?.setText(it)
            }
        }

        b.back.setOnClickListener { dialog.dismiss() }
        b.done.setOnClickListener {
            val name = b.name.editText?.text.toString()
            val phone = b.phone.editText?.text.toString()
            val province = b.provinceMenu.text.toString()
            val provinceTag = b.provinceMenu.tag
            val address = b.address.editText?.text.toString()

            if (name.isBlank() || phone.isBlank() || province.isBlank() || provinceTag == null || address.isBlank()) {
                val str = getString(R.string.book_address_incomplete, b.title.text)
                Toast.makeText(requireContext(), str, Toast.LENGTH_SHORT).show()
            } else {
                var mobile: String? = null
                var tel: String? = null
                if (phone.startsWith("0") || phone.contains("-")) {
                    tel = phone
                } else {
                    mobile = phone
                }

                val pairedAreaList = provinceTag as List<Pair<String, String>>
                val provinceName = pairedAreaList[0].first
                val provinceCode = pairedAreaList[0].second
                val cityName = pairedAreaList[1].first
                val cityCode = pairedAreaList[1].second
                val countyName = pairedAreaList[2].first
                val countyCode = pairedAreaList[2].second
                // ["440000", "441300", "441322"],
                val areaCodeList = listOf(provinceCode, cityCode, countyCode)

                if (isFrom) {
                    viewModel.bookBodyChanged {
                        it.copy(
                            senderName = name,
                            senderTel = tel,
                            senderMobile = mobile,
                            senderProvince = provinceName,
                            senderProvinceCode = provinceCode,
                            senderCity = cityName,
                            senderDistrict = countyName,
                            senderAddress = address,
                            senderValues = areaCodeList
                        )
                    }
                    binding.tvName.text = viewModel.bookBody.getSenderNameAndPhoneOrNull()
                        ?: getString(R.string.book_info_from)
                    binding.tvAddress.text = viewModel.bookBody.getSenderAddressOrNull()
                        ?: getString(R.string.book_address_tips)
                } else {
                    viewModel.bookBodyChanged {
                        it.copy(
                            receiveName = name,
                            receiveTel = tel,
                            receiveMobile = mobile,
                            receiveProvince = provinceName,
                            receiveProvinceCode = provinceCode,
                            receiveCity = cityName,
                            receiveDistrict = countyName,
                            receiveAddress = address,
                            receiveValues = areaCodeList
                        )
                    }
                    binding.tvName2.text = viewModel.bookBody.getReceiverNameAndPhoneOrNull()
                        ?: getString(R.string.book_info_to)
                    binding.tvAddress2.text = viewModel.bookBody.getReceiverAddressOrNull()
                        ?: getString(R.string.book_address_tips)
                }
                dialog.dismiss()
            }
        }
        b.parse.setOnClickListener {
            val rawAddress = b.rawAddress.text.toString().trim()
            if (rawAddress.length > 20) {
                viewModel.parseAddress(rawAddress)
                dialog.hideKeyboard()
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.book_fill_address_parse_lack_info,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        b.pasteOrClear.setOnClickListener {
            it.animate().rotation(it.rotation + 360F)
            if (b.auto.editText?.text.isNullOrBlank()) {
                val clipboard =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.primaryClip?.let { clip ->
                    b.auto.editText!!.setText(clip.getItemAt(0).text)
                    b.parse.callOnClick()
                }
            } else {
                b.auto.editText?.text = null
            }
        }
        b.auto.editText?.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrBlank()) {
                b.pasteOrClear.setImageResource(R.drawable.ic_baseline_content_paste_24)
            } else {
                b.pasteOrClear.setImageResource(R.drawable.ic_baseline_close_24)
            }
        }
        b.provinceMenu.apply {
            showSoftInputOnFocus = false
            setOnClickListener { showMenu(it) }
            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    v.performClick()
                    v.hideKeyboard()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.parsedAddressBySf.collectLatest { result ->
                result ?: return@collectLatest
                if (result.isEmpty()) {
                    val res = if (isFrom) {
                        R.string.book_info_from
                    } else {
                        R.string.book_info_to
                    }
                    val str = getString(R.string.book_fill_address_parse_failed, getString(res))
                    Toast.makeText(requireContext(), str, Toast.LENGTH_SHORT).show()
                    return@collectLatest
                } else {
                    result[0].apply {
                        b.name.editText?.setText(personalName)
                        if (!mobile.isNullOrBlank()) {
                            b.phone.editText?.setText(mobile)
                        } else if (!telephone.isNullOrBlank()) {
                            b.phone.editText?.setText(telephone)
                        }
                        if (originDestRegions.size < 3) {// 空、只有省、只有省市，都不行，必须精确到区
                            val str = getString(
                                R.string.book_fill_address_parse_failed,
                                getString(R.string.book_fill_address_province)
                            )
                            Toast.makeText(requireContext(), str, Toast.LENGTH_SHORT).show()
                        } else {
                            val provinceName = originDestRegions[0].name
                            val cityName = originDestRegions[1].name
                            val countyName = originDestRegions[2].name
                            // subString from "A610112000"
                            val provinceCode = originDestRegions[2].code.substring(1, 3) + "0000"
                            val cityCode = originDestRegions[2].code.substring(1, 5) + "00"
                            val countyCode = originDestRegions[2].code.substring(1, 7)
                            b.provinceMenu.setText(provinceName + cityName + countyName)
                            b.provinceMenu.tag = listOf(
                                Pair(provinceName, provinceCode),
                                Pair(cityName, cityCode),
                                Pair(countyName, countyCode)
                            )
                        }
                        b.address.editText?.setText(site)
                    }
                }
            }
        }
        dialog.show()
    }

    /**
     * 显示所有可用的渠道，供用户下单选择
     *
     * @param list: 渠道信息列表
     * @return
     */
    private fun showBookChannelListDialogs(list: List<Channel>?) {
        if (list.isNullOrEmpty()) return
        val b = CatBottomsheetScrollableContentBinding.inflate(layoutInflater, null, false)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(b.root)
        dialog.dismissWithAnimation = true

        val rootOnClickCallback: (channel: Channel) -> Unit = { channel ->
            val channelId =
                viewModel.smartPreOrderChannels.find { it.uiChannel == channel }!!.channelId
            viewModel.bookBodyChanged {
                it.copy(
                    deliveryType = channel.deliveryType,
                    channelId = channelId
                )
            }
            dialog.dismiss()
        }
        val mAdapter = ChannelRecyclerViewAdapter(rootOnClickCallback)
        mAdapter.submitList(list)
        b.recyclerView.adapter = mAdapter
        b.recyclerView.layoutManager = LinearLayoutManager(context)
        dialog.show()
    }

    /**
     * 显示下单结果
     *
     * @param bookResult: 下单结果，包含快递单号等信息
     * @return
     */
    private fun showBookResultDialog(bookResult: BookResult) {
        val deliveryIdStr = bookResult.deliveryId ?: getString(R.string.fees_tips_get_delivery_id)
        val brand = when {
            bookResult.orderNo.startsWith("YT") -> "圆通"
            bookResult.orderNo.startsWith("ST") -> "申通"
            bookResult.orderNo.startsWith("DB") -> "德邦"
            bookResult.orderNo.startsWith("JT") -> "极兔"
            bookResult.orderNo.startsWith("SF") -> "顺丰"
            bookResult.orderNo.startsWith("JD") -> "京东"
            else -> ""
        }
        var msg = "${brand}单号：${deliveryIdStr}\n" +
                "寄件人：${binding.tvName.text}\n" +
                "寄件地址：${binding.tvAddress.text}\n" +
                "收件人：${binding.tvName2.text}\n" +
                "收件地址：${binding.tvAddress2.text}\n" +
                "下单物品：${binding.tvGoodsName.text}\n" +
                "下单重量：${binding.tvGoodsWeight.text}kg\n" +
                "重要提示：超重需补差价；未保价最高赔付运费6倍；售后问题联系\"下单员\"或致电：4000-563-365。"
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.book_result_dialog_title)
            .setMessage(msg)
            .setPositiveButton(R.string.common_copy) { dialog, which ->
                // copy
                val clipboard =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("simple text", msg)
                // Set the clipboard's primary clip.
                clipboard.setPrimaryClip(clip)
                dialog.dismiss()
            }
            .create()
        dialog.setOnShowListener {
            if (bookResult.deliveryId == null) {
                // 极兔和部分圆通需要手动刷新获取，添加刷新按钮
                val neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                neutralButton.setText(R.string.fees_tips_refresh)
                neutralButton.setOnClickListener {
                    val deliverType =
                        if (bookResult.orderNo.startsWith("YT")) "YTO" else "JT"
                    viewModel.getDeliveryId(
                        bookResult.orderNo,
                        deliverType
                    )
                }
                neutralButton.visibility = View.VISIBLE
                val job = viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deliveryId.collectLatest {
                        it ?: return@collectLatest
                        msg = msg.replace(deliveryIdStr, it)
                        dialog.setMessage(msg)
                        neutralButton.visibility = View.GONE
                    }
                }
                dialog.setOnDismissListener {
                    job.cancel()
                }
            }
        }
        dialog.show()
    }

    private fun getTimeArray(): List<String> {
        val timeArray = ArrayList<String>()
        val startFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val endFormatter = DateTimeFormatter.ofPattern("-HH:mm")
        var time = LocalDateTime.now()
        var startHour = if (time.hour < 9) 9 else time.hour
        for (d in 1..3) {
            for (i in startHour..20) {
                val start = time.withHour(i).withMinute(0).format(startFormatter)
                val end = time.withHour(i + 1).withMinute(0).format(endFormatter)
                timeArray.add(start + end)
            }
            time = time.plusDays(1)
            startHour = 9
        }
        return timeArray
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.navigationBarColor =
            SurfaceColors.SURFACE_0.getColor(requireContext())
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.navigationBarColor =
            SurfaceColors.SURFACE_2.getColor(requireContext())
    }
}