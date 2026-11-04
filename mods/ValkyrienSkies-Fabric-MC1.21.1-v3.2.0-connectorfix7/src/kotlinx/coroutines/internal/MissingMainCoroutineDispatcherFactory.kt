package kotlinx.coroutines.internal

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher

@InternalCoroutinesApi
public object MissingMainCoroutineDispatcherFactory : MainDispatcherFactory {
   public open val loadPriority: Int
      public open get() {
         return -1
      }


   public override fun createDispatcher(allFactories: List<MainDispatcherFactory>): MainCoroutineDispatcher {
      return MissingMainCoroutineDispatcher(null, null, 2, null)
   }

   override fun hintOnError(): java.lang.String? {
      MainDispatcherFactory.DefaultImpls.hintOnError(this)
   }
}
