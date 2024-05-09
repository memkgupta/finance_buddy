package com.example.expensemanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensemanager.dataClasses.Split
import com.example.expensemanager.databinding.FragmentSplitBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue


class SplitFragment : Fragment() {



    lateinit var binding: FragmentSplitBinding
    lateinit var splitAdapter:SplitAdapter
    lateinit var splits:ArrayList<Split>
    lateinit var dbRef :DatabaseReference
    lateinit var readDbRef:DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      val view = inflater.inflate(R.layout.fragment_split, container, false)
        binding = FragmentSplitBinding.bind(view);
        // Inflate the layout for this fragment
        splits = ArrayList();
        initLayout()
        return view;
    }

private fun initLayout(){
    binding.splitRV.setHasFixedSize(true)
    binding.splitRV.setItemViewCacheSize(13)

    dbRef = MainActivity.database.child("splits").child(MainActivity.auth.uid.toString())
    dbRef.addValueEventListener(object:ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            binding.progressBar.visibility = View.GONE
            val tempSplits = ArrayList<Split>();
            snapshot.children.forEach{

                val splitObj = it.getValue<Split>()!!
                splitObj.id = it.key.toString();
                tempSplits.add(splitObj)

            }
            splitAdapter.updateData(tempSplits);
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    })
    splitAdapter = SplitAdapter(requireContext(), splits)
    binding.splitRV.layoutManager = LinearLayoutManager(requireContext())


    binding.splitRV.adapter = splitAdapter
}

}