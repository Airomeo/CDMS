package app.i.cdms.repository.book

import app.i.cdms.data.model.BookBody
import app.i.cdms.data.model.CompareFeeBody
import app.i.cdms.data.model.ParsedAddressByJd
import app.i.cdms.data.model.YunYangBaseResponse
import app.i.cdms.data.source.remote.book.BookDataSource
import app.i.cdms.repository.BaseRepository
import javax.inject.Inject

/**
 * @author ZZY
 * 2022/02/13.
 */
class BookRepository @Inject constructor(private val dataSource: BookDataSource) :
    BaseRepository() {

    suspend fun parseAddressByJd(rawAddress: String): YunYangBaseResponse<ParsedAddressByJd>? {
        return executeResponse { dataSource.parseAddressByJd(rawAddress) }
    }

    suspend fun parseAddressBySf(rawAddress: String) =
        executeResponse { dataSource.parseAddressBySf(rawAddress) }

    suspend fun fetchSmartPreOrderChannels(bookBody: BookBody) =
        executeResponse { dataSource.fetchSmartPreOrderChannels(bookBody) }

    suspend fun submitOrder(bookBody: BookBody) =
        executeResponse { dataSource.submitOrder(bookBody) }

    suspend fun fetchCompareFee(compareFeeBody: CompareFeeBody) =
        executeResponse { dataSource.fetchCompareFee(compareFeeBody) }

    suspend fun getDeliveryId(orderNo: String, deliveryType: String) =
        executeResponse { dataSource.getDeliveryId(orderNo, deliveryType) }
}