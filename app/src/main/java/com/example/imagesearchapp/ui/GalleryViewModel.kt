package com.example.imagesearchapp.ui

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.imagesearchapp.data.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(private val repository: UnsplashRepository): ViewModel() {

    var photos = repository.getStartingResults("popular").cachedIn(viewModelScope)

    fun searchPhotos(query: String) {
        photos = repository.getSearchResults(query).cachedIn(viewModelScope)
    }
}