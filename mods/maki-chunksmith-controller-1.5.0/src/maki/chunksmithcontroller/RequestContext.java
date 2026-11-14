package maki.chunksmithcontroller;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

public final class RequestContext {
   private static final ThreadLocal<RequestContext.Request> CURRENT = new ThreadLocal<>();

   private RequestContext() {
   }

   public static RequestContext.Request current() {
      return CURRENT.get();
   }

   public static String token(String dimension, int x, int z) {
      RequestContext.Request request = CURRENT.get();
      return request != null && request.dimension().equals(dimension) && request.x() == x && request.z() == z ? request.token() : null;
   }

   public static Executor wrap(Executor executor) {
      RequestContext.Request request = CURRENT.get();
      return action -> executor.execute(() -> with(request, () -> {
         action.run();
         return null;
      }));
   }

   public static <T> T with(RequestContext.Request request, Supplier<T> action) {
      RequestContext.Request previous = CURRENT.get();
      if (request == null) {
         CURRENT.remove();
      } else {
         CURRENT.set(request);
      }

      try {
         return action.get();
      } finally {
         if (previous == null) {
            CURRENT.remove();
         } else {
            CURRENT.set(previous);
         }
      }
   }

   public record Request(String token, String dimension, int x, int z) {
   }
}
