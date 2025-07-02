package de.hs_kl.libris.ui.library


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import de.hs_kl.libris.data.model.ReadingStatus
import de.hs_kl.libris.databinding.FragmentBookListBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookListFragment : Fragment() {
    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: LibraryViewModel by viewModels({ requireParentFragment() })
    private lateinit var bookAdapter: BookAdapter
    private var readingStatus: ReadingStatus? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        arguments?.getString(ARG_STATUS)?.let { statusString ->
            readingStatus = ReadingStatus.valueOf(statusString)
            observeBooks()
        }
    }

    private fun observeBooks() {
        readingStatus?.let { status ->
            viewLifecycleOwner.lifecycleScope.launch {
                sharedViewModel.getBooksForStatus(status).collect { books ->
                    bookAdapter.submitList(books)
                    binding.emptyView.isVisible = books.isEmpty()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun setupRecyclerView() {
        bookAdapter = BookAdapter { bookId ->
            findNavController().navigate(
                LibraryFragmentDirections.actionNavigationLibraryToBookDetail(bookId)
            )
        }

        binding.recyclerView.apply {
            adapter = bookAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_STATUS = "reading_status"

        fun newInstance(status: ReadingStatus): BookListFragment {
            return BookListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_STATUS, status.name)
                }
            }
        }
    }
}
