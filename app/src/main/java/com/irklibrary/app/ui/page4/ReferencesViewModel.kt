package com.irklibrary.app.ui.page4

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.irklibrary.app.data.models.SlideWithMatkulModel
import com.irklibrary.app.data.repositories.SlidesRepository

class ReferencesViewModel : ViewModel() {

    private val repository = SlidesRepository()

    private val _slides = MutableLiveData<List<SlideWithMatkulModel>>()
    val slides: LiveData<List<SlideWithMatkulModel>> = _slides

    private val _filteredSlides = MutableLiveData<List<SlideWithMatkulModel>>()
    val filteredSlides: LiveData<List<SlideWithMatkulModel>> = _filteredSlides

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    private val _selectedMatkul = MutableLiveData<String>()
    val selectedMatkul: LiveData<String> = _selectedMatkul

    private val _matkulOptions = MutableLiveData<List<String>>()
    val matkulOptions: LiveData<List<String>> = _matkulOptions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadData()
    }

    private fun loadData() {
        _isLoading.value = true

        try {
            val allSlides = repository.getAllSlides()
            _slides.value = allSlides

            val options = mutableListOf("Semua")
            options.addAll(repository.getMatkulNames())
            _matkulOptions.value = options

            _selectedMatkul.value = "Semua"
            _searchQuery.value = ""

            applyFilters()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _isLoading.value = false
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun updateSelectedMatkul(matkul: String) {
        _selectedMatkul.value = matkul
        applyFilters()
    }

    private fun applyFilters() {
        val allSlides = _slides.value ?: return
        val query = _searchQuery.value ?: ""
        val selectedMatkul = _selectedMatkul.value ?: "Semua"

        var filteredList = allSlides

        if (selectedMatkul != "Semua") {
            filteredList = filteredList.filter { it.matkul == selectedMatkul }
        }

        if (query.isNotBlank()) {
            filteredList = repository.searchSlides(query, filteredList)
        }

        _filteredSlides.value = filteredList
    }

    fun clearSearch() {
        updateSearchQuery("")
    }

    fun getSlideCount(): Int {
        return _filteredSlides.value?.size ?: 0
    }
}