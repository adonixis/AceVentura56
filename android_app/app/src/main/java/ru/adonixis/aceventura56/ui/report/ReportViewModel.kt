package ru.adonixis.aceventura56.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReportViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Сообщить о бездомном животном"
    }
    val text: LiveData<String> = _text
}