package ru.adonixis.aceventura56.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdoptViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Список животных, которых можно приютить"
    }
    val text: LiveData<String> = _text
}