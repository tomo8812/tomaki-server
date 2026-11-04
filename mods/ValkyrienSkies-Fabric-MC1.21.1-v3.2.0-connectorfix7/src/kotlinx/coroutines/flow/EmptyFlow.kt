package kotlinx.coroutines.flow

private object EmptyFlow : Flow {
   public override suspend fun collect(collector: FlowCollector<Nothing>) {
      return Unit.INSTANCE
   }
}
