package org.dhis2.usescases.jira

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.dhis2.usescases.general.FragmentGlobalAbstract

class JiraFragment : FragmentGlobalAbstract() {
//    @Inject
//    lateinit var jiraViewModelFactory: JiraViewModelFactory
//    private val jiraModel: JiraViewModel by viewModels {
//        jiraViewModelFactory
//    }
//    private val resourceManager: ResourceManager = ResourceManager(requireContext(), ColorUtils())
//    private val preferenceProvider: PreferenceProvider = PreferenceProviderImpl(requireContext())
//    private val jiraRepository: JiraRepository = JiraRepository(preferenceProvider)
//    private val jiraModel: JiraViewModel = JiraViewModel(jiraRepository, resourceManager)
//    private val jiraIssueAdapter by lazy {
//        JiraIssueAdapter { jiraModel.onJiraIssueClick(it) }
//    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is MainActivity) {
//            context.mainComponent.plus(JiraModule()).inject(this)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
//        return FragmentJiraBinding.inflate(inflater, container, false).apply {
//            jiraViewModel = jiraModel
//            rememberCheck.setOnCheckedChangeListener { _, isChecked ->
//                jiraModel.onCheckedChanged(isChecked)
//            }
//            sendReportButton.isEnabled = NetworkUtils.isOnline(context)
//            issueRecycler.apply {
//                adapter = jiraIssueAdapter
//                addItemDecoration(
//                    DividerItemDecoration(
//                        context,
//                        DividerItemDecoration.VERTICAL,
//                    ),
//                )
//            }
//            jiraModel.apply {
//                init()
//                issueListResponse.observe(viewLifecycleOwner, Observer { handleListResponse(it) })
//                issueMessage.observe(viewLifecycleOwner, Observer { handleMessage(it) })
//                clickedIssueData.observe(
//                    viewLifecycleOwner,
//                    Observer { openJiraTicketInBrowser(it) },
//                )
//            }
//        }.root
        return null
    }

//    private fun handleListResponse(result: JiraIssuesResult) {
//        if (result.isSuccess()) {
//            jiraIssueAdapter.submitList(result.issues)
//        } else {
//            displayMessage(result.errorMessage)
//        }
//    }

//    private fun handleMessage(message: String) {
//        displayMessage(message)
//    }
//
//    private fun openJiraTicketInBrowser(clickedIssueData: ClickedIssueData) {
//        val browserIntent = Intent(
//            Intent.ACTION_VIEW,
//            Uri.parse(clickedIssueData.uriString),
//        ).apply {
//            val bundle = Bundle().apply {
//                putString(clickedIssueData.authHeader(), clickedIssueData.basicAuth())
//            }
//            putExtra(Browser.EXTRA_HEADERS, bundle)
//        }
//        startActivity(browserIntent)
//    }
}
