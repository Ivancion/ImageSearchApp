package com.example.imagesearchapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.imagesearchapp.api.UnsplashApi
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE = 1

class UnsplashPagingSource(
    private val unsplashApi: UnsplashApi,
    private val query: String
) : PagingSource<Int, UnsplashPhoto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val position = params.key ?: STARTING_PAGE

            return try {
                val searchResponse = unsplashApi.searchPhotos(query, position, params.loadSize)
                val photos = searchResponse.results

                LoadResult.Page(
                    data = photos,
                    prevKey = if (position == STARTING_PAGE) null else position - 1,
                    nextKey = if (photos.isEmpty()) null else position + 1
                )
            } catch (exception: IOException) {
                LoadResult.Error(exception)
            } catch (exception: HttpException) {
                LoadResult.Error(exception)
            }
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashPhoto>): Int? {
        TODO("Not yet implemented")
    }

}