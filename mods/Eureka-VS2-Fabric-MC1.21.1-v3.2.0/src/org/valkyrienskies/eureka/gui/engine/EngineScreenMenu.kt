/*
$VF: Unable to decompile class
Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "n.simpleName" is null
  at org.vineflower.kotlin.KotlinWriter.lambda$writeClass$0(KotlinWriter.java:301)
  at java.base/java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:196)
  at java.base/java.util.ArrayList$ArrayListSpliterator.tryAdvance(ArrayList.java:1693)
  at java.base/java.util.stream.ReferencePipeline.forEachWithCancel(ReferencePipeline.java:147)
  at java.base/java.util.stream.AbstractPipeline.copyIntoWithCancel(AbstractPipeline.java:588)
  at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:574)
  at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:560)
  at java.base/java.util.stream.FindOps$FindOp.evaluateSequential(FindOps.java:150)
  at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:265)
  at java.base/java.util.stream.ReferencePipeline.findAny(ReferencePipeline.java:688)
  at org.vineflower.kotlin.KotlinWriter.writeClass(KotlinWriter.java:302)
  at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:521)
  at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:200)
  at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:221)
*/