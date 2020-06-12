package ru.adonixis.aceventura56.ui.donate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.adonixis.aceventura56.R

class DonateFragment : Fragment() {

    private lateinit var donateViewModel: DonateViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        donateViewModel =
            ViewModelProvider(this).get(DonateViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_donate, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        donateViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}