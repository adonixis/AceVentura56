package ru.adonixis.aceventura56.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_pet.view.*
import ru.adonixis.aceventura56.R
import ru.adonixis.aceventura56.model.Pet
import java.text.SimpleDateFormat
import java.util.*

class PetsAdapter(
    val context: Context,
    val pets : ArrayList<Pet>
) : RecyclerView.Adapter<PetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        return PetViewHolder(LayoutInflater.from(context).inflate(R.layout.item_pet, parent, false))
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        var pet = pets[position]

        Glide
            .with(context)
            .load(Uri.parse(pet.imageUrl))
            .apply(RequestOptions().circleCrop())
            .placeholder(R.drawable.ic_person)
            .into(holder.imagePet)

        if (pet.petSex == "Male") {
            holder.ivSex?.setImageResource(R.drawable.ic_sex_male)
        } else {
            holder.ivSex?.setImageResource(R.drawable.ic_sex_female)
        }

        holder.tvPetName?.text = pet.petName
        holder.tvPetBreed?.text = pet.petBreed
        val simpleDate = SimpleDateFormat("dd.MM.yyyy")
        val strDate: String = simpleDate.format(pet.date)
        holder.tvPetDate?.text = strDate
        holder.tvPetAge?.text = pet.petAge
    }

    override fun getItemCount(): Int {
        return pets.size
    }
}

class PetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imagePet = view.ivPet
    val ivSex = view.ivSex
    val tvPetName = view.tvPetName
    val tvPetBreed = view.tvPetBreed
    val tvPetDate = view.tvPetDate
    val tvPetAge = view.tvPetAge
}