package co.tpcreative.domain.usecase.album

import androidx.paging.PagedList
import co.tpcreative.domain.common.ResultState
import co.tpcreative.domain.entity.Entity
import co.tpcreative.domain.usecase.BaseUseCase
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Dr.jacky on 10/7/2018.
 */
/**
 * Album use case
 */
interface GetAlbumsUseCase : BaseUseCase {

    /**
     * Get all of albums use case
     */
    fun getAlbums(): Flowable<ResultState<PagedList<Entity.Album>>>

    fun deleteAlbum(album: Entity.Album): Single<ResultState<Int>>

    //fun loadAlbums(pageNumber: Int): Single<ResultState<List<Entity.Album>>>
}