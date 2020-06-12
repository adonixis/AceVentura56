package ru.adonixis.aceventura56.ui.adopt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.adonixis.aceventura56.R

class AdoptFragment : Fragment() {

    private lateinit var adoptViewModel: AdoptViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adoptViewModel =
            ViewModelProvider(this).get(AdoptViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment__adopt, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        adoptViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}