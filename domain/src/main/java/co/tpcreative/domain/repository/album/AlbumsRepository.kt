package co.tpcreative.domain.repository.album

import androidx.paging.PagedList
import co.tpcreative.domain.common.ResultState
import co.tpcreative.domain.entity.Entity
import co.tpcreative.domain.repository.BaseRepository
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Dr.jacky on 9/23/2018.
 */
/**
 * Album repository
 */
interface AlbumsRepository : BaseRepository {

    /**
     * Perform
     */
    fun getAlbums(): Flowable<ResultState<PagedList<Entity.Album>>>

    fun deleteAlbum(album: Entity.Album): Single<ResultState<Int>>

    //fun loadAlbums(pageNumber: Int): Single<ResultState<List<Entity.Album>>>
}