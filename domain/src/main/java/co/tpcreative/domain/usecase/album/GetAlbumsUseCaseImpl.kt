package co.tpcreative.domain.usecase.album

import androidx.paging.PagedList
import co.tpcreative.domain.common.ResultState
import co.tpcreative.domain.common.transformer.FTransformer
import co.tpcreative.domain.common.transformer.STransformer
import co.tpcreative.domain.entity.Entity
import co.tpcreative.domain.repository.album.AlbumsRepository
import io.reactivex.Flowable

/**
 * Created by Dr.jacky on 9/14/2018.
 */
/**
 * Album use case implementation
 */
class GetAlbumsUseCaseImpl(
    private val transformerFlowable: FTransformer<ResultState<PagedList<Entity.Album>>>,
    private val transformerSingle: STransformer<ResultState<Int>>,
    private val transformerSingleList: STransformer<ResultState<List<Entity.Album>>>,
    private val repository: AlbumsRepository
) : GetAlbumsUseCase {

    /**
     * Get all of albums use case implementation
     */
    override fun getAlbums(): Flowable<ResultState<PagedList<Entity.Album>>> =
            repository.getAlbums()/*.compose(transformerFlowable)*/

    override fun deleteAlbum(album: Entity.Album) = repository.deleteAlbum(album).compose(transformerSingle)

    /*override fun loadAlbums(pageNumber: Int): Single<ResultState<List<Entity.Album>>> =
            repository.loadAlbums(pageNumber).compose(transformerSingleList)*/
}