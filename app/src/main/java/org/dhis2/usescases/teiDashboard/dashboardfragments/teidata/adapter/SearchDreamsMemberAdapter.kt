package org.dhis2.usescases.teiDashboard.dashboardfragments.teidata.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.dhis2.databinding.DreamsMembersLitsItemBinding
import org.dhis2.usescases.teiDashboard.dashboardfragments.teidata.OnDreamsTeiSelectionListener
import org.dhis2.usescases.teiDashboard.dashboardfragments.teidata.model.DreamsTeiModel

class SearchDreamsMemberAdapter(private val onDreamsTeiSelectionListener: OnDreamsTeiSelectionListener)
    : ListAdapter<DreamsTeiModel, SearchDreamsMemberAdapter.ViewHolder>(diffUtil),
    Filterable {

    private var originList : MutableList<DreamsTeiModel> = mutableListOf()
    private lateinit var binding: DreamsMembersLitsItemBinding

    private val searchFilter: Filter = object : Filter() {
        override fun performFiltering(input: CharSequence): FilterResults {
            val filteredList = if (input.isEmpty()) {
                originList
            } else {
                originList.filter { it.fullName.lowercase().contains(input) }
            }
            return FilterResults().apply { values = filteredList }
        }

        override fun publishResults(input: CharSequence, results: FilterResults) {
            submitList(results.values as ArrayList<DreamsTeiModel>)
        }
    }

    inner class ViewHolder(val binding: DreamsMembersLitsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DreamsTeiModel) {
            binding.tvTEIDisplayName.text = item.fullName
            binding.tvTEIDob.text = item.dob
            binding.tvTEISex.text = item.sex

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
         binding =
            DreamsMembersLitsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
        binding.cbTEISelectedState.setOnCheckedChangeListener { compoundButton, _ ->
            if (compoundButton.isChecked) {
                onDreamsTeiSelectionListener.onSelected(originList[position])
            } else {
                onDreamsTeiSelectionListener.onDeselected(originList[position])
            }
        }
    }

    override fun onCurrentListChanged(
        previousList: MutableList<DreamsTeiModel>,
        currentList: MutableList<DreamsTeiModel>
    ) {
        super.onCurrentListChanged(previousList, currentList)


    }

    override fun getItemCount() = currentList.size



    override fun getFilter(): Filter {
        return searchFilter
    }

    fun setData(list: List<DreamsTeiModel>) {
        this.originList = list.toMutableList()
        submitList(list)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<DreamsTeiModel>() {
            override fun areContentsTheSame(oldItem: DreamsTeiModel, newItem: DreamsTeiModel) =
                oldItem.teiUid == newItem.teiUid

            override fun areItemsTheSame(oldItem: DreamsTeiModel, newItem: DreamsTeiModel) =
                oldItem.teiUid == newItem.teiUid
        }
    }

}