package com.android.mediproject.feature.medicine.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.android.mediproject.core.model.local.navargs.MedicineBasicInfoArgs
import com.android.mediproject.feature.medicine.databinding.FragmentCommentsHostBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostCommentsFragment : Fragment() {

    private var _binding: FragmentCommentsHostBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCommentsHostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment = binding.commentsFragmentContainerView.getFragment() as NavHostFragment
        val navInflater = navHostFragment.navController.navInflater
        val graph = navInflater.inflate(com.android.mediproject.feature.comments.R.navigation.medicine_comments_nav)

        MedicineBasicInfoArgs("", "").apply {
            graph.addInDefaultArgs(toBundle())
        }
        navHostFragment.navController.graph = graph
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}