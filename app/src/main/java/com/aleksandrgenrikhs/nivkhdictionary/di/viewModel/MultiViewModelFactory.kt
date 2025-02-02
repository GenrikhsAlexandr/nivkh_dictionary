package com.aleksandrgenrikhs.nivkhdictionary.di.viewModel

//
//class MultiViewModelFactory @Inject constructor(
//    private val viewModelFactories: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
//) : ViewModelProvider.Factory {
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        val viewModelProvider = viewModelFactories[modelClass as Class<ViewModel>]
//            ?: throw IllegalArgumentException("Declare $modelClass in ${ViewModelsBindingModule::class.simpleName}")
//        return viewModelProvider.get() as T
//    }
//}
